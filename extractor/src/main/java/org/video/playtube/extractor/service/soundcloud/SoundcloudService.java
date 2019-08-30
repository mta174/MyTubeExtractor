package org.video.playtube.extractor.service.soundcloud;

import static java.util.Collections.singletonList;
import static org.video.playtube.extractor.StreamingService.ServiceInfo.MediaCapability.AUDIO;

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
import org.video.playtube.extractor.service.soundcloud.channel.SoundcloudChannelExtractor;
import org.video.playtube.extractor.service.soundcloud.channel.SoundcloudChannelLinkHandlerFactory;
import org.video.playtube.extractor.service.soundcloud.genre.SoundcloudGenreExtractor;
import org.video.playtube.extractor.service.soundcloud.genre.SoundcloudGenreLinkHandlerFactory;
import org.video.playtube.extractor.service.soundcloud.kiosk.JedenTagEinSetExtractor;
import org.video.playtube.extractor.service.soundcloud.kiosk.JedenTagEinSetLinkHandlerFactory;
import org.video.playtube.extractor.service.soundcloud.kiosk.SoundcloudChartsExtractor;
import org.video.playtube.extractor.service.soundcloud.kiosk.SoundcloudChartsLinkHandlerFactory;
import org.video.playtube.extractor.service.soundcloud.listdata.SoundcloudListDataExtractor;
import org.video.playtube.extractor.service.soundcloud.listdata.SoundcloudListDataLinkHandlerFactory;
import org.video.playtube.extractor.service.soundcloud.playlist.SoundcloudPlaylistExtractor;
import org.video.playtube.extractor.service.soundcloud.playlist.SoundcloudPlaylistLinkHandlerFactory;
import org.video.playtube.extractor.service.soundcloud.search.SoundcloudSearchExtractor;
import org.video.playtube.extractor.service.soundcloud.search.SoundcloudSearchQueryHandlerFactory;
import org.video.playtube.extractor.service.soundcloud.streams.SoundcloudStreamExtractor;
import org.video.playtube.extractor.service.soundcloud.streams.SoundcloudStreamLinkHandlerFactory;
import org.video.playtube.extractor.stream.StreamExtractor;
import org.video.playtube.extractor.subscription.SubscriptionExtractor;
import org.video.playtube.extractor.suggest.SuggestExtractor;
import org.video.playtube.extractor.util.ExtractorConstant;
import org.video.playtube.extractor.util.Localization;

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
