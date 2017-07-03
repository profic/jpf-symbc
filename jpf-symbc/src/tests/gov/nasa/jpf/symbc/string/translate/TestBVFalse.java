package gov.nasa.jpf.symbc.string.translate;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestBVFalse {

	@Test
	public void toSMTLibShouldReturnFalse() {
		BVFalse bVFalse = new BVFalse();
		assertEquals("false", bVFalse.toSMTLib());
	}
}
