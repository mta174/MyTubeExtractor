package org.video.playtube.extractor.service.soundcloud.search;

import org.video.playtube.extractor.exception.ParsingException;
import org.video.playtube.extractor.exception.ReCaptchaException;
import org.video.playtube.extractor.linkhandler.SearchQueryHandlerFactory;
import org.video.playtube.extractor.service.soundcloud.SoundcloudParsingHelper;
import org.video.playtube.extractor.util.ExtractorConstant;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class SoundcloudSearchQueryHandlerFactory extends SearchQueryHandlerFactory {

    public static final String TRACKS = "tracks";
    public static final String USERS = "users";
    public static final String PLAYLISTS = "playlists";
    public static final String ALL = "all";

    public static final int ITEMS_PER_PAGE = 10;

    @Override
    public String getUrl(String id, List<String> contentFilter, String sortFilter) throws ParsingException {
        try {
            StringBuffer buffer = new StringBuffer();
            buffer.append("https://api-v2.soundcloud.com/search");

            if(contentFilter.size() > 0) {
                switch (contentFilter.get(0)) {
                    case TRACKS:
                        buffer.append("/tracks");
                        break;
                    case USERS:
                        buffer.append("/users");
                        break;
                    case PLAYLISTS:
                        buffer.append("/playlists");
                        break;
                    case ALL:
                    default:
                        break;
                }
            }
            buffer.append("?q=").append(URLEncoder.encode(id, ExtractorConstant.CHARSET_UTF_8))
                    .append("&client_id=").append(SoundcloudParsingHelper.clientId())
                    .append("&limit=").append(ITEMS_PER_PAGE).append("&offset=0");
            return buffer.toString();

        } catch (UnsupportedEncodingException e) {
            throw new ParsingException("Could not encode query", e);
        } catch (IOException e) {
            throw new ParsingException("Could not get client id", e);
        } catch (ReCaptchaException e) {
            throw new ParsingException("ReCaptcha required", e);
        }
    }

    @Override
    public String[] getAvailableContentFilter() {
        return new String[] {
                ALL,
                TRACKS,
                USERS,
                PLAYLISTS};
    }
}
