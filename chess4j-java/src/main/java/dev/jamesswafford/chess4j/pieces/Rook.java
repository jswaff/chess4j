package dev.jamesswafford.chess4j.pieces;

import dev.jamesswafford.chess4j.board.Color;

public final class Rook extends Piece {

    public static final Rook WHITE_ROOK = new Rook(Color.WHITE);
    public static final Rook BLACK_ROOK = new Rook(Color.BLACK);

    private Rook(Color color) {
        super(color);
    }

    public String toString() {
        return isWhite()?"R":"r";
    }

    @Override
    public Piece getOppositeColorPiece() {
        if (Color.WHITE.equals(getColor())) {
            return BLACK_ROOK;
        } else {
            return WHITE_ROOK;
        }
    }

}
