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

//
// Copyright (C) 2006 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
//
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//
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
 * Divide 2 doubles ..., value1, value2 => ..., value2/value1
 */
public class DDIV extends gov.nasa.jpf.jvm.bytecode.DDIV {

	@Override
	public Instruction execute(ThreadInfo threadInfo) {
		StackFrame stackFrame = threadInfo.getModifiableTopFrame();

		RealExpression symValue1 = (RealExpression) stackFrame.getOperandAttr(1);
		double doubleValue1;
		RealExpression symValue2 = (RealExpression) stackFrame.getOperandAttr(3);
		double doubleValue2;

		if (symValue1 == null && symValue2 == null) {
			doubleValue1 = Types.longToDouble(stackFrame.popLong());
			doubleValue2 = Types.longToDouble(stackFrame.popLong());
			if (doubleValue1 == 0) {
				return threadInfo.createAndThrowException("java.lang.ArithmeticException", "division by 0");
			}
			double doubleResult = doubleValue2 / doubleValue1;
			stackFrame.pushLong(Types.doubleToLong(doubleResult));

			return getNext(threadInfo);
		}

		// result is symbolic expression
		if (symValue1 == null && symValue2 != null) {
			doubleValue1 = Types.longToDouble(stackFrame.popLong());
			doubleValue2 = Types.longToDouble(stackFrame.popLong());
			if (doubleValue1 == 0)
				return threadInfo.createAndThrowException("java.lang.ArithmeticException", "div by 0");
			stackFrame.pushLong(0);
			RealExpression symResult = symValue2._div(doubleValue1);
			stackFrame.setLongOperandAttr(symResult);

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

		doubleValue1 = Types.longToDouble(stackFrame.popLong());
		doubleValue2 = Types.longToDouble(stackFrame.popLong());
		stackFrame.pushLong(0);

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
			pathCondition._addDet(Comparator.EQ, symValue1, 0);
			if (pathCondition.simplify()) { // satisfiable
				((PCChoiceGenerator) choiceGenerator).setCurrentPC(pathCondition);
				
				return threadInfo.createAndThrowException("java.lang.ArithmeticException", "div by 0");
			} else {
				threadInfo.getVM().getSystemState().setIgnored(true);
				
				return getNext(threadInfo);
			}
		} else {
			pathCondition._addDet(Comparator.NE, symValue1, 0);
			if (pathCondition.simplify()) { // satisfiable
				((PCChoiceGenerator) choiceGenerator).setCurrentPC(pathCondition);
				
				RealExpression result;
				if (symValue2 != null) {
					result = symValue2._div(symValue1);
				} else {
					result = symValue1._div_reverse(doubleValue2);
				}
				
				stackFrame = threadInfo.getModifiableTopFrame();
				stackFrame.setLongOperandAttr(result);
				
				return getNext(threadInfo);
			} else {
				threadInfo.getVM().getSystemState().setIgnored(true);
				
				return getNext(threadInfo);
			}
		}
	}
}
