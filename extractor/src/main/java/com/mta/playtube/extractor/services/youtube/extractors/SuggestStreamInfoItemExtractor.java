package com.mta.playtube.extractor.services.youtube.extractors;

import com.grack.nanojson.JsonObject;
import com.mta.playtube.extractor.exceptions.ParsingException;
import com.mta.playtube.extractor.stream.StreamInfoItemExtractor;
import com.mta.playtube.extractor.stream.StreamType;
import com.mta.playtube.extractor.utils.ExtractorConstant;
import com.mta.playtube.extractor.utils.LogHelper;
import com.mta.playtube.extractor.utils.Utils;

public class SuggestStreamInfoItemExtractor implements StreamInfoItemExtractor {
    protected static final String TAG = LogHelper.makeLogTag(SuggestStreamInfoItemExtractor.class.getSimpleName());
    private final JsonObject object;
    public SuggestStreamInfoItemExtractor(JsonObject object) {
        this.object = object;
    }
    @Override
    public String getName() throws ParsingException {
        return object.getObject(ExtractorConstant.BlockJson.SNIPPET).getString(ExtractorConstant.Video.TITLE);
    }

    @Override
    public String getUrl() throws ParsingException {
        String id = object.getString(ExtractorConstant.Video.ID);
        return ExtractorConstant.EXTRACTOR_PRE_LINK + id;
    }

    @Override
    public String getThumbnailUrl() throws ParsingException {
        return object.getObject(ExtractorConstant.BlockJson.SNIPPET).getObject(ExtractorConstant.Video.THUMBNAILS).getObject("default").getString("url");
    }

    @Override
    public StreamType getStreamType() throws ParsingException {
        return StreamType.VIDEO_STREAM;
    }

    @Override
    public boolean isAd() throws ParsingException {
        return false;
    }

    @Override
    public long getDuration() throws ParsingException {
        return Utils.convertToSeconds(object.getObject(ExtractorConstant.BlockJson.CONTENT_DETAILS).getString(ExtractorConstant.Video.DURATION));
    }

    @Override
    public long getViewCount() throws ParsingException {
        return Utils.parseLong(object.getObject(ExtractorConstant.BlockJson.STATISTICS).getString(ExtractorConstant.Video.VIEW_COUNT));
    }

    @Override
    public String getUploaderName() throws ParsingException {
        return object.getObject(ExtractorConstant.BlockJson.SNIPPET).getString(ExtractorConstant.Video.CHANNEL_TITLE);
    }

    @Override
    public String getUploaderUrl() throws ParsingException {
        String authorId =  object.getObject(ExtractorConstant.BlockJson.SNIPPET).getString(ExtractorConstant.Video.CHANNEL_ID);
        return  ExtractorConstant.EXTRACTOR_PRE_CHANNEL + authorId;
    }

    @Override
    public String getUploadDate() throws ParsingException {
        return object.getObject(ExtractorConstant.BlockJson.SNIPPET).getString(ExtractorConstant.Video.PUBLISHEDAT);
    }
}
