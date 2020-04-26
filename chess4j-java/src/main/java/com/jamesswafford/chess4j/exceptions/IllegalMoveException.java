package com.jamesswafford.chess4j.exceptions;

public class IllegalMoveException extends Exception {

    private String illegalMove;
    public IllegalMoveException(String illegalMove) {
        this.illegalMove = illegalMove;
    }

    @Override
    public String getMessage() {
        return "Illegal move: " + illegalMove;
    }
}
