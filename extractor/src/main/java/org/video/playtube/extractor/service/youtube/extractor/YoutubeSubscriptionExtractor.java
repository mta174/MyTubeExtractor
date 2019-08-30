package org.video.playtube.extractor.service.youtube.extractor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.video.playtube.extractor.exception.ExtractionException;
import org.video.playtube.extractor.service.youtube.YoutubeService;
import org.video.playtube.extractor.subscription.SubscriptionExtractor;
import org.video.playtube.extractor.subscription.SubscriptionItem;
import org.video.playtube.extractor.util.ExtractorConstant;
import org.video.playtube.extractor.util.Parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.video.playtube.extractor.subscription.SubscriptionExtractor.ContentSource.INPUT_STREAM;

/**
 * Extract subscriptions from a YouTube export (OPML format supported)
 */
public class YoutubeSubscriptionExtractor extends SubscriptionExtractor {

    public YoutubeSubscriptionExtractor(YoutubeService service) {
        super(service, Collections.singletonList(INPUT_STREAM));
    }

    @Override
    public String getRelatedUrl() {
        return "https://www.youtube.com/subscription_manager?action_takeout=1";
    }

    @Override
    public List<SubscriptionItem> fromInputStream(InputStream contentInputStream) throws ExtractionException {
        if (contentInputStream == null) throw new InvalidSourceException("input streams is null");

        return getItemsFromOPML(contentInputStream);
    }

    /*//////////////////////////////////////////////////////////////////////////
    // OPML implementation
    //////////////////////////////////////////////////////////////////////////*/

    private static final String ID_PATTERN = "/videos.xml\\?channel_id=([A-Za-z0-9_-]*)";
    private static final String BASE_CHANNEL_URL = "https://www.youtube.com/channel/";

    private List<SubscriptionItem> getItemsFromOPML(InputStream contentInputStream) throws ExtractionException {
        final List<SubscriptionItem> result = new ArrayList<>();

        final String contentString = readFromInputStream(contentInputStream);
        Document document = Jsoup.parse(contentString, "", org.jsoup.parser.Parser.xmlParser());

        if (document.select("opml").isEmpty()) {
            throw new InvalidSourceException("document does not have OPML tag");
        }

        if (document.select("outline").isEmpty()) {
            throw new InvalidSourceException("document does not have at least one outline tag");
        }

        for (Element outline : document.select("outline[type=rss]")) {
            String title = outline.attr("title");
            String xmlUrl = outline.attr("abs:xmlUrl");

            try {
                String id = Parser.matchGroup1(ID_PATTERN, xmlUrl);
                result.add(new SubscriptionItem(service.getServiceId(), BASE_CHANNEL_URL + id, title));
            } catch (Parser.RegexException ignored) { /* ignore invalid subscriptions */ }
        }

        return result;
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Utils
    //////////////////////////////////////////////////////////////////////////*/

    /**
     * Throws an exception if the string does not have the right tag/string from a valid export.
     */
    private void throwIfTagIsNotFound(String content) throws InvalidSourceException {
        if (!content.trim().contains("<opml")) {
            throw new InvalidSourceException("input streams does not have OPML tag");
        }
    }

    private String readFromInputStream(InputStream inputStream) throws InvalidSourceException {
        StringBuilder contentBuilder = new StringBuilder();
        boolean hasTag = false;
        try {
            byte[] buffer = new byte[16 * 1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                String currentPartOfContent = new String(buffer, 0, read, ExtractorConstant.CHARSET_UTF_8);
                contentBuilder.append(currentPartOfContent);

                // Fail-fast in case of reading a long unsupported input streams
                if (!hasTag && contentBuilder.length() > 128) {
                    throwIfTagIsNotFound(contentBuilder.toString());
                    hasTag = true;
                }
            }
        } catch (InvalidSourceException e) {
            throw e;
        } catch (Throwable e) {
            throw new InvalidSourceException(e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException ignored) {
            }
        }

        final String fileContent = contentBuilder.toString().trim();
        if (fileContent.isEmpty()) {
            throw new InvalidSourceException("Empty input streams");
        }

        if (!hasTag) {
            throwIfTagIsNotFound(fileContent);
        }

        return fileContent;
    }
}
