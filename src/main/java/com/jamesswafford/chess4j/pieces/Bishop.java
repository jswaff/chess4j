package com.jamesswafford.chess4j.pieces;

import com.jamesswafford.chess4j.Color;

public final class Bishop extends Piece {

    public static final Bishop WHITE_BISHOP = new Bishop(Color.WHITE);
    public static final Bishop BLACK_BISHOP = new Bishop(Color.BLACK);

    private Bishop(Color color) {
        super(color);
    }

    public String toString() {
        return isWhite()?"B":"b";
    }

    @Override
    public Piece getOppositeColorPiece() {
        if (Color.WHITE.equals(getColor())) {
            return BLACK_BISHOP;
        } else {
            return WHITE_BISHOP;
        }
    }

}
