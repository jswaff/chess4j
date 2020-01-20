package com.jamesswafford.chess4j.search.v2;

public class SearchParameters {

    private final int depth;
    private final int alpha;
    private final int beta;

    public SearchParameters(int depth, int alpha, int beta) {
        this.depth = depth;
        this.alpha = alpha;
        this.beta = beta;
    }

    public int getDepth() {
        return depth;
    }

    public int getAlpha() {
        return alpha;
    }

    public int getBeta() {
        return beta;
    }

    @Override
    public String toString() {
        return "SearchParameters [depth: " + depth + ", alpha: " + alpha + ", beta: " + beta + "]";
    }
}
