package com.mta.playtube.extractor.artist;

import com.mta.playtube.extractor.InfoItemExtractor;
import com.mta.playtube.extractor.exceptions.ParsingException;

public interface ArtistInfoItemExtractor extends InfoItemExtractor {

    String getArtistId() throws ParsingException;
    String getArtistName() throws ParsingException;
    //int getTotal() throws ParsingException;
}
