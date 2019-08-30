package org.video.playtube.extractor.genre;

import org.video.playtube.extractor.InfoItemExtractor;
import org.video.playtube.extractor.exception.ParsingException;

public interface GenreInfoItemExtractor extends InfoItemExtractor {

    String getGenreUrl() throws ParsingException;
    String getGenreName() throws ParsingException;
    String getThumbnail() throws ParsingException;
}
