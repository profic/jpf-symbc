package gov.nasa.jpf.symbc.string;

import gov.nasa.jpf.util.test.TestJPF;
import gov.nasa.jpf.symbc.numeric.IntegerConstant;
import gov.nasa.jpf.symbc.numeric.IntegerExpression;
import gov.nasa.jpf.symbc.numeric.RealConstant;
import gov.nasa.jpf.symbc.numeric.RealExpression;
import gov.nasa.jpf.symbc.string.StringConstant;
import gov.nasa.jpf.symbc.string.StringExpression;
import gov.nasa.jpf.symbc.string.SymbolicLastIndexOfInteger;

import org.junit.Test;

public class TestStringExpression extends TestJPF {
	
	@Test
	public void lastIndexOfShouldReturnCorrectSymbolicLastIndexOfInteger() {
		StringExpression string = new StringConstant("str");
		StringExpression substring = new StringConstant("tr");
		IntegerExpression integerExpression = string._lastIndexOf(substring);
		
		assertTrue(integerExpression instanceof SymbolicLastIndexOfInteger);
		SymbolicLastIndexOfInteger symLastIndexOfInt = (SymbolicLastIndexOfInteger)integerExpression;
		
		assertTrue(symLastIndexOfInt.getSource().equals(string));
		assertTrue(symLastIndexOfInt.getExpression().equals(substring));
	}
	
	@Test
	public void concatEmptyWithStringsShouldReturnCorrectString() {
		StringExpression stringExpression = new StringConstant();
		String string1 = "str1";
		String string2 = "str2";
		
		stringExpression = stringExpression._concat(string1);
		stringExpression = stringExpression._concat(string2);
		
		assertEquals(string1 + string2, stringExpression.solution());
	}
	
	@Test
	public void concatNonEmptyWithStringsShouldReturnCorrectString() {
		String initString = "str1";
		StringExpression stringExpression = new StringConstant(initString);
		String string1 = "str2";
		String string2 = "str3";
		
		stringExpression = stringExpression._concat(string1);
		stringExpression = stringExpression._concat(string2);
		
		assertEquals(initString + string1 + string2, stringExpression.solution());
	}
	
	@Test
	public void concatEmptyWithStringExpressionsShouldReturnCorrectString() {
		StringExpression stringExpression = new StringConstant();
		StringExpression string1 = new StringConstant("str1");
		StringExpression string2 = new StringConstant("str2");
		
		stringExpression = stringExpression._concat(string1);
		stringExpression = stringExpression._concat(string2);
		
		assertEquals("str1str2", stringExpression.solution());
	}
	
	@Test
	public void concatNonEmptyWithStringExpressionsShouldReturnCorrectString() {
		StringExpression stringExpression = new StringConstant("str1");
		StringExpression string1 = new StringConstant("str2");
		StringExpression string2 = new StringConstant("str3");
		
		stringExpression = stringExpression._concat(string1);
		stringExpression = stringExpression._concat(string2);
		
		assertEquals("str1str2str3", stringExpression.solution());
	}
	
	@Test
	public void concatEmptyWithIntegerExpressionsShouldReturnCorrectString() {
		StringExpression stringExpression = new StringConstant();
		IntegerExpression integer1 = new IntegerConstant(1);
		IntegerExpression integer2 = new IntegerConstant(2);
		
		stringExpression = stringExpression._concat(integer1);
		stringExpression = stringExpression._concat(integer2);
		
		assertEquals("12", stringExpression.solution());
	}
	
	@Test
	public void concatStringWithIntegerExpressionsShouldReturnCorrectString() {
		StringExpression stringExpression = new StringConstant("str");
		IntegerExpression integer1 = new IntegerConstant(1);
		IntegerExpression integer2 = new IntegerConstant(2);
		
		stringExpression = stringExpression._concat(integer1);
		stringExpression = stringExpression._concat(integer2);
		
		assertEquals("str12", stringExpression.solution());
	}
	
	@Test
	public void concatEmptyWithRealExpressionsShouldReturnCorrectString() {
		StringExpression stringExpression = new StringConstant();
		RealExpression real1 = new RealConstant(1.0);
		RealExpression real2 = new RealConstant(2.0);
		
		stringExpression = stringExpression._concat(real1);
		stringExpression = stringExpression._concat(real2);
		
		assertEquals("1.02.0", stringExpression.solution());
	}
	
	@Test
	public void concatNonEmptyWithRealExpressionsShouldReturnCorrectString() {
		StringExpression stringExpression = new StringConstant("str");
		RealExpression real1 = new RealConstant(1.0);
		RealExpression real2 = new RealConstant(2.0);
		
		stringExpression = stringExpression._concat(real1);
		stringExpression = stringExpression._concat(real2);
		
		assertEquals("str1.02.0", stringExpression.solution());
	}
	
	@Test
	public void substringIntAndIntegerExpressionShouldReturnCorrectSubstring() {
		StringExpression stringExpression = new StringConstant("abacaba");
		int beginIndex = 1;
		IntegerExpression endIndex = new IntegerConstant(4);
		
		StringExpression substringExpression = stringExpression._subString(beginIndex, endIndex);
		
		assertEquals("bac", substringExpression.solution());
	}
	
	@Test
	public void substringIntegerExpressionAndIntShouldReturnCorrectSubstring() {
		StringExpression stringExpression = new StringConstant("abacaba");
		IntegerExpression beginIndex = new IntegerConstant(1);
		int endIndex = 4;
		
		StringExpression substringExpression = stringExpression._subString(beginIndex, endIndex);
		
		assertEquals("bac", substringExpression.solution());
	}
	
	@Test
	public void replaceStringExpressionsShouldReturnStringWithReplacement() {
		StringExpression stringExpression = new StringConstant("abacaba");
		StringExpression target = new StringConstant("ba");
		StringExpression replacement = new StringConstant("ab");
		
		stringExpression = stringExpression._replace(target, replacement);
		
		assertEquals("aabcaab", stringExpression.solution());
	}
	
	@Test
	public void replaceStringExpressionAndStringShouldReturnStringWithReplacement() {
		StringExpression stringExpression = new StringConstant("abacaba");
		StringExpression target = new StringConstant("ba");
		String replacement = "ab";
		
		stringExpression = stringExpression._replace(target, replacement);
		
		assertEquals("aabcaab", stringExpression.solution());
	}
	
	@Test
	public void replaceStringAndStringExpressionShouldReturnStringWithReplacement() {
		StringExpression stringExpression = new StringConstant("abacaba");
		String target = "ba";
		StringExpression replacement = new StringConstant("ab");
		
		stringExpression = stringExpression._replace(target, replacement);
		
		assertEquals("aabcaab", stringExpression.solution());
	}
	
	@Test
	public void replaceStringsShouldReturnStringWithReplacement() {
		StringExpression stringExpression = new StringConstant("abacaba");
		String target = "ba";
		String replacement = "ab";
		
		stringExpression = stringExpression._replace(target, replacement);
		
		assertEquals("aabcaab", stringExpression.solution());
	}
	
	@Test
	public void replaceFirstStringExpressionsShouldReturnStringWithOnlyFirstReplacement() {
		StringExpression stringExpression = new StringConstant("abacaba");
		StringExpression target = new StringConstant("ba");
		StringExpression replacement = new StringConstant("ab");
		
		stringExpression = stringExpression._replaceFirst(target, replacement);
		
		assertEquals("aabcaba", stringExpression.solution());
	}
	
	@Test
	public void replaceFirstStringExpressionAndStringShouldReturnStringWithOnlyFirstReplacement() {
		StringExpression stringExpression = new StringConstant("abacaba");
		StringExpression target = new StringConstant("ba");
		String replacement = "ab";
		
		stringExpression = stringExpression._replaceFirst(target, replacement);
		
		assertEquals("aabcaba", stringExpression.solution());
	}
	
	@Test
	public void replaceFirstStringAndStringExpressionShouldReturnStringWithOnlyFirstReplacement() {
		StringExpression stringExpression = new StringConstant("abacaba");
		String target = "ba";
		StringExpression replacement = new StringConstant("ab");
		
		stringExpression = stringExpression._replaceFirst(target, replacement);
		
		assertEquals("aabcaba", stringExpression.solution());
	}
	
	@Test
	public void replaceFirstStringsShouldReturnStringWithOnlyFirstReplacement() {
		StringExpression stringExpression = new StringConstant("abacaba");
		String target = "ba";
		String replacement = "ab";
		
		stringExpression = stringExpression._replaceFirst(target, replacement);
		
		assertEquals("aabcaba", stringExpression.solution());
	}
	
	@Test
	public void integerValueOfShouldReturnCorrectInteger() {
		StringExpression integerStringExpression = new StringConstant("11");
		IntegerExpression integerExpression = integerStringExpression._integerValueOf();
		
		assertEquals(11, integerExpression.solution());
	}
	
	@Test
	public void realValueOfShouldReturnCorrectReal() {
		StringExpression realStringExpression = new StringConstant("11.55");
		RealExpression realExpression = realStringExpression._realValueOf();
		
		assertEquals(11.55, realExpression.solution());
	}
}
