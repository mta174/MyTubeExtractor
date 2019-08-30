package org.video.playtube.extractor.search;

import org.video.playtube.extractor.InfoItem;
import org.video.playtube.extractor.InfoItemExtractor;
import org.video.playtube.extractor.InfoItemsCollector;
import org.video.playtube.extractor.channel.ChannelInfoItemExtractor;
import org.video.playtube.extractor.channel.ChannelInfoItemsCollector;
import org.video.playtube.extractor.exception.ParsingException;
import org.video.playtube.extractor.playlist.PlaylistInfoItemExtractor;
import org.video.playtube.extractor.playlist.PlaylistInfoItemsCollector;
import org.video.playtube.extractor.stream.StreamInfoItemExtractor;
import org.video.playtube.extractor.stream.StreamInfoItemsCollector;

/**
 * Collector for search results
 *
 * This collector can handle the following extractor types:
 * <ul>
 *     <li>{@link StreamInfoItemExtractor}</li>
 *     <li>{@link ChannelInfoItemExtractor}</li>
 *     <li>{@link PlaylistInfoItemExtractor}</li>
 * </ul>
 * Calling {@link #extract(InfoItemExtractor)} or {@link #commit(Object)} with any
 * other extractor type will raise an exception.
 */
public class InfoItemsSearchCollector extends InfoItemsCollector<InfoItem, InfoItemExtractor> {
    private final StreamInfoItemsCollector streamCollector;
    private final ChannelInfoItemsCollector userCollector;
    private final PlaylistInfoItemsCollector playlistCollector;

    InfoItemsSearchCollector(int serviceId) {
        super(serviceId);
        streamCollector = new StreamInfoItemsCollector(serviceId);
        userCollector = new ChannelInfoItemsCollector(serviceId);
        playlistCollector = new PlaylistInfoItemsCollector(serviceId);
    }

    @Override
    public InfoItem extract(InfoItemExtractor extractor) throws ParsingException {
        // Use the corresponding collector for each item extractor type
        if(extractor instanceof StreamInfoItemExtractor) {
            return streamCollector.extract((StreamInfoItemExtractor) extractor);
        } else if(extractor instanceof ChannelInfoItemExtractor) {
            return userCollector.extract((ChannelInfoItemExtractor) extractor);
        } else if(extractor instanceof PlaylistInfoItemExtractor) {
            return playlistCollector.extract((PlaylistInfoItemExtractor) extractor);
        } else {
            throw new IllegalArgumentException("Invalid extractor type: " + extractor);
        }
    }
}
