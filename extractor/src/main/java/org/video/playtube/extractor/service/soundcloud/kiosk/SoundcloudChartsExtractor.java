package org.video.playtube.extractor.service.soundcloud.kiosk;

import org.video.playtube.extractor.Downloader;
import org.video.playtube.extractor.linkhandler.ListLinkHandler;
import org.video.playtube.extractor.StreamingService;
import org.video.playtube.extractor.exception.ExtractionException;
import org.video.playtube.extractor.kiosk.KioskExtractor;
import org.video.playtube.extractor.service.soundcloud.SoundcloudParsingHelper;
import org.video.playtube.extractor.stream.StreamInfoItem;
import org.video.playtube.extractor.stream.StreamInfoItemsCollector;
import org.video.playtube.extractor.util.ExtractorConstant;
import org.video.playtube.extractor.util.Localization;

import javax.annotation.Nonnull;
import java.io.IOException;

public class SoundcloudChartsExtractor extends KioskExtractor<StreamInfoItem> {
	private StreamInfoItemsCollector collector = null;
	private String nextPageUrl = null;

    public SoundcloudChartsExtractor(StreamingService service, ListLinkHandler linkHandler, String kioskId, Localization localization) {
        super(service, linkHandler, kioskId, localization);
    }

    @Override
    public void onFetchPage(@Nonnull Downloader downloader) {
    }

    @Nonnull
    @Override
    public String getName() {
        return getId();
    }

    @Override
    public InfoItemsPage<StreamInfoItem> getPage(String pageUrl) throws IOException, ExtractionException {
        if (pageUrl == null || pageUrl.isEmpty()) {
            throw new ExtractionException(new IllegalArgumentException("Page url is empty or null"));
        }

        StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());
        String nextPageUrl = SoundcloudParsingHelper.getStreamsFromApi(collector, pageUrl, true);

        return new InfoItemsPage<>(collector, nextPageUrl);
    }


    private void computNextPageAndStreams() throws IOException, ExtractionException {
        collector = new StreamInfoItemsCollector(getServiceId());

        StringBuffer buffer = new StringBuffer();
        buffer.append("https://api-v2.soundcloud.com/charts?genre=soundcloud:genres:all-music&client_id=").append(SoundcloudParsingHelper.clientId());
        if (getId().equals(ExtractorConstant.KIOS_TOP50)) {
            buffer.append("&kind=top");
        } else {
            buffer.append("&kind=trending");
        }
        String apiUrl = buffer.toString();
        nextPageUrl = SoundcloudParsingHelper.getStreamsFromApi(collector, apiUrl, true);
    }

    @Override
    public String getNextPageUrl() throws IOException, ExtractionException {
        if(nextPageUrl == null) {
            computNextPageAndStreams();
        }
        return nextPageUrl;
    }

    @Nonnull
    @Override
    public InfoItemsPage<StreamInfoItem> getInitialPage() throws IOException, ExtractionException {
        if(collector == null) {
            computNextPageAndStreams();
        }
        return new InfoItemsPage<>(collector, getNextPageUrl());
    }
}
