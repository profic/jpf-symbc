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
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * Convert long to float ..., value => ..., result
 */
public class L2F extends gov.nasa.jpf.jvm.bytecode.L2F {
	@Override
	public Instruction execute(ThreadInfo threadInfo) {
		IntegerExpression symLongValue = (IntegerExpression) threadInfo.getModifiableTopFrame().getLongOperandAttr();
		if (symLongValue == null) {
			return super.execute(threadInfo);
		} else {
			// throw new RuntimeException("## Error: symbolic L2F not yet hanled
			// ");

			// here we get a hold of the current path condition and
			// add an extra mixed constraint sym_dval==sym_ival

			ChoiceGenerator<?> choiceGenerator;
			if (!threadInfo.isFirstStepInsn()) { // first time around
				choiceGenerator = new PCChoiceGenerator(1); // only one choice
				threadInfo.getVM().getSystemState().setNextChoiceGenerator(choiceGenerator);

				return this;
			} else { // this is what really returns results
				choiceGenerator = threadInfo.getVM().getSystemState().getChoiceGenerator();
				assert (choiceGenerator instanceof PCChoiceGenerator) : "expected PCChoiceGenerator, got: "
						+ choiceGenerator;
			}

			// get the path condition from the
			// previous choice generator of the same type

			PathCondition pathCondition;
			ChoiceGenerator<?> prevChoiceGenerator = choiceGenerator.getPreviousChoiceGenerator();
			while (!((prevChoiceGenerator == null) || (prevChoiceGenerator instanceof PCChoiceGenerator))) {
				prevChoiceGenerator = prevChoiceGenerator.getPreviousChoiceGenerator();
			}

			if (prevChoiceGenerator == null) {
				pathCondition = new PathCondition(); // TODO: handling of
														// preconditions needs
														// to be changed
			} else {
				pathCondition = ((PCChoiceGenerator) prevChoiceGenerator).getCurrentPC();
			}
			assert pathCondition != null;
			StackFrame stackFrame = threadInfo.getModifiableTopFrame();
			stackFrame.popLong();
			stackFrame.push(0, false); // for symbolic expressions, the concrete
										// value does not matter
			SymbolicReal symFloatValue = new SymbolicReal();

			stackFrame.setOperandAttr(symFloatValue);

			pathCondition._addDet(Comparator.EQ, symFloatValue, symLongValue);

			if (!pathCondition.simplify()) { // not satisfiable
				threadInfo.getVM().getSystemState().setIgnored(true);
			} else {
				// pc.solve();
				((PCChoiceGenerator) choiceGenerator).setCurrentPC(pathCondition);
			}

			return getNext(threadInfo);
		}
	}
}
