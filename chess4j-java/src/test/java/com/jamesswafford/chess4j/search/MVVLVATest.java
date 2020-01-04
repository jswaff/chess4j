package com.jamesswafford.chess4j.search;

import java.util.List;

import org.junit.Test;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.movegen.MoveGen;
import com.jamesswafford.chess4j.io.MoveParser;

import static org.junit.Assert.*;


public class MVVLVATest {

    @Test
    public void testScore1() throws Exception {
        Board board = new Board("3r1rk1/p3qp1p/2bb2p1/2p5/3P4/1P6/PBQN1PPP/2R2RK1 b - -");

        List<Move> moves = MoveGen.genLegalMoves(board);
        MoveParser mp = new MoveParser();
        Move c5d4 = mp.parseMove("c5d4", board); // PxP
        Move c6g2 = mp.parseMove("c6g2", board); // BxP
        Move d6h2 = mp.parseMove("d6h2", board); // BxP

        assertTrue(moves.contains(c5d4));
        assertTrue(moves.contains(c6g2));
        assertTrue(moves.contains(d6h2));

        // whatever order the BxP moves were in should be preserved.
        boolean flag = moves.indexOf(c6g2) < moves.indexOf(d6h2);

        moves.sort(new MVVLVA(board));

        assertEquals(c5d4, moves.get(0));
        if (flag) {
            assertEquals(c6g2, moves.get(1));
            assertEquals(d6h2, moves.get(2));
        } else {
            assertEquals(d6h2, moves.get(1));
            assertEquals(c6g2, moves.get(2));
        }
    }

    @Test
    public void testScore2() throws Exception {
        Board board = new Board("8/4Pk1p/6p1/1r6/8/5N2/2B2PPP/b5K1 w - -");

        List<Move> moves = MoveGen.genLegalMoves(board);
        MoveParser mp = new MoveParser();
        Move e7e8q = mp.parseMove("e7e8=q", board);
        Move e7e8r = mp.parseMove("e7e8=r", board);
        Move e7e8b = mp.parseMove("e7e8=b", board);
        Move e7e8n = mp.parseMove("e7e8=n", board);
        Move c2g6 = mp.parseMove("c2g6", board);

        assertTrue(moves.contains(e7e8q));
        assertTrue(moves.contains(e7e8r));
        assertTrue(moves.contains(e7e8b));
        assertTrue(moves.contains(e7e8n));
        assertTrue(moves.contains(c2g6));

        moves.sort(new MVVLVA(board));

        assertEquals(e7e8q, moves.get(0));
        assertEquals(e7e8r, moves.get(1));
        assertEquals(e7e8b, moves.get(2));
        assertEquals(e7e8n, moves.get(3));
        assertEquals(c2g6, moves.get(4));
    }

    @Test
    public void testScore3() throws Exception {
        Board board = new Board("6r1/pp1b1P1p/5Q2/3p3k/5K2/8/2P3P1/8 w - -");
        List<Move> moves = MoveGen.genLegalMoves(board);

        MoveParser mp = new MoveParser();
        Move f7f8q = mp.parseMove("f7f8=q", board);
        Move f7f8r = mp.parseMove("f7f8=r", board);
        Move f7f8b = mp.parseMove("f7f8=b", board);
        Move f7f8n = mp.parseMove("f7f8=n", board);

        Move f7g8q = mp.parseMove("f7g8=q", board);
        Move f7g8r = mp.parseMove("f7g8=r", board);
        Move f7g8b = mp.parseMove("f7g8=b", board);
        Move f7g8n = mp.parseMove("f7g8=n", board);

        assertTrue(moves.contains(f7f8q));
        assertTrue(moves.contains(f7f8r));
        assertTrue(moves.contains(f7f8b));
        assertTrue(moves.contains(f7f8n));
        assertTrue(moves.contains(f7g8q));
        assertTrue(moves.contains(f7g8r));
        assertTrue(moves.contains(f7g8b));
        assertTrue(moves.contains(f7g8n));

        moves.sort(new MVVLVA(board));

        assertEquals(f7g8q, moves.get(0));
        assertEquals(f7g8r, moves.get(1));
        assertEquals(f7g8b, moves.get(2));
        assertEquals(f7g8n, moves.get(3));
        assertEquals(f7f8q, moves.get(4));
        assertEquals(f7f8r, moves.get(5));
        assertEquals(f7f8b, moves.get(6));
        assertEquals(f7f8n, moves.get(7));
    }

    @Test
    public void testScore4() throws Exception {
        Board board = new Board("6R1/kp6/8/1KpP4/8/8/8/6B1 w - c6");

        List<Move> moves = MoveGen.genLegalMoves(board);
        MoveParser mp = new MoveParser();
        Move d5c6 = mp.parseMove("d5c6", board);
        Move b5c5 = mp.parseMove("b5c5", board);
        Move g1c5 = mp.parseMove("g1c5", board);

        assertTrue(moves.contains(d5c6));
        assertTrue(moves.contains(b5c5));
        assertTrue(moves.contains(g1c5));

        moves.sort(new MVVLVA(board));

        assertEquals(d5c6, moves.get(0));
        assertEquals(g1c5, moves.get(1));
        assertEquals(b5c5, moves.get(2));
    }


}
