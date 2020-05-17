package com.jamesswafford.chess4j.search;

import com.jamesswafford.chess4j.board.Undo;
import org.junit.Ignore;
import org.junit.Test;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.movegen.MagicBitboardMoveGenerator;
import com.jamesswafford.chess4j.io.MoveParser;

import static org.junit.Assert.*;

public class ExtendTest {

    @Test
    public void testPassedPawn1() throws Exception {
        Board board = new Board();

        MoveParser mp = new MoveParser();
        Move e4 = mp.parseMove("e4", board);
        assertTrue(MagicBitboardMoveGenerator.genLegalMoves(board).contains(e4));
        board.applyMove(e4);

        assertEquals(0, Extend.extendDepth(board, e4));
    }

    @Test
    public void testExtendPromotion() throws Exception {
        Board board = new Board("k7/b1P5/KP6/6q1/8/8/8/4n3 w - -");

        MoveParser mp = new MoveParser();
        Move move = mp.parseMove("c8=q", board);
        assertTrue(MagicBitboardMoveGenerator.genLegalMoves(board).contains(move));
        board.applyMove(move);

        assertEquals(1, Extend.extendDepth(board, move));
    }

    @Test
    public void testWhitePushTo7th() throws Exception {
        Board board = new Board("k7/b1P5/KP6/6q1/8/8/8/4n3 w - -");

        MoveParser mp = new MoveParser();
        Move move = mp.parseMove("b7", board);
        assertTrue(MagicBitboardMoveGenerator.genLegalMoves(board).contains(move));
        board.applyMove(move);

        assertEquals(1, Extend.extendDepth(board, move));
    }

    @Test
    public void testPassedPawn4() throws Exception {
        Board board = new Board("8/5ppp/8/5PPP/8/6k1/8/6K1 b - -");

        MoveParser mp = new MoveParser();
        Move move = mp.parseMove("g6", board);
        assertTrue(MagicBitboardMoveGenerator.genLegalMoves(board).contains(move));
        board.applyMove(move);

        assertEquals(0, Extend.extendDepth(board, move));
    }

    @Test
    public void testPassedPawn5() throws Exception {
        Board board = new Board("8/8/1PP2PbP/3r4/8/1Q5p/p5N1/k3K3 b - -");

        MoveParser mp = new MoveParser();
        Move move = mp.parseMove("Rd4", board);
        assertTrue(MagicBitboardMoveGenerator.genLegalMoves(board).contains(move));
        board.applyMove(move);

        assertEquals(0, Extend.extendDepth(board, move));
    }

    @Ignore
    @Test
    public void testBlackPushTo2nd() throws Exception {
        Board board = new Board("8/8/1PP2PbP/3r4/8/1Q5p/p5N1/k3K3 b - -");

        MoveParser mp = new MoveParser();
        Move move = mp.parseMove("h2", board);
        assertTrue(MagicBitboardMoveGenerator.genLegalMoves(board).contains(move));
        board.applyMove(move);

        assertEquals(1, Extend.extendDepth(board, move));
    }

    @Test
    public void testPassedPawn7() throws Exception {
        Board board = new Board("8/4kp2/6p1/7p/P7/2K3P1/7P/8 w - -");

        MoveParser mp = new MoveParser();
        Move a5 = mp.parseMove("a5", board);
        assertTrue(MagicBitboardMoveGenerator.genLegalMoves(board).contains(a5));
        board.applyMove(a5);

        assertEquals(0, Extend.extendDepth(board, a5));
    }

    @Test
    public void testPassedPawn8() throws Exception {
        Board board = new Board("8/4kp2/6p1/7p/P7/2K3P1/7P/8 b - -");

        MoveParser mp = new MoveParser();
        Move f6 = mp.parseMove("f6", board);
        assertTrue(MagicBitboardMoveGenerator.genLegalMoves(board).contains(f6));
        board.applyMove(f6);

        assertEquals(0, Extend.extendDepth(board, f6));
    }

    @Test
    public void testPassedPawn9() throws Exception {
        Board board = new Board("8/p3q1kp/1p2Pnp1/3pQ3/2pP4/1nP3N1/1B4PP/6K1 b - -");

        MoveParser mp = new MoveParser();
        Move move = mp.parseMove("a6", board);
        assertTrue(MagicBitboardMoveGenerator.genLegalMoves(board).contains(move));
        board.applyMove(move);

        assertEquals(0, Extend.extendDepth(board, move));
    }

    @Test
    public void testPassedPawn10() throws Exception {
        Board board = new Board("8/p3q1kp/1p2Pnp1/3pQ3/2pP4/1nP3N1/1B4PP/6K1 b - -");

        MoveParser mp = new MoveParser();
        Move move = mp.parseMove("b5", board);
        assertTrue(MagicBitboardMoveGenerator.genLegalMoves(board).contains(move));
        board.applyMove(move);

        assertEquals(0, Extend.extendDepth(board, move));
    }

    @Test
    public void testNewlyCreatedPassedPawn() throws Exception {
        Board board = new Board("7k/8/6p1/4p3/5P2/8/8/7K w - -");

        MoveParser mp = new MoveParser();
        Move move = mp.parseMove("fxe5", board);
        assertTrue(MagicBitboardMoveGenerator.genLegalMoves(board).contains(move));
        Undo undo = board.applyMove(move);

        assertEquals(0, Extend.extendDepth(board, move));

        board.undoMove(undo);
        move = mp.parseMove("f5", board);
        assertTrue(MagicBitboardMoveGenerator.genLegalMoves(board).contains(move));
        board.applyMove(move);

        assertEquals(0, Extend.extendDepth(board, move));
    }

    @Ignore
    @Test
    public void testRecapture() throws Exception {
        Board board = new Board("7k/8/8/3r4/3R4/3r4/8/6K1 w - -");

        MoveParser mp = new MoveParser();
        Move m1 = mp.parseMove("Rxd5", board);
        assertTrue(MagicBitboardMoveGenerator.genLegalMoves(board).contains(m1));
        board.applyMove(m1);

        Move m2 = mp.parseMove("Rxd5", board);
        assertTrue(MagicBitboardMoveGenerator.genLegalMoves(board).contains(m2));
        board.applyMove(m2);

        assertEquals(1, Extend.extendDepth(board, m2));
    }

    @Ignore
    @Test
    public void testRecapture2() throws Exception {
        Board board = new Board("7k/8/8/3r4/3R4/3q4/8/6K1 w - -");

        MoveParser mp = new MoveParser();
        Move m1 = mp.parseMove("Rxd5", board);
        assertTrue(MagicBitboardMoveGenerator.genLegalMoves(board).contains(m1));
        board.applyMove(m1);

        Move m2 = mp.parseMove("Qxd5", board);
        assertTrue(MagicBitboardMoveGenerator.genLegalMoves(board).contains(m2));
        board.applyMove(m2);

        assertEquals(1, Extend.extendDepth(board, m2));
    }

    @Ignore
    @Test
    public void testRecapture3() throws Exception {
        Board board = new Board("7k/8/8/3b4/3R4/3q4/8/6K1 w - -");

        MoveParser mp = new MoveParser();

        Move m1 = mp.parseMove("Rxd5", board);
        assertTrue(MagicBitboardMoveGenerator.genLegalMoves(board).contains(m1));
        board.applyMove(m1);

        Move m2 = mp.parseMove("Qxd5", board);
        assertTrue(MagicBitboardMoveGenerator.genLegalMoves(board).contains(m2));
        board.applyMove(m2);

        assertEquals(0, Extend.extendDepth(board, m2));
    }

}
