package com.mta.playtube.extractor.services.media_ccc.extractors;

import com.mta.playtube.extractor.SuggestionExtractor;
import com.mta.playtube.extractor.exceptions.ExtractionException;
import com.mta.playtube.extractor.utils.Localization;

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
