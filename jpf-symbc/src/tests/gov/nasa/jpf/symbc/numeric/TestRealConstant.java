package gov.nasa.jpf.symbc.numeric;

import gov.nasa.jpf.util.test.TestJPF;

import static gov.nasa.jpf.symbc.numeric.Operator.*;

import org.junit.Test;

public class TestRealConstant extends TestJPF {

	@Test
	public void constructorShouldCreateObjectAndSetValue() {
		double value = 2.0d;

		RealConstant realConstant = new RealConstant(value);

		assertEquals(realConstant.getValue(), 2);
	}

	@Test
	public void plusDoubleShouldReturnCorrectSum() {
		double value = 11.0d;
		double[] addendums = { 0.0d, 10.0d, -5.0d, value };
		RealConstant realConstant = new RealConstant(value);

		for (double addendum : addendums) {
			RealConstant result = (RealConstant) realConstant._plus(addendum);

			assertEquals(result.getValue(), value + addendum);
		}
	}

	@Test
	public void plusRealConstantShouldReturnCorrectSum() {
		double value = 11.0d;
		double[] addendums = { 0.0d, 10.0d, -5.0d, value };
		RealConstant realConstant = new RealConstant(value);

		for (double addendum : addendums) {
			RealConstant addendumConstant = new RealConstant(addendum);
			RealConstant result = (RealConstant) realConstant._plus(addendumConstant);

			assertEquals(result.getValue(), value + addendum);
		}
	}

	@Test
	public void plusBinaryRealExpressionSolutionShouldReturnCorrectSum() {
		double value1 = 11.0d;
		double value2 = -5.0d;
		double value3 = 14.0d;
		RealConstant realConstant1 = new RealConstant(value1);
		RealConstant realConstant2 = new RealConstant(value2);
		RealConstant realConstant3 = new RealConstant(value3);
		BinaryRealExpression binaryExpression = new BinaryRealExpression(realConstant2, PLUS, realConstant3);

		RealExpression sumExpression = realConstant1._plus(binaryExpression);
		double result = sumExpression.solution();

		assertEquals(result, value1 + (value2 + value3));
	}

	@Test
	public void minusDoubleShouldReturnCorrectDifference() {
		double minued = 2.0d;
		double[] subtrahends = { 0.0d, 1.0d, 5.0d, -7.0d, minued };
		RealConstant minuedConstant = new RealConstant(minued);

		for (double subtrahend : subtrahends) {
			RealConstant result = (RealConstant) minuedConstant._minus(subtrahend);

			assertEquals(result.getValue(), minued - subtrahend);
		}
	}

	@Test
	public void minusRealConstantShouldReturnCorrectDifference() {
		double minued = 2.0d;
		double[] subtrahends = { 0.0d, 1.0d, 5.0d, -7.0d, minued };
		RealConstant minuedConstant = new RealConstant(minued);

		for (double subtrahend : subtrahends) {
			RealConstant subrahendConstant = new RealConstant(subtrahend);
			RealConstant result = (RealConstant) minuedConstant._minus(subrahendConstant);

			assertEquals(result.getValue(), minued - subtrahend);
		}
	}

	@Test
	public void minusBinaryRealExpressionSolutionShouldReturnCorrectDifference() {
		double value1 = 11.0d;
		double value2 = -5.0d;
		double value3 = 14.0d;
		RealConstant realConstant1 = new RealConstant(value1);
		RealConstant realConstant2 = new RealConstant(value2);
		RealConstant realConstant3 = new RealConstant(value3);
		BinaryRealExpression binaryExpression = new BinaryRealExpression(realConstant2, MINUS, realConstant3);

		RealExpression sumExpression = realConstant1._minus(binaryExpression);
		double result = sumExpression.solution();

		assertEquals(result, value1 - (value2 - value3));
	}

	@Test
	public void mulDoubleShouldReturnCorrectProduct() {
		double[] multipliers = { 0.0d, 1.0d, 7.0d, 8.0d, -2.0d, -3.0d, 13.3d, -6.5d };

		for (double multiplier1 : multipliers) {
			RealConstant multiplierConstant = new RealConstant(multiplier1);
			for (double multiplier2 : multipliers) {
				RealConstant result = (RealConstant) multiplierConstant._mul(multiplier2);

				assertEquals(result.getValue(), multiplier1 * multiplier2);
			}
		}
	}

	@Test
	public void mulRealConstantShouldReturnCorrectProduct() {
		double[] multipliers = { 0.0d, 1.0d, 7.0d, 8.0d, -2.0d, -3.0d, 13.3d, -6.5d };

		for (double multiplier1 : multipliers) {
			RealConstant multiplierConstant1 = new RealConstant(multiplier1);
			for (double multiplier2 : multipliers) {
				RealConstant multiplierConstant2 = new RealConstant(multiplier2);
				RealConstant result = (RealConstant) multiplierConstant1._mul(multiplierConstant2);

				assertEquals(result.getValue(), multiplier1 * multiplier2);
			}
		}
	}

	@Test
	public void mulBinaryRealExpressionSolutionShouldReturnCorrectProduct() {
		double value1 = 11.0d;
		double value2 = -5.0d;
		double value3 = 14.0d;
		RealConstant realConstant1 = new RealConstant(value1);
		RealConstant realConstant2 = new RealConstant(value2);
		RealConstant realConstant3 = new RealConstant(value3);
		BinaryRealExpression binaryExpression = new BinaryRealExpression(realConstant2, MUL, realConstant3);

		RealExpression sumExpression = realConstant1._mul(binaryExpression);
		double result = sumExpression.solution();

		assertEquals(result, value1 * (value2 * value3));
	}

	@Test
	public void divDoubleShouldReturnCorrectQuotient() {
		double[] dividends = { 0.0d, -6.5d, 11.0d, -15.0d, 12.0d };
		double[] dividers = { 1.0d, 3.0d, 5.0d, -6.0d, -7.0d, 12.0d };

		for (double dividend : dividends) {
			RealConstant dividendConstant = new RealConstant(dividend);
			for (double divider : dividers) {
				RealConstant result = (RealConstant) dividendConstant._div(divider);

				assertEquals(result.getValue(), dividend / divider);
			}
		}
	}

	@Test
	public void divRealConstantShouldReturnCorrectQuotient() {
		double dividend = 12.0d;
		double[] dividers = { 1.0d, 3.0d, 5.0d, -6.0d, -7.0d, dividend };
		RealConstant dividendConstant = new RealConstant(dividend);

		for (double divider : dividers) {
			RealConstant dividerConstant = new RealConstant(divider);
			RealConstant result = (RealConstant) dividendConstant._div(dividerConstant);

			assertEquals(result.getValue(), dividend / divider);
		}
	}

	@Test
	public void divBinaryRealExpressionSolutionShouldReturnCorrectQuotient() {
		double value1 = 11.0d;
		double value2 = -5.0d;
		double value3 = 14.0d;
		RealConstant realConstant1 = new RealConstant(value1);
		RealConstant realConstant2 = new RealConstant(value2);
		RealConstant realConstant3 = new RealConstant(value3);
		BinaryRealExpression binaryExpression = new BinaryRealExpression(realConstant2, DIV, realConstant3);

		RealExpression sumExpression = realConstant1._div(binaryExpression);
		double result = sumExpression.solution();

		assertEquals(result, value1 / (value2 / value3));
	}

	@Test
	public void negShouldReturnCorrectNegation() {
		double[] values = { 0.0d, -5.0d, 8.0d };

		for (double value : values) {
			RealConstant toNegate = new RealConstant(value);
			RealConstant result = (RealConstant) toNegate._neg();

			assertEquals(result.getValue(), -value);
		}
	}

	@Test
	public void equalsWithEqualRealConstantShouldReturnTrue() {
		double value = 2.0d;
		RealConstant realConstant1 = new RealConstant(value);
		RealConstant realConstant2 = new RealConstant(value);

		boolean result = realConstant1.equals(realConstant2);

		assertTrue(result);
	}

	@Test
	public void equalsWithNotEqualRealConstantShouldReturnFalse() {
		double value = 2.0d;
		RealConstant realConstant1 = new RealConstant(value);
		RealConstant realConstant2 = new RealConstant(value + 1.0d);

		boolean result = realConstant1.equals(realConstant2);

		assertFalse(result);
	}

	@Test
	public void equalsWithNotRealConstantShouldReturnFalse() {
		RealConstant realConstant = new RealConstant(2.0d);

		boolean result = realConstant.equals(new Double(2.0d));

		assertFalse(result);
	}

	@Test
	public void solutionShouldReturnSetValue() {
		double value = 2.0d;
		RealConstant realConstant = new RealConstant(value);

		double result = realConstant.solution();

		assertEquals(result, value);
	}
	
	@Test
	public void compareToForEqualRealConstantShouldReturnZero() {
		double value = 17.5d;
		RealConstant realConstant1 = new RealConstant(value);
		RealConstant realConstant2 = new RealConstant(value);
		
		assertEquals(0, realConstant1.compareTo(realConstant2));
	}
	
	@Test
	public void compareToWithGreaterRealConstantShouldReturnMinusOne() {
		double value = 17.5d;
		RealConstant realConstant1 = new RealConstant(value);
		RealConstant realConstant2 = new RealConstant(value + 1);
		
		assertEquals(-1, realConstant1.compareTo(realConstant2));
	}
	
	@Test
	public void compareToWithLesserIntegerConstantShouldReturnOne() {
		double value = 17.5d;
		RealConstant realConstant1 = new RealConstant(value);
		RealConstant realConstant2 = new RealConstant(value - 1);
		
		assertEquals(1, realConstant1.compareTo(realConstant2));
	}
}
