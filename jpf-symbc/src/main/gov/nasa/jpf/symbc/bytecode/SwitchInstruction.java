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
//Copyright (C) 2007 United States Government as represented by the
//Administrator of the National Aeronautics and Space Administration
//(NASA).  All Rights Reserved.
//
//This software is distributed under the NASA Open Source Agreement
//(NOSA), version 1.3.  The NOSA has been approved by the Open Source
//Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
//directory tree for the complete NOSA document.
//
//THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
//KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
//LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
//SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
//A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
//THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
//DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//

package gov.nasa.jpf.symbc.bytecode;

import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.IntegerExpression;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * common root class for LOOKUPSWITCH and TABLESWITCH insns
 */

public abstract class SwitchInstruction extends gov.nasa.jpf.jvm.bytecode.SwitchInstruction {
	protected SwitchInstruction(int defaultTarget, int numberOfTargets) {
		super(defaultTarget, numberOfTargets);
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("deprecation")
	@Override
	public Instruction execute(ThreadInfo threadInfo) {
		StackFrame stackFrame = threadInfo.getModifiableTopFrame();
		IntegerExpression symIntegerValue = (IntegerExpression) stackFrame.getOperandAttr();

		if (symIntegerValue == null) {
			return super.execute(threadInfo);
		} else {
			ChoiceGenerator<?> choiceGenerator;

			if (!threadInfo.isFirstStepInsn()) {
				choiceGenerator = new PCChoiceGenerator(matches.length + 1);
				((PCChoiceGenerator) choiceGenerator).setOffset(this.position);
				((PCChoiceGenerator) choiceGenerator).setMethodName(this.getMethodInfo().getCompleteName());
				threadInfo.getVM().getSystemState().setNextChoiceGenerator(choiceGenerator);

				return this;
			} else { // this is what really returns results
				choiceGenerator = threadInfo.getVM().getSystemState().getChoiceGenerator();
				assert (choiceGenerator instanceof PCChoiceGenerator) : "expected PCChoiceGenerator, got: "
						+ choiceGenerator;
			}
			symIntegerValue = (IntegerExpression) stackFrame.getOperandAttr();
			stackFrame.pop();
			PathCondition pathCondition;
			// pc is updated with the pc stored in the choice generator above
			// get the path condition from the
			// previous choice generator of the same type

			// TODO: could be optimized to not do this for each choice
			ChoiceGenerator<?> prevChoiceGenerator = choiceGenerator
					.getPreviousChoiceGeneratorOfType(PCChoiceGenerator.class);

			if (prevChoiceGenerator == null) {
				pathCondition = new PathCondition();
			} else {
				pathCondition = ((PCChoiceGenerator) prevChoiceGenerator).getCurrentPC();
			}
			assert pathCondition != null;
			int idx = (Integer) choiceGenerator.getNextChoice();
			if (idx == matches.length) { // default branch
				lastIdx = DEFAULT;
				for (int i = 0; i < matches.length; i++)
					pathCondition._addDet(Comparator.NE, symIntegerValue, matches[i]);
				if (!pathCondition.simplify()) { // not satisfiable
					threadInfo.getVM().getSystemState().setIgnored(true);
				} else {
					// pc.solve();
					((PCChoiceGenerator) choiceGenerator).setCurrentPC(pathCondition);
					// System.out.println(((PCChoiceGenerator)
					// cg).getCurrentPC());
				}
				return mi.getInstructionAt(target);
			} else {
				lastIdx = idx;
				// System.out.println("index "+idx);
				pathCondition._addDet(Comparator.EQ, symIntegerValue, matches[idx]);
				// System.out.println(sym_v + "eq"+ matches[idx]);
				// System.out.println("pc after "+pc);
				if (!pathCondition.simplify()) {// not satisfiable
					threadInfo.getVM().getSystemState().setIgnored(true);
				} else {
					// pc.solve();
					((PCChoiceGenerator) choiceGenerator).setCurrentPC(pathCondition);
					// System.out.println(((PCChoiceGenerator)
					// cg).getCurrentPC());
				}
				return mi.getInstructionAt(targets[idx]);
			}
		}
	}
}
