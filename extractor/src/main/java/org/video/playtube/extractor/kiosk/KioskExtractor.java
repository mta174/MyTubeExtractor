package org.video.playtube.extractor.kiosk;

import org.video.playtube.extractor.InfoItem;
import org.video.playtube.extractor.ListExtractor;
import org.video.playtube.extractor.StreamingService;
import org.video.playtube.extractor.exception.ParsingException;
import org.video.playtube.extractor.linkhandler.ListLinkHandler;
import org.video.playtube.extractor.util.Localization;

import javax.annotation.Nonnull;

public abstract class KioskExtractor<T extends InfoItem> extends ListExtractor<T> {
    private final String id;

    public KioskExtractor(StreamingService streamingService, ListLinkHandler linkHandler, String kioskId, Localization localization) {
        super(streamingService, linkHandler, localization);
        this.id = kioskId;
    }

    @Nonnull
    @Override
    public String getId() {
        return id;
    }

    /**
     * Id should be the name of the kiosk, tho Id is used for identifing it in the frontend,
     * so id should be kept in english.
     * In order to get the name of the kiosk in the desired language we have to
     * crawl if from the website.
     * @return the tranlsated version of id
     * @throws ParsingException
     */
    @Nonnull
    @Override
    public abstract String getName() throws ParsingException;
}
