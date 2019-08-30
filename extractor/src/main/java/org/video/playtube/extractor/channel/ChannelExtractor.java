package org.video.playtube.extractor.channel;

import org.video.playtube.extractor.ListExtractor;
import org.video.playtube.extractor.StreamingService;
import org.video.playtube.extractor.exception.ParsingException;
import org.video.playtube.extractor.stream.StreamInfoItem;
import org.video.playtube.extractor.linkhandler.ListLinkHandler;
import org.video.playtube.extractor.util.Localization;

public abstract class ChannelExtractor extends ListExtractor<StreamInfoItem> {

    public ChannelExtractor(StreamingService service, ListLinkHandler linkHandler, Localization localization) {
        super(service, linkHandler, localization);
    }

    public abstract String getAvatarUrl() throws ParsingException;
    public abstract String getBannerUrl() throws ParsingException;
    public abstract String getFeedUrl() throws ParsingException;
    public abstract long getSubscriberCount() throws ParsingException;
    public abstract String getDescription() throws ParsingException;
}
