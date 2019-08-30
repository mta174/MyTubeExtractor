package org.video.playtube.extractor.service.soundcloud.listdata;

import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;

import org.video.playtube.extractor.Downloader;
import org.video.playtube.extractor.StreamingService;
import org.video.playtube.extractor.exception.ExtractionException;
import org.video.playtube.extractor.exception.ParsingException;
import org.video.playtube.extractor.exception.ReCaptchaException;
import org.video.playtube.extractor.linkhandler.ListLinkHandler;
import org.video.playtube.extractor.listdata.ListDataExtractor;
import org.video.playtube.extractor.service.soundcloud.SoundcloudParsingHelper;
import org.video.playtube.extractor.stream.StreamInfoItem;
import org.video.playtube.extractor.stream.StreamInfoItemsCollector;
import org.video.playtube.extractor.util.Localization;
import org.video.playtube.extractor.util.LogHelper;
import org.video.playtube.extractor.util.Utils;

import java.io.IOException;

import javax.annotation.Nonnull;

public class SoundcloudListDataExtractor extends ListDataExtractor {
    private static final String TAG = LogHelper.makeLogTag(SoundcloudListDataExtractor.class.getSimpleName());
    private StreamInfoItemsCollector collector = null;
    private String nextPageUrl = null;
    private String genre = "";

    public SoundcloudListDataExtractor(StreamingService service, ListLinkHandler linkHandler, Localization localization) {
        super(service, linkHandler, localization);
    }

    @Override
    public void onFetchPage(@Nonnull Downloader downloader) {
       try {
            String apiUrl = getOriginalUrl();
            String response = downloader.download(apiUrl);
            JsonObject responseObject = JsonParser.object().from(response);
            genre = responseObject.getString("genre");
        } catch (JsonParserException e) {
        } catch (ParsingException e) {
        } catch (IOException e) {
        } catch (ReCaptchaException e) {
        }
    }

    @Nonnull
    @Override
    public String getName() {
        return !Utils.isNullOrEmpty(genre) ? Utils.getTitleByKey(genre)  : "";
    }

    @Override
    public InfoItemsPage<StreamInfoItem> getPage(String pageUrl) throws IOException, ExtractionException {
        if (pageUrl == null || pageUrl.isEmpty()) {
            throw new ExtractionException(new IllegalArgumentException("Page url is empty or null"));
        }

        StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());
        String nextPageUrl = SoundcloudParsingHelper.getStreamsFromApi(collector, pageUrl, true);

        return new InfoItemsPage<>(collector, nextPageUrl);
    }


    private void computNextPageAndStreams() throws IOException, ExtractionException {
        collector = new StreamInfoItemsCollector(getServiceId());
        String apiUrl = getOriginalUrl();

        /*try {
            String response = PlayTube.getDownloader().download(apiUrl);
            JsonObject responseObject = JsonParser.object().from(response);
            genre = responseObject.getString("genre");
        } catch (JsonParserException e) {
        }
        LogHelper.i(TAG, "computNextPageAndStreams", genre);*/

        nextPageUrl = SoundcloudParsingHelper.getStreamsFromApi(collector, apiUrl, true);
    }

    @Override
    public String getNextPageUrl() throws IOException, ExtractionException {
        if(nextPageUrl == null) {
            computNextPageAndStreams();
        }
        return nextPageUrl;
    }

    @Nonnull
    @Override
    public InfoItemsPage<StreamInfoItem> getInitialPage() throws IOException, ExtractionException {
        if(collector == null) {
            computNextPageAndStreams();
        }
        return new InfoItemsPage<>(collector, getNextPageUrl());
    }


}
