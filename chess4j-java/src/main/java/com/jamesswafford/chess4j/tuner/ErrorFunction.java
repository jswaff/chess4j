package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.Constants;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.search.AlphaBetaSearch;
import com.jamesswafford.chess4j.search.Search;
import com.jamesswafford.chess4j.search.SearchParameters;
import com.jamesswafford.chess4j.utils.GameResult;

public class ErrorFunction {

    private Search search;
    private double k = -1.13; // from Texel

    public ErrorFunction() {
        search = new AlphaBetaSearch();
    }

    public void setSearch(Search search) {
        this.search = search;
    }

    public double calculateError(Board board, GameResult gameResult) {

        SearchParameters parameters = new SearchParameters(0, -Constants.CHECKMATE, Constants.CHECKMATE);
        int score = search.search(board, parameters);

        return 0.0;
    }

    public double squishify(int score) {
        double exp = k * score / 400.0;
        double denom = 1 + Math.pow(10, exp);
        return 1.0 / denom;
    }

}
