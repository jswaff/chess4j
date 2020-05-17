package com.jamesswafford.chess4j.search;

public class SearchStats {

    public long nodes, failHighs, failLows, draws;

    public SearchStats() {
        initialize();
    }

    void initialize() {
        nodes = 0;
        failHighs = 0;
        failLows = 0;
        draws = 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof SearchStats))
            return false;

        SearchStats that = (SearchStats) obj;

        if (this.nodes != that.nodes) return false;
        if (this.failHighs != that.failHighs) return false;
        if (this.failLows != that.failLows) return false;
        if (this.draws != that.draws) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 31 * hash + Long.valueOf(nodes).hashCode();
        hash = 31 * hash + Long.valueOf(failHighs).hashCode();
        hash = 31 * hash + Long.valueOf(failLows).hashCode();
        hash = 37 * hash + Long.valueOf(draws).hashCode();

        return hash;
    }

    @Override
    public String toString() {
        return "SearchStats [nodes: " + nodes
                + ", failHighs: " + failHighs
                + ", failLows: " + failLows
                + ", draws: " + draws
                + "]";
    }
}
