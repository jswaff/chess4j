package com.jamesswafford.chess4j.board.squares;


import java.util.Optional;

public final class SouthEast extends Direction {

    private static final SouthEast INSTANCE = new SouthEast();

    private SouthEast() {
    }

    @Override
    public Optional<Square> next(Square sq) {
        return Square.valueOf(sq.file().east(), sq.rank().south());
    }

    public static SouthEast getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isDiagonal() {
        return true;
    }

    @Override
    public int value() {
        return 3;
    }
}
