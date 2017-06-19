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

import static gov.nasa.jpf.symbc.numeric.Operator.*;

public class IntegerConstant extends LinearIntegerExpression {
	private long value;

	public IntegerConstant(long value) {
		this.value = value;
	}

	public long getValue() {
		return this.value;
	}

	public IntegerExpression _plus(long i) {
		if (i == 0) {
			return this;
		} else {
			return new IntegerConstant(this.getValue() + i);
		}
	}

	public IntegerExpression _plus(IntegerExpression addendumExpression) {
		if (addendumExpression instanceof IntegerConstant) {
			IntegerConstant addendum = (IntegerConstant) addendumExpression;
			return this._plus(addendum.getValue());
		} else {
			return super._plus(addendumExpression);
		}
	}

	public IntegerExpression _minus(long subtrahend) {
		if (subtrahend == 0) {
			return this;
		} else {
			return new IntegerConstant(this.getValue() - subtrahend);
		}
	}

	public IntegerExpression _minus(IntegerExpression subtrahendExpression) {
		if (subtrahendExpression instanceof IntegerConstant) {
			IntegerConstant subtrahend = (IntegerConstant) subtrahendExpression;
			return this._minus(subtrahend.getValue());
		} else {
			return super._minus(subtrahendExpression);
		}
	}

	public IntegerExpression _minus_reverse(long minuend) {
		return new IntegerConstant(minuend - this.getValue());
	}

	public IntegerExpression _mul(long multiplier) {
		if (multiplier == 1) {
			return this;
		} else if (multiplier == 0) {
			return new IntegerConstant(0);
		} else {
			return new IntegerConstant(this.getValue() * multiplier);
		}
	}

	public IntegerExpression _mul(IntegerExpression multiplierExpression) {
		if (multiplierExpression instanceof IntegerConstant) {
			IntegerConstant multiplier = (IntegerConstant) multiplierExpression;
			return this._mul(multiplier.getValue());
		} else if (multiplierExpression instanceof LinearIntegerExpression) {
			return new BinaryLinearIntegerExpression(this, MUL, multiplierExpression);
		} else {
			return super._mul(multiplierExpression);
		}
	}

	public IntegerExpression _div(long divider) {
		assert (divider != 0);
		if (divider == 1) {
			return this;
		} else if (this.getValue() == 0) {
			return new IntegerConstant(0);
		} else {
			return new IntegerConstant(this.getValue() / divider);
		}
	}

	public IntegerExpression _div(IntegerExpression dividerExpression) {
		if (dividerExpression instanceof IntegerConstant) {
			IntegerConstant divider = (IntegerConstant) dividerExpression;
			return this._div(divider.getValue());
		} else {
			return super._div(dividerExpression);
		}
	}

	public IntegerExpression _div_reverse(long dividend) {
		assert (this.getValue() != 0);
		return new IntegerConstant(dividend / this.getValue());
	}

	public IntegerExpression _neg() {
		if (this.getValue() == 0) {
			return this;
		} else {
			return super._neg();
		}
	}

	public IntegerExpression _and(long conjunct) {
		if (conjunct == 0) {
			return new IntegerConstant(0);
		} else {
			return new IntegerConstant(this.getValue() & conjunct);
		}
	}

	public IntegerExpression _and(IntegerExpression conjunctExpression) {
		if (conjunctExpression instanceof IntegerConstant) {
			IntegerConstant conjunct = (IntegerConstant) conjunctExpression;
			return this._and(conjunct.getValue());
		} else {
			return new BinaryLinearIntegerExpression(this, AND, conjunctExpression);
		}
	}

	public IntegerExpression _or(long disjunct) {
		if (disjunct == 0) {
			return this;
		} else {
			return new IntegerConstant(this.getValue() | disjunct);
		}
	}

	public IntegerExpression _or(IntegerExpression disjunctExpression) {
		if (disjunctExpression instanceof IntegerConstant) {
			IntegerConstant disjunct = (IntegerConstant) disjunctExpression;
			return this._or(disjunct.getValue());
		} else {
			return new BinaryLinearIntegerExpression(this, OR, disjunctExpression);
		}
	}

	public IntegerExpression _xor(long operand) {
		return new IntegerConstant(this.getValue() ^ operand);
	}

	public IntegerExpression _xor(IntegerExpression operandExpression) {
		if (operandExpression instanceof IntegerConstant) {
			IntegerConstant operand = (IntegerConstant) operandExpression;
			return this._xor(operand.getValue());
		} else {
			return new BinaryLinearIntegerExpression(this, XOR, operandExpression);
		}
	}

	public IntegerExpression _shiftL(long numberOfPositions) {
		return new IntegerConstant(this.getValue() << numberOfPositions);
	}

	public IntegerExpression _shiftL(IntegerExpression numberOfPositionsExpression) {
		if (numberOfPositionsExpression instanceof IntegerConstant) {
			IntegerConstant numberOfPositions = (IntegerConstant) numberOfPositionsExpression;
			return this._shiftL(numberOfPositions.getValue());
		} else {
			return new BinaryLinearIntegerExpression(this, SHIFTL, numberOfPositionsExpression);
		}
	}

	public IntegerExpression _shiftR(long numberOfPositions) {
		return new IntegerConstant(this.getValue() >> numberOfPositions);
	}

	public IntegerExpression _shiftR(IntegerExpression numberOfPositionsExpression) {
		if (numberOfPositionsExpression instanceof IntegerConstant) {
			IntegerConstant numberOfPositions = (IntegerConstant) numberOfPositionsExpression;
			return this._shiftR(numberOfPositions.getValue());
		} else {
			return new BinaryLinearIntegerExpression(this, SHIFTR, numberOfPositionsExpression);
		}
	}

	public IntegerExpression _shiftUR(long numberOfPositions) {
		return new IntegerConstant(this.getValue() >>> numberOfPositions);
	}

	public IntegerExpression _shiftUR(IntegerExpression numberOfPositionsExpression) {
		if (numberOfPositionsExpression instanceof IntegerConstant) {
			IntegerConstant numberOfPositions = (IntegerConstant) numberOfPositionsExpression;
			return this._shiftUR(numberOfPositions.getValue());
		} else {
			return new BinaryLinearIntegerExpression(this, SHIFTUR, numberOfPositionsExpression);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof IntegerConstant)) {
			return false;
		} else {
			return this.getValue() == ((IntegerConstant) o).getValue();
		}
	}

	@Override
	public int hashCode() { // analogous to java.lang.Long
		return (int) (this.getValue() ^ (this.getValue() >>> 32));
	}

	public String toString() {
		return this.getStringPathCondition();
	}

	public String prefix_notation() {
		return "" + this.getValue();
	}

	public String getStringPathCondition() {
		return this.getValue() + "";
	}

	public long solution() { // to be fixed
		return this.getValue();
	}

	public int solutionInt() {
		assert (this.getValue() >= Integer.MIN_VALUE && this.getValue() <= Integer.MAX_VALUE);
		return (int) this.getValue();
	}

	public short solutionShort() {
		assert (this.getValue() >= Short.MIN_VALUE && this.getValue() <= Short.MAX_VALUE);
		return (short) this.getValue();
	}

	public byte solutionByte() {
		assert (this.getValue() >= Byte.MIN_VALUE && this.getValue() <= Byte.MAX_VALUE);
		return (byte) this.getValue();
	}

	public char solutionChar() {
		assert (this.getValue() >= Character.MIN_VALUE && this.getValue() <= Character.MAX_VALUE);
		return (char) this.getValue();
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
		if (expr instanceof IntegerConstant) {
			IntegerConstant e = (IntegerConstant) expr;
			long a = this.getValue();
			long b = e.getValue();
			return (a < b) ? -1 : (a > b) ? 1 : 0;
		} else {
			return getClass().getCanonicalName().compareTo(expr.getClass().getCanonicalName());
		}
	}
}
