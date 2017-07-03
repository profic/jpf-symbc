package gov.nasa.jpf.symbc.numeric;

import gov.nasa.jpf.util.test.TestJPF;

import org.junit.Test;

public class TestComparator extends TestJPF {

	@Test
	public void evaluateEqForEqualDoublesShouldReturnTrue() {
		double[] values = { 0, 15.5d, -22.7d, Double.MIN_VALUE, Double.MAX_VALUE };
		Comparator comparator = Comparator.EQ;

		for (double value : values) {
			assertTrue(comparator.evaluate(value, value));
		}
	}

	@Test
	public void evaluateEqForInequalDoublesShouldReturnFalse() {
		double[] values = { 0, 15.5d, -22.7d };
		Comparator comparator = Comparator.EQ;

		for (double value : values) {
			assertFalse(comparator.evaluate(value, value + 1));
		}
	}

	@Test
	public void evaluateEqNotForEqualDoublesShouldReturnFalse() {
		double[] values = { 0, 15.5d, -22.7d, Double.MIN_VALUE, Double.MAX_VALUE };
		Comparator comparator = Comparator.EQ;

		for (double value : values) {
			assertFalse(comparator.not().evaluate(value, value));
		}
	}

	@Test
	public void evaluateEqNotForInequalDoublesShouldReturnTrue() {
		double[] values = { 0, 15.5d, -22.7d };
		Comparator comparator = Comparator.EQ;

		for (double value : values) {
			assertTrue(comparator.not().evaluate(value, value + 1));
		}
	}

	@Test
	public void evaluateNeForEqualDoublesShouldReturnFalse() {
		double[] values = { 0, 15.5d, -22.7d, Double.MIN_VALUE, Double.MAX_VALUE };
		Comparator comparator = Comparator.NE;

		for (double value : values) {
			assertFalse(comparator.evaluate(value, value));
		}
	}

	@Test
	public void evaluateNeForInequalDoublesShouldReturnTrue() {
		double[] values = { 0, 15.5d, -22.7d };
		Comparator comparator = Comparator.NE;

		for (double value : values) {
			assertTrue(comparator.evaluate(value, value + 1));
		}
	}

	@Test
	public void evaluateNeNotForEqualDoublesShouldReturnTrue() {
		double[] values = { 0, 15.5d, -22.7d, Double.MIN_VALUE, Double.MAX_VALUE };
		Comparator comparator = Comparator.NE;

		for (double value : values) {
			assertTrue(comparator.not().evaluate(value, value));
		}
	}

	@Test
	public void evaluateNeNotForInequalDoublesShouldReturnFalse() {
		double[] values = { 0, 15.5d, -22.7d };
		Comparator comparator = Comparator.NE;

		for (double value : values) {
			assertFalse(comparator.not().evaluate(value, value + 1));
		}
	}
}
