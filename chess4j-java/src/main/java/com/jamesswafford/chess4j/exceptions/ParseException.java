package com.jamesswafford.chess4j.exceptions;

public class ParseException extends RuntimeException {

    public ParseException(String message) {
        super(message);
    }

    public ParseException(Exception e) {
        super(e);
    }
}
