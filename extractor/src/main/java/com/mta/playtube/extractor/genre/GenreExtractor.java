package com.mta.playtube.extractor.genre;

import com.mta.playtube.extractor.ListExtractor;
import com.mta.playtube.extractor.StreamingService;
import com.mta.playtube.extractor.exceptions.ParsingException;
import com.mta.playtube.extractor.linkhandler.ListLinkHandler;
import com.mta.playtube.extractor.utils.Localization;

import javax.annotation.Nonnull;

public abstract class GenreExtractor extends ListExtractor<GenreInfoItem> {

    public GenreExtractor(StreamingService service, ListLinkHandler linkHandler, Localization localization) {
        super(service, linkHandler, localization);
    }

    @Nonnull
    @Override
    public abstract String getName() throws ParsingException;

    public abstract String getVersion() throws ParsingException;
}