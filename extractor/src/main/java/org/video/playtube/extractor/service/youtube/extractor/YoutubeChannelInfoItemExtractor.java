package org.video.playtube.extractor.service.youtube.extractor;

import org.jsoup.nodes.Element;
import org.video.playtube.extractor.channel.ChannelInfoItemExtractor;
import org.video.playtube.extractor.exception.ParsingException;
import org.video.playtube.extractor.util.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class YoutubeChannelInfoItemExtractor implements ChannelInfoItemExtractor {
    private final Element el;

    public YoutubeChannelInfoItemExtractor(Element el) {
        this.el = el;
    }

    @Override
    public String getThumbnailUrl() throws ParsingException {
        Element img = el.select("span[class*=\"yt-thumb-simple\"]").first()
                .select("img").first();

        String url = img.attr("abs:src");

        if (url.contains("gif")) {
            url = img.attr("abs:data-thumb");
        }
        return url;
    }

    @Override
    public String getName() throws ParsingException {
        return el.select("a[class*=\"yt-uix-tile-link\"]").first()
                .text();
    }

    @Override
    public String getUrl() throws ParsingException {
        String buttonTrackingUrl = el.select("button[class*=\"yt-uix-button\"]").first()
                .attr("abs:data-href");

        Pattern channelIdPattern = Pattern.compile("(?:.*?)\\%252Fchannel\\%252F([A-Za-z0-9\\-\\_]+)(?:.*)");
        Matcher match = channelIdPattern.matcher(buttonTrackingUrl);

        if (match.matches()) {
            return YoutubeChannelExtractor.CHANNEL_URL_BASE + match.group(1);
        } else {
            // fallback method just in case youtube changes things; it should never run and tests will fail
            // provides an url with "/user/NAME", that is inconsistent with streams and channel extractor
            return el.select("a[class*=\"yt-uix-tile-link\"]").first()
                    .attr("abs:href");
        }
    }

    @Override
    public long getSubscriberCount() throws ParsingException {
        final Element subsEl = el.select("span[class*=\"yt-subscriber-count\"]").first();
        if (subsEl != null) {
            try {
                return Long.parseLong(Utils.removeNonDigitCharacters(subsEl.text()));
            } catch (NumberFormatException e) {
                throw new ParsingException("Could not get subscriber count", e);
            }
        } else {
            // If the element is null, the channel have the subscriber count disabled
            return -1;
        }
    }

    @Override
    public long getStreamCount() throws ParsingException {
        Element metaEl = el.select("ul[class*=\"yt-lockup-meta-info\"]").first();
        if (metaEl == null) {
            return 0;
        } else {
            return Long.parseLong(Utils.removeNonDigitCharacters(metaEl.text()));
        }
    }

    @Override
    public String getDescription() throws ParsingException {
        Element desEl = el.select("div[class*=\"yt-lockup-description\"]").first();
        if (desEl == null) {
            return "";
        } else {
            return desEl.text();
        }
    }
}
