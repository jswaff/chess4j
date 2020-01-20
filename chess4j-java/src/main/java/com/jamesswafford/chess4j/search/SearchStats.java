package com.jamesswafford.chess4j.search;

import java.util.ArrayList;
import java.util.List;

import com.jamesswafford.chess4j.board.Move;


public class SearchStats {

    private long failHighs=0;
    private long failLows=0;
    private long hashExactScores=0;

    private long nodes=0;
    private long qnodes=0;
    private List<Move> lastPV = new ArrayList<>();
    private List<Move> firstLine = new ArrayList<>();
    private long prunes=0;

    long getFailHighs() {
        return failHighs;
    }

    long getFailLows() {
        return failLows;
    }

    List<Move> getFirstLine() {
        return firstLine;
    }

    long getHashExactScores() {
        return hashExactScores;
    }

    List<Move> getLastPV() {
        return lastPV;
    }

    long getNodes() {
        return nodes;
    }

    long getQNodes() {
        return qnodes;
    }

    void incFailHighs() {
        failHighs++;
    }

    void incFailLows() {
        failLows++;
    }

    void incHashExactScores() {
        hashExactScores++;
    }

    void incNodes() {
        nodes++;
    }

    void incQNodes() {
        qnodes++;
    }

    public void setFirstLine(List<Move> firstLine) {
        this.firstLine.clear();
        this.firstLine.addAll(firstLine);
    }

    void setLastPV(List<Move> lastPV) {
        this.lastPV.clear();
        this.lastPV.addAll(lastPV);
    }

    long getPrunes() {
        return prunes;
    }

    void incPrunes() {
        prunes++;
    }
}
