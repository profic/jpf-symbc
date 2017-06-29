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

import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.numeric.RealExpression;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Types;

/**
 * Compare float ..., value1, value2 => ..., result
 */
public class FCMPG extends gov.nasa.jpf.jvm.bytecode.FCMPG {

	@Override
	public Instruction execute(ThreadInfo threadInfo) {
		StackFrame stackFrame = threadInfo.getModifiableTopFrame();

		RealExpression symValue1 = (RealExpression) stackFrame.getOperandAttr(0);
		RealExpression symValue2 = (RealExpression) stackFrame.getOperandAttr(1);

		if (symValue1 == null && symValue2 == null) { // both conditions are
														// concrete
			return super.execute(threadInfo);
		} else { // at least one condition is symbolic
			ChoiceGenerator<?> choiceGenerator;
			int conditionValue;

			if (!threadInfo.isFirstStepInsn()) { // first time around
				choiceGenerator = new PCChoiceGenerator(3);
				((PCChoiceGenerator) choiceGenerator).setOffset(this.position);
				((PCChoiceGenerator) choiceGenerator).setMethodName(this.getMethodInfo().getFullName());
				threadInfo.getVM().getSystemState().setNextChoiceGenerator(choiceGenerator);
				
				return this;
			} else { // this is what really returns results
				choiceGenerator = threadInfo.getVM().getSystemState().getChoiceGenerator();
				assert (choiceGenerator instanceof PCChoiceGenerator) : "expected PCChoiceGenerator, got: "
						+ choiceGenerator;
				conditionValue = ((PCChoiceGenerator) choiceGenerator).getNextChoice() - 1;
			}

			float floatValue1 = Types.intToFloat(stackFrame.pop());
			float floatValue2 = Types.intToFloat(stackFrame.pop());
			PathCondition pathCondition;
			ChoiceGenerator<?> prevChoiceGenerator = choiceGenerator
					.getPreviousChoiceGeneratorOfType(PCChoiceGenerator.class);

			if (prevChoiceGenerator == null) {
				pathCondition = new PathCondition();
			} else {
				pathCondition = ((PCChoiceGenerator) prevChoiceGenerator).getCurrentPC();
			}
			assert pathCondition != null;

			if (conditionValue == -1) {
				if (symValue1 != null) {
					if (symValue2 != null) {  // both are symbolic values
						pathCondition._addDet(Comparator.LT, symValue2, symValue1);
					} else {
						pathCondition._addDet(Comparator.LT, floatValue2, symValue1);
					}
				} else {
					pathCondition._addDet(Comparator.LT, symValue2, floatValue1);
				}

				if (!pathCondition.simplify()) {  // not satisfiable
					threadInfo.getVM().getSystemState().setIgnored(true);
				} else {
					((PCChoiceGenerator) choiceGenerator).setCurrentPC(pathCondition);
				}
			} else if (conditionValue == 0) {
				if (symValue1 != null) {
					if (symValue2 != null) {  // both are symbolic values
						pathCondition._addDet(Comparator.EQ, symValue1, symValue2);
					} else {
						pathCondition._addDet(Comparator.EQ, symValue1, floatValue2);
					}
				} else {
					pathCondition._addDet(Comparator.EQ, floatValue1, symValue2);
				}
				if (!pathCondition.simplify()) {  // not satisfiable
					threadInfo.getVM().getSystemState().setIgnored(true);
				} else {
					((PCChoiceGenerator) choiceGenerator).setCurrentPC(pathCondition);
				}
			} else {  // 1
				if (symValue1 != null) {
					if (symValue2 != null) {  // both are symbolic values
						pathCondition._addDet(Comparator.GT, symValue2, symValue1);
					} else {
						pathCondition._addDet(Comparator.GT, floatValue2, symValue1);
					}
				} else {
					pathCondition._addDet(Comparator.GT, symValue2, floatValue1);
				}
				if (!pathCondition.simplify()) {  // not satisfiable
					threadInfo.getVM().getSystemState().setIgnored(true);
				} else {
					((PCChoiceGenerator) choiceGenerator).setCurrentPC(pathCondition);
				}
			}

			stackFrame.push(conditionValue, false);
			return getNext(threadInfo);
		}
	}
}
