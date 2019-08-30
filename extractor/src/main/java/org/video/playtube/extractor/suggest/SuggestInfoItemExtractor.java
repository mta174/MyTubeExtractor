package org.video.playtube.extractor.suggest;

import org.video.playtube.extractor.InfoItemExtractor;
import org.video.playtube.extractor.exception.ParsingException;

public interface SuggestInfoItemExtractor extends InfoItemExtractor {

    String getNextPageToken() throws ParsingException;
    String getKey() throws ParsingException;
}
