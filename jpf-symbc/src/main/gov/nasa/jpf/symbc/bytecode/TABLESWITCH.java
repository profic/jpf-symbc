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

import gov.nasa.jpf.JPFException;

import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.IntegerExpression;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;

import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;

/**
 * Access jump table by index and jump ..., index ...
 */
public class TABLESWITCH extends SwitchInstruction implements gov.nasa.jpf.vm.bytecode.TableSwitchInstruction {

	int min, max;

	public TABLESWITCH(int defaultTarget, int min, int max) {
		super(defaultTarget, (max - min + 1));
		this.min = min;
		this.max = max;
	}

	@Override
	public Instruction execute(ThreadInfo threadInfo) {
		StackFrame stackFrame = threadInfo.getModifiableTopFrame();
		IntegerExpression symValue = (IntegerExpression) stackFrame.getOperandAttr();

		if (symValue == null) {
			return super.execute(threadInfo);
		}
		// the condition is symbolic
		ChoiceGenerator<?> choiceGenerator;

		if (!threadInfo.isFirstStepInsn()) { // first time around
			choiceGenerator = new PCChoiceGenerator(targets.length + 1);
			((PCChoiceGenerator) choiceGenerator).setOffset(this.position);
			((PCChoiceGenerator) choiceGenerator).setMethodName(this.getMethodInfo().getFullName());
			threadInfo.getVM().getSystemState().setNextChoiceGenerator(choiceGenerator);

			return this;
		} else { // this is what really returns results
			choiceGenerator = threadInfo.getVM().getSystemState().getChoiceGenerator();
			assert (choiceGenerator instanceof PCChoiceGenerator) : "expected PCChoiceGenerator, got: "
					+ choiceGenerator;
		}
		symValue = (IntegerExpression) stackFrame.getOperandAttr();
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
		// System.out.println("Execute Switch: PC"+pc);
		int idx = (Integer) choiceGenerator.getNextChoice();
		// System.out.println("Execute Switch: "+ idx);

		if (idx == targets.length) { // default branch
			lastIdx = -1;

			for (int i = 0; i < targets.length; i++) {
				pathCondition._addDet(Comparator.NE, symValue._minus(min), i);
			}
			// this could be replaced safely with only one constraint:
			// pc._addDet(Comparator.GT, sym_v._minus(min), targets.length);

			if (!pathCondition.simplify()) {// not satisfiable
				threadInfo.getVM().getSystemState().setIgnored(true);
			} else {
				// pc.solve();
				((PCChoiceGenerator) choiceGenerator).setCurrentPC(pathCondition);
				// System.out.println(((PCChoiceGenerator) cg).getCurrentPC());
			}

			return mi.getInstructionAt(target);
		} else {
			lastIdx = idx;
			pathCondition._addDet(Comparator.EQ, symValue._minus(min), idx);
			if (!pathCondition.simplify()) {// not satisfiable
				threadInfo.getVM().getSystemState().setIgnored(true);
			} else {
				// pc.solve();
				((PCChoiceGenerator) choiceGenerator).setCurrentPC(pathCondition);
				// System.out.println(((PCChoiceGenerator) cg).getCurrentPC());
			}
			return mi.getInstructionAt(targets[idx]);
		}
	}

	@Override
	protected Instruction executeConditional(ThreadInfo threadInfo) {
		StackFrame stackFrame = threadInfo.getModifiableTopFrame();
		int value = stackFrame.pop();
		int i = value - min;
		int patchCondition;

		if (i >= 0 && i < targets.length) {
			lastIdx = i;
			patchCondition = targets[i];
		} else {
			lastIdx = -1;
			patchCondition = target;
		}

		// <2do> this is BAD - we should compute the target insns just once
		return mi.getInstructionAt(patchCondition);
	}

	@Override
	public void setTarget(int value, int target) {
		int i = value - min;

		if (i >= 0 && i < targets.length) {
			targets[i] = target;
		} else {
			throw new JPFException("illegal tableswitch target: " + value);
		}
	}

	@Override
	public int getByteCode() {
		// TODO Auto-generated method stub
		return 0xAA;
	}
}
