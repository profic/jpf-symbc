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
import gov.nasa.jpf.symbc.SymbolicInstructionFactory;
import gov.nasa.jpf.symbc.arrays.ArrayExpression;
import gov.nasa.jpf.symbc.heap.HeapChoiceGenerator;
import gov.nasa.jpf.symbc.heap.HeapNode;
import gov.nasa.jpf.symbc.heap.Helper;
import gov.nasa.jpf.symbc.heap.SymbolicInputHeap;
import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.IntegerConstant;
import gov.nasa.jpf.symbc.numeric.IntegerExpression;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.numeric.SymbolicInteger;
import gov.nasa.jpf.symbc.string.StringExpression;
import gov.nasa.jpf.symbc.string.SymbolicStringBuilder;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ClassLoaderInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.KernelState;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
//import gov.nasa.jpf.symbc.uberlazy.TypeHierarchy;
import gov.nasa.jpf.vm.ThreadInfo;

// Corina: I need to add the latest fix from the v6 to treat properly "this"

public class ALOAD extends gov.nasa.jpf.jvm.bytecode.ALOAD {

	public ALOAD(int localVarIndex) {
		super(localVarIndex);
	}

	// private int numNewRefs = 0; // # of new reference objects to account for
	// polymorphism -- work of Neha Rungta -- needs to be updated
	boolean abstractClass = false;

	@Override
	public Instruction execute(ThreadInfo threadInfo) {
		HeapNode[] prevSymRefs = null; // previously initialized objects of same
										// type: candidates for lazy init
		int numSymRefs = 0; // # of prev. initialized objects
		ChoiceGenerator<?> prevHeapChoiceGenerator = null;

		Config conf = threadInfo.getVM().getConfig();
		String[] lazy = conf.getStringArray("symbolic.lazy");
		if (lazy == null || !lazy[0].equalsIgnoreCase("true"))
			return super.execute(threadInfo);

		// TODO: fix handle polymorphism

		StackFrame stackFrame = threadInfo.getModifiableTopFrame();
		int objRef = stackFrame.peek();
		ElementInfo elementInfo = threadInfo.getElementInfo(objRef);
		Object attr = stackFrame.getLocalAttr(index);
		String typeOfLocalVar = super.getLocalVariableType();

		if (attr == null || typeOfLocalVar.equals("?") || attr instanceof SymbolicStringBuilder
				|| attr instanceof StringExpression || attr instanceof ArrayExpression) {
			return super.execute(threadInfo);
		}

		ClassInfo typeClassInfo = ClassLoaderInfo.getCurrentResolvedClassInfo(typeOfLocalVar);

		int currentChoice;
		ChoiceGenerator<?> thisHeapChoiceGenerator;

		if (!threadInfo.isFirstStepInsn()) {
			// System.out.println("the first time");

			prevSymRefs = null;
			numSymRefs = 0;
			prevHeapChoiceGenerator = null;

			prevHeapChoiceGenerator = threadInfo.getVM().getLastChoiceGeneratorOfType(HeapChoiceGenerator.class);

			if (prevHeapChoiceGenerator != null) {
				// determine # of candidates for lazy initialization
				SymbolicInputHeap symInputHeap = ((HeapChoiceGenerator) prevHeapChoiceGenerator)
						.getCurrentSymInputHeap();

				prevSymRefs = symInputHeap.getNodesOfType(typeClassInfo);
				numSymRefs = prevSymRefs.length;

			}
			int increment = 2;
			if (typeClassInfo.isAbstract() || (((IntegerExpression) attr).toString()).contains("this")) {
				abstractClass = true;
				increment = 1; // only null for abstract, non null for this
			}

			// TODO fix: subtypes

			thisHeapChoiceGenerator = new HeapChoiceGenerator(numSymRefs + increment); // +null,new
			threadInfo.getVM().setNextChoiceGenerator(thisHeapChoiceGenerator);

			return this;
		} else {
			// this is what returns the results
			thisHeapChoiceGenerator = threadInfo.getVM().getChoiceGenerator();
			assert (thisHeapChoiceGenerator instanceof HeapChoiceGenerator) : "expected HeapChoiceGenerator, got:"
					+ thisHeapChoiceGenerator;
			currentChoice = ((HeapChoiceGenerator) thisHeapChoiceGenerator).getNextChoice();
		}

		PathCondition pathConditionHeap;
		SymbolicInputHeap symInputHeap;

		prevHeapChoiceGenerator = thisHeapChoiceGenerator.getPreviousChoiceGeneratorOfType(HeapChoiceGenerator.class);

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

		int dynamicAreaIndex = 0; // index into JPF's dynamic area

		if (currentChoice < numSymRefs) { // lazy initialization using a
											// previously lazily initialized
											// object
			HeapNode candidateNode = prevSymRefs[currentChoice];
			// here we should update pcHeap with the constraint attr ==
			// candidateNode.sym_v
			pathConditionHeap._addDet(Comparator.EQ, (SymbolicInteger) attr, candidateNode.getSymbolic());
			dynamicAreaIndex = candidateNode.getIndex();
		} else if (currentChoice == numSymRefs && !(((IntegerExpression) attr).toString()).contains("this")) { // null
																												// object
			pathConditionHeap._addDet(Comparator.EQ, (SymbolicInteger) attr, new IntegerConstant(-1));
			dynamicAreaIndex = MJIEnv.NULL;
		} else if ((currentChoice == (numSymRefs + 1) && !abstractClass)
				| (currentChoice == numSymRefs && (((IntegerExpression) attr).toString()).contains("this"))) {
			// creates a new object with all fields symbolic
			boolean shared = (elementInfo == null ? false : elementInfo.isShared());
			dynamicAreaIndex = Helper.addNewHeapNode(typeClassInfo, threadInfo, attr, pathConditionHeap, symInputHeap,
					numSymRefs, prevSymRefs, shared);
		} else {
			// TODO: fix subtypes
			System.err.println("subtypes not handled");
		}

		stackFrame.setLocalVariable(index, dynamicAreaIndex, true);
		stackFrame.setLocalAttr(index, null);
		stackFrame.push(dynamicAreaIndex, true);

		((HeapChoiceGenerator) thisHeapChoiceGenerator).setCurrentPCheap(pathConditionHeap);
		((HeapChoiceGenerator) thisHeapChoiceGenerator).setCurrentSymInputHeap(symInputHeap);
		if (SymbolicInstructionFactory.debugMode) {
			System.out.println("ALOAD pcHeap: " + pathConditionHeap);
		}

		return getNext(threadInfo);
	}
}
