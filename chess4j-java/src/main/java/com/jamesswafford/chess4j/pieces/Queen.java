package com.jamesswafford.chess4j.pieces;

import com.jamesswafford.chess4j.board.Color;

public final class Queen extends Piece {

    public static final Queen WHITE_QUEEN = new Queen(Color.WHITE);
    public static final Queen BLACK_QUEEN = new Queen(Color.BLACK);

    private Queen(Color color) {
        super(color);
    }

    public String toString() {
        return isWhite()?"Q":"q";
    }

    @Override
    public Piece getOppositeColorPiece() {
        if (Color.WHITE.equals(getColor())) {
            return BLACK_QUEEN;
        } else {
            return WHITE_QUEEN;
        }
    }

}
