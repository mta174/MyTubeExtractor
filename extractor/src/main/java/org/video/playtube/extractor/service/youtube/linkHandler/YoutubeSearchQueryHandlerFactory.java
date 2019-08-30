package org.video.playtube.extractor.service.youtube.linkHandler;

import org.video.playtube.extractor.exception.ParsingException;
import org.video.playtube.extractor.linkhandler.SearchQueryHandlerFactory;
import org.video.playtube.extractor.util.ExtractorConstant;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class YoutubeSearchQueryHandlerFactory extends SearchQueryHandlerFactory {

    public static final String VIDEOS = "videos";
    public static final String CHANNELS = "channels";
    public static final String PLAYLISTS = "playlists";
    public static final String ALL = "all";

    public static YoutubeSearchQueryHandlerFactory getInstance() {
        return new YoutubeSearchQueryHandlerFactory();
    }

    @Override
    public String getUrl(String searchString, List<String> contentFilters, String sortFilter) throws ParsingException {
        try {
            final String url = "https://www.youtube.com/results" + "?q=" + URLEncoder.encode(searchString, ExtractorConstant.CHARSET_UTF_8);

            if(contentFilters.size() > 0) {
                switch (contentFilters.get(0)) {
                    case VIDEOS: return url + "&sp=EgIQAVAU";
                    case CHANNELS: return url + "&sp=EgIQAlAU";
                    case PLAYLISTS: return url + "&sp=EgIQA1AU";
                    case ALL:
                    default:
                }
            }

            return url;
        } catch (UnsupportedEncodingException e) {
            throw new ParsingException("Could not encode query", e);
        }
    }

    @Override
    public String[] getAvailableContentFilter() {
        return new String[] {
                ALL,
                VIDEOS,
                CHANNELS,
                PLAYLISTS};
    }
}
