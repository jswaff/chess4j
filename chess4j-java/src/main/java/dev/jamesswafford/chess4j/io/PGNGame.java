package dev.jamesswafford.chess4j.io;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PGNGame {

    private final List<PGNTag> tags = new ArrayList<>();
    private final List<MoveWithNAG> moves = new ArrayList<>();
    @Getter
    private final PGNResult result;

    public PGNGame(List<PGNTag> tags, List<MoveWithNAG> moves, PGNResult result) {
        this.tags.addAll(tags);
        this.moves.addAll(moves);
        this.result = result;
    }

    public List<PGNTag> getTags() {
        return Collections.unmodifiableList(tags);
    }

    public List<MoveWithNAG> getMoves() {
        return Collections.unmodifiableList(moves);
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
