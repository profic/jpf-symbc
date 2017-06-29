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

import gov.nasa.jpf.symbc.numeric.IntegerConstant;
import gov.nasa.jpf.symbc.numeric.IntegerExpression;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * Arithmetic shift right long
 * ..., value1, value2  =>..., result
 */
public class LSHR extends gov.nasa.jpf.jvm.bytecode.LSHR {
	@Override
	public Instruction execute(ThreadInfo threadInfo) {
		StackFrame stackFrame = threadInfo.getModifiableTopFrame();

		IntegerExpression symLongValue1 = (IntegerExpression) stackFrame.getOperandAttr(0);
		IntegerExpression symLongValue2 = (IntegerExpression) stackFrame.getOperandAttr(2);

		if (symLongValue1 == null && symLongValue2 == null) {
			return super.execute(threadInfo);
		} else {
			int intValue1 = stackFrame.pop();
			long longValue2 = stackFrame.popLong();
			stackFrame.pushLong(0); 

			IntegerExpression result = null;
			if (symLongValue1 != null) {
				if (symLongValue2 != null) {
					//result = sym_v1._shiftR(sym_v2);
					result = symLongValue2._shiftR(symLongValue1);
				} else { // v2 is concrete
								//result = sym_v1._shiftR(v2);
					result = (new IntegerConstant((int) longValue2))._shiftR(symLongValue1);
				}
			} else if (symLongValue2 != null) {
				result = symLongValue2._shiftR(intValue1);
			}

			stackFrame.setLongOperandAttr(result);
			
			return getNext(threadInfo);
		}
	}
}
