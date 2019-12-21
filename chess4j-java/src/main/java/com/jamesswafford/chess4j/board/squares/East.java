package com.jamesswafford.chess4j.board.squares;

public final class East extends Direction {

    private static final East INSTANCE = new East();

    private East() {
    }

    public static East getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isDiagonal() {
        return false;
    }

    @Override
    public int value() {
        return 2;
    }
}
