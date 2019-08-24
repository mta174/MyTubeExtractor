package com.mta.playtube.extractor.playlist;

import com.mta.playtube.extractor.ListExtractor;
import com.mta.playtube.extractor.ListInfo;
import com.mta.playtube.extractor.NewPipe;
import com.mta.playtube.extractor.StreamingService;
import com.mta.playtube.extractor.exceptions.ExtractionException;
import com.mta.playtube.extractor.exceptions.ParsingException;
import com.mta.playtube.extractor.linkhandler.ListLinkHandler;
import com.mta.playtube.extractor.stream.StreamInfoItem;
import com.mta.playtube.extractor.utils.ExtractorHelper;

import java.io.IOException;

public class PlaylistInfo extends ListInfo<StreamInfoItem> {
    private static final String TAG = PlaylistInfo.class.getSimpleName();

    private PlaylistInfo(int serviceId, ListLinkHandler linkHandler, String name) throws ParsingException {
        super(serviceId, linkHandler, name);
    }

    public static PlaylistInfo getInfo(String url) throws IOException, ExtractionException {
        return getInfo(NewPipe.getServiceByUrl(url), url);
    }

    public static PlaylistInfo getInfo(StreamingService service, String url) throws IOException, ExtractionException {
        PlaylistExtractor extractor = service.getPlaylistExtractor(url);
        extractor.fetchPage();
        return getInfo(extractor);
    }

    public static ListExtractor.InfoItemsPage<StreamInfoItem> getMoreItems(StreamingService service, String url, String pageUrl) throws IOException, ExtractionException {
        return service.getPlaylistExtractor(url).getPage(pageUrl);
    }

    /**
     * Get PlaylistInfo from PlaylistExtractor
     *
     * @param extractor an extractor where fetchPage() was already got called on.
     */
    public static PlaylistInfo getInfo(PlaylistExtractor extractor) throws ExtractionException {
        final PlaylistInfo info = new PlaylistInfo(extractor.getServiceId(), extractor.getLinkHandler(), extractor.getName());

        try {
            info.setOriginalUrl(extractor.getOriginalUrl());
        } catch (Exception e) {
            info.addError(e);
        }
        try {
            info.setStreamCount(extractor.getStreamCount());
        } catch (Exception e) {
            info.addError(e);
        }
        try {
            info.setThumbnailUrl(extractor.getThumbnailUrl());
        } catch (Exception e) {
            info.addError(e);
        }
        try {
            info.setUploaderUrl(extractor.getUploaderUrl());
        } catch (Exception e) {
            info.addError(e);
        }
        try {
            info.setUploaderName(extractor.getUploaderName());
        } catch (Exception e) {
            info.addError(e);
        }
        try {
            info.setUploaderAvatarUrl(extractor.getUploaderAvatarUrl());
        } catch (Exception e) {
            info.addError(e);
        }
        try {
            info.setBannerUrl(extractor.getBannerUrl());
        } catch (Exception e) {
            info.addError(e);
        }

        final ListExtractor.InfoItemsPage<StreamInfoItem> itemsPage = ExtractorHelper.getItemsPageOrLogError(info, extractor);
        info.setRelatedItems(itemsPage.getItems());
        info.setNextPageUrl(itemsPage.getNextPageUrl());

        return info;
    }

    private String thumbnailUrl;
    private String bannerUrl;
    private String uploaderUrl;
    private String uploaderName;
    private String uploaderAvatarUrl;
    private long streamCount = 0;

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public String getUploaderUrl() {
        return uploaderUrl;
    }

    public void setUploaderUrl(String uploaderUrl) {
        this.uploaderUrl = uploaderUrl;
    }

    public String getUploaderName() {
        return uploaderName;
    }

    public void setUploaderName(String uploaderName) {
        this.uploaderName = uploaderName;
    }

    public String getUploaderAvatarUrl() {
        return uploaderAvatarUrl;
    }

    public void setUploaderAvatarUrl(String uploaderAvatarUrl) {
        this.uploaderAvatarUrl = uploaderAvatarUrl;
    }

    public long getStreamCount() {
        return streamCount;
    }

    public void setStreamCount(long streamCount) {
        this.streamCount = streamCount;
    }
}
