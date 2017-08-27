package com.jamesswafford.chess4j.board;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.MoveGen;
import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.io.EPDParser;
import com.jamesswafford.chess4j.io.FenParser;
import com.jamesswafford.chess4j.io.MoveParser;
import com.jamesswafford.chess4j.pieces.Bishop;
import com.jamesswafford.chess4j.pieces.King;
import com.jamesswafford.chess4j.pieces.Knight;
import com.jamesswafford.chess4j.pieces.Pawn;
import com.jamesswafford.chess4j.pieces.Queen;
import com.jamesswafford.chess4j.pieces.Rook;


public class MoveGenTest {

	@Test
	public void testKnightMoves() throws Exception {
		Board b = Board.INSTANCE;
		b.resetBoard();
		
		List<Move> moves = new ArrayList<Move>();
		MoveGen.genKnightMoves(b,moves,true,true);
		
		Assert.assertEquals(4, moves.size());
		Assert.assertTrue(moves.contains(new Move(Knight.WHITE_KNIGHT,Square.valueOf(File.FILE_B, Rank.RANK_1),Square.valueOf(File.FILE_A, Rank.RANK_3))));
		Assert.assertTrue(moves.contains(new Move(Knight.WHITE_KNIGHT,Square.valueOf(File.FILE_B, Rank.RANK_1),Square.valueOf(File.FILE_C, Rank.RANK_3))));
		Assert.assertTrue(moves.contains(new Move(Knight.WHITE_KNIGHT,Square.valueOf(File.FILE_G, Rank.RANK_1),Square.valueOf(File.FILE_F, Rank.RANK_3))));
		Assert.assertTrue(moves.contains(new Move(Knight.WHITE_KNIGHT,Square.valueOf(File.FILE_G, Rank.RANK_1),Square.valueOf(File.FILE_H, Rank.RANK_3))));
	}
	
	@Test
	public void testKnightCaptures() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "4k3/8/3P1p2/8/4N3/8/8/4K3 w - - 0 1");
		
		List<Move> moves = new ArrayList<Move>();
		MoveGen.genKnightMoves(b,moves,true,false);
		
		Assert.assertEquals(1, moves.size());
		Assert.assertTrue(moves.contains(new Move(Knight.WHITE_KNIGHT,Square.valueOf(File.FILE_E, Rank.RANK_4),Square.valueOf(File.FILE_F, Rank.RANK_6),Pawn.BLACK_PAWN)));
	}
	
	@Test
	public void testKnightNoncaptures() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "4k3/8/3P1p2/8/4N3/8/8/4K3 w - - 0 1");
		
		List<Move> moves = new ArrayList<Move>();
		MoveGen.genKnightMoves(b,moves,false,true);
		
		Assert.assertEquals(6, moves.size());
		Assert.assertTrue(moves.contains(new Move(Knight.WHITE_KNIGHT,Square.valueOf(File.FILE_E, Rank.RANK_4),Square.valueOf(File.FILE_G, Rank.RANK_5))));
		Assert.assertTrue(moves.contains(new Move(Knight.WHITE_KNIGHT,Square.valueOf(File.FILE_E, Rank.RANK_4),Square.valueOf(File.FILE_G, Rank.RANK_3))));
		Assert.assertTrue(moves.contains(new Move(Knight.WHITE_KNIGHT,Square.valueOf(File.FILE_E, Rank.RANK_4),Square.valueOf(File.FILE_F, Rank.RANK_2))));
		Assert.assertTrue(moves.contains(new Move(Knight.WHITE_KNIGHT,Square.valueOf(File.FILE_E, Rank.RANK_4),Square.valueOf(File.FILE_D, Rank.RANK_2))));
		Assert.assertTrue(moves.contains(new Move(Knight.WHITE_KNIGHT,Square.valueOf(File.FILE_E, Rank.RANK_4),Square.valueOf(File.FILE_C, Rank.RANK_3))));
		Assert.assertTrue(moves.contains(new Move(Knight.WHITE_KNIGHT,Square.valueOf(File.FILE_E, Rank.RANK_4),Square.valueOf(File.FILE_C, Rank.RANK_5))));
	}

	@Test
	public void testBishopMoves() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "4k3/2p3P1/8/4B3/4B3/3p1P2/8/4K3 w - - 0 1");
		List<Move> moves = new ArrayList<Move>();
		MoveGen.genBishopMoves(b, moves, true, true);
		Assert.assertEquals(18, moves.size());
		Assert.assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP,Square.valueOf(File.FILE_E, Rank.RANK_5),Square.valueOf(File.FILE_F, Rank.RANK_6))));
		Assert.assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP,Square.valueOf(File.FILE_E, Rank.RANK_5),Square.valueOf(File.FILE_F, Rank.RANK_4))));
		Assert.assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP,Square.valueOf(File.FILE_E, Rank.RANK_5),Square.valueOf(File.FILE_G, Rank.RANK_3))));
		Assert.assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP,Square.valueOf(File.FILE_E, Rank.RANK_5),Square.valueOf(File.FILE_H, Rank.RANK_2))));
		Assert.assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP,Square.valueOf(File.FILE_E, Rank.RANK_5),Square.valueOf(File.FILE_D, Rank.RANK_4))));
		Assert.assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP,Square.valueOf(File.FILE_E, Rank.RANK_5),Square.valueOf(File.FILE_C, Rank.RANK_3))));
		Assert.assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP,Square.valueOf(File.FILE_E, Rank.RANK_5),Square.valueOf(File.FILE_B, Rank.RANK_2))));
		Assert.assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP,Square.valueOf(File.FILE_E, Rank.RANK_5),Square.valueOf(File.FILE_A, Rank.RANK_1))));
		Assert.assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP,Square.valueOf(File.FILE_E, Rank.RANK_5),Square.valueOf(File.FILE_D, Rank.RANK_6))));
		Assert.assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP,Square.valueOf(File.FILE_E, Rank.RANK_5),Square.valueOf(File.FILE_C, Rank.RANK_7),Pawn.BLACK_PAWN)));
		Assert.assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP,Square.valueOf(File.FILE_E, Rank.RANK_4),Square.valueOf(File.FILE_F, Rank.RANK_5))));
		Assert.assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP,Square.valueOf(File.FILE_E, Rank.RANK_4),Square.valueOf(File.FILE_G, Rank.RANK_6))));
		Assert.assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP,Square.valueOf(File.FILE_E, Rank.RANK_4),Square.valueOf(File.FILE_H, Rank.RANK_7))));
		Assert.assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP,Square.valueOf(File.FILE_E, Rank.RANK_4),Square.valueOf(File.FILE_D, Rank.RANK_3),Pawn.BLACK_PAWN)));
		Assert.assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP,Square.valueOf(File.FILE_E, Rank.RANK_4),Square.valueOf(File.FILE_D, Rank.RANK_5))));
		Assert.assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP,Square.valueOf(File.FILE_E, Rank.RANK_4),Square.valueOf(File.FILE_C, Rank.RANK_6))));
		Assert.assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP,Square.valueOf(File.FILE_E, Rank.RANK_4),Square.valueOf(File.FILE_B, Rank.RANK_7))));
		Assert.assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP,Square.valueOf(File.FILE_E, Rank.RANK_4),Square.valueOf(File.FILE_A, Rank.RANK_8))));
	}

	@Test
	public void testBishopCaptures() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "4k3/2p3P1/8/4B3/4B3/3p1P2/8/4K3 w - - 0 1");
		List<Move> moves = new ArrayList<Move>();
		MoveGen.genBishopMoves(b, moves, true, false);
		Assert.assertEquals(2, moves.size());
		Assert.assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP,Square.valueOf(File.FILE_E, Rank.RANK_5),Square.valueOf(File.FILE_C, Rank.RANK_7),Pawn.BLACK_PAWN)));
		Assert.assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP,Square.valueOf(File.FILE_E, Rank.RANK_4),Square.valueOf(File.FILE_D, Rank.RANK_3),Pawn.BLACK_PAWN)));
	}

	@Test
	public void testBishopNoncaptures() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "4k3/2p3P1/8/4B3/4B3/3p1P2/8/4K3 w - - 0 1");
		List<Move> moves = new ArrayList<Move>();
		MoveGen.genBishopMoves(b, moves, false, true);
		Assert.assertEquals(16, moves.size());
		Assert.assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP,Square.valueOf(File.FILE_E, Rank.RANK_5),Square.valueOf(File.FILE_F, Rank.RANK_6))));
		Assert.assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP,Square.valueOf(File.FILE_E, Rank.RANK_5),Square.valueOf(File.FILE_F, Rank.RANK_4))));
		Assert.assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP,Square.valueOf(File.FILE_E, Rank.RANK_5),Square.valueOf(File.FILE_G, Rank.RANK_3))));
		Assert.assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP,Square.valueOf(File.FILE_E, Rank.RANK_5),Square.valueOf(File.FILE_H, Rank.RANK_2))));
		Assert.assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP,Square.valueOf(File.FILE_E, Rank.RANK_5),Square.valueOf(File.FILE_D, Rank.RANK_4))));
		Assert.assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP,Square.valueOf(File.FILE_E, Rank.RANK_5),Square.valueOf(File.FILE_C, Rank.RANK_3))));
		Assert.assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP,Square.valueOf(File.FILE_E, Rank.RANK_5),Square.valueOf(File.FILE_B, Rank.RANK_2))));
		Assert.assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP,Square.valueOf(File.FILE_E, Rank.RANK_5),Square.valueOf(File.FILE_A, Rank.RANK_1))));
		Assert.assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP,Square.valueOf(File.FILE_E, Rank.RANK_5),Square.valueOf(File.FILE_D, Rank.RANK_6))));
		Assert.assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP,Square.valueOf(File.FILE_E, Rank.RANK_4),Square.valueOf(File.FILE_F, Rank.RANK_5))));
		Assert.assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP,Square.valueOf(File.FILE_E, Rank.RANK_4),Square.valueOf(File.FILE_G, Rank.RANK_6))));
		Assert.assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP,Square.valueOf(File.FILE_E, Rank.RANK_4),Square.valueOf(File.FILE_H, Rank.RANK_7))));
		Assert.assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP,Square.valueOf(File.FILE_E, Rank.RANK_4),Square.valueOf(File.FILE_D, Rank.RANK_5))));
		Assert.assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP,Square.valueOf(File.FILE_E, Rank.RANK_4),Square.valueOf(File.FILE_C, Rank.RANK_6))));
		Assert.assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP,Square.valueOf(File.FILE_E, Rank.RANK_4),Square.valueOf(File.FILE_B, Rank.RANK_7))));
		Assert.assertTrue(moves.contains(new Move(Bishop.WHITE_BISHOP,Square.valueOf(File.FILE_E, Rank.RANK_4),Square.valueOf(File.FILE_A, Rank.RANK_8))));
	}

	@Test
	public void testRookMoves() throws Exception {
		Board b = Board.INSTANCE;
		b.resetBoard();
		
		List<Move> moves = new ArrayList<Move>();
		MoveGen.genRookMoves(b, moves, true, true);
		Assert.assertEquals(0, moves.size());
		
		FenParser.setPos(b, "8/8/3k1p2/8/3K4/8/1R3r2/8 b - - 0 1");
		MoveGen.genRookMoves(b, moves, true, true);
	
		Assert.assertEquals(10, moves.size());
		Assert.assertTrue(moves.contains(new Move(Rook.BLACK_ROOK,Square.valueOf(File.FILE_F, Rank.RANK_2),Square.valueOf(File.FILE_F, Rank.RANK_3))));
		Assert.assertTrue(moves.contains(new Move(Rook.BLACK_ROOK,Square.valueOf(File.FILE_F, Rank.RANK_2),Square.valueOf(File.FILE_F, Rank.RANK_4))));
		Assert.assertTrue(moves.contains(new Move(Rook.BLACK_ROOK,Square.valueOf(File.FILE_F, Rank.RANK_2),Square.valueOf(File.FILE_F, Rank.RANK_5))));
		Assert.assertTrue(moves.contains(new Move(Rook.BLACK_ROOK,Square.valueOf(File.FILE_F, Rank.RANK_2),Square.valueOf(File.FILE_F, Rank.RANK_1))));
		Assert.assertTrue(moves.contains(new Move(Rook.BLACK_ROOK,Square.valueOf(File.FILE_F, Rank.RANK_2),Square.valueOf(File.FILE_G, Rank.RANK_2))));
		Assert.assertTrue(moves.contains(new Move(Rook.BLACK_ROOK,Square.valueOf(File.FILE_F, Rank.RANK_2),Square.valueOf(File.FILE_H, Rank.RANK_2))));
		Assert.assertTrue(moves.contains(new Move(Rook.BLACK_ROOK,Square.valueOf(File.FILE_F, Rank.RANK_2),Square.valueOf(File.FILE_E, Rank.RANK_2))));
		Assert.assertTrue(moves.contains(new Move(Rook.BLACK_ROOK,Square.valueOf(File.FILE_F, Rank.RANK_2),Square.valueOf(File.FILE_D, Rank.RANK_2))));
		Assert.assertTrue(moves.contains(new Move(Rook.BLACK_ROOK,Square.valueOf(File.FILE_F, Rank.RANK_2),Square.valueOf(File.FILE_C, Rank.RANK_2))));
		Assert.assertTrue(moves.contains(new Move(Rook.BLACK_ROOK,Square.valueOf(File.FILE_F, Rank.RANK_2),Square.valueOf(File.FILE_B, Rank.RANK_2),Rook.WHITE_ROOK)));
	}
	
	@Test
	public void testRookCaptures() throws Exception {
		Board b = Board.INSTANCE;
		
		List<Move> moves = new ArrayList<Move>();
		FenParser.setPos(b, "8/8/3k1p2/8/3K4/8/1R3r2/8 b - - 0 1");
		MoveGen.genRookMoves(b, moves, true, false);
	
		Assert.assertEquals(1, moves.size());
		Assert.assertTrue(moves.contains(new Move(Rook.BLACK_ROOK,Square.valueOf(File.FILE_F, Rank.RANK_2),Square.valueOf(File.FILE_B, Rank.RANK_2),Rook.WHITE_ROOK)));
	}

	@Test
	public void testRookNoncaptures() throws Exception {
		Board b = Board.INSTANCE;

		List<Move> moves = new ArrayList<Move>();
		FenParser.setPos(b, "8/8/3k1p2/8/3K4/8/1R3r2/8 b - - 0 1");
		MoveGen.genRookMoves(b, moves, false, true);
	
		Assert.assertEquals(9, moves.size());
		Assert.assertTrue(moves.contains(new Move(Rook.BLACK_ROOK,Square.valueOf(File.FILE_F, Rank.RANK_2),Square.valueOf(File.FILE_F, Rank.RANK_3))));
		Assert.assertTrue(moves.contains(new Move(Rook.BLACK_ROOK,Square.valueOf(File.FILE_F, Rank.RANK_2),Square.valueOf(File.FILE_F, Rank.RANK_4))));
		Assert.assertTrue(moves.contains(new Move(Rook.BLACK_ROOK,Square.valueOf(File.FILE_F, Rank.RANK_2),Square.valueOf(File.FILE_F, Rank.RANK_5))));
		Assert.assertTrue(moves.contains(new Move(Rook.BLACK_ROOK,Square.valueOf(File.FILE_F, Rank.RANK_2),Square.valueOf(File.FILE_F, Rank.RANK_1))));
		Assert.assertTrue(moves.contains(new Move(Rook.BLACK_ROOK,Square.valueOf(File.FILE_F, Rank.RANK_2),Square.valueOf(File.FILE_G, Rank.RANK_2))));
		Assert.assertTrue(moves.contains(new Move(Rook.BLACK_ROOK,Square.valueOf(File.FILE_F, Rank.RANK_2),Square.valueOf(File.FILE_H, Rank.RANK_2))));
		Assert.assertTrue(moves.contains(new Move(Rook.BLACK_ROOK,Square.valueOf(File.FILE_F, Rank.RANK_2),Square.valueOf(File.FILE_E, Rank.RANK_2))));
		Assert.assertTrue(moves.contains(new Move(Rook.BLACK_ROOK,Square.valueOf(File.FILE_F, Rank.RANK_2),Square.valueOf(File.FILE_D, Rank.RANK_2))));
		Assert.assertTrue(moves.contains(new Move(Rook.BLACK_ROOK,Square.valueOf(File.FILE_F, Rank.RANK_2),Square.valueOf(File.FILE_C, Rank.RANK_2))));
	}

	@Test
	public void testQueenMoves() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "8/8/3bk3/8/8/2K3Q1/8/8 w - - 0 1");
		
		List<Move> moves = new ArrayList<Move>();
		MoveGen.genQueenMoves(b, moves, true, true);
		Assert.assertEquals(18, moves.size());
		Assert.assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN,Square.valueOf(File.FILE_G, Rank.RANK_3),Square.valueOf(File.FILE_G, Rank.RANK_4))));
		Assert.assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN,Square.valueOf(File.FILE_G, Rank.RANK_3),Square.valueOf(File.FILE_G, Rank.RANK_5))));
		Assert.assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN,Square.valueOf(File.FILE_G, Rank.RANK_3),Square.valueOf(File.FILE_G, Rank.RANK_6))));
		Assert.assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN,Square.valueOf(File.FILE_G, Rank.RANK_3),Square.valueOf(File.FILE_G, Rank.RANK_7))));
		Assert.assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN,Square.valueOf(File.FILE_G, Rank.RANK_3),Square.valueOf(File.FILE_G, Rank.RANK_8))));
		Assert.assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN,Square.valueOf(File.FILE_G, Rank.RANK_3),Square.valueOf(File.FILE_H, Rank.RANK_4))));
		Assert.assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN,Square.valueOf(File.FILE_G, Rank.RANK_3),Square.valueOf(File.FILE_H, Rank.RANK_3))));
		Assert.assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN,Square.valueOf(File.FILE_G, Rank.RANK_3),Square.valueOf(File.FILE_H, Rank.RANK_2))));
		Assert.assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN,Square.valueOf(File.FILE_G, Rank.RANK_3),Square.valueOf(File.FILE_G, Rank.RANK_2))));
		Assert.assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN,Square.valueOf(File.FILE_G, Rank.RANK_3),Square.valueOf(File.FILE_G, Rank.RANK_1))));
		Assert.assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN,Square.valueOf(File.FILE_G, Rank.RANK_3),Square.valueOf(File.FILE_F, Rank.RANK_2))));
		Assert.assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN,Square.valueOf(File.FILE_G, Rank.RANK_3),Square.valueOf(File.FILE_E, Rank.RANK_1))));
		Assert.assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN,Square.valueOf(File.FILE_G, Rank.RANK_3),Square.valueOf(File.FILE_F, Rank.RANK_3))));
		Assert.assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN,Square.valueOf(File.FILE_G, Rank.RANK_3),Square.valueOf(File.FILE_E, Rank.RANK_3))));
		Assert.assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN,Square.valueOf(File.FILE_G, Rank.RANK_3),Square.valueOf(File.FILE_D, Rank.RANK_3))));
		Assert.assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN,Square.valueOf(File.FILE_G, Rank.RANK_3),Square.valueOf(File.FILE_F, Rank.RANK_4))));
		Assert.assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN,Square.valueOf(File.FILE_G, Rank.RANK_3),Square.valueOf(File.FILE_E, Rank.RANK_5))));
		Assert.assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN,Square.valueOf(File.FILE_G, Rank.RANK_3),Square.valueOf(File.FILE_D, Rank.RANK_6),Bishop.BLACK_BISHOP)));
	}

	@Test
	public void testQueenCaptures() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "8/8/3bk3/8/8/2K3Q1/8/8 w - - 0 1");
		
		List<Move> moves = new ArrayList<Move>();
		MoveGen.genQueenMoves(b, moves, true, false);
		Assert.assertEquals(1, moves.size());
		Assert.assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN,Square.valueOf(File.FILE_G, Rank.RANK_3),Square.valueOf(File.FILE_D, Rank.RANK_6),Bishop.BLACK_BISHOP)));
	}
	
	@Test
	public void testQueenNoncaptures() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "8/8/3bk3/8/8/2K3Q1/8/8 w - - 0 1");
		
		List<Move> moves = new ArrayList<Move>();
		MoveGen.genQueenMoves(b, moves, false, true);
		Assert.assertEquals(17, moves.size());
		Assert.assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN,Square.valueOf(File.FILE_G, Rank.RANK_3),Square.valueOf(File.FILE_G, Rank.RANK_4))));
		Assert.assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN,Square.valueOf(File.FILE_G, Rank.RANK_3),Square.valueOf(File.FILE_G, Rank.RANK_5))));
		Assert.assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN,Square.valueOf(File.FILE_G, Rank.RANK_3),Square.valueOf(File.FILE_G, Rank.RANK_6))));
		Assert.assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN,Square.valueOf(File.FILE_G, Rank.RANK_3),Square.valueOf(File.FILE_G, Rank.RANK_7))));
		Assert.assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN,Square.valueOf(File.FILE_G, Rank.RANK_3),Square.valueOf(File.FILE_G, Rank.RANK_8))));
		Assert.assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN,Square.valueOf(File.FILE_G, Rank.RANK_3),Square.valueOf(File.FILE_H, Rank.RANK_4))));
		Assert.assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN,Square.valueOf(File.FILE_G, Rank.RANK_3),Square.valueOf(File.FILE_H, Rank.RANK_3))));
		Assert.assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN,Square.valueOf(File.FILE_G, Rank.RANK_3),Square.valueOf(File.FILE_H, Rank.RANK_2))));
		Assert.assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN,Square.valueOf(File.FILE_G, Rank.RANK_3),Square.valueOf(File.FILE_G, Rank.RANK_2))));
		Assert.assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN,Square.valueOf(File.FILE_G, Rank.RANK_3),Square.valueOf(File.FILE_G, Rank.RANK_1))));
		Assert.assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN,Square.valueOf(File.FILE_G, Rank.RANK_3),Square.valueOf(File.FILE_F, Rank.RANK_2))));
		Assert.assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN,Square.valueOf(File.FILE_G, Rank.RANK_3),Square.valueOf(File.FILE_E, Rank.RANK_1))));
		Assert.assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN,Square.valueOf(File.FILE_G, Rank.RANK_3),Square.valueOf(File.FILE_F, Rank.RANK_3))));
		Assert.assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN,Square.valueOf(File.FILE_G, Rank.RANK_3),Square.valueOf(File.FILE_E, Rank.RANK_3))));
		Assert.assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN,Square.valueOf(File.FILE_G, Rank.RANK_3),Square.valueOf(File.FILE_D, Rank.RANK_3))));
		Assert.assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN,Square.valueOf(File.FILE_G, Rank.RANK_3),Square.valueOf(File.FILE_F, Rank.RANK_4))));
		Assert.assertTrue(moves.contains(new Move(Queen.WHITE_QUEEN,Square.valueOf(File.FILE_G, Rank.RANK_3),Square.valueOf(File.FILE_E, Rank.RANK_5))));
	}
	

	@Test
	public void testKingMoves() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "8/8/3k4/2n1P3/8/8/3rP3/R3K2R b KQ - 0 1");
		List<Move> moves = new ArrayList<Move>();
		MoveGen.genKingMoves(b, moves, true, true);
		Assert.assertEquals(7, moves.size());
		Assert.assertTrue(moves.contains(new Move(King.BLACK_KING,Square.valueOf(File.FILE_D, Rank.RANK_6),Square.valueOf(File.FILE_D, Rank.RANK_7))));
		Assert.assertTrue(moves.contains(new Move(King.BLACK_KING,Square.valueOf(File.FILE_D, Rank.RANK_6),Square.valueOf(File.FILE_E, Rank.RANK_7))));
		Assert.assertTrue(moves.contains(new Move(King.BLACK_KING,Square.valueOf(File.FILE_D, Rank.RANK_6),Square.valueOf(File.FILE_E, Rank.RANK_6))));
		Assert.assertTrue(moves.contains(new Move(King.BLACK_KING,Square.valueOf(File.FILE_D, Rank.RANK_6),Square.valueOf(File.FILE_E, Rank.RANK_5),Pawn.WHITE_PAWN)));
		Assert.assertTrue(moves.contains(new Move(King.BLACK_KING,Square.valueOf(File.FILE_D, Rank.RANK_6),Square.valueOf(File.FILE_D, Rank.RANK_5))));
		Assert.assertTrue(moves.contains(new Move(King.BLACK_KING,Square.valueOf(File.FILE_D, Rank.RANK_6),Square.valueOf(File.FILE_C, Rank.RANK_6))));
		Assert.assertTrue(moves.contains(new Move(King.BLACK_KING,Square.valueOf(File.FILE_D, Rank.RANK_6),Square.valueOf(File.FILE_C, Rank.RANK_7))));
		
		// flip sides
		FenParser.setPos(b, "8/8/3k4/2n1P3/8/8/3rP3/RN2K2R w KQ - 0 1");
		moves.clear();
		MoveGen.genKingMoves(b, moves, true, true);
		Assert.assertEquals(5, moves.size());
		Assert.assertTrue(moves.contains(new Move(King.WHITE_KING,Square.valueOf(File.FILE_E, Rank.RANK_1),Square.valueOf(File.FILE_F, Rank.RANK_2))));
		Assert.assertTrue(moves.contains(new Move(King.WHITE_KING,Square.valueOf(File.FILE_E, Rank.RANK_1),Square.valueOf(File.FILE_F, Rank.RANK_1))));
		Assert.assertTrue(moves.contains(new Move(King.WHITE_KING,Square.valueOf(File.FILE_E, Rank.RANK_1),Square.valueOf(File.FILE_D, Rank.RANK_1))));
		Assert.assertTrue(moves.contains(new Move(King.WHITE_KING,Square.valueOf(File.FILE_E, Rank.RANK_1),Square.valueOf(File.FILE_D, Rank.RANK_2),Rook.BLACK_ROOK)));
		Assert.assertTrue(moves.contains(new Move(King.WHITE_KING,Square.valueOf(File.FILE_E, Rank.RANK_1),Square.valueOf(File.FILE_G, Rank.RANK_1),true)));
		
	}
	
	@Test
	public void testKingMoves2() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "3k4/8/8/2n1P3/8/8/3rP3/RN2K2R w KQ - 0 1");
		List<Move> moves = new ArrayList<Move>();
		MoveGen.genKingMoves(b, moves, true, true);

		Assert.assertEquals(5, moves.size());
		Assert.assertTrue(moves.contains(new Move(King.WHITE_KING,Square.valueOf(File.FILE_E, Rank.RANK_1),Square.valueOf(File.FILE_F, Rank.RANK_2))));
		Assert.assertTrue(moves.contains(new Move(King.WHITE_KING,Square.valueOf(File.FILE_E, Rank.RANK_1),Square.valueOf(File.FILE_F, Rank.RANK_1))));
		// Kd1 illegal but that's handled elsewhere
		Assert.assertTrue(moves.contains(new Move(King.WHITE_KING,Square.valueOf(File.FILE_E, Rank.RANK_1),Square.valueOf(File.FILE_D, Rank.RANK_1))));
		Assert.assertTrue(moves.contains(new Move(King.WHITE_KING,Square.valueOf(File.FILE_E, Rank.RANK_1),Square.valueOf(File.FILE_D, Rank.RANK_2),Rook.BLACK_ROOK)));
		Assert.assertTrue(moves.contains(new Move(King.WHITE_KING,Square.valueOf(File.FILE_E, Rank.RANK_1),Square.valueOf(File.FILE_G, Rank.RANK_1),true)));
	}
	
	@Test
	public void testKingMovesInCorner() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "k7/8/8/8/8/8/8/7K w - - 0 1");
		List<Move> moves = new ArrayList<Move>();
		MoveGen.genKingMoves(b, moves, true, true);

		Assert.assertEquals(3, moves.size());
		Assert.assertTrue(moves.contains(new Move(King.WHITE_KING,Square.valueOf(File.FILE_H, Rank.RANK_1),Square.valueOf(File.FILE_G, Rank.RANK_1))));
		Assert.assertTrue(moves.contains(new Move(King.WHITE_KING,Square.valueOf(File.FILE_H, Rank.RANK_1),Square.valueOf(File.FILE_G, Rank.RANK_2))));
		Assert.assertTrue(moves.contains(new Move(King.WHITE_KING,Square.valueOf(File.FILE_H, Rank.RANK_1),Square.valueOf(File.FILE_H, Rank.RANK_2))));
	}

	@Test
	public void testKingMovesInCorner2() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "k7/8/8/8/8/8/8/7K b - - 0 1");
		List<Move> moves = new ArrayList<Move>();
		MoveGen.genKingMoves(b, moves, true, true);

		Assert.assertEquals(3, moves.size());
		Assert.assertTrue(moves.contains(new Move(King.BLACK_KING,Square.valueOf(File.FILE_A, Rank.RANK_8),Square.valueOf(File.FILE_A, Rank.RANK_7))));
		Assert.assertTrue(moves.contains(new Move(King.BLACK_KING,Square.valueOf(File.FILE_A, Rank.RANK_8),Square.valueOf(File.FILE_B, Rank.RANK_7))));
		Assert.assertTrue(moves.contains(new Move(King.BLACK_KING,Square.valueOf(File.FILE_A, Rank.RANK_8),Square.valueOf(File.FILE_B, Rank.RANK_8))));
	}
	
	@Test
	public void testKingNoCastleToEscapeCheck() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "3k4/8/8/2N1P3/7q/8/4P3/R3K2R w KQ - 0 1");
		List<Move> moves = new ArrayList<Move>();
		MoveGen.genKingMoves(b, moves, true, true);

		Assert.assertEquals(4, moves.size());
		Assert.assertTrue(moves.contains(new Move(King.WHITE_KING,Square.valueOf(File.FILE_E, Rank.RANK_1),Square.valueOf(File.FILE_F, Rank.RANK_1))));
		// Kf2 stays in check, but that's handled separately
		Assert.assertTrue(moves.contains(new Move(King.WHITE_KING,Square.valueOf(File.FILE_E, Rank.RANK_1),Square.valueOf(File.FILE_F, Rank.RANK_2))));
		Assert.assertTrue(moves.contains(new Move(King.WHITE_KING,Square.valueOf(File.FILE_E, Rank.RANK_1),Square.valueOf(File.FILE_D, Rank.RANK_1))));
		Assert.assertTrue(moves.contains(new Move(King.WHITE_KING,Square.valueOf(File.FILE_E, Rank.RANK_1),Square.valueOf(File.FILE_D, Rank.RANK_2))));
	}
	
	@Test
	public void testKingCannotCastleThroughCheck() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "r3k2r/8/8/8/8/5Q2/8/4K3 b kq - 0 1");
		List<Move> moves = new ArrayList<Move>();
		MoveGen.genKingMoves(b, moves, true, true);

		Assert.assertEquals(6, moves.size());
		Assert.assertTrue(moves.contains(new Move(King.BLACK_KING,Square.valueOf(File.FILE_E, Rank.RANK_8),Square.valueOf(File.FILE_F, Rank.RANK_8))));
		Assert.assertTrue(moves.contains(new Move(King.BLACK_KING,Square.valueOf(File.FILE_E, Rank.RANK_8),Square.valueOf(File.FILE_F, Rank.RANK_7))));
		Assert.assertTrue(moves.contains(new Move(King.BLACK_KING,Square.valueOf(File.FILE_E, Rank.RANK_8),Square.valueOf(File.FILE_D, Rank.RANK_8))));
		Assert.assertTrue(moves.contains(new Move(King.BLACK_KING,Square.valueOf(File.FILE_E, Rank.RANK_8),Square.valueOf(File.FILE_D, Rank.RANK_7))));
		Assert.assertTrue(moves.contains(new Move(King.BLACK_KING,Square.valueOf(File.FILE_E, Rank.RANK_8),Square.valueOf(File.FILE_E, Rank.RANK_7))));
		Assert.assertTrue(moves.contains(new Move(King.BLACK_KING,Square.valueOf(File.FILE_E, Rank.RANK_8),Square.valueOf(File.FILE_C, Rank.RANK_8),true)));
	}

	@Test
	public void testKingCaptures() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "8/8/3k4/2n1P3/8/8/3rP3/R3K2R b KQ - 0 1");
		List<Move> moves = new ArrayList<Move>();
		MoveGen.genKingMoves(b, moves, true, false);
		
		Assert.assertEquals(1, moves.size());
		Assert.assertTrue(moves.contains(new Move(King.BLACK_KING,Square.valueOf(File.FILE_D, Rank.RANK_6),Square.valueOf(File.FILE_E, Rank.RANK_5),Pawn.WHITE_PAWN)));
	}
	
	@Test
	public void testKingNoncaptures() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "8/8/3k4/2n1P3/8/8/3rP3/R3K2R b KQ - 0 1");
		List<Move> moves = new ArrayList<Move>();
		MoveGen.genKingMoves(b, moves, false,true);
		Assert.assertEquals(6, moves.size());
		
		Assert.assertTrue(moves.contains(new Move(King.BLACK_KING,Square.valueOf(File.FILE_D, Rank.RANK_6),Square.valueOf(File.FILE_D, Rank.RANK_7))));
		Assert.assertTrue(moves.contains(new Move(King.BLACK_KING,Square.valueOf(File.FILE_D, Rank.RANK_6),Square.valueOf(File.FILE_E, Rank.RANK_7))));
		Assert.assertTrue(moves.contains(new Move(King.BLACK_KING,Square.valueOf(File.FILE_D, Rank.RANK_6),Square.valueOf(File.FILE_E, Rank.RANK_6))));
		Assert.assertTrue(moves.contains(new Move(King.BLACK_KING,Square.valueOf(File.FILE_D, Rank.RANK_6),Square.valueOf(File.FILE_D, Rank.RANK_5))));
		Assert.assertTrue(moves.contains(new Move(King.BLACK_KING,Square.valueOf(File.FILE_D, Rank.RANK_6),Square.valueOf(File.FILE_C, Rank.RANK_6))));
		Assert.assertTrue(moves.contains(new Move(King.BLACK_KING,Square.valueOf(File.FILE_D, Rank.RANK_6),Square.valueOf(File.FILE_C, Rank.RANK_7))));
	}
	
	@Test
	public void testPawnMoves() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "2b1k3/PP6/8/3pP3/4P3/8/6P1/4K3 w - d6 0 1");
		List<Move> moves = new ArrayList<Move>();
		MoveGen.genPawnMoves(b, moves, true, true);
		
		Assert.assertEquals(17, moves.size());
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_A, Rank.RANK_7),Square.valueOf(File.FILE_A, Rank.RANK_8),null,Queen.WHITE_QUEEN)));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_A, Rank.RANK_7),Square.valueOf(File.FILE_A, Rank.RANK_8),null,Rook.WHITE_ROOK)));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_A, Rank.RANK_7),Square.valueOf(File.FILE_A, Rank.RANK_8),null,Bishop.WHITE_BISHOP)));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_A, Rank.RANK_7),Square.valueOf(File.FILE_A, Rank.RANK_8),null,Knight.WHITE_KNIGHT)));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_B, Rank.RANK_7),Square.valueOf(File.FILE_B, Rank.RANK_8),null,Queen.WHITE_QUEEN)));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_B, Rank.RANK_7),Square.valueOf(File.FILE_B, Rank.RANK_8),null,Rook.WHITE_ROOK)));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_B, Rank.RANK_7),Square.valueOf(File.FILE_B, Rank.RANK_8),null,Bishop.WHITE_BISHOP)));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_B, Rank.RANK_7),Square.valueOf(File.FILE_B, Rank.RANK_8),null,Knight.WHITE_KNIGHT)));
		// Capture + Promotion
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_B, Rank.RANK_7),Square.valueOf(File.FILE_C, Rank.RANK_8),Bishop.BLACK_BISHOP,Queen.WHITE_QUEEN)));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_B, Rank.RANK_7),Square.valueOf(File.FILE_C, Rank.RANK_8),Bishop.BLACK_BISHOP,Rook.WHITE_ROOK)));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_B, Rank.RANK_7),Square.valueOf(File.FILE_C, Rank.RANK_8),Bishop.BLACK_BISHOP,Bishop.WHITE_BISHOP)));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_B, Rank.RANK_7),Square.valueOf(File.FILE_C, Rank.RANK_8),Bishop.BLACK_BISHOP,Knight.WHITE_KNIGHT)));

		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_5),Square.valueOf(File.FILE_E, Rank.RANK_6))));
		// EP
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_5),Square.valueOf(File.FILE_D, Rank.RANK_6),Pawn.BLACK_PAWN,true)));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_4),Square.valueOf(File.FILE_D, Rank.RANK_5),Pawn.BLACK_PAWN)));

		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_G, Rank.RANK_2),Square.valueOf(File.FILE_G, Rank.RANK_3))));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_G, Rank.RANK_2),Square.valueOf(File.FILE_G, Rank.RANK_4))));
	}

	@Test
	public void testPawnCaptures() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "2b1k3/PP6/8/3pP3/4P3/8/6P1/4K3 w - d6 0 1");
		List<Move> moves = new ArrayList<Move>();
		MoveGen.genPawnMoves(b, moves, true, false);
		
		Assert.assertEquals(14, moves.size());
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_A, Rank.RANK_7),Square.valueOf(File.FILE_A, Rank.RANK_8),null,Queen.WHITE_QUEEN)));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_A, Rank.RANK_7),Square.valueOf(File.FILE_A, Rank.RANK_8),null,Rook.WHITE_ROOK)));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_A, Rank.RANK_7),Square.valueOf(File.FILE_A, Rank.RANK_8),null,Bishop.WHITE_BISHOP)));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_A, Rank.RANK_7),Square.valueOf(File.FILE_A, Rank.RANK_8),null,Knight.WHITE_KNIGHT)));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_B, Rank.RANK_7),Square.valueOf(File.FILE_B, Rank.RANK_8),null,Queen.WHITE_QUEEN)));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_B, Rank.RANK_7),Square.valueOf(File.FILE_B, Rank.RANK_8),null,Rook.WHITE_ROOK)));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_B, Rank.RANK_7),Square.valueOf(File.FILE_B, Rank.RANK_8),null,Bishop.WHITE_BISHOP)));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_B, Rank.RANK_7),Square.valueOf(File.FILE_B, Rank.RANK_8),null,Knight.WHITE_KNIGHT)));
		// Capture + Promotion
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_B, Rank.RANK_7),Square.valueOf(File.FILE_C, Rank.RANK_8),Bishop.BLACK_BISHOP,Queen.WHITE_QUEEN)));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_B, Rank.RANK_7),Square.valueOf(File.FILE_C, Rank.RANK_8),Bishop.BLACK_BISHOP,Rook.WHITE_ROOK)));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_B, Rank.RANK_7),Square.valueOf(File.FILE_C, Rank.RANK_8),Bishop.BLACK_BISHOP,Bishop.WHITE_BISHOP)));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_B, Rank.RANK_7),Square.valueOf(File.FILE_C, Rank.RANK_8),Bishop.BLACK_BISHOP,Knight.WHITE_KNIGHT)));

		// EP
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_5),Square.valueOf(File.FILE_D, Rank.RANK_6),Pawn.BLACK_PAWN,true)));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_4),Square.valueOf(File.FILE_D, Rank.RANK_5),Pawn.BLACK_PAWN)));
	}

	@Test
	public void testPawnNoncaps() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "2b1k3/PP6/8/3pP3/4P3/8/6P1/4K3 w - d6 0 1");
		List<Move> moves = new ArrayList<Move>();
		MoveGen.genPawnMoves(b, moves, false, true);
		
		Assert.assertEquals(3, moves.size());
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_5),Square.valueOf(File.FILE_E, Rank.RANK_6))));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_G, Rank.RANK_2),Square.valueOf(File.FILE_G, Rank.RANK_3))));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_G, Rank.RANK_2),Square.valueOf(File.FILE_G, Rank.RANK_4))));
	}
	
	@Test
	public void testPawnMoves2() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1");
		List<Move> moves = new ArrayList<Move>();
		MoveGen.genPawnMoves(b, moves, true, true);
		
		Assert.assertEquals(16, moves.size());
		Assert.assertTrue(moves.contains(new Move(Pawn.BLACK_PAWN,Square.valueOf(File.FILE_D, Rank.RANK_7),Square.valueOf(File.FILE_D, Rank.RANK_6))));
		Assert.assertTrue(moves.contains(new Move(Pawn.BLACK_PAWN,Square.valueOf(File.FILE_D, Rank.RANK_7),Square.valueOf(File.FILE_D, Rank.RANK_5))));
	}
	
	@Test
	public void testMovesFromInitialPos() {
		Board b = Board.INSTANCE;
		b.resetBoard();
		
		List<Move> moves = MoveGen.genLegalMoves(b);
		Assert.assertEquals(20, moves.size());
		
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_A, Rank.RANK_2),Square.valueOf(File.FILE_A, Rank.RANK_3))));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_A, Rank.RANK_2),Square.valueOf(File.FILE_A, Rank.RANK_4))));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_B, Rank.RANK_2),Square.valueOf(File.FILE_B, Rank.RANK_3))));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_B, Rank.RANK_2),Square.valueOf(File.FILE_B, Rank.RANK_4))));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_C, Rank.RANK_2),Square.valueOf(File.FILE_C, Rank.RANK_3))));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_C, Rank.RANK_2),Square.valueOf(File.FILE_C, Rank.RANK_4))));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_D, Rank.RANK_2),Square.valueOf(File.FILE_D, Rank.RANK_3))));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_D, Rank.RANK_2),Square.valueOf(File.FILE_D, Rank.RANK_4))));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_2),Square.valueOf(File.FILE_E, Rank.RANK_3))));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_2),Square.valueOf(File.FILE_E, Rank.RANK_4))));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_F, Rank.RANK_2),Square.valueOf(File.FILE_F, Rank.RANK_3))));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_F, Rank.RANK_2),Square.valueOf(File.FILE_F, Rank.RANK_4))));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_G, Rank.RANK_2),Square.valueOf(File.FILE_G, Rank.RANK_3))));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_G, Rank.RANK_2),Square.valueOf(File.FILE_G, Rank.RANK_4))));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_H, Rank.RANK_2),Square.valueOf(File.FILE_H, Rank.RANK_3))));
		Assert.assertTrue(moves.contains(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_H, Rank.RANK_2),Square.valueOf(File.FILE_H, Rank.RANK_4))));

		Assert.assertTrue(moves.contains(new Move(Knight.WHITE_KNIGHT,Square.valueOf(File.FILE_B, Rank.RANK_1),Square.valueOf(File.FILE_A, Rank.RANK_3))));
		Assert.assertTrue(moves.contains(new Move(Knight.WHITE_KNIGHT,Square.valueOf(File.FILE_B, Rank.RANK_1),Square.valueOf(File.FILE_C, Rank.RANK_3))));
		Assert.assertTrue(moves.contains(new Move(Knight.WHITE_KNIGHT,Square.valueOf(File.FILE_G, Rank.RANK_1),Square.valueOf(File.FILE_F, Rank.RANK_3))));
		Assert.assertTrue(moves.contains(new Move(Knight.WHITE_KNIGHT,Square.valueOf(File.FILE_G, Rank.RANK_1),Square.valueOf(File.FILE_H, Rank.RANK_3))));
	}
	
	@Test
	public void testCapturesPromosOnlyFromInitialPosition() throws Exception {
		Board b = Board.INSTANCE;
		b.resetBoard();
		List<Move> moves = MoveGen.genPseudoLegalMoves(b, true,false);
		Assert.assertTrue(moves.isEmpty());
	}
	
	@Test
	public void testCapturesPromosOnlyContainsPromotions() throws Exception {
		Board b = Board.INSTANCE;
		EPDParser.setPos(b, "8/4Pk1p/6p1/1r6/8/5N2/2B2PPP/b5K1 w - - bm e8=Q+; id \"position 0631\";");

		List<Move> moves = MoveGen.genPseudoLegalMoves(b, true,false);
		
		MoveParser mp = new MoveParser();
		Move e7e8q = mp.parseMove("e7e8=q", b);
		Move e7e8r = mp.parseMove("e7e8=r", b);
		Move e7e8b = mp.parseMove("e7e8=b", b);
		Move e7e8n = mp.parseMove("e7e8=n", b);
		Move c2g6 = mp.parseMove("c2g6", b);
		Assert.assertTrue(moves.contains(e7e8q));
		Assert.assertTrue(moves.contains(e7e8r));
		Assert.assertTrue(moves.contains(e7e8b));
		Assert.assertTrue(moves.contains(e7e8n));
		Assert.assertTrue(moves.contains(c2g6));
	}
	
	@Test
	public void testCapturesPromosOnlyContainsEP() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "8/8/8/3pP3/8/8/K6k/8 w - d6");
		
		List<Move> moves = MoveGen.genPseudoLegalMoves(b, true,false);
		Assert.assertEquals(1, moves.size());
		MoveParser mp = new MoveParser();
		Assert.assertTrue(moves.contains(mp.parseMove("e5d6", b)));
	}
}
