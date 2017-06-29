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
 * Subtract long
 * ..., value1, value2 => ..., result
 */
public class LSUB extends gov.nasa.jpf.jvm.bytecode.LSUB {

	@Override
	public Instruction execute(ThreadInfo threadInfo) {
		StackFrame stackFrame = threadInfo.getModifiableTopFrame();

		IntegerExpression symValue1 = (IntegerExpression) stackFrame.getOperandAttr(1);
		IntegerExpression symValue2 = (IntegerExpression) stackFrame.getOperandAttr(3);

		if (symValue1 == null && symValue2 == null) {
			return super.execute(threadInfo);
		} else {
			long longValue1 = stackFrame.popLong();
			long longValue2 = stackFrame.popLong();
			stackFrame.pushLong(0); 

			IntegerExpression result = null;
			if (symValue2 != null) {
				if (symValue1 != null) {
					result = symValue2._minus(symValue1);
				} else {
					result = symValue2._minus(longValue1);
				}
			} else if (symValue1 != null) {
				result = symValue1._minus_reverse(longValue2);
			}

			stackFrame.setLongOperandAttr(result);

			return getNext(threadInfo);
		}
	}
}
