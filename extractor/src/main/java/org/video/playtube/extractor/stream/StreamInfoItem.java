package org.video.playtube.extractor.stream;

import org.video.playtube.extractor.InfoItem;

/**
 * Info object for previews of unopened videos, eg search results, related videos
 */
public class StreamInfoItem extends InfoItem {
    private final StreamType streamType;

    private String uploaderName;
    private String uploadDate;
    private long viewCount = -1;
    private long duration = -1;

    private String uploaderUrl = null;

    public StreamInfoItem(int serviceId, String url, String name, StreamType streamType) {
        super(InfoType.STREAM, serviceId, url, name);
        this.streamType = streamType;
    }

    public StreamType getStreamType() {
        return streamType;
    }

    public String getUploaderName() {
        return uploaderName;
    }

    public void setUploaderName(String uploader_name) {
        this.uploaderName = uploader_name;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String upload_date) {
        this.uploadDate = upload_date;
    }

    public long getViewCount() {
        return viewCount;
    }

    public void setViewCount(long view_count) {
        this.viewCount = view_count;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getUploaderUrl() {
        return uploaderUrl;
    }

    public void setUploaderUrl(String uploaderUrl) {
        this.uploaderUrl = uploaderUrl;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("StreamInfoItem{");
        buffer.append("streamType=").append(streamType);
        buffer.append("uploaderName='").append(uploaderName).append('\'');
        buffer.append("uploadDate='").append(uploadDate).append('\'');
        buffer.append("viewCount=").append(viewCount);
        buffer.append("duration=").append(duration);
        buffer.append("uploaderUrl='").append(uploaderUrl).append('\'');
        buffer.append("infoType=").append(getInfoType());
        buffer.append("serviceId=").append(getServiceId());
        buffer.append("url='").append(getUrl()).append('\'');
        buffer.append("name='").append(getName()).append('\'');
        buffer.append("thumbnailUrl='").append(getThumbnailUrl()).append('\'');
        buffer.append('}');
        return  buffer.toString();
    }
}