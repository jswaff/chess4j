package com.jamesswafford.chess4j.board;

import junit.framework.Assert;

import org.junit.Test;

import com.jamesswafford.chess4j.board.CastlingRights;

public class CastlingRightsTest {

	@Test
	public void testLabel() {
		Assert.assertEquals("K", CastlingRights.WHITE_KINGSIDE.getLabel());
		Assert.assertEquals("Q", CastlingRights.WHITE_QUEENSIDE.getLabel());
		Assert.assertEquals("k", CastlingRights.BLACK_KINGSIDE.getLabel());
		Assert.assertEquals("q", CastlingRights.BLACK_QUEENSIDE.getLabel());
	}
}
