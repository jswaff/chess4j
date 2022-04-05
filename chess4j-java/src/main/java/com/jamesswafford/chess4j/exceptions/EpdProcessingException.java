package com.jamesswafford.chess4j.exceptions;

public class EpdProcessingException extends RuntimeException {

    public EpdProcessingException(String msg) {
        super(msg);
    }

    public EpdProcessingException(String msg, Throwable t) {
        super(msg, t);
    }
}
