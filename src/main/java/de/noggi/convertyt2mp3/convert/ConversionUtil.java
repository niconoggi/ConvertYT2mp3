package de.noggi.convertyt2mp3.convert;

import de.noggi.convertyt2mp3.LogWriter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ConversionUtil {

    public static final String YOUTUBE_QUERY = "https://www.youtube.com/watch?v=";
    private static final String YT_DLP_PATH = "./lib/yt-dlp.exe";
    private static final Path TMP_FOLDER = Path.of("./temp");
    private static final String LIB_PATH = "./lib";

    private static final char NEW_LINE = '\n';

    public static void cleanTemp() {
        if (Files.exists(TMP_FOLDER)) {
            for (final File tmpFile : TMP_FOLDER.toFile().listFiles()) {
                tmpFile.delete();
            }
        }

        LogWriter.info(ConversionUtil.class, "temp directory cleaned");
    }

    public static ConversionDto convert(final String videoId, final String title, final String format) {
        if (!ensureDirectoryExists()) {
            return new ConversionDto("Fehler beim Erstellen des Temp-Ordners");
        }
        final String fullyQualifiedUrl = YOUTUBE_QUERY + videoId;

        String outputTemplate = TMP_FOLDER.toString() + "/" + fixTitle(title) + format;

        ProcessBuilder conversionProcess = new ProcessBuilder(
                YT_DLP_PATH,
                "-x",
                "--audio-format", format.replace(".", ""),
                "-o", outputTemplate,
                "--ffmpeg-location", new File(LIB_PATH).getAbsolutePath(),
                fullyQualifiedUrl
        );

        try {
            conversionProcess.redirectErrorStream(true);
            final Process process = conversionProcess.start();

            readProcessStream(process.getInputStream());
            readProcessStream(process.getErrorStream());

            final int exitCode = process.waitFor();
            if (exitCode != 0) {
                return new ConversionDto("Error during the ytdl process: " + exitCode);
            } else {
                return new ConversionDto(Path.of(outputTemplate));
            }
        } catch (Exception e) {
            if (!unzipFaultyErrorDownload(Path.of(outputTemplate.replace(format, "")))) {
                LogWriter.error(ConversionUtil.class, "Error converting the video!", e);
                return new ConversionDto("Fehler bei der Umwandlung!");
            } else {
                return new ConversionDto(TMP_FOLDER.resolve(title).resolve(format));
            }
        }
    }

    private static void readProcessStream(final InputStream stream) throws IOException {
        // Output lesen!
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream)
        );

        final StringBuilder processStreamContent = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            processStreamContent.append(line).append(NEW_LINE);
        }
        LogWriter.info(ConversionUtil.class, "read process stream: " + processStreamContent);
    }

    private static String fixTitle(final String title) {
        return title.replace("/", "");
    }

    private static boolean ensureDirectoryExists() {
        if (!Files.exists(TMP_FOLDER)) {
            try {
                Files.createDirectory(TMP_FOLDER);
            } catch (IOException e) {
                LogWriter.error(ConversionUtil.class, "Unable to read or create tmp folder");
                return false;
            }
        }

        return true;
    }

    /**
     * It could happen, that a download itself works, but lends an error because of filemetadata.
     * This is the workaround
     */
    private static boolean unzipFaultyErrorDownload(final Path zippedFileLocation) {
        final Path zipPath = Path.of(zippedFileLocation.toString() + ".zip");
        if (!Files.exists(zipPath)) {
            return false;
        }

        //it exists, try to unzip it
        try {
            File destDir = TMP_FOLDER.toFile();

            byte[] buffer = new byte[1024];
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipPath.toFile()));
            ZipEntry zipEntry = zis.getNextEntry();
            if (zipEntry != null) {
                final File destFile = new File(destDir, zipEntry.getName());
                FileOutputStream fos = new FileOutputStream(destFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            } else {
                LogWriter.warn(ConversionUtil.class, "Unable to locate zip entry!");
                return false;
            }
            zis.closeEntry();
            zis.close();
        } catch (final Exception ex) {
            LogWriter.error(ConversionUtil.class,"Unable to locate or unzip downloaded file!", ex);
            return false;
        }
        return true;
    }

    private ConversionUtil() {}
}
