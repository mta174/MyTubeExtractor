package org.video.playtube.extractor.service.youtube.extractor;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;

import org.video.playtube.extractor.Downloader;
import org.video.playtube.extractor.PlayTube;
import org.video.playtube.extractor.StreamingService;
import org.video.playtube.extractor.exception.ExtractionException;
import org.video.playtube.extractor.exception.ParsingException;
import org.video.playtube.extractor.linkhandler.ListLinkHandler;
import org.video.playtube.extractor.stream.StreamInfoItem;
import org.video.playtube.extractor.stream.StreamInfoItemsCollector;
import org.video.playtube.extractor.stream.StreamType;
import org.video.playtube.extractor.suggest.SuggestExtractor;
import org.video.playtube.extractor.util.ExtractorConstant;
import org.video.playtube.extractor.util.Localization;
import org.video.playtube.extractor.util.LogHelper;
import org.video.playtube.extractor.util.Utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MySuggestExtractor extends SuggestExtractor {
    private static final String TAG = MySuggestExtractor.class.getSimpleName();
    private JsonObject jsonObject = null;
    private String videoIds = "";
    private String pageToken = "";
    public MySuggestExtractor(StreamingService service, ListLinkHandler linkHandler, Localization localization) {
        super(service, linkHandler, localization);
    }

    @Override
    public void onFetchPage(@Nonnull Downloader downloader) throws IOException, ExtractionException {
        try {
            JsonObject object = JsonParser.object().from(downloader.download(getUrl()));
            videoIds = "";
            if(object != null) {
                videoIds = getVideoIds(object);
                pageToken = object.getString(ExtractorConstant.BlockJson.PAGE_TOKEN);
            }
            if(videoIds.length() > 0) {
                jsonObject = JsonParser.object().from(downloader.download(Utils.buildUrlInfoVideo(videoIds, getKey())));
            }
        } catch (JsonParserException jpe) {
            throw new ExtractionException("Could not parse json returnd by url: " + getUrl());
        }
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        return "";
    }

    @Override
    public String getNextPageToken() throws ParsingException {
        return pageToken;
    }

    @Override
    public String getKey() throws ParsingException {
        URL urlObj = null;
        try {
            urlObj = Utils.stringToURL(getUrl());
        } catch (MalformedURLException e) {
            LogHelper.i(TAG, "MalformedURLException", e.getMessage());
        }
        String key = Utils.getQueryValue(urlObj, "key");
        return key;
    }

    public String getVideoIds(JsonObject object) throws ParsingException{
        String videoIds = "";
        Vector<String> data = new Vector<>();
        JsonArray jsonArray = object.getArray(ExtractorConstant.BlockJson.ITEMS);
        if(jsonArray != null) {
            for (int i = 0, len = jsonArray.size(); i < len; i++) {
                data.add(jsonArray.getObject(i).getObject(ExtractorConstant.BlockJson.ID).getString(ExtractorConstant.Video.VIDEO_ID));
            }
        }
        if(data != null && data.size() > 0) {
            videoIds = Utils.buildListIdQuery(data);
        }
        return videoIds;
    }

    @Override
    public String getNextPageUrl() {
        String url = "";
        try {
            url = getUrl();
        } catch (ParsingException e) {
            return "";
        }
        if(!Utils.isNullOrEmpty(pageToken)) {
            url = url.replaceFirst("pageToken=.*?(&|$)", "pageToken="+pageToken+"$1");
            return  url;
        }
        return "";
    }

    @Override
    public InfoItemsPage<StreamInfoItem> getPage(String pageUrl) throws ExtractionException, IOException {
        if (pageUrl == null || pageUrl.isEmpty()) {
            throw new ExtractionException(new IllegalArgumentException("Page url is empty or null"));
        }
        try {
            JsonObject object = JsonParser.object().from(PlayTube.getDownloader().download(pageUrl));
            videoIds = "";
            if(object != null) {
                videoIds = getVideoIds(object);
                pageToken = object.getString(ExtractorConstant.BlockJson.PAGE_TOKEN);
                if (Utils.isNullOrEmpty(videoIds)) {
                    jsonObject = null;
                    pageToken = "";
                }
            }
            if(videoIds.length() > 0) {
                jsonObject = JsonParser.object().from(PlayTube.getDownloader().download(Utils.buildUrlInfoVideo(videoIds, getKey())));
            }
        } catch (JsonParserException jpe) {
            throw new ExtractionException("Could not parse json returnd by url: " + getUrl());
        }
        StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());
        collector.reset();
        collectStreamsFrom(collector, jsonObject);
        return new InfoItemsPage<>(collector, getNextPageUrl());
    }

    private void collectStreamsFrom(@Nonnull StreamInfoItemsCollector collector, @Nullable JsonObject jsonObject) {
        if (jsonObject != null) {
            JsonArray jsonArray = jsonObject.getArray(ExtractorConstant.BlockJson.ITEMS);
            if(jsonArray != null) {
                for (int i = 0, len = jsonArray.size(); i < len; i++) {
                    final JsonObject object = jsonArray.getObject(i);
                    collector.commit(new SuggestStreamInfoItemExtractor(object) {
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
                            String authorId = object.getObject(ExtractorConstant.BlockJson.SNIPPET).getString(ExtractorConstant.Video.CHANNEL_ID);
                            return ExtractorConstant.EXTRACTOR_PRE_CHANNEL + authorId;
                        }

                        @Override
                        public String getUploadDate() throws ParsingException {
                            return "";//object.getObject(ExtractorConstant.BlockJson.SNIPPET).getString(ExtractorConstant.Video.PUBLISHEDAT);
                        }
                    });
                }
            }
        }
    }


    @Nonnull
    @Override
    public InfoItemsPage<StreamInfoItem> getInitialPage() throws ParsingException {
        StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());
        collector.reset();
        collectStreamsFrom(collector, jsonObject);
        return new InfoItemsPage<>(collector, getNextPageUrl());
    }
}
