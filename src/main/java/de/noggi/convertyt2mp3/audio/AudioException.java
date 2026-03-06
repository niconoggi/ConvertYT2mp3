package de.noggi.convertyt2mp3.audio;

import java.io.Serial;

public class AudioException extends Exception {

    @Serial
    private static final long serialVersionUID = 551631814649168127L;

    public AudioException() {super();}

    public AudioException(String message) {super(message);}

}
