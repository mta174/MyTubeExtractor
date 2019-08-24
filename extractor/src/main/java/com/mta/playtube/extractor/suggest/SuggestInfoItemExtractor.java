package com.mta.playtube.extractor.suggest;

import com.mta.playtube.extractor.InfoItemExtractor;
import com.mta.playtube.extractor.exceptions.ParsingException;

public interface SuggestInfoItemExtractor extends InfoItemExtractor {

    String getNextPageToken() throws ParsingException;
    String getKey() throws ParsingException;
}
