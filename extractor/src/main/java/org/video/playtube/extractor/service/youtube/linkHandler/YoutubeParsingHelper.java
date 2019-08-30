package org.video.playtube.extractor.service.youtube.linkHandler;


import org.video.playtube.extractor.exception.ParsingException;

import java.net.URL;

public class YoutubeParsingHelper {

    private YoutubeParsingHelper() {
    }

    private static boolean isHTTP(URL url) {
        // make sure its http or https
        String protocol = url.getProtocol();
        if (!protocol.equals("http") && !protocol.equals("https")) {
            return false;
        }

        boolean usesDefaultPort = url.getPort() == url.getDefaultPort();
        boolean setsNoPort = url.getPort() == -1;

        return setsNoPort || usesDefaultPort;
    }

    public static boolean isYoutubeURL(URL url) {
        // make sure its http or https
        if (!isHTTP(url))
            return false;

        // make sure its a known youtube url
        String host = url.getHost();
        return host.equalsIgnoreCase("youtube.com") || host.equalsIgnoreCase("www.youtube.com")
                || host.equalsIgnoreCase("m.youtube.com");
    }

    public static boolean isYoutubeServiceURL(URL url) {
        String host = url.getHost();
        return host.equalsIgnoreCase("www.youtube-nocookie.com") || host.equalsIgnoreCase("youtu.be");
    }

    public static boolean isHooktubeURL(URL url) {
        String host = url.getHost();
        return host.equalsIgnoreCase("hooktube.com");
    }

    public static boolean isInvidioURL(URL url) {
        String host = url.getHost();
        return host.equalsIgnoreCase("invidio.us") || host.equalsIgnoreCase("www.invidio.us");
    }

    public static long parseDurationString(String input)
            throws ParsingException, NumberFormatException {

        // If time separator : is not detected, try . instead

        final String[] splitInput = input.contains(":")
                ? input.split(":")
                : input.split("\\.");

        String days = "0";
        String hours = "0";
        String minutes = "0";
        final String seconds;

        switch (splitInput.length) {
            case 4:
                days = splitInput[0];
                hours = splitInput[1];
                minutes = splitInput[2];
                seconds = splitInput[3];
                break;
            case 3:
                hours = splitInput[0];
                minutes = splitInput[1];
                seconds = splitInput[2];
                break;
            case 2:
                minutes = splitInput[0];
                seconds = splitInput[1];
                break;
            case 1:
                seconds = splitInput[0];
                break;
            default:
                throw new ParsingException("Error duration string with unknown format: " + input);
        }
        return ((((Long.parseLong(days) * 24)
                + Long.parseLong(hours) * 60)
                + Long.parseLong(minutes)) * 60)
                + Long.parseLong(seconds);
    }
}
