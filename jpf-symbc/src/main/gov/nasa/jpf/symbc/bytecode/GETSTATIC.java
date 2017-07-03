/*
 * Copyright (C) 2014, United States Government, as represented by the
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *
 * Symbolic Pathfinder (jpf-symbc) is licensed under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0. 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package gov.nasa.jpf.symbc.bytecode;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPFException;
import gov.nasa.jpf.symbc.SymbolicInstructionFactory;
import gov.nasa.jpf.symbc.arrays.ArrayExpression;
import gov.nasa.jpf.symbc.heap.HeapChoiceGenerator;
import gov.nasa.jpf.symbc.heap.HeapNode;
import gov.nasa.jpf.symbc.heap.Helper;
import gov.nasa.jpf.symbc.heap.SymbolicInputHeap;
import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.IntegerConstant;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.numeric.SymbolicInteger;
import gov.nasa.jpf.symbc.string.StringExpression;
import gov.nasa.jpf.symbc.string.SymbolicStringBuilder;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.LoadOnJPFRequired;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.StackFrame;
//import gov.nasa.jpf.symbc.uberlazy.TypeHierarchy;
import gov.nasa.jpf.vm.ThreadInfo;

public class GETSTATIC extends gov.nasa.jpf.jvm.bytecode.GETSTATIC {
	public GETSTATIC(String fieldName, String clsName, String fieldDescriptor) {
		super(fieldName, clsName, fieldDescriptor);
	}

	// private int numNewRefs = 0; // # of new reference objects to account for
	// polymorphism -- work of Neha Rungta -- needs to be updated

	boolean abstractClass = false;

	@Override
	public Instruction execute(ThreadInfo threadInfo) {
		ChoiceGenerator<?> prevHeapChoiceGenerator = null;
		HeapNode[] prevSymRefs = null;
		int numSymRefs = 0;

		Config config = threadInfo.getVM().getConfig();
		String[] lazy = config.getStringArray("symbolic.lazy");
		if (lazy == null || !lazy[0].equalsIgnoreCase("true")) {
			return super.execute(threadInfo);
		}

		ClassInfo fieldClassInfo;
		FieldInfo fieldInfo;

		try {
			fieldInfo = getFieldInfo();
		} catch (LoadOnJPFRequired lre) {
			return threadInfo.getPC();
		}

		if (fieldInfo == null) {
			return threadInfo.createAndThrowException("java.lang.NoSuchFieldError", (className + '.' + fname));
		}

		fieldClassInfo = fieldInfo.getClassInfo();

		if (!mi.isClinit(fieldClassInfo) && fieldClassInfo.initializeClass(threadInfo)) {
			// note - this returns the next insn in the topmost clinit that just
			// got pushed
			return threadInfo.getPC();
		}

		ElementInfo elementInfo = fieldClassInfo.getModifiableStaticElementInfo();
		// ei = ei. getInstanceWithUpdatedSharedness(ti); POR broken again

		if (elementInfo == null) {
			throw new JPFException(
					"attempt to access field: " + fname + " of uninitialized class: " + fieldClassInfo.getName());
		}

		Object fieldAttr = elementInfo.getFieldAttr(fi);

		if (!(fi.isReference() && fieldAttr != null)) {
			return super.execute(threadInfo);
		}

		if (fieldAttr instanceof StringExpression || fieldAttr instanceof SymbolicStringBuilder
				|| fieldAttr instanceof ArrayExpression) {
			return super.execute(threadInfo); // Strings are handled specially
		}

		// else: lazy initialization
		if (SymbolicInstructionFactory.debugMode) {
			System.out.println("lazy initialization");
		}

		// may introduce thread choices
		// POR broken again
		// if (isNewPorFieldBoundary(ti)) {
		// if (createAndSetSharedFieldAccessCG( ei, ti)) {
		// return this; // not yet because well create the heap cg
		// }
		// }

		int currentChoice;
		ChoiceGenerator<?> heapChoiceGenerator;

		ClassInfo typeClassInfo = fi.getTypeClassInfo(); // use this instead of
															// fullType

		// first time around

		if (!threadInfo.isFirstStepInsn()) {
			prevSymRefs = null;
			numSymRefs = 0;

			prevHeapChoiceGenerator = threadInfo.getVM().getSystemState()
					.getLastChoiceGeneratorOfType(HeapChoiceGenerator.class);

			if (prevHeapChoiceGenerator != null) {
				// collect candidates for lazy initialization
				SymbolicInputHeap symInputHeap = ((HeapChoiceGenerator) prevHeapChoiceGenerator)
						.getCurrentSymInputHeap();

				prevSymRefs = symInputHeap.getNodesOfType(typeClassInfo);
				numSymRefs = prevSymRefs.length;

			}

			// TODO: fix subtypes
			heapChoiceGenerator = new HeapChoiceGenerator(numSymRefs + 2); // +null,new
			threadInfo.getVM().getSystemState().setNextChoiceGenerator(heapChoiceGenerator);

			return this;
		} else { // this is what really returns results
			heapChoiceGenerator = threadInfo.getVM().getSystemState()
					.getLastChoiceGeneratorOfType(HeapChoiceGenerator.class);
			assert (heapChoiceGenerator != null
					&& heapChoiceGenerator instanceof HeapChoiceGenerator) : "expected HeapChoiceGenerator, got: "
							+ heapChoiceGenerator;
			currentChoice = ((HeapChoiceGenerator) heapChoiceGenerator).getNextChoice();
		}

		PathCondition pathConditionHeap; // this pc contains only the
											// constraints on the
		// heap
		SymbolicInputHeap symInputHeap;

		// pcHeap is updated with the pcHeap stored in the choice generator
		// above
		// get the pcHeap from the previous choice generator of the same type

		prevHeapChoiceGenerator = heapChoiceGenerator.getPreviousChoiceGeneratorOfType(HeapChoiceGenerator.class);

		if (prevHeapChoiceGenerator == null) {
			pathConditionHeap = new PathCondition();
			symInputHeap = new SymbolicInputHeap();
		} else {
			pathConditionHeap = ((HeapChoiceGenerator) prevHeapChoiceGenerator).getCurrentPCheap();
			symInputHeap = ((HeapChoiceGenerator) prevHeapChoiceGenerator).getCurrentSymInputHeap();
		}
		assert pathConditionHeap != null;
		assert symInputHeap != null;

		prevSymRefs = symInputHeap.getNodesOfType(typeClassInfo);
		numSymRefs = prevSymRefs.length;

		int dynamicAreaIndex = 0;
		if (currentChoice < numSymRefs) { // lazy initialization
			HeapNode candidateNode = prevSymRefs[currentChoice];
			// here we should update pcHeap with the constraint attr ==
			// candidateNode.sym_v
			pathConditionHeap._addDet(Comparator.EQ, (SymbolicInteger) fieldAttr, candidateNode.getSymbolic());
			dynamicAreaIndex = candidateNode.getIndex();
		} else if (currentChoice == numSymRefs) { // existing (null)
			pathConditionHeap._addDet(Comparator.EQ, (SymbolicInteger) fieldAttr, new IntegerConstant(-1));
			dynamicAreaIndex = MJIEnv.NULL;
		} else if (currentChoice == (numSymRefs + 1) && !abstractClass) {
			// creates a new object with all fields symbolic and adds the object
			// to SymbolicHeap
			dynamicAreaIndex = Helper.addNewHeapNode(typeClassInfo, threadInfo, fieldAttr, pathConditionHeap,
					symInputHeap, numSymRefs, prevSymRefs, elementInfo.isShared());
		} else {
			// TODO: fix
			System.err.println("subtyping not handled");
		}

		elementInfo.setReferenceField(fi, dynamicAreaIndex);
		elementInfo.setFieldAttr(fi, null);// Helper.SymbolicNull); // was null
		StackFrame frame = threadInfo.getModifiableTopFrame();
		frame.pushRef(dynamicAreaIndex);
		((HeapChoiceGenerator) heapChoiceGenerator).setCurrentPCheap(pathConditionHeap);
		((HeapChoiceGenerator) heapChoiceGenerator).setCurrentSymInputHeap(symInputHeap);
		if (SymbolicInstructionFactory.debugMode) {
			System.out.println("GETSTATIC pcHeap: " + pathConditionHeap);
		}

		return getNext(threadInfo);
	}
}
