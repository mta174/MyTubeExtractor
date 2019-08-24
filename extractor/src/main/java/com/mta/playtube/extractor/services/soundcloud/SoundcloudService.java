package com.mta.playtube.extractor.services.soundcloud;

import static java.util.Collections.singletonList;
import static com.mta.playtube.extractor.StreamingService.ServiceInfo.MediaCapability.AUDIO;

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
import com.mta.playtube.extractor.services.soundcloud.channel.SoundcloudChannelExtractor;
import com.mta.playtube.extractor.services.soundcloud.channel.SoundcloudChannelLinkHandlerFactory;
import com.mta.playtube.extractor.services.soundcloud.genre.SoundcloudGenreExtractor;
import com.mta.playtube.extractor.services.soundcloud.genre.SoundcloudGenreLinkHandlerFactory;
import com.mta.playtube.extractor.services.soundcloud.kiosk.JedenTagEinSetExtractor;
import com.mta.playtube.extractor.services.soundcloud.kiosk.JedenTagEinSetLinkHandlerFactory;
import com.mta.playtube.extractor.services.soundcloud.kiosk.SoundcloudChartsExtractor;
import com.mta.playtube.extractor.services.soundcloud.kiosk.SoundcloudChartsLinkHandlerFactory;
import com.mta.playtube.extractor.services.soundcloud.listdata.SoundcloudListDataExtractor;
import com.mta.playtube.extractor.services.soundcloud.listdata.SoundcloudListDataLinkHandlerFactory;
import com.mta.playtube.extractor.services.soundcloud.playlist.SoundcloudPlaylistExtractor;
import com.mta.playtube.extractor.services.soundcloud.playlist.SoundcloudPlaylistLinkHandlerFactory;
import com.mta.playtube.extractor.services.soundcloud.search.SoundcloudSearchExtractor;
import com.mta.playtube.extractor.services.soundcloud.search.SoundcloudSearchQueryHandlerFactory;
import com.mta.playtube.extractor.services.soundcloud.streams.SoundcloudStreamExtractor;
import com.mta.playtube.extractor.services.soundcloud.streams.SoundcloudStreamLinkHandlerFactory;
import com.mta.playtube.extractor.stream.StreamExtractor;
import com.mta.playtube.extractor.subscription.SubscriptionExtractor;
import com.mta.playtube.extractor.suggest.SuggestExtractor;
import com.mta.playtube.extractor.utils.ExtractorConstant;
import com.mta.playtube.extractor.utils.Localization;

public class SoundcloudService extends StreamingService {

    public SoundcloudService(int id) {
        super(id, "SoundCloud", singletonList(AUDIO));
    }

    @Override
    public SearchExtractor getSearchExtractor(SearchQueryHandler queryHandler, Localization localization) {
        return new SoundcloudSearchExtractor(this, queryHandler, localization);
    }

    @Override
    public SearchQueryHandlerFactory getSearchQHFactory() {
        return new SoundcloudSearchQueryHandlerFactory();
    }

    @Override
    public LinkHandlerFactory getStreamLHFactory() {
        return SoundcloudStreamLinkHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getChannelLHFactory() {
        return SoundcloudChannelLinkHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getGenreLHFactory() {
        return SoundcloudGenreLinkHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getPlaylistLHFactory() {
        return SoundcloudPlaylistLinkHandlerFactory.getInstance();
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
        return SoundcloudListDataLinkHandlerFactory.getInstance();
    }


    @Override
    public StreamExtractor getStreamExtractor(LinkHandler LinkHandler, Localization localization) {
        return new SoundcloudStreamExtractor(this, LinkHandler, localization);
    }

    @Override
    public ChannelExtractor getChannelExtractor(ListLinkHandler linkHandler, Localization localization) {
        return new SoundcloudChannelExtractor(this, linkHandler, localization);
    }

    @Override
    public GenreExtractor getGenreExtractor(ListLinkHandler linkHandler, Localization localization) throws ExtractionException {
        return new SoundcloudGenreExtractor(this, linkHandler, localization);
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
        return new SoundcloudListDataExtractor(this, linkHandler, localization);
    }

    @Override
    public PlaylistExtractor getPlaylistExtractor(ListLinkHandler linkHandler, Localization localization) {
        return new SoundcloudPlaylistExtractor(this, linkHandler, localization);
    }

    @Override
    public SuggestionExtractor getSuggestionExtractor(Localization localization) {
        return new SoundcloudSuggestionExtractor(getServiceId(), localization);
    }

    @Override
    public KioskList getKioskList() throws ExtractionException {
        KioskList.KioskExtractorFactory chartsFactory = new KioskList.KioskExtractorFactory() {
            @Override
            public KioskExtractor createNewKiosk(StreamingService streamingService, String url, String id, Localization local) throws ExtractionException {
                if(new JedenTagEinSetLinkHandlerFactory().onAcceptUrl(url)) {
                    return new JedenTagEinSetExtractor(SoundcloudService.this, new JedenTagEinSetLinkHandlerFactory().fromUrl(url), id, local);
                } else {
                    return new SoundcloudChartsExtractor(SoundcloudService.this, new SoundcloudChartsLinkHandlerFactory().fromUrl(url), id, local);
                }
            }
        };

        KioskList list = new KioskList(getServiceId());

        // add kiosks here e.g.:
        final SoundcloudChartsLinkHandlerFactory h = new SoundcloudChartsLinkHandlerFactory();
        final JedenTagEinSetLinkHandlerFactory jh = new JedenTagEinSetLinkHandlerFactory();
        try {
            list.addKioskEntry(chartsFactory, h, ExtractorConstant.KIOS_TOP50);
            list.addKioskEntry(chartsFactory, h, ExtractorConstant.KIOS_NEW_HOT);
            list.addKioskEntry(chartsFactory, jh, ExtractorConstant.KIOS_JEDEN_TAG_EIN_SET);
            list.setDefaultKiosk(ExtractorConstant.KIOS_NEW_HOT);
        } catch (Exception e) {
            throw new ExtractionException(e);
        }

        return list;
    }


    @Override
    public SubscriptionExtractor getSubscriptionExtractor() {
        return new SoundcloudSubscriptionExtractor(this);
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
