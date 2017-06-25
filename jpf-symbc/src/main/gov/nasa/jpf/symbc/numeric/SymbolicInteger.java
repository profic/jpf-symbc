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
//Copyright (C) 2006 United States Government as represented by the
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

import gov.nasa.jpf.symbc.SymbolicInstructionFactory;

import java.util.Map;
import java.util.Random;

public class SymbolicInteger extends LinearIntegerExpression {
	public static long UNDEFINED = 0;
	public long solution = UNDEFINED; // C

	private int unique_id;

	public static String SYM_INT_SUFFIX = "_SYMINT";

	private long min = 0;
	private long max = 0;
	private String name;

	public SymbolicInteger() {
		this(MinMax.getMinInt(), MinMax.getMaxInt());
	}

	public SymbolicInteger(String name) {
		this(MinMax.getMinInt(), MinMax.getMaxInt());
		this.name = name;
	}

	public SymbolicInteger(long lowerBound, long upperBound) {
		this(null, lowerBound, upperBound);
	}

	public SymbolicInteger(String name, long lowerBound, long upperBound) {
		super();
		this.unique_id = MinMax.UniqueId++;
		this.min = lowerBound;
		this.max = upperBound;
		this.name = (name != null) ? name : "INT_" + hashCode();
		PathCondition.setSolved(false);
	}

	public long getMin() {
		return min;
	}

	public long getMax() {
		return max;
	}

	public String getName() {
		return (name != null) ? name : "INT_" + hashCode();
	}

	public String getStringPathCondition() {
		return (name != null) ? name : "INT_" + hashCode();
	}

	public String toString() {
		if (!PathCondition.isSolved()) {
			return (name != null) ? name : "INT_" + hashCode();

		} else {
			return (name != null) ? name /* + "[" + solution + "]" */ : "INT_" + hashCode() + "[" + solution + "]";
		}
	}

	public String prefix_notation() {
		return (name != null) ? name : "INT_" + hashCode();
	}

	public long solution() {
		if (PathCondition.isSolved()) {
			if (solution == UNDEFINED && SymbolicInstructionFactory.concolicMode) {
				solution = (new Random().nextLong() % (max - min)) + min;
			}
			return solution;
		} else
			throw new RuntimeException("## Error: PC not solved!");
	}

	public void getVarsVals(Map<String, Object> varsVals) {
		varsVals.put(fixName(name), solution);
	}

	private String fixName(String name) {
		if (name.endsWith(SYM_INT_SUFFIX)) {
			name = name.substring(0, name.lastIndexOf(SYM_INT_SUFFIX));
		}
		return name;
	}

	public boolean equals(Object o) {
		return (o instanceof SymbolicInteger) && (this.equals((SymbolicInteger) o));
	}

	private boolean equals(SymbolicInteger s) {
		// if (name != null)
		// return (this.name.equals(s.name)) &&
		// (this._max == s._max) &&
		// (this._min == s._min);
		// else
		// return (this._max == s._max) &&
		// (this._min == s._min);
		return this.unique_id == s.unique_id;
	}

	public int hashCode() {
		// return Integer.toHexString(_min ^ _max).hashCode();
		return unique_id;
	}

	protected void finalize() throws Throwable {
		// System.out.println("Finalized " + this);
	}

	@Override
	public void accept(ConstraintExpressionVisitor visitor) {
		visitor.preVisit(this);
		visitor.postVisit(this);
	}

	@Override
	public int compareTo(Expression expr) {
		if (expr instanceof SymbolicInteger) {
			SymbolicInteger e = (SymbolicInteger) expr;
			int a = unique_id;
			int b = e.unique_id;
			return (a < b) ? -1 : (a > b) ? 1 : 0;
		} else {
			return getClass().getCanonicalName().compareTo(expr.getClass().getCanonicalName());
		}
	}
}
