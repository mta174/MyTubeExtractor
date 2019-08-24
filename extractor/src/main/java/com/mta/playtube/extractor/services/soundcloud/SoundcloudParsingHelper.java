package com.mta.playtube.extractor.services.soundcloud;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import com.mta.playtube.extractor.Downloader;
import com.mta.playtube.extractor.NewPipe;
import com.mta.playtube.extractor.channel.ChannelInfoItemsCollector;
import com.mta.playtube.extractor.exceptions.ParsingException;
import com.mta.playtube.extractor.exceptions.ReCaptchaException;
import com.mta.playtube.extractor.services.soundcloud.channel.SoundcloudChannelInfoItemExtractor;
import com.mta.playtube.extractor.services.soundcloud.streams.SoundcloudStreamInfoItemExtractor;
import com.mta.playtube.extractor.stream.StreamInfoItemsCollector;
import com.mta.playtube.extractor.utils.ExtractorConstant;
import com.mta.playtube.extractor.utils.LogHelper;
import com.mta.playtube.extractor.utils.Parser;
import com.mta.playtube.extractor.utils.Parser.RegexException;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static com.mta.playtube.extractor.utils.Utils.replaceHttpWithHttps;

public class SoundcloudParsingHelper {
    private static final String TAG = LogHelper.makeLogTag(SoundcloudParsingHelper.class.getSimpleName());
    private static String clientId;
    
    private SoundcloudParsingHelper() {
    }

    public static String clientId() throws ReCaptchaException, IOException, RegexException {
        if (clientId != null && !clientId.isEmpty()) return clientId;

        Downloader dl = NewPipe.getDownloader();
        String response = dl.download("https://soundcloud.com");

        Document doc = Jsoup.parse(response);
        Element jsElement = doc.select("script[src^=https://a-v2.sndcdn.com/assets/app]").first();

        final String clientIdPattern = ",client_id:\"(.*?)\"";

        try {
            final HashMap<String, String> headers = new HashMap<>();
            headers.put("Range", "bytes=0-16384");
            String js = dl.download(jsElement.attr("src"), headers);

            return clientId = Parser.matchGroup1(clientIdPattern, js);
        } catch (IOException | RegexException ignored) {
            // Ignore it and proceed to download the whole js file
        }

        String js = dl.download(jsElement.attr("src"));
        return clientId = Parser.matchGroup1(clientIdPattern, js);
    }

    public static String toDateString(String time) throws ParsingException {
        try {
            Date date;
            // Have two date formats, one for the 'api.soundc...' and the other 'api-v2.soundc...'.
            try {
                date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(time);
            } catch (Exception e) {
                date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss +0000").parse(time);
            }

            SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return newDateFormat.format(date);
        } catch (ParseException e) {
            throw new ParsingException(e.getMessage(), e);
        }
    }

    /**
     * Call the endpoint "/resolve" of the api.<p>
     * 
     * See https://developers.soundcloud.com/docs/api/reference#resolve
     */
    public static JsonObject resolveFor(Downloader downloader, String url) throws IOException, ReCaptchaException, ParsingException {
        StringBuffer apiUrl = new StringBuffer();
        apiUrl.append("https://api.soundcloud.com/resolve").append("?url=").append(URLEncoder.encode(url, ExtractorConstant.CHARSET_UTF_8)).append("&client_id=").append(clientId());
        try {
            return JsonParser.object().from(downloader.download(apiUrl.toString()));
        } catch (JsonParserException e) {
            throw new ParsingException("Could not parse json response", e);
        }
    }

    /**
     * Fetch the embed player with the apiUrl and return the canonical url (like the permalink_url from the json api).
     *
     * @return the url resolved
     */
    public static String resolveUrlWithEmbedPlayer(String apiUrl) throws IOException, ReCaptchaException{

        String response = NewPipe.getDownloader().download("https://w.soundcloud.com/player/?url=" + URLEncoder.encode(apiUrl, ExtractorConstant.CHARSET_UTF_8));
        return Jsoup.parse(response).select("link[rel=\"canonical\"]").first().attr("abs:href");
    }

    /**
     * Fetch the embed player with the url and return the id (like the id from the json api).
     *
     * @return the resolved id
     */
    public static String resolveIdWithEmbedPlayer(String url) throws IOException, ReCaptchaException, ParsingException {

        String response = NewPipe.getDownloader().download("https://w.soundcloud.com/player/?url=" + URLEncoder.encode(url, ExtractorConstant.CHARSET_UTF_8));
        // handle playlists / sets different and get playlist id via uir field in JSON
        if (url.contains("sets") && !url.endsWith("sets") && !url.endsWith("sets/"))
            return Parser.matchGroup1("\"uri\":\\s*\"https:\\/\\/api\\.soundcloud\\.com\\/playlists\\/((\\d)*?)\"", response);
        return Parser.matchGroup1(",\"id\":(([^}\\n])*?),", response);
    }

    /**
     * Fetch the users from the given api and commit each of them to the collector.
     * <p>
     * This differ from {@link #getUsersFromApi(ChannelInfoItemsCollector, String)} in the sense that they will always
     * get MIN_ITEMS or more.
     *
     * @param minItems the method will return only when it have extracted that many items (equal or more)
     */
    public static String getUsersFromApiMinItems(int minItems, ChannelInfoItemsCollector collector, String apiUrl) throws IOException, ReCaptchaException, ParsingException {
        String nextPageUrl = SoundcloudParsingHelper.getUsersFromApi(collector, apiUrl);

        while (!nextPageUrl.isEmpty() && collector.getItems().size() < minItems) {
            nextPageUrl = SoundcloudParsingHelper.getUsersFromApi(collector, nextPageUrl);
        }

        return nextPageUrl;
    }

    /**
     * Fetch the user items from the given api and commit each of them to the collector.
     *
     * @return the next streams url, empty if don't have
     */
    public static String getUsersFromApi(ChannelInfoItemsCollector collector, String apiUrl) throws IOException, ReCaptchaException, ParsingException {
        String response = NewPipe.getDownloader().download(apiUrl);
        JsonObject responseObject;
        try {
            responseObject = JsonParser.object().from(response);
        } catch (JsonParserException e) {
            throw new ParsingException("Could not parse json response", e);
        }

        JsonArray responseCollection = responseObject.getArray("collection");
        for (Object o : responseCollection) {
            if (o instanceof JsonObject) {
                JsonObject object = (JsonObject) o;
                collector.commit(new SoundcloudChannelInfoItemExtractor(object));
            }
        }

        String nextPageUrl;
        try {
            nextPageUrl = responseObject.getString("next_href");
            if (!nextPageUrl.contains("client_id=")) nextPageUrl += "&client_id=" + SoundcloudParsingHelper.clientId();
        } catch (Exception ignored) {
            nextPageUrl = "";
        }

        return nextPageUrl;
    }

    /**
     * Fetch the streams from the given api and commit each of them to the collector.
     * <p>
     * This differ from {@link #getStreamsFromApi(StreamInfoItemsCollector, String)} in the sense that they will always
     * get MIN_ITEMS or more items.
     *
     * @param minItems the method will return only when it have extracted that many items (equal or more)
     */
    public static String getStreamsFromApiMinItems(int minItems, StreamInfoItemsCollector collector, String apiUrl) throws IOException, ReCaptchaException, ParsingException {
        String nextPageUrl = SoundcloudParsingHelper.getStreamsFromApi(collector, apiUrl);

        while (!nextPageUrl.isEmpty() && collector.getItems().size() < minItems) {
            nextPageUrl = SoundcloudParsingHelper.getStreamsFromApi(collector, nextPageUrl);
        }

        return nextPageUrl;
    }

    /**
     * Fetch the streams from the given api and commit each of them to the collector.
     *
     * @return the next streams url, empty if don't have
     */
    public static String getStreamsFromApi(StreamInfoItemsCollector collector, String apiUrl, boolean charts) throws IOException, ReCaptchaException, ParsingException {
        String response = NewPipe.getDownloader().download(apiUrl);
        JsonObject responseObject;
        try {
            responseObject = JsonParser.object().from(response);
        } catch (JsonParserException e) {
            throw new ParsingException("Could not parse json response", e);
        }

        JsonArray responseCollection = responseObject.getArray("collection");
        for (Object o : responseCollection) {
            if (o instanceof JsonObject) {
                JsonObject object = (JsonObject) o;
                collector.commit(new SoundcloudStreamInfoItemExtractor(charts ? object.getObject("track") : object));
            }
        }

        String nextPageUrl;
        try {
            nextPageUrl = responseObject.getString("next_href");
            if (!nextPageUrl.contains("client_id=")) nextPageUrl += "&client_id=" + SoundcloudParsingHelper.clientId();
        } catch (Exception ignored) {
            nextPageUrl = "";
        }

        return nextPageUrl;
    }

    public static String getStreamsFromApi(StreamInfoItemsCollector collector, String apiUrl) throws ReCaptchaException, ParsingException, IOException {
        return getStreamsFromApi(collector, apiUrl, false);
    }

    @Nonnull
    public static String getUploaderUrl(JsonObject object) {
        String url = object.getObject("user").getString("permalink_url", "");
        return replaceHttpWithHttps(url);
    }

    @Nonnull
    public static String getAvatarUrl(JsonObject object) {
        String url = object.getObject("user", new JsonObject()).getString("avatar_url", "");
        return replaceHttpWithHttps(url);
    }

    public static String getUploaderName(JsonObject object) {
        return object.getObject("user").getString("username", "");
    }
}
