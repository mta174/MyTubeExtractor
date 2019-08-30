package org.video.playtube.extractor.exception;

public class ParsingException extends ExtractionException {
    public ParsingException(String message) {
        super(message);
    }

    public ParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}