package com.alexereh.hilbert;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HPRTreeTests {
	@Test
	public void disallowedInsert(){
		HPRTree<Integer> t = new HPRTree<>(3);
		t.insert(new MBR(0, 0, 0, 0), 1);
		t.insert(new MBR(0, 0, 0, 0), 3);
		t.query(new MBR());
		try {
			t.insert(new MBR(0, 0, 0, 0), 2);
			fail();
		}
		catch (IllegalStateException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testQuery3() {
		HPRTree<Integer> t = new HPRTree<>();
		for (int i = 0; i < 3; i++ ) {
			t.insert(new MBR(i, i+1, i, i+1), i);
		}
		t.query(new MBR(0,1,0,1));
		assertEquals(3, t.query(new MBR(1, 2, 1, 2)).size());
		assertEquals(0, t.query(new MBR(9, 10, 9, 10)).size());
	}

	@Test
	public void testQuery10() {
		HPRTree<Integer> t = new HPRTree<>();
		for (int i = 0; i < 10; i++ ) {
			t.insert(new MBR(i, i+1, i, i+1), i);
		}
		t.query(new MBR(0,1,0,1));
		assertEquals(3, t.query(new MBR(5, 6, 5, 6)).size());
		assertEquals(2, t.query(new MBR(9, 10, 9, 10)).size());
		assertEquals(0, t.query(new MBR(25, 26, 25, 26)).size());
		assertEquals(10, t.query(new MBR(0, 10, 0, 10)).size());
	}
}
