package dev.jamesswafford.chess4j.board.squares;

public final class NorthWest extends Direction {

    private static final NorthWest INSTANCE = new NorthWest();

    private NorthWest() {
    }

    public static NorthWest getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isDiagonal() {
        return true;
    }

    @Override
    public int value() {
        return 7;
    }
}
