package gov.nasa.jpf.symbc.heap;

import static org.junit.Assert.*;

import org.junit.Test;

import gov.nasa.jpf.symbc.heap.TestHeapNode.MockClassInfo;
import gov.nasa.jpf.symbc.numeric.SymbolicInteger;
import gov.nasa.jpf.vm.ClassInfo;

public class TestSymbolicInputHeap {
	
	@Test
	public void defaultConstructorShouldReturnHeapWithoutHead() {
		SymbolicInputHeap symInputHeap = new SymbolicInputHeap();
		
		assertEquals(null, symInputHeap.header());
		assertEquals(0, symInputHeap.count());
	}
	
	@Test
	public void addShouldAddNodeToHeap() {
		int index = 2;
		ClassInfo classInfo = new MockClassInfo();
		SymbolicInteger symInteger = new SymbolicInteger();
		
		HeapNode heapNode = new HeapNode(index, classInfo, symInteger);
		SymbolicInputHeap symInputHeap = new SymbolicInputHeap();
		symInputHeap._add(heapNode);
		
		assertTrue(symInputHeap.hasNode(heapNode));
		assertTrue(symInputHeap.header() == heapNode);
		assertTrue(symInputHeap.getNode(index) == symInteger);
		assertEquals(1, symInputHeap.count());
	}
	
	@Test
	public void addInvokedWithOneHeapNodeTwiceShouldAddOne() {
		int index = 2;
		ClassInfo classInfo = new MockClassInfo();
		SymbolicInteger symInteger = new SymbolicInteger();
	
		HeapNode heapNode = new HeapNode(index, classInfo, symInteger);
		SymbolicInputHeap symInputHeap = new SymbolicInputHeap();
		symInputHeap._add(heapNode);
		symInputHeap._add(heapNode);
		
		assertEquals(1, symInputHeap.count());
	}
	
	@Test
	public void getNodesOfTypeShouldReturnAllAddedNodesOfType() {
		int index = 2;
		ClassInfo classInfo = new MockClassInfo();
		SymbolicInteger symInteger = new SymbolicInteger();
	
		HeapNode heapNode1 = new HeapNode(index, classInfo, symInteger);
		HeapNode heapNode2 = new HeapNode(index, classInfo, symInteger);
		SymbolicInputHeap symInputHeap = new SymbolicInputHeap();
		symInputHeap._add(heapNode1);
		symInputHeap._add(heapNode2);
		
		assertArrayEquals(new HeapNode[] { heapNode2, heapNode1 }, symInputHeap.getNodesOfType(classInfo));
	}
	
	@Test
	public void makeCopyShouldReturnCopySymbolicInputHeap() {
		int index = 2;
		ClassInfo classInfo = new MockClassInfo();
		SymbolicInteger symInteger = new SymbolicInteger();
	
		HeapNode heapNode = new HeapNode(index, classInfo, symInteger);
		SymbolicInputHeap symInputHeap1 = new SymbolicInputHeap();
		SymbolicInputHeap symInputHeap2 = symInputHeap1.make_copy();
		symInputHeap1._add(heapNode);
		
		assertTrue(symInputHeap1.header() == heapNode);
		assertEquals(1, symInputHeap1.count());
		assertTrue(symInputHeap2.header() == null);
		assertEquals(0, symInputHeap2.count());
	}
}
