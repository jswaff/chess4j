package com.jamesswafford.chess4j.exceptions;

public class PgnToBookException extends RuntimeException {

    public PgnToBookException(String msg, Throwable t) {
        super(msg, t);
    }

}
