package org.video.playtube.extractor.service.media_ccc.extractors.infoItems;

import com.grack.nanojson.JsonObject;
import org.video.playtube.extractor.channel.ChannelInfoItemExtractor;
import org.video.playtube.extractor.exception.ParsingException;

public class MediaCCCConferenceInfoItemExtractor implements ChannelInfoItemExtractor {

    JsonObject conference;

    public MediaCCCConferenceInfoItemExtractor(JsonObject conference) {
        this.conference = conference;
    }

    @Override
    public String getDescription() throws ParsingException {
        return "";
    }

    @Override
    public long getSubscriberCount() throws ParsingException {
        return -1;
    }

    @Override
    public long getStreamCount() throws ParsingException {
        return -1;
    }

    @Override
    public String getName() throws ParsingException {
        return conference.getString("title");
    }

    @Override
    public String getUrl() throws ParsingException {
        return conference.getString("url");
    }

    @Override
    public String getThumbnailUrl() throws ParsingException {
        return conference.getString("logo_url");
    }
}
