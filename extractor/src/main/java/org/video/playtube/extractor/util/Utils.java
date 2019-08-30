package org.video.playtube.extractor.util;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.video.playtube.extractor.exception.ParsingException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class Utils {
    private static final String TAG = Utils.class.getSimpleName();
    private Utils() {
        //no instance
    }

    /**
     * Remove all non-digit characters from a string.<p>
     * Examples:<p>
     * <ul><li>1 234 567 view -&gt; 1234567</li>
     * <li>$31,133.124 -&gt; 31133124</li></ul>
     *
     * @param toRemove string to remove non-digit chars
     * @return a string that contains only digits
     */
    public static String removeNonDigitCharacters(String toRemove) {
        return toRemove.replaceAll("\\D+", "");
    }

    /**
     * Check if the url matches the pattern.
     *
     * @param pattern the pattern that will be used to check the url
     * @param url     the url to be tested
     */
    public static void checkUrl(String pattern, String url) throws ParsingException {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("Url can't be null or empty");
        }

        if (!Parser.isMatch(pattern, url.toLowerCase())) {
            throw new ParsingException("Url don't match the pattern");
        }
    }

    public static void printErrors(List<Throwable> errors) {
        for (Throwable e : errors) {
            e.printStackTrace();
            System.err.println("----------------");
        }
    }

    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";

    public static String replaceHttpWithHttps(final String url) {
        if (url == null) return null;

        if (!url.isEmpty() && url.startsWith(HTTP)) {
            return HTTPS + url.substring(HTTP.length());
        }
        return url;
    }

    /**
     * get the value of a URL-query by name.
     * if a url-query is give multiple times, only the value of the first query is returned
     *
     * @param url           the url to be used
     * @param parameterName the pattern that will be used to check the url
     * @return a string that contains the value of the query parameter or null if nothing was found
     */
    public static String getQueryValue(URL url, String parameterName) {
        String urlQuery = url.getQuery();

        if (urlQuery != null) {
            for (String param : urlQuery.split("&")) {
                String[] params = param.split("=", 2);

                String query;
                try {
                    query = URLDecoder.decode(params[0], "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    LogHelper.i(TAG, "Cannot decode string with UTF-8. using the string without decoding", e.getMessage());
                    //e.printStackTrace();
                    query = params[0];
                }

                if (query.equals(parameterName)) {
                    try {
                        return URLDecoder.decode(params[1], "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        LogHelper.i(TAG, "Cannot decode string with UTF-8. using the string without decoding", e.getMessage());
                        //e.printStackTrace();
                        return params[1];
                    }
                }
            }
        }

        return null;
    }

    /**
     * converts a string to a URL-Object.
     * defaults to HTTP if no protocol is given
     *
     * @param url the string to be converted to a URL-Object
     * @return a URL-Object containing the url
     */
    public static URL stringToURL(String url) throws MalformedURLException {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            // if no protocol is given try prepending "http://"
            if (e.getMessage().equals("no protocol: " + url)) {
                return new URL(HTTP + url);
            }

            throw e;
        }
    }

    public static boolean isHTTP(URL url) {
        // make sure its http or https
        String protocol = url.getProtocol();
        if (!protocol.equals("http") && !protocol.equals("https")) {
            return false;
        }

        boolean usesDefaultPort = url.getPort() == url.getDefaultPort();
        boolean setsNoPort = url.getPort() == -1;

        return setsNoPort || usesDefaultPort;
    }
    
    public static String removeUTF8BOM(String s) {
        if (s.startsWith("\uFEFF")) {
            s = s.substring(1);
        }
        if (s.endsWith("\uFEFF")) {
            s = s.substring(0,  s.length()-1);
        }
        return s;
    }


    public static int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Throwable e) {
            //Utils.showDebugTrace(e);
            return 0;
        }
    }

    public static long parseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (Throwable e) {
            //Utils.showDebugTrace(e);
            return 0;
        }
    }



    public static boolean isNullOrEmpty(String input) {
        return input == null || input.length() == 0;
    }



    public static int getInt(Object value, int defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (Throwable e) {
            //Utils.showDebugTrace(e);
            return defaultValue;
        }
    }

    public static Object toObject(Class clazz, String value) {
        if (boolean.class == clazz)
            return Boolean.parseBoolean(value);
        if (byte.class == clazz)
            return Byte.parseByte(value);
        if (short.class == clazz)
            return Short.parseShort(value);
        if (int.class == clazz)
            return Integer.parseInt(value);
        if (long.class == clazz)
            return Long.parseLong(value);
        if (float.class == clazz)
            return Float.parseFloat(value);
        if (double.class == clazz)
            return Double.parseDouble(value);
        return value;
    }

    public static int getInt(Object value) {
        if (value == null ) {
            return 0;
        }
        if(value.toString().length() == 0) {
            return 0;
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (Throwable e) {
            //Utils.showDebugTrace(e);
            return 0;
        }
    }

    public static String serializeDocument(org.w3c.dom.Document doc) throws TransformerFactoryConfigurationError, TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        StringWriter stw = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(stw));
        return stw.toString();
    }

    public static int convertToSeconds(String duration) {
        String temp = "";
        int seconds = 0, hour = 0, minute = 0, second = 0;
        // Starts in position 2 to ignore P and T characters
        for (int i = 2; i < duration.length(); ++ i){
            // Put current char in c
            char c = duration.charAt(i);
            // Put number in temp
            if (c >= '0' && c <= '9') {
                temp = temp + c;
            }
            else {
                // Test char after number
                switch (c){
                    case 'H' : // Deal with hours
                        hour = parseInt(temp);
                        break;
                    case 'M' : // Deal with minutes
                        minute = parseInt(temp);
                        break;
                    case  'S': // Deal with seconds
                        second = parseInt(temp);
                        break;
                }
                temp = "";
            } // else
        } // for
        if(hour == 0 && minute == 0){ // Only seconds
            seconds = second;
        }
        else {
            if (hour == 0) { // Minutes and seconds
                seconds = (minute  * 60) + second;
            }
            else { // Hours, minutes and seconds
                seconds = (hour * 3600) +  (minute * 60) +  second;
            }
        }
        return seconds;
    }

    private static char[] charFm = new char[]{'K', 'M', 'B', 'T'};
    /**
     * Recursive implementation, invokes itself for each factor of a thousand, increasing the class on each invokation.
     * @param n the number to format
     * @param iteration in fact this is the class from the array c
     * @return a String representing the number n formatted in a cool looking way.
     */
    public static String customerFormatNumber(double n, int iteration) {
        if(n < 1000 && iteration == 0) {
            return  String.valueOf((int)n);
        }
        double d = ((long) n / 100) / 10.0;
        boolean isRound = (d * 10) %10 == 0;//true if the decimal part is equal to 0 (then it's trimmed anyway)
        return (d < 1000? //this determines the class, i.e. 'k', 'm' etc
                ((d > 99.9 || isRound || (!isRound && d > 9.99)? //this decides whether to trim the decimals
                        (int) d * 10 / 10 : d + "" // (int) d * 10 / 10 drops the decimal
                ) + "" + charFm[iteration])  : customerFormatNumber(d, iteration+1));
    }



    // use this to increase readUTF performance - a lots
    public static StringBuffer sb = new StringBuffer();
    public static String utf8ToUnicode(byte[] utf8, int off, int len) {
        if ((utf8 == null) || (len - off <= 0)) {
            return "";
        }
       /* if (len == 0) {
            len = utf8.length;
        }*/

        int y, x, w, v, u;
        int sFinal;

        StringBuffer s;
        if (len < Utils.sb.length() / 2) {
            s = Utils.sb;
        } else {
            s = new StringBuffer();
        }
        s.setLength(0);

        int z;

        for (int i = off; i < len; i++) {
            z = utf8[i];
            if (z < 0) {
                z += 256;
            }
            if (z <= 127) {
                s.append((char) z);
            } else {
                sFinal = 0;
                if (z >= 192 && z <= 223) {
                    y = utf8[i + 1];
                    if (y < 0) {
                        y += 256;
                    }
                    sFinal = ((z - 192) * 64 + (y - 128));
                    i += 1;
                } else if (z >= 224 && z <= 239) {
                    // character is three bytes
                    y = utf8[i + 1];
                    if (y < 0) {
                        y += 256;
                    }
                    x = utf8[i + 2];
                    if (x < 0) {
                        x += 256;
                    }
                    sFinal = ((z - 224) * 4096 + (y - 128) * 64 + (x - 128));
                    i += 2;
                } else if (z >= 240 && z <= 247) {
                    // character is four bytes
                    y = utf8[i + 1];
                    if (y < 0) {
                        y += 256;
                    }
                    x = utf8[i + 2];
                    if (x < 0) {
                        x += 256;
                    }
                    w = utf8[i + 3];
                    if (w < 0) {
                        w += 256;
                    }
                    sFinal = ((z - 240) * 262144 + (y - 128) * 4096 + (x - 128) * 64 + (w - 128));
                    i += 3;
                } else if (z >= 248 && z <= 251) {
                    // character is five bytes
                    y = utf8[i + 1];
                    if (y < 0) {
                        y += 256;
                    }
                    x = utf8[i + 2];
                    if (x < 0) {
                        x += 256;
                    }
                    w = utf8[i + 3];
                    if (w < 0) {
                        w += 256;
                    }
                    v = utf8[i + 4];
                    if (z < 0) {
                        z += 256;
                    }
                    sFinal += ((z - 248) * 16777216 + (y - 128) * 262144
                            + (x - 128) * 4096 + (w - 128) * 64 + (v - 128));
                    i += 4;
                } else if (z >= 252 && z <= 253) {
                    // character is six bytes
                    y = utf8[i + 1];
                    if (y < 0) {
                        y += 256;
                    }
                    x = utf8[i + 2];
                    if (x < 0) {
                        x += 256;
                    }
                    w = utf8[i + 3];
                    if (w < 0) {
                        w += 256;
                    }
                    v = utf8[i + 4];
                    if (v < 0) {
                        v += 256;
                    }
                    u = utf8[i + 5];
                    if (u < 0) {
                        u += 256;
                    }
                    sFinal += ((z - 252) * 1073741824 + (y - 128) * 16777216
                            + (x - 128) * 262144 + (w - 128) * 4096 + (v - 128) * 64 + (u - 128));
                    i += 5;
                }
                s.append((char) sFinal);
            }
        }

        return s.toString();
    }

    public  static String getHostMyData() {
        return  new String(new CryptHelper().decrypt(ExtractorConstant.MY_URL_DATA));
    }

    public static String buildUrlRelatedVideo(String videoId, String key,String pageToken) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(ExtractorConstant.YOUTUBE_URL).append("search?part=id&type=video&videoEmbeddable=true&maxResults=20&fields=nextPageToken,items(id(videoId))&relatedToVideoId=").append(videoId).append("&key=").append(key).append("&pageToken=").append(pageToken);
        return buffer.toString();
    }

    public static String buildUrlInfoVideo(String videoId, String key) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(ExtractorConstant.YOUTUBE_URL).append("videos?part=contentDetails,snippet,statistics&fields=items(id,snippet(publishedAt,title,channelId,thumbnails(default(url)),channelTitle),contentDetails(duration),statistics(viewCount))&id=").append(videoId).append("&key=").append(key);
        return buffer.toString();
    }

    public static String buildInfoVideo(String videoId, String key) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(ExtractorConstant.YOUTUBE_URL).append("videos?part=snippet,contentDetails,statistics&id=").append(videoId).append("&key=").append(key);
        return buffer.toString();
    }

    public static String buildListIdQuery(Vector<String> data) {
        StringBuffer buffer = new StringBuffer();
        for (String str : data) {
            if (Utils.isNullOrEmpty(buffer.toString())) {
                buffer.append(str);
            }
            else {
                buffer.append(",");
                buffer.append(str);
            }
        }
        return buffer.toString();
    }

    public static String getValueId(String url) {
        if (url != null) {
            for (String param : url.split("&")) {
                String[] params = param.split("=", 2);
                String query;
                try {
                    query = URLDecoder.decode(params[0], "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    LogHelper.i(TAG, "Cannot decode string with UTF-8. using the string without decoding", e.getMessage());
                    //e.printStackTrace();
                    query = params[0];
                }

                if (query.equals("id")) {
                    try {
                        return URLDecoder.decode(params[1], "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        LogHelper.i(TAG, "Cannot decode string with UTF-8. using the string without decoding", e.getMessage());
                        //e.printStackTrace();
                        return params[1];
                    }
                }
            }
        }
        return null;
    }

    public static  String getRandomKey(String data) {
        String[] aryData = data.split(",");
        int index = new Random().nextInt(aryData.length);
        return aryData[index];
    }

    public static String getTitleByKey(String key) {
        String title = "";
        HashMap<String, String> hashMap = ExtractorConstant.getHashMap();
        if(hashMap.containsKey(key)) {
            return hashMap.get(key);
        }
        return  title;
    }
}