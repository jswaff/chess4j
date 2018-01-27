package com.jamesswafford.chess4j.board.squares;


import java.util.Optional;

public final class NorthEast extends Direction {

    private static final NorthEast INSTANCE = new NorthEast();

    private NorthEast() {
    }

    @Override
    public Optional<Square> next(Square sq) {
        return Square.valueOf(sq.file().east(), sq.rank().north());
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
