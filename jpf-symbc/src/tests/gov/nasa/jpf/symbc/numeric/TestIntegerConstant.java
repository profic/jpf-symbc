package gov.nasa.jpf.symbc.numeric;

import gov.nasa.jpf.util.test.TestJPF;

import org.junit.Test;

public class TestIntegerConstant extends TestJPF {

	@Test
	public void constructorShouldSetValue() {
		long value = 120;

		IntegerConstant integerConstant = new IntegerConstant(value);

		assertEquals(integerConstant.getValue(), value);
	}

	@Test
	public void plusLongShouldReturnCorrectSum() {
		long value = 11;
		long[] addendums = { 0, 10, -5, value };
		IntegerConstant integerConstant = new IntegerConstant(value);

		for (long addendum : addendums) {
			IntegerConstant result = (IntegerConstant) integerConstant._plus(addendum);

			assertEquals(result.getValue(), value + addendum);
		}
	}

	@Test
	public void plusIntegerConstantShouldReturnCorrectSum() {
		long value = 11;
		long[] addendums = { 0, 10, -5, value };
		IntegerConstant integerConstant = new IntegerConstant(value);

		for (long addendum : addendums) {
			IntegerConstant addendumConstant = new IntegerConstant(addendum);
			IntegerConstant result = (IntegerConstant) integerConstant._plus(addendumConstant);

			assertEquals(result.getValue(), value + addendum);
		}
	}

	@Test
	public void minusLongShouldReturnCorrectDifference() {
		long minued = 4;
		long[] subtrahends = { 0, 1, 5, -7, minued };
		IntegerConstant minuedConstant = new IntegerConstant(minued);

		for (long subtrahend : subtrahends) {
			IntegerConstant result = (IntegerConstant) minuedConstant._minus(subtrahend);

			assertEquals(result.getValue(), minued - subtrahend);
		}
	}

	@Test
	public void minusIntegerConstantShouldReturnCorrectDifference() {
		long minued = 4;
		long[] subtrahends = { 0, 1, 5, -7, minued };
		IntegerConstant minuedConstant = new IntegerConstant(minued);

		for (long subtrahend : subtrahends) {
			IntegerConstant subtrahendConstant = new IntegerConstant(subtrahend);
			IntegerConstant result = (IntegerConstant) minuedConstant._minus(subtrahendConstant);

			assertEquals(result.getValue(), minued - subtrahend);
		}
	}

	@Test
	public void minusReverseLongShouldReturnCorrectDifference() {
		long subtrahend = 4;
		long[] minueds = { 0, 1, 5, -7, subtrahend };
		IntegerConstant subtrahendConstant = new IntegerConstant(subtrahend);

		for (long minued : minueds) {
			IntegerConstant result = (IntegerConstant) subtrahendConstant._minus_reverse(minued);

			assertEquals(result.getValue(), minued - subtrahend);
		}
	}

	@Test
	public void mulLongShouldReturnCorrectProduct() {
		long[] multipliers = { 0, 1, 7, 16, -6, -21 };

		for (long multiplier1 : multipliers) {
			IntegerConstant multiplierConstant = new IntegerConstant(multiplier1);
			for (long multiplier2 : multipliers) {
				IntegerConstant result = (IntegerConstant) multiplierConstant._mul(multiplier2);

				assertEquals(result.getValue(), multiplier1 * multiplier2);
			}
		}
	}

	@Test
	public void mulIntegerConstantShouldReturnCorrectProduct() {
		long[] multipliers = { 0, 1, 7, 16, -6, -21 };

		for (long multiplier1 : multipliers) {
			IntegerConstant multiplierConstant1 = new IntegerConstant(multiplier1);
			for (long multiplier2 : multipliers) {
				IntegerConstant multiplierConstant2 = new IntegerConstant(multiplier2);
				IntegerConstant result = (IntegerConstant) multiplierConstant1._mul(multiplierConstant2);

				assertEquals(result.getValue(), multiplier1 * multiplier2);
			}
		}
	}

	@Test
	public void divLongShouldReturnCorrectQuotient() {
		long[] dividends = { 0, 11, 12, -6, -15 };
		long[] dividers = { 1, 4, 5, -6, -7 };

		for (long dividend : dividends) {
			IntegerConstant dividendConstant = new IntegerConstant(dividend);
			for (long divider : dividers) {
				IntegerConstant result = (IntegerConstant) dividendConstant._div(divider);

				assertEquals(result.getValue(), dividend / divider);
			}
		}
	}

	@Test
	public void divIntegerConstShouldReturnCorrectQuotient() {
		long[] dividends = { 0, 11, 12, -6, -15 };
		long[] dividers = { 1, 4, 5, -6, -7 };

		for (long dividend : dividends) {
			IntegerConstant dividendConstant = new IntegerConstant(dividend);
			for (long divider : dividers) {
				IntegerConstant dividerConst = new IntegerConstant(divider);
				IntegerConstant result = (IntegerConstant) dividendConstant._div(dividerConst);

				assertEquals(result.getValue(), dividend / divider);
			}
		}
	}

	@Test
	public void divReverseLongShouldReturnCorrectQuotient() {
		long[] dividers = { 1, 4, 5, -6, -7 };
		long[] dividends = { 0, 11, 12, -6, -15 };

		for (long divider : dividers) {
			IntegerConstant dividerConstant = new IntegerConstant(divider);
			for (long dividend : dividends) {
				IntegerConstant result = (IntegerConstant) dividerConstant._div_reverse(dividend);

				assertEquals(result.getValue(), dividend / divider);
			}
		}
	}

	@Test
	public void negShouldReturnCorrectNegation() {
		long[] values = { 0, -5, 8, Long.MIN_VALUE, Long.MAX_VALUE };

		for (long value : values) {
			IntegerConstant toNegate = new IntegerConstant(value);
			IntegerConstant result = (IntegerConstant) toNegate._neg();

			assertEquals(result.getValue(), -value);
		}
	}

	@Test
	public void andLongShouldReturnCorrectBitwiseConjunction() {
		long[] values = { 11, 20, -5, -14, 0, Long.MIN_VALUE, Long.MAX_VALUE };

		for (long value : values) {
			IntegerConstant integerConstant = new IntegerConstant(value);
			for (long operand : values) {
				IntegerConstant result = (IntegerConstant) integerConstant._and(operand);

				assertEquals(result.getValue(), value & operand);
			}
		}
	}

	@Test
	public void andIntegerConstantShouldReturnCorrectBitwiseConjunction() {
		long[] values = { 11, 20, -5, -14, 0, Long.MIN_VALUE, Long.MAX_VALUE };

		for (long value : values) {
			IntegerConstant integerConstant = new IntegerConstant(value);
			for (long operand : values) {
				IntegerConstant operandConstant = new IntegerConstant(operand);
				IntegerConstant result = (IntegerConstant) integerConstant._and(operandConstant);

				assertEquals(result.getValue(), value & operand);
			}
		}
	}

	@Test
	public void orLongShouldReturnCorrectBitwiseDisjunction() {
		long[] values = { 11, 20, -5, -14, 0, Long.MIN_VALUE, Long.MAX_VALUE };

		for (long value : values) {
			IntegerConstant integerConstant = new IntegerConstant(value);
			for (long operand : values) {
				IntegerConstant result = (IntegerConstant) integerConstant._or(operand);

				assertEquals(result.getValue(), value | operand);
			}
		}
	}

	@Test
	public void orIntegerConstantShouldReturnCorrectBitwiseDisjunction() {
		long[] values = { 11, 20, -5, -14, 0, Long.MIN_VALUE, Long.MAX_VALUE };

		for (long value : values) {
			IntegerConstant integerConstant = new IntegerConstant(value);
			for (long operand : values) {
				IntegerConstant operandConstant = new IntegerConstant(operand);
				IntegerConstant result = (IntegerConstant) integerConstant._or(operandConstant);

				assertEquals(result.getValue(), value | operand);
			}
		}
	}

	@Test
	public void xorLongShouldReturnCorrectBitwiseDisjunction() {
		long[] values = { 11, 20, -5, -14, 0, Long.MIN_VALUE, Long.MAX_VALUE };

		for (long value : values) {
			IntegerConstant integerConstant = new IntegerConstant(value);
			for (long operand : values) {
				IntegerConstant result = (IntegerConstant) integerConstant._xor(operand);

				assertEquals(result.getValue(), value ^ operand);
			}
		}
	}

	@Test
	public void xorIntegerConstantShouldReturnCorrectBitwiseDisjunction() {
		long[] values = { 11, 20, -5, -14, 0, Long.MIN_VALUE, Long.MAX_VALUE };

		for (long value : values) {
			IntegerConstant integerConstant = new IntegerConstant(value);
			for (long operand : values) {
				IntegerConstant operandConstant = new IntegerConstant(operand);
				IntegerConstant result = (IntegerConstant) integerConstant._xor(operandConstant);

				assertEquals(result.getValue(), value ^ operand);
			}
		}
	}

	@Test
	public void shiftLLongShouldReturnCorrectShifting() {
		long[] values = { 11, 20, -5, -14, 0, Long.MIN_VALUE, Long.MAX_VALUE };
		long[] shifts = { 0, 1, -1, Long.MIN_VALUE, Long.MIN_VALUE + 1, Long.MAX_VALUE - 1 };

		for (long value : values) {
			IntegerConstant integerConstant = new IntegerConstant(value);
			for (long shift : shifts) {
				IntegerConstant result = (IntegerConstant) integerConstant._shiftL(shift);

				assertEquals(result.getValue(), value << shift);
			}
		}
	}

	@Test
	public void shiftLIntegerConstantShouldReturnCorrectShifting() {
		long[] values = { 11, 20, -5, -14, 0, Long.MIN_VALUE, Long.MAX_VALUE };
		long[] shifts = { 0, 1, -1, Long.MIN_VALUE, Long.MIN_VALUE + 1, Long.MAX_VALUE - 1 };

		for (long value : values) {
			IntegerConstant integerConstant = new IntegerConstant(value);
			for (long shift : shifts) {
				IntegerConstant shiftConstant = new IntegerConstant(shift);
				IntegerConstant result = (IntegerConstant) integerConstant._shiftL(shiftConstant);

				assertEquals(result.getValue(), value << shift);
			}
		}
	}

	@Test
	public void shiftRLongShouldReturnCorrectShifting() {
		long[] values = { 11, 20, -5, -14, 0, Long.MIN_VALUE, Long.MAX_VALUE };
		long[] shifts = { 0, 1, -1, Long.MIN_VALUE, Long.MIN_VALUE + 1, Long.MAX_VALUE - 1 };

		for (long value : values) {
			IntegerConstant integerConstant = new IntegerConstant(value);
			for (long shift : shifts) {
				IntegerConstant result = (IntegerConstant) integerConstant._shiftR(shift);

				assertEquals(result.getValue(), value >> shift);
			}
		}
	}

	@Test
	public void shiftRIntegerConstantShouldReturnCorrectShifting() {
		long[] values = { 11, 20, -5, -14, 0, Long.MIN_VALUE, Long.MAX_VALUE };
		long[] shifts = { 0, 1, -1, Long.MIN_VALUE, Long.MIN_VALUE + 1, Long.MAX_VALUE - 1 };

		for (long value : values) {
			IntegerConstant integerConstant = new IntegerConstant(value);
			for (long shift : shifts) {
				IntegerConstant shiftConstant = new IntegerConstant(shift);
				IntegerConstant result = (IntegerConstant) integerConstant._shiftR(shiftConstant);

				assertEquals(result.getValue(), value >> shift);
			}
		}
	}

	@Test
	public void shiftULongShouldReturnCorrectShifting() {
		long[] values = { 11, 20, -5, -14, 0, Long.MIN_VALUE, Long.MAX_VALUE };
		long[] shifts = { 0, 1, -1, Long.MIN_VALUE, Long.MIN_VALUE + 1, Long.MAX_VALUE - 1 };

		for (long value : values) {
			IntegerConstant integerConstant = new IntegerConstant(value);
			for (long shift : shifts) {
				IntegerConstant result = (IntegerConstant) integerConstant._shiftUR(shift);

				assertEquals(result.getValue(), value >>> shift);
			}
		}
	}

	@Test
	public void shiftUIntegerConstantShouldReturnCorrectShifting() {
		long[] values = { 11, 20, -5, -14, 0, Long.MIN_VALUE, Long.MAX_VALUE };
		long[] shifts = { 0, 1, -1, Long.MIN_VALUE, Long.MIN_VALUE + 1, Long.MAX_VALUE - 1 };

		for (long value : values) {
			IntegerConstant integerConstant = new IntegerConstant(value);
			for (long shift : shifts) {
				IntegerConstant shiftConstant = new IntegerConstant(shift);
				IntegerConstant result = (IntegerConstant) integerConstant._shiftUR(shiftConstant);

				assertEquals(result.getValue(), value >>> shift);
			}
		}
	}

	@Test
	public void solutionShouldReturnSetLong() {
		long[] values = { 0, -713, 55122, Long.MIN_VALUE, Long.MAX_VALUE };

		for (long value : values) {
			IntegerConstant integerConstant = new IntegerConstant(value);
			
			assertEquals(integerConstant.solution(), value);
		}
	}
	
	@Test
	public void solutionByteShouldReturnSetByte() {
		byte[] values = { 0, -7, 5, Byte.MIN_VALUE, Byte.MAX_VALUE };

		for (byte value : values) {
			IntegerConstant integerConstant = new IntegerConstant(value);
			
			assertEquals(integerConstant.solutionByte(), value);
		}
	}
	
	@Test
	public void solutionCharShouldReturnSetChar() {
		char[] values = { 1, 55, Character.MIN_VALUE, Character.MAX_VALUE };

		for (char value : values) {
			IntegerConstant integerConstant = new IntegerConstant(value);
			
			assertEquals(integerConstant.solutionChar(), value);
		}
	}
	
	@Test
	public void solutionIntShouldReturnSetInteger() {
		int[] values = { 0, -12, 999, Integer.MIN_VALUE, Integer.MAX_VALUE };

		for (int value : values) {
			IntegerConstant integerConstant = new IntegerConstant(value);
			
			assertEquals(integerConstant.solutionInt(), value);
		}
	}
	
	@Test
	public void solutionShortShouldReturnSetInteger() {
		int[] values = { 0, -199, 556, Short.MIN_VALUE, Short.MAX_VALUE };

		for (int value : values) {
			IntegerConstant integerConstant = new IntegerConstant(value);
			
			assertEquals(integerConstant.solutionShort(), value);
		}
	}
	
	@Test
	public void equalsIntegerConstantWithEqualValueShouldReturnTrue() {
		int value = 17;
		IntegerConstant integerConstant1 = new IntegerConstant(value);
		IntegerConstant integerConstant2 = new IntegerConstant(value);
		
		assertTrue(integerConstant1.equals(integerConstant2));
	}
	
	@Test
	public void equalsIntegerConstantWithUnequalValueShouldReturnFalse() {
		int value = 17;
		IntegerConstant integerConstant1 = new IntegerConstant(value);
		IntegerConstant integerConstant2 = new IntegerConstant(value - 5);
		
		assertFalse(integerConstant1.equals(integerConstant2));
	}
	
	@Test
	public void equalsNotIntegerConstantShouldReturnFalse() {
		int value = 17;
		IntegerConstant integerConstant = new IntegerConstant(value);
		
		assertFalse(integerConstant.equals(value));
	}
	
	@Test
	public void hashCodeForEqualIntegerConstantsShouldBeEqual() {
		int value = 17;
		IntegerConstant integerConstant1 = new IntegerConstant(value);
		IntegerConstant integerConstant2 = new IntegerConstant(value);
		
		assertTrue(integerConstant1.hashCode() == integerConstant2.hashCode());
	}
	
	@Test
	public void compareToForEqualIntegerConstantShouldReturnZero() {
		int value = 17;
		IntegerConstant integerConstant1 = new IntegerConstant(value);
		IntegerConstant integerConstant2 = new IntegerConstant(value);
		
		assertEquals(0, integerConstant1.compareTo(integerConstant2));
	}
	
	@Test
	public void compareToWithGreaterIntegerConstantShouldReturnMinusOne() {
		int value = 17;
		IntegerConstant integerConstant1 = new IntegerConstant(value);
		IntegerConstant integerConstant2 = new IntegerConstant(value + 1);
		
		assertEquals(-1, integerConstant1.compareTo(integerConstant2));
	}
	
	@Test
	public void compareToWithLesserIntegerConstantShouldReturnOne() {
		int value = 17;
		IntegerConstant integerConstant1 = new IntegerConstant(value);
		IntegerConstant integerConstant2 = new IntegerConstant(value - 1);
		
		assertEquals(1, integerConstant1.compareTo(integerConstant2));
	}
}
