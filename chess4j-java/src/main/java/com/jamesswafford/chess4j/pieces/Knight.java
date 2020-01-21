package com.jamesswafford.chess4j.pieces;

import com.jamesswafford.chess4j.board.Color;

public final class Knight extends Piece {

    public static final Knight WHITE_KNIGHT = new Knight(Color.WHITE);
    public static final Knight BLACK_KNIGHT = new Knight(Color.BLACK);

    private Knight(Color color) {
        super(color);
    }

    public String toString() {
        return isWhite()?"N":"n";
    }

    @Override
    public Piece getOppositeColorPiece() {
        if (Color.WHITE.equals(getColor())) {
            return BLACK_KNIGHT;
        } else {
            return WHITE_KNIGHT;
        }
    }
}
