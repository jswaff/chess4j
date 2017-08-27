package com.jamesswafford.chess4j.board;

import junit.framework.Assert;

import org.junit.Test;

import com.jamesswafford.chess4j.io.FenParser;

public class ZugzwangDetectorTest {

	@Test
	public void testInitialPos() {
		Board b = Board.INSTANCE;
		b.resetBoard();
		
		Assert.assertFalse(ZugzwangDetector.isZugzwang(b));
	}
	
	@Test
	public void testJustKings() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "7k/8/8/8/8/8/8/7K w - - ");
		
		Assert.assertTrue(ZugzwangDetector.isZugzwang(b));
	}
	
	@Test
	public void testSingleWhiteBishop() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "7k/8/8/B7/8/8/8/7K b - - ");
		
		Assert.assertTrue(ZugzwangDetector.isZugzwang(b));
	}
	
	@Test
	public void testWhitePawnBlackKnight() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "7k/8/8/n7/P7/8/8/7K b - - ");
		
		Assert.assertTrue(ZugzwangDetector.isZugzwang(b));
	}

	@Test
	public void testWhiteRookBlackKnight() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "7k/8/8/n7/R7/8/8/7K b - - ");
		
		Assert.assertFalse(ZugzwangDetector.isZugzwang(b));
	}

}
