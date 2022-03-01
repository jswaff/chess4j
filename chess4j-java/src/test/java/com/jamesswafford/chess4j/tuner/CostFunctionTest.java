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

public class CostFunctionTest {

    CostFunction costFunction;

    @Before
    public void setUp() {
        costFunction = new CostFunction();
    }

    @Test
    public void calculateCostFromBoard() {
        Board board = new Board();
        SearchParameters parameters = new SearchParameters(0, -Constants.CHECKMATE, Constants.CHECKMATE);
        Search search = mock(Search.class);
        costFunction.setSearch(search);
        when(search.search(board, parameters)).thenReturn(50);
        assertDoubleEquals(costFunction.calculateCost(board, WIN), 0.1759);
    }

    @Test
    public void calculateCostFromBoardRealSearch() {
        Board board = new Board();
        System.out.println(costFunction.calculateCost(board, WIN));
    }

    @Test
    public void calculateCostFromSquishedScore() {
        assertDoubleEquals(costFunction.calculateCost(0.5806, WIN), 0.1759);
        assertDoubleEquals(costFunction.calculateCost(1, WIN), 0);
        assertDoubleEquals(costFunction.calculateCost(0.5, DRAW), 0);
        assertDoubleEquals(costFunction.calculateCost(0, LOSS), 0);
        assertDoubleEquals(costFunction.calculateCost(0.5, WIN), 0.25);
        assertDoubleEquals(costFunction.calculateCost(0.5, LOSS), 0.25);
        assertDoubleEquals(costFunction.calculateCost(1, LOSS), 1);
        assertDoubleEquals(costFunction.calculateCost(0, WIN), 1);
        assertDoubleEquals(costFunction.calculateCost(0.31459, DRAW), 0.0344);
    }

    @Test
    public void squishify() {
        assertDoubleEquals(costFunction.squishify(50), 0.5806);
        assertDoubleEquals(costFunction.squishify(0), 0.5);
        assertDoubleEquals(costFunction.squishify(-500), 0.0372);
        assertDoubleEquals(costFunction.squishify(CHECKMATE), 1);
        assertDoubleEquals(costFunction.squishify(-CHECKMATE), 0);
    }

    private void assertDoubleEquals(double val, double expected) {
        double epsilon = 0.0001;
        assertTrue(val >= expected - epsilon);
        assertTrue(val <= expected + epsilon);
    }
}
