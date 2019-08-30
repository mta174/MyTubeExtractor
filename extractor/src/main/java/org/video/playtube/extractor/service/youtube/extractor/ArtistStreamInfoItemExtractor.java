package org.video.playtube.extractor.service.youtube.extractor;

import com.grack.nanojson.JsonObject;
import org.video.playtube.extractor.exception.ParsingException;
import org.video.playtube.extractor.stream.StreamInfoItemExtractor;
import org.video.playtube.extractor.stream.StreamType;
import org.video.playtube.extractor.util.CryptHelper;
import org.video.playtube.extractor.util.ExtractorConstant;
import org.video.playtube.extractor.util.LogHelper;
import org.video.playtube.extractor.util.Utils;

public class ArtistStreamInfoItemExtractor implements StreamInfoItemExtractor {
    protected static final String TAG = LogHelper.makeLogTag(ArtistStreamInfoItemExtractor.class.getSimpleName());
    private final JsonObject object;
    private final CryptHelper objScrypt;

    public ArtistStreamInfoItemExtractor(JsonObject object) {
        this.object = object;
        this.objScrypt = new CryptHelper();
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
        return Utils.parseInt(object.getString(ExtractorConstant.SongFieldName.DURATION));
    }

    @Override
    public long getViewCount() throws ParsingException {
        return Utils.parseLong(object.getString(ExtractorConstant.SongFieldName.TOTAL_VIEW));
    }

    @Override
    public String getUploaderName() throws ParsingException {
        return  object.getString(ExtractorConstant.SongFieldName.AUTHOR_NAME);
    }

    @Override
    public String getUploaderUrl() throws ParsingException {
        String authorId = object.getString(ExtractorConstant.SongFieldName.AUTHOR_ID);
        return  ExtractorConstant.EXTRACTOR_PRE_CHANNEL + authorId;
    }

    @Override
    public String getUploadDate() throws ParsingException {
        return "";
    }

    @Override
    public String getName() throws ParsingException {
        return object.getString(ExtractorConstant.SongFieldName.TITLE);
    }

    @Override
    public String getUrl() throws ParsingException {
        String id = new String(objScrypt.decrypt(object.getString(ExtractorConstant.SongFieldName.ID)));
        return ExtractorConstant.EXTRACTOR_PRE_LINK + id;
    }

    @Override
    public String getThumbnailUrl() throws ParsingException {
        String id = new String(objScrypt.decrypt(object.getString(ExtractorConstant.SongFieldName.ID)));
        return String.format(ExtractorConstant.EXTRACTOR_PRE_THUMBNAIL, id);
    }
}
