package org.video.playtube.extractor.exception;

public class ContentNotAvailableException extends ParsingException {
    public ContentNotAvailableException(String message) {
        super(message);
    }

    public ContentNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
