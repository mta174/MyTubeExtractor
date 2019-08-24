package com.mta.playtube.extractor.services.soundcloud.kiosk;

import com.mta.playtube.extractor.services.soundcloud.SoundcloudParsingHelper;
import com.mta.playtube.extractor.Downloader;
import com.mta.playtube.extractor.linkhandler.ListLinkHandler;
import com.mta.playtube.extractor.StreamingService;
import com.mta.playtube.extractor.exceptions.ExtractionException;
import com.mta.playtube.extractor.kiosk.KioskExtractor;
import com.mta.playtube.extractor.stream.StreamInfoItem;
import com.mta.playtube.extractor.stream.StreamInfoItemsCollector;
import com.mta.playtube.extractor.utils.ExtractorConstant;
import com.mta.playtube.extractor.utils.Localization;

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
