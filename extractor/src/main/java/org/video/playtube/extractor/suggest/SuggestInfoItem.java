package org.video.playtube.extractor.suggest;

import org.video.playtube.extractor.InfoItem;

public class SuggestInfoItem extends InfoItem {

    private String nextPageToken;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public SuggestInfoItem(int serviceId, String url, String name) {
        super(InfoType.SUGGEST, serviceId, url, name);
    }
}
