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
 * Negate float ..., value => ..., result
 */
public class FNEG extends gov.nasa.jpf.jvm.bytecode.FNEG {

	@Override
	public Instruction execute(ThreadInfo threadInfo) {
		StackFrame stackFrame = threadInfo.getModifiableTopFrame();
		RealExpression symValue1 = (RealExpression) stackFrame.getOperandAttr();
		float floatValue1 = Types.intToFloat(stackFrame.pop());

		if (symValue1 == null) {
			stackFrame.push(Types.floatToInt(-floatValue1), false);
		} else {
			stackFrame.push(0, false);
			RealExpression result = symValue1._neg();
			stackFrame.setOperandAttr(result);
		}

		return getNext(threadInfo);
	}
}
