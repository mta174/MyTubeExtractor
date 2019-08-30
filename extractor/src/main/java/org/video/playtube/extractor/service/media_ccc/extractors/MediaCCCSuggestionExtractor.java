package org.video.playtube.extractor.service.media_ccc.extractors;

import org.video.playtube.extractor.SuggestionExtractor;
import org.video.playtube.extractor.exception.ExtractionException;
import org.video.playtube.extractor.util.Localization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MediaCCCSuggestionExtractor extends SuggestionExtractor {

    public MediaCCCSuggestionExtractor(int serviceId, Localization localization) {
        super(serviceId, localization);
    }

    @Override
    public List<String> suggestionList(String query) throws IOException, ExtractionException {
        return new ArrayList<>(0);
    }
}
