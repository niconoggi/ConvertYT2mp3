package de.noggi.convertyt2mp3.audio;

import javax.sound.sampled.Clip;
import java.nio.file.Path;

public class AudioPlayer {

    private static final Path TMP_FOLDER = Path.of("./temp");

    private static Clip activeClip;

    public static void playTemporaryWav() throws AudioException{
        //TODO
    }

    public static void stop() {
        if (activeClip != null) {
            activeClip.stop();
        }
    }

}
