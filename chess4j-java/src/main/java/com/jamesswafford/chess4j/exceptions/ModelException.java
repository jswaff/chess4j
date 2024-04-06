package com.jamesswafford.chess4j.exceptions;

public class ModelException extends RuntimeException {
    public ModelException(String msg) {
        super(msg);
    }

    public ModelException(Throwable t) {
        super(t);
    }

    public ModelException(String msg, Throwable t) {
        super(msg, t);
    }

}
