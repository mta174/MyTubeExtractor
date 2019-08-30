package org.video.playtube.extractor.service.youtube.linkHandler;

import org.video.playtube.extractor.exception.ParsingException;
import org.video.playtube.extractor.linkhandler.ListLinkHandlerFactory;
import org.video.playtube.extractor.util.Utils;

import java.net.URL;
import java.util.List;

public class YoutubeChannelLinkHandlerFactory extends ListLinkHandlerFactory {

    private static final YoutubeChannelLinkHandlerFactory instance = new YoutubeChannelLinkHandlerFactory();

    public static YoutubeChannelLinkHandlerFactory getInstance() {
        return instance;
    }

    @Override
    public String getUrl(String id, List<String> contentFilters, String searchFilter) {
        return "https://www.youtube.com/" + id;
    }

    @Override
    public String getId(String url) throws ParsingException {
        try {
            URL urlObj = Utils.stringToURL(url);
            String path = urlObj.getPath();

            if (!Utils.isHTTP(urlObj) || !(YoutubeParsingHelper.isYoutubeURL(urlObj) || YoutubeParsingHelper.isInvidioURL(urlObj) || YoutubeParsingHelper.isHooktubeURL(urlObj))) {
                throw new ParsingException("the URL given is not a Youtube-URL");
            }

            if (!path.startsWith("/user/") && !path.startsWith("/channel/")) {
                throw new ParsingException("the URL given is neither a channel nor an user");
            }

            // remove leading "/"
            path = path.substring(1);

            String[] splitPath = path.split("/");
            String id = splitPath[1];

            if (id == null || !id.matches("[A-Za-z0-9_-]+")) {
                throw new ParsingException("The given id is not a Youtube-Video-ID");
            }

            return splitPath[0] + "/" + id;
        } catch (final Exception exception) {
            throw new ParsingException("Error could not parse url :" + exception.getMessage(), exception);
        }
    }

    @Override
    public boolean onAcceptUrl(String url) {
        try {
            getId(url);
        } catch (ParsingException e) {
            return false;
        }
        return true;
    }
}
