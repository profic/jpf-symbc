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

//Copyright (C) 2007 United States Government as represented by the
//Administrator of the National Aeronautics and Space Administration
//(NASA).  All Rights Reserved.

//This software is distributed under the NASA Open Source Agreement
//(NOSA), version 1.3.  The NOSA has been approved by the Open Source
//Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
//directory tree for the complete NOSA document.

//THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
//KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
//LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
//SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
//A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
//THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
//DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
package gov.nasa.jpf.symbc.bytecode;

import gov.nasa.jpf.symbc.numeric.*;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

public class IDIV extends gov.nasa.jpf.jvm.bytecode.IDIV {

	@Override
	public Instruction execute(ThreadInfo threadInfo) {
		StackFrame stackFrame = threadInfo.getModifiableTopFrame();
		
		IntegerExpression symIntegerValue1 = (IntegerExpression) stackFrame.getOperandAttr(0);
		IntegerExpression symIntegerValue2 = (IntegerExpression) stackFrame.getOperandAttr(1);
		
		int integerValue1;
		int integerValue2;

		if (symIntegerValue1 == null && symIntegerValue2 == null) {
			return super.execute(threadInfo);  // we'll still do the concrete execution
		}

		// result is symbolic

		if (symIntegerValue1 == null && symIntegerValue2 != null) {
			integerValue1 = stackFrame.pop();
			integerValue2 = stackFrame.pop();
			if (integerValue1 == 0) {
				return threadInfo.createAndThrowException("java.lang.ArithmeticException", "div by 0");
			}
			stackFrame.push(0, false);
			IntegerExpression result = symIntegerValue2._div(integerValue1);
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
			threadInfo.getVM().setNextChoiceGenerator(choiceGenerator);
			
			return this;
		} else { // this is what really returns results
			choiceGenerator = threadInfo.getVM().getChoiceGenerator();
			assert (choiceGenerator instanceof PCChoiceGenerator) : "expected PCChoiceGenerator, got: " + choiceGenerator;
			condition = (Integer) choiceGenerator.getNextChoice() == 0 ? false : true;
		}

		integerValue1 = stackFrame.pop();
		integerValue2 = stackFrame.pop();
		stackFrame.push(0, false);

		PathCondition pathCondition;
		ChoiceGenerator<?> prevChoiceGenerator = choiceGenerator.getPreviousChoiceGeneratorOfType(PCChoiceGenerator.class);

		if (prevChoiceGenerator == null) {
			pathCondition = new PathCondition();
		} else {
			pathCondition = ((PCChoiceGenerator) prevChoiceGenerator).getCurrentPC();
		}
		assert pathCondition != null;

		if (condition) { // check div by zero
			pathCondition._addDet(Comparator.EQ, symIntegerValue1, 0);
			if (pathCondition.simplify()) { // satisfiable
				((PCChoiceGenerator) choiceGenerator).setCurrentPC(pathCondition);

				return threadInfo.createAndThrowException("java.lang.ArithmeticException", "div by 0");
			} else {
				threadInfo.getVM().getSystemState().setIgnored(true);
				
				return getNext(threadInfo);
			}
		} else {
			pathCondition._addDet(Comparator.NE, symIntegerValue1, 0);
			if (pathCondition.simplify()) { // satisfiable
				((PCChoiceGenerator) choiceGenerator).setCurrentPC(pathCondition);

				// set the result
				IntegerExpression result;
				if (symIntegerValue2 != null) {
					result = symIntegerValue2._div(symIntegerValue1);
				} else {
					result = symIntegerValue1._div_reverse(integerValue2);
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