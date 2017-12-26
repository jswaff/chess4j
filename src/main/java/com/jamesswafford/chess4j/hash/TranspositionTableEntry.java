package com.jamesswafford.chess4j.hash;

import com.jamesswafford.chess4j.board.Move;

public class TranspositionTableEntry {

    private long zobristKey;

    private TranspositionTableEntryType type;
    private int score;
    private int depth;
    private Move move;

    public TranspositionTableEntry(long zobristKey,
            TranspositionTableEntryType type,int score,int depth,Move move) {
        this.zobristKey=zobristKey;
        this.type=type;
        this.score=score;
        this.depth=depth;
        this.move=move;
    }

    public TranspositionTableEntryType getType() {
        return type;
    }

    public long getZobristKey() {
        return zobristKey;
    }

    public int getScore() {
        return score;
    }

    public Move getMove() {
        return move;
    }

    public int getDepth() {
        return depth;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TranspositionTableEntry)) {
            return false;
        }
        TranspositionTableEntry that = (TranspositionTableEntry)obj;
        if (this.getZobristKey() != that.getZobristKey())
            return false;
        if (this.getScore() != that.getScore())
            return false;
        if (this.getDepth() != that.getDepth())
            return false;
        if (this.getMove()==null) {
            if (that.getMove() != null) {
                return false;
            }
        } else {
            if (!this.getMove().equals(that.getMove())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hc = (int)this.getZobristKey();
        hc = hc * 31 + this.getScore();
        hc = hc * 17 + this.getDepth();
        hc = hc * 31 + (this.getMove()==null ? 0 : this.getMove().hashCode());

        return hc;
    }

}
