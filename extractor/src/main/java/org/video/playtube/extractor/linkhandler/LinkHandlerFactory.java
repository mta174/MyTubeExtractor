package org.video.playtube.extractor.linkhandler;

import org.video.playtube.extractor.exception.FoundAdException;
import org.video.playtube.extractor.exception.ParsingException;

public abstract class LinkHandlerFactory {

    ///////////////////////////////////
    // To Override
    ///////////////////////////////////

    public abstract String getId(String url) throws ParsingException;
    public abstract String getUrl(String id) throws ParsingException;
    public abstract boolean onAcceptUrl(final String url) throws ParsingException;

    ///////////////////////////////////
    // Logic
    ///////////////////////////////////

    public LinkHandler fromUrl(String url) throws ParsingException {
        if(url == null) throw new IllegalArgumentException("url can not be null");
        if(!acceptUrl(url)) {
            throw new ParsingException("Malformed unacceptable url: " + url);
        }

        final String id = getId(url);
        return new LinkHandler(url, getUrl(id), id);
    }

    public LinkHandler fromId(String id) throws ParsingException {
        if(id == null) throw new IllegalArgumentException("id can not be null");
        final String url = getUrl(id);
        return new LinkHandler(url, url, id);
    }

    /**
     * When a VIEW_ACTION is caught this function will test if the url delivered within the calling
     * Intent was meant to be watched with this Service.
     * Return false if this service shall not allow to be called through ACTIONs.
     */
    public boolean acceptUrl(final String url) throws ParsingException {
        try {
            return onAcceptUrl(url);
        } catch (FoundAdException fe) {
            throw fe;
        }
    }
}
