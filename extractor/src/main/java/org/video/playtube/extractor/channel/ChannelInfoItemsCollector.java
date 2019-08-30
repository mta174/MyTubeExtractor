package org.video.playtube.extractor.channel;

import org.video.playtube.extractor.InfoItemsCollector;
import org.video.playtube.extractor.exception.ParsingException;

public class ChannelInfoItemsCollector extends InfoItemsCollector<ChannelInfoItem, ChannelInfoItemExtractor> {
    public ChannelInfoItemsCollector(int serviceId) {
        super(serviceId);
    }

    @Override
    public ChannelInfoItem extract(ChannelInfoItemExtractor extractor) throws ParsingException {
        // important information
        int serviceId = getServiceId();
        String name = extractor.getName();
        String  url = extractor.getUrl();

        ChannelInfoItem resultItem = new ChannelInfoItem(serviceId, url, name);


        // optional information
        try {
            resultItem.setSubscriberCount(extractor.getSubscriberCount());
        } catch (Exception e) {
            addError(e);
        }
        try {
            resultItem.setStreamCount(extractor.getStreamCount());
        } catch (Exception e) {
            addError(e);
        }
        try {
            resultItem.setThumbnailUrl(extractor.getThumbnailUrl());
        } catch (Exception e) {
            addError(e);
        }
        try {
            resultItem.setDescription(extractor.getDescription());
        } catch (Exception e) {
            addError(e);
        }
        return resultItem;
    }
}
