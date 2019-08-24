package com.mta.playtube.extractor.listdata;

import com.mta.playtube.extractor.InfoItem;
import com.mta.playtube.extractor.InfoItemsCollector;
import com.mta.playtube.extractor.exceptions.ParsingException;

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
