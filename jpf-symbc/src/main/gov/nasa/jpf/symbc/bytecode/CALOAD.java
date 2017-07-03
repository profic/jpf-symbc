/*
 * Copyright (C) 2014, United States Government, as represented by the
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *
 * The Java Pathfinder core (jpf-core) platform is licensed under the
 * Apache License, Version 2.0 (the "License"); you may not use this file except
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

// author corina pasareanu corina.pasareanu@sv.cmu.edu

package gov.nasa.jpf.symbc.bytecode;

import gov.nasa.jpf.symbc.SymbolicInstructionFactory;
import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.IntegerExpression;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.vm.ArrayIndexOutOfBoundsExecutiveException;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * Load char from array ..., arrayref, index => ..., value
 */
public class CALOAD extends gov.nasa.jpf.jvm.bytecode.CALOAD {

	@Override
	public Instruction execute(ThreadInfo threadInfo) {
		if (peekIndexAttr(threadInfo) == null || !(peekIndexAttr(threadInfo) instanceof IntegerExpression)) {
			return super.execute(threadInfo);
		}
		StackFrame stackFrame = threadInfo.getModifiableTopFrame();
		arrayRef = stackFrame.peek(1); // ..,arrayRef,idx
		if (arrayRef == MJIEnv.NULL) {
			return threadInfo.createAndThrowException("java.lang.NullPointerException");
		}

		ElementInfo arrayElementInfo = threadInfo.getElementInfo(arrayRef);
		int len = (arrayElementInfo.getArrayFields()).arrayLength(); // assumed
																		// concrete
		if (!threadInfo.isFirstStepInsn()) {
			PCChoiceGenerator arrayChoiceGenerator = new PCChoiceGenerator(0, len + 1); // add
																						// 2
																						// error
																						// cases:
																						// <0,
																						// >=len
			threadInfo.getVM().getSystemState().setNextChoiceGenerator(arrayChoiceGenerator);

			if (SymbolicInstructionFactory.debugMode) {
				System.out.println("# array cg registered: " + arrayChoiceGenerator);
			}

			return this;
		} else { // this is what really returns results

			// index = frame.peek();
			PCChoiceGenerator lastChoiceGenerator = threadInfo.getVM().getSystemState()
					.getLastChoiceGeneratorOfType(PCChoiceGenerator.class);
			assert (lastChoiceGenerator != null);
			PCChoiceGenerator prevChoiceGenerator = lastChoiceGenerator
					.getPreviousChoiceGeneratorOfType(PCChoiceGenerator.class);

			index = lastChoiceGenerator.getNextChoice();
			// System.out.println("array index "+index);
			IntegerExpression symIndex = (IntegerExpression) peekIndexAttr(threadInfo);
			// check the constraint

			PathCondition pathCondition;

			if (prevChoiceGenerator == null) {
				pathCondition = new PathCondition();
			} else {
				pathCondition = ((PCChoiceGenerator) prevChoiceGenerator).getCurrentPC();
			}

			assert pathCondition != null;

			if (index < len) {
				pathCondition._addDet(Comparator.EQ, index, symIndex);
				if (pathCondition.simplify()) { // satisfiable
					((PCChoiceGenerator) lastChoiceGenerator).setCurrentPC(pathCondition);
				} else {
					threadInfo.getVM().getSystemState().setIgnored(true);// backtrack
					return getNext(threadInfo);
				}
			} else if (index == len) { // now check for out of bounds exceptions
				pathCondition._addDet(Comparator.LT, symIndex, 0);
				if (pathCondition.simplify()) { // satisfiable
					((PCChoiceGenerator) lastChoiceGenerator).setCurrentPC(pathCondition);
					return threadInfo.createAndThrowException("java.lang.ArrayIndexOutOfBoundsException");
				} else {
					threadInfo.getVM().getSystemState().setIgnored(true);// backtrack
					return getNext(threadInfo);
				}
			} else if (index == len + 1) {
				pathCondition._addDet(Comparator.GE, symIndex, len);
				if (pathCondition.simplify()) { // satisfiable
					((PCChoiceGenerator) lastChoiceGenerator).setCurrentPC(pathCondition);
					return threadInfo.createAndThrowException("java.lang.ArrayIndexOutOfBoundsException");
				} else {
					threadInfo.getVM().getSystemState().setIgnored(true);// backtrack
					return getNext(threadInfo);
				}
			}

			// original code for concrete execution
			arrayOperandAttr = peekArrayAttr(threadInfo);
			indexOperandAttr = peekIndexAttr(threadInfo);

			// corina: Ignore POR for now
			/*
			 * Scheduler scheduler = ti.getScheduler(); if
			 * (scheduler.canHaveSharedarrayChoiceGenerator( ti, this, eiArray, index)){ //
			 * don't modify the frame before this eiArray =
			 * scheduler.updateArraySharedness(ti, eiArray, index); if
			 * (scheduler.setsSharedarrayChoiceGenerator( ti, this, eiArray, index)){ return
			 * this; } }
			 */

			stackFrame.pop(2); // now we can pop index and array reference
			// assign to index any value between 0 and array length

			try {
				push(stackFrame, arrayElementInfo, index);

				Object elementAttr = arrayElementInfo.getElementAttr(index);
				if (elementAttr != null) {
					if (getElementSize() == 1) {
						stackFrame.setOperandAttr(elementAttr);
					} else {
						stackFrame.setLongOperandAttr(elementAttr);
					}
				}

				return getNext(threadInfo);

			} catch (ArrayIndexOutOfBoundsExecutiveException ex) {
				return ex.getInstruction();
			}
		}
	}
}
