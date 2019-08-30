package org.video.playtube.extractor;

import org.video.playtube.extractor.exception.ExtractionException;
import org.video.playtube.extractor.util.Localization;

import java.io.IOException;
import java.util.List;


public abstract class SuggestionExtractor {

    private final int serviceId;
    private final Localization localization;

    public SuggestionExtractor(int serviceId, Localization localization) {
        this.serviceId = serviceId;
        this.localization = localization;
    }

    public abstract List<String> suggestionList(String query) throws IOException, ExtractionException;

    public int getServiceId() {
        return serviceId;
    }

    protected Localization getLocalization() {
        return localization;
    }
}
