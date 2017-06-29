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

public class DCMPG extends gov.nasa.jpf.jvm.bytecode.DCMPG {

	@Override
	public Instruction execute(ThreadInfo threadInfo) {
		StackFrame stackFrame = threadInfo.getModifiableTopFrame();

		RealExpression symDoubleValue1 = (RealExpression) stackFrame.getOperandAttr(1);
		RealExpression symDoubleValue2 = (RealExpression) stackFrame.getOperandAttr(3);

		if (symDoubleValue1 == null && symDoubleValue2 == null) { // both conditions are
														// concrete
			return super.execute(threadInfo);
		} else { // at least one condition is symbolic
			ChoiceGenerator<Integer> choiceGenerator;
			int conditionValue;

			if (!threadInfo.isFirstStepInsn()) { // first time around
				choiceGenerator = new PCChoiceGenerator(3);
				((PCChoiceGenerator) choiceGenerator).setOffset(this.position);
				((PCChoiceGenerator) choiceGenerator).setMethodName(this.getMethodInfo().getFullName());
				threadInfo.getVM().getSystemState().setNextChoiceGenerator(choiceGenerator);

				return this;
			} else { // this is what really returns results
				ChoiceGenerator<?> curChoiceGenerator = threadInfo.getVM().getSystemState().getChoiceGenerator();
				assert (curChoiceGenerator instanceof PCChoiceGenerator) : "expected PCChoiceGenerator, got: "
						+ curChoiceGenerator;
				choiceGenerator = (PCChoiceGenerator) curChoiceGenerator;
				conditionValue = choiceGenerator.getNextChoice().intValue() - 1;
			}

			double doubleValue1 = Types.longToDouble(stackFrame.popLong());
			double doubleValue2 = Types.longToDouble(stackFrame.popLong());
			// System.out.println("Execute DCMPG: " + conditionValue);
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

			if (conditionValue == -1) {
				if (symDoubleValue1 != null) {
					if (symDoubleValue2 != null) { // both are symbolic values
						pathCondition._addDet(Comparator.LT, symDoubleValue2, symDoubleValue1);
					} else
						pathCondition._addDet(Comparator.LT, doubleValue2, symDoubleValue1);
				} else {
					pathCondition._addDet(Comparator.LT, symDoubleValue2, doubleValue1);
				}
				if (!pathCondition.simplify()) {// not satisfiable
					threadInfo.getVM().getSystemState().setIgnored(true);
				} else {
					// pc.solve();
					((PCChoiceGenerator) choiceGenerator).setCurrentPC(pathCondition);
					// System.out.println(((PCChoiceGenerator)
					// cg).getCurrentPC());
				}
			} else if (conditionValue == 0) {
				if (symDoubleValue1 != null) {
					if (symDoubleValue2 != null) { // both are symbolic values
						pathCondition._addDet(Comparator.EQ, symDoubleValue1, symDoubleValue2);
					} else {
						pathCondition._addDet(Comparator.EQ, symDoubleValue1, doubleValue2);
					}
				} else {
					pathCondition._addDet(Comparator.EQ, doubleValue1, symDoubleValue2);
				}
				if (!pathCondition.simplify()) {// not satisfiable
					threadInfo.getVM().getSystemState().setIgnored(true);
				} else {
					// pc.solve();
					((PCChoiceGenerator) choiceGenerator).setCurrentPC(pathCondition);
					// System.out.println(((PCChoiceGenerator)
					// cg).getCurrentPC());
				}
			} else {
				if (symDoubleValue1 != null) {
					if (symDoubleValue2 != null) { // both are symbolic values
						pathCondition._addDet(Comparator.GT, symDoubleValue2, symDoubleValue1);
					} else {
						pathCondition._addDet(Comparator.GT, doubleValue2, symDoubleValue1);
					}
				} else {
					pathCondition._addDet(Comparator.GT, symDoubleValue2, doubleValue1);
				}
				if (!pathCondition.simplify()) {// not satisfiable
					threadInfo.getVM().getSystemState().setIgnored(true);
				} else {
					// pc.solve();
					((PCChoiceGenerator) choiceGenerator).setCurrentPC(pathCondition);
					// System.out.println(((PCChoiceGenerator)
					// cg).getCurrentPC());
				}
			}
			stackFrame.push(conditionValue, false);
			
			return getNext(threadInfo);
		}
	}
}
