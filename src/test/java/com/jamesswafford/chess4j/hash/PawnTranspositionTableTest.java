package com.jamesswafford.chess4j.hash;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.MoveGen;
import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.io.EPDParser;
import com.jamesswafford.chess4j.io.MoveParser;
import com.jamesswafford.chess4j.pieces.Pawn;

public class PawnTranspositionTableTest {

    PawnTranspositionTable ptable = new PawnTranspositionTable();
    Board board = Board.INSTANCE;

    @Before
    public void setUp() {
        ptable.clear();
    }

    @Test
    public void test1() {
        board.resetBoard();
        long key = Zobrist.getPawnKey(board);
        // shouldn't be anything
        PawnTranspositionTableEntry tte = ptable.probe(key);
        Assert.assertNull(tte);

        // now store and reload
        ptable.store(key,100);
        tte = ptable.probe(key);
        Assert.assertNotNull(tte);

        PawnTranspositionTableEntry lbe = new PawnTranspositionTableEntry(key,100);
        Assert.assertEquals(lbe, tte);

        // now make move and reprobe
        Move m = new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_2),Square.valueOf(File.FILE_E,Rank.RANK_4));
        board.applyMove(m);
        key = Zobrist.getPawnKey(board);
        tte = ptable.probe(key);
        Assert.assertNull(tte);

        // finally undo move and reprobe again
        board.undoLastMove();
        key = Zobrist.getPawnKey(board);
        tte = ptable.probe(key);
        Assert.assertNotNull(tte);
        Assert.assertEquals(lbe, tte);
    }

    @Test
    public void testClear() throws Exception {
        EPDParser.setPos(board, "3qrrk1/1pp2pp1/1p2bn1p/5N2/2P5/P1P3B1/1P4PP/2Q1RRK1 w - - bm Nxg7; id \"WAC.090\";");
        long key = Zobrist.getPawnKey(board);

        List<Move> moves = MoveGen.genLegalMoves(board);
        MoveParser mp = new MoveParser();
        Move b2b4 = mp.parseMove("b2b4", board);
        Assert.assertTrue(moves.contains(b2b4));

        ptable.store(key, 77);
        PawnTranspositionTableEntry pte = ptable.probe(key);
        Assert.assertNotNull(pte);
        Assert.assertEquals(77, pte.getScore());
        Assert.assertEquals(key, pte.getZobristKey());

        ptable.clear();
        pte = ptable.probe(key);
        Assert.assertNull(pte);;
    }

    @Test
    public void testOverwrite() throws Exception {
        EPDParser.setPos(board, "8/k7/p7/3Qp2P/n1P5/3KP3/1q6/8 b - - bm e4+; id \"WAC.094\";");
        long key = Zobrist.getPawnKey(board);

        List<Move> moves = MoveGen.genLegalMoves(board);
        MoveParser mp = new MoveParser();
        Move e5e4 = mp.parseMove("e4", board);
        Assert.assertTrue(moves.contains(e5e4));
        ptable.store(key, 71);
        PawnTranspositionTableEntry tte = ptable.probe(key);
        PawnTranspositionTableEntry lbe = new PawnTranspositionTableEntry(key,71);
        Assert.assertEquals(lbe, tte);

        // overwrite
        ptable.store(key, 59);
        tte = ptable.probe(key);
        Assert.assertFalse(lbe.equals(tte));
        lbe = new PawnTranspositionTableEntry(key,59);
        Assert.assertTrue(lbe.equals(tte));

        // use different key to store new entry
        long key2 = ~key;
        tte = ptable.probe(key2);
        Assert.assertNull(tte);
        ptable.store(key2, 45);

        // now make sure we didn't overwrite lbe
        tte = ptable.probe(key);
        Assert.assertEquals(lbe, tte);

        PawnTranspositionTableEntry lbe2 = new PawnTranspositionTableEntry(key2,45);
        tte = ptable.probe(key2);
        Assert.assertEquals(lbe2, tte);

    }
}
