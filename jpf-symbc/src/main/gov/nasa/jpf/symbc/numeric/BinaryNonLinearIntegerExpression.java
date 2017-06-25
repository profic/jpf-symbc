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

import java.util.Map;

/**
 * @author Sarfraz Khurshid (khurshid@lcs.mit.edu)
 *
 */
public class BinaryNonLinearIntegerExpression extends NonLinearIntegerExpression {

	private IntegerExpression left;
	private Operator operator;
	private IntegerExpression right;

	public BinaryNonLinearIntegerExpression(IntegerExpression left, Operator operator, IntegerExpression right) {
		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	public IntegerExpression getLeft() {
		return left;
	}
	
	public Operator getOperator() {
		return operator;
	}
	
	public IntegerExpression getRight() {
		return right;
	}
	
	public long solution() {
		long leftSolution = getLeft().solution();
		long rightSolution = getRight().solution();
		
		switch (getOperator()) {
		case PLUS:
			return leftSolution + rightSolution;
		case MINUS:
			return leftSolution - rightSolution;
		case MUL:
			return leftSolution * rightSolution;
		case DIV:
			return leftSolution / rightSolution;
		case AND:
			return leftSolution & rightSolution;
		case OR:
			return leftSolution | rightSolution;
		case XOR:
			return leftSolution ^ rightSolution;
		case SHIFTL:
			return leftSolution << rightSolution;
		case SHIFTR:
			return leftSolution >> rightSolution;
		case SHIFTUR:
			return leftSolution >>> rightSolution;
		default:
			throw new RuntimeException(
					"## Error: BinaryNonLinearSolution solution: l " + leftSolution + " op " + getOperator() + " r " + rightSolution);
		}
	}

	public void getVarsVals(Map<String, Object> varsVals) {
		getLeft().getVarsVals(varsVals);
		getRight().getVarsVals(varsVals);
	}

	public String getStringPathCondition() {
		return "(" + getLeft().getStringPathCondition() + getOperator().toString() + getRight().getStringPathCondition()
				+ ")";
	}

	public String toString() {
		return "(" + getLeft().toString() + getOperator().toString() + getRight().toString() + ")";
	}

	public String prefix_notation() {
		return "(" + getOperator().prefix_notation() + " " + getLeft().prefix_notation() + " "
				+ getRight().prefix_notation() + ")";
	}

	// JacoGeldenhuys
	@Override
	public void accept(ConstraintExpressionVisitor visitor) {
		visitor.preVisit(this);
		getLeft().accept(visitor);
		getRight().accept(visitor);
		visitor.postVisit(this);
	}

	@Override
	public int compareTo(Expression expr) {
		if (expr instanceof BinaryNonLinearIntegerExpression) {
			BinaryNonLinearIntegerExpression e = (BinaryNonLinearIntegerExpression) expr;
			int r = getOperator().compareTo(e.getOperator());
			
			if (r == 0) {
				r = getLeft().compareTo(e.getLeft());
			}
			if (r == 0) {
				r = getRight().compareTo(e.getRight());
			}
			
			return r;
		} else {
			return getClass().getCanonicalName().compareTo(expr.getClass().getCanonicalName());
		}
	}
}
