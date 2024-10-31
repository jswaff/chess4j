package dev.jamesswafford.chess4j.hash;

import java.util.List;

import dev.jamesswafford.chess4j.board.Undo;
import io.vavr.Tuple2;
import org.junit.Before;
import org.junit.Test;

import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.Move;
import dev.jamesswafford.chess4j.movegen.MagicBitboardMoveGenerator;
import dev.jamesswafford.chess4j.io.EPDParser;
import dev.jamesswafford.chess4j.io.MoveParser;

import static org.junit.Assert.*;

import static dev.jamesswafford.chess4j.pieces.Pawn.*;
import static dev.jamesswafford.chess4j.board.squares.Square.*;

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

        // shouldn't be anything
        PawnTranspositionTableEntry entry = ptable.probe(board);
        assertNull(entry);

        // now store and reload
        ptable.store(board,100, 110);
        entry = ptable.probe(board);
        assertNotNull(entry);

        PawnTranspositionTableEntry expected = new PawnTranspositionTableEntry(board.getPawnKey(),100, 110);
        assertEquals(expected, entry);

        // now make move and reprobe
        Move m = new Move(WHITE_PAWN, E2, E4);
        Undo u = board.applyMove(m);
        entry = ptable.probe(board);
        assertNull(entry);

        // finally undo move and reprobe again
        board.undoMove(u);
        entry = ptable.probe(board);
        assertNotNull(entry);
        assertEquals(expected, entry);
    }

    @Test
    public void clearTable() throws Exception {
        EPDParser.setPos(board, "3qrrk1/1pp2pp1/1p2bn1p/5N2/2P5/P1P3B1/1P4PP/2Q1RRK1 w - - bm Nxg7; id \"WAC.090\";");

        List<Move> moves = MagicBitboardMoveGenerator.genLegalMoves(board);
        MoveParser mp = new MoveParser();
        Move b2b4 = mp.parseMove("b2b4", board);
        assertTrue(moves.contains(b2b4));

        ptable.store(board, 77, 80);
        PawnTranspositionTableEntry found = ptable.probe(board);
        assertNotNull(found);
        assertEquals(new Tuple2<>(77, 80), found.getScore());
        assertEquals(board.getPawnKey(), found.getZobristKey());

        ptable.clear();
        found = ptable.probe(board);
        assertNull(found);
    }

    @Test
    public void overwrite() throws Exception {
        EPDParser.setPos(board, "8/k7/p7/3Qp2P/n1P5/3KP3/1q6/8 b - - bm e4+; id \"WAC.094\";");

        List<Move> moves = MagicBitboardMoveGenerator.genLegalMoves(board);
        MoveParser mp = new MoveParser();
        Move e5e4 = mp.parseMove("e4", board);
        assertTrue(moves.contains(e5e4));
        Move a6a5 = mp.parseMove("a5", board);
        assertTrue(moves.contains(a6a5));

        ptable.store(board, 71, -17);
        PawnTranspositionTableEntry found = ptable.probe(board);
        PawnTranspositionTableEntry entry = new PawnTranspositionTableEntry(board.getPawnKey(),71, -17);
        assertEquals(entry, found);

        // overwrite
        ptable.store(board, 59, -2);
        found = ptable.probe(board);
        assertNotEquals(entry, found);
        entry = new PawnTranspositionTableEntry(board.getPawnKey(),59, -2);
        assertEquals(entry, found);

        // use different key to store new entry
        Undo u = board.applyMove(e5e4);
        found = ptable.probe(board);
        assertNull(found);
        ptable.store(board, 45, 3);

        // now make sure we didn't overwrite lbe
        board.undoMove(u);
        found = ptable.probe(board);
        assertEquals(entry, found);

        board.applyMove(e5e4);
        PawnTranspositionTableEntry entry2 = new PawnTranspositionTableEntry(board.getPawnKey(),45, 3);
        found = ptable.probe(board);
        assertEquals(entry2, found);
    }

    @Test
    public void resize() {

        PawnTranspositionTable ptt = new PawnTranspositionTable(1024);
        assertEquals(1024 / 16, ptt.tableCapacity());

        int fourMb = 4 * 1024 * 1024;
        ptt.resizeTable(fourMb);

        assertEquals(fourMb / 16, ptt.tableCapacity());
    }

}
