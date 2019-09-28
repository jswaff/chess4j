package com.jamesswafford.chess4j.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jamesswafford.chess4j.board.Move;

public class PGNGame {

    private List<PGNTag> tags = new ArrayList<>();
    private List<Move> moves = new ArrayList<>();
    private PGNResult result;

    public PGNGame(List<PGNTag> tags,List<Move> moves,PGNResult result) {
        this.tags.addAll(tags);
        this.moves.addAll(moves);
        this.result = result;
    }

    public List<PGNTag> getTags() {
        return Collections.unmodifiableList(tags);
    }

    public List<Move> getMoves() {
        return Collections.unmodifiableList(moves);
    }

    public PGNResult getResult() {
        return result;
    }

    @Override
    public int hashCode() {
        int hc = tags.hashCode();
        hc *= 31 * moves.hashCode();
        hc *= 17 * result.hashCode();
        return hc;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PGNGame)) {
            return false;
        }

        PGNGame that = (PGNGame)obj;

        if (!this.getTags().equals(that.getTags())) return false;
        if (!this.getMoves().equals(that.getMoves())) return false;
        if (!this.getResult().equals(that.getResult())) return false;

        return true;
    }


}
