package com.jamesswafford.chess4j.search;

import junit.framework.Assert;

import org.junit.Test;

import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.pieces.Pawn;

public class KillerMovesTest {

    @Test
    public void testClear() throws Exception {
        Move m = new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_2), Square.valueOf(File.FILE_E,Rank.RANK_4));
        KillerMoves.getInstance().addKiller(1, m);
        Assert.assertNotNull(KillerMoves.getInstance().getKiller1(1));
        KillerMoves.getInstance().clear();
        Assert.assertNull(KillerMoves.getInstance().getKiller1(1));
    }

    @Test
    public void testAddAndRetrieve() {
        Move m = new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_2), Square.valueOf(File.FILE_E,Rank.RANK_4));
        KillerMoves.getInstance().addKiller(14, m);
        Assert.assertEquals(m, KillerMoves.getInstance().getKiller1(14));
    }

    @Test
    public void testReplacementStrategy() {
        Move m = new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_2), Square.valueOf(File.FILE_E,Rank.RANK_4));
        Move m2 = new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_2), Square.valueOf(File.FILE_E,Rank.RANK_3));
        Move m3 = new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_D, Rank.RANK_2), Square.valueOf(File.FILE_D,Rank.RANK_3));

        KillerMoves.getInstance().addKiller(10, m);
        KillerMoves.getInstance().addKiller(10, m2);

        // at this point m2 should be in slot 1 and m in slot 2
        Assert.assertEquals(m2, KillerMoves.getInstance().getKiller1(10));
        Assert.assertEquals(m, KillerMoves.getInstance().getKiller2(10));

        KillerMoves.getInstance().addKiller(10, m3);
        // now m3 should be in slot 1 and m2 in slot 2
        Assert.assertEquals(m3, KillerMoves.getInstance().getKiller1(10));
        Assert.assertEquals(m2, KillerMoves.getInstance().getKiller2(10));
    }

    @Test
    public void testAddDuplicate() {
        KillerMoves.getInstance().clear();

        Move m = new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_2), Square.valueOf(File.FILE_E,Rank.RANK_4));
        KillerMoves.getInstance().addKiller(7, m);
        Assert.assertEquals(m, KillerMoves.getInstance().getKiller1(7));
        Assert.assertNull(KillerMoves.getInstance().getKiller2(7));

        // adding it again should do nothing
        KillerMoves.getInstance().addKiller(7, m);
        Assert.assertEquals(m, KillerMoves.getInstance().getKiller1(7));
        Assert.assertNull(KillerMoves.getInstance().getKiller2(7));

        // now add a new move
        Move m2 = new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_2), Square.valueOf(File.FILE_E,Rank.RANK_3));
        KillerMoves.getInstance().addKiller(7, m2);
        Assert.assertEquals(m2, KillerMoves.getInstance().getKiller1(7));
        Assert.assertEquals(m, KillerMoves.getInstance().getKiller2(7));

        // add new move again
        KillerMoves.getInstance().addKiller(7, m2);
        Assert.assertEquals(m2, KillerMoves.getInstance().getKiller1(7));
        Assert.assertEquals(m, KillerMoves.getInstance().getKiller2(7));
    }

    @Test
    public void testSwap() {
        Move m = new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_2), Square.valueOf(File.FILE_E,Rank.RANK_4));
        Move m2 = new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_2), Square.valueOf(File.FILE_E,Rank.RANK_3));

        KillerMoves.getInstance().addKiller(3, m);
        KillerMoves.getInstance().addKiller(3, m2);

        Assert.assertEquals(m2, KillerMoves.getInstance().getKiller1(3));
        Assert.assertEquals(m, KillerMoves.getInstance().getKiller2(3));

        // now add m again.  the result should be m in slot 1 and m2 in slot 2
        KillerMoves.getInstance().addKiller(3, m);
        Assert.assertEquals(m, KillerMoves.getInstance().getKiller1(3));
        Assert.assertEquals(m2, KillerMoves.getInstance().getKiller2(3));
    }
}
