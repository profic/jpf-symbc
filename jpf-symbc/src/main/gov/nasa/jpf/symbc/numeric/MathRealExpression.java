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
//Copyright (C) 2005 United States Government as represented by the
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

package gov.nasa.jpf.symbc.numeric;
// supports math lib

import java.util.Map;

public class MathRealExpression extends RealExpression {
	private RealExpression argument1;
	private RealExpression argument2;
	private MathFunction function;
	// int exp; // for power

	public MathRealExpression(final MathFunction function, final RealExpression argument) {
		assert function == MathFunction.ABS || // Added for dReal by Nima
				function == MathFunction.SIN || function == MathFunction.COS || function == MathFunction.EXP
				|| function == MathFunction.ASIN || function == MathFunction.ACOS || function == MathFunction.ATAN
				|| function == MathFunction.LOG || function == MathFunction.TAN || function == MathFunction.SQRT;

		this.function = function;
		this.argument1 = argument;
	}

	// TODO: to generalize this...
	// public MathRealExpression (MathFunction o, RealExpression a, int e)
	// {
	// op = o;
	// arg1 = a;
	// if (o == MathFunction.POW)
	// exp = e;
	//
	// }

	public MathRealExpression(final MathFunction function, final RealExpression argument1, final double argument2) {
		assert function == MathFunction.POW || function == MathFunction.ATAN2;
		this.function = function;
		this.argument1 = argument1;
		this.argument2 = new RealConstant(argument2);

	}

	public MathRealExpression(final MathFunction function, final double argument1, final RealExpression argument2) {
		assert function == MathFunction.POW || function == MathFunction.ATAN2;
		this.function = function;
		this.argument1 = new RealConstant(argument1);
		this.argument2 = argument2;

	}

	public MathRealExpression(final MathFunction function, final RealExpression argument1,
			final RealExpression argument2) {
		assert function == MathFunction.POW || function == MathFunction.ATAN2;
		this.function = function;
		this.argument1 = argument1;
		this.argument2 = argument2;

	}

	public RealExpression getArgument1() {
		return argument1;
	}

	public RealExpression getArgument2() {
		return argument2;
	}

	public MathFunction getFunction() {
		return function;
	}

	@Override
	public double solution() {
		final double argumentSolution1 = (argument1 == null ? 0 : argument1.solution());
		final double argumentSolution2 = (argument2 == null ? 0 : argument2.solution());

		switch (function) {
		case ABS:
			return Math.abs(argumentSolution1); // Added for dReal by Nima
		case COS:
			return Math.cos(argumentSolution1);
		case SIN:
			return Math.sin(argumentSolution1);
		case EXP:
			return Math.exp(argumentSolution1);
		case ASIN:
			return Math.asin(argumentSolution1);
		case ACOS:
			return Math.acos(argumentSolution1);
		case ATAN:
			return Math.atan(argumentSolution1);
		case LOG:
			return Math.log(argumentSolution1);
		case TAN:
			return Math.tan(argumentSolution1);
		case SQRT:
			return Math.sqrt(argumentSolution1);
		case POW:
			return Math.pow(argumentSolution1, argumentSolution2);
		case ATAN2:
			return Math.atan2(argumentSolution1, argumentSolution2);
		default:
			throw new RuntimeException("## Error: MathRealExpression solution: math function " + function);
		}
	}

	@Override
	public void getVarsVals(final Map<String, Object> varsVals) {
		if (argument1 != null) {
			argument1.getVarsVals(varsVals);
		}
		if (argument2 != null) {
			argument2.getVarsVals(varsVals);
		}
	}

	@Override
	public String getStringPathCondition() {
		if (function == MathFunction.ABS || // Added for dReal by Nima
				function == MathFunction.SIN || function == MathFunction.COS || function == MathFunction.EXP
				|| function == MathFunction.ASIN || function == MathFunction.ACOS || function == MathFunction.ATAN
				|| function == MathFunction.LOG || function == MathFunction.TAN || function == MathFunction.SQRT) {
			return "(" + function.toString() + "(" + argument1.getStringPathCondition() + "))";
		} else {
			// op == MathFunction.POW || op == MathFunction.ATAN2
			return "(" + function.toString() + "(" + argument1.getStringPathCondition() + ","
					+ argument2.getStringPathCondition() + "))";
		}
	}

	@Override
	public String toString() {
		if (function == MathFunction.ABS || // Added for dReal by Nima
				function == MathFunction.SIN || function == MathFunction.COS || function == MathFunction.EXP
				|| function == MathFunction.ASIN || function == MathFunction.ACOS || function == MathFunction.ATAN
				|| function == MathFunction.LOG || function == MathFunction.TAN || function == MathFunction.SQRT) {
			return function.toString() + "(" + argument1.toString() + ")";
		} else {
			// op == MathFunction.POW || op == MathFunction.ATAN2
			return function.toString() + "(" + argument1.toString() + "," + argument2.toString() + ")";
		}
	}

	@Override
	public void accept(final ConstraintExpressionVisitor visitor) {
		visitor.preVisit(this);
		if (argument1 != null) {
			argument1.accept(visitor);
		}
		if (argument2 != null) {
			argument2.accept(visitor);
		}
		visitor.postVisit(this);
	}

	@Override
	public int compareTo(final Expression expression) {
		if (expression instanceof MathRealExpression) {
			final MathRealExpression e = (MathRealExpression) expression;
			int result = getFunction().compareTo(e.getFunction());

			if (result == 0) {
				result = compareArguments(getArgument1(), e.getArgument1());
			}
			if (result == 0) {
				result = compareArguments(getArgument2(), e.getArgument2());
			}
			
			return result;
		} else {
			return getClass().getCanonicalName().compareTo(expression.getClass().getCanonicalName());
		}
	}

	private int compareArguments(RealExpression arg1, RealExpression arg2) {
		int result = 0;

		if (arg1 != null) {
			if (arg2 == null) {
				result = 1;
			} else {
				result = arg1.compareTo(arg2);
			}
		} else if (arg2 != null) {
			result = -1;
		}

		return result;
	}
}
