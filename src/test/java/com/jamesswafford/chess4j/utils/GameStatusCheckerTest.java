package com.jamesswafford.chess4j.utils;

import junit.framework.Assert;

import org.junit.Test;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.io.FenParser;
import com.jamesswafford.chess4j.pieces.Knight;
import com.jamesswafford.chess4j.pieces.Pawn;
import com.jamesswafford.chess4j.utils.GameStatus;
import com.jamesswafford.chess4j.utils.GameStatusChecker;


public class GameStatusCheckerTest {

	@Test
	public void testInitialPosInProgress() throws Exception {
		Board b = Board.INSTANCE;
		b.resetBoard();
		GameStatus gs = GameStatusChecker.getGameStatus();
		Assert.assertEquals(GameStatus.INPROGRESS, gs);
	}
	
	@Test
	public void testGetCheckmateStatus_FoolsMate() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "rnb1kbnr/pppp1ppp/8/4p3/6Pq/5P2/PPPPP2P/RNBQKBNR w KQkq -");
		GameStatus gs = GameStatusChecker.getGameStatus();
		Assert.assertEquals(GameStatus.CHECKMATED, gs);
	}
	
	@Test
	public void testGetCheckmateStatus_ByrneFischer() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "1Q6/5pk1/2p3p1/1p2N2p/1b5P/1bn5/2r3P1/2K5 w - -");
		GameStatus gs = GameStatusChecker.getGameStatus();
		Assert.assertEquals(GameStatus.CHECKMATED, gs);
	}

	@Test
	public void testGetCheckmateStatus_SimpleMate() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "3k2R1/8/3K4/8/8/8/8/8 b - -");
		GameStatus gs = GameStatusChecker.getGameStatus();
		Assert.assertEquals(GameStatus.CHECKMATED, gs);
	}

	@Test
	public void testGetStalemateStatus() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "8/8/8/6K1/8/1Q6/p7/k7 b - -");
		GameStatus gs = GameStatusChecker.getGameStatus();
		Assert.assertEquals(GameStatus.STALEMATED, gs);
	}

	@Test
	public void testGetStalemateStatus_BurnPilsbury() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "5k2/5P2/5K2/8/8/8/8/8 b - -");
		GameStatus gs = GameStatusChecker.getGameStatus();
		Assert.assertEquals(GameStatus.STALEMATED, gs);
	}


	@Test
	public void testGetStalemateStatus2() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "kb5R/8/1K6/8/8/8/8/8 b - - ");
		GameStatus gs = GameStatusChecker.getGameStatus();
		Assert.assertEquals(GameStatus.STALEMATED, gs);
	}

	@Test
	public void testGetStalemateStatus3() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "8/8/8/8/8/2K5/1R6/k7 b - -");
		GameStatus gs = GameStatusChecker.getGameStatus();
		Assert.assertEquals(GameStatus.STALEMATED, gs);
	}

	@Test
	public void testGetDraw50Status() throws Exception {
		Board b = Board.INSTANCE;
		b.resetBoard();
		GameStatus gs = GameStatusChecker.getGameStatus();
		Assert.assertEquals(GameStatus.INPROGRESS, gs);
		
		b.setFiftyCounter(49);
		gs = GameStatusChecker.getGameStatus();
		Assert.assertEquals(GameStatus.INPROGRESS, gs);

		b.setFiftyCounter(50);
		gs = GameStatusChecker.getGameStatus();
		Assert.assertEquals(GameStatus.INPROGRESS, gs);

		b.setFiftyCounter(51);
		gs = GameStatusChecker.getGameStatus();
		Assert.assertEquals(GameStatus.INPROGRESS, gs);

		b.setFiftyCounter(100);
		gs = GameStatusChecker.getGameStatus();
		Assert.assertEquals(GameStatus.DRAW_BY_50, gs);
	}

	@Test
	public void testNoMaterialStatus() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "kb6/8/1K6/8/8/8/8/8 b - - ");
		GameStatus gs = GameStatusChecker.getGameStatus();
		Assert.assertEquals(GameStatus.DRAW_MATERIAL, gs);
	}

	@Test
	public void testNoMaterialStatusOnePawn() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "4k3/8/8/8/8/8/P7/4K3 w - -");
		GameStatus gs = GameStatusChecker.getGameStatus();
		Assert.assertNotSame(GameStatus.DRAW_MATERIAL, gs);
	}

	@Test
	public void testNoMaterialStatusOneKnight() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "4k3/8/8/8/8/8/n7/4K3 w - -");
		GameStatus gs = GameStatusChecker.getGameStatus();
		Assert.assertEquals(GameStatus.DRAW_MATERIAL, gs);
	}

	@Test
	public void testNoMaterialStatusOneBishop() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "4k3/8/8/8/8/8/B7/4K3 w - -");
		GameStatus gs = GameStatusChecker.getGameStatus();
		Assert.assertEquals(GameStatus.DRAW_MATERIAL, gs);
	}

	@Test
	public void testNoMaterialStatusOneRook() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "4k3/8/8/8/8/8/r7/4K3 b - -");
		GameStatus gs = GameStatusChecker.getGameStatus();
		Assert.assertNotSame(GameStatus.DRAW_MATERIAL, gs);
	}

	@Test
	public void testNoMaterialStatusOneQueen() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "4k3/8/8/8/8/8/Q7/4K3 w - -");
		GameStatus gs = GameStatusChecker.getGameStatus();
		Assert.assertNotSame(GameStatus.DRAW_MATERIAL, gs);
	}

	@Test
	public void testNoMaterialStatusJustKings() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "4k3/8/8/8/8/8/8/4K3 w - -");
		GameStatus gs = GameStatusChecker.getGameStatus();
		Assert.assertEquals(GameStatus.DRAW_MATERIAL, gs);
	}

	@Test
	public void testNoMaterialStatusTwoWhiteKnights() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "4k3/8/8/8/8/8/NN6/4K3 w - -");
		GameStatus gs = GameStatusChecker.getGameStatus();
		Assert.assertNotSame(GameStatus.DRAW_MATERIAL, gs);
	}

	@Test
	public void testNoMaterialStatusTwoOpposingKnights() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "4k3/8/8/8/8/8/Nn6/4K3 b - -");
		GameStatus gs = GameStatusChecker.getGameStatus();
		Assert.assertNotSame(GameStatus.DRAW_MATERIAL, gs);
	}

	@Test
	public void testNoMaterialStatusTwoBishopsDifferentColors() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "4k3/8/8/8/8/8/Bb6/4K3 w - -");
		GameStatus gs = GameStatusChecker.getGameStatus();
		Assert.assertNotSame(GameStatus.DRAW_MATERIAL, gs);
	}

	@Test
	public void testNoMaterialStatusTwoBishopsSameColor() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "4k3/8/8/8/8/8/B1b5/4K3 b - -");
		GameStatus gs = GameStatusChecker.getGameStatus();
		Assert.assertEquals(GameStatus.DRAW_MATERIAL, gs);
	}

	@Test
	public void testNoMaterialStatusBishopVsKnight() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "4k3/8/8/8/8/8/B1n5/5K2 b - -");
		GameStatus gs = GameStatusChecker.getGameStatus();
		Assert.assertNotSame(GameStatus.DRAW_MATERIAL, gs);
	}
	
	@Test
	public void testDrawByRep() throws Exception {
		Board b = Board.INSTANCE;
		b.resetBoard();
		GameStatus gs = GameStatusChecker.getGameStatus();
		Assert.assertNotSame(GameStatus.DRAW_REP, gs);
		
		b.applyMove(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_2),Square.valueOf(File.FILE_E, Rank.RANK_4)));
		Assert.assertNotSame(GameStatus.DRAW_REP, GameStatusChecker.getGameStatus());

		b.applyMove(new Move(Knight.BLACK_KNIGHT,Square.valueOf(File.FILE_G, Rank.RANK_8),Square.valueOf(File.FILE_F, Rank.RANK_6)));
		Assert.assertNotSame(GameStatus.DRAW_REP, GameStatusChecker.getGameStatus());

		b.applyMove(new Move(Knight.WHITE_KNIGHT,Square.valueOf(File.FILE_G, Rank.RANK_1),Square.valueOf(File.FILE_F, Rank.RANK_3)));
		Assert.assertNotSame(GameStatus.DRAW_REP, GameStatusChecker.getGameStatus());

		b.applyMove(new Move(Knight.BLACK_KNIGHT,Square.valueOf(File.FILE_F, Rank.RANK_6),Square.valueOf(File.FILE_G, Rank.RANK_8)));
		Assert.assertNotSame(GameStatus.DRAW_REP, GameStatusChecker.getGameStatus());

		b.applyMove(new Move(Knight.WHITE_KNIGHT,Square.valueOf(File.FILE_F, Rank.RANK_3),Square.valueOf(File.FILE_G, Rank.RANK_1)));
		Assert.assertNotSame(GameStatus.DRAW_REP, GameStatusChecker.getGameStatus()); // still 1 (first has ep square)

		b.applyMove(new Move(Knight.BLACK_KNIGHT,Square.valueOf(File.FILE_G, Rank.RANK_8),Square.valueOf(File.FILE_F, Rank.RANK_6)));
		Assert.assertNotSame(GameStatus.DRAW_REP, GameStatusChecker.getGameStatus()); // 2

		b.applyMove(new Move(Knight.WHITE_KNIGHT,Square.valueOf(File.FILE_G, Rank.RANK_1),Square.valueOf(File.FILE_F, Rank.RANK_3)));
		Assert.assertNotSame(GameStatus.DRAW_REP, GameStatusChecker.getGameStatus()); // 2

		b.applyMove(new Move(Knight.BLACK_KNIGHT,Square.valueOf(File.FILE_F, Rank.RANK_6),Square.valueOf(File.FILE_G, Rank.RANK_8)));
		Assert.assertNotSame(GameStatus.DRAW_REP, GameStatusChecker.getGameStatus()); // 2

		b.applyMove(new Move(Knight.WHITE_KNIGHT,Square.valueOf(File.FILE_F, Rank.RANK_3),Square.valueOf(File.FILE_G, Rank.RANK_1)));
		Assert.assertNotSame(GameStatus.DRAW_REP, GameStatusChecker.getGameStatus()); // 2

		b.applyMove(new Move(Knight.BLACK_KNIGHT,Square.valueOf(File.FILE_G, Rank.RANK_8),Square.valueOf(File.FILE_F, Rank.RANK_6)));
		Assert.assertEquals(GameStatus.DRAW_REP, GameStatusChecker.getGameStatus()); // 3

		b.applyMove(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_D, Rank.RANK_2),Square.valueOf(File.FILE_D, Rank.RANK_4)));
		Assert.assertNotSame(GameStatus.DRAW_REP, GameStatusChecker.getGameStatus()); 
	}
}
