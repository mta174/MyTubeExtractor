package com.mta.playtube.extractor.services.soundcloud.channel;

import com.mta.playtube.extractor.services.soundcloud.SoundcloudParsingHelper;
import com.mta.playtube.extractor.linkhandler.ListLinkHandlerFactory;
import com.mta.playtube.extractor.exceptions.ParsingException;
import com.mta.playtube.extractor.utils.Parser;
import com.mta.playtube.extractor.utils.Utils;

import java.util.List;

public class SoundcloudChannelLinkHandlerFactory extends ListLinkHandlerFactory {
    private static final SoundcloudChannelLinkHandlerFactory instance = new SoundcloudChannelLinkHandlerFactory();
    private final String URL_PATTERN = "^https?://(www\\.|m\\.)?soundcloud.com/[0-9a-z_-]+" +
            "(/((tracks|albums|sets|reposts|followers|following)/?)?)?([#?].*)?$";

    public static SoundcloudChannelLinkHandlerFactory getInstance() {
        return instance;
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
    public String getUrl(String id, List<String> contentFilter, String sortFilter) throws ParsingException {
        try {
            return SoundcloudParsingHelper.resolveUrlWithEmbedPlayer("https://api.soundcloud.com/users/" + id);
        } catch (Exception e) {
            throw new ParsingException(e.getMessage(), e);
        }
    }

    @Override
    public boolean onAcceptUrl(final String url) {
        return Parser.isMatch(URL_PATTERN, url.toLowerCase());
    }
}
