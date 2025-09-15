package dev.jamesswafford.chess4j.board;

import dev.jamesswafford.chess4j.board.squares.Square;

public class Undo {

    private Move move;
    private int fiftyCounter;
    private int castlingRights;
    private Square epSquare;
    private long zobristKey;

    public Undo(Move move,int fiftyCounter,int castlingRights, Square epSquare,long zobristKey) {
        this.move=move;
        this.fiftyCounter=fiftyCounter;
        this.castlingRights = castlingRights;
        this.epSquare=epSquare;
        this.zobristKey=zobristKey;
    }

    public int getCastlingRights() {
        return castlingRights;
    }

    public Square getEpSquare() {
        return epSquare;
    }

    public int getFiftyCounter() {
        return fiftyCounter;
    }


    public Move getMove() {
        return move;
    }


    public long getZobristKey() {
        return zobristKey;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Undo)) {
            return false;
        }

        Undo that = (Undo)obj;
        if (!this.getMove().equals(that.getMove())) {
            return false;
        }
        if (this.getFiftyCounter() != that.getFiftyCounter()) {
            return false;
        }
        if (this.getCastlingRights() != that.getCastlingRights()) {
            return false;
        }
        if (this.getEpSquare()==null) {
            if (that.getEpSquare()!=null) {
                return false;
            }
        } else {
            if (!this.getEpSquare().equals(that.getEpSquare())) {
                return false;
            }
        }
        if (this.getZobristKey() != that.getZobristKey()) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = (int)zobristKey;
        hash = hash * 17 + move.hashCode();
        hash = hash * 19 + (epSquare == null ? 0 : epSquare.hashCode());
        hash = hash * 23 + castlingRights;
        hash = hash * 31 + fiftyCounter;

        return hash;
    }

    @Override
    public String toString() {
        return "Undo [move=" + getMove() + ", fiftyCounter=" + getFiftyCounter()
                + ", castlingRights=" + getCastlingRights() + ", epSquare=" + getEpSquare()
                + ", zobristKey=" + getZobristKey() + "]";
    }
}
