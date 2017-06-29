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

import gov.nasa.jpf.symbc.numeric.IntegerExpression;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * Boolean XOR long
 * ..., value1, value2 => ..., result
 */
public class LXOR extends gov.nasa.jpf.jvm.bytecode.LXOR {

	@Override
	public Instruction execute(ThreadInfo threadInfo) {
		StackFrame stackFrame = threadInfo.getModifiableTopFrame();

		IntegerExpression symLongValue1 = (IntegerExpression) stackFrame.getOperandAttr(1);
		IntegerExpression symLongValue2 = (IntegerExpression) stackFrame.getOperandAttr(3);

		if (symLongValue1 == null && symLongValue2 == null) {
			return super.execute(threadInfo);
		} else {
			long longValue1 = stackFrame.popLong();
			long longValue2 = stackFrame.popLong();
			stackFrame.pushLong(0);

			IntegerExpression symResult = null;
			if (symLongValue1 != null) {
				if (symLongValue2 != null) {
					symResult = symLongValue1._xor(symLongValue2);
				} else {
					symResult = symLongValue1._xor(longValue2);
				}
			} else if (symLongValue2 != null) {
				symResult = symLongValue2._xor(longValue1);
			}
			stackFrame.setLongOperandAttr(symResult);

			return getNext(threadInfo);
		}
	}
}
