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
TERMINATION OF THIS AGREEMENT. */

package gov.nasa.jpf.symbc.string;

import java.util.Map;

import gov.nasa.jpf.symbc.numeric.ConstraintExpressionVisitor;
import gov.nasa.jpf.symbc.numeric.IntegerExpression;
import gov.nasa.jpf.symbc.numeric.RealExpression;
import gov.nasa.jpf.symbc.numeric.Expression;

public class SymbolicStringBuilder extends Expression {

	private StringExpression stringExpression;

	public SymbolicStringBuilder() {
		super();
		this.stringExpression = null;
	}

	public SymbolicStringBuilder(StringExpression stringExpression) {
		super();
		this.stringExpression = stringExpression;
	}

	public SymbolicStringBuilder(String str) {
		this(new StringConstant(str));
	}
	
	public StringExpression getStringExpression() {
		return stringExpression;
	}

	public void setStringExpression(StringExpression stringExpression) {
		this.stringExpression = stringExpression;
	}
	
	public String getStringPathCondition() {
		return this.stringExpression.getStringPathCondition();
	}

	public SymbolicStringBuilder clone() {
		return new SymbolicStringBuilder((StringExpression) this.stringExpression.clone());
	}
	
	public String toString() {
		return this.stringExpression.toString();
	}

	public String _formattedToString() {
		return this.stringExpression._formattedToString();
	}

	public void _append(SymbolicStringBuilder symbolicStringBuilder) {
		this.stringExpression = this.stringExpression._concat(symbolicStringBuilder.stringExpression);
	}

	public void _append(StringExpression stringExpression) {
		this.stringExpression = this.stringExpression._concat(stringExpression);
	}

	public void _append(IntegerExpression integerExpression) {
		this.stringExpression = this.stringExpression._concat(integerExpression);
	}

	public void _append(RealExpression realExpression) {
		this.stringExpression = this.stringExpression._concat(realExpression);
	}

	public void _append(String string) {
		this.stringExpression = this.stringExpression._concat(new StringConstant(string));
	}

	public void _append(int intValue) {
		this._append(Integer.toString(intValue));
	}

	public void _append(long longValue) {
		this._append(Long.toString(longValue));
	}

	public void _append(float floatValue) {
		this._append(Float.toString(floatValue));
	}

	public void _append(double doubleValue) {
		this._append(Double.toString(doubleValue));
	}

	public void getVarsVals(Map<String, Object> varsVals) {
	}

	// JacoGeldenhuys
	@Override
	public void accept(ConstraintExpressionVisitor visitor) {
		visitor.preVisit(this);
		stringExpression.accept(visitor);
		visitor.postVisit(this);
	}

	@Override
	public int compareTo(Expression expr) {
		if (expr instanceof SymbolicStringBuilder) {
			SymbolicStringBuilder s = (SymbolicStringBuilder) expr;
			return getStringExpression().compareTo(s.getStringExpression());
		} else {
			return getClass().getCanonicalName().compareTo(expr.getClass().getCanonicalName());
		}
	}
}