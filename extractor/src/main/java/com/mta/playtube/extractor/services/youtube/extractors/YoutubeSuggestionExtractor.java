package com.mta.playtube.extractor.services.youtube.extractors;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import com.mta.playtube.extractor.Downloader;
import com.mta.playtube.extractor.NewPipe;
import com.mta.playtube.extractor.SuggestionExtractor;
import com.mta.playtube.extractor.exceptions.ExtractionException;
import com.mta.playtube.extractor.utils.ExtractorConstant;
import com.mta.playtube.extractor.utils.Localization;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/*
 * Created by Christian Schabesberger on 28.09.16.
 *
 * Copyright (C) Christian Schabesberger 2015 <chris.schabesberger@mailbox.org>
 * YoutubeSuggestionExtractor.java is part of NewPipe.
 *
 * NewPipe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NewPipe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NewPipe.  If not, see <http://www.gnu.org/licenses/>.
 */

public class YoutubeSuggestionExtractor extends SuggestionExtractor {


    public YoutubeSuggestionExtractor(int serviceId, Localization localization) {
        super(serviceId, localization);
    }

    @Override
    public List<String> suggestionList(String query) throws IOException, ExtractionException {
        Downloader dl = NewPipe.getDownloader();
        List<String> suggestions = new ArrayList<>();

        StringBuffer buffer = new StringBuffer();
        buffer.append("https://suggestqueries.google.com/complete/search")
                .append("?client=youtube")//"firefox" for JSON, 'toolbar' for xml
                .append("&jsonp=JP")
                .append("&ds=yt")
                .append("&hl=").append(URLEncoder.encode(getLocalization().getCountry(), ExtractorConstant.CHARSET_UTF_8))
                .append("&q=").append(URLEncoder.encode(query, ExtractorConstant.CHARSET_UTF_8));

        String url = buffer.toString();
        String response = dl.download(url);
        // trim JSONP part "JP(...)"
        response = response.substring(3, response.length()-1);
        try {
            JsonArray collection = JsonParser.array().from(response).getArray(1, new JsonArray());
            for (Object suggestion : collection) {
                if (!(suggestion instanceof JsonArray)) continue;
                String suggestionStr = ((JsonArray)suggestion).getString(0);
                if (suggestionStr == null) continue;
                suggestions.add(suggestionStr);
            }

            return suggestions;
        } catch (JsonParserException e) {
            return  suggestions;
            //throw new ParsingException("Could not parse json response", e);
        }
    }
}
