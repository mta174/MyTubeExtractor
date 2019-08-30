package org.video.playtube.extractor.service.media_ccc.linkHandler;

import org.video.playtube.extractor.exception.ParsingException;
import org.video.playtube.extractor.linkhandler.LinkHandlerFactory;

public class MediaCCCStreamLinkHandlerFactory extends LinkHandlerFactory {

    @Override
    public String getId(String url) throws ParsingException {
        if(url.startsWith("https://api.media.ccc.de/public/events/") &&
            !url.contains("?q=")) {
            return url.replace("https://api.media.ccc.de/public/events/", "");
        }
        throw new ParsingException("Could not get id from url: " + url);
    }

    @Override
    public String getUrl(String id) throws ParsingException {
        return "https://api.media.ccc.de/public/events/" + id;
    }

    @Override
    public boolean onAcceptUrl(String url) throws ParsingException {
        return url.startsWith("https://api.media.ccc.de/public/events/") &&
                !url.contains("?q=");
    }
}
