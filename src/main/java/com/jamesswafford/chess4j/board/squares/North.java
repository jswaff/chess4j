package com.jamesswafford.chess4j.board.squares;


import java.util.Optional;

public final class North extends Direction {

    private static final North INSTANCE = new North();

    private North() {
    }

    @Override
    public Optional<Square> next(Square sq) {
        return Square.valueOf(sq.file(), sq.rank().north());
    }

    public static North getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isDiagonal() {
        return false;
    }

    @Override
    public int value() {
        return 0;
    }
}
