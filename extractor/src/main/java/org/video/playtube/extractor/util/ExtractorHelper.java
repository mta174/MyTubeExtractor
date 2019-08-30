package org.video.playtube.extractor.util;

import org.video.playtube.extractor.Info;
import org.video.playtube.extractor.InfoItem;
import org.video.playtube.extractor.InfoItemsCollector;
import org.video.playtube.extractor.ListExtractor;
import org.video.playtube.extractor.ListExtractor.InfoItemsPage;
import org.video.playtube.extractor.stream.StreamExtractor;
import org.video.playtube.extractor.stream.StreamInfo;

import java.util.Collections;
import java.util.List;

public class ExtractorHelper {
    private ExtractorHelper() {}

    public static <T extends InfoItem> InfoItemsPage<T> getItemsPageOrLogError(Info info, ListExtractor<T> extractor) {
        try {
            InfoItemsPage<T> page = extractor.getInitialPage();
            info.addAllErrors(page.getErrors());

            return page;
        } catch (Exception e) {
            info.addError(e);
            return InfoItemsPage.emptyPage();
        }
    }


    public static List<InfoItem> getRelatedVideosOrLogError(StreamInfo info, StreamExtractor extractor) {
        try {
            InfoItemsCollector<? extends InfoItem, ?> collector = extractor.getRelatedStreams();
            if(collector == null) return Collections.emptyList();
            info.addAllErrors(collector.getErrors());

            //noinspection unchecked
            return (List<InfoItem>) collector.getItems();
        } catch (Exception e) {
            info.addError(e);
            return Collections.emptyList();
        }
    }

}
