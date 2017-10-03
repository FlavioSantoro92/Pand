package it.unibas.pand.exception;

public class PandException extends Exception {
    public PandException() {}

    public PandException(String message) {
        super(message);
    }

    public PandException(String message, Throwable cause) {
        super(message, cause);
    }

    public PandException(Throwable cause) {
        super(cause);
    }
}
