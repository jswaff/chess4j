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

    public long getFailHighs() {
        return failHighs;
    }

    public long getFailLows() {
        return failLows;
    }

    public List<Move> getFirstLine() {
        return firstLine;
    }

    public long getHashExactScores() {
        return hashExactScores;
    }

    public List<Move> getLastPV() {
        return lastPV;
    }

    public long getNodes() {
        return nodes;
    }

    public long getQNodes() {
        return qnodes;
    }

    public void incFailHighs() {
        failHighs++;
    }

    public void incFailLows() {
        failLows++;
    }

    public void incHashExactScores() {
        hashExactScores++;
    }

    public void incNodes() {
        nodes++;
    }

    public void incQNodes() {
        qnodes++;
    }

    public void setFirstLine(List<Move> firstLine) {
        this.firstLine.clear();
        this.firstLine.addAll(firstLine);
    }

    public void setLastPV(List<Move> lastPV) {
        this.lastPV.clear();
        this.lastPV.addAll(lastPV);
    }

}
