package com.jamesswafford.chess4j.search;

import com.jamesswafford.chess4j.board.Move;

import java.util.ArrayList;
import java.util.List;

public class PvCallbackDTO {

    public int ply;
    public List<Move> pv;

    public PvCallbackDTO() {
        pv = new ArrayList<>();
    }

    public PvCallbackDTO(int ply, List<Move> pv) {
        this.ply = ply;
        this.pv = pv;
    }

    public int getPly() {
        return ply;
    }

    public void setPly(int ply) {
        this.ply = ply;
    }

    public List<Move> getPv() {
        return pv;
    }

    public void setPv(List<Move> pv) {
        this.pv = pv;
    }

}
