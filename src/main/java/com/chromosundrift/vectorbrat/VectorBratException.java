package com.chromosundrift.vectorbrat;

public class VectorBratException extends Exception {
    public VectorBratException(Exception e) {
        super(e);
    }

    public VectorBratException() {
        super();
    }

    public VectorBratException(String message) {
        super(message);
    }

    public VectorBratException(String message, Throwable cause) {
        super(message, cause);
    }
}
