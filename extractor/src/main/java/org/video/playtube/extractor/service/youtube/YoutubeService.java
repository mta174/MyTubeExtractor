package org.video.playtube.extractor.service.youtube;

import static java.util.Arrays.asList;
import static org.video.playtube.extractor.StreamingService.ServiceInfo.MediaCapability.AUDIO;
import static org.video.playtube.extractor.StreamingService.ServiceInfo.MediaCapability.COMMENTS;
import static org.video.playtube.extractor.StreamingService.ServiceInfo.MediaCapability.LIVE;
import static org.video.playtube.extractor.StreamingService.ServiceInfo.MediaCapability.VIDEO;

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
import org.video.playtube.extractor.service.youtube.extractor.MyArtistExtractor;
import org.video.playtube.extractor.service.youtube.extractor.YoutubeChannelExtractor;
import org.video.playtube.extractor.service.youtube.extractor.YoutubeCommentsExtractor;
import org.video.playtube.extractor.service.youtube.extractor.YoutubePlaylistExtractor;
import org.video.playtube.extractor.service.youtube.extractor.MySuggestExtractor;
import org.video.playtube.extractor.service.youtube.extractor.YoutubeSearchExtractor;
import org.video.playtube.extractor.service.youtube.extractor.YoutubeStreamExtractor;
import org.video.playtube.extractor.service.youtube.extractor.YoutubeSubscriptionExtractor;
import org.video.playtube.extractor.service.youtube.extractor.YoutubeSuggestionExtractor;
import org.video.playtube.extractor.service.youtube.extractor.YoutubeTrendingExtractor;
import org.video.playtube.extractor.service.youtube.linkHandler.ArtistLinkHandlerFactory;
import org.video.playtube.extractor.service.youtube.linkHandler.YoutubeChannelLinkHandlerFactory;
import org.video.playtube.extractor.service.youtube.linkHandler.YoutubeCommentsLinkHandlerFactory;
import org.video.playtube.extractor.service.youtube.linkHandler.YoutubePlaylistLinkHandlerFactory;
import org.video.playtube.extractor.service.youtube.linkHandler.YoutubeSuggestLinkHandlerFactory;
import org.video.playtube.extractor.service.youtube.linkHandler.YoutubeSearchQueryHandlerFactory;
import org.video.playtube.extractor.service.youtube.linkHandler.YoutubeStreamLinkHandlerFactory;
import org.video.playtube.extractor.service.youtube.linkHandler.YoutubeTrendingLinkHandlerFactory;
import org.video.playtube.extractor.stream.StreamExtractor;
import org.video.playtube.extractor.subscription.SubscriptionExtractor;
import org.video.playtube.extractor.suggest.SuggestExtractor;
import org.video.playtube.extractor.util.ExtractorConstant;
import org.video.playtube.extractor.util.Localization;

public class YoutubeService extends StreamingService {

    public YoutubeService(int id) {
        super(id, "YouTube", asList(AUDIO, VIDEO, LIVE, COMMENTS));
    }

    @Override
    public SearchExtractor getSearchExtractor(SearchQueryHandler query, Localization localization) {
        return new YoutubeSearchExtractor(this, query, localization);
    }

    @Override
    public LinkHandlerFactory getStreamLHFactory() {
        return YoutubeStreamLinkHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getChannelLHFactory() {
        return YoutubeChannelLinkHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getGenreLHFactory() {
        return null;
    }

    @Override
    public ListLinkHandlerFactory getPlaylistLHFactory() {
        return YoutubePlaylistLinkHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getArtistLHFactory() {
        return ArtistLinkHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getSuggestLHFactory() {
        return YoutubeSuggestLinkHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getListDataLHFactory() {
        return null;
    }

    @Override
    public SearchQueryHandlerFactory getSearchQHFactory() {
        return YoutubeSearchQueryHandlerFactory.getInstance();
    }

    @Override
    public StreamExtractor getStreamExtractor(LinkHandler linkHandler, Localization localization) {
        return new YoutubeStreamExtractor(this, linkHandler, localization);
    }

    @Override
    public ChannelExtractor getChannelExtractor(ListLinkHandler linkHandler, Localization localization) {
        return new YoutubeChannelExtractor(this, linkHandler, localization);
    }

    @Override
    public GenreExtractor getGenreExtractor(ListLinkHandler linkHandler, Localization localization) throws ExtractionException {
        return null;
    }

    @Override
    public ArtistExtractor getArtistExtractor(ListLinkHandler linkHandler, Localization localization) {
        return new MyArtistExtractor(this, linkHandler, localization);
    }

    @Override
    public SuggestExtractor getSuggestExtractor(ListLinkHandler linkHandler, Localization localization) throws ExtractionException {
        return new MySuggestExtractor(this, linkHandler, localization);
    }

    @Override
    public ListDataExtractor getListDataExtractor(ListLinkHandler linkHandler, Localization localization) throws ExtractionException {
        return null;
    }

    @Override
    public PlaylistExtractor getPlaylistExtractor(ListLinkHandler linkHandler, Localization localization) {
        return new YoutubePlaylistExtractor(this, linkHandler, localization);
    }

    @Override
    public SuggestionExtractor getSuggestionExtractor(Localization localization) {
        return new YoutubeSuggestionExtractor(getServiceId(), localization);
    }

    @Override
    public KioskList getKioskList() throws ExtractionException {
        KioskList list = new KioskList(getServiceId());
        // add kiosks here e.g.:
        try {
            list.addKioskEntry(new KioskList.KioskExtractorFactory() {
                @Override
                public KioskExtractor createNewKiosk(StreamingService streamingService, String url, String id, Localization local) throws ExtractionException {
                    return new YoutubeTrendingExtractor(YoutubeService.this, new YoutubeTrendingLinkHandlerFactory().fromUrl(url), id, local);
                }
            }, new YoutubeTrendingLinkHandlerFactory(), ExtractorConstant.KIOS_TRENDING);
            list.setDefaultKiosk(ExtractorConstant.KIOS_TRENDING);
        } catch (Exception e) {
            throw new ExtractionException(e);
        }

        return list;
    }

    @Override
    public SubscriptionExtractor getSubscriptionExtractor() {
        return new YoutubeSubscriptionExtractor(this);
    }

    @Override
    public ListLinkHandlerFactory getCommentsLHFactory() {
        return YoutubeCommentsLinkHandlerFactory.getInstance();
    }

    @Override
    public CommentsExtractor getCommentsExtractor(ListLinkHandler urlIdHandler, Localization localization) throws ExtractionException {
        return new YoutubeCommentsExtractor(this, urlIdHandler, localization);
    }

}
