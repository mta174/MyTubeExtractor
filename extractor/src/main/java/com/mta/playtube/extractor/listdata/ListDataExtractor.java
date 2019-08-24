package com.mta.playtube.extractor.listdata;

import com.mta.playtube.extractor.ListExtractor;
import com.mta.playtube.extractor.StreamingService;
import com.mta.playtube.extractor.exceptions.ParsingException;
import com.mta.playtube.extractor.linkhandler.ListLinkHandler;
import com.mta.playtube.extractor.stream.StreamInfoItem;
import com.mta.playtube.extractor.utils.Localization;

import javax.annotation.Nonnull;

public abstract class ListDataExtractor extends ListExtractor<StreamInfoItem> {
    @Nonnull
    @Override
    public abstract String getName() throws ParsingException;

    public ListDataExtractor(StreamingService service, ListLinkHandler linkHandler, Localization localization) {
        super(service, linkHandler, localization);
    }
}
