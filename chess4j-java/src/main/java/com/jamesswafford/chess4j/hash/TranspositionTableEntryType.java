package com.jamesswafford.chess4j.hash;

public enum TranspositionTableEntryType {

    LOWER_BOUND, UPPER_BOUND, EXACT_SCORE, MOVE_ONLY;

    public static final TranspositionTableEntryType[] values = values();
}
