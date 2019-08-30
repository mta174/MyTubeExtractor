package org.video.playtube.extractor.service.soundcloud;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import org.video.playtube.extractor.PlayTube;
import org.video.playtube.extractor.SuggestionExtractor;
import org.video.playtube.extractor.exception.ExtractionException;
import org.video.playtube.extractor.exception.ParsingException;
import org.video.playtube.extractor.util.ExtractorConstant;
import org.video.playtube.extractor.util.Localization;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SoundcloudSuggestionExtractor extends SuggestionExtractor {

    public SoundcloudSuggestionExtractor(int serviceId, Localization localization) {
        super(serviceId, localization);
    }

    @Override
    public List<String> suggestionList(String query) throws IOException, ExtractionException {
        List<String> suggestions = new ArrayList<>();
        StringBuffer url = new StringBuffer();
        url.append("https://api-v2.soundcloud.com/search/queries")
                .append("?q=").append(URLEncoder.encode(query, ExtractorConstant.CHARSET_UTF_8))
                .append("&client_id=").append(SoundcloudParsingHelper.clientId()).append("&limit=10");
        String response = PlayTube.getDownloader().download(url.toString());
        try {
            JsonArray collection = JsonParser.object().from(response).getArray("collection");
            for (Object suggestion : collection) {
                if (suggestion instanceof JsonObject) suggestions.add(((JsonObject) suggestion).getString("query"));
            }

            return suggestions;
        } catch (JsonParserException e) {
            throw new ParsingException("Could not parse json response", e);
        }
    }
}
