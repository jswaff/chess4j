package dev.jamesswafford.chess4j.search;

import dev.jamesswafford.chess4j.board.Move;
import lombok.Builder;

import java.util.List;

@Builder
public class PvCallbackDTO {

    public int ply, depth, score;
    public long elapsedMS, nodes;
    public List<Move> pv;

}
