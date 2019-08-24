package com.mta.playtube.extractor.services.youtube.extractors;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;

import org.jsoup.nodes.Element;
import com.mta.playtube.extractor.Downloader;
import com.mta.playtube.extractor.artist.ArtistExtractor;
import com.mta.playtube.extractor.linkhandler.ListLinkHandler;
import com.mta.playtube.extractor.StreamingService;
import com.mta.playtube.extractor.exceptions.ExtractionException;
import com.mta.playtube.extractor.exceptions.ParsingException;
import com.mta.playtube.extractor.stream.StreamInfoItem;
import com.mta.playtube.extractor.stream.StreamInfoItemsCollector;
import com.mta.playtube.extractor.utils.CryptHelper;
import com.mta.playtube.extractor.utils.ExtractorConstant;
import com.mta.playtube.extractor.utils.Localization;

import javax.annotation.Nonnull;

import java.io.IOException;

public class MyArtistExtractor extends ArtistExtractor{
    private static final String TAG = MyArtistExtractor.class.getSimpleName();
    private JsonObject jsonObject = null;

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        return  getArtistName();
    }

    @Override
    public String getThumbnail() throws ParsingException {
        return jsonObject.getString(ExtractorConstant.ConstantJson.THUMBNAIL);
    }

    @Override
    public String getArtistId() throws ParsingException {
        return String.valueOf(jsonObject.getInt(ExtractorConstant.ConstantJson.ID));
    }

    @Override
    public String getArtistName() throws ParsingException {
        return jsonObject.getString(ExtractorConstant.ConstantJson.NAME);
    }

    @Override
    public String getVersion() throws ParsingException {
        return String.valueOf(jsonObject.getInt(ExtractorConstant.ConstantJson.VERSION));
    }

    /*@Override
    public int getTotal() throws ParsingException {
        return 0;
    }*/

    public MyArtistExtractor(StreamingService service, ListLinkHandler linkHandler, Localization localization) {
        super(service, linkHandler, localization);
    }

    @Override
    public void onFetchPage(@Nonnull Downloader downloader) throws IOException, ExtractionException {
        try {
            jsonObject = JsonParser.object().from(downloader.downloadCustomize(getUrl()));
        } catch (JsonParserException jpe) {
            throw new ExtractionException("Could not parse json returnd by url: " + getUrl());
        }
    }

    @Override
    public String getNextPageUrl() {
        return "";
    }

    @Override
    public InfoItemsPage<StreamInfoItem> getPage(String pageUrl) {
        return null;
    }


    @Nonnull
    @Override
    public InfoItemsPage<StreamInfoItem> getInitialPage() throws ParsingException {
        StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());
        collector.reset();
        if(jsonObject != null) {
            CryptHelper objScrypt = new CryptHelper();
            JsonArray jsonArray = jsonObject.getArray(ExtractorConstant.ConstantJson.DATA);
            if(jsonArray != null) {
                for (int i = 0, len = jsonArray.size(); i < len; i++) {
                    final JsonObject object = jsonArray.getObject(i);
                    collector.commit(new ArtistStreamInfoItemExtractor(object) {
                        @Override
                        public String getUrl() throws ParsingException {
                            String id = new String(objScrypt.decrypt(object.getString(ExtractorConstant.SongFieldName.ID)));
                            return ExtractorConstant.EXTRACTOR_PRE_LINK + id;
                        }

                        @Override
                        public String getName() throws ParsingException {
                            return object.getString(ExtractorConstant.SongFieldName.TITLE);
                        }

                        @Override
                        public String getUploaderUrl() throws ParsingException {
                            String authorId = object.getString(ExtractorConstant.SongFieldName.AUTHOR_ID);
                            return ExtractorConstant.EXTRACTOR_PRE_CHANNEL + authorId;
                        }

                        private Element getUploaderLink() {
                            return null;
                        }

                        @Override
                        public String getUploaderName() throws ParsingException {
                            return object.getString(ExtractorConstant.SongFieldName.AUTHOR_NAME);
                        }

                        @Override
                        public String getThumbnailUrl() throws ParsingException {
                            String id = new String(objScrypt.decrypt(object.getString(ExtractorConstant.SongFieldName.ID)));
                            return String.format(ExtractorConstant.EXTRACTOR_PRE_THUMBNAIL, id);
                        }
                    });
                }
            }
        }
        return new InfoItemsPage<>(collector, getNextPageUrl());
    }

}
