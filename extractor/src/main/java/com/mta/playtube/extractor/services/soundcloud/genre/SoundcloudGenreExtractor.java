package com.mta.playtube.extractor.services.soundcloud.genre;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;

import com.mta.playtube.extractor.Downloader;
import com.mta.playtube.extractor.StreamingService;
import com.mta.playtube.extractor.exceptions.ExtractionException;
import com.mta.playtube.extractor.exceptions.ParsingException;
import com.mta.playtube.extractor.genre.GenreExtractor;
import com.mta.playtube.extractor.genre.GenreInfoItem;
import com.mta.playtube.extractor.genre.GenreInfoItemsCollector;
import com.mta.playtube.extractor.linkhandler.ListLinkHandler;
import com.mta.playtube.extractor.utils.Localization;
import com.mta.playtube.extractor.utils.LogHelper;

import java.io.IOException;

import javax.annotation.Nonnull;

public class SoundcloudGenreExtractor extends GenreExtractor {
    private static final String TAG = LogHelper.makeLogTag(SoundcloudGenreExtractor.class.getSimpleName());
    private JsonObject jsonObject = null;

    public SoundcloudGenreExtractor(StreamingService service, ListLinkHandler linkHandler, Localization localization) {
        super(service, linkHandler, localization);
    }

    @Nonnull
    @Override
    public InfoItemsPage<GenreInfoItem> getInitialPage() throws IOException, ExtractionException {
        GenreInfoItemsCollector collector = new GenreInfoItemsCollector(getServiceId());
        collector.reset();
        if(jsonObject != null) {
            JsonArray jsonArray = jsonObject.getArray("Data");
            int version = jsonObject.getInt("Version");
            if(jsonArray != null) {
                for (int i = 0, len = jsonArray.size(); i < len; i++) {
                    final JsonObject object = jsonArray.getObject(i);
                    collector.commit(new GenreItemExtractor(object, version));
                }
            }
        }
        return new InfoItemsPage<>(collector, getNextPageUrl());
    }

    @Override
    public String getNextPageUrl() throws IOException, ExtractionException {
        return null;
    }

    @Override
    public InfoItemsPage<GenreInfoItem> getPage(String pageUrl) throws IOException, ExtractionException {
        return null;
    }

    @Override
    public void onFetchPage(@Nonnull Downloader downloader) throws IOException, ExtractionException {
        try {
            jsonObject = JsonParser.object().from(downloader.downloadCustomize(getUrl()));
        } catch (JsonParserException jpe) {
            throw new ExtractionException("Could not parse json returnd by url: " + getUrl());
        }
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        return jsonObject.getString("Name");
    }

    @Override
    public String getVersion() throws ParsingException {
        return String.valueOf(jsonObject.getInt("Version"));
    }
}