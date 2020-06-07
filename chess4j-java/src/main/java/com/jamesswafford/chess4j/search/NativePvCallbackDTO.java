package com.jamesswafford.chess4j.search;

import java.util.ArrayList;
import java.util.List;

public class NativePvCallbackDTO {

    public int ply, depth, score;
    public long nodes;
    public List<Long> pv;

    public NativePvCallbackDTO() {
        pv = new ArrayList<>();
    }

    public NativePvCallbackDTO(int ply, List<Long> pv, int depth, int score, long nodes) {
        this.ply = ply;
        this.pv = pv;
        this.depth = depth;
        this.score = score;
        this.nodes = nodes;
    }

    public static NativePvCallbackDTO with(int ply, List<Long> pv, int depth, int score, long nodes) {
        return new NativePvCallbackDTO(ply, pv, depth, score, nodes);
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

    public List<Long> getPv() {
        return pv;
    }

    public void setPv(List<Long> pv) {
        this.pv = pv;
    }

}
