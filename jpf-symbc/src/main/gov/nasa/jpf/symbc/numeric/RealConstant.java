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

public class RealConstant extends RealExpression {
	private double value;

	public RealConstant(double value) {
		this.value = value;
	}

	public double getValue() {
		return this.value;
	}

	public RealExpression _plus(double addendum) {
		if (addendum == 0) {
			return this;
		} else {
			return new RealConstant(this.getValue() + addendum);
		}
	}

	public RealExpression _plus(RealExpression addendumExpression) {
		if (addendumExpression instanceof RealConstant) {
			RealConstant addendum = (RealConstant) addendumExpression;
			return this._plus(addendum.getValue());
		} else {
			return super._plus(addendumExpression);
		}
	}

	public RealExpression _minus(double subtrahend) {
		if (subtrahend == 0) {
			return this;
		} else {
			return new RealConstant(this.getValue() - subtrahend);
		}
	}

	public RealExpression _minus(RealExpression subtrahendExpression) {
		if (subtrahendExpression instanceof RealConstant) {
			RealConstant subtrahend = (RealConstant) subtrahendExpression;
			return this._minus(subtrahend.getValue());
		} else {
			return super._minus(subtrahendExpression);
		}
	}

	public RealExpression _mul(double multiplier) {
		if (multiplier == 1) {
			return this;
		} else if (multiplier == 0) {
			return new RealConstant(0);
		} else {
			return new RealConstant(this.getValue() * multiplier);
		}
	}

	public RealExpression _mul(RealExpression multiplierExpression) {
		if (multiplierExpression instanceof RealConstant) {
			RealConstant multiplier = (RealConstant) multiplierExpression;
			return this._mul(multiplier.getValue());
		} else {
			return super._mul(multiplierExpression);
		}
	}

	public RealExpression _div(double divider) {
		assert (divider != 0);
		if (divider == 1) {
			return this;
		} else if (this.getValue() == 0) {
			return new RealConstant(0);
		} else {
			return new RealConstant(this.getValue() / divider);
		}
	}

	public RealExpression _div(RealExpression dividerExpression) {
		if (dividerExpression instanceof RealConstant) {
			RealConstant divider = (RealConstant) dividerExpression;
			return this._div(divider.getValue());
		} else {
			return super._div(dividerExpression);
		}
	}

	public RealExpression _neg() {
		if (this.getValue() == 0) {
			return this;
		} else {
			return new RealConstant(-this.getValue());
		}
	}

	public boolean equals(Object o) {
		if (!(o instanceof RealConstant)) {
			return false;
		} else {
			return this.getValue() == ((RealConstant) o).getValue();
		}
	}

	public String toString() {
		return "CONST_" + this.getValue() + "";
	}

	public String prefix_notation() {
		return "" + this.getValue();
	}

	public String getStringPathCondition() {
		return "CONST_" + this.getValue() + "";
	}

	public double solution() {
		return this.getValue();
	}

	public void getVarsVals(Map<String, Object> varsVals) {
	}

	// JacoGeldenhuys
	@Override
	public void accept(ConstraintExpressionVisitor visitor) {
		visitor.preVisit(this);
		visitor.postVisit(this);
	}

	@Override
	public int compareTo(Expression expr) {
		if (expr instanceof RealConstant) {
			RealConstant e = (RealConstant) expr;
			return Double.compare(this.getValue(), e.getValue());
		} else {
			return getClass().getCanonicalName().compareTo(expr.getClass().getCanonicalName());
		}
	}
}