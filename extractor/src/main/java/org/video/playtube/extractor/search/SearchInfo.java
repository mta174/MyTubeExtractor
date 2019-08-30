package org.video.playtube.extractor.search;

import org.video.playtube.extractor.InfoItem;
import org.video.playtube.extractor.ListExtractor;
import org.video.playtube.extractor.ListInfo;
import org.video.playtube.extractor.StreamingService;
import org.video.playtube.extractor.exception.ExtractionException;
import org.video.playtube.extractor.linkhandler.SearchQueryHandler;
import org.video.playtube.extractor.util.ExtractorHelper;

import java.io.IOException;


public class SearchInfo extends ListInfo<InfoItem> {

    private String searchString;
    private String searchSuggestion;

    public SearchInfo(int serviceId, SearchQueryHandler qIHandler, String searchString) {
        super(serviceId, qIHandler, "Search");
        this.searchString = searchString;
    }

    public static SearchInfo getInfo(StreamingService service, SearchQueryHandler searchQuery) throws ExtractionException, IOException {
        SearchExtractor extractor = service.getSearchExtractor(searchQuery);
        extractor.fetchPage();
        return getInfo(extractor);
    }

    public static SearchInfo getInfo(SearchExtractor extractor) throws ExtractionException, IOException {
        final SearchInfo info = new SearchInfo(extractor.getServiceId(), extractor.getLinkHandler(), extractor.getSearchString());

        try {
            info.setOriginalUrl(extractor.getOriginalUrl());
        } catch (Exception e) {
            info.addError(e);
        }
        try {
            info.searchSuggestion = extractor.getSearchSuggestion();
        } catch (Exception e) {
            info.addError(e);
        }

        ListExtractor.InfoItemsPage<InfoItem> page = ExtractorHelper.getItemsPageOrLogError(info, extractor);
        info.setRelatedItems(page.getItems());
        info.setNextPageUrl(page.getNextPageUrl());

        return info;
    }


    public static ListExtractor.InfoItemsPage<InfoItem> getMoreItems(StreamingService service, SearchQueryHandler query, String pageUrl) throws IOException, ExtractionException {
        return service.getSearchExtractor(query).getPage(pageUrl);
    }

    // Getter
    public String getSearchString() {
        return searchString;
    }

    public String getSearchSuggestion() {
        return searchSuggestion;
    }
}
