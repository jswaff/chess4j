package com.jamesswafford.chess4j.utils;

import junit.framework.Assert;

import org.junit.Test;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.io.FenParser;
import com.jamesswafford.chess4j.pieces.Pawn;
import com.jamesswafford.chess4j.pieces.Piece;

public class PawnUtilsTest {

    @Test
    public void testPassedPawnInitialPos() {
        Board b = Board.INSTANCE;
        b.resetBoard();

        int n = 0;
        for (Square sq : Square.allSquares()) {
            Piece p = b.getPiece(sq);
            if (p instanceof Pawn) {
                Assert.assertFalse(PawnUtils.isPassedPawn(b,sq,p.isWhite()));
                n++;
            }
        }

        Assert.assertEquals(16, n);
    }

    @Test
    public void testPassedPawnWikiPos() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "7k/8/7p/1P2Pp1P/2Pp1PP1/8/8/7K w - -");

        Assert.assertTrue(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_B,Rank.RANK_5),true));
        Assert.assertTrue(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_C,Rank.RANK_4),true));
        Assert.assertTrue(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_D,Rank.RANK_4),false));
        Assert.assertTrue(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_E,Rank.RANK_5),true));

        Assert.assertFalse(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_F,Rank.RANK_5),false));
        Assert.assertFalse(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_F,Rank.RANK_4),true));
        Assert.assertFalse(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_G,Rank.RANK_4),true));
        Assert.assertFalse(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_H,Rank.RANK_5),true));
        Assert.assertFalse(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_H,Rank.RANK_6),false));
    }

    @Test
    public void testPassedPawnWikiPos2() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "8/5ppp/8/5PPP/8/6k1/8/6K1 w - -");

        Assert.assertFalse(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_F,Rank.RANK_7),false));
        Assert.assertFalse(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_G,Rank.RANK_7),false));
        Assert.assertFalse(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_H,Rank.RANK_7),false));

        Assert.assertFalse(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_F,Rank.RANK_5),true));
        Assert.assertFalse(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_G,Rank.RANK_5),true));
        Assert.assertFalse(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_H,Rank.RANK_5),true));
    }

    @Test
    public void tesPassedPawnWikiPos3() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "8/8/1PP2PbP/3r4/8/1Q5p/p5N1/k3K3 b - -");

        Assert.assertTrue(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_B,Rank.RANK_6),true));
        Assert.assertTrue(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_C,Rank.RANK_6),true));
        Assert.assertTrue(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_F,Rank.RANK_6),true));
        Assert.assertTrue(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_H,Rank.RANK_6),true));
        Assert.assertTrue(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_A,Rank.RANK_2),false));
        Assert.assertTrue(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_H,Rank.RANK_3),false));
    }

    @Test
    public void testPassedPawnWikiPos4() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "k7/b1P5/KP6/6q1/8/8/8/4n3 b - -");

        Assert.assertTrue(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_B,Rank.RANK_6),true));
        Assert.assertTrue(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_C,Rank.RANK_7),true));
    }

    @Test
    public void testLevinfishSmyslov57() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "R7/6k1/P5p1/5p1p/5P1P/r5P1/5K2/8 w - -");

        Assert.assertTrue(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_A,Rank.RANK_6),true));
        Assert.assertFalse(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_G,Rank.RANK_6),false));
        Assert.assertFalse(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_F,Rank.RANK_5),false));
        Assert.assertFalse(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_H,Rank.RANK_5),false));
        Assert.assertFalse(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_F,Rank.RANK_4),true));
        Assert.assertFalse(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_H,Rank.RANK_4),true));
        Assert.assertFalse(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_G,Rank.RANK_3),true));
    }

    @Test
    public void testFischerLarsen71() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "8/4kp2/6p1/7p/P7/2K3P1/7P/8 b - -");

        Assert.assertFalse(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_F,Rank.RANK_7),false));
        Assert.assertFalse(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_G,Rank.RANK_6),false));
        Assert.assertFalse(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_H,Rank.RANK_5),false));

        Assert.assertTrue(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_A,Rank.RANK_4),true));
        Assert.assertFalse(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_G,Rank.RANK_3),true));
        Assert.assertFalse(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_H,Rank.RANK_5),true));
    }

    @Test
    public void testBotvinnikCapablanca38() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "8/p3q1kp/1p2Pnp1/3pQ3/2pP4/1nP3N1/1B4PP/6K1 w - -");

        Assert.assertTrue(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_A,Rank.RANK_7),false));
        Assert.assertFalse(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_H,Rank.RANK_7),false));
        Assert.assertFalse(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_B,Rank.RANK_6),false));
        Assert.assertFalse(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_G,Rank.RANK_6),false));
        Assert.assertFalse(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_D,Rank.RANK_5),false));

        Assert.assertFalse(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_C,Rank.RANK_4),false));
        Assert.assertFalse(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_D,Rank.RANK_4),true));
        Assert.assertFalse(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_C,Rank.RANK_3),true));
        Assert.assertFalse(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_G,Rank.RANK_2),true));
        Assert.assertFalse(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_H,Rank.RANK_2),true));
    }

    @Test
    public void testIsolatedPawn() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "k7/p1p3p1/3p3p/1P5P/1PP1P1P1/8/8/K7 w - - 0 1");

        // white's pawn on the E file and black's pawn on the A file are isolated
        Assert.assertTrue(PawnUtils.isIsolated(b,Square.valueOf(File.FILE_E, Rank.RANK_4),true));
        Assert.assertFalse(PawnUtils.isDoubled(b,Square.valueOf(File.FILE_E, Rank.RANK_4),true));
        Assert.assertFalse(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_E, Rank.RANK_4),true));

        Assert.assertTrue(PawnUtils.isIsolated(b,Square.valueOf(File.FILE_A, Rank.RANK_7),false));
        Assert.assertFalse(PawnUtils.isDoubled(b,Square.valueOf(File.FILE_A, Rank.RANK_7),false));
        Assert.assertFalse(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_A, Rank.RANK_7),false));

        Assert.assertFalse(PawnUtils.isIsolated(b,Square.valueOf(File.FILE_C, Rank.RANK_7),true));
        Assert.assertFalse(PawnUtils.isDoubled(b,Square.valueOf(File.FILE_C, Rank.RANK_7),false));
        Assert.assertFalse(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_C, Rank.RANK_7),false));

        Assert.assertFalse(PawnUtils.isIsolated(b,Square.valueOf(File.FILE_G, Rank.RANK_4),true));
        Assert.assertFalse(PawnUtils.isDoubled(b,Square.valueOf(File.FILE_G, Rank.RANK_4),true));
        Assert.assertFalse(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_G, Rank.RANK_4),true));
    }

    @Test
    public void testDoubled() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "k7/p1p3p1/3p3p/1P5P/1PP1P1P1/8/8/K7 w - - 0 1");

        Assert.assertTrue(PawnUtils.isDoubled(b,Square.valueOf(File.FILE_B, Rank.RANK_5),true));
        Assert.assertFalse(PawnUtils.isIsolated(b,Square.valueOf(File.FILE_B, Rank.RANK_5),true));
        Assert.assertFalse(PawnUtils.isPassedPawn(b,Square.valueOf(File.FILE_B, Rank.RANK_5),true));

        Assert.assertFalse(PawnUtils.isIsolated(b,Square.valueOf(File.FILE_B, Rank.RANK_4),true));
        Assert.assertTrue(PawnUtils.isDoubled(b,Square.valueOf(File.FILE_B, Rank.RANK_4),true));
        Assert.assertFalse(PawnUtils.isIsolated(b,Square.valueOf(File.FILE_B, Rank.RANK_4),true));
    }

}
