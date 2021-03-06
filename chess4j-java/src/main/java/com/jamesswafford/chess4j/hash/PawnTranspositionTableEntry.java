package com.jamesswafford.chess4j.hash;

public class PawnTranspositionTableEntry {

    private final long zobristKey;
    private final int score;

    public PawnTranspositionTableEntry(long zobristKey,int score) {
        this.zobristKey = zobristKey;
        this.score = score;
    }

    public long getZobristKey() {
        return zobristKey;
    }

    public int getScore() {
        return score;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PawnTranspositionTableEntry)) {
            return false;
        }
        PawnTranspositionTableEntry that = (PawnTranspositionTableEntry)obj;
        if (this.getZobristKey() != that.getZobristKey())
            return false;
        if (this.getScore() != that.getScore())
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int hc = (int)this.getZobristKey();
        hc = hc * 31 + this.getScore();

        return hc;
    }

    public static int sizeOf() {
        return (Long.SIZE + Integer.SIZE) / Byte.SIZE;
    }

}
