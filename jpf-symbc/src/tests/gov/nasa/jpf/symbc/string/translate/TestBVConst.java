package gov.nasa.jpf.symbc.string.translate;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestBVConst {

	@Test
	public void constructorShouldSetValue() {
		int[] values = new int[] { -36, 0, 15 };
		for (int value : values) {
			BVConst bVConst = new BVConst(value);

			assertEquals(value, bVConst.value);
		}
	}

	@Test
	public void toSMTLibShouldReturnCorrectTranslation() {
		int[] values = new int[] { -36, 0, 15 };
		for (int value : values) {
			BVConst bVConst = new BVConst(value);

			assertEquals(String.format("(_ bv%s 8)", value), bVConst.toSMTLib());
		}
	}
}
