package dev.jamesswafford.chess4j.board.squares;

public final class SouthWest extends Direction {

    private static final SouthWest INSTANCE = new SouthWest();

    private SouthWest() {
    }

    public static SouthWest getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isDiagonal() {
        return true;
    }

    @Override
    public int value() {
        return 5;
    }
}
