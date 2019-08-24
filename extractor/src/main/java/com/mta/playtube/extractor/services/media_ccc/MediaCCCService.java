package com.mta.playtube.extractor.services.media_ccc;

import static java.util.Arrays.asList;
import static com.mta.playtube.extractor.StreamingService.ServiceInfo.MediaCapability.AUDIO;
import static com.mta.playtube.extractor.StreamingService.ServiceInfo.MediaCapability.VIDEO;

import java.io.IOException;

import com.mta.playtube.extractor.StreamingService;
import com.mta.playtube.extractor.SuggestionExtractor;
import com.mta.playtube.extractor.artist.ArtistExtractor;
import com.mta.playtube.extractor.channel.ChannelExtractor;
import com.mta.playtube.extractor.comments.CommentsExtractor;
import com.mta.playtube.extractor.exceptions.ExtractionException;
import com.mta.playtube.extractor.genre.GenreExtractor;
import com.mta.playtube.extractor.kiosk.KioskExtractor;
import com.mta.playtube.extractor.kiosk.KioskList;
import com.mta.playtube.extractor.linkhandler.LinkHandler;
import com.mta.playtube.extractor.linkhandler.LinkHandlerFactory;
import com.mta.playtube.extractor.linkhandler.ListLinkHandler;
import com.mta.playtube.extractor.linkhandler.ListLinkHandlerFactory;
import com.mta.playtube.extractor.linkhandler.SearchQueryHandler;
import com.mta.playtube.extractor.linkhandler.SearchQueryHandlerFactory;
import com.mta.playtube.extractor.listdata.ListDataExtractor;
import com.mta.playtube.extractor.playlist.PlaylistExtractor;
import com.mta.playtube.extractor.search.SearchExtractor;
import com.mta.playtube.extractor.services.media_ccc.extractors.MediaCCCConferenceExtractor;
import com.mta.playtube.extractor.services.media_ccc.extractors.MediaCCCConferenceKiosk;
import com.mta.playtube.extractor.services.media_ccc.extractors.MediaCCCSearchExtractor;
import com.mta.playtube.extractor.services.media_ccc.extractors.MediaCCCStreamExtractor;
import com.mta.playtube.extractor.services.media_ccc.linkHandler.MediaCCCConferenceLinkHandlerFactory;
import com.mta.playtube.extractor.services.media_ccc.linkHandler.MediaCCCConferencesListLinkHandlerFactory;
import com.mta.playtube.extractor.services.media_ccc.linkHandler.MediaCCCSearchQueryHandlerFactory;
import com.mta.playtube.extractor.services.media_ccc.linkHandler.MediaCCCStreamLinkHandlerFactory;
import com.mta.playtube.extractor.stream.StreamExtractor;
import com.mta.playtube.extractor.subscription.SubscriptionExtractor;
import com.mta.playtube.extractor.suggest.SuggestExtractor;
import com.mta.playtube.extractor.utils.Localization;

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
