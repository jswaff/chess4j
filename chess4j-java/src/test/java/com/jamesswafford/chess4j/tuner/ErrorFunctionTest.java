package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.Constants;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.search.Search;
import com.jamesswafford.chess4j.search.SearchParameters;
import org.junit.Before;
import org.junit.Test;

import static com.jamesswafford.chess4j.Constants.CHECKMATE;
import static com.jamesswafford.chess4j.utils.GameResult.*;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ErrorFunctionTest {

    ErrorFunction errorFunction;

    @Before
    public void setUp() {
        errorFunction = new ErrorFunction();
    }

    @Test
    public void calculateErrorFromBoard() {
        Board board = new Board();
        SearchParameters parameters = new SearchParameters(0, -Constants.CHECKMATE, Constants.CHECKMATE);
        Search search = mock(Search.class);
        errorFunction.setSearch(search);
        when(search.search(board, parameters)).thenReturn(50);
        assertDoubleEquals(errorFunction.calculateError(board, WIN), 0.1759);
    }

    @Test
    public void calculateErrorFromSquishedScore() {
        assertDoubleEquals(errorFunction.calculateError(0.5806, WIN), 0.1759);
        assertDoubleEquals(errorFunction.calculateError(1, WIN), 0);
        assertDoubleEquals(errorFunction.calculateError(0.5, DRAW), 0);
        assertDoubleEquals(errorFunction.calculateError(0, LOSS), 0);
        assertDoubleEquals(errorFunction.calculateError(0.5, WIN), 0.25);
        assertDoubleEquals(errorFunction.calculateError(0.5, LOSS), 0.25);
        assertDoubleEquals(errorFunction.calculateError(1, LOSS), 1);
        assertDoubleEquals(errorFunction.calculateError(0, WIN), 1);
        assertDoubleEquals(errorFunction.calculateError(0.31459, DRAW), 0.0344);
    }

    @Test
    public void squishify() {
        assertDoubleEquals(errorFunction.squishify(50), 0.5806);
        assertDoubleEquals(errorFunction.squishify(0), 0.5);
        assertDoubleEquals(errorFunction.squishify(-500), 0.0372);
        assertDoubleEquals(errorFunction.squishify(CHECKMATE), 1);
        assertDoubleEquals(errorFunction.squishify(-CHECKMATE), 0);
    }

    private void assertDoubleEquals(double val, double expected) {
        double epsilon = 0.0001;
        assertTrue(val >= expected - epsilon);
        assertTrue(val <= expected + epsilon);
    }
}
