package org.video.playtube.extractor.listdata;

import org.video.playtube.extractor.InfoItem;

public class ListDataInfoItem extends InfoItem {
    public ListDataInfoItem(int serviceId, String url, String name) {
        super(InfoType.LISTDATA, serviceId, url, name);
    }
}
