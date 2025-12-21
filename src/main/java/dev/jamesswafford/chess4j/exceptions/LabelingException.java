package dev.jamesswafford.chess4j.exceptions;

public class LabelingException extends RuntimeException {

    public LabelingException(String message) {
        super(message);
    }

    public LabelingException(Exception e) {
        super(e);
    }

}
