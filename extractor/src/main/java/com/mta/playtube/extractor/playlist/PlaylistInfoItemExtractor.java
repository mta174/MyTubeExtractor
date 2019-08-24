package com.mta.playtube.extractor.playlist;

import com.mta.playtube.extractor.InfoItemExtractor;
import com.mta.playtube.extractor.exceptions.ParsingException;

public interface PlaylistInfoItemExtractor extends InfoItemExtractor {

    /**
     * Get the uploader name
     * @return the uploader name
     * @throws ParsingException
     */
    String getUploaderName() throws ParsingException;

    /**
     * Get the number of streams
     * @return the number of streams
     * @throws ParsingException
     */
    long getStreamCount() throws ParsingException;
}
