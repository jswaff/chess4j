package com.jamesswafford.chess4j.book;

import com.jamesswafford.chess4j.board.Move;

public class BookMove {

    private Move move;
    private int frequency = 1;
    private int wins,losses,draws;

    public BookMove(Move move) {
        this(move,1);
    }

    public BookMove(Move move,int frequency) {
        this(move,frequency,0,0,0);
    }

    public BookMove(Move move,int frequency,int wins,int losses,int draws) {
        this.move = move;
        this.frequency = frequency;
        this.wins = wins;
        this.losses = losses;
        this.draws = draws;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public Move getMove() {
        return move;
    }

    @Override
    public String toString() {
        return "BookMove [move=" + getMove() + ", frequency=" + getFrequency()
                + ", wins=" + getWins() + ", losses=" + getLosses() + ", draws=" + getDraws() + "]";
    }

    @Override
    public int hashCode() {
        int hc = move.hashCode();
        hc = hc * 31 + frequency;
        hc = hc * 17 + wins;
        hc = hc * 23 + losses;
        hc = hc * 37 + draws;
        return hc;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BookMove)) {
            return false;
        }

        BookMove that = (BookMove)obj;
        if (!this.getMove().equals(that.getMove())) return false;
        if (this.getFrequency() != that.getFrequency()) return false;
        if (this.getWins() != that.getWins()) return false;
        if (this.getLosses() != that.getLosses()) return false;
        if (this.getDraws() != that.getDraws()) return false;

        return true;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }


}
