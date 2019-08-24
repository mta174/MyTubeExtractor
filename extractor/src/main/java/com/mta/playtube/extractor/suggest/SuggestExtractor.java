package com.mta.playtube.extractor.suggest;

import com.mta.playtube.extractor.ListExtractor;
import com.mta.playtube.extractor.StreamingService;
import com.mta.playtube.extractor.exceptions.ParsingException;
import com.mta.playtube.extractor.linkhandler.ListLinkHandler;
import com.mta.playtube.extractor.stream.StreamInfoItem;
import com.mta.playtube.extractor.utils.Localization;

import javax.annotation.Nonnull;

public abstract class SuggestExtractor extends ListExtractor<StreamInfoItem> {

    @Nonnull
    @Override
    public abstract String getName() throws ParsingException;

    public abstract String getNextPageToken() throws ParsingException;
    public abstract String getKey() throws ParsingException;

    public SuggestExtractor(StreamingService service, ListLinkHandler linkHandler, Localization localization) {
        super(service, linkHandler, localization);
    }
}
