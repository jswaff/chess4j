package com.jamesswafford.chess4j.board;

import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.pieces.Piece;


public class Move {

    private Square from,to;
    private Piece piece,captured,promotion;
    private boolean castle,epCapture;

    public Move(Piece piece, Square from, Square to) {
        this.piece = piece;
        this.from = from;
        this.to = to;
    }

    public Move(Piece piece, Square from, Square to, boolean castle) {
        this(piece,from,to);
        this.castle = castle;
    }

    public Move(Piece piece, Square from, Square to, Piece captured) {
        this(piece,from,to);
        this.captured = captured;
    }

    public Move(Piece piece, Square from, Square to, Piece captured, boolean epCapture) {
        this(piece,from,to,captured);
        this.epCapture = epCapture;
    }

    public Move(Piece piece, Square from, Square to, Piece captured, Piece promotion) {
        this(piece,from,to,captured);
        this.promotion = promotion;
    }

    public Move(Piece piece, Square from, Square to, Piece captured, Piece promotion,
        boolean castle, boolean epCapture) {

        this(piece,from,to,captured,promotion);
        this.castle = castle;
        this.epCapture = epCapture;
    }

    public Piece piece() {
        return piece;
    }

    public Piece captured() {
        return captured;
    }

    public Square from() {
        return from;
    }

    public Piece promotion() {
        return promotion;
    }

    public Square to() {
        return to;
    }

    public boolean isEpCapture() {
        return epCapture;
    }

    public boolean isCastle() {
        return castle;
    }

    @Override
    public String toString() {
        String s = from.toString() + to.toString();
        if (promotion != null) {
            s += "=" + promotion.toString();
        }
        return s;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Move)) {
            return false;
        }
        Move mv = (Move)obj;
        if (!mv.piece().equals(this.piece())) {
            return false;
        }
        if (!mv.from().equals(this.from())) {
            return false;
        }
        if (!mv.to().equals(this.to())) {
            return false;
        }
        if (mv.captured()==null) {
            if (this.captured()!=null) {
                return false;
            }
        } else {
            if (!mv.captured().equals(this.captured())) {
                return false;
            }
        }
        if (mv.promotion()==null) {
            if (this.promotion()!=null) {
                return false;
            }
        } else {
            if (!mv.promotion().equals(this.promotion())) {
                return false;
            }
        }
        if (mv.isCastle() != this.isCastle()) {
            return false;
        }
        if (mv.isEpCapture() != this.isEpCapture()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 7 + piece.hashCode();
        hash = hash * 13 + from.hashCode();
        hash = hash * 17 + to.hashCode();
        hash = hash * 31 + (captured==null ? 0 : captured.hashCode());
        hash = hash * 37 + (promotion==null ? 0 : promotion.hashCode());
        hash = hash * 41 + Boolean.valueOf(castle).hashCode();
        hash = hash * 43 + Boolean.valueOf(epCapture).hashCode();
        return hash;
    }
}
