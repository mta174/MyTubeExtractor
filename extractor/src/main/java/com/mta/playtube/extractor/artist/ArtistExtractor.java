package com.mta.playtube.extractor.artist;

import com.mta.playtube.extractor.ListExtractor;
import com.mta.playtube.extractor.StreamingService;
import com.mta.playtube.extractor.exceptions.ParsingException;
import com.mta.playtube.extractor.linkhandler.ListLinkHandler;
import com.mta.playtube.extractor.utils.Localization;
import com.mta.playtube.extractor.stream.StreamInfoItem;

import javax.annotation.Nonnull;


public abstract class ArtistExtractor extends ListExtractor<StreamInfoItem> {

    @Nonnull
    @Override
    public abstract String getName() throws ParsingException;

    public abstract String getThumbnail() throws ParsingException;
    public abstract String getArtistId() throws ParsingException;
    public abstract String getArtistName() throws ParsingException;
    public abstract String getVersion() throws ParsingException;
    //public abstract int getTotal() throws ParsingException;

    public ArtistExtractor(StreamingService service, ListLinkHandler linkHandler, Localization localization) {
        super(service, linkHandler, localization);
    }

}
