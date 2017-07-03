package gov.nasa.jpf.symbc.string.translate;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestBVTrue {

	@Test
	public void toSMTLibShouldReturnTrue() {
		BVTrue bVTrue = new BVTrue();
		assertEquals("true", bVTrue.toSMTLib());
	}
}
