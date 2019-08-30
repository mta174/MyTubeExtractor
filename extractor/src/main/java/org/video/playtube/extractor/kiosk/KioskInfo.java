package org.video.playtube.extractor.kiosk;

import org.video.playtube.extractor.*;
import org.video.playtube.extractor.exception.ExtractionException;
import org.video.playtube.extractor.exception.ParsingException;
import org.video.playtube.extractor.stream.StreamInfoItem;
import org.video.playtube.extractor.linkhandler.ListLinkHandler;
import org.video.playtube.extractor.util.ExtractorHelper;

import java.io.IOException;

public class KioskInfo extends ListInfo<StreamInfoItem> {
    private static final String TAG = KioskInfo.class.getSimpleName();
    private KioskInfo(int serviceId, ListLinkHandler linkHandler, String name) throws ParsingException {
        super(serviceId, linkHandler, name);
    }

    public static ListExtractor.InfoItemsPage<StreamInfoItem> getMoreItems(StreamingService service, String url, String pageUrl) throws IOException, ExtractionException {
        KioskList kl = service.getKioskList();
        KioskExtractor extractor = kl.getExtractorByUrl(url, pageUrl);
        return extractor.getPage(pageUrl);
    }

    public static KioskInfo getInfo(String url) throws IOException, ExtractionException {
        return getInfo(PlayTube.getServiceByUrl(url), url);
    }

    public static KioskInfo getInfo(StreamingService service, String url) throws IOException, ExtractionException {
        KioskList kl = service.getKioskList();
        KioskExtractor extractor = kl.getExtractorByUrl(url, null);
        extractor.fetchPage();
        return getInfo(extractor);
    }

    /**
     * Get KioskInfo from KioskExtractor
     *
     * @param extractor an extractor where fetchPage() was already got called on.
     */
    public static KioskInfo getInfo(KioskExtractor extractor) throws ExtractionException {
        final KioskInfo info = new KioskInfo(extractor.getServiceId(), extractor.getLinkHandler(), extractor.getName());
        final ListExtractor.InfoItemsPage<StreamInfoItem> itemsPage = ExtractorHelper.getItemsPageOrLogError(info, extractor);
        info.setRelatedItems(itemsPage.getItems());
        info.setNextPageUrl(itemsPage.getNextPageUrl());
        return info;
    }
}
