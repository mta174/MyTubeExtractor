package org.video.playtube.extractor.exception;

public class ReCaptchaException extends ExtractionException {
    private String url;

    public ReCaptchaException(String message, String url) {
        super(message);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
