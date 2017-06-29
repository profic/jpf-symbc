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

public class FDIV extends gov.nasa.jpf.jvm.bytecode.FDIV {

	@Override
	public Instruction execute(ThreadInfo threadInfo) {
		StackFrame stackFrame = threadInfo.getModifiableTopFrame();

		RealExpression symFloatValue1 = (RealExpression) stackFrame.getOperandAttr(0);
		RealExpression symFloatValue2 = (RealExpression) stackFrame.getOperandAttr(1);
		
		float floatValue1;
		float floatValue2;

		if (symFloatValue1 == null && symFloatValue2 == null) {
			floatValue1 = Types.intToFloat(stackFrame.pop());
			floatValue2 = Types.intToFloat(stackFrame.pop());
			if (floatValue1 == 0) {
				return threadInfo.createAndThrowException("java.lang.ArithmeticException", "div by 0");
			}
			float r = floatValue2 / floatValue1;
			stackFrame.push(Types.floatToInt(r), false);

			return getNext(threadInfo);
		}

		// result is symbolic expression

		if (symFloatValue1 == null && symFloatValue2 != null) {
			floatValue1 = Types.intToFloat(stackFrame.pop());
			floatValue2 = Types.intToFloat(stackFrame.pop());
			if (floatValue1 == 0) {
				return threadInfo.createAndThrowException("java.lang.ArithmeticException", "div by 0");
			}
			stackFrame.push(0, false);
			RealExpression result = symFloatValue2._div(floatValue1);
			stackFrame.setOperandAttr(result);

			return getNext(threadInfo);
		}

		// div by zero check affects path condition
		// sym_v1 is non-null and should be checked against zero

		ChoiceGenerator<?> choiceGenerator;
		boolean condition;

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
			condition = (Integer) choiceGenerator.getNextChoice() == 0 ? false : true;
		}

		floatValue1 = Types.intToFloat(stackFrame.pop());
		floatValue2 = Types.intToFloat(stackFrame.pop());
		stackFrame.push(0, false);

		PathCondition pathCondition;
		ChoiceGenerator<?> prevChoiceGenerator = choiceGenerator
				.getPreviousChoiceGeneratorOfType(PCChoiceGenerator.class);

		if (prevChoiceGenerator == null) {
			pathCondition = new PathCondition();
		} else {
			pathCondition = ((PCChoiceGenerator) prevChoiceGenerator).getCurrentPC();
		}
		assert pathCondition != null;

		if (condition) { // check div by zero
			pathCondition._addDet(Comparator.EQ, symFloatValue1, 0);
			if (pathCondition.simplify()) { // satisfiable
				((PCChoiceGenerator) choiceGenerator).setCurrentPC(pathCondition);

				return threadInfo.createAndThrowException("java.lang.ArithmeticException", "div by 0");
			} else {
				threadInfo.getVM().getSystemState().setIgnored(true);
				return getNext(threadInfo);
			}
		} else {
			pathCondition._addDet(Comparator.NE, symFloatValue1, 0);
			if (pathCondition.simplify()) { // satisfiable
				((PCChoiceGenerator) choiceGenerator).setCurrentPC(pathCondition);

				// set the result
				RealExpression result;
				if (symFloatValue2 != null) {
					result = symFloatValue2._div(symFloatValue1);
				} else {
					result = symFloatValue1._div_reverse(floatValue2);
				}
				stackFrame = threadInfo.getModifiableTopFrame();
				stackFrame.setOperandAttr(result);

				return getNext(threadInfo);
			} else {
				threadInfo.getVM().getSystemState().setIgnored(true);
				return getNext(threadInfo);
			}
		}
	}
}
