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

import gov.nasa.jpf.jvm.ChoiceGenerator;
import gov.nasa.jpf.jvm.KernelState;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.jvm.StackFrame;

import gov.nasa.jpf.symbc.numeric.*;

public class IDIV extends gov.nasa.jpf.jvm.bytecode.IDIV {

	@Override
	public Instruction execute (SystemState ss, KernelState ks, ThreadInfo th) {
		StackFrame sf = th.getTopFrame();
		IntegerExpression sym_v1 = (IntegerExpression) sf.getOperandAttr(0);
		IntegerExpression sym_v2 = (IntegerExpression) sf.getOperandAttr(1);
		int v1, v2;


		if(sym_v1==null && sym_v2==null)
			return super.execute(ss, ks, th); // we'll still do the concrete execution

		// result is symbolic

		if(sym_v1==null && sym_v2!=null) {
	    	v1 = th.pop();
	    	v2 = th.pop();
	    	if(v1==0)
				return th.createAndThrowException("java.lang.ArithmeticException","div by 0");
	    	th.push(0, false);
	    	IntegerExpression result = sym_v2._div(v1);
			sf.setOperandAttr(result);
		    return getNext(th);
	    }

		// div by zero check affects path condition
	    // sym_v1 is non-null and should be checked against zero

		ChoiceGenerator<?> cg;
		boolean condition;

		if (!th.isFirstStepInsn()) { // first time around
			cg = new PCChoiceGenerator(2);
			((PCChoiceGenerator)cg).setOffset(this.position);
			((PCChoiceGenerator)cg).setMethodName(this.getMethodInfo().getCompleteName());
			ss.setNextChoiceGenerator(cg);
			return this;
		} else {  // this is what really returns results
			cg = ss.getChoiceGenerator();
			assert (cg instanceof PCChoiceGenerator) : "expected PCChoiceGenerator, got: " + cg;
			condition = (Integer)cg.getNextChoice()==0 ? false: true;
		}


		v1 = th.pop();
		v2 = th.pop();
		th.push(0, false);

		PathCondition pc;
		ChoiceGenerator<?> prev_cg = cg.getPreviousChoiceGenerator();

		while (!((prev_cg == null) || (prev_cg instanceof PCChoiceGenerator))) {
			prev_cg = prev_cg.getPreviousChoiceGenerator();
		}
		if (prev_cg == null)
			pc = new PathCondition();
		else
			pc = ((PCChoiceGenerator)prev_cg).getCurrentPC();

		assert pc != null;

		if(condition) { // check div by zero
			pc._addDet(Comparator.EQ, sym_v1, 0);
			if(pc.simplify())  { // satisfiable
				((PCChoiceGenerator) cg).setCurrentPC(pc);

				return th.createAndThrowException("java.lang.ArithmeticException","div by 0");
			}
			else {
				ss.setIgnored(true);
				return getNext(th);
			}
		}
		else {
			pc._addDet(Comparator.NE, sym_v1, 0);
			if(pc.simplify())  { // satisfiable
				((PCChoiceGenerator) cg).setCurrentPC(pc);

				// set the result
				IntegerExpression result;
				if(sym_v2!=null)
					result = sym_v2._div(sym_v1);
				else
					result = sym_v1._div_reverse(v2);

				sf = th.getTopFrame();
				sf.setOperandAttr(result);
			    return getNext(th);

			}
			else {
				ss.setIgnored(true);
				return getNext(th);
			}
		}

	}
}