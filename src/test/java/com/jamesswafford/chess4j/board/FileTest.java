package com.jamesswafford.chess4j.board;

import junit.framework.Assert;

import org.junit.Test;

import com.jamesswafford.chess4j.board.squares.File;

public class FileTest {

	@Test
	public void testValue() {
		Assert.assertEquals(0, File.FILE_A.getValue());
		Assert.assertEquals(1, File.FILE_B.getValue());
		Assert.assertEquals(2, File.FILE_C.getValue());
		Assert.assertEquals(3, File.FILE_D.getValue());
		Assert.assertEquals(4, File.FILE_E.getValue());
		Assert.assertEquals(5, File.FILE_F.getValue());
		Assert.assertEquals(6, File.FILE_G.getValue());
		Assert.assertEquals(7, File.FILE_H.getValue());

		Assert.assertEquals(File.FILE_A, File.file(0));
		Assert.assertEquals(File.FILE_B, File.file(1));
		Assert.assertEquals(File.FILE_C, File.file(2));
		Assert.assertEquals(File.FILE_D, File.file(3));
		Assert.assertEquals(File.FILE_E, File.file(4));
		Assert.assertEquals(File.FILE_F, File.file(5));
		Assert.assertEquals(File.FILE_G, File.file(6));
		Assert.assertEquals(File.FILE_H, File.file(7));
	}
	
	@Test
	public void testLabel() {
		Assert.assertEquals("a", File.FILE_A.getLabel());
		Assert.assertEquals("b", File.FILE_B.getLabel());
		Assert.assertEquals("c", File.FILE_C.getLabel());
		Assert.assertEquals("d", File.FILE_D.getLabel());
		Assert.assertEquals("e", File.FILE_E.getLabel());
		Assert.assertEquals("f", File.FILE_F.getLabel());
		Assert.assertEquals("g", File.FILE_G.getLabel());
		Assert.assertEquals("h", File.FILE_H.getLabel());
		
		Assert.assertEquals(File.FILE_A, File.file("a"));
		Assert.assertEquals(File.FILE_B, File.file("B"));
		Assert.assertEquals(File.FILE_C, File.file("c"));
		Assert.assertEquals(File.FILE_D, File.file("D"));
		Assert.assertEquals(File.FILE_E, File.file("e"));
		Assert.assertEquals(File.FILE_F, File.file("F"));
		Assert.assertEquals(File.FILE_G, File.file("G"));
		Assert.assertEquals(File.FILE_H, File.file("h"));
	}
	
	@Test
	public void testWest() {
		Assert.assertNull(File.FILE_A.west());
		Assert.assertEquals(File.FILE_A, File.FILE_B.west());
		Assert.assertEquals(File.FILE_B, File.FILE_C.west());
		Assert.assertEquals(File.FILE_C, File.FILE_D.west());
		Assert.assertEquals(File.FILE_D, File.FILE_E.west());
		Assert.assertEquals(File.FILE_E, File.FILE_F.west());
		Assert.assertEquals(File.FILE_F, File.FILE_G.west());
		Assert.assertEquals(File.FILE_G, File.FILE_H.west());
	}
	
	@Test
	public void testEast() {
		Assert.assertEquals(File.FILE_B, File.FILE_A.east());
		Assert.assertEquals(File.FILE_C, File.FILE_B.east());
		Assert.assertEquals(File.FILE_D, File.FILE_C.east());
		Assert.assertEquals(File.FILE_E, File.FILE_D.east());
		Assert.assertEquals(File.FILE_F, File.FILE_E.east());
		Assert.assertEquals(File.FILE_G, File.FILE_F.east());
		Assert.assertEquals(File.FILE_H, File.FILE_G.east());
		Assert.assertNull(File.FILE_H.east());
	}

	@Test
	public void testWestOf() {
		Assert.assertTrue(File.FILE_A.westOf(File.FILE_B));
		Assert.assertTrue(File.FILE_C.westOf(File.FILE_G));
		Assert.assertFalse(File.FILE_D.westOf(File.FILE_D));
		Assert.assertFalse(File.FILE_E.westOf(File.FILE_B));
	}
	
	@Test
	public void testEastOf() {
		Assert.assertTrue(File.FILE_B.eastOf(File.FILE_A));
		Assert.assertTrue(File.FILE_G.eastOf(File.FILE_C));
		Assert.assertFalse(File.FILE_D.eastOf(File.FILE_D));
		Assert.assertFalse(File.FILE_B.eastOf(File.FILE_E));
	}

	@Test
	public void testDistance() {
		Assert.assertEquals(0, File.FILE_A.distance(File.FILE_A));
		Assert.assertEquals(7, File.FILE_A.distance(File.FILE_H));
		Assert.assertEquals(7, File.FILE_H.distance(File.FILE_A));
		Assert.assertEquals(2, File.FILE_C.distance(File.FILE_E));
	}
	
	@Test
	public void testFlip() {
		Assert.assertEquals(File.FILE_A, File.FILE_H.flip());
		Assert.assertEquals(File.FILE_B, File.FILE_G.flip());
		Assert.assertEquals(File.FILE_C, File.FILE_F.flip());
		Assert.assertEquals(File.FILE_D, File.FILE_E.flip());
		Assert.assertEquals(File.FILE_E, File.FILE_D.flip());
		Assert.assertEquals(File.FILE_F, File.FILE_C.flip());
		Assert.assertEquals(File.FILE_G, File.FILE_B.flip());
		Assert.assertEquals(File.FILE_H, File.FILE_A.flip());
	}
}
