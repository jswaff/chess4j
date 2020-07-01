package com.jamesswafford.chess4j.search;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class SearchStats {

    public long nodes, qnodes, failHighs, failLows, draws;

    public SearchStats() {
        initialize();
    }

    public SearchStats(SearchStats searchStats) {
        set(searchStats);
    }

    void initialize() {
        nodes = 0;
        qnodes = 0;
        failHighs = 0;
        failLows = 0;
        draws = 0;
    }

    void set(SearchStats searchStats) {
        this.nodes = searchStats.nodes;
        this.qnodes = searchStats.qnodes;
        this.failHighs = searchStats.failHighs;
        this.failLows = searchStats.failLows;
        this.draws = searchStats.draws;
    }

}
