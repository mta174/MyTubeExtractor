package org.video.playtube.extractor.artist;

import org.video.playtube.extractor.InfoItemExtractor;
import org.video.playtube.extractor.exception.ParsingException;

public interface ArtistInfoItemExtractor extends InfoItemExtractor {

    String getArtistId() throws ParsingException;
    String getArtistName() throws ParsingException;
    //int getTotal() throws ParsingException;
}
