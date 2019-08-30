package org.video.playtube.extractor.listdata;

import org.video.playtube.extractor.ListExtractor;
import org.video.playtube.extractor.StreamingService;
import org.video.playtube.extractor.exception.ParsingException;
import org.video.playtube.extractor.linkhandler.ListLinkHandler;
import org.video.playtube.extractor.stream.StreamInfoItem;
import org.video.playtube.extractor.util.Localization;

import javax.annotation.Nonnull;

public abstract class ListDataExtractor extends ListExtractor<StreamInfoItem> {
    @Nonnull
    @Override
    public abstract String getName() throws ParsingException;

    public ListDataExtractor(StreamingService service, ListLinkHandler linkHandler, Localization localization) {
        super(service, linkHandler, localization);
    }
}
