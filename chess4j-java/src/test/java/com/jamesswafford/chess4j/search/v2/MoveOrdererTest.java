package com.jamesswafford.chess4j.search.v2;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.movegen.MoveGen;
import com.jamesswafford.chess4j.movegen.MoveGenerator;
import com.jamesswafford.chess4j.search.MVVLVA;
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
        moveGenerator = new MoveGen();
        moveScorer = new MVVLVA();
    }

    @Test
    public void nonCapturesAreNotGeneratedUntilNeeded() {
        Board board = new Board( "b2b1r1k/3R1ppp/4qP2/4p1PQ/4P3/5B2/4N1K1/8 w - -");

        // need a mock for the verify() call
        moveGenerator = mock(MoveGenerator.class);
        when(moveGenerator.generatePseudoLegalCaptures(board))
                .thenReturn(MoveGen.genPseudoLegalMoves(board, true, false));

        MoveOrderer mo = new MoveOrderer(board, moveGenerator, moveScorer);
        mo.selectNextMove();

        assertEquals(CAPTURES_PROMOS, mo.getNextMoveOrderStage());

        verify(moveGenerator, times(1)).generatePseudoLegalCaptures(board);
        verify(moveGenerator, times(0)).generatePseudoLegalNonCaptures(board);
    }

    @Test
    public void nonCapturesPlayedInOrderGenerated() {
        Board board = new Board();

        List<Move> moves = MoveGen.genLegalMoves(board);
        assertEquals(20, moves.size());

        // without a PV or hash the order shouldn't change, since there are no captures
        MoveOrderer mo = new MoveOrderer(board, moveGenerator, moveScorer);
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

        MoveOrderer mo = new MoveOrderer(board, moveGenerator, moveScorer);

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

}
