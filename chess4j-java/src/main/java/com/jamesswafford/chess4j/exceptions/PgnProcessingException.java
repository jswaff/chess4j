package com.jamesswafford.chess4j.exceptions;

public class PgnProcessingException extends RuntimeException {

    public PgnProcessingException(String msg, Throwable t) {
        super(msg, t);
    }

}
