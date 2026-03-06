package de.noggi.convertyt2mp3;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PropertyStore {

    private static final Path RES_PATH;

    static {
        final URL propertiesResource = PropertyStore.class.getResource("properties");
        if (propertiesResource == null) {
            throw new RuntimeException("Properties could not be loaded, as the resource was not found!");
        }
        final String propertiesPath = propertiesResource.getFile();
        if (propertiesPath == null || propertiesPath.isBlank()) {
            throw new RuntimeException("Properties could not be loaded! The resource was found, but getPath() failed!");
        }

        RES_PATH = Path.of(propertiesPath.replace("/C:", ""));
    }

    private static String apiKey;
    private static int tokens;
    private static LocalDate prevUsage;

    public static void apikey(final String key) {
        if (apiKey != null) {
            LogWriter.error(PropertyStore.class, "Overriding the Api Key is forbidden!");
        } else {
            apiKey = key;
        }
    }

    public static String apikey() { return apiKey; }

    public static void tokens(final int count) {
        tokens = count;
    }

    public static int tokens() { return tokens; }

    public static void increaseTokens(final int count) {
        tokens += count;
        prevUsage = LocalDate.now();
        write();
    }

    private static void write() {
        final StringBuilder sb = new StringBuilder();
        sb.append("apikey=").append(apiKey).append("\ntokens=").append(tokens)
                .append("\nprev_usage=").append(prevUsage.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        try {
            Files.writeString(RES_PATH, sb.toString(), StandardOpenOption.WRITE);
        } catch (IOException e) {
            LogWriter.error(PropertyStore.class, "Unable to write properties!", e);
        }
    }

    public static boolean read() {
        try {
            final List<String> propertyContent = Files.readAllLines(
                    RES_PATH);

            if (propertyContent.isEmpty()) {
                LogWriter.error(PropertyStore.class, "Unable to read properties! Properties are empty!");
                return false;
            }

            for (final String property : propertyContent) {
                final String[] parts = property.split("=");
                if (parts.length != 2) {
                    LogWriter.error(PropertyStore.class, "Unable to read properties! Invalid property: " + property);
                    return false;
                }
                if (parts[0].equals("apikey")) {
                    apikey(parts[1]);
                } else  if (parts[0].equals("tokens")) {
                    tokens(Integer.parseInt(parts[1]));
                } else if (parts[0].equals("prev_usage")) {
                    prevUsage = LocalDate.parse(parts[1], DateTimeFormatter.ofPattern("yyyyMMdd"));
                    if (LocalDate.now().isAfter(prevUsage)) {
                        //reset the tokens on a new day
                        tokens = 0;
                    }
                }
            }
            return true;
        } catch (final Exception ex) {
            LogWriter.error(PropertyStore.class, "Unable to read properties!", ex);
            return false;
        }
    }

    private PropertyStore() {}
}
