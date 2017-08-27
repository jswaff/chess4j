package com.jamesswafford.chess4j.hash;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import com.jamesswafford.chess4j.Color;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.CastlingRights;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.exceptions.IllegalMoveException;
import com.jamesswafford.chess4j.exceptions.ParseException;
import com.jamesswafford.chess4j.hash.Zobrist;
import com.jamesswafford.chess4j.io.FenParser;
import com.jamesswafford.chess4j.io.MoveParser;
import com.jamesswafford.chess4j.pieces.Bishop;
import com.jamesswafford.chess4j.pieces.King;
import com.jamesswafford.chess4j.pieces.Knight;
import com.jamesswafford.chess4j.pieces.Pawn;
import com.jamesswafford.chess4j.pieces.Piece;
import com.jamesswafford.chess4j.pieces.Queen;
import com.jamesswafford.chess4j.pieces.Rook;


public class ZobristTest {

	@Test
	public void testHammingDistances() {
		long key=Zobrist.getPlayerKey(Color.WHITE);
		String strKey=DecimalToBinaryString.longToBinary(key);
		Hamming h = new Hamming(strKey,strKey);
		int hd=h.getHammingDistance();
		Assert.assertEquals(0, hd);
	}
	
	@Test
	public void testHammingDistances2() {
		String key1="0000000000000000000000000000000000000000000000000000000000000000";
		String key2="1111111111111111111111111111111111111111111111111111111111111111";
		Assert.assertEquals(64,key1.length());
		Assert.assertEquals(64, key2.length());
		Hamming h = new Hamming(key1,key2);
		int hd=h.getHammingDistance();
		Assert.assertEquals(64,hd);
	}
	
	@Test
	// this test the distances of keys
	public void testHammingDistances3() {
		List<String> keys = getStringKeys();
		// should have 12x64 for pieces + 2 for colors + 64 for EP + 4 for CR
		Assert.assertEquals((12*64)+2+64+4,keys.size());
		
		List<Integer> hammingDistances = new ArrayList<Integer>();
		for (int i=0;i<keys.size();i++) {
			String key = keys.get(i);
			for (int j=i+1;j<keys.size();j++) {
				String key2=keys.get(j);
				int hd = new Hamming(key,key2).getHammingDistance();
				hammingDistances.add(hd);
			}
		}
		
		// should have sum of 1 to numKeys-1 hamming distances to test
		int numHDs = ((keys.size()-1) * keys.size()) / 2;
		Assert.assertEquals(numHDs, hammingDistances.size());
		
		double[] hds = new double[hammingDistances.size()];
		for (int i=0;i<hammingDistances.size();i++) {
			hds[i] = Double.valueOf(hammingDistances.get(i));
		}
		double minHD = StdStats.min(hds);
		//double maxHD = StdStats.max(hds);
		double meanHD = StdStats.mean(hds);
		double stdDevHD = StdStats.stddev(hds);
		/*System.out.println("min hd: " + minHD);
		System.out.println("max hd: " + maxHD);
		System.out.println("mean: " + meanHD);
		System.out.println("stddev: " + stdDevHD);*/
		Assert.assertTrue(minHD >= 3);
		Assert.assertTrue(meanHD >= 31.0 && meanHD <= 33.0);
		Assert.assertTrue(stdDevHD > 3.0);
	}
	
	@Test
	public void testHammingDistancesInGame() throws ParseException, IllegalMoveException {
		Board b = Board.INSTANCE;
		b.resetBoard();
		
		List<String> keys = new ArrayList<String>();
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		MoveParser mp = new MoveParser();
		b.applyMove(mp.parseMove("Nf3", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Nf6", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("c4", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("g6", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Nc3", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Bg7", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("d4", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("O-O", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Bf4", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("d5", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Qb3", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("dxc4", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Qxc4", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("c6", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("e4", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Nbd7", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Rd1", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Nb6", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Qc5", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Bg4", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Bg5", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Na4", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Qa3", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Nxc3", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("bxc3", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Nxe4", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Bxe7", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Qb6", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Bc4", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Nxc3", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Bc5", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Rfe8+", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Kf1", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Be6", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Bxb6", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Bxc4+", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Kg1", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Ne2+", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Kf1", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Nxd4+", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Kg1", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Ne2+", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Kf1", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Nc3+", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Kg1", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("axb6", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Qb4", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Ra4", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Qxb6", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Nxd1", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("h3", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Rxa2", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Kh2", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Nxf2", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Re1", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Rxe1", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Qd8+", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Bf8", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Nxe1", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Bd5", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Nf3", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Ne4", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Qb8", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("b5", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("h4", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("h5", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Ne5", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Kg7", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Kg1", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Bc5+", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Kf1", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Ng3+", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Ke1", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Bb4+", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Kd1", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Bb3+", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Kc1", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Ne2+", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Kb1", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Nc3+", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Kc1", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));
		b.applyMove(mp.parseMove("Rc2#", b));
		keys.add(DecimalToBinaryString.longToBinary(Zobrist.getBoardKey(b)));

		Assert.assertTrue(keys.size()==83);
		
		List<Integer> hammingDistances = new ArrayList<Integer>();
		for (int i=0;i<keys.size();i++) {
			String key = keys.get(i);
			for (int j=i+1;j<keys.size();j++) {
				String key2=keys.get(j);
				int hd = new Hamming(key,key2).getHammingDistance();
				hammingDistances.add(hd);
			}
		}
		
		// should have sum of 1 to numKeys-1 hamming distances to test
		int numHDs = ((keys.size()-1) * keys.size()) / 2;
		Assert.assertEquals(numHDs, hammingDistances.size());
		
		double[] hds = new double[hammingDistances.size()];
		for (int i=0;i<hammingDistances.size();i++) {
			hds[i] = Double.valueOf(hammingDistances.get(i));
		}
		double minHD = StdStats.min(hds);
		//double maxHD = StdStats.max(hds);
		double meanHD = StdStats.mean(hds);
		double stdDevHD = StdStats.stddev(hds);
		/*System.out.println("min hd: " + minHD);
		System.out.println("max hd: " + maxHD);
		System.out.println("mean: " + meanHD);
		System.out.println("stddev: " + stdDevHD);*/
		Assert.assertTrue(minHD >= 3);
		Assert.assertTrue(meanHD >= 31.0 && meanHD <= 33.0);
		Assert.assertTrue(stdDevHD > 3.0);

	}
	
	private List<String> getStringKeys() {
		List<String> skeys = new ArrayList<String>();
		List<Long> ikeys = getKeys();
		for (Long ikey : ikeys) {
			skeys.add(DecimalToBinaryString.longToBinary(ikey));
		}
		return skeys;
	}
	
	private List<Long> getKeys() {
		List<Long> keys = new ArrayList<Long>();
		
		// add colors
		keys.add(Zobrist.getPlayerKey(Color.WHITE));
		keys.add(Zobrist.getPlayerKey(Color.BLACK));
		
		// add ep squares
		List<Square> sqs = Square.allSquares();
		for (Square sq : sqs) {
			keys.add(Zobrist.getEnPassantKey(sq));
		}
		
		// add castling rights
		Set<CastlingRights> crs = EnumSet.allOf(CastlingRights.class);
		for (CastlingRights cr : crs) {
			keys.add(Zobrist.getCastlingKey(cr));
		}
		
		// add piece/square keys
		addToKeys(keys,Pawn.BLACK_PAWN);
		addToKeys(keys,Pawn.WHITE_PAWN);
		addToKeys(keys,Rook.BLACK_ROOK);
		addToKeys(keys,Rook.WHITE_ROOK);
		addToKeys(keys,Knight.BLACK_KNIGHT);
		addToKeys(keys,Knight.WHITE_KNIGHT);
		addToKeys(keys,Bishop.BLACK_BISHOP);
		addToKeys(keys,Bishop.WHITE_BISHOP);
		addToKeys(keys,Queen.BLACK_QUEEN);
		addToKeys(keys,Queen.WHITE_QUEEN);
		addToKeys(keys,King.BLACK_KING);
		addToKeys(keys,King.WHITE_KING);
		
		return keys;
	}
	
	private void addToKeys(List<Long> keys,Piece p) {
		List<Square> sqs = Square.allSquares();
		for (Square sq : sqs) {
			keys.add(Zobrist.getPieceKey(sq, p));
		}		
	}
	
	@Test
	// the idea here is to progress through a series of moves, and for each one set up an equivalent board using
	// FEN notation, and make sure we compute the same zobrist keys.  Also keep track of a set of these
	// keys as we go an make sure they are all unique.
	public void testGetBoardKey() throws Exception {
		Board b = Board.INSTANCE;
		b.resetBoard();
		Board b2 = b.deepCopy();
		
		Set<Long> keys = new HashSet<Long>();

		Move m = new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_2), Square.valueOf(File.FILE_E, Rank.RANK_4));
		b.applyMove(m);
		FenParser.setPos(b2, "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1");
		long key = Zobrist.getBoardKey(b);
		Assert.assertEquals(Zobrist.getBoardKey(b2), key);
		Assert.assertTrue(!keys.contains(key));
		keys.add(key);
		
		b.applyMove(new Move(Pawn.BLACK_PAWN,Square.valueOf(File.FILE_C, Rank.RANK_7), Square.valueOf(File.FILE_C, Rank.RANK_5)));
		FenParser.setPos(b2, "rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2");
		key = Zobrist.getBoardKey(b);
		Assert.assertEquals(Zobrist.getBoardKey(b2), key);
		Assert.assertTrue(!keys.contains(key));
		keys.add(key);
		
		b.applyMove(new Move(Knight.WHITE_KNIGHT,Square.valueOf(File.FILE_G, Rank.RANK_1), Square.valueOf(File.FILE_F, Rank.RANK_3)));
		FenParser.setPos(b2, "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2");
		key = Zobrist.getBoardKey(b);
		Assert.assertEquals(Zobrist.getBoardKey(b2), key);
		Assert.assertTrue(!keys.contains(key));
		keys.add(key);

		b.applyMove(new Move(Queen.BLACK_QUEEN,Square.valueOf(File.FILE_D, Rank.RANK_8), Square.valueOf(File.FILE_A, Rank.RANK_5)));
		FenParser.setPos(b2, "rnb1kbnr/pp1ppppp/8/q1p5/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 2 3");
		key = Zobrist.getBoardKey(b);
		Assert.assertEquals(Zobrist.getBoardKey(b2), key);
		Assert.assertTrue(!keys.contains(key));
		keys.add(key);
		
		b.applyMove(new Move(Bishop.WHITE_BISHOP,Square.valueOf(File.FILE_F, Rank.RANK_1), Square.valueOf(File.FILE_E, Rank.RANK_2)));
		FenParser.setPos(b2, "rnb1kbnr/pp1ppppp/8/q1p5/4P3/5N2/PPPPBPPP/RNBQK2R b KQkq - 3 3");
		key = Zobrist.getBoardKey(b);
		Assert.assertEquals(Zobrist.getBoardKey(b2), key);
		Assert.assertTrue(!keys.contains(key));
		keys.add(key);
		
		b.applyMove(new Move(Queen.BLACK_QUEEN,Square.valueOf(File.FILE_A, Rank.RANK_5), Square.valueOf(File.FILE_D, Rank.RANK_2),Bishop.WHITE_BISHOP));
		FenParser.setPos(b2, "rnb1kbnr/pp1ppppp/8/2p5/4P3/5N2/PPPqBPPP/RNBQK2R w KQkq - 0 4");
		key = Zobrist.getBoardKey(b);
		Assert.assertEquals(Zobrist.getBoardKey(b2), key);
		Assert.assertTrue(!keys.contains(key));
		keys.add(key);

		b.applyMove(new Move(Knight.WHITE_KNIGHT,Square.valueOf(File.FILE_B, Rank.RANK_1), Square.valueOf(File.FILE_D, Rank.RANK_2),Queen.BLACK_QUEEN));
		FenParser.setPos(b2, "rnb1kbnr/pp1ppppp/8/2p5/4P3/5N2/PPPNBPPP/R1BQK2R b KQkq - 0 4");
		key = Zobrist.getBoardKey(b);
		Assert.assertEquals(Zobrist.getBoardKey(b2), key);
		Assert.assertTrue(!keys.contains(key));
		keys.add(key);

		b.applyMove(new Move(Pawn.BLACK_PAWN,Square.valueOf(File.FILE_C, Rank.RANK_5), Square.valueOf(File.FILE_C, Rank.RANK_4)));
		FenParser.setPos(b2, "rnb1kbnr/pp1ppppp/8/8/2p1P3/5N2/PPPNBPPP/R1BQK2R w KQkq - 0 5");
		key = Zobrist.getBoardKey(b);
		Assert.assertEquals(Zobrist.getBoardKey(b2), key);
		Assert.assertTrue(!keys.contains(key));
		keys.add(key);

		b.applyMove(new Move(King.WHITE_KING,Square.valueOf(File.FILE_E, Rank.RANK_1), Square.valueOf(File.FILE_G, Rank.RANK_1),true));
		FenParser.setPos(b2, "rnb1kbnr/pp1ppppp/8/8/2p1P3/5N2/PPPNBPPP/R1BQ1RK1 b kq - 0 5");
		key = Zobrist.getBoardKey(b);
		Assert.assertEquals(Zobrist.getBoardKey(b2), key);
		Assert.assertTrue(!keys.contains(key));
		keys.add(key);

		b.applyMove(new Move(King.BLACK_KING,Square.valueOf(File.FILE_E, Rank.RANK_8), Square.valueOf(File.FILE_D, Rank.RANK_8)));
		FenParser.setPos(b2, "rnbk1bnr/pp1ppppp/8/8/2p1P3/5N2/PPPNBPPP/R1BQ1RK1 w - - 1 6");
		key = Zobrist.getBoardKey(b);
		Assert.assertEquals(Zobrist.getBoardKey(b2), key);
		Assert.assertTrue(!keys.contains(key));
		keys.add(key);

		b.applyMove(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_B, Rank.RANK_2), Square.valueOf(File.FILE_B, Rank.RANK_4)));
		FenParser.setPos(b2, "rnbk1bnr/pp1ppppp/8/8/1Pp1P3/5N2/P1PNBPPP/R1BQ1RK1 b - b3 0 6");
		key = Zobrist.getBoardKey(b);
		Assert.assertEquals(Zobrist.getBoardKey(b2), key);
		Assert.assertTrue(!keys.contains(key));
		keys.add(key);
		b.applyMove(new Move(Pawn.BLACK_PAWN,Square.valueOf(File.FILE_C, Rank.RANK_4), Square.valueOf(File.FILE_B, Rank.RANK_3),Pawn.BLACK_PAWN,true));
		FenParser.setPos(b2, "rnbk1bnr/pp1ppppp/8/8/4P3/1p3N2/P1PNBPPP/R1BQ1RK1 w - - 0 7");
		key = Zobrist.getBoardKey(b);
		Assert.assertEquals(Zobrist.getBoardKey(b2), key);
		Assert.assertTrue(!keys.contains(key));
		keys.add(key);

		b.applyMove(new Move(Rook.WHITE_ROOK,Square.valueOf(File.FILE_F, Rank.RANK_1), Square.valueOf(File.FILE_E, Rank.RANK_1)));
		FenParser.setPos(b2, "rnbk1bnr/pp1ppppp/8/8/4P3/1p3N2/P1PNBPPP/R1BQR1K1 b - - 1 7");
		key = Zobrist.getBoardKey(b);
		Assert.assertEquals(Zobrist.getBoardKey(b2), key);
		Assert.assertTrue(!keys.contains(key));
		keys.add(key);

		b.applyMove(new Move(Pawn.BLACK_PAWN,Square.valueOf(File.FILE_B, Rank.RANK_3), Square.valueOf(File.FILE_B, Rank.RANK_2)));
		FenParser.setPos(b2, "rnbk1bnr/pp1ppppp/8/8/4P3/5N2/PpPNBPPP/R1BQR1K1 w - - 0 8");
		key = Zobrist.getBoardKey(b);
		Assert.assertEquals(Zobrist.getBoardKey(b2), key);
		Assert.assertTrue(!keys.contains(key));
		keys.add(key);

		b.applyMove(new Move(King.WHITE_KING,Square.valueOf(File.FILE_G, Rank.RANK_1), Square.valueOf(File.FILE_H, Rank.RANK_1)));
		FenParser.setPos(b2, "rnbk1bnr/pp1ppppp/8/8/4P3/5N2/PpPNBPPP/R1BQR2K b - - 1 8");
		key = Zobrist.getBoardKey(b);
		Assert.assertEquals(Zobrist.getBoardKey(b2), key);
		Assert.assertTrue(!keys.contains(key));
		keys.add(key);

		b.applyMove(new Move(Pawn.BLACK_PAWN,Square.valueOf(File.FILE_B, Rank.RANK_2), 
				Square.valueOf(File.FILE_A, Rank.RANK_1),
				Rook.WHITE_ROOK,
				Knight.BLACK_KNIGHT));
		FenParser.setPos(b2, "rnbk1bnr/pp1ppppp/8/8/4P3/5N2/P1PNBPPP/n1BQR2K w - - 0 9");
		key = Zobrist.getBoardKey(b);
		Assert.assertEquals(Zobrist.getBoardKey(b2), key);
		Assert.assertTrue(!keys.contains(key));
		keys.add(key);
	}

	@Test
	/*
	 * Should be able to obtain an equal position using the French Defense and Petrov Defense
	 */
	public void testGetBoardKey2() throws Exception {
		List<Long> keys1 = new ArrayList<Long>();
		List<Long> keys2 = new ArrayList<Long>();
		
		Board b1 = Board.INSTANCE;
		b1.resetBoard();
		Board b2 = b1.deepCopy();
		
		Assert.assertEquals(Zobrist.getBoardKey(b1), Zobrist.getBoardKey(b2));

		// step through French Defense with b1
		b1.applyMove(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_2),Square.valueOf(File.FILE_E, Rank.RANK_4)));
		keys1.add(Zobrist.getBoardKey(b1));
		b1.applyMove(new Move(Pawn.BLACK_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_7),Square.valueOf(File.FILE_E, Rank.RANK_6)));
		keys1.add(Zobrist.getBoardKey(b1));
		b1.applyMove(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_D, Rank.RANK_2),Square.valueOf(File.FILE_D, Rank.RANK_4)));
		keys1.add(Zobrist.getBoardKey(b1));
		b1.applyMove(new Move(Pawn.BLACK_PAWN,Square.valueOf(File.FILE_D, Rank.RANK_7),Square.valueOf(File.FILE_D, Rank.RANK_5)));
		keys1.add(Zobrist.getBoardKey(b1));
		b1.applyMove(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_4),Square.valueOf(File.FILE_D, Rank.RANK_5),Pawn.BLACK_PAWN));
		keys1.add(Zobrist.getBoardKey(b1));
		b1.applyMove(new Move(Pawn.BLACK_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_6),Square.valueOf(File.FILE_D, Rank.RANK_5),Pawn.WHITE_PAWN));
		keys1.add(Zobrist.getBoardKey(b1));
		b1.applyMove(new Move(Knight.WHITE_KNIGHT,Square.valueOf(File.FILE_G, Rank.RANK_1),Square.valueOf(File.FILE_F, Rank.RANK_3)));
		keys1.add(Zobrist.getBoardKey(b1));
		b1.applyMove(new Move(Knight.BLACK_KNIGHT,Square.valueOf(File.FILE_G, Rank.RANK_8),Square.valueOf(File.FILE_F, Rank.RANK_6)));
		keys1.add(Zobrist.getBoardKey(b1));
		
		
		// step through the Petrov Defense with b2
		b2.applyMove(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_2),Square.valueOf(File.FILE_E, Rank.RANK_4)));
		keys2.add(Zobrist.getBoardKey(b2));
		b2.applyMove(new Move(Pawn.BLACK_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_7),Square.valueOf(File.FILE_E, Rank.RANK_5)));
		keys2.add(Zobrist.getBoardKey(b2));
		b2.applyMove(new Move(Knight.WHITE_KNIGHT,Square.valueOf(File.FILE_G, Rank.RANK_1),Square.valueOf(File.FILE_F, Rank.RANK_3)));
		keys2.add(Zobrist.getBoardKey(b2));
		b2.applyMove(new Move(Knight.BLACK_KNIGHT,Square.valueOf(File.FILE_G, Rank.RANK_8),Square.valueOf(File.FILE_F, Rank.RANK_6)));
		keys2.add(Zobrist.getBoardKey(b2));
		b2.applyMove(new Move(Knight.WHITE_KNIGHT,Square.valueOf(File.FILE_F, Rank.RANK_3),Square.valueOf(File.FILE_E, Rank.RANK_5),Pawn.BLACK_PAWN));
		keys2.add(Zobrist.getBoardKey(b2));
		b2.applyMove(new Move(Pawn.BLACK_PAWN,Square.valueOf(File.FILE_D, Rank.RANK_7),Square.valueOf(File.FILE_D, Rank.RANK_6)));
		keys2.add(Zobrist.getBoardKey(b2));
		b2.applyMove(new Move(Knight.WHITE_KNIGHT,Square.valueOf(File.FILE_E, Rank.RANK_5),Square.valueOf(File.FILE_F, Rank.RANK_3)));
		keys2.add(Zobrist.getBoardKey(b2));
		b2.applyMove(new Move(Knight.BLACK_KNIGHT,Square.valueOf(File.FILE_F, Rank.RANK_6),Square.valueOf(File.FILE_E, Rank.RANK_4),Pawn.WHITE_PAWN));
		keys2.add(Zobrist.getBoardKey(b2));
		b2.applyMove(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_D, Rank.RANK_2),Square.valueOf(File.FILE_D, Rank.RANK_3)));
		keys2.add(Zobrist.getBoardKey(b2));
		b2.applyMove(new Move(Knight.BLACK_KNIGHT,Square.valueOf(File.FILE_E, Rank.RANK_4),Square.valueOf(File.FILE_F, Rank.RANK_6)));
		keys2.add(Zobrist.getBoardKey(b2));
		b2.applyMove(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_D, Rank.RANK_3),Square.valueOf(File.FILE_D, Rank.RANK_4)));
		keys2.add(Zobrist.getBoardKey(b2));
		b2.applyMove(new Move(Pawn.BLACK_PAWN,Square.valueOf(File.FILE_D, Rank.RANK_6),Square.valueOf(File.FILE_D, Rank.RANK_5)));
		keys2.add(Zobrist.getBoardKey(b2));
		

		// Positions would be equal at this point, except for move history and fifty counter  
		Assert.assertFalse(b1.equals(b2));
		Assert.assertEquals(Zobrist.getBoardKey(b1), Zobrist.getBoardKey(b2));

		// by adding a pawn move we should be equal except move history and number of moves
		b1.applyMove(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_G, Rank.RANK_2),Square.valueOf(File.FILE_G, Rank.RANK_3)));
		b2.applyMove(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_G, Rank.RANK_2),Square.valueOf(File.FILE_G, Rank.RANK_3)));
		
		Assert.assertFalse(b1.equals(b2));
		Assert.assertEquals(Zobrist.getBoardKey(b1), Zobrist.getBoardKey(b2));

		
		// hash codes should be equal at beginning, move 1, move 7 and end only.
		for (int i=0;i<keys1.size();i++) {
			long key1 = keys1.get(i);
			if (i==0) {
				Assert.assertTrue(keys2.get(i)==key1);
				Assert.assertFalse(keys2.subList(1, keys2.size()).contains(key1));
			} else if (i==7) {
				Assert.assertTrue(keys2.get(11).equals(key1));
				Assert.assertFalse(keys2.subList(0, keys2.size()-1).contains(key1));
			} else {
				Assert.assertFalse(keys2.contains(key1));
			}
		}
	}

	@Test
	/*
	 * Should be able to obtain an equal position using the Queen's Gambit (d4,d5,c4,e6,Nc3,Nf6) and
	 * the English Opening (c4,Nf6,Nc3,e6,d4,d5).
	 */
	public void testGetBoardKey3() throws Exception {
		List<Long> keys1 = new ArrayList<Long>();
		List<Long> keys2 = new ArrayList<Long>();
		
		Board b1 = Board.INSTANCE;
		b1.resetBoard();
		Board b2 = b1.deepCopy();
		
		Assert.assertEquals(b1.hashCode(), b2.hashCode());
		Assert.assertEquals(b1.hashCodeWithoutMoveHistory(true), b2.hashCodeWithoutMoveHistory(true));
		
		// Go through Queen's Gambit with b1
		b1.applyMove(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_D, Rank.RANK_2),Square.valueOf(File.FILE_D, Rank.RANK_4)));
		keys1.add(Zobrist.getBoardKey(b1));
		b1.applyMove(new Move(Pawn.BLACK_PAWN,Square.valueOf(File.FILE_D, Rank.RANK_7),Square.valueOf(File.FILE_D, Rank.RANK_5)));
		keys1.add(Zobrist.getBoardKey(b1));
		b1.applyMove(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_C, Rank.RANK_2),Square.valueOf(File.FILE_C, Rank.RANK_4)));
		keys1.add(Zobrist.getBoardKey(b1));
		b1.applyMove(new Move(Pawn.BLACK_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_7),Square.valueOf(File.FILE_E, Rank.RANK_6)));
		keys1.add(Zobrist.getBoardKey(b1));
		b1.applyMove(new Move(Knight.WHITE_KNIGHT,Square.valueOf(File.FILE_B, Rank.RANK_1),Square.valueOf(File.FILE_C, Rank.RANK_3)));
		keys1.add(Zobrist.getBoardKey(b1));
		b1.applyMove(new Move(Knight.BLACK_KNIGHT,Square.valueOf(File.FILE_G, Rank.RANK_8),Square.valueOf(File.FILE_F, Rank.RANK_6)));
		keys1.add(Zobrist.getBoardKey(b1));
		
		// Step through English Opening with b2
		b2.applyMove(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_C, Rank.RANK_2),Square.valueOf(File.FILE_C, Rank.RANK_4)));
		keys2.add(Zobrist.getBoardKey(b2));
		b2.applyMove(new Move(Knight.BLACK_KNIGHT,Square.valueOf(File.FILE_G, Rank.RANK_8),Square.valueOf(File.FILE_F, Rank.RANK_6)));
		keys2.add(Zobrist.getBoardKey(b2));
		b2.applyMove(new Move(Knight.WHITE_KNIGHT,Square.valueOf(File.FILE_B, Rank.RANK_1),Square.valueOf(File.FILE_C, Rank.RANK_3)));
		keys2.add(Zobrist.getBoardKey(b2));
		b2.applyMove(new Move(Pawn.BLACK_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_7),Square.valueOf(File.FILE_E, Rank.RANK_6)));
		keys2.add(Zobrist.getBoardKey(b2));
		b2.applyMove(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_D, Rank.RANK_2),Square.valueOf(File.FILE_D, Rank.RANK_4)));
		keys2.add(Zobrist.getBoardKey(b2));
		b2.applyMove(new Move(Pawn.BLACK_PAWN,Square.valueOf(File.FILE_D, Rank.RANK_7),Square.valueOf(File.FILE_D, Rank.RANK_5)));
		keys2.add(Zobrist.getBoardKey(b2));
		
		// Positions would be equal at this point, except for move history, fifty counter and ep square 
		Assert.assertFalse(b1.equals(b2));
		Assert.assertFalse(Zobrist.getBoardKey(b1)==Zobrist.getBoardKey(b2));
		
		// by adding a pawn move we should be equal except move history
		b1.applyMove(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_G, Rank.RANK_2),Square.valueOf(File.FILE_G, Rank.RANK_3)));
		b2.applyMove(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_G, Rank.RANK_2),Square.valueOf(File.FILE_G, Rank.RANK_3)));
		Assert.assertFalse(b1.equals(b2));
		Assert.assertEquals(Zobrist.getBoardKey(b1), Zobrist.getBoardKey(b2));
		
		// keys should be equal at beginning and end only.  Neither were
		// saved in list so lists should contain completely different codes
		for (long key1 : keys1) {
			Assert.assertFalse(keys2.contains(key1));
		}
	}
}
