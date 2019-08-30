package org.video.playtube.extractor.service.soundcloud.genre;

import com.grack.nanojson.JsonObject;

import org.video.playtube.extractor.exception.ParsingException;
import org.video.playtube.extractor.genre.GenreInfoItemExtractor;

public class GenreItemExtractor implements GenreInfoItemExtractor {
    protected static final String TAG = GenreItemExtractor.class.getSimpleName();
    private final JsonObject object;
    private final int version;

    public GenreItemExtractor(JsonObject object, int version) {
        this.object = object;
        this.version = version;
    }

    @Override
    public String getName() throws ParsingException {
        return getGenreName();
    }

    @Override
    public String getUrl() throws ParsingException {
        return null;
    }

    @Override
    public String getThumbnailUrl() throws ParsingException {
        return getThumbnail();
    }

    @Override
    public String getGenreUrl() throws ParsingException {
        return object.getString("Url");
    }

    @Override
    public String getGenreName() throws ParsingException {
        return object.getString("Name");
    }

    @Override
    public String getThumbnail() throws ParsingException {
        return object.getString("ThumbNail");
    }
}