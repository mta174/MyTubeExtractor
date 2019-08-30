package org.video.playtube.extractor.service.media_ccc;

import static java.util.Arrays.asList;
import static org.video.playtube.extractor.StreamingService.ServiceInfo.MediaCapability.AUDIO;
import static org.video.playtube.extractor.StreamingService.ServiceInfo.MediaCapability.VIDEO;

import java.io.IOException;

import org.video.playtube.extractor.StreamingService;
import org.video.playtube.extractor.SuggestionExtractor;
import org.video.playtube.extractor.artist.ArtistExtractor;
import org.video.playtube.extractor.channel.ChannelExtractor;
import org.video.playtube.extractor.comment.CommentsExtractor;
import org.video.playtube.extractor.exception.ExtractionException;
import org.video.playtube.extractor.genre.GenreExtractor;
import org.video.playtube.extractor.kiosk.KioskExtractor;
import org.video.playtube.extractor.kiosk.KioskList;
import org.video.playtube.extractor.linkhandler.LinkHandler;
import org.video.playtube.extractor.linkhandler.LinkHandlerFactory;
import org.video.playtube.extractor.linkhandler.ListLinkHandler;
import org.video.playtube.extractor.linkhandler.ListLinkHandlerFactory;
import org.video.playtube.extractor.linkhandler.SearchQueryHandler;
import org.video.playtube.extractor.linkhandler.SearchQueryHandlerFactory;
import org.video.playtube.extractor.listdata.ListDataExtractor;
import org.video.playtube.extractor.playlist.PlaylistExtractor;
import org.video.playtube.extractor.search.SearchExtractor;
import org.video.playtube.extractor.service.media_ccc.extractors.MediaCCCConferenceExtractor;
import org.video.playtube.extractor.service.media_ccc.extractors.MediaCCCConferenceKiosk;
import org.video.playtube.extractor.service.media_ccc.extractors.MediaCCCSearchExtractor;
import org.video.playtube.extractor.service.media_ccc.extractors.MediaCCCStreamExtractor;
import org.video.playtube.extractor.service.media_ccc.linkHandler.MediaCCCConferenceLinkHandlerFactory;
import org.video.playtube.extractor.service.media_ccc.linkHandler.MediaCCCConferencesListLinkHandlerFactory;
import org.video.playtube.extractor.service.media_ccc.linkHandler.MediaCCCSearchQueryHandlerFactory;
import org.video.playtube.extractor.service.media_ccc.linkHandler.MediaCCCStreamLinkHandlerFactory;
import org.video.playtube.extractor.stream.StreamExtractor;
import org.video.playtube.extractor.subscription.SubscriptionExtractor;
import org.video.playtube.extractor.suggest.SuggestExtractor;
import org.video.playtube.extractor.util.Localization;

public class MediaCCCService extends StreamingService {
    public MediaCCCService(int id) {
        super(id, "MediaCCC", asList(AUDIO, VIDEO));
    }

    @Override
    public SearchExtractor getSearchExtractor(SearchQueryHandler query, Localization localization) {
        return new MediaCCCSearchExtractor(this, query, localization);
    }

    @Override
    public LinkHandlerFactory getStreamLHFactory() {
        return new MediaCCCStreamLinkHandlerFactory();
    }

    @Override
    public ListLinkHandlerFactory getChannelLHFactory() {
        return new MediaCCCConferenceLinkHandlerFactory();
    }

    @Override
    public ListLinkHandlerFactory getGenreLHFactory() {
        return null;
    }

    @Override
    public ListLinkHandlerFactory getPlaylistLHFactory() {
        return null;
    }

    @Override
    public ListLinkHandlerFactory getArtistLHFactory() {
        return null;
    }

    @Override
    public ListLinkHandlerFactory getSuggestLHFactory() {
        return null;
    }

    @Override
    public ListLinkHandlerFactory getListDataLHFactory() {
        return null;
    }

    @Override
    public SearchQueryHandlerFactory getSearchQHFactory() {
        return new MediaCCCSearchQueryHandlerFactory();
    }

    @Override
    public StreamExtractor getStreamExtractor(LinkHandler linkHandler, Localization localization) {
        return new MediaCCCStreamExtractor(this, linkHandler, localization);
    }

    @Override
    public ChannelExtractor getChannelExtractor(ListLinkHandler linkHandler, Localization localization) {
        return new MediaCCCConferenceExtractor(this, linkHandler, localization);
    }

    @Override
    public GenreExtractor getGenreExtractor(ListLinkHandler linkHandler, Localization localization) throws ExtractionException {
        return null;
    }

    @Override
    public ArtistExtractor getArtistExtractor(ListLinkHandler linkHandler, Localization localization) throws ExtractionException {
        return null;
    }

    @Override
    public SuggestExtractor getSuggestExtractor(ListLinkHandler linkHandler, Localization localization) throws ExtractionException {
        return null;
    }

    @Override
    public ListDataExtractor getListDataExtractor(ListLinkHandler linkHandler, Localization localization) throws ExtractionException {
        return null;
    }

    @Override
    public PlaylistExtractor getPlaylistExtractor(ListLinkHandler linkHandler, Localization localization) {
        return null;
    }

    @Override
    public SuggestionExtractor getSuggestionExtractor(Localization localization) {
        return null;
    }

    @Override
    public KioskList getKioskList() throws ExtractionException {
        KioskList list = new KioskList(getServiceId());

        // add kiosks here e.g.:
        try {
            list.addKioskEntry(new KioskList.KioskExtractorFactory() {
                @Override
                public KioskExtractor createNewKiosk(StreamingService streamingService, String url, String kioskId, Localization localization) throws ExtractionException, IOException {
                    return new MediaCCCConferenceKiosk(MediaCCCService.this, new MediaCCCConferencesListLinkHandlerFactory().fromUrl(url), kioskId, localization);
                }
            }, new MediaCCCConferencesListLinkHandlerFactory(), "conferences");
            list.setDefaultKiosk("conferences");
        } catch (Exception e) {
            throw new ExtractionException(e);
        }

        return list;
    }

    @Override
    public SubscriptionExtractor getSubscriptionExtractor() {
        return null;
    }

    @Override
    public ListLinkHandlerFactory getCommentsLHFactory() {
        return null;
    }

    @Override
    public CommentsExtractor getCommentsExtractor(ListLinkHandler linkHandler, Localization localization) throws ExtractionException {
        return null;
    }

}
