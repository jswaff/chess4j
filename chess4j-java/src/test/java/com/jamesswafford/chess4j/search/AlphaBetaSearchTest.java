package com.jamesswafford.chess4j.search;

import com.jamesswafford.chess4j.board.Board;
import org.junit.Test;

import static com.jamesswafford.chess4j.Constants.INFINITY;

public class AlphaBetaSearchTest {

    @Test
    public void testSearch_initialPos() {

        Board board = new Board();

        SearchParameters params = new SearchParameters(3, -INFINITY, INFINITY);
        AlphaBetaSearch alphaBetaSearch = new AlphaBetaSearch(board, params);
        int score = alphaBetaSearch.search();

        System.out.println("search score: " + score);
    }

}
