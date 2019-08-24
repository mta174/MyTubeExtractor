package com.mta.playtube.extractor.services.soundcloud.streams;

import com.mta.playtube.extractor.services.soundcloud.SoundcloudParsingHelper;
import com.mta.playtube.extractor.linkhandler.LinkHandlerFactory;
import com.mta.playtube.extractor.exceptions.ParsingException;
import com.mta.playtube.extractor.utils.Parser;
import com.mta.playtube.extractor.utils.Utils;

public class SoundcloudStreamLinkHandlerFactory extends LinkHandlerFactory {
    private static final SoundcloudStreamLinkHandlerFactory instance = new SoundcloudStreamLinkHandlerFactory();
    private final String URL_PATTERN = "^https?://(www\\.|m\\.)?soundcloud.com/[0-9a-z_-]+" +
            "/(?!(tracks|albums|sets|reposts|followers|following)/?$)[0-9a-z_-]+/?([#?].*)?$";

    private SoundcloudStreamLinkHandlerFactory() {
    }

    public static SoundcloudStreamLinkHandlerFactory getInstance() {
        return instance;
    }

    @Override
    public String getUrl(String id) throws ParsingException {
        try {
            return SoundcloudParsingHelper.resolveUrlWithEmbedPlayer("https://api.soundcloud.com/tracks/" + id);
        } catch (Exception e) {
            throw new ParsingException(e.getMessage(), e);
        }
    }

    @Override
    public String getId(String url) throws ParsingException {
        Utils.checkUrl(URL_PATTERN, url);

        try {
            return SoundcloudParsingHelper.resolveIdWithEmbedPlayer(url);
        } catch (Exception e) {
            throw new ParsingException(e.getMessage(), e);
        }
    }

    @Override
    public boolean onAcceptUrl(final String url) throws ParsingException {
        return Parser.isMatch(URL_PATTERN, url.toLowerCase());
    }
}
