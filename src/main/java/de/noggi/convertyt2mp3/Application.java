package de.noggi.convertyt2mp3;

import de.noggi.convertyt2mp3.convert.ConversionDto;
import de.noggi.convertyt2mp3.convert.ConversionUtil;
import de.noggi.convertyt2mp3.ui.UIFrame;


public class Application {

    public static void main(final String[] args) {
        //TODO: If at any point this actually goes "live" as an application,
        //set this flag to false to enable UI
        final boolean applicationModeConsoleFlag = true;
        LogWriter.create(applicationModeConsoleFlag);
        setup(applicationModeConsoleFlag);
        if (applicationModeConsoleFlag) {
            ConsoleApplication.start();
        } else {
            //this starts the ui based application
            new UIFrame();
        }

    }

    private static void setup(final boolean consoleApp) {
        ConversionUtil.cleanTemp();
        LogWriter.info(Application.class, "Application started");
        if (!PropertyStore.read(!consoleApp)) {
            return;
        }
        LogWriter.info(Application.class, "Properties read successfully");
    }

    private static class ConsoleApplication {

        private static final String END_CONSOLE_APP_COMMAND = "E";
        private static final String BACK_TO_QUERY_COMMAND = "B";

        private static final String PROMPT_QUERY = "Input URL (or 'E' to exit)";
        private static final String PROMPT_NAME = "Input downloaded file name (or 'E' to exit; or 'B' to go back to the query input)";

        static void start() {
            String input;
            boolean inputModeQuery = true;
            String videoQuery = null;
            while (!END_CONSOLE_APP_COMMAND.equalsIgnoreCase(
                    input = getConsoleInput(inputModeQuery ? PROMPT_QUERY : PROMPT_NAME)
            )) {
                if (inputModeQuery) {
                    videoQuery = getVideoID(input);
                    inputModeQuery = false;
                } else if (BACK_TO_QUERY_COMMAND.equalsIgnoreCase(input)) {
                    inputModeQuery = true;
                } else {
                    //input variable counts as name during this else block
                    System.out.println("Downloading " + videoQuery + " as mp3...");
                    final ConversionDto result = ConversionUtil.convert(videoQuery, input, ".mp3");
                    System.out.println("Finished download! Result: " + result);
                    //reset the loop to start over
                    inputModeQuery = true;
                    videoQuery = null;
                }
            }
            System.out.println("Exit command received! Stopping application");
        }

        private static String getConsoleInput(final String prompt) {
            System.out.println(prompt);
            return System.console().readLine().trim();
        }

        /**
         * Given a String x, this removes the youtube domain from it and returns the result. <br>
         * <b>WARNING:</b> If the input string does not conform to either a video ID or the full
         * youtube query, the result is not going to be a youtube video ID
         */
        private static String getVideoID(final String input) {
            if (input.startsWith(ConversionUtil.YOUTUBE_QUERY)) {
                //the ConversionUtil expects the videoID, not the full youtube URL
                return input.replace(ConversionUtil.YOUTUBE_QUERY, "");
            }
            return input;
        }

        private ConsoleApplication() {}
    }

}
