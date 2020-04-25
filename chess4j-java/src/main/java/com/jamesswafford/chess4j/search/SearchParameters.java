package com.jamesswafford.chess4j.search;

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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SearchParameters)) return false;

        SearchParameters that = (SearchParameters)obj;

        if (this.getDepth() != that.getDepth()) return false;
        if (this.getAlpha() != that.getAlpha()) return false;
        if (this.getBeta() != that.getBeta()) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int hc = 7;

        hc = 31 * hc + depth;
        hc = 37 * hc + alpha;
        hc = 43 * hc + beta;

        return hc;
    }

}
