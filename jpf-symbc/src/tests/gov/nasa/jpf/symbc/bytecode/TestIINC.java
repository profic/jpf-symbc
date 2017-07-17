package gov.nasa.jpf.symbc.bytecode;

import gov.nasa.jpf.symbc.numeric.SymbolicInteger;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestIINC extends TestBytecode {

	@Test
	public void executeWithConcreteShouldIncreaseVariableOnStack() {
		stackFrame.setLocalVariable(0, 2);
		IINC iinc = new IINC(0, 1);
		iinc.execute(threadInfo);

		assertEquals(stackFrame.getLocalVariable(0), 3);
	}

	@Test
	public void executeWithSymbolicShouldSetPlusAttribute() {
		SymbolicInteger symInteger = new SymbolicInteger();
		stackFrame.setLocalAttr(0, symInteger);
		IINC iinc = new IINC(0, 1);
		iinc.execute(threadInfo);

		assertEquals(symInteger._plus(1), stackFrame.getLocalAttr(0));
	}
}
