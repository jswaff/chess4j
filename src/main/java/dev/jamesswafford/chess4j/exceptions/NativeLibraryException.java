package dev.jamesswafford.chess4j.exceptions;

public class NativeLibraryException extends RuntimeException {

    public NativeLibraryException(String msg) {
        super(msg);
    }

    public NativeLibraryException(String msg, Throwable t) {
        super(msg, t);
    }

}
