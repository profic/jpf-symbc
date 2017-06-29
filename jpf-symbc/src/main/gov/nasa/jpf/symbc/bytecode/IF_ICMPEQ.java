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

import gov.nasa.jpf.symbc.numeric.*;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

// we should factor out some of the code and put it in a parent class for all "if statements"

public class IF_ICMPEQ extends gov.nasa.jpf.jvm.bytecode.IF_ICMPEQ {
	public IF_ICMPEQ(int targetPosition) {
		super(targetPosition);
	}

	@Override
	public Instruction execute(ThreadInfo threadInfo) {
		StackFrame stackFrame = threadInfo.getModifiableTopFrame();

		IntegerExpression symIntegerValue1 = (IntegerExpression) stackFrame.getOperandAttr(1);
		IntegerExpression symIntegerValue2 = (IntegerExpression) stackFrame.getOperandAttr(0);

		if ((symIntegerValue1 == null) && (symIntegerValue2 == null)) {
			return super.execute(threadInfo);
		} else {
			ChoiceGenerator<?> choiceGenerator;

			if (!threadInfo.isFirstStepInsn()) { // first time around
				choiceGenerator = new PCChoiceGenerator(2);
				((PCChoiceGenerator) choiceGenerator).setOffset(this.position);
				((PCChoiceGenerator) choiceGenerator).setMethodName(this.getMethodInfo().getFullName());
				threadInfo.getVM().getSystemState().setNextChoiceGenerator(choiceGenerator);

				return this;
			} else { // this is what really returns results
				choiceGenerator = threadInfo.getVM().getSystemState().getChoiceGenerator();
				assert (choiceGenerator instanceof PCChoiceGenerator) : "expected PCChoiceGenerator, got: "
						+ choiceGenerator;
				conditionValue = (Integer) choiceGenerator.getNextChoice() == 0 ? false : true;
			}

			int integerValue1 = stackFrame.pop();
			int integerValue2 = stackFrame.pop();

			PathCondition pathCondition;

			// pc is updated with the pc stored in the choice generator above
			// get the path condition from the
			// previous choice generator of the same type

			ChoiceGenerator<?> prevChoiceGenerator = choiceGenerator
					.getPreviousChoiceGeneratorOfType(PCChoiceGenerator.class);

			if (prevChoiceGenerator == null) {
				pathCondition = new PathCondition();
			} else {
				pathCondition = ((PCChoiceGenerator) prevChoiceGenerator).getCurrentPC();
			}
			assert pathCondition != null;

			if (conditionValue) {
				if (symIntegerValue1 != null) {
					if (symIntegerValue2 != null) { // both are symbolic values
						pathCondition._addDet(Comparator.EQ, symIntegerValue1, symIntegerValue2);
					} else {
						pathCondition._addDet(Comparator.EQ, symIntegerValue1, integerValue2);
					}
				} else {
					pathCondition._addDet(Comparator.EQ, integerValue1, symIntegerValue2);
				}
				if (!pathCondition.simplify()) {// not satisfiable
					threadInfo.getVM().getSystemState().setIgnored(true);
				} else {
					// pc.solve();
					((PCChoiceGenerator) choiceGenerator).setCurrentPC(pathCondition);
					// System.out.println(((PCChoiceGenerator)
					// cg).getCurrentPC());
				}

				return getTarget();
			} else {
				if (symIntegerValue1 != null) {
					if (symIntegerValue2 != null) {
						pathCondition._addDet(Comparator.NE, symIntegerValue1, symIntegerValue2);
					} else {
						pathCondition._addDet(Comparator.NE, symIntegerValue1, integerValue2);
					}
				} else {
					pathCondition._addDet(Comparator.NE, integerValue1, symIntegerValue2);
				}
				if (!pathCondition.simplify()) {// not satisfiable
					threadInfo.getVM().getSystemState().setIgnored(true);
				} else {
					// pc.solve();
					((PCChoiceGenerator) choiceGenerator).setCurrentPC(pathCondition);
					// System.out.println(((PCChoiceGenerator)
					// cg).getCurrentPC());
				}

				return getNext(threadInfo);
			}
		}
	}
}