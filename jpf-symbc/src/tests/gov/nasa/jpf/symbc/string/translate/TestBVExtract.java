package gov.nasa.jpf.symbc.string.translate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestBVExtract {
	
	@Test
	public void constructorShouldSetVarAndOffsets() {
		BVConst var = new BVConst(12);
		BVExtract bVExtract = new BVExtract(var, 2, 6);
		
		assertTrue(bVExtract.varName == var);
		assertEquals(2, bVExtract.startOffset);
		assertEquals(6, bVExtract.endOffset);
	}
	
	@Test
	public void toSMTLibShouldReturnCorrectTranslation() {
		BVConst var = new BVConst(12);
		BVExtract bVExtract = new BVExtract(var, 2, 6);
		
		assertEquals("((_ extract 2 6) (_ bv12 8))", bVExtract.toSMTLib());
	}
}
