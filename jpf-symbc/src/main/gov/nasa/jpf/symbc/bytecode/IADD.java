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
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

public class IADD extends gov.nasa.jpf.jvm.bytecode.IADD {

	@Override
	public Instruction execute(ThreadInfo threadInfo) {
		StackFrame stackFrame = threadInfo.getModifiableTopFrame();
		IntegerExpression symIntegerValue1 = (IntegerExpression) stackFrame.getOperandAttr(0);
		IntegerExpression symIntegerValue2 = (IntegerExpression) stackFrame.getOperandAttr(1);

		if (symIntegerValue1 == null && symIntegerValue2 == null) {
			return super.execute(threadInfo);  // we'll still do the concrete execution
		} else {
			int integerValue1 = stackFrame.pop();
			int integerValue2 = stackFrame.pop();
			stackFrame.push(0, false);  // for symbolic expressions, the concrete value does not matter

			IntegerExpression symResult = null;
			if (symIntegerValue1 != null) {
				if (symIntegerValue2 != null) {
					symResult = symIntegerValue1._plus(symIntegerValue2);
				} else {  // v2 is concrete
					symResult = symIntegerValue1._plus(integerValue2);
				}
			} else if (symIntegerValue2 != null) {
				symResult = symIntegerValue2._plus(integerValue1);
			}
			stackFrame.setOperandAttr(symResult);

			return getNext(threadInfo);
		}
	}
}
