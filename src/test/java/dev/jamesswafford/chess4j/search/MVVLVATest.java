package dev.jamesswafford.chess4j.search;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.Move;
import dev.jamesswafford.chess4j.movegen.MagicBitboardMoveGenerator;
import dev.jamesswafford.chess4j.io.MoveParser;

import static org.junit.Assert.*;


public class MVVLVATest {

    @Test
    public void bishopTakesPawnVsPawnTakesPawn() throws Exception {
        Board board = new Board("3r1rk1/p3qp1p/2bb2p1/2p5/3P4/1P6/PBQN1PPP/2R2RK1 b - -");

        List<Move> moves = MagicBitboardMoveGenerator.genLegalMoves(board);
        Collections.shuffle(moves);

        MoveParser mp = new MoveParser();
        Move c5d4 = mp.parseMove("c5d4", board); // PxP
        Move c6g2 = mp.parseMove("c6g2", board); // BxP
        Move d6h2 = mp.parseMove("d6h2", board); // BxP

        assertTrue(moves.contains(c5d4));
        assertTrue(moves.contains(c6g2));
        assertTrue(moves.contains(d6h2));

        // whatever order the BxP moves were in should be preserved.
        boolean flag = moves.indexOf(c6g2) < moves.indexOf(d6h2);

        moves.sort(Comparator.comparingInt(MVVLVA::score).reversed());

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
    public void noncapturingPromos() throws Exception {
        Board board = new Board("8/4Pk1p/6p1/1r6/8/5N2/2B2PPP/b5K1 w - -");

        List<Move> moves = MagicBitboardMoveGenerator.genLegalMoves(board);
        Collections.shuffle(moves);

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

        moves.sort(Comparator.comparingInt(MVVLVA::score).reversed());

        assertEquals(e7e8q, moves.get(0));
        assertEquals(e7e8r, moves.get(1));
        assertEquals(e7e8b, moves.get(2));
        assertEquals(e7e8n, moves.get(3));
        assertEquals(c2g6, moves.get(4));
    }

    @Test
    public void capturingVsNoncapturingPromos() throws Exception {
        Board board = new Board("6r1/pp1b1P1p/5Q2/3p3k/5K2/8/2P3P1/8 w - -");
        List<Move> moves = MagicBitboardMoveGenerator.genLegalMoves(board);
        Collections.shuffle(moves);

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

        moves.sort(Comparator.comparingInt(MVVLVA::score).reversed());

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
    public void promosCapturesEp() throws Exception {
        Board board = new Board("3b2R1/kp2P3/8/1KpP4/8/6q1/5B1P/5N2 w - c6");

        List<Move> moves = MagicBitboardMoveGenerator.genLegalMoves(board);
        Collections.shuffle(moves);

        MoveParser mp = new MoveParser();
        Move d5c6 = mp.parseMove("d5c6", board);
        Move b5c5 = mp.parseMove("b5c5", board);
        Move f2c5 = mp.parseMove("f2c5", board);
        Move f2g3 = mp.parseMove("f2g3", board);
        Move g8g3 = mp.parseMove("g8g3", board);
        Move g8d8 = mp.parseMove("g8d8", board);
        Move f1g3 = mp.parseMove("f1g3", board);
        Move h2g3 = mp.parseMove("h2g3", board);
        Move e7d8q = mp.parseMove("e7d8q", board);
        Move e7d8r = mp.parseMove("e7d8r", board);
        Move e7d8b = mp.parseMove("e7d8b", board);
        Move e7d8n = mp.parseMove("e7d8n", board);
        Move e7e8q = mp.parseMove("e7e8q", board);
        Move e7e8r = mp.parseMove("e7e8r", board);
        Move e7e8b = mp.parseMove("e7e8b", board);
        Move e7e8n = mp.parseMove("e7e8n", board);

        assertTrue(moves.contains(d5c6));
        assertTrue(moves.contains(b5c5));
        assertTrue(moves.contains(f2c5));
        assertTrue(moves.contains(f2g3));
        assertTrue(moves.contains(g8g3));
        assertTrue(moves.contains(g8d8));
        assertTrue(moves.contains(f1g3));
        assertTrue(moves.contains(h2g3));
        assertTrue(moves.contains(e7d8q));
        assertTrue(moves.contains(e7d8r));
        assertTrue(moves.contains(e7d8b));
        assertTrue(moves.contains(e7d8n));
        assertTrue(moves.contains(e7e8q));
        assertTrue(moves.contains(e7e8r));
        assertTrue(moves.contains(e7e8b));
        assertTrue(moves.contains(e7e8n));

        moves.sort(Comparator.comparingInt(MVVLVA::score).reversed());

        assertEquals(e7d8q, moves.get(0));
        assertEquals(e7d8r, moves.get(1));
        assertEquals(e7d8b, moves.get(2));
        assertEquals(e7d8n, moves.get(3));
        assertEquals(e7e8q, moves.get(4));
        assertEquals(e7e8r, moves.get(5));
        assertEquals(e7e8b, moves.get(6));
        assertEquals(e7e8n, moves.get(7));
        assertEquals(h2g3, moves.get(8));
        assertEquals(f1g3, moves.get(9));
        assertEquals(f2g3, moves.get(10));
        assertEquals(g8g3, moves.get(11));
        assertEquals(g8d8, moves.get(12));
        assertEquals(d5c6, moves.get(13));
        assertEquals(f2c5, moves.get(14));
        assertEquals(b5c5, moves.get(15));
    }

    @Test
    public void blackToMove() throws Exception {
        Board board = new Board("7k/8/4p3/r2P2q1/4P3/1b6/8/7K b - - ");

        List<Move> moves = MagicBitboardMoveGenerator.genLegalMoves(board);
        Collections.shuffle(moves);

        MoveParser mp = new MoveParser();
        Move e6d5 = mp.parseMove("e6d5", board);
        Move b3d5 = mp.parseMove("b3d5", board);
        Move a5d5 = mp.parseMove("a5d5", board);
        Move g5d5 = mp.parseMove("g5d5", board);

        assertTrue(moves.contains(e6d5));
        assertTrue(moves.contains(b3d5));
        assertTrue(moves.contains(a5d5));
        assertTrue(moves.contains(g5d5));

        moves.sort(Comparator.comparingInt(MVVLVA::score).reversed());

        assertEquals(e6d5, moves.get(0));
        assertEquals(b3d5, moves.get(1));
        assertEquals(a5d5, moves.get(2));
        assertEquals(g5d5, moves.get(3));
    }

}
