package org.video.playtube.extractor.channel;

import org.video.playtube.extractor.InfoItemExtractor;
import org.video.playtube.extractor.exception.ParsingException;

public interface ChannelInfoItemExtractor extends InfoItemExtractor {
    String getDescription() throws ParsingException;

    long getSubscriberCount() throws ParsingException;
    long getStreamCount() throws ParsingException;
}
