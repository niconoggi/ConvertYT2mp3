package de.noggi.convertyt2mp3.audio;

import de.noggi.convertyt2mp3.LogWriter;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AudioPlayer {

    private static final Path TMP_FOLDER = Path.of("./temp");

    private static Clip activeClip;

    public static void playTemporaryWav() throws AudioException{
    }

    public static void stop() {
        if (activeClip != null) {
            activeClip.stop();
        }
    }

}
