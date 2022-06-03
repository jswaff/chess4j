package com.jamesswafford.chess4j.hash;

import io.vavr.Tuple2;

public class PawnTranspositionTableEntry {

    private final long zobristKey;
    private long val;

    public PawnTranspositionTableEntry(long zobristKey, int mgscore, int egscore) {
        this.zobristKey = zobristKey;
        buildStoredValue(mgscore, egscore);
    }

    public PawnTranspositionTableEntry(long zobristKey, long val) {
        this.zobristKey = zobristKey;
        this.val = val;
    }

    private void buildStoredValue(int mgscore, int egscore) {
        assert(mgscore >= -32767);
        assert(mgscore <= 32767);
        val = ((long)mgscore + 32767) << 32;

        assert(egscore >= -32767);
        assert(egscore <= 32767);
        val |= (long)egscore + 32767;
    }

    public long getZobristKey() {
        return zobristKey;
    }

    public long getVal() { return val; }

    public Tuple2<Integer, Integer> getScore() {
        return new Tuple2<>(getMgscore(), getEgscore());
    }

    public int getMgscore() {
        return (int)((val >> 32) & 0xFFFF) - 32767;
    }

    public int getEgscore() {
        return (int)(val & 0xFFFF) - 32767;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PawnTranspositionTableEntry)) {
            return false;
        }
        PawnTranspositionTableEntry that = (PawnTranspositionTableEntry)obj;

        return (this.zobristKey == that.zobristKey) && (this.val == that.val);
    }

    @Override
    public int hashCode() {
        int hc = (int)this.zobristKey;
        hc = hc * (int)this.val;

        return hc;
    }

    public static int sizeOf() {
        return Long.SIZE * 2 / Byte.SIZE;
    }

}
