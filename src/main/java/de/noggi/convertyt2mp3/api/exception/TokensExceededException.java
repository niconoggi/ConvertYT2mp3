package de.noggi.convertyt2mp3.api.exception;

import java.io.Serial;

public class TokensExceededException extends APIConsumerException{

    @Serial
    private static final long serialVersionUID = 7555349980581324989L;

    //This has to be overriden specifically to ensure no UnsupportedOperationException
    public TokensExceededException() {}
}
