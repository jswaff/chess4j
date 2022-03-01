package com.jamesswafford.chess4j.exceptions;

public class GameRecordNotFoundException extends RuntimeException {

    public GameRecordNotFoundException(String msg) {
        super(msg);
    }
}
