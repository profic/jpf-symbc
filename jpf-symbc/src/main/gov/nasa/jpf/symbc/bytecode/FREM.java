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

import gov.nasa.jpf.symbc.numeric.RealExpression;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Types;

/**
 * Remainder float ..., value1, value2 => ..., result
 */
public class FREM extends gov.nasa.jpf.jvm.bytecode.FREM {

	@Override
	public Instruction execute(ThreadInfo threadInfo) {
		StackFrame stackFrame = threadInfo.getModifiableTopFrame();
		RealExpression symFloatValue1 = (RealExpression) stackFrame.getOperandAttr();
		float floatValue1 = Types.intToFloat(stackFrame.pop());

		RealExpression symValue2 = (RealExpression) stackFrame.getOperandAttr();
		float floatValue2 = Types.intToFloat(stackFrame.pop());

		if (symFloatValue1 == null && symValue2 == null) {
			if (floatValue1 == 0) {
				return threadInfo.createAndThrowException("java.lang.ArithmeticException", "division by zero");
			}
			stackFrame.push(Types.floatToInt(floatValue2 % floatValue1), false);
		} else {
			stackFrame.push(0, false);
			throw new RuntimeException("## Error: SYMBOLIC FREM not supported");
		}

		return getNext(threadInfo);
	}
}
