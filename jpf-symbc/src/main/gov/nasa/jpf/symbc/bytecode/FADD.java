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
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Types;

public class FADD extends gov.nasa.jpf.jvm.bytecode.FADD {

	@Override
	public Instruction execute(ThreadInfo threadInfo) {
		StackFrame stackFrame = threadInfo.getModifiableTopFrame();

		RealExpression symFloatValue1 = (RealExpression) stackFrame.getOperandAttr();
		RealExpression symFloatValue2 = (RealExpression) stackFrame.getOperandAttr();
		
		float floatValue1 = Types.intToFloat(stackFrame.pop());
		float floatValue2 = Types.intToFloat(stackFrame.pop());

		float floatResult = floatValue1 + floatValue2;

		if (symFloatValue1 == null && symFloatValue2 == null) {
			stackFrame.push(Types.floatToInt(floatResult), false);
		} else {
			stackFrame.push(0, false);
		}
		
		RealExpression symResult = null;
		if (symFloatValue1 != null) {
			if (symFloatValue2 != null) {
				symResult = symFloatValue2._plus(symFloatValue1);
			} else { // v2 is concrete
				symResult = symFloatValue1._plus(floatValue2);
			}
		} else if (symFloatValue2 != null) {
			symResult = symFloatValue2._plus(floatValue1);
		}
		
		stackFrame.setOperandAttr(symResult);

		return getNext(threadInfo);
	}
}
