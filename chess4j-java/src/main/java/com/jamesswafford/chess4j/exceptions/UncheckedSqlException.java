package com.jamesswafford.chess4j.exceptions;

public class UncheckedSqlException extends RuntimeException {

    public UncheckedSqlException(Throwable t) {
        super(t);
    }

    public UncheckedSqlException(String msg) {
        super(msg);
    }

    public UncheckedSqlException(String msg, Throwable t) {
        super(msg, t);
    }

}
