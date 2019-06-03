package com.jamesswafford.chess4j.exceptions;

public class ParseException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ParseException(String message) {
        super(message);
    }

    public ParseException(Exception e) {
        super(e);
    }
}
