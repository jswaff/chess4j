package dev.jamesswafford.chess4j.exceptions;

public class IllegalMoveException extends RuntimeException {

    private final String illegalMove;

    public IllegalMoveException(String illegalMove) {
        this.illegalMove = illegalMove;
    }

    @Override
    public String getMessage() {
        return "Illegal move: " + illegalMove;
    }
}
