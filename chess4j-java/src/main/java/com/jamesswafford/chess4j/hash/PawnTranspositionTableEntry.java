package com.jamesswafford.chess4j.hash;

import io.vavr.Tuple2;

public class PawnTranspositionTableEntry {

    private final long zobristKey;
    private final int mgscore;
    private final int egscore;

    public PawnTranspositionTableEntry(long zobristKey, int mgscore, int egscore) {
        this.zobristKey = zobristKey;
        this.mgscore = mgscore;
        this.egscore = egscore;
    }

    public PawnTranspositionTableEntry(long zobristKey, long val) {
        this.zobristKey = zobristKey;
        this.mgscore = 0; // FIXME
        this.egscore = 0; // FIXME
    }

    public long getZobristKey() {
        return zobristKey;
    }

    public Tuple2<Integer, Integer> getScore() {
        return new Tuple2<>(mgscore, egscore);
    }

    public int getMgscore() {
        return mgscore;
    }

    public int getEgscore() {
        return egscore;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PawnTranspositionTableEntry)) {
            return false;
        }
        PawnTranspositionTableEntry that = (PawnTranspositionTableEntry)obj;
        if (this.getZobristKey() != that.getZobristKey())
            return false;
        if (this.getMgscore() != that.getMgscore())
            return false;
        if (this.getEgscore() != that.getEgscore())
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int hc = (int)this.getZobristKey();
        hc = hc * 31 + this.getMgscore();
        hc = hc * 37 + this.getEgscore();

        return hc;
    }

    public static int sizeOf() {
        return (Long.SIZE + Integer.SIZE + Integer.SIZE) / Byte.SIZE;
    }

}
