package com.jamesswafford.chess4j.search;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode
@ToString
public class SearchStats {

    public long nodes, qnodes;
    public long failHighs, failLows, draws;
    public Map<Integer,Long> failHighByMove = new HashMap<>();
    public long hashFailHighs, hashFailLows, hashExactScores;
    public long nullMvFailHighs;

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
        failHighByMove.clear();
        for (int i=0;i<1000;i++) { failHighByMove.put(i, 0L); }
        hashFailHighs = 0;
        hashFailLows = 0;
        hashExactScores = 0;
        nullMvFailHighs = 0;
    }

    void set(SearchStats searchStats) {
        this.nodes = searchStats.nodes;
        this.qnodes = searchStats.qnodes;
        this.failHighs = searchStats.failHighs;
        this.failLows = searchStats.failLows;
        this.draws = searchStats.draws;
        this.failHighByMove.clear();
        this.failHighByMove.putAll(searchStats.failHighByMove);
        this.hashFailHighs = searchStats.hashFailHighs;
        this.hashFailLows = searchStats.hashFailLows;
        this.hashExactScores = searchStats.hashExactScores;
        this.nullMvFailHighs = searchStats.nullMvFailHighs;
    }

}
