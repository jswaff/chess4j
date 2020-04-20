package com.jamesswafford.chess4j.search;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.movegen.MoveGeneratorImpl;
import com.jamesswafford.chess4j.movegen.MoveGenerator;
import org.junit.Before;
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
    private MoveScorer moveScorer;

    @Before
    public void setUp() {
        // default impls
        moveGenerator = new MoveGeneratorImpl();
        moveScorer = new MVVLVA();
    }

    @Test
    public void nonCapturesAreNotGeneratedUntilNeeded() {
        Board board = new Board( "b2b1r1k/3R1ppp/4qP2/4p1PQ/4P3/5B2/4N1K1/8 w - -");

        // need a mock for the verify() call
        moveGenerator = mock(MoveGenerator.class);
        when(moveGenerator.generatePseudoLegalCaptures(board))
                .thenReturn(MoveGeneratorImpl.genPseudoLegalMoves(board, true, false));

        MoveOrderer mo = new MoveOrderer(board, moveGenerator, moveScorer, null, null);
        mo.selectNextMove();

        assertEquals(CAPTURES_PROMOS, mo.getNextMoveOrderStage());

        verify(moveGenerator, times(1)).generatePseudoLegalCaptures(board);
        verify(moveGenerator, times(0)).generatePseudoLegalNonCaptures(board);
    }

    @Test
    public void nonCapturesPlayedInOrderGenerated() {
        Board board = new Board();

        List<Move> moves = MoveGeneratorImpl.genLegalMoves(board);
        assertEquals(20, moves.size());

        // without a PV or hash the order shouldn't change, since there are no captures
        MoveOrderer mo = new MoveOrderer(board, moveGenerator, moveScorer, null, null);
        List<Move> moves2 = new ArrayList<>();
        for (int i=0;i<20;i++) {
            moves2.add(mo.selectNextMove());
        }

        assertEquals(moves2, moves);
    }

    @Test
    public void capturesBeforeNonCaptures() {

        Board board = new Board();

        MoveGenerator moveGenerator = mock(MoveGenerator.class);
        MoveScorer moveScorer = mock(MoveScorer.class);

        // create two non-caps
        Move e2e4 = new Move(WHITE_PAWN, E2, E4);
        Move e2e3 = new Move(WHITE_PAWN, E2, E3);
        List<Move> noncaps = Arrays.asList(e2e4, e2e3);
        Collections.shuffle(noncaps);

        // create two captures.  these aren't really captures but it doesn't matter.
        Move e3d4 = new Move(WHITE_PAWN, E3, D4, BLACK_PAWN);
        Move d4b6 = new Move(WHITE_BISHOP, D4, B6, BLACK_KNIGHT);

        // and one promotion
        Move a7a8 = new Move(WHITE_PAWN, A7, A8, null, WHITE_QUEEN);
        List<Move> caps = Arrays.asList(e3d4, d4b6, a7a8);
        Collections.shuffle(caps);

        when(moveGenerator.generatePseudoLegalCaptures(board)).thenReturn(caps);
        when(moveGenerator.generatePseudoLegalNonCaptures(board)).thenReturn(noncaps);

        // assign scores to captures
        when(moveScorer.calculateStaticScore(e3d4)).thenReturn(100);
        when(moveScorer.calculateStaticScore(d4b6)).thenReturn(-50);
        when(moveScorer.calculateStaticScore(a7a8)).thenReturn(900);

        MoveOrderer mo = new MoveOrderer(board, moveGenerator, moveScorer, null, null);

        assertEquals(GENCAPS, mo.getNextMoveOrderStage());
        assertEquals(a7a8, mo.selectNextMove());
        assertEquals(CAPTURES_PROMOS, mo.getNextMoveOrderStage());
        assertEquals(e3d4, mo.selectNextMove());
        assertEquals(d4b6, mo.selectNextMove());

        // that's all the captures, now for a noncapture in the order generated
        assertEquals(noncaps.get(0), mo.selectNextMove());
        assertEquals(REMAINING, mo.getNextMoveOrderStage());
        assertEquals(noncaps.get(1), mo.selectNextMove());

        // we should have called the move generator once for caps and once for noncaps
        verify(moveGenerator, times(1)).generatePseudoLegalCaptures(board);
        verify(moveGenerator, times(1)).generatePseudoLegalNonCaptures(board);
    }

    @Test
    public void killersAfterCaps() {

        Board board = new Board("8/7p/5k2/5p2/p1p2P2/Pr1pPK2/1P1R3P/8 b - - "); // WAC-2

        List<Move> noncaps = moveGenerator.generatePseudoLegalNonCaptures(board);


        Move b3b7 = new Move(BLACK_ROOK, B3, B7);
        Move f6e7 = new Move(BLACK_KING, F6, E7);

        assertTrue(noncaps.contains(b3b7));
        assertTrue(noncaps.contains(f6e7));

        MoveOrderer mo = new MoveOrderer(board, moveGenerator, moveScorer, b3b7, f6e7);

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

}
