package com.jamesswafford.chess4j.hash;

import java.util.List;

import com.jamesswafford.chess4j.board.Undo;
import org.junit.Before;
import org.junit.Test;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.movegen.MagicBitboardMoveGenerator;
import com.jamesswafford.chess4j.io.EPDParser;
import com.jamesswafford.chess4j.io.MoveParser;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.pieces.Pawn.*;
import static com.jamesswafford.chess4j.board.squares.Square.*;

public class PawnTranspositionTableTest {

    private final PawnTranspositionTable ptable = new PawnTranspositionTable();
    private final Board board = new Board();

    @Before
    public void setUp() {
        ptable.clear();
    }

    @Test
    public void storeAndProbe() {
        board.resetBoard();
        long key = Zobrist.calculatePawnKey(board);

        // shouldn't be anything
        PawnTranspositionTableEntry tte = ptable.probe(key);
        assertNull(tte);

        // now store and reload
        ptable.store(key,100);
        tte = ptable.probe(key);
        assertNotNull(tte);

        PawnTranspositionTableEntry lbe = new PawnTranspositionTableEntry(key,100);
        assertEquals(lbe, tte);

        // now make move and reprobe
        Move m = new Move(WHITE_PAWN, E2, E4);
        Undo u = board.applyMove(m);
        key = Zobrist.calculatePawnKey(board);
        tte = ptable.probe(key);
        assertNull(tte);

        // finally undo move and reprobe again
        board.undoMove(u);
        key = Zobrist.calculatePawnKey(board);
        tte = ptable.probe(key);
        assertNotNull(tte);
        assertEquals(lbe, tte);
    }

    @Test
    public void clearTable() throws Exception {
        EPDParser.setPos(board, "3qrrk1/1pp2pp1/1p2bn1p/5N2/2P5/P1P3B1/1P4PP/2Q1RRK1 w - - bm Nxg7; id \"WAC.090\";");

        long key = Zobrist.calculatePawnKey(board);

        List<Move> moves = MagicBitboardMoveGenerator.genLegalMoves(board);
        MoveParser mp = new MoveParser();
        Move b2b4 = mp.parseMove("b2b4", board);
        assertTrue(moves.contains(b2b4));

        ptable.store(key, 77);
        PawnTranspositionTableEntry pte = ptable.probe(key);
        assertNotNull(pte);
        assertEquals(77, pte.getScore());
        assertEquals(key, pte.getZobristKey());

        ptable.clear();
        pte = ptable.probe(key);
        assertNull(pte);
    }

    @Test
    public void overwrite() throws Exception {
        EPDParser.setPos(board, "8/k7/p7/3Qp2P/n1P5/3KP3/1q6/8 b - - bm e4+; id \"WAC.094\";");
        long key = Zobrist.calculatePawnKey(board);

        List<Move> moves = MagicBitboardMoveGenerator.genLegalMoves(board);
        MoveParser mp = new MoveParser();
        Move e5e4 = mp.parseMove("e4", board);
        assertTrue(moves.contains(e5e4));
        ptable.store(key, 71);
        PawnTranspositionTableEntry tte = ptable.probe(key);
        PawnTranspositionTableEntry lbe = new PawnTranspositionTableEntry(key,71);
        assertEquals(lbe, tte);

        // overwrite
        ptable.store(key, 59);
        tte = ptable.probe(key);
        assertNotEquals(lbe, tte);
        lbe = new PawnTranspositionTableEntry(key,59);
        assertEquals(lbe, tte);

        // use different key to store new entry
        long key2 = ~key;
        tte = ptable.probe(key2);
        assertNull(tte);
        ptable.store(key2, 45);

        // now make sure we didn't overwrite lbe
        tte = ptable.probe(key);
        assertEquals(lbe, tte);

        PawnTranspositionTableEntry lbe2 = new PawnTranspositionTableEntry(key2,45);
        tte = ptable.probe(key2);
        assertEquals(lbe2, tte);
    }

    @Test
    public void resize() {

        PawnTranspositionTable ptt = new PawnTranspositionTable(1024);
        assertEquals(1024 / 12, ptt.tableCapacity());

        int fourMb = 4 * 1024 * 1024;
        ptt.resizeTable(fourMb);

        assertEquals(fourMb / 12, ptt.tableCapacity());
    }

}
