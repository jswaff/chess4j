package com.jamesswafford.chess4j.io;

import junit.framework.Assert;

import org.junit.Test;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.exceptions.IllegalMoveException;
import com.jamesswafford.chess4j.exceptions.ParseException;
import com.jamesswafford.chess4j.pieces.King;
import com.jamesswafford.chess4j.pieces.Knight;
import com.jamesswafford.chess4j.pieces.Pawn;
import com.jamesswafford.chess4j.pieces.Queen;
import com.jamesswafford.chess4j.pieces.Rook;

public class MoveParserTest {

	Board board = Board.INSTANCE;
	MoveParser mp = new MoveParser();
	
	@Test
	public void moveParserTest1() throws ParseException, IllegalMoveException {
		board.resetBoard();
		Board b = board.deepCopy();
		Assert.assertEquals(board, b);
		Move mv = mp.parseMove("b1c3",board);
		/// should not have changed state of board
		Assert.assertEquals(board, b);
		board.applyMove(mv);
		FenParser.setPos(b, "rnbqkbnr/pppppppp/8/8/8/2N5/PPPPPPPP/R1BQKBNR b KQkq - 0 1");
		Assert.assertTrue(board.equalExceptMoveHistory(b, false));
	}
	
	@Test(expected=IllegalMoveException.class)
	public void moveParserTest2() throws ParseException, IllegalMoveException {
		board.resetBoard();
		mp.parseMove("O-O",board);
	}
	
	@Test
	public void moveParserTest3() throws ParseException, IllegalMoveException {
		FenParser.setPos(board, "5k2/8/8/8/8/8/8/4K2R w K - 0 1");
		Move m = mp.parseMove("O-O",board);
		Move m2 = new Move(King.WHITE_KING,Square.valueOf(File.FILE_E, Rank.RANK_1),Square.valueOf(File.FILE_G, Rank.RANK_1),true);
		Assert.assertEquals(m2, m);
	}

	@Test
	public void moveParserTest4() throws ParseException, IllegalMoveException {
		FenParser.setPos(board, "5k2/1P6/1K6/8/8/8/8/8 w - -");
		Move m = mp.parseMove("b7b8n",board);
		Move m2 = new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_B, Rank.RANK_7),Square.valueOf(File.FILE_B, Rank.RANK_8),null,Knight.WHITE_KNIGHT);
		Assert.assertEquals(m2, m);
	}

	@Test
	public void moveParserTest5() throws ParseException, IllegalMoveException {
		FenParser.setPos(board, "8/8/8/8/8/8/3pk3/1KR5 b - -");
		Move m = mp.parseMove("d2xc1q",board);
		Move m2 = new Move(Pawn.BLACK_PAWN,Square.valueOf(File.FILE_D, Rank.RANK_2),
				Square.valueOf(File.FILE_C, Rank.RANK_1),Rook.WHITE_ROOK,Queen.BLACK_QUEEN);
		Assert.assertEquals(m2, m);
	}

}
