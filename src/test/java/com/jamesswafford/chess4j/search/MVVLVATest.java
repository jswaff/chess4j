package com.jamesswafford.chess4j.search;

import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.MoveGen;
import com.jamesswafford.chess4j.io.EPDParser;
import com.jamesswafford.chess4j.io.FenParser;
import com.jamesswafford.chess4j.io.MoveParser;

public class MVVLVATest {

	@Test
	public void testScore1() throws Exception {
		Board b = Board.INSTANCE;
		EPDParser.setPos(b, "3r1rk1/p3qp1p/2bb2p1/2p5/3P4/1P6/PBQN1PPP/2R2RK1 b - - bm Bxg2 Bxh2+; id \"WAC.297\";");
		
		List<Move> moves = MoveGen.genLegalMoves(b);
		MoveParser mp = new MoveParser();
		Move c5d4 = mp.parseMove("c5d4", b); // PxP
		Move c6g2 = mp.parseMove("c6g2", b); // BxP
		Move d6h2 = mp.parseMove("d6h2", b); // BxP
		
		Assert.assertTrue(moves.contains(c5d4));
		Assert.assertTrue(moves.contains(c6g2));
		Assert.assertTrue(moves.contains(d6h2));
		
		// whatever order the BxP moves were in should be preserved.
		boolean flag = moves.indexOf(c6g2) < moves.indexOf(d6h2);

		Collections.sort(moves, new MVVLVA(b));

		Assert.assertTrue(moves.get(0).equals(c5d4));
		if (flag) {
			Assert.assertTrue(moves.get(1).equals(c6g2));
			Assert.assertTrue(moves.get(2).equals(d6h2));
		} else {
			Assert.assertTrue(moves.get(1).equals(d6h2));
			Assert.assertTrue(moves.get(2).equals(c6g2));
		}
	}
	
	@Test
	public void testScore2() throws Exception {
		Board b = Board.INSTANCE;
		EPDParser.setPos(b, "8/4Pk1p/6p1/1r6/8/5N2/2B2PPP/b5K1 w - - bm e8=Q+; id \"position 0631\";");
		
		List<Move> moves = MoveGen.genLegalMoves(b);
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
		
		Collections.sort(moves, new MVVLVA(b));
		
		Assert.assertTrue(moves.get(0).equals(e7e8q));
		Assert.assertTrue(moves.get(1).equals(e7e8r));
		Assert.assertTrue(moves.get(2).equals(e7e8b));
		Assert.assertTrue(moves.get(3).equals(e7e8n));
		Assert.assertTrue(moves.get(4).equals(c2g6));
	}
	
	@Test
	public void testScore3() throws Exception {
		Board b = Board.INSTANCE;
		EPDParser.setPos(b, "6r1/pp1b1P1p/5Q2/3p3k/5K2/8/2P3P1/8 w - - bm fxg8=N; id \"made up 001\";");
		List<Move> moves = MoveGen.genLegalMoves(b);
		
		MoveParser mp = new MoveParser();
		Move f7f8q = mp.parseMove("f7f8=q", b);
		Move f7f8r = mp.parseMove("f7f8=r", b);
		Move f7f8b = mp.parseMove("f7f8=b", b);
		Move f7f8n = mp.parseMove("f7f8=n", b);

		Move f7g8q = mp.parseMove("f7g8=q", b);
		Move f7g8r = mp.parseMove("f7g8=r", b);
		Move f7g8b = mp.parseMove("f7g8=b", b);
		Move f7g8n = mp.parseMove("f7g8=n", b);

		Assert.assertTrue(moves.contains(f7f8q));
		Assert.assertTrue(moves.contains(f7f8r));
		Assert.assertTrue(moves.contains(f7f8b));
		Assert.assertTrue(moves.contains(f7f8n));
		Assert.assertTrue(moves.contains(f7g8q));
		Assert.assertTrue(moves.contains(f7g8r));
		Assert.assertTrue(moves.contains(f7g8b));
		Assert.assertTrue(moves.contains(f7g8n));

		Collections.sort(moves, new MVVLVA(b));

		Assert.assertTrue(moves.get(0).equals(f7g8q));
		Assert.assertTrue(moves.get(1).equals(f7g8r));
		Assert.assertTrue(moves.get(2).equals(f7g8b));
		Assert.assertTrue(moves.get(3).equals(f7g8n));
		Assert.assertTrue(moves.get(4).equals(f7f8q));
		Assert.assertTrue(moves.get(5).equals(f7f8r));
		Assert.assertTrue(moves.get(6).equals(f7f8b));
		Assert.assertTrue(moves.get(7).equals(f7f8n));		
	}
	
	@Test
	public void testScore4() throws Exception {
		Board b = Board.INSTANCE;
		FenParser.setPos(b, "6R1/kp6/8/1KpP4/8/8/8/6B1 w - c6");

		List<Move> moves = MoveGen.genLegalMoves(b);
		MoveParser mp = new MoveParser();
		Move d5c6 = mp.parseMove("d5c6", b);
		Move b5c5 = mp.parseMove("b5c5", b);
		Move g1c5 = mp.parseMove("g1c5", b);
		
		Assert.assertTrue(moves.contains(d5c6));
		Assert.assertTrue(moves.contains(b5c5));
		Assert.assertTrue(moves.contains(g1c5));

		Collections.sort(moves, new MVVLVA(b));

		Assert.assertTrue(moves.get(0).equals(d5c6));
		Assert.assertTrue(moves.get(1).equals(g1c5));
		Assert.assertTrue(moves.get(2).equals(b5c5));
	}
	
	
}
