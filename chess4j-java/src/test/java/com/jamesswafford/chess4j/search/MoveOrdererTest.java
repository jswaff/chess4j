package com.jamesswafford.chess4j.search;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.io.DrawBoard;
import com.jamesswafford.chess4j.io.MoveParser;
import com.jamesswafford.chess4j.movegen.MagicBitboardMoveGenerator;
import com.jamesswafford.chess4j.movegen.MoveGenerator;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import static com.jamesswafford.chess4j.search.MoveOrderStage.*;
import static com.jamesswafford.chess4j.pieces.Bishop.*;
import static com.jamesswafford.chess4j.pieces.King.*;
import static com.jamesswafford.chess4j.pieces.Knight.*;
import static com.jamesswafford.chess4j.pieces.Pawn.*;
import static com.jamesswafford.chess4j.pieces.Queen.*;
import static com.jamesswafford.chess4j.pieces.Rook.*;
import static com.jamesswafford.chess4j.board.squares.Square.*;

public class MoveOrdererTest {

    private MoveGenerator moveGenerator;

    @Before
    public void setUp() {
        moveGenerator = new MagicBitboardMoveGenerator();
    }

    @Test
    public void pv() {

        // test that the PV move is played first
        Board board = new Board("b2b1r1k/3R1ppp/4qP2/4p1PQ/4P3/5B2/4N1K1/8 w - -");
        List<Move> moves = moveGenerator.generateLegalMoves(board);
        Move pvMove = moves.get(5); // no particular reason
        MoveOrderer mo = new MoveOrderer(board, moveGenerator, pvMove, null, null, null,
                true);
        assertEquals(pvMove, mo.selectNextMove());
        assertEquals(HASH_MOVE, mo.getNextMoveOrderStage());
    }

    @Test
    public void noPvThenHash() {
        Board board = new Board("1R6/1brk2p1/4p2p/p1P1Pp2/P7/6P1/1P4P1/2R3K1 w - -");
        List<Move> moves = moveGenerator.generateLegalMoves(board);

        // randomly make the 5th move the hash move
        Move hashMove = moves.get(4);
        MoveOrderer mo = new MoveOrderer(board, moveGenerator, null, hashMove, null, null,
                true);
        Move nextMv = mo.selectNextMove();
        assertEquals(GENCAPS, mo.getNextMoveOrderStage());
        assertEquals(hashMove, nextMv);
    }

    @Test
    public void pvThenHash() {
        Board board = new Board("6k1/p4p1p/1p3np1/2q5/4p3/4P1N1/PP3PPP/3Q2K1 w - -");
        List<Move> moves = moveGenerator.generateLegalMoves(board);

        // make move 4 the PV move and move 2 the hash move
        Move pvMove = moves.get(4);
        Move hashMove = moves.get(2);

        MoveOrderer mo = new MoveOrderer(board, moveGenerator, pvMove, hashMove, null, null,
                true);
        Move nextMv = mo.selectNextMove();
        assertEquals(HASH_MOVE, mo.getNextMoveOrderStage());
        assertEquals(pvMove, nextMv);

        nextMv = mo.selectNextMove();
        assertEquals(GENCAPS, mo.getNextMoveOrderStage());
        assertEquals(hashMove, nextMv);
    }

    @Test
    public void pvAndHashSameMove() {
        Board board = new Board();
        List<Move> moves = moveGenerator.generateLegalMoves(board);
        Collections.shuffle(moves);

        Move pv = moves.get(9);
        MoveOrderer mo = new MoveOrderer(board, moveGenerator, pv, pv,null,null,
                true);
        Move nextMv = mo.selectNextMove();
        assertEquals(pv, nextMv);

        for (int i=1;i<20;i++) {
            nextMv = mo.selectNextMove();
            assertNotNull(nextMv);
            assertNotEquals(pv, nextMv);
        }

        assertNull(mo.selectNextMove());
    }

    @Test
    public void pvThenHashThenCaptures() {
        Board board = new Board("6R1/kp6/8/1KpP4/8/8/8/6B1 w - c6");

        List<Move> moves = moveGenerator.generateLegalMoves(board);
        Move d5c6 = new Move(WHITE_PAWN, D5, C6, BLACK_PAWN, true);
        Move b5c5 = new Move(WHITE_KING, B5, C5, BLACK_PAWN);
        Move g1c5 = new Move(WHITE_BISHOP, G1, C5, BLACK_PAWN);
        Move g8g7 = new Move(WHITE_ROOK, G8, G7);

        assertTrue(moves.contains(d5c6));
        assertTrue(moves.contains(b5c5));
        assertTrue(moves.contains(g1c5));
        assertTrue(moves.contains(g8g7));

        MoveOrderer mo = new MoveOrderer(board, moveGenerator, g8g7, d5c6,null,null,
                true);
        Move nextMv = mo.selectNextMove();
        assertEquals(g8g7, nextMv);
        nextMv = mo.selectNextMove();
        assertEquals(d5c6, nextMv);
        nextMv = mo.selectNextMove();
        assertTrue(g1c5.equals(nextMv) || b5c5.equals(nextMv));
        nextMv = mo.selectNextMove();
        assertTrue(g1c5.equals(nextMv) || b5c5.equals(nextMv));
    }

    @Test
    public void goodCapturesThenNonCapturesThenBadCaptures() {

        Board board = new Board("5k1r/8/4R2N/5P2/p7/1N2r2Q/2p5/1B2BK2 b - -");
        DrawBoard.drawBoard(board);

        List<Move> moves = moveGenerator.generateLegalMoves(board);
        moves.forEach(System.out::println);

        MoveOrderer mo = new MoveOrderer(board, moveGenerator, null, null,null,null,
                true);

        // the capturing promotions should be first
        MoveParser parser = new MoveParser();
        assertEquals(parser.parseMove("c2b1q", board), mo.selectNextMove());
        assertEquals(parser.parseMove("c2b1r", board), mo.selectNextMove());
        assertEquals(parser.parseMove("c2b1b", board), mo.selectNextMove());
        assertEquals(parser.parseMove("c2b1n", board), mo.selectNextMove());

        // non-capturing promotions next
        assertEquals(parser.parseMove("c2c1q", board), mo.selectNextMove());
        assertEquals(parser.parseMove("c2c1r", board), mo.selectNextMove());
        assertEquals(parser.parseMove("c2c1b", board), mo.selectNextMove());
        assertEquals(parser.parseMove("c2c1n", board), mo.selectNextMove());

        // Queen captures
        assertEquals(parser.parseMove("Rxh3", board), mo.selectNextMove());
        assertEquals(parser.parseMove("Rxe6", board), mo.selectNextMove());

        // MVV/LVA says Rxe1 (capturing bishop) should be next, but it's a losing capture

        // Knight captures  RxN is a losing capture due to queen defender
        assertEquals(parser.parseMove("axb3", board), mo.selectNextMove());
        assertEquals(parser.parseMove("Rxb3", board), mo.selectNextMove());

        // those include one pawn push, 9 rook moves, and 5 king moves, total 13 moves
        for (int i=0;i<15;i++) {
            Move nextMv = mo.selectNextMove();
            assertNotNull(nextMv);
            assertNull(nextMv.captured());
        }

        // now the losing capture
        assertEquals(parser.parseMove("Rxe1", board), mo.selectNextMove()); // rook for bishop
        assertEquals(parser.parseMove("Rxh6", board), mo.selectNextMove()); // rook for knight

        // no more moves
        assertNull(mo.selectNextMove());
    }

    // TODO: killers after WINNING captures
    @Ignore
    @Test
    public void killersAfterCaps() {

        Board board = new Board("8/7p/5k2/5p2/p1p2P2/Pr1pPK2/1P1R3P/8 b - - "); // WAC-2

        List<Move> noncaps = moveGenerator.generatePseudoLegalNonCaptures(board);

        Move b3b7 = new Move(BLACK_ROOK, B3, B7);
        Move f6e7 = new Move(BLACK_KING, F6, E7);

        assertTrue(noncaps.contains(b3b7));
        assertTrue(noncaps.contains(f6e7));

        MoveOrderer mo = new MoveOrderer(board, moveGenerator, null, null, b3b7, f6e7,
                true);

        // first two moves should be the captures Rxa3 and Rxb2
        assertNotNull(mo.selectNextMove().captured());
        assertNotNull(mo.selectNextMove().captured());

        // the next move should be our first killer
        assertEquals(b3b7, mo.selectNextMove());

        // and then our second killer
        assertEquals(f6e7, mo.selectNextMove());

        List<Move> selectedNonCaps = new ArrayList<>();
        selectedNonCaps.add(b3b7);
        selectedNonCaps.add(f6e7);

        // the killers shouldn't be selected again
        Move nextMv = mo.selectNextMove();
        while(nextMv != null) {
            assertNotEquals(b3b7, nextMv);
            assertNotEquals(f6e7, nextMv);
            assertTrue(noncaps.contains(nextMv));
            selectedNonCaps.add(nextMv);
            nextMv = mo.selectNextMove();
        }

        // and all noncaps should have been selected
        assertTrue(selectedNonCaps.containsAll(noncaps));
    }

    @Test
    public void nonCapturesAreNotGeneratedUntilNeeded() {
        Board board = new Board( "b2b1r1k/3R1ppp/4qP2/4p1PQ/4P3/5B2/4N1K1/8 w - -");

        // need a mock for the verify() call
        moveGenerator = mock(MoveGenerator.class);
        when(moveGenerator.generatePseudoLegalCaptures(board))
                .thenReturn(MagicBitboardMoveGenerator.genPseudoLegalMoves(board, true, false));

        MoveOrderer mo = new MoveOrderer(board, moveGenerator, null, null, null,
                null,true);
        mo.selectNextMove();

        assertEquals(GOOD_CAPTURES_PROMOS, mo.getNextMoveOrderStage());

        verify(moveGenerator, times(1)).generatePseudoLegalCaptures(board);
        verify(moveGenerator, times(0)).generatePseudoLegalNonCaptures(board);
    }

    @Test
    public void nonCapturesGeneratedOnlyWhenRequested() {

        Board board = new Board(); // no captures possible

        moveGenerator = mock(MoveGenerator.class);

        MoveOrderer mo = new MoveOrderer(board, moveGenerator, null, null, null,
                null,true);
        mo.selectNextMove();

        /// this time do not request non-captures
        mo = new MoveOrderer(board, moveGenerator, null, null, null, null,
                false);
        mo.selectNextMove();

        verify(moveGenerator, times(2)).generatePseudoLegalCaptures(board);
        verify(moveGenerator, times(1)).generatePseudoLegalNonCaptures(board);
    }

    @Test
    public void nonCapturesPlayedInOrderGenerated() {
        Board board = new Board();

        List<Move> moves = MagicBitboardMoveGenerator.genLegalMoves(board);
        assertEquals(20, moves.size());

        // without a PV or hash the order shouldn't change, since there are no captures
        MoveOrderer mo = new MoveOrderer(board, moveGenerator, null, null, null,
                null,true);
        List<Move> moves2 = new ArrayList<>();
        for (int i=0;i<20;i++) {
            moves2.add(mo.selectNextMove());
        }

        assertEquals(moves2, moves);
    }

    @Test
    public void movesAreNotRepeated() {

        // the PV is not repeated
        Board board = new Board("8/7p/5k2/5p2/p1p2P2/Pr1pPK2/1P1R3P/8 b - - "); // WAC-2

        // choose a non-capture so we can use it again as a killer
        Move pvMove = moveGenerator.generateLegalMoves(board).stream()
                .filter(mv -> mv.captured() == null)
                .peek(System.out::println)
                .findFirst().get();

        List<Move> noncaps = moveGenerator.generatePseudoLegalNonCaptures(board);
        Collections.shuffle(noncaps);

        MoveOrderer mo = new MoveOrderer(board, moveGenerator, pvMove, null, pvMove,
                noncaps.get(0),true);
        List<Move> selected = new ArrayList<>();
        Move selectedMv;
        while ((selectedMv = mo.selectNextMove()) != null) {
            selected.add(selectedMv);
        }
        assertEquals(1L, selected.stream().filter(mv -> mv.equals(pvMove)).count());

        assertEquals(selected.stream().distinct().count(), selected.size());
    }

}
