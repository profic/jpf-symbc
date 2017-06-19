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

package gov.nasa.jpf.symbc.string;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.util.test.TestJPF;
import gov.nasa.jpf.symbc.SymbolicInstructionFactory;
import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.IntegerConstant;
import gov.nasa.jpf.symbc.numeric.IntegerExpression;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.numeric.SymbolicInteger;
import gov.nasa.jpf.symbc.string.StringComparator;
import gov.nasa.jpf.symbc.string.StringConstant;
import gov.nasa.jpf.symbc.string.StringExpression;
import gov.nasa.jpf.symbc.string.StringPathCondition;
import gov.nasa.jpf.symbc.string.StringSymbolic;
import gov.nasa.jpf.symbc.string.SymbolicLastIndexOfInteger;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;


@RunWith(Parameterized.class)
public class TestNewAutomata extends TestJPF {
	
	@Parameters
	public static Collection<Object[]> solvers() {
		return Arrays.asList(new Object[][] {
				{ AUTOMATA }
		});
	}
	
	private final static String CHOCO = "choco";
	private final static String AUTOMATA = "automata";
	
	private String solver;
	private PathCondition pathCondition;
	private StringPathCondition stringPathCondition;
	
	public TestNewAutomata(String solver) {
		this.solver = solver;
	}
		
	@Before
	public void setUp() {
		String[] options = { "+symbolic.dp=" + CHOCO,
				"+symbolic.string_dp=" + this.solver,
				"+symbolic.string_dp_timeout_ms=0" };
		Config cfg = new Config(options);
		new SymbolicInstructionFactory(cfg);
		pathCondition = new PathCondition();
		stringPathCondition = new StringPathCondition(pathCondition);
	}
	
	@Test
	public void testEQUALS_lengthAndEqualsConstraints() {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		pathCondition._addDet(Comparator.GE, var1._length(), new IntegerConstant(2));
		stringPathCondition._addDet(StringComparator.EQUALS, var2, var1);		
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var1.solution().equals(var2.solution()));
	}
	
	@Test
	public void testEQUALS_twoEqualsConstraints() {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("a"), var1);
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("b"), var2);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var1.solution().equals("a"));
		assertTrue(var2.solution().equals("b"));
	}
	
	@Test
	public void testEQUALS_equalsAndNotEqualsConstraints () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("a"), var1);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, new StringConstant("b"), var2);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var1.solution().equals("a"));
		assertTrue(var2.solution() == null || !var2.solution().equals("b"));
	}
	
	@Test
	public void testNOTEQUALS_threeSymbStringsAndTwoNotEqualsConstraints() {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		StringSymbolic var3 = new StringSymbolic("var3");
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var1, var2);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var2, var3);		
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var1.solution().equals(var2.solution()));
		assertFalse(var2.solution().equals(var3.solution()));
	}
	
	@Test
	public void testNOTEQUALS_threeSymbStringsAndThreeNotEqualsConstraints() {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		StringSymbolic var3 = new StringSymbolic("var3");
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var1, var2);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var2, var3);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var1, var3);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var1.solution().equals(var2.solution()));
		assertFalse(var2.solution().equals(var3.solution()));
		assertFalse(var1.solution().equals(var3.solution()));
	}
	
	@Test
	public void testNOTEQUALS_notEqualsAndEqualsConstraints() {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.NOTEQUALS, new StringConstant("a"), var1);
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("b"), var2);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var1.solution().equals("a"));
		assertTrue(var2.solution().equals("b"));
	}
	
	@Test
	public void testNOTEQUALS_twoNotEqualsConstraints() {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.NOTEQUALS, new StringConstant("a"), var1);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, new StringConstant("b"), var2);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var1.solution().equals("a"));
		assertFalse(var2.solution().equals("b"));
	}
	
	@Test
	public void testNOTEQUALS_pairwiseNotEqualsConstraintsForMultipleSymbStrings() {
		StringSymbolic var[] = new StringSymbolic[10];
		
		for (int i = 0; i < var.length; i++) {
			var[i] = new StringSymbolic("var" + i);
		}
		for (int i = 0; i < var.length - 1; i++) {
			for (int j = i + 1; j < var.length; j++) {
				stringPathCondition._addDet(StringComparator.NOTEQUALS, var[i], var[j]);
			}
		}
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		for (int i = 0; i < var.length - 1; i++) {
			for (int j = i + 1; j < var.length; j++) {
				assertFalse(var[i].solution().equals(var[j].solution()));
			}
		}			
	}
	
	@Test
	public void testSTARTSWITH_twoStartsWithConstraints() {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("a"), var1);
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("b"), var2);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var1.solution().startsWith("a"));
		assertTrue(var2.solution().startsWith("b"));
	}
	
	@Test
	public void testSTARTSWITH_startsWithAndNotStartsWithConstraints() {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("a"), var1);
		stringPathCondition._addDet(StringComparator.NOTSTARTSWITH, new StringConstant("b"), var2);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var1.solution().startsWith("a"));
		assertFalse(var2.solution().startsWith("b"));
	}
	
	@Test
	public void testNOTSTARTSWITH_lengthAndNotStartsWithConstraints() {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		pathCondition._addDet(Comparator.GE, var1._length(), new IntegerConstant(2));
		stringPathCondition._addDet(StringComparator.NOTSTARTSWITH, var2, var1);		
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var1.solution().startsWith(var2.solution()));
	}
	
	@Test
	public void testNOTSTARTSWITH_notStartsWithAndStartsWithConstraints() {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.NOTSTARTSWITH, new StringConstant("a"), var1);
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("b"), var2);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var1.solution().startsWith("a"));
		assertTrue(var2.solution().startsWith("b"));
	}
	
	@Test
	public void testNOTSTARTSWITH_twoNotStartsWithConstraints() {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.NOTSTARTSWITH, new StringConstant("a"), var1);
		stringPathCondition._addDet(StringComparator.NOTSTARTSWITH, new StringConstant("b"), var2);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var1.solution().startsWith("a"));
		assertFalse(var2.solution().startsWith("b"));
	}
	
	@Test
	public void testENDSWITH_twoEndsWithConstraints() {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.ENDSWITH, new StringConstant("a"), var1);
		stringPathCondition._addDet(StringComparator.ENDSWITH, new StringConstant("b"), var2);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var1.solution().startsWith("a"));
		assertTrue(var2.solution().startsWith("b"));
	}
	
	@Test
	public void testENDSWITH_endsWithAndNotEndsWithConstraints() {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.ENDSWITH, new StringConstant("a"), var1);
		stringPathCondition._addDet(StringComparator.NOTENDSWITH, new StringConstant("b"), var2);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var1.solution().startsWith("a"));
		assertFalse(var2.solution().startsWith("b"));
	}
	
	@Test
	public void testNOTENDSWITH_lengthAndNotEndsWithConstraints() {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		pathCondition._addDet(Comparator.GE, var1._length(), new IntegerConstant(2));
		stringPathCondition._addDet(StringComparator.NOTENDSWITH, var2, var1);		
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var1.solution().endsWith(var2.solution()));
	}
	
	@Test
	public void testNOTENDSWITH_notEndsWithAndEndsWithConstraints() {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.NOTENDSWITH, new StringConstant("a"), var1);
		stringPathCondition._addDet(StringComparator.ENDSWITH, new StringConstant("b"), var2);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var1.solution().startsWith("a"));
		assertTrue(var2.solution().startsWith("b"));
	}
	
	@Test
	public void testNOTENDSWITH_twoNotEndsWithConstraints() {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.NOTENDSWITH, new StringConstant("a"), var1);
		stringPathCondition._addDet(StringComparator.NOTENDSWITH, new StringConstant("b"), var2);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var1.solution().startsWith("a"));
		assertFalse(var2.solution().startsWith("b"));
	}
	
	@Test
	public void testCONTAINS_twoContainsConstraints() {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.CONTAINS, new StringConstant("a"), var1);
		stringPathCondition._addDet(StringComparator.CONTAINS, new StringConstant("b"), var2);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var1.solution().contains("a"));
		assertTrue(var2.solution().contains("b"));
	}
	
	@Test
	public void testCONTAINS_containsAndNotContainsConstraints() {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.CONTAINS, new StringConstant("a"), var1);
		stringPathCondition._addDet(StringComparator.NOTCONTAINS, new StringConstant("b"), var2);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var1.solution().contains("a"));
		assertFalse(var2.solution().contains("b"));
	}
	
	@Test
	public void testNOTCONTAINS_lengthAndNotContainsConstraints() {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		pathCondition._addDet(Comparator.GE, var1._length(), new IntegerConstant(2));
		stringPathCondition._addDet(StringComparator.NOTCONTAINS, var2, var1);		
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var1.solution().contains(var2.solution()));
	}

	@Test
	public void testNOTCONTAINS_notContainsAndContainsConstraints() {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.NOTCONTAINS, new StringConstant("a"), var1);
		stringPathCondition._addDet(StringComparator.CONTAINS, new StringConstant("b"), var2);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var1.solution().contains("a"));
		assertTrue(var2.solution().contains("b"));
	}
	
	@Test
	public void testNOTCONTAINS_twoNotContainsConstraints() {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.NOTCONTAINS, new StringConstant("a"), var1);
		stringPathCondition._addDet(StringComparator.NOTCONTAINS, new StringConstant("b"), var2);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var1.solution().contains("a"));
		assertFalse(var2.solution().contains("b"));
	}
	
	@Test
	public void testTrim_equalsConstraint() {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringExpression var2 = var1._trim();
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("cc"), var2);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var2.solution().equals("cc"));
		assertTrue(var1.solution().trim().equals("cc"));
	}
	
	@Test
	public void testTrim_notEqualsConstraint() {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringExpression var2 = var1._trim();
		stringPathCondition._addDet(StringComparator.NOTEQUALS, new StringConstant("cc"), var2);
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var2.solution().equals("cc"));
		assertFalse(var1.solution().trim().equals("cc"));
	}
	
	@Test
	public void testConcat_twoContainsAndLessOrEqualLengthConstraints() {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.CONTAINS, new StringConstant("a"), var1);
		stringPathCondition._addDet(StringComparator.CONTAINS, new StringConstant("b"), var2);
		StringExpression var3 = var1._concat(var2);
		pathCondition._addDet(Comparator.LE, var3._length(), 10);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var1.solution().contains("a"));
		assertTrue(var2.solution().contains("b"));
		assertTrue(var1.solution().concat(var2.solution()).equals(var3.solution()));
		assertTrue(var3.solution().length() < 10);
	}
	
	@Test
	public void testConcat_twoContainsAndGreaterThanLengthConstraints() {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.CONTAINS, new StringConstant("a"), var1);
		stringPathCondition._addDet(StringComparator.CONTAINS, new StringConstant("b"), var2);
		StringExpression var3 = var1._concat(var2);
		pathCondition._addDet(Comparator.GT, var3._length(), 10);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var1.solution().contains("a"));
		assertTrue(var2.solution().contains("b"));
		assertTrue(var1.solution().concat(var2.solution()).equals(var3.solution()));
		assertTrue(var3.solution().length() > 10);
	}
	
	@Test
	public void testConcat_notContainsAndContainsAndLessOrEqualLengthConstraints() {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.NOTCONTAINS, new StringConstant("a"), var1);
		stringPathCondition._addDet(StringComparator.CONTAINS, new StringConstant("b"), var2);
		StringExpression var3 = var1._concat(var2);
		pathCondition._addDet(Comparator.LE, var3._length(), 10);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var1.solution().contains("a"));
		assertTrue(var2.solution().contains("b"));
		assertTrue(var1.solution().concat(var2.solution()).equals(var3.solution()));
		assertTrue(var3.solution().length() < 10);
	}
	
	@Test
	public void testConcat_notContainsAndContainsAndGreaterThanLengthConstraints() {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.NOTCONTAINS, new StringConstant("a"), var1);
		stringPathCondition._addDet(StringComparator.CONTAINS, new StringConstant("b"), var2);
		StringExpression var3 = var1._concat(var2);
		pathCondition._addDet(Comparator.GT, var3._length(), 10);
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var1.solution().contains("a"));
		assertTrue(var2.solution().contains("b"));
		assertTrue(var1.solution().concat(var2.solution()).equals(var3.solution()));
		assertTrue(var3.solution().length() > 10);
	}
	
	@Test
	public void testConcat_containsAndNotContainsAndLessOrEqualLengthConstraints() {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.CONTAINS, new StringConstant("a"), var1);
		stringPathCondition._addDet(StringComparator.NOTCONTAINS, new StringConstant("b"), var2);
		StringExpression var3 = var1._concat(var2);
		pathCondition._addDet(Comparator.LE, var3._length(), 10);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var1.solution().contains("a"));
		assertFalse(var2.solution().contains("b"));
		assertTrue(var1.solution().concat(var2.solution()).equals(var3.solution()));
		assertTrue(var3.solution().length() < 10);
	}
	
	@Test
	public void testConcat_containsAndNotContainsAndGreaterThanLengthConstraints() {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.CONTAINS, new StringConstant("a"), var1);
		stringPathCondition._addDet(StringComparator.NOTCONTAINS, new StringConstant("b"), var2);
		StringExpression var3 = var1._concat(var2);
		pathCondition._addDet(Comparator.GT, var3._length(), 10);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var1.solution().contains("a"));
		assertFalse(var2.solution().contains("b"));
		assertTrue(var1.solution().concat(var2.solution()).equals(var3.solution()));
		assertTrue(var3.solution().length() > 10);
	}
	
	@Test
	public void testConcat_twoNotContainsAndLessOrEqualLengthConstraints() {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.NOTCONTAINS, new StringConstant("a"), var1);
		stringPathCondition._addDet(StringComparator.NOTCONTAINS, new StringConstant("b"), var2);
		StringExpression var3 = var1._concat(var2);
		pathCondition._addDet(Comparator.LE, var3._length(), 10);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var1.solution().contains("a"));
		assertFalse(var2.solution().contains("b"));
		assertTrue(var1.solution().concat(var2.solution()).equals(var3.solution()));
		assertTrue(var3.solution().length() < 10);
	}
	
	@Test
	public void testConcat_twoNotContainsAndGreaterThanLengthConstraints() {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.NOTCONTAINS, new StringConstant("a"), var1);
		stringPathCondition._addDet(StringComparator.NOTCONTAINS, new StringConstant("b"), var2);
		StringExpression var3 = var1._concat(var2);
		pathCondition._addDet(Comparator.GT, var3._length(), 10);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var1.solution().contains("a"));
		assertFalse(var2.solution().contains("b"));
		assertTrue(var1.solution().concat(var2.solution()).equals(var3.solution()));
		assertTrue(var3.solution().length() > 10);
	}
	
	//INDEXOF
	@Test
	public void Test12_1() {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("aa"), var1);
		pathCondition._addDet(Comparator.LE, var1._length(), 10);
		pathCondition._addDet(Comparator.GT, var1._indexOf(new StringConstant("a")), 0);
		
		boolean result = stringPathCondition.simplify();
		
		assertFalse(result);
	}
	
	@Test
	public void Test12_2 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("bb"), var1);
		pathCondition._addDet(Comparator.GT, var1._indexOf(new StringConstant("a")), 0);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var1.solution().startsWith("bb"));
		assertTrue(var1.solution().indexOf("a") > 0);
	}
	
	@Test
	public void Test12_3 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("bb"), var1);
		pathCondition._addDet(Comparator.EQ, var1._indexOf(new StringConstant("a")), -1);
		
		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertTrue(var1.solution().startsWith("bb"));
		assertTrue(var1.solution().indexOf("a") == -1);
	}
	
	@Test
	public void Test12_4 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("bb"), var1);
		pathCondition._addDet(Comparator.EQ, var1._indexOf(new StringConstant("b")), -1);
		
		boolean result = stringPathCondition.simplify();
		
		assertFalse(result);
	}
	
	@Test
	public void Test12_5 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("bb"), var1);
		pathCondition._addDet(Comparator.EQ, var1._indexOf(new StringConstant("b")), 1);
		
		boolean result = stringPathCondition.simplify();

		assertFalse(result);
	}
	
	@Test
	//TODO: Could do with a speedup
	public void Test13_1 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("aa"), var1);
		SymbolicInteger si = new SymbolicInteger("int1");
		pathCondition._addDet(Comparator.LE, var1._length(), 10);
		pathCondition._addDet(Comparator.EQ, var1._indexOf(new StringConstant("a"), new IntegerConstant(5)), si);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);		
		assertTrue(var1.solution().startsWith("aa"));
		assertTrue(var1.solution().indexOf("a", 5) == si.solution());
	}
	
	@Test
	public void Test13_2 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("bb"), var1);
		SymbolicInteger si = new SymbolicInteger("int1");
		pathCondition._addDet(Comparator.EQ, var1._indexOf(new StringConstant("a"), new IntegerConstant(5)), si);
		
		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertTrue(var1.solution().startsWith("bb"));
		assertTrue(var1.solution().indexOf("a", 5) == si.solution());
	}
	
	@Test
	public void Test13_3 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("aa"), var1);
		stringPathCondition._addDet(StringComparator.ENDSWITH, new StringConstant("bb"), var2);
		pathCondition._addDet(Comparator.GT, var1._indexOf(var2), 0);
		
		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertTrue(var1.solution().startsWith("aa"));
		assertTrue(var2.solution().endsWith("bb"));
		assertTrue(var1.solution().indexOf(var2.solution()) > 0);
	}
	
	@Test
	public void Test14_1 () {
		/*
		 	"SYM_a"->"C_CONST_gLR" [label="StartsWith"]
			"C_CONST_kR"->"SYM_a" [label="!StartsWith"]
			"C_CONST_O4+]"->"SYM_a" [label="!StartsWith"]
		 */
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("gLR"), var1);
		stringPathCondition._addDet(StringComparator.NOTSTARTSWITH, var1, new StringConstant("kR"));

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertTrue(var1.solution().startsWith("gLR"));
		assertFalse(var1.solution().startsWith("kR"));
	}
	
	@Test
	public void Test14_2 () {
		/*
		 	"SYM_b"->"C_CONST_Y&v^" [label="EndsWith"]
			"SYM_a"->"SYM_b" [label="!Equal", dir=both]
			"C_CONST_N@"->"SYM_b" [label="!EndsWith"]
		 */
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.ENDSWITH, new StringConstant("Y&v^"), var1);
		stringPathCondition._addDet(StringComparator.NOTENDSWITH, var1, new StringConstant("N@"));

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertTrue(var1.solution().endsWith("Y&v^"));
		assertFalse(var1.solution().startsWith("N@"));
	}
	
	@Test
	public void Test14_3 () {
		/*
		 	"SYM_b"->"C_CONST_9u" [label="StartsWith"]
			"SYM_a"->"C_CONST_9u" [label="EndsWith"]
			"SYM_a"->"SYM_b" [label="EdgeNotContains"]
		 */
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("9u"), var1);
		stringPathCondition._addDet(StringComparator.ENDSWITH, new StringConstant("9u"), var2);
		stringPathCondition._addDet(StringComparator.NOTCONTAINS, var2, var1);

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertTrue(var1.solution().startsWith("9u"));
		assertTrue(var2.solution().endsWith("9u"));
		assertFalse(var1.solution().contains(var2.solution()));
	}
	
	@Test
	public void Test14_4 () {
		//(stringvar1 notequals stringvar0) && (stringvar0 endswith stringvar1) && (stringvar1 endswith CONST_M.m)
		StringSymbolic var0 = new StringSymbolic("var0");
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.ENDSWITH, new StringConstant("M.m"), var1);
		stringPathCondition._addDet(StringComparator.ENDSWITH, var1, var0);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var0, var1);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var1.solution().startsWith("M.m"));
		assertTrue(var0.solution().endsWith(var1.solution()));
		assertFalse(var1.solution().equals(var0.solution()));
	}
	
	@Test
	public void indexOfCharEqualToCorrectIndexShouldBeSolved() {
		StringSymbolic var0 = new StringSymbolic("var0");
		IntegerConstant var1 = new IntegerConstant('t');
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("strtr"), var0);
		pathCondition._addDet(Comparator.EQ, var0._indexOf(var1), new IntegerConstant(1));

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var0.solution().equals("strtr"));
	}
	
	@Test
	public void indexOfCharEqualToIncorrectIndexShouldNotBeSolved() {
		StringSymbolic var0 = new StringSymbolic("var0");
		IntegerConstant var1 = new IntegerConstant('t');
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("strtr"), var0);
		pathCondition._addDet(Comparator.EQ, var0._indexOf(var1), new IntegerConstant(3));

		boolean result = stringPathCondition.simplify();
		
		assertFalse(result);
	}
	
	@Test
	public void indexOfNonexistentCharEqualToMinusOneShouldBeSolved() {
		StringSymbolic var0 = new StringSymbolic("var0");
		IntegerConstant var1 = new IntegerConstant('a');
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("strtr"), var0);
		pathCondition._addDet(Comparator.EQ, var0._indexOf(var1), new IntegerConstant(-1));

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var0.solution().indexOf('a') == -1);
	}
	
	@Test
	public void indexOfNonexistentCharNotEqualToMinusOneShouldNotBeSolved() {
		StringSymbolic var0 = new StringSymbolic("var0");
		IntegerConstant var1 = new IntegerConstant('a');
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("strtr"), var0);
		pathCondition._addDet(Comparator.NE, var0._indexOf(var1), new IntegerConstant(-1));

		boolean result = stringPathCondition.simplify();
		
		assertFalse(result);
	}
	
	@Test
	public void indexOfCharWithBoundEqualToCorrectIndexShouldBeSolved() {
		StringSymbolic var0 = new StringSymbolic("var0");
		IntegerConstant var1 = new IntegerConstant('t');
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("strtr"), var0);
		pathCondition._addDet(Comparator.EQ,
				var0._indexOf(var1, new IntegerConstant(2)), new IntegerConstant(3));

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var0.solution().equals("strstr"));
	}
	
	@Test
	public void indexOfCharWithBoundEqualToIncorrectIndexShouldNotBeSolved() {
		StringSymbolic var0 = new StringSymbolic("var0");
		IntegerConstant var1 = new IntegerConstant('t');
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("strtr"), var0);
		pathCondition._addDet(Comparator.EQ,
				var0._indexOf(var1, new IntegerConstant(1)), new IntegerConstant(3));

		boolean result = stringPathCondition.simplify();
		
		assertFalse(result);
	}
	
	@Test
	public void indexOfNonexistentCharWithBoundEqualToMinusOneShouldBeSolved() {
		StringSymbolic var0 = new StringSymbolic("var0");
		IntegerConstant var1 = new IntegerConstant('s');
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("strtr"), var0);
		pathCondition._addDet(Comparator.EQ,
				var0._indexOf(var1, new IntegerConstant(1)), new IntegerConstant(-1));

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var0.solution().equals("strstr"));
	}
	
	@Test
	public void indexOfNonexistentCharWithBoundNotEqualToMinusOneShouldNotBeSolved() {
		StringSymbolic var0 = new StringSymbolic("var0");
		IntegerConstant var1 = new IntegerConstant('s');
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("strtr"), var0);
		pathCondition._addDet(Comparator.NE,
				var0._indexOf(var1, new IntegerConstant(1)), new IntegerConstant(-1));

		boolean result = stringPathCondition.simplify();
		
		assertFalse(result);
	}
	
	@Test
	public void lastIndexOfCharEqualToCorrectIndexShouldBeSolved() {
		StringSymbolic var0 = new StringSymbolic("var0");
		IntegerConstant var1 = new IntegerConstant('t');
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("strtr"), var0);
		pathCondition._addDet(Comparator.EQ, var0._lastIndexOf(var1), new IntegerConstant(3));

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var0.solution().equals("strtr"));
	}
	
	@Test
	public void lastIndexOfNonexistentCharEqualToMinusOneShouldBeSolved() {
		StringSymbolic var0 = new StringSymbolic("var0");
		IntegerConstant var1 = new IntegerConstant('a');
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("strtr"), var0);
		pathCondition._addDet(Comparator.EQ, var0._lastIndexOf(var1), new IntegerConstant(-1));

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var0.solution().lastIndexOf('a') == -1);
	}
	
	@Test
	public void lastIndexOfNonexistentCharNotEqualToMinusOneShouldBeSolved() {
		StringSymbolic var0 = new StringSymbolic("var0");
		IntegerConstant var1 = new IntegerConstant('a');
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("strtr"), var0);
		pathCondition._addDet(Comparator.NE, var0._lastIndexOf(var1), new IntegerConstant(-1));

		boolean result = stringPathCondition.simplify();
		
		assertFalse(result);
	}
	
	@Test
	public void lastIndexOfCharEqualToIncorrectIndexShouldNotBeSolved() {
		StringSymbolic var0 = new StringSymbolic("var0");
		IntegerConstant var1 = new IntegerConstant('t');
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("strtr"), var0);
		pathCondition._addDet(Comparator.EQ, var0._lastIndexOf(var1), new IntegerConstant(1));

		boolean result = stringPathCondition.simplify();
		
		assertFalse(result);
	}
	
	@Test
	public void lastIndexOfCharWithBoundEqualToCorrectIndexShouldBeSolved() {
		StringSymbolic var0 = new StringSymbolic("var0");
		IntegerConstant var1 = new IntegerConstant('t');
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("strtr"), var0);
		pathCondition._addDet(Comparator.EQ,
				var0._lastIndexOf(var1, new IntegerConstant(2)), new IntegerConstant(1));

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var0.solution().equals("strtr"));
	}
	
	@Test
	public void lastIndexOfCharWithBoundEqualToIncorrectIndexShouldNotBeSolved() {
		StringSymbolic var0 = new StringSymbolic("var0");
		IntegerConstant var1 = new IntegerConstant('t');
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("strtr"), var0);
		pathCondition._addDet(Comparator.EQ,
				var0._lastIndexOf(var1, new IntegerConstant(4)), new IntegerConstant(1));

		boolean result = stringPathCondition.simplify();
		
		assertFalse(result);
	}
	
	@Test
	public void lastIndexOfSubstringEqualToCorrectIndexShouldBeSolved() {
		StringSymbolic var0 = new StringSymbolic("var0");
		StringConstant var1 = new StringConstant("tr");
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("strtr"), var0);
		pathCondition._addDet(Comparator.EQ, var0._lastIndexOf(var1), new IntegerConstant(3));

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var0.solution().equals("strtr"));
	}
	
	@Test
	public void lastIndexOfSubstringEqualToIncorrectIndexShouldNotBeSolved() {
		StringSymbolic var0 = new StringSymbolic("var0");
		StringConstant var1 = new StringConstant("tr");
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("strtr"), var0);
		pathCondition._addDet(Comparator.EQ, var0._lastIndexOf(var1), new IntegerConstant(1));

		boolean result = stringPathCondition.simplify();
		
		assertFalse(result);
	}
	
	@Test
	public void lastIndexOfSubstringWithBoundEqualToCorrectIndexShouldBeSolved() {
		StringSymbolic var0 = new StringSymbolic("var0");
		StringConstant var1 = new StringConstant("tr");
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("strtr"), var0);
		pathCondition._addDet(Comparator.EQ,
				var0._lastIndexOf(var1, new IntegerConstant(3)), new IntegerConstant(1));

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var0.solution().equals("strtr"));
	}
	
	@Test
	public void lastIndexOfSubstringWithBoundEqualToIncorrectIndexShouldNotBeSolved() {
		StringSymbolic var0 = new StringSymbolic("var0");
		StringConstant var1 = new StringConstant("tr");
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("strtr"), var0);
		pathCondition._addDet(Comparator.EQ,
				var0._lastIndexOf(var1, new IntegerConstant(4)), new IntegerConstant(1));

		boolean result = stringPathCondition.simplify();
		
		assertFalse(result);
	}
	
	@Test
	public void lastIndexOfNonExistentSubstringWithBoundEqualToMinusOneShouldBeSolved() {
		StringSymbolic var0 = new StringSymbolic("var0");
		StringConstant var1 = new StringConstant("tr");
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("strtr"), var0);
		pathCondition._addDet(Comparator.EQ,
				var0._lastIndexOf(var1, new IntegerConstant(1)), new IntegerConstant(-1));

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var0.solution().equals("strtr"));
	}
	
	@Test
	public void lastIndexOfNonExistentSubstringWithBoundNotEqualToMinusOneShouldNotBeSolved() {
		StringSymbolic var0 = new StringSymbolic("var0");
		StringConstant var1 = new StringConstant("tr");
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("strtr"), var0);
		pathCondition._addDet(Comparator.NE,
				var0._lastIndexOf(var1, new IntegerConstant(1)), new IntegerConstant(-1));

		boolean result = stringPathCondition.simplify();
		
		assertFalse(result);
	}
}
