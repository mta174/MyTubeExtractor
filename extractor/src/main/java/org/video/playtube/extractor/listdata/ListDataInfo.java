package org.video.playtube.extractor.listdata;

import org.video.playtube.extractor.ListExtractor;
import org.video.playtube.extractor.ListInfo;
import org.video.playtube.extractor.PlayTube;
import org.video.playtube.extractor.StreamingService;
import org.video.playtube.extractor.exception.ExtractionException;
import org.video.playtube.extractor.exception.ParsingException;
import org.video.playtube.extractor.linkhandler.ListLinkHandler;
import org.video.playtube.extractor.stream.StreamInfoItem;
import org.video.playtube.extractor.util.ExtractorHelper;

import java.io.IOException;

public class ListDataInfo extends ListInfo<StreamInfoItem> {
    private static final String TAG = ListDataInfo.class.getSimpleName();

    public ListDataInfo(int serviceId, ListLinkHandler linkHandler, String name) throws ParsingException {
        super(serviceId, linkHandler, name);
    }

    public static ListDataInfo getInfo(String url) throws IOException, ExtractionException {
        return getInfo(PlayTube.getServiceByUrl(url), url);
    }

    public static ListDataInfo getInfo(StreamingService service, String url) throws IOException, ExtractionException {
        ListDataExtractor extractor = service.getListDataExtractor(url);
        extractor.fetchPage();
        return getInfo(extractor);
    }

    public static ListExtractor.InfoItemsPage<StreamInfoItem> getMoreItems(StreamingService service, String url, String pageUrl) throws IOException, ExtractionException {
        return service.getListDataExtractor(url).getPage(pageUrl);
    }

    public static ListDataInfo getInfo(ListDataExtractor extractor) throws ExtractionException {
        final ListDataInfo info = new ListDataInfo(extractor.getServiceId(), extractor.getLinkHandler(), extractor.getName());

        try {
            info.setOriginalUrl(extractor.getOriginalUrl());
        } catch (Exception e) {
            info.addError(e);
        }

        final ListExtractor.InfoItemsPage<StreamInfoItem> itemsPage = ExtractorHelper.getItemsPageOrLogError(info, extractor);
        info.setRelatedItems(itemsPage.getItems());
        info.setNextPageUrl(itemsPage.getNextPageUrl());
        return info;
    }
}
