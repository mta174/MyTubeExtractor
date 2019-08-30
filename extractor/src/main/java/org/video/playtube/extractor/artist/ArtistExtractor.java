package org.video.playtube.extractor.artist;

import org.video.playtube.extractor.ListExtractor;
import org.video.playtube.extractor.StreamingService;
import org.video.playtube.extractor.exception.ParsingException;
import org.video.playtube.extractor.stream.StreamInfoItem;
import org.video.playtube.extractor.linkhandler.ListLinkHandler;
import org.video.playtube.extractor.util.Localization;
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
