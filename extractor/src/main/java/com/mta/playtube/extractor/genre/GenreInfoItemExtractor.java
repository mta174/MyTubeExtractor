package com.mta.playtube.extractor.genre;

import com.mta.playtube.extractor.InfoItemExtractor;
import com.mta.playtube.extractor.exceptions.ParsingException;

public interface GenreInfoItemExtractor extends InfoItemExtractor {

    String getGenreUrl() throws ParsingException;
    String getGenreName() throws ParsingException;
    String getThumbnail() throws ParsingException;
}
