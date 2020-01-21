package com.jamesswafford.chess4j.pieces;

import com.jamesswafford.chess4j.board.Color;

public abstract class Piece {

    private Color color;

    public Piece(Color color) {
        this.color=color;
    }

    public Color getColor() {
        return color;
    }

    public abstract Piece getOppositeColorPiece();

    public boolean isBlack() {
        return color.getColor()==Color.BLACK.getColor();
    }

    public boolean isWhite() {
        return color.getColor()==Color.WHITE.getColor();
    }

}
