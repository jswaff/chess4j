package com.jamesswafford.chess4j.board.squares;

public final class South extends Direction {

    private static final South INSTANCE = new South();

    private South() {
    }

    public static South getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isDiagonal() {
        return false;
    }

    @Override
    public int value() {
        return 4;
    }
}
