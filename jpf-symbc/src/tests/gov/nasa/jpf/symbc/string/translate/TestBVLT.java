package gov.nasa.jpf.symbc.string.translate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestBVLT {
	
	@Test
	public void constructorShouldSetLeftAndRight() {
		BVConst left = new BVConst(-7);
		BVConst right = new BVConst(24);
		
		BVLT bVLT = new BVLT(left, right);
		
		assertTrue(left == bVLT.left);
		assertTrue(right == bVLT.right);
	}
	
	@Test
	public void toSMTLibShouldReturnCorrectTranslation() {
		BVConst left = new BVConst(-7);
		BVConst right = new BVConst(24);
		
		BVLT bVLT = new BVLT(left, right);
		
		assertEquals("(bvult (_ bv-7 8) (_ bv24 8))", bVLT.toSMTLib());
	}
}
