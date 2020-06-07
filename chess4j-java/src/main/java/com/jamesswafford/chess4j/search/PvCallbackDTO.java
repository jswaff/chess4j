package com.jamesswafford.chess4j.search;

import com.jamesswafford.chess4j.board.Move;

import java.util.ArrayList;
import java.util.List;

public class PvCallbackDTO {

    public int ply, depth, score;
    public long nodes;
    public List<Move> pv;

    public PvCallbackDTO() {
        pv = new ArrayList<>();
    }

    public PvCallbackDTO(int ply, List<Move> pv, int depth, int score, long nodes) {
        this.ply = ply;
        this.pv = pv;
        this.depth = depth;
        this.score = score;
        this.nodes = nodes;
    }

    public int getPly() {
        return ply;
    }

    public void setPly(int ply) {
        this.ply = ply;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public long getNodes() {
        return nodes;
    }

    public void setNodes(long nodes) {
        this.nodes = nodes;
    }

    public List<Move> getPv() {
        return pv;
    }

    public void setPv(List<Move> pv) {
        this.pv = pv;
    }

}
