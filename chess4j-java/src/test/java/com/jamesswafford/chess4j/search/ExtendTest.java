package com.jamesswafford.chess4j.search;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.movegen.MoveGen;
import com.jamesswafford.chess4j.io.FenParser;
import com.jamesswafford.chess4j.io.MoveParser;

import static org.junit.Assert.*;

public class ExtendTest {

    @Test
    public void testPassedPawn1() throws Exception {
        Board b = Board.INSTANCE;
        b.resetBoard();

        MoveParser mp = new MoveParser();
        Move e4 = mp.parseMove("e4", b);
        Assert.assertTrue(MoveGen.genLegalMoves(b).contains(e4));
        b.applyMove(e4);

        Assert.assertEquals(0, Extend.extendDepth(b, e4));
    }

    @Test
    public void testExtendPromotion() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "k7/b1P5/KP6/6q1/8/8/8/4n3 w - -");

        MoveParser mp = new MoveParser();
        Move m = mp.parseMove("c8=q", b);
        Assert.assertTrue(MoveGen.genLegalMoves(b).contains(m));
        b.applyMove(m);

        Assert.assertEquals(1, Extend.extendDepth(b, m));
    }

    @Test
    public void testWhitePushTo7th() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "k7/b1P5/KP6/6q1/8/8/8/4n3 w - -");

        MoveParser mp = new MoveParser();
        Move m = mp.parseMove("b7", b);
        Assert.assertTrue(MoveGen.genLegalMoves(b).contains(m));
        b.applyMove(m);

        Assert.assertEquals(1, Extend.extendDepth(b, m));
    }


    @Test
    public void testPassedPawn4() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "8/5ppp/8/5PPP/8/6k1/8/6K1 b - -");

        MoveParser mp = new MoveParser();
        Move m = mp.parseMove("g6", b);
        Assert.assertTrue(MoveGen.genLegalMoves(b).contains(m));
        b.applyMove(m);

        Assert.assertEquals(0, Extend.extendDepth(b, m));
    }

    @Test
    public void testPassedPawn5() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "8/8/1PP2PbP/3r4/8/1Q5p/p5N1/k3K3 b - -");

        MoveParser mp = new MoveParser();
        Move m = mp.parseMove("Rd4", b);
        Assert.assertTrue(MoveGen.genLegalMoves(b).contains(m));
        b.applyMove(m);

        Assert.assertEquals(0, Extend.extendDepth(b, m));
    }

    @Ignore
    @Test
    public void testBlackPushTo2nd() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "8/8/1PP2PbP/3r4/8/1Q5p/p5N1/k3K3 b - -");

        MoveParser mp = new MoveParser();
        Move m = mp.parseMove("h2", b);
        Assert.assertTrue(MoveGen.genLegalMoves(b).contains(m));
        b.applyMove(m);

        Assert.assertEquals(1, Extend.extendDepth(b, m));
    }

    @Test
    public void testPassedPawn7() throws Exception {
        Board b = Board.INSTANCE;
        // Fischer-Larsen 71
        FenParser.setPos(b, "8/4kp2/6p1/7p/P7/2K3P1/7P/8 w - -");

        MoveParser mp = new MoveParser();
        Move a5 = mp.parseMove("a5", b);
        Assert.assertTrue(MoveGen.genLegalMoves(b).contains(a5));
        b.applyMove(a5);

        Assert.assertEquals(0, Extend.extendDepth(b, a5));
    }

    @Test
    public void testPassedPawn8() throws Exception {
        Board b = Board.INSTANCE;
        // Fischer-Larsen 71
        FenParser.setPos(b, "8/4kp2/6p1/7p/P7/2K3P1/7P/8 b - -");

        MoveParser mp = new MoveParser();
        Move f6 = mp.parseMove("f6", b);
        Assert.assertTrue(MoveGen.genLegalMoves(b).contains(f6));
        b.applyMove(f6);

        Assert.assertEquals(0, Extend.extendDepth(b, f6));
    }

    @Test
    public void testPassedPawn9() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "8/p3q1kp/1p2Pnp1/3pQ3/2pP4/1nP3N1/1B4PP/6K1 b - -");
        MoveParser mp = new MoveParser();
        Move m = mp.parseMove("a6", b);
        Assert.assertTrue(MoveGen.genLegalMoves(b).contains(m));
        b.applyMove(m);

        Assert.assertEquals(0, Extend.extendDepth(b, m));
    }

    @Test
    public void testPassedPawn10() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "8/p3q1kp/1p2Pnp1/3pQ3/2pP4/1nP3N1/1B4PP/6K1 b - -");
        MoveParser mp = new MoveParser();
        Move m = mp.parseMove("b5", b);
        Assert.assertTrue(MoveGen.genLegalMoves(b).contains(m));
        b.applyMove(m);

        Assert.assertEquals(0, Extend.extendDepth(b, m));
    }

    @Test
    public void testNewlyCreatedPassedPawn() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "7k/8/6p1/4p3/5P2/8/8/7K w - -");

        MoveParser mp = new MoveParser();
        Move m = mp.parseMove("fxe5", b);
        Assert.assertTrue(MoveGen.genLegalMoves(b).contains(m));
        b.applyMove(m);

        Assert.assertEquals(0, Extend.extendDepth(b, m));

        b.undoLastMove();
        m = mp.parseMove("f5", b);
        Assert.assertTrue(MoveGen.genLegalMoves(b).contains(m));
        b.applyMove(m);

        Assert.assertEquals(0, Extend.extendDepth(b, m));
    }

    @Ignore
    @Test
    public void testRecapture() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "7k/8/8/3r4/3R4/3r4/8/6K1 w - -");
        MoveParser mp = new MoveParser();

        Move m1 = mp.parseMove("Rxd5", b);
        Assert.assertTrue(MoveGen.genLegalMoves(b).contains(m1));
        b.applyMove(m1);

        Move m2 = mp.parseMove("Rxd5", b);
        Assert.assertTrue(MoveGen.genLegalMoves(b).contains(m2));
        b.applyMove(m2);

        Assert.assertEquals(1, Extend.extendDepth(b, m2));
    }

    @Ignore
    @Test
    public void testRecapture2() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "7k/8/8/3r4/3R4/3q4/8/6K1 w - -");
        MoveParser mp = new MoveParser();

        Move m1 = mp.parseMove("Rxd5", b);
        Assert.assertTrue(MoveGen.genLegalMoves(b).contains(m1));
        b.applyMove(m1);

        Move m2 = mp.parseMove("Qxd5", b);
        Assert.assertTrue(MoveGen.genLegalMoves(b).contains(m2));
        b.applyMove(m2);

        Assert.assertEquals(1, Extend.extendDepth(b, m2));
    }

    @Ignore
    @Test
    public void testRecapture3() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "7k/8/8/3b4/3R4/3q4/8/6K1 w - -");
        MoveParser mp = new MoveParser();

        Move m1 = mp.parseMove("Rxd5", b);
        Assert.assertTrue(MoveGen.genLegalMoves(b).contains(m1));
        b.applyMove(m1);

        Move m2 = mp.parseMove("Qxd5", b);
        Assert.assertTrue(MoveGen.genLegalMoves(b).contains(m2));
        b.applyMove(m2);

        Assert.assertEquals(0, Extend.extendDepth(b, m2));
    }

}
