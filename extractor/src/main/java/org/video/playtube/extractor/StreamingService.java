package org.video.playtube.extractor;

import java.util.Collections;
import java.util.List;

import org.video.playtube.extractor.artist.ArtistExtractor;
import org.video.playtube.extractor.channel.ChannelExtractor;
import org.video.playtube.extractor.comment.CommentsExtractor;
import org.video.playtube.extractor.exception.ExtractionException;
import org.video.playtube.extractor.exception.ParsingException;
import org.video.playtube.extractor.genre.GenreExtractor;
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
import org.video.playtube.extractor.stream.StreamExtractor;
import org.video.playtube.extractor.subscription.SubscriptionExtractor;
import org.video.playtube.extractor.suggest.SuggestExtractor;
import org.video.playtube.extractor.util.Localization;


public abstract class StreamingService {

    /**
     * This class holds meta information about the service implementation.
     */
    public static class ServiceInfo {
        private final String name;
        private final List<MediaCapability> mediaCapabilities;

        /**
         * Creates a new instance of a ServiceInfo
         * @param name the name of the service
         * @param mediaCapabilities the type of media this service can handle
         */
        public ServiceInfo(String name, List<MediaCapability> mediaCapabilities) {
            this.name = name;
            this.mediaCapabilities = Collections.unmodifiableList(mediaCapabilities);
        }

        public String getName() {
            return name;
        }

        public List<MediaCapability> getMediaCapabilities() {
            return mediaCapabilities;
        }

        public enum MediaCapability {
            AUDIO, VIDEO, LIVE, COMMENTS
        }
    }

    /**
     * LinkType will be used to determine which type of URL you are handling, and therefore which part
     * of PlayTube should handle a certain URL.
     */
    public enum LinkType {
        NONE,
        STREAM,
        CHANNEL,
        PLAYLIST,
        ARTIST,
        LISTDATA,
        GENRE,
        SUGGEST,
    }

    private final int serviceId;
    private final ServiceInfo serviceInfo;

    /**
     * Creates a new Streaming service.
     * If you Implement one do not set id within your implementation of this extractor, instead
     * set the id when you put the extractor into
     * All other parameters can be set directly from the overriding constructor.
     * @param id the number of the service to identify him within the PlayTube frontend
     * @param name the name of the service
     * @param capabilities the type of media this service can handle
     */
    public StreamingService(int id, String name, List<ServiceInfo.MediaCapability> capabilities) {
        this.serviceId = id;
        this.serviceInfo = new ServiceInfo(name, capabilities);
    }

    public final int getServiceId() {
        return serviceId;
    }

    public ServiceInfo getServiceInfo() {
        return serviceInfo;
    }

    @Override
    public String toString() {
        return serviceId + ":" + serviceInfo.getName();
    }

    ////////////////////////////////////////////
    // Url Id handler
    ////////////////////////////////////////////

    /**
     * Must return a new instance of an implementation of LinkHandlerFactory for streams.
     * @return an instance of a LinkHandlerFactory for streams
     */
    public abstract LinkHandlerFactory getStreamLHFactory();

    /**
     * Must return a new instance of an implementation of ListLinkHandlerFactory for channels.
     * If support for channels is not given null must be returned.
     * @return an instance of a ListLinkHandlerFactory for channels or null
     */
    public abstract ListLinkHandlerFactory getChannelLHFactory();

    /**
     * Must return a new instance of an implementation of ListLinkHandlerFactory for channels.
     * If support for channels is not given null must be returned.
     * @return an instance of a ListLinkHandlerFactory for channels or null
     */
    public abstract ListLinkHandlerFactory getGenreLHFactory();

    /**
     * Must return a new instance of an implementation of ListLinkHandlerFactory for playlists.
     * If support for playlists is not given null must be returned.
     * @return an instance of a ListLinkHandlerFactory for playlists or null
     */
    public abstract ListLinkHandlerFactory getPlaylistLHFactory();


    /**
     * Must return a new instance of an implementation of ListLinkHandlerFactory for artists.
     * If support for channels is not given null must be returned.
     * @return an instance of a ListLinkHandlerFactory for artists or null
     */
    public abstract ListLinkHandlerFactory getArtistLHFactory();


    /**
     * Must return a new instance of an implementation of ListLinkHandlerFactory for artists.
     * If support for channels is not given null must be returned.
     * @return an instance of a ListLinkHandlerFactory for artists or null
     */
    public abstract ListLinkHandlerFactory getSuggestLHFactory();


    /**
     * Must return a new instance of an implementation of ListLinkHandlerFactory for artists.
     * If support for channels is not given null must be returned.
     * @return an instance of a ListLinkHandlerFactory for artists or null
     */
    public abstract ListLinkHandlerFactory getListDataLHFactory();

    /**
     * Must return an instance of an implementation of SearchQueryHandlerFactory.
     * @return an instance of a SearchQueryHandlerFactory
     */
    public abstract SearchQueryHandlerFactory getSearchQHFactory();
    public abstract ListLinkHandlerFactory getCommentsLHFactory();


    ////////////////////////////////////////////
    // Extractor
    ////////////////////////////////////////////

    /**
     * Must create a new instance of a SearchExtractor implementation.
     * @param queryHandler specifies the keyword lock for, and the filters which should be applied.
     * @param localization specifies the language/country for the extractor.
     * @return a new SearchExtractor instance
     */
    public abstract SearchExtractor getSearchExtractor(SearchQueryHandler queryHandler, Localization localization);

    /**
     * Must create a new instance of a SuggestionExtractor implementation.
     * @param localization specifies the language/country for the extractor.
     * @return a new SuggestionExtractor instance
     */
    public abstract SuggestionExtractor getSuggestionExtractor(Localization localization);

    /**
     * Outdated or obsolete. null can be returned.
     * @return just null
     */
    public abstract SubscriptionExtractor getSubscriptionExtractor();

    /**
     * Must create a new instance of a KioskList implementation.
     * @return a new KioskList instance
     * @throws ExtractionException
     */
    public abstract KioskList getKioskList() throws ExtractionException;

    /**
     * Must create a new instance of a ChannelExtractor implementation.
     * @param linkHandler is pointing to the channel which should be handled by this new instance.
     * @param localization specifies the language used for the request.
     * @return a new ChannelExtractor
     * @throws ExtractionException
     */
    public abstract ChannelExtractor getChannelExtractor(ListLinkHandler linkHandler, Localization localization) throws ExtractionException;

    /**
     * Must create a new instance of a GenreExtractor implementation.
     * @param linkHandler is pointing to the channel which should be handled by this new instance.
     * @param localization specifies the language used for the request.
     * @return a new GenreExtractor
     * @throws ExtractionException
     */
    public abstract GenreExtractor getGenreExtractor(ListLinkHandler linkHandler, Localization localization) throws ExtractionException;

    /**
     * Must create a new instance of a ArtistExtractor implementation.
     * @param linkHandler is pointing to the artist which should be handled by this new instance.
     * @param localization specifies the language used for the request.
     * @return a new MyArtistExtractor
     * @throws ExtractionException
     */
    public abstract ArtistExtractor getArtistExtractor(ListLinkHandler linkHandler, Localization localization) throws ExtractionException;

    /**
     * Must create a new instance of a SuggestExtractor implementation.
     * @param linkHandler is pointing to the artist which should be handled by this new instance.
     * @param localization specifies the language used for the request.
     * @return a new MyArtistExtractor
     * @throws ExtractionException
     */
    public abstract SuggestExtractor getSuggestExtractor(ListLinkHandler linkHandler, Localization localization) throws ExtractionException;


    /**
     * Must create a new instance of a ListDataExtractor implementation.
     * @param linkHandler is pointing to the artist which should be handled by this new instance.
     * @param localization specifies the language used for the request.
     * @return a new ListDataExtractor
     * @throws ExtractionException
     */
    public abstract ListDataExtractor getListDataExtractor(ListLinkHandler linkHandler, Localization localization) throws ExtractionException;


    /**
     * Must crete a new instance of a PlaylistExtractor implementation.
     * @param linkHandler is pointing to the playlist which should be handled by this new instance.
     * @param localization specifies the language used for the request.
     * @return a new PlaylistExtractor
     * @throws ExtractionException
     */
    public abstract PlaylistExtractor getPlaylistExtractor(ListLinkHandler linkHandler, Localization localization) throws ExtractionException;

    /**
     * Must create a new instance of a StreamExtractor implementation.
     * @param linkHandler is pointing to the streams which should be handled by this new instance.
     * @param localization specifies the language used for the request.
     * @return a new StreamExtractor
     * @throws ExtractionException
     */
    public abstract StreamExtractor getStreamExtractor(LinkHandler linkHandler, Localization localization) throws ExtractionException;
    public abstract CommentsExtractor getCommentsExtractor(ListLinkHandler linkHandler, Localization localization) throws ExtractionException;
    ////////////////////////////////////////////
    // Extractor with default localization
    ////////////////////////////////////////////

    public SearchExtractor getSearchExtractor(SearchQueryHandler queryHandler) {
        return getSearchExtractor(queryHandler, PlayTube.getPreferredLocalization());
    }

    public SuggestionExtractor getSuggestionExtractor() {
        return getSuggestionExtractor(PlayTube.getPreferredLocalization());
    }

    ////////////////////////////////////////////
    // Extractor without link handler
    ////////////////////////////////////////////


    ////////////////////////////////////////////
    // Short extractor without localization
    ////////////////////////////////////////////

    public ChannelExtractor getChannelExtractor(String url) throws ExtractionException {
        return getChannelExtractor(getChannelLHFactory().fromUrl(url), PlayTube.getPreferredLocalization());
    }

    public GenreExtractor getGenreExtractor(String url) throws ExtractionException {
        return getGenreExtractor(getGenreLHFactory().fromUrl(url), PlayTube.getPreferredLocalization());
    }

    public ArtistExtractor getArtistExtractor(String url) throws ExtractionException {
        return getArtistExtractor(getArtistLHFactory().fromUrl(url), PlayTube.getPreferredLocalization());
    }

    public SuggestExtractor getSuggestExtractor(String url) throws ExtractionException {
        return getSuggestExtractor(getSuggestLHFactory().fromUrl(url), PlayTube.getPreferredLocalization());
    }

    public ListDataExtractor getListDataExtractor(String url) throws ExtractionException {
        return getListDataExtractor(getListDataLHFactory().fromUrl(url), PlayTube.getPreferredLocalization());
    }

    public PlaylistExtractor getPlaylistExtractor(String url) throws ExtractionException {
        return getPlaylistExtractor(getPlaylistLHFactory().fromUrl(url), PlayTube.getPreferredLocalization());
    }

    public StreamExtractor getStreamExtractor(String url) throws ExtractionException {
        return getStreamExtractor(getStreamLHFactory().fromUrl(url), PlayTube.getPreferredLocalization());
    }
    
    public CommentsExtractor getCommentsExtractor(String url) throws ExtractionException {
        ListLinkHandlerFactory llhf = getCommentsLHFactory();
        if(null == llhf) {
            return null;
        }
        return getCommentsExtractor(llhf.fromUrl(url), PlayTube.getPreferredLocalization());
    }


    /**
     * Figures out where the link is pointing to (a channel, a video, a playlist, etc.)
     * @param url the url on which it should be decided of which link type it is
     * @return the link type of url
     * @throws ParsingException
     */
    public final LinkType getLinkTypeByUrl(String url) throws ParsingException {
        LinkHandlerFactory sH = getStreamLHFactory();
        LinkHandlerFactory cH = getChannelLHFactory();
        LinkHandlerFactory pH = getPlaylistLHFactory();
        LinkHandlerFactory aH = getArtistLHFactory();
        LinkHandlerFactory rH = getSuggestLHFactory();
        LinkHandlerFactory aG = getGenreLHFactory();
        LinkHandlerFactory listData = getListDataLHFactory();

        if (sH != null && sH.acceptUrl(url)) {
            return LinkType.STREAM;
        } else if (cH != null && cH.acceptUrl(url)) {
            return LinkType.CHANNEL;
        } else if (pH != null && pH.acceptUrl(url)) {
            return LinkType.PLAYLIST;
        } else if (aH != null && aH.acceptUrl(url)) {
            return LinkType.ARTIST;
        } else if (rH != null && rH.acceptUrl(url)) {
            return LinkType.SUGGEST;
        } else if (listData != null && listData.acceptUrl(url)) {
            return LinkType.LISTDATA;
        } else if (aG != null && aG.acceptUrl(url)) {
            return LinkType.GENRE;
        } else {
            return LinkType.NONE;
        }
    }
}
