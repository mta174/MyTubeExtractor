package org.video.playtube.extractor.listdata;

import org.video.playtube.extractor.InfoItem;
import org.video.playtube.extractor.InfoItemsCollector;
import org.video.playtube.extractor.exception.ParsingException;

import java.util.List;
import java.util.Vector;


public class ListDataInfoItemsCollector  extends InfoItemsCollector<ListDataInfoItem, ListDataInfoItemExtractor> {

    public ListDataInfoItemsCollector(int serviceId) {
        super(serviceId);
    }

    @Override
    public ListDataInfoItem extract(ListDataInfoItemExtractor extractor) throws ParsingException {

        // important information
        int serviceId = getServiceId();
        String url = extractor.getUrl();
        String name = extractor.getName();
        ListDataInfoItem resultItem = new ListDataInfoItem(serviceId, url, name);

        // optional information
        try {
            resultItem.setThumbnailUrl(extractor.getThumbnailUrl());
        } catch (Exception e) {
            addError(e);
        }
        return resultItem;
    }

    @Override
    public void commit(ListDataInfoItemExtractor extractor) {
        try {
            addItem(extract(extractor));
        } catch (Exception e) {
            addError(e);
        }
    }

    public List<ListDataInfoItem> getListDataInfoItemList() {
        List<ListDataInfoItem> siiList = new Vector<>();
        for(InfoItem ii : super.getItems()) {
            if(ii instanceof ListDataInfoItem) {
                siiList.add((ListDataInfoItem) ii);
            }
        }
        return siiList;
    }
}
