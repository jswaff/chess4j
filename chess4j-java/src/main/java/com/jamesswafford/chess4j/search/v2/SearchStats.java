package com.jamesswafford.chess4j.search.v2;

public class SearchStats {

    public long nodes, failHighs, failLows;

    public SearchStats() {
        initialize();
    }

    void initialize() {
        nodes = 0;
        failHighs = 0;
        failLows = 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof SearchStats))
            return false;

        SearchStats that = (SearchStats) obj;

        if (this.nodes != that.nodes) return false;
        if (this.failHighs != that.failHighs) return false;
        if (this.failLows != that.failLows) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 31 * hash + Long.valueOf(nodes).hashCode();
        hash = 31 * hash + Long.valueOf(failHighs).hashCode();
        hash = 31 * hash + Long.valueOf(failLows).hashCode();

        return hash;
    }

    @Override
    public String toString() {
        return "SearchStats [nodes: " + nodes
                + ", failHighs: " + failHighs
                + ", failLows: " + failLows
                + "]";
    }
}
