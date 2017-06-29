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

public class DADD extends gov.nasa.jpf.jvm.bytecode.DADD {

	@Override
	public Instruction execute(ThreadInfo threadInfo) {
		StackFrame stackFrame = threadInfo.getModifiableTopFrame();

		RealExpression symDoubleValue1 = (RealExpression) stackFrame.getLongOperandAttr();
		RealExpression symDoubleValue2 = (RealExpression) stackFrame.getLongOperandAttr();
		
		double doubleValue1 = Types.longToDouble(stackFrame.popLong());
		double doubleValue2 = Types.longToDouble(stackFrame.popLong());

		double doubleResult = doubleValue1 + doubleValue2;

		if (symDoubleValue1 == null && symDoubleValue2 == null) {
			stackFrame.pushLong(Types.doubleToLong(doubleResult));
		} else {
			stackFrame.pushLong(0);
		}

		RealExpression symResult = null;
		if (symDoubleValue1 != null) {
			if (symDoubleValue2 != null) {
				symResult = symDoubleValue2._plus(symDoubleValue1);
			} else {
				// v2 is concrete
				symResult = symDoubleValue1._plus(doubleValue2);
			}
		} else if (symDoubleValue2 != null) {
			symResult = symDoubleValue2._plus(doubleValue1);
		}

		stackFrame.setLongOperandAttr(symResult);

		return getNext(threadInfo);
	}
}
