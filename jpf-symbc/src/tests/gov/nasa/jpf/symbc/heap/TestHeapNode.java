package gov.nasa.jpf.symbc.heap;

import static org.junit.Assert.*;

import org.junit.Test;

import gov.nasa.jpf.symbc.numeric.SymbolicInteger;
import gov.nasa.jpf.vm.ClassInfo; 

public class TestHeapNode {
	
	static protected class MockClassInfo extends ClassInfo {
	}
	
	@Test
	public void constructorShouldSetIndexTypeAndSymbolic() {
		int index = 2;
		ClassInfo classInfo = new MockClassInfo();
		SymbolicInteger symInteger = new SymbolicInteger();
		
		HeapNode heapNode = new HeapNode(index, classInfo, symInteger);
		
		assertEquals(index, heapNode.getIndex());
		assertTrue(classInfo == heapNode.getType());
		assertTrue(symInteger == heapNode.getSymbolic());
	}
	
	@Test
	public void replaceTypeShouldSetProvidedClassInfo() {
		int index = 2;
		ClassInfo classInfo1 = new MockClassInfo();
		ClassInfo classInfo2 = new MockClassInfo();
		SymbolicInteger symInteger = new SymbolicInteger();
		
		HeapNode heapNode = new HeapNode(index, classInfo1, symInteger);
		heapNode.replaceType(classInfo2);
		
		assertTrue(classInfo2 == heapNode.getType());
	}
	
	@Test
	public void setSymbolicShouldSetNewSymbolic() {
		int index = 2;
		ClassInfo classInfo = new MockClassInfo();
		SymbolicInteger symInteger1 = new SymbolicInteger();
		SymbolicInteger symInteger2 = new SymbolicInteger();
		
		HeapNode heapNode = new HeapNode(index, classInfo, symInteger1);
		heapNode.setSymbolic(symInteger2);
		
		assertTrue(symInteger2 == heapNode.getSymbolic());
	}
	
	@Test
	public void setNextShouldSetNextToProvidedHeapNode() {
		int index = 2;
		ClassInfo classInfo = new MockClassInfo();
		SymbolicInteger symInteger = new SymbolicInteger();
		
		HeapNode heapNode1 = new HeapNode(index, classInfo, symInteger);
		HeapNode heapNode2 = new HeapNode(index, classInfo, symInteger);
		heapNode1.setNext(heapNode2);
		
		assertTrue(heapNode2 == heapNode1.getNext());
	}
}
