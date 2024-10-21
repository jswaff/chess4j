package dev.jamesswafford.chess4j.pieces;

import dev.jamesswafford.chess4j.board.Color;

public final class Pawn extends Piece {

    public static final Pawn WHITE_PAWN = new Pawn(Color.WHITE);
    public static final Pawn BLACK_PAWN = new Pawn(Color.BLACK);

    private Pawn(Color color) {
        super(color);
    }

    public String toString() {
        return isWhite()?"P":"p";
    }

    @Override
    public Piece getOppositeColorPiece() {
        if (Color.WHITE.equals(getColor())) {
            return BLACK_PAWN;
        } else {
            return WHITE_PAWN;
        }
    }
}
