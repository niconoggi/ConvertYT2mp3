package de.noggi.convertyt2mp3.api.exception;

import java.io.Serial;

public class APIConsumerException extends Exception{

    @Serial
    private static final long serialVersionUID = 2813395466762998676L;

    /**
     * Only use this constructor for subclasses like TokensExceededException
     */
    public APIConsumerException() {
        throw new UnsupportedOperationException("This constructor is used only for subclasses");
    }

    public APIConsumerException(final String message) {
        super(message);
    }
}
