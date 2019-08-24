package com.mta.playtube.extractor.listdata;

import com.mta.playtube.extractor.InfoItem;

public class ListDataInfoItem extends InfoItem {
    public ListDataInfoItem(int serviceId, String url, String name) {
        super(InfoType.LISTDATA, serviceId, url, name);
    }
}
