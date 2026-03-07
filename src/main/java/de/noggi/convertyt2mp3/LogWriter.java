package de.noggi.convertyt2mp3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

public class LogWriter {

    private static final Path LOG_DIR_PATH = Path.of("./log");
    private static final Path LOG_FILE_PATH = LOG_DIR_PATH.resolve(Path.of("log.txt"));

    private static final char NEW_LINE = '\n';
    private static final byte MAX_STACK_TRACE_PER_CAUSE = 10;
    private static final String CAUSED_BY = "Caused by: ";

    private static boolean devMode = false;

    public static void create(final boolean dev) {
        devMode = dev;
        if (!devMode) {
            if (Files.exists(LOG_FILE_PATH)) {
                try {
                    Files.delete(LOG_FILE_PATH);
                } catch (IOException e) {
                    //ignore this here and accept the fate
                }
            }
        }
    }

    public static void info(final Class<?> caller, final String msg) {
        write(createLogMsg(caller, "INFO", msg));
    }

    public static void warn(final Class<?> caller, final String msg) {
        write(createLogMsg(caller, "WARN", msg));
    }

    public static void error(final Class<?> caller, final String msg) {
        write(createLogMsg(caller, "ERROR", msg));
    }

    public static void error(final Class<?> caller, final String msg, final Throwable t) {
        final StringBuilder fullMsg = new StringBuilder(msg);
        fullMsg.append(NEW_LINE);

        Throwable activeEx = t;
        boolean cause = false;
        while (activeEx != null) {
            if (cause) {
                fullMsg.append(CAUSED_BY);
            }
            appendStackTrace(fullMsg, activeEx);
            activeEx = activeEx.getCause();
            cause = true;
        }

        error(caller, fullMsg.toString());
    }

    private static void appendStackTrace(final StringBuilder target, final Throwable t) {
        target.append(t.getMessage()).append(NEW_LINE);

        byte countElements = 0;
        for (final StackTraceElement ste : t.getStackTrace()) {
            if (countElements == MAX_STACK_TRACE_PER_CAUSE) {
                return;
            }
            target.append(ste.toString()).append(NEW_LINE);
            countElements++;
        }
    }

    private static String createLogMsg(final Class<?> caller, final String severity, final String msg) {
        return severity + ": " + LocalDateTime.now() + " - " + caller.getSimpleName() + " - " + msg;
    }

    private static void write(final String msg) {
        if (devMode) {
            System.out.println(msg);
            return;
        }

        if (!ensureFileExists()) {
            //don't cause runtime exceptions with logging
            return;
        }

        try {
            Files.writeString(LOG_FILE_PATH, msg, StandardOpenOption.APPEND);
        } catch (IOException e) {
            //accept the fate and leave it be
        }
    }

    private static boolean ensureFileExists() {
        if (!Files.exists(LOG_DIR_PATH) || !Files.isDirectory(LOG_DIR_PATH)) {
            try {
                Files.createDirectory(LOG_DIR_PATH);
            } catch (IOException e) {
                //accept the fate and leave it be
                return false;
            }
        }

        if (!Files.exists(LOG_FILE_PATH)) {
            try {
                Files.createFile(LOG_DIR_PATH);
            } catch (IOException e) {
                //accept the fate and leave it be
                return false;
            }
        }

        return true;
    }

    private LogWriter() {}
}
