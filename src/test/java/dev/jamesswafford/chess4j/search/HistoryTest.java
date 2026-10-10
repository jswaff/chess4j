package dev.jamesswafford.chess4j.search;

import dev.jamesswafford.chess4j.board.Color;
import dev.jamesswafford.chess4j.board.Move;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import static dev.jamesswafford.chess4j.pieces.Pawn.*;
import static dev.jamesswafford.chess4j.board.squares.Square.*;

public class HistoryTest {

    private final History history = History.getInstance();

    @Before
    public void setUp() {
        history.clear();
    }

    @Test
    public void testClear() {
        Move m = new Move(WHITE_PAWN, E2, E4);
        history.addCutoff(Color.WHITE, m, 5);
        assertTrue(history.getScore(Color.WHITE, m) > 0);
        history.clear();
        assertEquals(0, history.getScore(Color.WHITE, m));
    }

    @Test
    public void testCutoffRewardsAndFailurePenalizes() {
        Move m = new Move(WHITE_PAWN, E2, E4);
        Move m2 = new Move(WHITE_PAWN, D2, D4);

        history.addCutoff(Color.WHITE, m, 4);
        assertEquals(16, history.getScore(Color.WHITE, m));

        history.addFailure(Color.WHITE, m2, 4);
        assertEquals(-16, history.getScore(Color.WHITE, m2));
    }

    @Test
    public void testDeeperCutoffsWeighMore() {
        Move m = new Move(WHITE_PAWN, E2, E4);
        Move m2 = new Move(WHITE_PAWN, D2, D4);

        history.addCutoff(Color.WHITE, m, 3);
        history.addCutoff(Color.WHITE, m2, 6);
        assertTrue(history.getScore(Color.WHITE, m2) > history.getScore(Color.WHITE, m));
    }

    @Test
    public void testScoresAreTrackedPerColor() {
        Move m = new Move(BLACK_PAWN, E7, E5);
        history.addCutoff(Color.BLACK, m, 5);
        assertTrue(history.getScore(Color.BLACK, m) > 0);
        assertEquals(0, history.getScore(Color.WHITE, m));
    }

    @Test
    public void testScoresAreBounded() {
        Move m = new Move(WHITE_PAWN, E2, E4);
        Move m2 = new Move(WHITE_PAWN, D2, D4);
        for (int i=0;i<10000;i++) {
            history.addCutoff(Color.WHITE, m, 50);
            history.addFailure(Color.WHITE, m2, 50);
        }
        assertTrue(history.getScore(Color.WHITE, m) <= History.MAX_SCORE);
        assertTrue(history.getScore(Color.WHITE, m2) >= -History.MAX_SCORE);
    }
}
