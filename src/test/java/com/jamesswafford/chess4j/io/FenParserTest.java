package com.jamesswafford.chess4j.io;

import junit.framework.Assert;

import org.junit.Test;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.CastlingRights;
import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.pieces.Knight;
import com.jamesswafford.chess4j.pieces.Pawn;

public class FenParserTest {

	Board board = Board.INSTANCE;
	
	@Test
	public void test1() throws Exception {
		String fen = "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2";
		FenParser.setPos(board, fen);
		
		Assert.assertEquals(Knight.WHITE_KNIGHT, board.getPiece(Square.valueOf(File.FILE_F, Rank.RANK_3)));
		Assert.assertFalse(board.canCastle(CastlingRights.BLACK_KINGSIDE));
		Assert.assertFalse(board.canCastle(CastlingRights.BLACK_QUEENSIDE));
		Assert.assertFalse(board.canCastle(CastlingRights.WHITE_KINGSIDE));
		Assert.assertFalse(board.canCastle(CastlingRights.WHITE_QUEENSIDE));
		Assert.assertEquals(3,board.getMoveCounter());
		Assert.assertEquals(1, board.getFiftyCounter());
		Assert.assertEquals(null, board.getEPSquare());
	}
	
	@Test
	public void test2() throws Exception {
		String fen = "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq -";
		FenParser.setPos(board, fen);
		
		Assert.assertEquals(Knight.WHITE_KNIGHT, board.getPiece(Square.valueOf(File.FILE_F, Rank.RANK_3)));
		Assert.assertFalse(board.canCastle(CastlingRights.BLACK_KINGSIDE));
		Assert.assertFalse(board.canCastle(CastlingRights.BLACK_QUEENSIDE));
		Assert.assertFalse(board.canCastle(CastlingRights.WHITE_KINGSIDE));
		Assert.assertFalse(board.canCastle(CastlingRights.WHITE_QUEENSIDE));
		Assert.assertEquals(1,board.getMoveCounter());
		Assert.assertEquals(0, board.getFiftyCounter());
		Assert.assertEquals(null, board.getEPSquare());
	}

	@Test
	public void test3() throws Exception {
		String fen = "rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2";
		FenParser.setPos(board, fen);
		
		Assert.assertEquals(Pawn.BLACK_PAWN, board.getPiece(Square.valueOf(File.FILE_C, Rank.RANK_5)));
		Assert.assertFalse(board.canCastle(CastlingRights.BLACK_KINGSIDE));
		Assert.assertFalse(board.canCastle(CastlingRights.BLACK_QUEENSIDE));
		Assert.assertFalse(board.canCastle(CastlingRights.WHITE_KINGSIDE));
		Assert.assertFalse(board.canCastle(CastlingRights.WHITE_QUEENSIDE));
		Assert.assertEquals(2,board.getMoveCounter());
		Assert.assertEquals(0, board.getFiftyCounter());
		Assert.assertEquals(Square.valueOf(File.FILE_C, Rank.RANK_6), board.getEPSquare());
	}

}
