package com.jamesswafford.chess4j.board.squares;


import java.util.Optional;

public abstract class Direction {

    public abstract boolean isDiagonal();
    public abstract int value();
    private static Direction directionTo[][] = new Direction[64][64];

    static {
        for (int i=0;i<64;i++) {
            Square sq = Square.valueOf(i);
            for (int j=0;j<64;j++) {
                Square sq2 = Square.valueOf(j);
                directionTo[i][j] = calculateDirectionTo(sq,sq2);
            }
        }
    }

    public static Optional<Direction> getDirectionTo(Square sq1,Square sq2) {
        return getDirectionTo(sq1.value(),sq2.value());
    }

    public static Optional<Direction> getDirectionTo(int sq1, int sq2) {
        return Optional.ofNullable(directionTo[sq1][sq2]);
    }

    private static Direction calculateDirectionTo(Square from, Square to) {
        int fDiff = from.file().getValue() - to.file().getValue();
        int rDiff = from.rank().getValue() - to.rank().getValue();

        if (fDiff == 0) {
            // same file
            if (rDiff < 0) return South.getInstance();
            if (rDiff > 0) return North.getInstance();
        } else if (fDiff < 0) {
            // to is east of from
            if (rDiff == 0) return East.getInstance();
            if (rDiff == fDiff) return SouthEast.getInstance();
            if (rDiff == -fDiff) return NorthEast.getInstance();
        } else { // fDiff > 0
            // to is west of from
            if (rDiff == 0) return West.getInstance();
            if (rDiff == fDiff) return NorthWest.getInstance();
            if (rDiff == -fDiff) return SouthWest.getInstance();
        }

        return null;
    }

}
