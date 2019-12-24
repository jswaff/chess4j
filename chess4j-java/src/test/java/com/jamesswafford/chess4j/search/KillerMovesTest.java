package com.jamesswafford.chess4j.search;

import org.junit.Test;

import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.pieces.Pawn;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.File.*;
import static com.jamesswafford.chess4j.board.squares.Rank.*;

public class KillerMovesTest {

    @Test
    public void testClear() throws Exception {
        Move m = new Move(Pawn.WHITE_PAWN,Square.valueOf(FILE_E, RANK_2), Square.valueOf(FILE_E,RANK_4));
        KillerMoves.getInstance().addKiller(1, m);
        assertNotNull(KillerMoves.getInstance().getKiller1(1));
        KillerMoves.getInstance().clear();
        assertNull(KillerMoves.getInstance().getKiller1(1));
    }

    @Test
    public void testAddAndRetrieve() {
        Move m = new Move(Pawn.WHITE_PAWN,Square.valueOf(FILE_E, RANK_2), Square.valueOf(FILE_E,RANK_4));
        KillerMoves.getInstance().addKiller(14, m);
        assertEquals(m, KillerMoves.getInstance().getKiller1(14));
    }

    @Test
    public void testReplacementStrategy() {
        Move m = new Move(Pawn.WHITE_PAWN,Square.valueOf(FILE_E, RANK_2), Square.valueOf(FILE_E,RANK_4));
        Move m2 = new Move(Pawn.WHITE_PAWN,Square.valueOf(FILE_E, RANK_2), Square.valueOf(FILE_E,RANK_3));
        Move m3 = new Move(Pawn.WHITE_PAWN,Square.valueOf(FILE_D, RANK_2), Square.valueOf(FILE_D,RANK_3));

        KillerMoves.getInstance().addKiller(10, m);
        KillerMoves.getInstance().addKiller(10, m2);

        // at this point m2 should be in slot 1 and m in slot 2
        assertEquals(m2, KillerMoves.getInstance().getKiller1(10));
        assertEquals(m, KillerMoves.getInstance().getKiller2(10));

        KillerMoves.getInstance().addKiller(10, m3);
        // now m3 should be in slot 1 and m2 in slot 2
        assertEquals(m3, KillerMoves.getInstance().getKiller1(10));
        assertEquals(m2, KillerMoves.getInstance().getKiller2(10));
    }

    @Test
    public void testAddDuplicate() {
        KillerMoves.getInstance().clear();

        Move m = new Move(Pawn.WHITE_PAWN,Square.valueOf(FILE_E, RANK_2), Square.valueOf(FILE_E,RANK_4));
        KillerMoves.getInstance().addKiller(7, m);
        assertEquals(m, KillerMoves.getInstance().getKiller1(7));
        assertNull(KillerMoves.getInstance().getKiller2(7));

        // adding it again should do nothing
        KillerMoves.getInstance().addKiller(7, m);
        assertEquals(m, KillerMoves.getInstance().getKiller1(7));
        assertNull(KillerMoves.getInstance().getKiller2(7));

        // now add a new move
        Move m2 = new Move(Pawn.WHITE_PAWN,Square.valueOf(FILE_E, RANK_2), Square.valueOf(FILE_E,RANK_3));
        KillerMoves.getInstance().addKiller(7, m2);
        assertEquals(m2, KillerMoves.getInstance().getKiller1(7));
        assertEquals(m, KillerMoves.getInstance().getKiller2(7));

        // add new move again
        KillerMoves.getInstance().addKiller(7, m2);
        assertEquals(m2, KillerMoves.getInstance().getKiller1(7));
        assertEquals(m, KillerMoves.getInstance().getKiller2(7));
    }

    @Test
    public void testSwap() {
        Move m = new Move(Pawn.WHITE_PAWN,Square.valueOf(FILE_E, RANK_2), Square.valueOf(FILE_E,RANK_4));
        Move m2 = new Move(Pawn.WHITE_PAWN,Square.valueOf(FILE_E, RANK_2), Square.valueOf(FILE_E,RANK_3));

        KillerMoves.getInstance().addKiller(3, m);
        KillerMoves.getInstance().addKiller(3, m2);

        assertEquals(m2, KillerMoves.getInstance().getKiller1(3));
        assertEquals(m, KillerMoves.getInstance().getKiller2(3));

        // now add m again.  the result should be m in slot 1 and m2 in slot 2
        KillerMoves.getInstance().addKiller(3, m);
        assertEquals(m, KillerMoves.getInstance().getKiller1(3));
        assertEquals(m2, KillerMoves.getInstance().getKiller2(3));
    }
}
