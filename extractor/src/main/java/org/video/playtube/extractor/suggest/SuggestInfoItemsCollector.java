package org.video.playtube.extractor.suggest;

import org.video.playtube.extractor.InfoItemsCollector;
import org.video.playtube.extractor.exception.ParsingException;

public class SuggestInfoItemsCollector extends InfoItemsCollector<SuggestInfoItem, SuggestInfoItemExtractor> {

    public SuggestInfoItemsCollector(int serviceId) {
        super(serviceId);
    }

    @Override
    public SuggestInfoItem extract(SuggestInfoItemExtractor extractor) throws ParsingException {

        String name = extractor.getName();
        int serviceId = getServiceId();
        String url = extractor.getUrl();
        SuggestInfoItem resultItem = new SuggestInfoItem(serviceId, url, name);
        try {
            resultItem.setNextPageToken(extractor.getNextPageToken());
            resultItem.setKey(extractor.getKey());
        } catch (Exception e) {
            addError(e);
        }
        return resultItem;
    }
}
