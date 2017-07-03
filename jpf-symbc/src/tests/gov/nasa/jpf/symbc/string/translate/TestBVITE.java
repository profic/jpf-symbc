package gov.nasa.jpf.symbc.string.translate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestBVITE {
	
	@Test
	public void constructorShouldSetIfElseAndThenParts() {
		BVTrue bVTrue = new BVTrue();
		BVConst bVConst = new BVConst(10);
		BVNot bVNot = new BVNot(new BVFalse());
		
		BVITE bVITE = new BVITE(bVTrue, bVConst, bVNot);
		
		assertTrue(bVTrue == bVITE.ifpart);
		assertTrue(bVConst == bVITE.thenpart);
		assertTrue(bVNot == bVITE.elsepart);
	}
	
	@Test
	public void toSMTLibShouldReturnCorrectTranslation() {
		BVTrue bVTrue = new BVTrue();
		BVConst bVConst = new BVConst(10);
		BVNot bVNot = new BVNot(new BVFalse());
		
		BVITE bVITE = new BVITE(bVTrue, bVConst, bVNot);
		
		assertEquals("(ite true (_ bv10 8) (not false))", bVITE.toSMTLib());
	}
}
