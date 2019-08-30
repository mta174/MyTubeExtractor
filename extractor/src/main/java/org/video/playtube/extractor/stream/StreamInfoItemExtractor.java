package org.video.playtube.extractor.stream;

import org.video.playtube.extractor.InfoItemExtractor;
import org.video.playtube.extractor.exception.ParsingException;

public interface StreamInfoItemExtractor extends InfoItemExtractor {


    /**
     * Get the streams type
     * @return the streams type
     * @throws ParsingException thrown if there is an error in the extraction
     */
    StreamType getStreamType() throws ParsingException;

    /**
     * Check if the streams is an ad.
     * @return {@code true} if the streams is an ad.
     * @throws ParsingException thrown if there is an error in the extraction
     */
    boolean isAd() throws ParsingException;

    /**
     * Get the streams duration in seconds
     * @return the streams duration in seconds
     * @throws ParsingException thrown if there is an error in the extraction
     */
    long getDuration() throws ParsingException;

    /**
     * Parses the number of view
     * @return the number of view or -1 for live streams
     * @throws ParsingException thrown if there is an error in the extraction
     */
    long getViewCount() throws ParsingException;

    /**
     * Get the uploader name
     * @return the uploader name
     * @throws ParsingException if parsing fails
     */
    String getUploaderName() throws ParsingException;

    String getUploaderUrl() throws ParsingException;

    /**
     * Extract the uploader name
     * @return the uploader name
     * @throws ParsingException thrown if there is an error in the extraction
     */
    String getUploadDate() throws ParsingException;

}
