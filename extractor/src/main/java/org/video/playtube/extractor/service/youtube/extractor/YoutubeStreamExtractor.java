package org.video.playtube.extractor.service.youtube.extractor;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptableObject;
import org.video.playtube.extractor.*;
import org.video.playtube.extractor.exception.ContentNotAvailableException;
import org.video.playtube.extractor.exception.ExtractionException;
import org.video.playtube.extractor.exception.ParsingException;
import org.video.playtube.extractor.exception.ReCaptchaException;
import org.video.playtube.extractor.linkhandler.LinkHandler;
import org.video.playtube.extractor.service.youtube.ItagItem;
import org.video.playtube.extractor.stream.*;
import org.video.playtube.extractor.util.ExtractorConstant;
import org.video.playtube.extractor.util.Localization;
import org.video.playtube.extractor.util.LogHelper;
import org.video.playtube.extractor.util.Parser;
import org.video.playtube.extractor.util.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubeStreamExtractor extends StreamExtractor {
    private static final String TAG = YoutubeStreamExtractor.class.getSimpleName();

    /*//////////////////////////////////////////////////////////////////////////
    // Exceptions
    //////////////////////////////////////////////////////////////////////////*/

    public class DecryptException extends ParsingException {
        DecryptException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public class GemaException extends ContentNotAvailableException {
        GemaException(String message) {
            super(message);
        }
    }

    public class SubtitlesException extends ContentNotAvailableException {
        SubtitlesException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /*//////////////////////////////////////////////////////////////////////////*/

    private Document doc;

    private JsonObject jsonObject;

    @Nullable
    private JsonObject playerArgs;
    @Nonnull
    private final Map<String, String> videoInfoPage = new HashMap<>();

    @Nonnull
    private List<SubtitlesInfo> subtitlesInfos = new ArrayList<>();

    private boolean isAgeRestricted;
    private static boolean isUseYoutubeApi = false;

    public YoutubeStreamExtractor(StreamingService service, LinkHandler linkHandler, Localization localization) {
        super(service, linkHandler, localization);
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Impl
    //////////////////////////////////////////////////////////////////////////*/

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        assertPageFetched();
        if(isUseYoutubeApi) {
            if(jsonObject != null) {
                return jsonObject.getObject(ExtractorConstant.BlockJson.SNIPPET).getString(ExtractorConstant.Video.TITLE);
            } else {
                throw new ParsingException("Could not get the title");
            }
        }
        else {
            String name = getStringFromMetaData("title");
            if (name == null) {
                // Fallback to HTML method
                try {
                    name = doc.select("meta[name=title]").attr(CONTENT);
                } catch (Exception e) {
                    throw new ParsingException("Could not get the title", e);
                }
            }
            if (name == null || name.isEmpty()) {
                throw new ParsingException("Could not get the title");
            }
            return name;
        }
    }

    @Nonnull
    @Override
    public String getUploadDate() throws ParsingException {
        assertPageFetched();
        if(isUseYoutubeApi) {
            if(jsonObject != null) {
                return jsonObject.getObject(ExtractorConstant.BlockJson.SNIPPET).getString(ExtractorConstant.Video.PUBLISHEDAT);
            } else {
                throw new ParsingException("Could not get upload date");
            }
        }
        else {
            try {
                return doc.select("meta[itemprop=datePublished]").attr(CONTENT);
            } catch (Exception e) {//todo: add fallback method
                throw new ParsingException("Could not get upload date", e);
            }
        }
    }

    @Nonnull
    @Override
    public String getThumbnailUrl() throws ParsingException {
        assertPageFetched();
        if(isUseYoutubeApi) {
            if(jsonObject != null) {
                return jsonObject.getObject(ExtractorConstant.BlockJson.SNIPPET).getObject(ExtractorConstant.Video.THUMBNAILS).getObject("default").getString("url");
            } else {
                throw new ParsingException("Could not get thumbnail url");
            }
        }
        else {
            // Try to get high resolution thumbnail first, if it fails, use low res from the player instead
            try {
                return doc.select("link[itemprop=\"thumbnailUrl\"]").first().attr("abs:href");
            } catch (Exception ignored) {
                // Try other method...
            }

            try {
                if (playerArgs != null && playerArgs.isString("thumbnail_url"))
                    return playerArgs.getString("thumbnail_url");
            } catch (Exception ignored) {
                // Try other method...
            }

            try {
                return videoInfoPage.get("thumbnail_url");
            } catch (Exception e) {
                throw new ParsingException("Could not get thumbnail url", e);
            }
        }
    }

    @Nonnull
    @Override
    public String getDescription() throws ParsingException {
        assertPageFetched();
        if(isUseYoutubeApi) {
            if(jsonObject != null) {
                return jsonObject.getObject(ExtractorConstant.BlockJson.SNIPPET).getString("description");
            } else {
                throw new ParsingException("Could not get the description");
            }
        }
        else {
            try {
                return parseHtmlAndGetFullLinks(doc.select("p[id=\"eow-description\"]").first().html());
            } catch (Exception e) {
                throw new ParsingException("Could not get the description", e);
            }
        }
    }

    // onclick="yt.www.watch.player.seekTo(0*3600+00*60+00);return false;"
    // :00 is NOT recognized as a timestamp in description or comment.
    // 0:00 is recognized in both description and comment.
    // https://www.youtube.com/watch?v=4cccfDXu1vA
    private final static Pattern DESCRIPTION_TIMESTAMP_ONCLICK_REGEX = Pattern.compile(
            "seekTo\\("
                    + "(?:(\\d+)\\*3600\\+)?"  // hours?
                    + "(\\d+)\\*60\\+"  // minutes
                    + "(\\d+)"  // seconds
                    + "\\)");

    @SafeVarargs
    private static <T> T coalesce(T... args) {
        for (T arg : args) {
            if (arg != null) return arg;
        }
        throw new IllegalArgumentException("all arguments to coalesce() were null");
    }

    private String parseHtmlAndGetFullLinks(String descriptionHtml) throws MalformedURLException, UnsupportedEncodingException, ParsingException {
        final Document description = Jsoup.parse(descriptionHtml, getUrl());
        for(Element a : description.select("a")) {
            final String rawUrl = a.attr("abs:href");
            final URL redirectLink = new URL(rawUrl);
            final Matcher onClickTimestamp;
            final String queryString;
            if ((onClickTimestamp = DESCRIPTION_TIMESTAMP_ONCLICK_REGEX.matcher(a.attr("onclick")))
                    .find()) {
                a.removeAttr("onclick");

                String hours = coalesce(onClickTimestamp.group(1), "0");
                String minutes = onClickTimestamp.group(2);
                String seconds = onClickTimestamp.group(3);

                int timestamp = 0;
                timestamp += Integer.parseInt(hours) * 3600;
                timestamp += Integer.parseInt(minutes) * 60;
                timestamp += Integer.parseInt(seconds);

                String setTimestamp = "&t=" + timestamp;

                // Even after clicking https://youtu.be/...?t=6,
                // getUrl() is https://www.youtube.com/watch?v=..., never youtu.be, never &t=.
                a.attr("href", getUrl() + setTimestamp);

            } else if((queryString = redirectLink.getQuery()) != null) {
                // if the query string is null we are not dealing with a redirect link,
                // so we don't need to override it.
                final String link = Parser.compatParseMap(queryString).get("q");

                if(link != null) {
                    // if link is null the a tag is a hashtag.
                    // They refer to the youtube search. We do not handle them.
                    a.text(link);
                    a.attr("href", link);
                } else if(redirectLink.toString().contains("https://www.youtube.com/")) {
                    a.text(redirectLink.toString());
                    a.attr("href", redirectLink.toString());
                }
            } else if(redirectLink.toString().contains("https://www.youtube.com/")) {
                descriptionHtml = descriptionHtml.replace(rawUrl, redirectLink.toString());
                a.text(redirectLink.toString());
                a.attr("href", redirectLink.toString());
            }
        }
        return description.select("body").first().html();
    }

    @Override
    public int getAgeLimit() throws ParsingException {
        assertPageFetched();
        if (!isAgeRestricted) {
            return NO_AGE_LIMIT;
        }
        try {
            LogHelper.i(TAG, "getUploadDate", Integer.valueOf(doc.select("meta[property=\"og:restrictions:age\"]").attr(CONTENT).replace("+", "")));
            return Integer.valueOf(doc.select("meta[property=\"og:restrictions:age\"]").attr(CONTENT).replace("+", ""));
        } catch (Exception e) {
            throw new ParsingException("Could not get age restriction");
        }
    }

    @Override
    public long getLength() throws ParsingException {
        assertPageFetched();
        if(isUseYoutubeApi) {
            if(jsonObject != null) {
                return Utils.convertToSeconds(jsonObject.getObject(ExtractorConstant.BlockJson.CONTENT_DETAILS).getString(ExtractorConstant.Video.DURATION));
            } else {
                throw new ParsingException("Could not get video length");
            }
        }
        else {
            final JsonObject playerResponse;
            try {
                final String pr;
                if(playerArgs != null) {
                    pr = playerArgs.getString("player_response");
                } else {
                    pr = videoInfoPage.get("player_response");
                }
                playerResponse = JsonParser.object().from(pr);
            } catch (Exception e) {
                throw new ParsingException("Could not get playerResponse", e);
            }

            // try getting duration from playerargs
            try {
                String durationMs = playerResponse
                        .getObject("streamingData")
                        .getArray("formats")
                        .getObject(0)
                        .getString("approxDurationMs");
                return Long.parseLong(durationMs)/1000;
            } catch (Exception e) {
            }

            //try getting value from age gated video
            try {
                String duration = playerResponse
                        .getObject("videoDetails")
                        .getString("lengthSeconds");
                return Long.parseLong(duration);
            } catch (Exception e) {
                throw new ParsingException("Every methode to get the duration has failed: ", e);
            }
        }
    }

    /**
     * Attempts to parse (and return) the offset to start playing the video from.
     *
     * @return the offset (in seconds), or 0 if no timestamp is found.
     */
    @Override
    public long getTimeStamp() throws ParsingException {
        if(isUseYoutubeApi) {
            return 0;
        }
        else {
            return getTimestampSeconds("((#|&|\\?)t=\\d{0,3}h?\\d{0,3}m?\\d{1,3}s?)");
        }
    }

    @Override
    public long getViewCount() throws ParsingException {
        assertPageFetched();
        if(isUseYoutubeApi) {
            if(jsonObject != null) {
                return Long.parseLong(jsonObject.getObject(ExtractorConstant.BlockJson.STATISTICS).getString("viewCount"));
            } else {
                throw new ParsingException("Could not get number of view");
            }
        }
        else {
            try {
                return Long.parseLong(doc.select("meta[itemprop=interactionCount]").attr(CONTENT));
            } catch (Exception e) {//todo: find fallback method
                throw new ParsingException("Could not get number of view", e);
            }
        }
    }

    @Override
    public long getLikeCount() throws ParsingException {
        assertPageFetched();
        if(isUseYoutubeApi) {
            if(jsonObject != null) {
                return Long.parseLong(jsonObject.getObject(ExtractorConstant.BlockJson.STATISTICS).getString("likeCount"));
            } else {
                throw new ParsingException("Could not get like count");
            }
        }
        else {
            String likesString = "";
            try {
                Element button = doc.select("button.like-button-renderer-like-button").first();
                try {
                    likesString = button.select("span.yt-uix-button-content").first().text();
                } catch (NullPointerException e) {
                    //if this kicks in our button has no content and therefore likes/dislikes are disabled
                    return -1;
                }
                return Integer.parseInt(Utils.removeNonDigitCharacters(likesString));
            } catch (NumberFormatException nfe) {
                throw new ParsingException("Could not parse \"" + likesString + "\" as an Integer", nfe);
            } catch (Exception e) {
                throw new ParsingException("Could not get like count", e);
            }
        }
    }

    @Override
    public long getDislikeCount() throws ParsingException {
        assertPageFetched();
        if(isUseYoutubeApi) {
            if(jsonObject != null) {
                return Long.parseLong(jsonObject.getObject(ExtractorConstant.BlockJson.STATISTICS).getString("dislikeCount"));
            } else {
                throw new ParsingException("Could not get dislike count");
            }
        }
        else {
            String dislikesString = "";
            try {
                Element button = doc.select("button.like-button-renderer-dislike-button").first();
                try {
                    dislikesString = button.select("span.yt-uix-button-content").first().text();
                } catch (NullPointerException e) {
                    //if this kicks in our button has no content and therefore likes/dislikes are disabled
                    return -1;
                }
                return Integer.parseInt(Utils.removeNonDigitCharacters(dislikesString));
            } catch (NumberFormatException nfe) {
                throw new ParsingException("Could not parse \"" + dislikesString + "\" as an Integer", nfe);
            } catch (Exception e) {
                throw new ParsingException("Could not get dislike count", e);
            }
        }
    }

    @Nonnull
    @Override
    public String getUploaderUrl() throws ParsingException {
        assertPageFetched();
        if(isUseYoutubeApi) {
            if(jsonObject != null) {
                String authorId = jsonObject.getObject(ExtractorConstant.BlockJson.SNIPPET).getString(ExtractorConstant.Video.CHANNEL_ID);
                return ExtractorConstant.EXTRACTOR_PRE_CHANNEL + authorId;
            } else {
                throw new ParsingException("Could not get channel link");
            }
        }
        else {
            try {
                return doc.select("div[class=\"yt-user-info\"]").first().children().select("a").first().attr("abs:href");
            } catch (Exception e) {
                throw new ParsingException("Could not get channel link", e);
            }
        }
    }


    @Nullable
    private String getStringFromMetaData(String field) {
        assertPageFetched();
        String value = null;
        if(playerArgs != null) {
            // This can not fail
            value = playerArgs.getString(field);
        }
        if(value == null) {
            // This can not fail too
            value = videoInfoPage.get(field);
        }
        return value;
    }

    @Nonnull
    @Override
    public String getUploaderName() throws ParsingException {
        assertPageFetched();
        if(isUseYoutubeApi) {
            if(jsonObject != null) {
                return jsonObject.getObject(ExtractorConstant.BlockJson.SNIPPET).getString("channelTitle");
            } else {
                throw new ParsingException("Could not get uploader name");
            }
        }
        else {
            String name = getStringFromMetaData("author");
            if (name == null) {
                try {
                    // Fallback to HTML method
                    name = doc.select("div.yt-user-info").first().text();
                } catch (Exception e) {
                    throw new ParsingException("Could not get uploader name", e);
                }
            }
            if (name == null || name.isEmpty()) {
                throw new ParsingException("Could not get uploader name");
            }
            return name;
        }
    }

    @Nonnull
    @Override
    public String getUploaderAvatarUrl() throws ParsingException {
        assertPageFetched();
        if(isUseYoutubeApi) {
            return "";
        }
        else {
            try {
                return doc.select("a[class*=\"yt-user-photo\"]").first()
                        .select("img").first().attr("abs:data-thumb");
            } catch (Exception e) {//todo: add fallback method
                throw new ParsingException("Could not get uploader thumbnail URL.", e);
            }
        }
    }

    @Nonnull
    @Override
    public String getDashMpdUrl() throws ParsingException {
        assertPageFetched();
        String dashManifestUrl = "";
        if(isUseYoutubeApi) {
            return dashManifestUrl;
        }
        else {
            try {
                if (videoInfoPage.containsKey("dashmpd")) {
                    dashManifestUrl = videoInfoPage.get("dashmpd");
                } else if (playerArgs != null && playerArgs.isString("dashmpd")) {
                    dashManifestUrl = playerArgs.getString("dashmpd", "");
                } else {
                    return "";
                }

                if (!dashManifestUrl.contains("/signature/")) {
                    String encryptedSig = Parser.matchGroup1("/s/([a-fA-F0-9\\.]+)", dashManifestUrl);
                    String decryptedSig;

                    decryptedSig = decryptSignature(encryptedSig, decryptionCode);
                    dashManifestUrl = dashManifestUrl.replace("/s/" + encryptedSig, "/signature/" + decryptedSig);
                }
                return dashManifestUrl;
            } catch (Exception e) {
                throw new ParsingException("Could not get dash manifest url", e);
            }
        }
    }

    @Nonnull
    @Override
    public String getHlsUrl() throws ParsingException {
        assertPageFetched();
        String hlsvp = "";
        if(isUseYoutubeApi) {
            return hlsvp;
        }
        else {
            try {
                if (playerArgs != null) {
                    if (playerArgs.isString("hlsvp")) {
                        hlsvp = playerArgs.getString("hlsvp", "");
                    } else {
                        hlsvp = JsonParser.object().from(playerArgs.getString("player_response", "{}"))
                                .getObject("streamingData", new JsonObject())
                                .getString("hlsManifestUrl", "");
                    }
                }
                return hlsvp;
            } catch (Exception e) {
                throw new ParsingException("Could not get hls manifest url", e);
            }
        }
    }

    @Override
    public List<AudioStream> getAudioStreams() throws IOException, ExtractionException {
        assertPageFetched();
        List<AudioStream> audioStreams = new ArrayList<>();
        if(isUseYoutubeApi) {
            return audioStreams;
        }
        else {
            try {
                for (Map.Entry<String, ItagItem> entry : getItags(ADAPTIVE_FMTS, ItagItem.ItagType.AUDIO).entrySet()) {
                    ItagItem itag = entry.getValue();

                    AudioStream audioStream = new AudioStream(entry.getKey(), itag.getMediaFormat(), itag.avgBitrate);
                    if (!Stream.containSimilarStream(audioStream, audioStreams)) {
                        audioStreams.add(audioStream);
                    }
                }
            } catch (Exception e) {
                throw new ParsingException("Could not get audio streams", e);
            }
            return audioStreams;
        }
    }

    @Override
    public List<VideoStream> getVideoStreams() throws IOException, ExtractionException {
        assertPageFetched();
        List<VideoStream> videoStreams = new ArrayList<>();
        if(isUseYoutubeApi) {
            return videoStreams;
        }
        else {
            try {
                for (Map.Entry<String, ItagItem> entry : getItags(URL_ENCODED_FMT_STREAM_MAP, ItagItem.ItagType.VIDEO).entrySet()) {
                    ItagItem itag = entry.getValue();
                    VideoStream videoStream = new VideoStream(entry.getKey(), itag.getMediaFormat(), itag.resolutionString);
                    if (!Stream.containSimilarStream(videoStream, videoStreams)) {
                        videoStreams.add(videoStream);
                    }
                }
            } catch (Exception e) {
                throw new ParsingException("Could not get video streams", e);
            }
            return videoStreams;
        }
    }

    @Override
    public List<VideoStream> getVideoOnlyStreams() throws ExtractionException {
        assertPageFetched();
        List<VideoStream> videoOnlyStreams = new ArrayList<>();
        try {
            for (Map.Entry<String, ItagItem> entry : getItags(ADAPTIVE_FMTS, ItagItem.ItagType.VIDEO_ONLY).entrySet()) {
                ItagItem itag = entry.getValue();

                VideoStream videoStream = new VideoStream(entry.getKey(), itag.getMediaFormat(), itag.resolutionString, true);
                if (!Stream.containSimilarStream(videoStream, videoOnlyStreams)) {
                    videoOnlyStreams.add(videoStream);
                }
            }
        } catch (Exception e) {
            throw new ParsingException("Could not get video only streams", e);
        }

        return videoOnlyStreams;
    }

    @Override
    @Nonnull
    public List<SubtitlesStream> getSubtitlesDefault() throws IOException, ExtractionException {
        return getSubtitles(MediaFormat.TTML);
    }

    @Override
    @Nonnull
    public List<SubtitlesStream> getSubtitles(final MediaFormat format) throws IOException, ExtractionException {
        assertPageFetched();
        List<SubtitlesStream> subtitles = new ArrayList<>();
        if(isUseYoutubeApi) {
            return subtitles;
        }
        else {
            for (final SubtitlesInfo subtitlesInfo : subtitlesInfos) {
                subtitles.add(subtitlesInfo.getSubtitle(format));
            }
            return subtitles;
        }
    }

    @Override
    public StreamType getStreamType() throws ParsingException {
        assertPageFetched();
        if(isUseYoutubeApi) {
            return StreamType.VIDEO_STREAM;
        }
        else {
            try {
                if (playerArgs != null && (playerArgs.has("ps") && playerArgs.get("ps").toString().equals("live") ||
                        playerArgs.get(URL_ENCODED_FMT_STREAM_MAP).toString().isEmpty())) {
                    return StreamType.LIVE_STREAM;
                }
            } catch (Exception e) {
                throw new ParsingException("Could not get hls manifest url", e);
            }
            return StreamType.VIDEO_STREAM;
        }
    }

    @Override
    public StreamInfoItem getNextStream() throws IOException, ExtractionException {
        assertPageFetched();
        if(isUseYoutubeApi) {
            return  null;
        }
        else {
            try {
                StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());

                Elements watch = doc.select("div[class=\"watch-sidebar-section\"]");
                if (watch.size() < 1) {
                    return null;// prevent the snackbar notification "report error" on age-restricted videos
                }

                collector.commit(extractVideoPreviewInfo(watch.first().select("li").first()));
                return collector.getItems().get(0);
            } catch (Exception e) {
                throw new ParsingException("Could not get next video", e);
            }
        }
    }

    @Override
    public StreamInfoItemsCollector getRelatedStreams() throws IOException, ExtractionException {
        assertPageFetched();
        if(isUseYoutubeApi) {
            return null;
        }
        else {
            try {
                StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());
                Element ul = doc.select("ul[id=\"watch-related\"]").first();
                if (ul != null) {
                    for (Element li : ul.children()) {
                        // first check if we have a playlist. If so leave them out
                        if (li.select("a[class*=\"content-link\"]").first() != null) {
                            collector.commit(extractVideoPreviewInfo(li));
                        }
                    }
                }
                return collector;
            } catch (Exception e) {
                throw new ParsingException("Could not get related videos", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getErrorMessage() {
        if(isUseYoutubeApi) {
            return  null;
        }
        else {
            String errorMessage = doc.select("h1[id=\"unavailable-message\"]").first().text();
            StringBuilder errorReason;

            if (errorMessage == null || errorMessage.isEmpty()) {
                errorReason = null;
            } else if (errorMessage.contains("GEMA")) {
                // Gema sometimes blocks youtube music content in germany:
                // https://www.gema.de/en/
                // Detailed description:
                // https://en.wikipedia.org/wiki/GEMA_%28German_organization%29
                errorReason = new StringBuilder("GEMA");
            } else {
                errorReason = new StringBuilder(errorMessage);
                errorReason.append("  ");
                errorReason.append(doc.select("[id=\"unavailable-submessage\"]").first().text());
            }

            return errorReason != null ? errorReason.toString() : null;
        }
    }

    @Override
    public boolean isUserYoutubeApi() {
        return isUseYoutubeApi;
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Fetch page
    //////////////////////////////////////////////////////////////////////////*/

    private static final String URL_ENCODED_FMT_STREAM_MAP = "url_encoded_fmt_stream_map";
    private static final String ADAPTIVE_FMTS = "adaptive_fmts";
    private static final String HTTPS = "https:";
    private static final String CONTENT = "content";
    private static final String DECRYPTION_FUNC_NAME = "decrypt";

    private static final String VERIFIED_URL_PARAMS = "&has_verified=1&bpctr=9999999999";

    private final static String DECYRYPTION_SIGNATURE_FUNCTION_REGEX = "([\\w$]+)\\s*=\\s*function\\((\\w+)\\)\\{\\s*\\2=\\s*\\2\\.split\\(\"\"\\)\\s*;";
    private final static String DECRYPTION_AKAMAIZED_STRING_REGEX = "yt\\.akamaized\\.net/\\)\\s*\\|\\|\\s*.*?\\s*c\\s*&&\\s*d\\.set\\([^,]+\\s*,\\s*(:encodeURIComponent\\s*\\()([a-zA-Z0-9$]+)\\(";
    private final static String DECRYPTION_AKAMAIZED_SHORT_STRING_REGEX = "\\bc\\s*&&\\s*d\\.set\\([^,]+\\s*,\\s*(:encodeURIComponent\\s*\\()([a-zA-Z0-9$]+)\\(";

    private volatile String decryptionCode = "";

    private String pageHtml = null;

    private String getPageHtml(Downloader downloader) throws IOException, ExtractionException {
        final String verifiedUrl = getUrl() + VERIFIED_URL_PARAMS;
        if (pageHtml == null) {
            pageHtml = downloader.download(verifiedUrl);
        }
        return pageHtml;
    }

    @Override
    public void onFetchPage(@Nonnull Downloader downloader) throws IOException, ExtractionException {
        URL url = Utils.stringToURL(getUrl());
        String host = url.getHost();
        if(host.equalsIgnoreCase("googleapis.com") || host.equalsIgnoreCase("www.googleapis.com")){
            try {
                JsonObject object = JsonParser.object().from(downloader.download(getUrl()));
                if(object != null) {
                   JsonArray array =  object.getArray(ExtractorConstant.BlockJson.ITEMS);//.getObject(0)
                   if(array != null) {
                       jsonObject = array.getObject(0);
                   }
                }
                isUseYoutubeApi = true;
            } catch (JsonParserException e) {
                jsonObject = null;
                LogHelper.i(TAG, "onFetchPage", e.getMessage());
            }
            doc = null;
        }
        else {
            final String pageContent = getPageHtml(downloader);
            doc = Jsoup.parse(pageContent, getUrl());

            final String playerUrl;
            // Check if the video is age restricted
            if (pageContent.contains("<meta property=\"og:restrictions:age")) {
                final EmbeddedInfo info = getEmbeddedInfo();
                final String videoInfoUrl = getVideoInfoUrl(getId(), info.sts);
                final String infoPageResponse = downloader.download(videoInfoUrl);
                videoInfoPage.putAll(Parser.compatParseMap(infoPageResponse));
                playerUrl = info.url;
                isAgeRestricted = true;
            } else {
                final JsonObject ytPlayerConfig = getPlayerConfig(pageContent);
                playerArgs = getPlayerArgs(ytPlayerConfig);
                playerUrl = getPlayerUrl(ytPlayerConfig);
                isAgeRestricted = false;
            }

            if (decryptionCode.isEmpty()) {
                decryptionCode = loadDecryptionCode(playerUrl);
            }

            if (subtitlesInfos.isEmpty()) {
                subtitlesInfos.addAll(getAvailableSubtitlesInfo());
            }
        }
    }

    private JsonObject getPlayerConfig(String pageContent) throws ParsingException {
        try {
            String ytPlayerConfigRaw = Parser.matchGroup1("ytplayer.config\\s*=\\s*(\\{.*?\\});", pageContent);
            return JsonParser.object().from(ytPlayerConfigRaw);
        } catch (Parser.RegexException e) {
            String errorReason = getErrorMessage();
            switch (errorReason) {
                case "GEMA":
                    throw new GemaException(errorReason);
                case "":
                    throw new ContentNotAvailableException("Content not available: player config empty", e);
                default:
                    throw new ContentNotAvailableException("Content not available", e);
            }
        } catch (Exception e) {
            throw new ParsingException("Could not parse yt player config", e);
        }
    }

    private JsonObject getPlayerArgs(JsonObject playerConfig) throws ParsingException {
        JsonObject playerArgs;

        //attempt to load the youtube js player JSON arguments
        try {
            playerArgs = playerConfig.getObject("args");
        } catch (Exception e) {
            throw new ParsingException("Could not parse yt player config", e);
        }

        return playerArgs;
    }

    private String getPlayerUrl(JsonObject playerConfig) throws ParsingException {
        try {
            // The Youtube service needs to be initialized by downloading the
            // js-Youtube-player. This is done in order to get the algorithm
            // for decrypting cryptic signatures inside certain streams urls.
            String playerUrl;

            JsonObject ytAssets = playerConfig.getObject("assets");
            playerUrl = ytAssets.getString("js");

            if (playerUrl.startsWith("//")) {
                playerUrl = HTTPS + playerUrl;
            }
            return playerUrl;
        } catch (Exception e) {
            throw new ParsingException("Could not load decryption code for the Youtube service.", e);
        }
    }

    @Nonnull
    private EmbeddedInfo getEmbeddedInfo() throws ParsingException, ReCaptchaException {
        try {
            final Downloader downloader = PlayTube.getDownloader();
            final String embedUrl = "https://www.youtube.com/embed/" + getId();
            final String embedPageContent = downloader.download(embedUrl);

            // Get player url
            final String assetsPattern = "\"assets\":.+?\"js\":\\s*(\"[^\"]+\")";
            String playerUrl = Parser.matchGroup1(assetsPattern, embedPageContent).replace("\\", "").replace("\"", "");
            if (playerUrl.startsWith("//")) {
                playerUrl = HTTPS + playerUrl;
            }

            // Get embed sts
            try {
                // Get embed sts
                final String stsPattern = "\"sts\"\\s*:\\s*(\\d+)";
                final String sts = Parser.matchGroup1(stsPattern, embedPageContent);
                return new EmbeddedInfo(playerUrl, sts);
            } catch (Exception i) {
                // if it failes we simply reply with no sts as then it does not seem to be necessary
                return new EmbeddedInfo(playerUrl, "");
            }
        } catch (IOException e) {
            throw new ParsingException("Could load decryption code form restricted video for the Youtube service.", e);
        }
    }

    private String loadDecryptionCode(String playerUrl) throws DecryptException {
        try {
            Downloader downloader = PlayTube.getDownloader();
            if (!playerUrl.contains("https://youtube.com")) {
                //sometimes the https://youtube.com part does not get send with
                //than we have to add it by hand
                playerUrl = "https://youtube.com" + playerUrl;
            }

            final String playerCode = downloader.download(playerUrl);
            final String decryptionFunctionName = getDecryptionFuncName(playerCode);

            final String functionPattern = "(" + decryptionFunctionName.replace("$", "\\$") + "=function\\([a-zA-Z0-9_]+\\)\\{.+?\\})";
            final String decryptionFunction = "var " + Parser.matchGroup1(functionPattern, playerCode) + ";";

            final String helperObjectName = Parser.matchGroup1(";([A-Za-z0-9_\\$]{2})\\...\\(", decryptionFunction);
            final String helperPattern = "(var " + helperObjectName.replace("$", "\\$") + "=\\{.+?\\}\\};)";
            final String helperObject = Parser.matchGroup1(helperPattern, playerCode.replace("\n", ""));

            final String callerFunction = "function " + DECRYPTION_FUNC_NAME + "(a){return " + decryptionFunctionName + "(a);}";

            return helperObject + decryptionFunction + callerFunction;
        } catch (IOException ioe) {
            throw new DecryptException("Could not load decrypt function", ioe);
        } catch (Exception e) {
            throw new DecryptException("Could not parse decrypt function ", e);
        }
    }

    private String decryptSignature(String encryptedSig, String decryptionCode) throws DecryptException {
        Context context = Context.enter();
        context.setOptimizationLevel(-1);
        Object result;
        try {
            ScriptableObject scope = context.initStandardObjects();
            context.evaluateString(scope, decryptionCode, "decryptionCode", 1, null);
            Function decryptionFunc = (Function) scope.get("decrypt", scope);
            result = decryptionFunc.call(context, scope, scope, new Object[]{encryptedSig});
        } catch (Exception e) {
            throw new DecryptException("could not get decrypt signature", e);
        } finally {
            Context.exit();
        }
        return result == null ? "" : result.toString();
    }

    private String getDecryptionFuncName(String playerCode) throws DecryptException {
        String decryptionFunctionName;
        // Cascading things in catch is ugly, but its faster than running a match before getting the actual name
        // to se if the function can actually be found with the given regex.
        // However if this cascading should propably be cleaned up somehow as it looks a bit weird.
        try {
            decryptionFunctionName = Parser.matchGroup1(DECYRYPTION_SIGNATURE_FUNCTION_REGEX, playerCode);
        } catch (Parser.RegexException re) {
            try {
                decryptionFunctionName = Parser.matchGroup1(DECRYPTION_AKAMAIZED_SHORT_STRING_REGEX, playerCode);
            } catch (Parser.RegexException re2) {
                try {
                    decryptionFunctionName = Parser.matchGroup1(DECRYPTION_AKAMAIZED_STRING_REGEX, playerCode);
                } catch (Parser.RegexException re3) {
                    throw new DecryptException("Could not find decrypt function with any of the given patterns.", re);
                }
            }
        }
        return decryptionFunctionName;
    }

    @Nonnull
    private List<SubtitlesInfo> getAvailableSubtitlesInfo() throws SubtitlesException {
        // If the video is age restricted getPlayerConfig will fail
        if(isAgeRestricted) return Collections.emptyList();

        final JsonObject playerConfig;
        try {
            playerConfig = getPlayerConfig(getPageHtml(PlayTube.getDownloader()));
        } catch (IOException | ExtractionException e) {
            throw new SubtitlesException("Unable to download player configs", e);
        }
        final String playerResponse = playerConfig.getObject("args", new JsonObject()).getString("player_response");

        final JsonObject captions;
        try {
            if (playerResponse == null || !JsonParser.object().from(playerResponse).has("captions")) {
                // Captions does not exist
                return Collections.emptyList();
            }
            captions = JsonParser.object().from(playerResponse).getObject("captions");
        } catch (JsonParserException e) {
            throw new SubtitlesException("Unable to parse subtitles listing", e);
        }

        final JsonObject renderer = captions.getObject("playerCaptionsTracklistRenderer", new JsonObject());
        final JsonArray captionsArray = renderer.getArray("captionTracks", new JsonArray());
        // todo: use this to apply auto translation to different language from a source language
        final JsonArray autoCaptionsArray = renderer.getArray("translationLanguages", new JsonArray());

        // This check is necessary since there may be cases where subtitles metadata do not contain caption track info
        // e.g. https://www.youtube.com/watch?v=-Vpwatutnko
        final int captionsSize = captionsArray.size();
        if(captionsSize == 0) return Collections.emptyList();

        List<SubtitlesInfo> result = new ArrayList<>();
        for (int i = 0; i < captionsSize; i++) {
            final String languageCode = captionsArray.getObject(i).getString("languageCode");
            final String baseUrl = captionsArray.getObject(i).getString("baseUrl");
            final String vssId = captionsArray.getObject(i).getString("vssId");

            if (languageCode != null && baseUrl != null && vssId != null) {
                final boolean isAutoGenerated = vssId.startsWith("a.");
                result.add(new SubtitlesInfo(baseUrl, languageCode, isAutoGenerated));
            }
        }

        return result;
    }
    /*//////////////////////////////////////////////////////////////////////////
    // Data Class
    //////////////////////////////////////////////////////////////////////////*/

    private class EmbeddedInfo {
        final String url;
        final String sts;

        EmbeddedInfo(final String url, final String sts) {
            this.url = url;
            this.sts = sts;
        }
    }

    private class SubtitlesInfo {
        final String cleanUrl;
        final String languageCode;
        final boolean isGenerated;

        public SubtitlesInfo(final String baseUrl, final String languageCode, final boolean isGenerated) {
            this.cleanUrl = baseUrl.replaceAll("&fmt=[^&]*", "") // Remove preexisting format if exists
                    .replaceAll("&tlang=[^&]*", ""); // Remove translation language
            this.languageCode = languageCode;
            this.isGenerated = isGenerated;
        }

        public SubtitlesStream getSubtitle(final MediaFormat format) {
            return new SubtitlesStream(format, languageCode, cleanUrl + "&fmt=" + format.getSuffix(), isGenerated);
        }
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Utils
    //////////////////////////////////////////////////////////////////////////*/

    @Nonnull
    private static String getVideoInfoUrl(final String id, final String sts) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("https://www.youtube.com/get_video_info?video_id=").append(id).append("&eurl=https://youtube.googleapis.com/v/").append(id).append("&sts=").append(sts).append("&ps=default&gl=US&hl=en");
        return  buffer.toString();
    }

    private Map<String, ItagItem> getItags(String encodedUrlMapKey, ItagItem.ItagType itagTypeWanted) throws ParsingException {
        Map<String, ItagItem> urlAndItags = new LinkedHashMap<>();

        String encodedUrlMap = "";
        if (playerArgs != null && playerArgs.isString(encodedUrlMapKey)) {
            encodedUrlMap = playerArgs.getString(encodedUrlMapKey, "");
        } else if (videoInfoPage.containsKey(encodedUrlMapKey)) {
            encodedUrlMap = videoInfoPage.get(encodedUrlMapKey);
        }

        for (String url_data_str : encodedUrlMap.split(",")) {
            try {
                // This loop iterates through multiple streams, therefore tags
                // is related to one and the same streams at a time.
                Map<String, String> tags = Parser.compatParseMap(org.jsoup.parser.Parser.unescapeEntities(url_data_str, true));

                int itag = Integer.parseInt(tags.get("itag"));

                if (ItagItem.isSupported(itag)) {
                    ItagItem itagItem = ItagItem.getItag(itag);
                    if (itagItem.itagType == itagTypeWanted) {
                        String streamUrl = tags.get("url");
                        // if video has a signature: decrypt it and add it to the url
                        if (tags.get("s") != null) {
                            if (tags.get("sp") == null) {
                                // fallback for urls not conaining the "sp" tag
                                streamUrl = streamUrl + "&signature=" + decryptSignature(tags.get("s"), decryptionCode);
                            }
                            else {
                                streamUrl = streamUrl + "&" + tags.get("sp") + "=" + decryptSignature(tags.get("s"), decryptionCode);
                            }
                        }
                        urlAndItags.put(streamUrl, itagItem);
                    }
                }
            } catch (DecryptException e) {
                throw e;
            } catch (Exception ignored) {
            }
        }

        return urlAndItags;
    }

    /**
     * Provides information about links to other videos on the video page, such as related videos.
     * This is encapsulated in a StreamInfoItem object, which is a subset of the fields in a full StreamInfo.
     */
    private StreamInfoItemExtractor extractVideoPreviewInfo(final Element li) {
        return new YoutubeStreamInfoItemExtractor(li) {

            @Override
            public String getUrl() throws ParsingException {
                return li.select("a.content-link").first().attr("abs:href");
            }

            @Override
            public String getName() throws ParsingException {
                //todo: check NullPointerException causing
                return li.select("span.title").first().text();
                //this page causes the NullPointerException, after finding it by searching for "tjvg":
                //https://www.youtube.com/watch?v=Uqg0aEhLFAg
            }

            @Override
            public String getUploaderName() throws ParsingException {
                //return li.select("span[class*=\"attribution\"").first().select("span").first().text();
                return li.select("span.attribution").first().select("span").first().text();
            }

            @Override
            public String getUploaderUrl() throws ParsingException {
                return ""; // The uploader is not linked
            }

            @Override
            public String getUploadDate() throws ParsingException {
                return "";
            }

            @Override
            public long getViewCount() throws ParsingException {
                try {
                    if (getStreamType() == StreamType.LIVE_STREAM) return -1;

                    return Long.parseLong(Utils.removeNonDigitCharacters(li.select("span.view-count").first().text()));
                } catch (Exception e) {
                    //related videos sometimes have no view count
                    return 0;
                }
            }

            @Override
            public String getThumbnailUrl() throws ParsingException {
                Element img = li.select("img").first();
                String thumbnailUrl = img.attr("abs:src");
                // Sometimes youtube sends links to gif files which somehow seem to not exist
                // anymore. Items with such gif also offer a secondary image source. So we are going
                // to use that if we caught such an item.
                if (thumbnailUrl.contains(".gif")) {
                    thumbnailUrl = img.attr("data-thumb");
                }
                if (thumbnailUrl.startsWith("//")) {
                    thumbnailUrl = HTTPS + thumbnailUrl;
                }
                return thumbnailUrl;
            }
        };
    }
}
