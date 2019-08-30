package org.video.playtube.extractor;

import java.io.Serializable;

public abstract class InfoItem implements Serializable {
    private final InfoType infoType;
    private final int serviceId;
    private final String url;
    private final String name;
    private String thumbnailUrl;

    public InfoItem(InfoType infoType, int serviceId, String url, String name) {
        this.infoType = infoType;
        this.serviceId = serviceId;
        this.url = url;
        this.name = name;
    }

    public InfoType getInfoType() {
        return infoType;
    }

    public int getServiceId() {
        return serviceId;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getClass().getSimpleName()).append("[url=\"").append(url).append("\", name=\"").append(name).append( "\"]");
        return buffer.toString();
    }

    public enum InfoType {
        STREAM,
        PLAYLIST,
        CHANNEL,
        COMMENT,
        ARTIST,
        LISTDATA,
        GENRE,
        NATIVEAD,
        SUGGEST
    }
}
