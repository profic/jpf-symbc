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

/*  Copyright (C) 2005 United States Government as represented by the
Administrator of the National Aeronautics and Space Administration
(NASA).  All Rights Reserved.

Copyright (C) 2009 Fujitsu Laboratories of America, Inc. 

DISCLAIMER OF WARRANTIES AND LIABILITIES; WAIVER AND INDEMNIFICATION

A. No Warranty: THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY
WARRANTY OF ANY KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY,
INCLUDING, BUT NOT LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE
WILL CONFORM TO SPECIFICATIONS, ANY IMPLIED WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR FREEDOM FROM
INFRINGEMENT, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL BE ERROR
FREE, OR ANY WARRANTY THAT DOCUMENTATION, IF PROVIDED, WILL CONFORM TO
THE SUBJECT SOFTWARE. NO SUPPORT IS WARRANTED TO BE PROVIDED AS IT IS PROVIDED "AS-IS". 

B. Waiver and Indemnity: RECIPIENT AGREES TO WAIVE ANY AND ALL CLAIMS
AGAINST FUJITSU LABORATORIES OF AMERICA AND ANY OF ITS AFFILIATES, THE 
UNITED STATES GOVERNMENT, ITS CONTRACTORS AND SUBCONTRACTORS, AS WELL 
AS ANY PRIOR RECIPIENT.  IF RECIPIENT'S USE OF THE SUBJECT SOFTWARE 
RESULTS IN ANY LIABILITIES, DEMANDS, DAMAGES, EXPENSES OR LOSSES ARISING 
FROM SUCH USE, INCLUDING ANY DAMAGES FROM PRODUCTS BASED ON, OR RESULTING 
FROM, RECIPIENT'S USE OF THE SUBJECT SOFTWARE, RECIPIENT SHALL INDEMNIFY 
AND HOLD HARMLESS FUJITSU LABORATORTIES OF AMERICA AND ANY OF ITS AFFILIATES, 
THE UNITED STATES GOVERNMENT, ITS CONTRACTORS AND SUBCONTRACTORS, AS WELL 
AS ANY PRIOR RECIPIENT, TO THE EXTENT PERMITTED BY LAW.  RECIPIENT'S SOLE
REMEDY FOR ANY SUCH MATTER SHALL BE THE IMMEDIATE, UNILATERAL
TERMINATION OF THIS AGREEMENT.

*/

package gov.nasa.jpf.symbc.string;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.symbc.numeric.ConstraintExpressionVisitor;
import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.symbc.numeric.IntegerExpression;

public class DerivedStringExpression extends StringExpression {

	private StringExpression left;
	private StringOperator operator;
	private StringExpression right;

	public Expression[] oprlist;

	public DerivedStringExpression(StringExpression left, StringOperator operator, StringExpression right) {
		this.oprlist = null;
		this.left = left;
		this.operator = operator;
		this.right = right;
		// left.addDependent(this);
		// right.addDependent(this);
	}

	public DerivedStringExpression(StringOperator operator, Expression[] oprlist) {
		this.operator = operator;
		this.oprlist = oprlist.clone();
		// if(olist[j] instanceof StringExpression){
		// ((StringExpression) oprlist[j]).addDependent(this);
		// }
	}

	public DerivedStringExpression(StringOperator operator, StringExpression right) {
		this.operator = operator;
		this.right = right;
		// right.addDependent(this);
	}

	public StringExpression getLeft() {
		return left;
	}

	public StringOperator getOperator() {
		return operator;
	}

	public StringExpression getRight() {
		return right;
	}

	public DerivedStringExpression clone() {
		throw new RuntimeException("Operation not implemented");
	}

	public Set<Expression> getOperands() {
		Set<Expression> operands = new HashSet<Expression>();
		if (getRight() != null) {
			operands.add(getRight());
		}
		if (getLeft() != null) {
			operands.add(getLeft());
		}
		if (oprlist != null) {
			for (Expression e : oprlist) {
				operands.add(e);
			}
		}
		return operands;
	}

	// TODO: add solution() cases for all supported operators
	/*
	 * REPLACEALL("replaceall"), TOLOWERCASE("tolowercase"),
	 * TOUPPERCASE("touppercase"),
	 */
	public String solution() {
		switch (operator) {
		case CONCAT:
			return solveConcat();
		case REPLACE:
			return solveReplace();
		case REPLACEFIRST:
			return solveReplaceFirst();
		case TRIM:
			return solveTrim();
		case SUBSTRING:
			return solveSubstring();
		case VALUEOF:
			return solveValueOf();
		default:
			throw new RuntimeException("## Error: BinaryStringSolution solution: l " + left.solution() + " op "
					+ getOperator() + " r " + right.solution());
		}
	}

	public void getVarsVals(Map<String, Object> varsVals) {
		if (getLeft() != null) {
			getLeft().getVarsVals(varsVals);
		}
		getRight().getVarsVals(varsVals);
	}

	public String getStringPathCondition() {
		if (getLeft() != null)
			return getLeft().getStringPathCondition() + "." + getOperator().toString() + "("
					+ getRight().getStringPathCondition() + ")";
		else if (getRight() != null)
			return "." + getOperator().toString() + "(" + getRight().getStringPathCondition() + ")";
		else {
			StringBuilder s = new StringBuilder();
			s.append("{");
			for (int i = 0; i < oprlist.length; i++) {
				s.append("(");
				s.append(oprlist[i].toString());
				s.append(")");
			}
			s.append("}");
			return "." + getOperator().toString() + s;

		}
	}

	public String toString() {
		if (getLeft() != null)
			return getLeft().toString() + "." + getOperator().toString() + "(" + getRight().toString() + ")";
		else if (getRight() != null)
			return "." + getOperator().toString() + "(" + getRight().toString() + ")";
		else {
			StringBuilder s = new StringBuilder();
			s.append("[");
			for (int i = 0; i < oprlist.length; i++) {
				s.append("(");
				s.append(oprlist[i].toString());
				s.append(")");
			}
			s.append("]");
			return "." + getOperator().toString() + s;
		}

	}

	public String getName() {
		String name;
		if (getLeft() != null)
			name = getLeft().getName() + "_" + getOperator().toString() + "__" + getRight().getName() + "___";
		else if (getRight() != null)
			name = "_" + getOperator().toString() + "__" + getRight().getName() + "___";
		else {
			StringBuilder s = new StringBuilder();
			s.append("__");
			for (int i = 0; i < oprlist.length; i++) {
				s.append("__");
				if (oprlist[i] instanceof StringExpression) {
					s.append(((StringExpression) oprlist[i]).getName());
				} else {
					s.append(oprlist[i].toString());
				}
				s.append("___");
			}
			s.append("___");
			name = "_" + getOperator().toString() + s;
		}

		return "STRING_" + name;
	}

	@Override
	public void accept(ConstraintExpressionVisitor visitor) {
		visitor.preVisit(this);
		if (getLeft() != null) {
			getLeft().accept(visitor);
		}
		if (getRight() != null) {
			getRight().accept(visitor);
		}
		if (oprlist != null) {
			for (Expression e : oprlist) {
				e.accept(visitor);
			}
		}
		visitor.postVisit(this);
	}

	private String solveConcat() {
		String leftString = left != null ? left.solution() : new String();
		String rightString = right != null ? right.solution() : new String();

		return leftString.concat(rightString);
	}

	private String solveReplace() {
		String baseString = ((StringExpression) oprlist[0]).solution();
		String target = ((StringExpression) oprlist[1]).solution();
		String replacement = ((StringExpression) oprlist[2]).solution();

		return baseString.replace(target, replacement);
	}

	private String solveReplaceFirst() {
		String baseString = ((StringExpression) oprlist[0]).solution();
		String target = ((StringExpression) oprlist[1]).solution();
		String replacement = ((StringExpression) oprlist[2]).solution();

		return baseString.replaceFirst(target, replacement);
	}

	private String solveSubstring() {
		String baseString = ((StringExpression) oprlist[0]).solution();
		int beginIndex = (int) ((IntegerExpression) oprlist[1]).solution();

		if (oprlist.length < 3) {
			return baseString.substring(beginIndex);
		} else {
			int endIndex = (int) ((IntegerExpression) oprlist[2]).solution();
			return baseString.substring(beginIndex, endIndex);
		}
	}

	private String solveValueOf() {
		return String.valueOf(oprlist[0]);
	}

	private String solveTrim() {
		String rightString = right != null ? right.solution() : new String();
		return rightString.trim();
	}
}
