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

import java.util.Arrays;
import java.util.Collection;

import javax.print.attribute.IntegerSyntax;

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
import gov.nasa.jpf.symbc.string.SymbolicStringConstraintsGeneral;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;


@RunWith(Parameterized.class)
public class TestSymString extends TestJPF {

	@Parameters
	public static Collection<Object[]> solvers() {
		return Arrays.asList(new Object[][] {
				{ AUTOMATA },
				{ Z3 }
		});
	}
	
	private final static String CHOCO = "choco";
	private final static String AUTOMATA = "automata";
	private final static String Z3 = "z3";
	
	private String solver;
	private PathCondition pathCondition;
	private StringPathCondition stringPathCondition;
	
	public TestSymString(String solver) {
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
	public void Test1 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringExpression pre = var1;
		StringExpression var = pre._subString(3, 2);
		StringExpression constant1 = new StringConstant("test");
		StringExpression constant2 = new StringConstant("s");
		
		stringPathCondition._addDet(StringComparator.NOTEQUALS, constant1, pre);
		stringPathCondition._addDet(StringComparator.EQUALS, constant2, var);	
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var1.solution().equals("test"));
		assertTrue(var1.solution().substring(2,3).equals("s"));
	}
	
	@Test
	public void Test2_1 () {
		StringPathCondition stringPathCondition = new StringPathCondition(new PathCondition());
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
	public void Test2_2 () {
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
	public void Test2_3 () {
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
	public void Test2_4 () {
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
	public void Test3_1 () {
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
	public void Test3_2 () {
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
	public void Test3_3 () {
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
	public void Test3_4 () {
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
	public void Test4_1 () {
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
	public void Test4_2 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("a"), var1);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, new StringConstant("b"), var2);
		
		boolean result = stringPathCondition.simplify();
		
		System.out.println(SymbolicStringConstraintsGeneral.getSolution());
		assertTrue(result);
		assertTrue(var1.solution().equals("a"));
		assertTrue(var2.solution() == null || !var2.solution().equals("b"));
	}

	@Test
	public void Test4_3 () {
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
	public void Test4_4 () {
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
	public void Test5_1 () {
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
	public void Test5_2 () {
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
	public void Test5_3 () {
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
	public void Test5_4 () {
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
	public void Test6_1 () {
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
	public void Test6_2 () {
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
	public void Test6_3 () {
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
	public void Test6_4 () {
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
	public void Test6_5 () {
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
	public void Test6_6 () {
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
	public void Test6_7 () {
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
	public void Test6_8 () {
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
	
	@Test
	public void Test7_1 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringExpression var2 = var1._trim();
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("cc"), var2);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var2.solution().equals("cc"));
		assertTrue(var1.solution().trim().equals("cc"));
	}
	
	@Test
	public void Test7_2 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringExpression var2 = var1._trim();
		stringPathCondition._addDet(StringComparator.NOTEQUALS, new StringConstant("cc"), var2);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var2.solution().equals("cc"));
		assertFalse(var1.solution().trim().equals("cc"));
	}
	
	@Test
	//TODO: Could do with a speedup
	public void Test8_1 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("aa"), var1);
		pathCondition._addDet(Comparator.LE, var1._length(), 10);
		pathCondition._addDet(Comparator.GT, var1._indexOf(new StringConstant("a")), 0);
		
		boolean result = stringPathCondition.simplify();
		
		assertFalse(result);
	}
	
	@Test
	public void Test8_2 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("bb"), var1);
		pathCondition._addDet(Comparator.GT, var1._indexOf(new StringConstant("a")), 0);
		
		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertTrue(var1.solution().startsWith("bb"));
		assertTrue(var1.solution().indexOf("a") > 0);
	}
	
	@Test
	//TODO: Could do with a speedup
	public void Test9_1 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("aa"), var1);
		pathCondition._addDet(Comparator.LE, var1._length(), 10);
		pathCondition._addDet(Comparator.GT, var1._indexOf(new IntegerConstant((int) 'a')), 0);
		
		boolean result = stringPathCondition.simplify();
		
		assertFalse(result);
	}
	
	@Test
	public void Test9_2 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("bb"), var1);
		pathCondition._addDet(Comparator.GT, var1._indexOf(new IntegerConstant((int) 'a')), 0);
		
		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertTrue(var1.solution().startsWith("bb"));
		assertTrue(var1.solution().indexOf("a") > 0);
	}
	
	@Test
	//TODO: Could do with a speedup
	public void Test10_1 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("aa"), var1);
		SymbolicInteger si = new SymbolicInteger("int1");
		pathCondition._addDet(Comparator.LE, var1._length(), 10);
		pathCondition._addDet(Comparator.EQ, var1._indexOf(new StringConstant("a"), new IntegerConstant(5)), si);
		
		boolean result = stringPathCondition.simplify();

		assertTrue("Solver " + solver + " failed",  result);
		assertTrue(var1.solution().startsWith("aa"));
		assertTrue(var1.solution().indexOf("a", 5) == si.solution());
	}
	
	@Test
	public void Test10_2 () {
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
	public void Test11_1 () {
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
	public void Test12_1 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		pathCondition._addDet(Comparator.EQ, var1._charAt(new IntegerConstant(1)), new IntegerConstant((int) 'a'));
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var1.solution().charAt(1) == 'a');
	}
	
	@Test
	public void Test12_2 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		pathCondition._addDet(Comparator.NE, var1._charAt(new IntegerConstant(1)), new IntegerConstant((int) 'a'));
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var1.solution().charAt(1) != 'a');
	}
	
	@Test
	public void Test13_1 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		SymbolicInteger si = new SymbolicInteger("si1");
		pathCondition._addDet(Comparator.EQ, var1._charAt(new IntegerConstant(1)), si);
		
		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertTrue(var1.solution().charAt(1) == si.solution());
	}
	
	@Test
	public void Test14_1 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.STARTSWITH, var1, new StringConstant("abc"));
		stringPathCondition._addDet(StringComparator.STARTSWITH, var2, new StringConstant("b"));
		stringPathCondition._addDet(StringComparator.EQUALS, var1, var2);
		
		boolean result = stringPathCondition.simplify();
		
		assertFalse(result);
	}
	
	@Test
	public void Test14_2 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.STARTSWITH, var1, new StringConstant("abc"));
		stringPathCondition._addDet(StringComparator.STARTSWITH, var2, new StringConstant("b"));
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var1, var2);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var1.solution().equals(var2.solution()));
	}
	
	@Test
	public void Test15_1 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var1, var2);
		
		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertFalse(var1.solution().equals(var2.solution()));
	}
	
	@Test
	public void Test15_2 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.EQUALS, var1, var2);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var1.solution().equals(var2.solution()));
	}
	
	@Test
	public void Test16 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringExpression var2 = var1._trim();
		stringPathCondition._addDet(StringComparator.EQUALS, var2, new StringConstant("ab"));
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var1.solution().trim().equals("ab"));
	}
	
	@Test
	public void Test17 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant(" "), var1);
		StringExpression var2 = var1._trim();
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("ab"), var2);
		
		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertTrue(var1.solution().trim().equals("ab"));
		assertTrue(var1.solution().startsWith(" "));
	}
	
	@Test
	public void Test18 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		StringExpression var1ss = var1._subString(1);
		stringPathCondition._addDet(StringComparator.EQUALS, var1ss, var2);
		
		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertTrue(var1.solution().substring(1).equals(var2.solution()));
	}
	
	@Test
	public void Test19_1 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.ENDSWITH, new StringConstant("a"), var2);
		stringPathCondition._addDet(StringComparator.STARTSWITH, var2, var1);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var2.solution().endsWith("a"));
		assertTrue(var1.solution().startsWith(var2.solution()));
	}
	
	@Test
	public void Test19_2 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.NOTENDSWITH, new StringConstant("a"), var2);
		stringPathCondition._addDet(StringComparator.STARTSWITH, var2, var1);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var2.solution().endsWith("a"));
		assertTrue(var1.solution().startsWith(var2.solution()));
	}
	
	@Test
	public void Test19_3 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.ENDSWITH, new StringConstant("a"), var2);
		stringPathCondition._addDet(StringComparator.NOTSTARTSWITH, var2, var1);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var2.solution().endsWith("a"));
		assertFalse(var1.solution().startsWith(var2.solution()));
	}
	
	@Test
	public void Test19_4 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.NOTENDSWITH, new StringConstant("a"), var2);
		stringPathCondition._addDet(StringComparator.NOTSTARTSWITH, var2, var1);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var2.solution().endsWith("a"));
		assertFalse(var1.solution().startsWith(var2.solution()));
	}
	
	@Test
	public void Test20_1 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		StringSymbolic var3 = new StringSymbolic("var3");
		stringPathCondition._addDet(StringComparator.EQUALS, var1, var2);
		stringPathCondition._addDet(StringComparator.EQUALS, var2, var3);

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertTrue(var2.solution().equals(var1.solution()));
		assertTrue(var2.solution().equals(var3.solution()));
	}
	
	@Test
	public void Test20_2 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		StringSymbolic var3 = new StringSymbolic("var3");
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var1, var2);
		stringPathCondition._addDet(StringComparator.EQUALS, var2, var3);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var2.solution().equals(var1.solution()));
		assertTrue(var2.solution().equals(var3.solution()));
	}
	
	@Test
	public void Test20_3 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		StringSymbolic var3 = new StringSymbolic("var3");
		stringPathCondition._addDet(StringComparator.EQUALS, var1, var2);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var2, var3);
		System.out.println(stringPathCondition);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var2.solution().equals(var1.solution()));
		assertFalse(var2.solution().equals(var3.solution()));
	}
	
	@Test
	public void Test20_4 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		StringSymbolic var3 = new StringSymbolic("var3");
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var1, var2);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var2, var3);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var2.solution().equals(var1.solution()));
		assertFalse(var2.solution().equals(var3.solution()));
	}
	
	@Test
	public void Test21_1 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		StringSymbolic var3 = new StringSymbolic("var3");
		StringSymbolic var4 = new StringSymbolic("var4");
		stringPathCondition._addDet(StringComparator.EQUALS, var1, var2);
		stringPathCondition._addDet(StringComparator.EQUALS, var2, var3);
		stringPathCondition._addDet(StringComparator.EQUALS, var3, var4);
		
		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertTrue(var2.solution().equals(var1.solution()));
		assertTrue(var2.solution().equals(var3.solution()));
		assertTrue(var3.solution().equals(var4.solution()));
	}
	
	@Test
	public void Test21_2 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		StringSymbolic var3 = new StringSymbolic("var3");
		StringSymbolic var4 = new StringSymbolic("var4");
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var1, var2);
		stringPathCondition._addDet(StringComparator.EQUALS, var2, var3);
		stringPathCondition._addDet(StringComparator.EQUALS, var3, var4);
		System.out.println(stringPathCondition);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var2.solution().equals(var1.solution()));
		assertTrue(var2.solution().equals(var3.solution()));
		assertTrue(var3.solution().equals(var4.solution()));
	}
	
	@Test
	public void Test21_3 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		StringSymbolic var3 = new StringSymbolic("var3");
		StringSymbolic var4 = new StringSymbolic("var4");
		stringPathCondition._addDet(StringComparator.EQUALS, var1, var2);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var2, var3);
		stringPathCondition._addDet(StringComparator.EQUALS, var3, var4);
		System.out.println(stringPathCondition);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var2.solution().equals(var1.solution()));
		assertFalse(var2.solution().equals(var3.solution()));
		assertTrue(var3.solution().equals(var4.solution()));
	}
	
	@Test
	public void Test21_4 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		StringSymbolic var3 = new StringSymbolic("var3");
		StringSymbolic var4 = new StringSymbolic("var4");
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var1, var2);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var2, var3);
		stringPathCondition._addDet(StringComparator.EQUALS, var3, var4);
		System.out.println(stringPathCondition);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var2.solution().equals(var1.solution()));
		assertFalse(var2.solution().equals(var3.solution()));
		assertTrue(var3.solution().equals(var4.solution()));
	}
	
	@Test
	public void Test21_5 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		StringSymbolic var3 = new StringSymbolic("var3");
		StringSymbolic var4 = new StringSymbolic("var4");
		stringPathCondition._addDet(StringComparator.EQUALS, var1, var2);
		stringPathCondition._addDet(StringComparator.EQUALS, var2, var3);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var3, var4);
		
		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertTrue(var2.solution().equals(var1.solution()));
		assertTrue(var2.solution().equals(var3.solution()));
		assertFalse(var3.solution().equals(var4.solution()));
	}
	
	@Test
	public void Test21_6 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		StringSymbolic var3 = new StringSymbolic("var3");
		StringSymbolic var4 = new StringSymbolic("var4");
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var1, var2);
		stringPathCondition._addDet(StringComparator.EQUALS, var2, var3);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var3, var4);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var2.solution().equals(var1.solution()));
		assertTrue(var2.solution().equals(var3.solution()));
		assertFalse(var3.solution().equals(var4.solution()));
	}
	
	@Test
	public void Test21_7 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		StringSymbolic var3 = new StringSymbolic("var3");
		StringSymbolic var4 = new StringSymbolic("var4");
		stringPathCondition._addDet(StringComparator.EQUALS, var1, var2);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var2, var3);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var3, var4);
		System.out.println(stringPathCondition);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var2.solution().equals(var1.solution()));
		assertFalse(var2.solution().equals(var3.solution()));
		assertFalse(var3.solution().equals(var4.solution()));
	}
	
	@Test
	public void Test21_8 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		StringSymbolic var3 = new StringSymbolic("var3");
		StringSymbolic var4 = new StringSymbolic("var4");
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var1, var2);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var2, var3);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var3, var4);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var2.solution().equals(var1.solution()));
		assertFalse(var2.solution().equals(var3.solution()));
		assertFalse(var3.solution().equals(var4.solution()));
	}
	
	@Test
	public void Test22_1 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		StringSymbolic var3 = new StringSymbolic("var3");
		StringSymbolic var4 = new StringSymbolic("var4");
		stringPathCondition._addDet(StringComparator.EQUALS, var1, var2);
		stringPathCondition._addDet(StringComparator.EQUALS, var2, var3);
		stringPathCondition._addDet(StringComparator.EQUALS, var3, var4);
		stringPathCondition._addDet(StringComparator.EQUALS, var4, var1);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var2.solution().equals(var1.solution()));
		assertTrue(var2.solution().equals(var3.solution()));
		assertTrue(var3.solution().equals(var4.solution()));
		assertTrue(var4.solution().equals(var1.solution()));
	}
	
	@Test
	public void Test22_2 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		StringSymbolic var3 = new StringSymbolic("var3");
		StringSymbolic var4 = new StringSymbolic("var4");
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var1, var2);
		stringPathCondition._addDet(StringComparator.EQUALS, var2, var3);
		stringPathCondition._addDet(StringComparator.EQUALS, var3, var4);
		stringPathCondition._addDet(StringComparator.EQUALS, var4, var1);
		
		boolean result = stringPathCondition.simplify();
		
		assertFalse(result);
	}
	
	@Test
	public void Test22_3 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		StringSymbolic var3 = new StringSymbolic("var3");
		StringSymbolic var4 = new StringSymbolic("var4");
		stringPathCondition._addDet(StringComparator.EQUALS, var1, var2);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var2, var3);
		stringPathCondition._addDet(StringComparator.EQUALS, var3, var4);
		stringPathCondition._addDet(StringComparator.EQUALS, var4, var1);

		boolean result = stringPathCondition.simplify();
		
		assertFalse(result);
	}
	
	@Test
	public void Test22_4 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		StringSymbolic var3 = new StringSymbolic("var3");
		StringSymbolic var4 = new StringSymbolic("var4");
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var1, var2);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var2, var3);
		stringPathCondition._addDet(StringComparator.EQUALS, var3, var4);
		stringPathCondition._addDet(StringComparator.EQUALS, var4, var1);

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertFalse(var2.solution().equals(var1.solution()));
		assertFalse(var2.solution().equals(var3.solution()));
		assertTrue(var3.solution().equals(var4.solution()));
		assertTrue(var4.solution().equals(var1.solution()));
	}
	
	@Test
	public void Test22_5 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		StringSymbolic var3 = new StringSymbolic("var3");
		StringSymbolic var4 = new StringSymbolic("var4");
		stringPathCondition._addDet(StringComparator.EQUALS, var1, var2);
		stringPathCondition._addDet(StringComparator.EQUALS, var2, var3);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var3, var4);
		stringPathCondition._addDet(StringComparator.EQUALS, var4, var1);
		
		boolean result = stringPathCondition.simplify();
		
		assertFalse(result);
	}
	
	@Test
	public void Test22_6 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		StringSymbolic var3 = new StringSymbolic("var3");
		StringSymbolic var4 = new StringSymbolic("var4");
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var1, var2);
		stringPathCondition._addDet(StringComparator.EQUALS, var2, var3);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var3, var4);
		stringPathCondition._addDet(StringComparator.EQUALS, var4, var1);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var2.solution().equals(var1.solution()));
		assertTrue(var2.solution().equals(var3.solution()));
		assertFalse(var3.solution().equals(var4.solution()));
		assertTrue(var4.solution().equals(var1.solution()));
	}
	
	@Test
	public void Test22_7 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		StringSymbolic var3 = new StringSymbolic("var3");
		StringSymbolic var4 = new StringSymbolic("var4");
		stringPathCondition._addDet(StringComparator.EQUALS, var1, var2);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var2, var3);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var3, var4);
		stringPathCondition._addDet(StringComparator.EQUALS, var4, var1);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var2.solution().equals(var1.solution()));
		assertFalse(var2.solution().equals(var3.solution()));
		assertFalse(var3.solution().equals(var4.solution()));
		assertTrue(var4.solution().equals(var1.solution()));
	}
	
	@Test
	public void Test22_8 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		StringSymbolic var3 = new StringSymbolic("var3");
		StringSymbolic var4 = new StringSymbolic("var4");
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var1, var2);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var2, var3);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var3, var4);
		stringPathCondition._addDet(StringComparator.EQUALS, var4, var1);

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertFalse(var2.solution().equals(var1.solution()));
		assertFalse(var2.solution().equals(var3.solution()));
		assertFalse(var3.solution().equals(var4.solution()));
		assertTrue(var4.solution().equals(var1.solution()));
	}
	
	@Test
	public void Test22_9 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		StringSymbolic var3 = new StringSymbolic("var3");
		StringSymbolic var4 = new StringSymbolic("var4");
		stringPathCondition._addDet(StringComparator.EQUALS, var1, var2);
		stringPathCondition._addDet(StringComparator.EQUALS, var2, var3);
		stringPathCondition._addDet(StringComparator.EQUALS, var3, var4);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var4, var1);

		boolean result = stringPathCondition.simplify();

		assertFalse(result);
	}
	
	@Test
	public void Test22_10 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		StringSymbolic var3 = new StringSymbolic("var3");
		StringSymbolic var4 = new StringSymbolic("var4");
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var1, var2);
		stringPathCondition._addDet(StringComparator.EQUALS, var2, var3);
		stringPathCondition._addDet(StringComparator.EQUALS, var3, var4);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var4, var1);

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertFalse(var2.solution().equals(var1.solution()));
		assertTrue(var2.solution().equals(var3.solution()));
		assertTrue(var3.solution().equals(var4.solution()));
		assertFalse(var4.solution().equals(var1.solution()));
	}
	
	@Test
	public void Test22_11 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		StringSymbolic var3 = new StringSymbolic("var3");
		StringSymbolic var4 = new StringSymbolic("var4");
		stringPathCondition._addDet(StringComparator.EQUALS, var1, var2);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var2, var3);
		stringPathCondition._addDet(StringComparator.EQUALS, var3, var4);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var4, var1);
		
		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertTrue(var2.solution().equals(var1.solution()));
		assertFalse(var2.solution().equals(var3.solution()));
		assertTrue(var3.solution().equals(var4.solution()));
		assertFalse(var4.solution().equals(var1.solution()));
	}
	
	@Test
	public void Test22_12 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		StringSymbolic var3 = new StringSymbolic("var3");
		StringSymbolic var4 = new StringSymbolic("var4");
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var1, var2);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var2, var3);
		stringPathCondition._addDet(StringComparator.EQUALS, var3, var4);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var4, var1);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var2.solution().equals(var1.solution()));
		assertFalse(var2.solution().equals(var3.solution()));
		assertTrue(var3.solution().equals(var4.solution()));
		assertFalse(var4.solution().equals(var1.solution()));
	}
	
	@Test
	public void Test22_13 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		StringSymbolic var3 = new StringSymbolic("var3");
		StringSymbolic var4 = new StringSymbolic("var4");
		stringPathCondition._addDet(StringComparator.EQUALS, var1, var2);
		stringPathCondition._addDet(StringComparator.EQUALS, var2, var3);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var3, var4);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var4, var1);
		
		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertTrue(var2.solution().equals(var1.solution()));
		assertTrue(var2.solution().equals(var3.solution()));
		assertFalse(var3.solution().equals(var4.solution()));
		assertFalse(var4.solution().equals(var1.solution()));
	}
	
	@Test
	public void Test22_14 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		StringSymbolic var3 = new StringSymbolic("var3");
		StringSymbolic var4 = new StringSymbolic("var4");
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var1, var2);
		stringPathCondition._addDet(StringComparator.EQUALS, var2, var3);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var3, var4);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var4, var1);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var2.solution().equals(var1.solution()));
		assertTrue(var2.solution().equals(var3.solution()));
		assertFalse(var3.solution().equals(var4.solution()));
		assertFalse(var4.solution().equals(var1.solution()));
	}
	
	@Test
	public void Test22_15 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		StringSymbolic var3 = new StringSymbolic("var3");
		StringSymbolic var4 = new StringSymbolic("var4");
		stringPathCondition._addDet(StringComparator.EQUALS, var1, var2);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var2, var3);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var3, var4);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var4, var1);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var2.solution().equals(var1.solution()));
		assertFalse(var2.solution().equals(var3.solution()));
		assertFalse(var3.solution().equals(var4.solution()));
		assertFalse(var4.solution().equals(var1.solution()));
	}
	
	@Test
	public void Test22_16 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		StringSymbolic var3 = new StringSymbolic("var3");
		StringSymbolic var4 = new StringSymbolic("var4");
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var1, var2);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var2, var3);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var3, var4);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var4, var1);

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertFalse(var2.solution().equals(var1.solution()));
		assertFalse(var2.solution().equals(var3.solution()));
		assertFalse(var3.solution().equals(var4.solution()));
		assertFalse(var4.solution().equals(var1.solution()));
	}
	
	@Test
	public void Test23 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		StringSymbolic var3 = new StringSymbolic("var3");
		stringPathCondition._addDet(StringComparator.EQUALS, var1, var2);
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("aa"), var1);
		stringPathCondition._addDet(StringComparator.STARTSWITH, var3, var2);

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertTrue(var2.solution().equals(var1.solution()));
		assertTrue(var1.solution().startsWith("aa"));
		assertTrue(var2.solution().startsWith(var3.solution()));
	}
	
	@Test
	public void Test24_1 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		StringExpression var3 = var1._concat(var2);
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("abc"), var3);

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertTrue(var1.solution().concat(var2.solution()).equals("abc"));
	}
	
	@Test
	public void Test24_2 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringSymbolic var2 = new StringSymbolic("var2");
		StringExpression var3 = var1._concat(var2);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, new StringConstant("abc"), var3);

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertFalse(var1.solution().concat(var2.solution()).equals("abc"));
	}
	
	@Test
	public void Test25_1 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringExpression var2 = new StringConstant("c");
		StringExpression var3 = var1._concat(var2);
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("abc"), var3);

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertTrue(var1.solution().concat(var2.solution()).equals("abc"));
	}
	
	@Test
	public void Test25_2 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringExpression var2 = new StringConstant("c");
		StringExpression var3 = var1._concat(var2);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, new StringConstant("abc"), var3);

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertFalse(var1.solution().concat(var2.solution()).equals("abc"));
	}
	
	@Test
	public void Test26_1 () {
		StringExpression var1 = new StringConstant("a");
		StringSymbolic var2 = new StringSymbolic("var2");
		StringExpression var3 = var1._concat(var2);
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("abc"), var3);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var1.solution().concat(var2.solution()).equals("abc"));
	}
	
	@Test
	public void Test26_2 () {
		StringExpression var1 = new StringConstant("a");
		StringSymbolic var2 = new StringSymbolic("var2");
		StringExpression var3 = var1._concat(var2);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, new StringConstant("abc"), var3);

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertFalse(var1.solution().concat(var2.solution()).equals("abc"));
	}
	
	@Test
	public void Test27_1 () {
		StringExpression var1 = new StringConstant("a");
		StringExpression var2 = new StringConstant("bc");
		StringExpression var3 = var1._concat(var2);
		StringSymbolic var4 = new StringSymbolic("var4");
		stringPathCondition._addDet(StringComparator.EQUALS, var3, var4);

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertTrue(var4.solution().equals("abc"));
	}
	
	@Test
	public void Test27_2 () {
		StringExpression var1 = new StringConstant("a");
		StringExpression var2 = new StringConstant("bc");
		StringExpression var3 = var1._concat(var2);
		StringSymbolic var4 = new StringSymbolic("var4");
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var3, var4);

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertFalse(var4.solution().equals("abc"));
	}
	
	@Test
	public void Test28_1 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("abc"), var1);
		pathCondition._addDet(Comparator.GT, var1._length(), 5);

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertTrue(var1.solution().startsWith("abc"));
		assertTrue(var1.solution().length() > 5);
	}
	
	@Test
	public void Test28_2 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.NOTSTARTSWITH, new StringConstant("abc"), var1);
		pathCondition._addDet(Comparator.GT, var1._length(), 5);

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertFalse(var1.solution().startsWith("abc"));
		assertTrue(var1.solution().length() > 5);
	}
	
	@Test
	public void Test28_3 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("abc"), var1);
		pathCondition._addDet(Comparator.LE, var1._length(), 5);

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertTrue(var1.solution().startsWith("abc"));
		assertTrue(var1.solution().length() <= 5);
	}
	
	@Test
	public void Test28_4 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.NOTSTARTSWITH, new StringConstant("abc"), var1);
		pathCondition._addDet(Comparator.LE, var1._length(), 5);

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertFalse(var1.solution().startsWith("abc"));
		assertTrue(var1.solution().length() <= 5);
	}
	
	@Test
	public void Test29_1 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("aa"), var1);
		pathCondition._addDet(Comparator.EQ, var1._charAt(new IntegerConstant(5)), (int) 'c');

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertTrue(var1.solution().startsWith("aa"));
		assertTrue(var1.solution().charAt(5) == 'c');
	}
	
	@Test
	public void Test29_2 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.NOTSTARTSWITH, new StringConstant("aa"), var1);
		pathCondition._addDet(Comparator.EQ, var1._charAt(new IntegerConstant(5)), (int) 'c');

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertFalse(var1.solution().startsWith("aa"));
		assertTrue(var1.solution().charAt(5) == 'c');
	}
	
	@Test
	public void Test29_3 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("aa"), var1);
		pathCondition._addDet(Comparator.NE, var1._charAt(new IntegerConstant(5)), (int) 'c');

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertTrue(var1.solution().startsWith("aa"));
		assertTrue(var1.solution().charAt(5) != 'c');
	}
	
	@Test
	public void Test29_4 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.NOTSTARTSWITH, new StringConstant("aa"), var1);
		pathCondition._addDet(Comparator.NE, var1._charAt(new IntegerConstant(5)), (int) 'c');

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var1.solution().startsWith("aa"));
		assertTrue(var1.solution().charAt(5) != 'c');
	}
	
	@Test
	public void Test30_1 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("aa"), var1);
		pathCondition._addDet(Comparator.EQ, var1._charAt(new IntegerConstant(1)), (int) 'a');

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertTrue(var1.solution().startsWith("aa"));
		assertTrue(var1.solution().charAt(1) == 'a');
	}
	
	@Test
	public void Test30_2 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.NOTSTARTSWITH, new StringConstant("aa"), var1);
		pathCondition._addDet(Comparator.EQ, var1._charAt(new IntegerConstant(1)), (int) 'a');

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertFalse(var1.solution().startsWith("aa"));
		assertTrue(var1.solution().charAt(1) == 'a');
	}
	
	@Test
	public void Test30_3 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("aa"), var1);
		pathCondition._addDet(Comparator.NE, var1._charAt(new IntegerConstant(1)), (int) 'a');
		
		boolean result = stringPathCondition.simplify();
		
		assertFalse(result);
	}
	
	@Test
	public void Test30_4 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.NOTSTARTSWITH, new StringConstant("aa"), var1);
		pathCondition._addDet(Comparator.NE, var1._charAt(new IntegerConstant(1)), (int) 'a');
		
		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertFalse(var1.solution().startsWith("aa"));
		assertTrue(var1.solution().charAt(1) != 'a');
	}
	
	@Test
	public void Test31_1 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("aa"), var1);
		SymbolicInteger si = new SymbolicInteger("si1");
		pathCondition._addDet(Comparator.EQ, var1._charAt(si), (int) 'a');

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertTrue(var1.solution().startsWith("aa"));
		assertTrue(var1.solution().charAt(si.solutionInt()) == 'a');
	}
	
	@Test
	public void Test31_2 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.NOTSTARTSWITH, new StringConstant("aa"), var1);
		SymbolicInteger si = new SymbolicInteger("si1");
		pathCondition._addDet(Comparator.EQ, var1._charAt(si), (int) 'a');

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var1.solution().startsWith("aa"));
		assertTrue(var1.solution().charAt(si.solutionInt()) == 'a');
	}
	
	@Test
	public void Test31_3 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("aa"), var1);
		SymbolicInteger si = new SymbolicInteger("si1");
		pathCondition._addDet(Comparator.NE, var1._charAt(si), (int) 'a');
		
		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertTrue(var1.solution().startsWith("aa"));
		assertTrue(var1.solution().charAt(si.solutionInt()) != 'a');
	}
	
	@Test
	public void Test31_4 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.NOTSTARTSWITH, new StringConstant("aa"), var1);
		SymbolicInteger si = new SymbolicInteger("si1");
		pathCondition._addDet(Comparator.NE, var1._charAt(si), (int) 'a');

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var1.solution().startsWith("aa"));
		assertTrue(var1.solution().charAt(si.solutionInt()) != 'a');
	}
	
	@Test
	public void Test32_1 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.ENDSWITH, new StringConstant("aa"), var1);
		pathCondition._addDet(Comparator.EQ, var1._charAt(new IntegerConstant(0)), (int) 'a');
		System.out.println(stringPathCondition);
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var1.solution().endsWith("aa"));
		assertTrue(var1.solution().charAt(0) == 'a');
	}
	
	@Test
	public void Test32_2 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.NOTENDSWITH, new StringConstant("aa"), var1);
		pathCondition._addDet(Comparator.EQ, var1._charAt(new IntegerConstant(0)), (int) 'a');
		
		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertFalse(var1.solution().endsWith("aa"));
		assertTrue(var1.solution().charAt(0) == 'a');
	}
	
	@Test
	public void Test32_3 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.ENDSWITH, new StringConstant("aa"), var1);
		pathCondition._addDet(Comparator.NE, var1._charAt(new IntegerConstant(0)), (int) 'a');
		
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var1.solution().endsWith("aa"));
		assertTrue(var1.solution().charAt(0) != 'a');
	}
	
	@Test
	public void Test32_4 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.NOTENDSWITH, new StringConstant("aa"), var1);
		pathCondition._addDet(Comparator.NE, var1._charAt(new IntegerConstant(0)), (int) 'a');

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertFalse(var1.solution().endsWith("aa"));
		assertTrue(var1.solution().charAt(0) != 'a');
	}
	
	@Test
	public void Test33_1 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringExpression var2 = var1._subString(5,3);
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("aa"), var2);

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertTrue(var1.solution().substring(3,5).equals("aa"));
	}
	
	@Test
	public void Test33_2 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringExpression var2 = var1._subString(5,3);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, new StringConstant("aa"), var2);

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertFalse(var1.solution().substring(3,5).equals("aa"));
	}
	
	@Test
	public void Test34_1 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringExpression var2 = var1._subString(5,3);
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("aa"), var2);
		pathCondition._addDet(Comparator.EQ, var1._charAt(new IntegerConstant(0)), (int) 'a');

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertTrue(var1.solution().substring(3,5).equals("aa"));
		assertTrue(var1.solution().charAt(0) == 'a');
	}
	
	@Test
	public void Test34_2 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringExpression var2 = var1._subString(5,3);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, new StringConstant("aa"), var2);
		pathCondition._addDet(Comparator.EQ, var1._charAt(new IntegerConstant(0)), (int) 'a');

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertFalse(var1.solution().substring(3,5).equals("aa"));
		assertTrue(var1.solution().charAt(0) == 'a');
	}
	
	@Test
	public void Test34_3 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringExpression var2 = var1._subString(5,3);
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("aa"), var2);
		pathCondition._addDet(Comparator.NE, var1._charAt(new IntegerConstant(0)), (int) 'a');

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertTrue(var1.solution().substring(3,5).equals("aa"));
		assertTrue(var1.solution().charAt(0) != 'a');
	}
	
	@Test
	public void Test34_4 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringExpression var2 = var1._subString(5,3);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, new StringConstant("aa"), var2);
		pathCondition._addDet(Comparator.NE, var1._charAt(new IntegerConstant(0)), (int) 'a');
		
		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertFalse(var1.solution().substring(3,5).equals("aa"));
		assertTrue(var1.solution().charAt(0) != 'a');
	}
	
	@Test
	public void Test35_1 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringExpression var2 = var1._subString(3);
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("aa"), var2);

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertTrue(var1.solution().substring(3).equals("aa"));
	}
	
	@Test
	public void Test35_2 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringExpression var2 = var1._subString(3);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, new StringConstant("aa"), var2);

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertFalse(var1.solution().substring(3).equals("aa"));
	}
	
	@Test
	public void Test36_1 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringExpression var2 = var1._subString(3);
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("aa"), var2);
		pathCondition._addDet(Comparator.EQ, var1._charAt(new IntegerConstant(0)), (int) 'a');

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertTrue(var1.solution().substring(3).equals("aa"));
		assertTrue(var1.solution().charAt(0) == 'a');
	}
	
	@Test
	public void Test36_2 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringExpression var2 = var1._subString(3);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, new StringConstant("aa"), var2);
		pathCondition._addDet(Comparator.EQ, var1._charAt(new IntegerConstant(0)), (int) 'a');

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertFalse(var1.solution().substring(3).equals("aa"));
		assertTrue(var1.solution().charAt(0) == 'a');
	}
	
	@Test
	public void Test36_3 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringExpression var2 = var1._subString(3);
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("aa"), var2);
		pathCondition._addDet(Comparator.NE, var1._charAt(new IntegerConstant(0)), (int) 'a');

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertTrue(var1.solution().substring(3).equals("aa"));
		assertTrue(var1.solution().charAt(0) != 'a');
	}
	
	@Test
	public void Test36_4 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		StringExpression var2 = var1._subString(3);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, new StringConstant("aa"), var2);
		pathCondition._addDet(Comparator.NE, var1._charAt(new IntegerConstant(0)), (int) 'a');

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertFalse(var1.solution().substring(3).equals("aa"));
		assertTrue(var1.solution().charAt(0) != 'a');
	}
	
	@Test
	public void Test37 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("hello"), var1);
		pathCondition._addDet(Comparator.EQ, var1._indexOf(new StringConstant("el")), 2);

		boolean result = stringPathCondition.simplify();
	
		assertFalse(result);
	}
	
	@Test
	public void Test38_1 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.ENDSWITH, new StringConstant("hello"), var1);
		pathCondition._addDet(Comparator.EQ, var1._indexOf(new StringConstant("el")), 2);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var1.solution().endsWith("hello"));
		assertTrue(var1.solution().indexOf("el") == 2);
	}
	
	@Test
	public void Test38_2 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.NOTENDSWITH, new StringConstant("hello"), var1);
		pathCondition._addDet(Comparator.EQ, var1._indexOf(new StringConstant("el")), 2);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var1.solution().endsWith("hello"));
		assertTrue(var1.solution().indexOf("el") == 2);
	}
	
	@Test
	public void Test38_3 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.ENDSWITH, new StringConstant("hello"), var1);
		pathCondition._addDet(Comparator.NE, var1._indexOf(new StringConstant("el")), 2);

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertTrue(var1.solution().endsWith("hello"));
		assertTrue(var1.solution().indexOf("el") != 2);
	}
	
	@Test
	public void Test38_4 () {
		StringSymbolic var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.NOTENDSWITH, new StringConstant("hello"), var1);
		pathCondition._addDet(Comparator.NE, var1._indexOf(new StringConstant("el")), 2);
		
		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertFalse(var1.solution().endsWith("hello"));
		assertTrue(var1.solution().indexOf("el") != 2);
	}
	
	@Test
	public void Test39_1 () {
		StringExpression var1 = new StringSymbolic("var1");
		StringExpression var2 = var1._subString(7, 2);
		stringPathCondition._addDet(StringComparator.EQUALS, var2, new StringConstant("hello"));
		pathCondition._addDet(Comparator.EQ, var1._indexOf(new StringConstant("el")), 2);

		boolean result = stringPathCondition.simplify();
		
		assertFalse(result);
	}

	@Test
	public void Test39_2 () {
		StringExpression var1 = new StringSymbolic("var1");
		StringExpression var2 = var1._subString(7, 2);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var2, new StringConstant("hello"));
		pathCondition._addDet(Comparator.EQ, var1._indexOf(new StringConstant("el")), 2);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var1.solution().substring(2,7).equals("hello"));
		assertTrue(var1.solution().indexOf("el") == 2);
	}

	@Test
	public void Test39_3 () {
		StringExpression var1 = new StringSymbolic("var1");
		StringExpression var2 = var1._subString(7, 2);
		stringPathCondition._addDet(StringComparator.EQUALS, var2, new StringConstant("hello"));
		pathCondition._addDet(Comparator.NE, var1._indexOf(new StringConstant("el")), 2);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var1.solution().substring(2,7).equals("hello"));
		assertTrue(var1.solution().indexOf("el") != 2);
	}

	@Test
	public void Test39_4 () {
		StringExpression var1 = new StringSymbolic("var1");
		StringExpression var2 = var1._subString(7, 2);
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var2, new StringConstant("hello"));
		pathCondition._addDet(Comparator.NE, var1._indexOf(new StringConstant("el")), 2);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var1.solution().substring(2,7).equals("hello"));
		assertTrue(var1.solution().indexOf("el") != 2);
	}
	
	@Test
	public void Test40_1 () {
		StringExpression var1 = new StringSymbolic("var1");
		pathCondition._addDet(Comparator.EQ, new StringConstant("hello")._indexOf(var1), 2);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue("hello".indexOf(var1.solution()) == 2);
	}
	
	@Test
	public void Test40_2 () {
		StringExpression var1 = new StringSymbolic("var1");
		pathCondition._addDet(Comparator.NE, new StringConstant("hello")._indexOf(var1), 2);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue("hello".indexOf(var1.solution()) != 2);
	}
	
	@Test
	public void Test41_1 () {
		StringExpression var1 = new StringSymbolic("var1");
		pathCondition._addDet(Comparator.EQ, new StringConstant("hello ")._indexOf(var1), 2);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue("hello".indexOf(var1.solution()) == 2);
	}
	
	@Test
	public void Test41_2 () {
		StringExpression var1 = new StringSymbolic("var1");
		pathCondition._addDet(Comparator.NE, new StringConstant("hello ")._indexOf(var1), 2);

		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertTrue("hello".indexOf(var1.solution()) != 2);
	}
	
	@Test
	public void Test42_1 () {
		StringExpression var1 = new StringSymbolic("var1");
		StringExpression var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("bol"), var2);
		pathCondition._addDet(Comparator.EQ, var2._indexOf(var1), 2);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var2.solution().startsWith("bol"));
		assertTrue(var2.solution().indexOf(var1.solution()) == 2);
	}
	
	@Test
	public void Test42_2 () {
		StringExpression var1 = new StringSymbolic("var1");
		StringExpression var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.NOTSTARTSWITH, new StringConstant("bol"), var2);
		pathCondition._addDet(Comparator.EQ, var2._indexOf(var1), 2);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var2.solution().startsWith("bol"));
		assertTrue(var2.solution().indexOf(var1.solution()) == 2);
	}
	
	@Test
	public void Test42_3 () {
		StringExpression var1 = new StringSymbolic("var1");
		StringExpression var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("bol"), var2);
		pathCondition._addDet(Comparator.NE, var2._indexOf(var1), 2);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var2.solution().startsWith("bol"));
		assertTrue(var2.solution().indexOf(var1.solution()) != 2);
	}
	
	@Test
	public void Test42_4 () {
		StringExpression var1 = new StringSymbolic("var1");
		StringExpression var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.NOTSTARTSWITH, new StringConstant("bol"), var2);
		pathCondition._addDet(Comparator.NE, var2._indexOf(var1), 2);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var2.solution().startsWith("bol"));
		assertTrue(var2.solution().indexOf(var1.solution()) != 2);
	}
	
	/*
	 * Slow automata
	 */
	//@Test
	public void remTest43 () {
		String[] solvers = new String[]{"automata", "z3_inc"};
		
		for (String solver: solvers) {
			long startTime = System.currentTimeMillis();
			System.out.println("Solver: " + solver);
			String[] options = {"+symbolic.dp=choco",
					"+symbolic.string_dp=" + solver,
					"+symbolic.string_dp_timeout_ms=0"};
			Config cfg = new Config(options);
			new SymbolicInstructionFactory(cfg);
			PathCondition pc = new PathCondition();
			StringPathCondition stringPathCondition = new StringPathCondition(pc);
			
			StringSymbolic var[] = new StringSymbolic[25];
			for (int i = 0; i < var.length; i++) {
				var[i] = new StringSymbolic ("var" + i);
				pathCondition._addDet(Comparator.EQ, var[i]._length(), new IntegerConstant(20));
			}
			
			for (int i = 0; i < var.length; i++) {
				StringBuffer sb = new StringBuffer();
				for (char j = 'a'; j < 'a' + 25; j++) {
					sb.append (j);
				}
				stringPathCondition._addDet(StringComparator.STARTSWITH, var[i], new StringConstant(sb.toString()));
			}
			System.out.println(stringPathCondition);
			boolean result = stringPathCondition.simplify();
			assertTrue(result);
			System.out.println(solver + " " + (System.currentTimeMillis() - startTime));
		}
	}
	
	//@Test
	public void remTest44 () {
		String[] solvers = new String[]{"automata", "z3_inc"};
		
		for (String solver: solvers) {
			long startTime = System.currentTimeMillis();
			System.out.println("Solver: " + solver);
			String[] options = {"+symbolic.dp=choco",
					"+symbolic.string_dp=" + solver,
					"+symbolic.string_dp_timeout_ms=0"};
			Config cfg = new Config(options);
			new SymbolicInstructionFactory(cfg);
			PathCondition pc = new PathCondition();
			StringPathCondition stringPathCondition = new StringPathCondition(pc);
			
			StringSymbolic var[] = new StringSymbolic[25];
			//StringSymbolic var[] = new StringSymbolic[5];
			for (int i = 0; i < var.length; i++) {
				var[i] = new StringSymbolic ("var" + i);
				pathCondition._addDet(Comparator.EQ, var[i]._length(), new IntegerConstant(30));
				//pathCondition._addDet(Comparator.EQ, var[i]._length(), new IntegerConstant(5));
				stringPathCondition._addDet(StringComparator.ENDSWITH, new StringConstant(String.valueOf((char) (i + 'a'))), var[i]);
			}
			
			for (int i = 0; i < var.length-1; i++) {
				stringPathCondition._addDet(StringComparator.NOTCONTAINS, var[i], var[i+1]);
			}
			System.out.println(stringPathCondition);
			boolean result = stringPathCondition.simplify();
			assertTrue(result);
			System.out.println(solver + " " + (System.currentTimeMillis() - startTime));
		}
	}
	
	@Test
	public void Test45_1 () {
		StringExpression var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.CONTAINS, new StringConstant("a"), var1);
		
		boolean result = stringPathCondition.simplify();

		assertTrue(result);
		assertTrue(var1.solution().contains("a"));
	}
	
	@Test
	public void Test45_2 () {
		StringExpression var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.NOTCONTAINS, new StringConstant("a"), var1);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var1.solution().contains("a"));
	}
	
	@Test
	public void Test46_1 () {
		StringExpression var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.CONTAINS, new StringConstant("a"), var1);
		pathCondition._addDet(Comparator.LE, var1._length(), 3);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var1.solution().contains("a"));
		assertTrue(var1.solution().length() <= 3);
	}
	
	@Test
	public void Test46_2 () {
		StringExpression var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.NOTCONTAINS, new StringConstant("a"), var1);
		pathCondition._addDet(Comparator.LE, var1._length(), 3);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var1.solution().contains("a"));
		assertTrue(var1.solution().length() <= 3);
	}
	
	@Test
	public void Test46_3 () {
		StringExpression var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.CONTAINS, new StringConstant("a"), var1);
		pathCondition._addDet(Comparator.GT, var1._length(), 3);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var1.solution().contains("a"));
		assertTrue(var1.solution().length() > 3);
	}
	
	@Test
	public void Test46_4 () {
		StringExpression var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.NOTCONTAINS, new StringConstant("a"), var1);
		pathCondition._addDet(Comparator.GT, var1._length(), 3);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var1.solution().contains("a"));
		assertTrue(var1.solution().length() > 3);
	}
	
	@Test
	public void Test47_1 () {
		StringExpression var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.CONTAINS, new StringConstant("hello"), var1);
		pathCondition._addDet(Comparator.LE, var1._length(), 3);

		boolean result = stringPathCondition.simplify();
		
		assertFalse(result);
	}
	
	@Test
	public void Test47_2 () {
		StringExpression var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.CONTAINS, new StringConstant("hello"), var1);
		pathCondition._addDet(Comparator.GT, var1._length(), 3);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var1.solution().contains("hello"));
		assertTrue(var1.solution().length() > 3);
	}
	
	@Test
	public void Test47_3 () {
		StringExpression var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.NOTCONTAINS, new StringConstant("hello"), var1);
		pathCondition._addDet(Comparator.LE, var1._length(), 3);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var1.solution().contains("hello"));
		assertTrue(var1.solution().length() <= 3);
	}
	
	@Test
	public void Test47_4 () {
		StringExpression var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.NOTCONTAINS, new StringConstant("hello"), var1);
		pathCondition._addDet(Comparator.GT, var1._length(), 3);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var1.solution().contains("hello"));
		assertTrue(var1.solution().length() > 3);
	}
	
	@Test
	public void Test48_1 () {
		StringExpression var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.CONTAINS, var1, new StringConstant("hello"));
		pathCondition._addDet(Comparator.LE, var1._length(), 3);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue("hello".contains(var1.solution()));
		assertTrue(var1.solution().length() <= 3);
	}
	
	@Test
	public void Test48_2 () {
		StringExpression var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.CONTAINS, var1, new StringConstant("hello"));
		pathCondition._addDet(Comparator.GT, var1._length(), 3);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue("hello".contains(var1.solution()));
		assertTrue(var1.solution().length() > 3);
	}
	
	@Test
	public void Test48_3 () {
		StringExpression var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.NOTCONTAINS, new StringConstant("hello"), var1);
		pathCondition._addDet(Comparator.LE, var1._length(), 3);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse("hello".contains(var1.solution()));
		assertTrue(var1.solution().length() <= 3);
	}
	
	@Test
	public void Test48_4 () {
		StringExpression var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.NOTCONTAINS, new StringConstant("hello"), var1);
		pathCondition._addDet(Comparator.GT, var1._length(), 3);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse("hello".contains(var1.solution()));
		assertTrue(var1.solution().length() > 3);
	}
	
	@Test
	public void Test49_1 () {
		StringExpression var1 = new StringSymbolic("var1");
		StringExpression var2 = new StringSymbolic("var2");
		pathCondition._addDet(Comparator.GT, var1._length(), 3);
		pathCondition._addDet(Comparator.LT, var2._length(), 2);
		stringPathCondition._addDet(StringComparator.CONTAINS, var1, var2);

		boolean result = stringPathCondition.simplify();
		
		assertFalse(result);
	}
	
	@Test
	public void Test49_2 () {
		StringExpression var1 = new StringSymbolic("var1");
		StringExpression var2 = new StringSymbolic("var2");
		pathCondition._addDet(Comparator.LT, var1._length(), 3);
		pathCondition._addDet(Comparator.GT, var2._length(), 2);
		stringPathCondition._addDet(StringComparator.CONTAINS, var1, var2);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var2.solution().contains(var1.solution()));
		assertTrue(var1.solution().length() < 3);
		assertTrue(var2.solution().length() > 2);
	}
	
	@Test
	public void Test50 () {
		StringExpression var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.EQUALS, var1, new StringConstant("aaa"));
		stringPathCondition._addDet(StringComparator.NOTEQUALS, var1, new StringConstant("aaa"));

		boolean result = stringPathCondition.simplify();
		
		assertFalse(result);
	}
	
	@Test
	public void Test51 () {
		StringExpression var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("a"), var1);
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("b"), var1);

		boolean result = stringPathCondition.simplify();
		
		assertFalse(result);
	}
	
	@Test
	public void Test52 () {
		StringExpression var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("ab"), var1);
		stringPathCondition._addDet(StringComparator.ENDSWITH, new StringConstant("ab"), var1);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var1.solution().startsWith("ab"));
		assertTrue(var1.solution().endsWith("ab"));
	}
	
	@Test
	public void Test53_1 () {
		StringExpression var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("c"), var1);
		pathCondition._addDet(Comparator.EQ, var1._indexOf(new StringConstant("d")), -1);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var1.solution().startsWith("c"));
		assertTrue(var1.solution().indexOf("d") == -1);
	}
	
	@Test
	public void Test53_2 () {
		StringExpression var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("c"), var1);
		pathCondition._addDet(Comparator.EQ, var1._indexOf(new StringConstant("d")), 2);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var1.solution().startsWith("c"));
		assertTrue(var1.solution().indexOf("d") == 2);
	}
	
	@Test
	public void Test54 () {
		StringExpression var1 = new StringSymbolic("var1");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("c"), var1);
		stringPathCondition._addDet(StringComparator.CONTAINS, new StringConstant("d"), var1);
		pathCondition._addDet(Comparator.EQ, var1._indexOf(new StringConstant("d")), -1);

		boolean result = stringPathCondition.simplify();
		
		assertFalse(result);
	}
	
	@Test
	public void Test55 () {
		StringExpression var1 = new StringSymbolic("var1");
		StringExpression var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant(" "), var1);
		stringPathCondition._addDet(StringComparator.STARTSWITH, var2, var1);
		stringPathCondition._addDet(StringComparator.ENDSWITH, new StringConstant("c"), var1);
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("ab"), var2._trim());

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var1.solution().startsWith(" "));
		assertTrue(var1.solution().startsWith(var2.solution()));
		assertTrue(var1.solution().endsWith("c"));
		assertTrue(var2.solution().trim().equals("ab"));
	}
	
	@Test
	public void Test56 () {
		StringExpression var1 = new StringSymbolic("var1");
		StringExpression var2 = new StringSymbolic("var2");
		StringExpression var3 = var1._concat(var2);
		stringPathCondition._addDet(StringComparator.EQUALS, new StringConstant("hello"), var3._subString(new IntegerConstant(2)));
		stringPathCondition._addDet(StringComparator.EQUALS, var1, var3._subString(new IntegerConstant(2), new IntegerConstant(0)));

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var3.solution().substring(2).equals("hello"));
		assertTrue(var3.solution().substring(0,2).equals(var1.solution()));
	}
	
	@Test
	public void Test57_1 () {
		StringExpression var1 = new StringSymbolic("var1");
		StringExpression var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("bol"), var2);
		pathCondition._addDet(Comparator.EQ, var2._indexOf(var1, new IntegerConstant(0)), 2);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var2.solution().startsWith("bol"));
		assertTrue(var2.solution().indexOf(var1.solution()) == 2);
	}
	
	@Test
	public void Test57_2 () {
		StringExpression var1 = new StringSymbolic("var1");
		StringExpression var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.NOTSTARTSWITH, new StringConstant("bol"), var2);
		pathCondition._addDet(Comparator.EQ, var2._indexOf(var1, new IntegerConstant(0)), 2);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var2.solution().startsWith("bol"));
		// assertTrue(var2.solution().indexOf(var1.solution()) == 2);
		assertTrue(var2.solution().indexOf(var1.solution(), 2) == 2);
	}
	
	@Test
	public void Test57_3 () {
		StringExpression var1 = new StringSymbolic("var1");
		StringExpression var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("bol"), var2);
		pathCondition._addDet(Comparator.NE, var2._indexOf(var1, new IntegerConstant(0)), 2);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var2.solution().startsWith("bol"));
		assertTrue(var2.solution().indexOf(var1.solution()) != 2);
	}
	
	@Test
	public void Test57_4 () {
		StringExpression var1 = new StringSymbolic("var1");
		StringExpression var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.NOTSTARTSWITH, new StringConstant("bol"), var2);
		pathCondition._addDet(Comparator.NE, var2._indexOf(var1, new IntegerConstant(0)), 2);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var2.solution().startsWith("bol"));
		assertTrue(var2.solution().indexOf(var1.solution()) != 2);
	}
	
	@Test
	public void Test58_1 () {
		SymbolicInteger var1 = new SymbolicInteger("var1");
		StringExpression var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("bol"), var2);
		pathCondition._addDet(Comparator.EQ, var2._indexOf(var1), 2);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var2.solution().startsWith("bol"));
		assertTrue(var2.solution().indexOf(var1.solutionChar()) == 2);
	}
	
	@Test
	public void Test58_2 () {
		SymbolicInteger var1 = new SymbolicInteger("var1");
		StringExpression var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.NOTSTARTSWITH, new StringConstant("bol"), var2);
		pathCondition._addDet(Comparator.EQ, var2._indexOf(var1), 2);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var2.solution().startsWith("bol"));
		assertTrue(var2.solution().indexOf(var1.solutionInt()) == 2);
	}
	
	@Test
	public void Test58_3 () {
		SymbolicInteger var1 = new SymbolicInteger("var1");
		StringExpression var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("bol"), var2);
		pathCondition._addDet(Comparator.NE, var2._indexOf(var1), 2);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var2.solution().startsWith("bol"));
		assertTrue(var2.solution().indexOf(var1.solutionInt()) != 2);
	}
	
	@Test
	public void Test58_4 () {
		SymbolicInteger var1 = new SymbolicInteger("var1");
		StringExpression var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.NOTSTARTSWITH, new StringConstant("bol"), var2);
		pathCondition._addDet(Comparator.NE, var2._indexOf(var1), 2);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var2.solution().startsWith("bol"));
		assertTrue(var2.solution().indexOf(var1.solutionInt()) != 2);
	}
	
	@Test
	public void Test59_1 () {
		SymbolicInteger var1 = new SymbolicInteger("var1");
		StringExpression var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("bol"), var2);
		pathCondition._addDet(Comparator.EQ, var2._indexOf(var1, new IntegerConstant(0)), 2);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var2.solution().startsWith("bol"));
		assertTrue(var2.solution().indexOf(var1.solutionInt()) == 2);
	}
	
	@Test
	public void Test59_2 () {
		SymbolicInteger var1 = new SymbolicInteger("var1");
		StringExpression var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.NOTSTARTSWITH, new StringConstant("bol"), var2);
		pathCondition._addDet(Comparator.EQ, var2._indexOf(var1, new IntegerConstant(0)), 2);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var2.solution().startsWith("bol"));
		assertTrue(var2.solution().indexOf(var1.solutionChar()) == 2);
	}
	
	@Test
	public void Test59_3 () {
		SymbolicInteger var1 = new SymbolicInteger("var1");
		StringExpression var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("bol"), var2);
		pathCondition._addDet(Comparator.NE, var2._indexOf(var1, new IntegerConstant(0)), 2);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(var2.solution().startsWith("bol"));
		assertTrue(var2.solution().indexOf(var1.solutionChar()) != 2);
	}
	
	@Test
	public void Test59_4 () {
		SymbolicInteger var1 = new SymbolicInteger("var1");
		StringExpression var2 = new StringSymbolic("var2");
		stringPathCondition._addDet(StringComparator.NOTSTARTSWITH, new StringConstant("bol"), var2);
		pathCondition._addDet(Comparator.NE, var2._indexOf(var1, new IntegerConstant(0)), 2);

		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertFalse(var2.solution().startsWith("bol"));
		assertTrue(var2.solution().indexOf(var1.solutionChar()) != 2);
	}
	
	@Test
	//MS_Example
	public void Test60_1 () {	
		StringExpression str = new StringSymbolic("str");
		IntegerExpression ie1 = str._lastIndexOf(new IntegerConstant('/'));
		pathCondition._addDet(Comparator.GE, ie1, new IntegerConstant(0));
		StringExpression rest = str._subString(ie1._plus(1));
		stringPathCondition._addDet(StringComparator.CONTAINS, new StringConstant("EasyChair"), rest);
		stringPathCondition._addDet(StringComparator.STARTSWITH, new StringConstant("http://"), str);
	
		boolean result = stringPathCondition.simplify();
		
		assertTrue(result);
		assertTrue(str.solution().lastIndexOf('/') >= 0);
		assertTrue(str.solution().substring(ie1.solutionInt() + 1).contains("EasyChair"));
		assertTrue(str.solution().startsWith("http://"));
	}
}
