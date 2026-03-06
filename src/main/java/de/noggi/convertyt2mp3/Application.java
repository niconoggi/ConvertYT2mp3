package de.noggi.convertyt2mp3;

import de.noggi.convertyt2mp3.convert.ConversionDto;
import de.noggi.convertyt2mp3.convert.ConversionUtil;
import de.noggi.convertyt2mp3.ui.UIFrame;


public class Application {

    private static final String END_CONSOLE_APP_COMMAND = "E";

    public static void main(final String[] args) {
        //TODO: If at any point this actually goes "live" as an application,
        //set this flag to false to enable UI
        final boolean applicationModeConsoleFlag = true;
        LogWriter.create(applicationModeConsoleFlag);
        setup(applicationModeConsoleFlag);
        if (applicationModeConsoleFlag) {
            startConsoleApplication();
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

    private static void startConsoleApplication() {
        String input = null;
        while (!END_CONSOLE_APP_COMMAND.equalsIgnoreCase(input = getConsoleInput())) {
            if (input.startsWith(ConversionUtil.YOUTUBE_QUERY)) {
                //the ConversionUtil expects the videoID, not the full youtube URL
                input = input.replace(ConversionUtil.YOUTUBE_QUERY, "");
            }
            System.out.println("trying to download " + input + " as mp3...");
            final ConversionDto result = ConversionUtil.convert(input, "console_app_download", ".mp3");
            System.out.println(result.toString());
        }
        System.out.println("Exit command received! Stopping application");
    }

    private static String getConsoleInput() {
        System.out.println("Input URL (or 'E' to exit)");
        return System.console().readLine().trim();
    }

}
