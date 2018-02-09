package com.jamesswafford.chess4j.board.squares;

public final class NorthEast extends Direction {

    private static final NorthEast INSTANCE = new NorthEast();

    private NorthEast() {
    }

    public static NorthEast getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isDiagonal() {
        return true;
    }

    @Override
    public int value() {
        return 1;
    }
}
