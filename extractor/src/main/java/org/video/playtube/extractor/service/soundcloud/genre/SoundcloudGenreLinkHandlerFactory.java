package org.video.playtube.extractor.service.soundcloud.genre;

import org.video.playtube.extractor.exception.ParsingException;
import org.video.playtube.extractor.linkhandler.ListLinkHandlerFactory;
import org.video.playtube.extractor.util.ExtractorConstant;
import org.video.playtube.extractor.util.LogHelper;
import org.video.playtube.extractor.util.Utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class SoundcloudGenreLinkHandlerFactory extends ListLinkHandlerFactory {
    private static final String TAG = LogHelper.makeLogTag(SoundcloudGenreLinkHandlerFactory.class.getSimpleName());
    private static final SoundcloudGenreLinkHandlerFactory instance = new SoundcloudGenreLinkHandlerFactory();
    public static SoundcloudGenreLinkHandlerFactory getInstance() {
        return instance;
    }


    @Override
    public String getId(String url) throws ParsingException {
        String id = "";
        try {
            URL urlObj = Utils.stringToURL(url);
            String path = urlObj.getPath();
            if (!path.startsWith("/data_apps/") && !path.startsWith("/playtube/")) {
                throw new ParsingException("the URL not match");
            }
            id = path.substring(20);
        } catch (MalformedURLException e) {
            LogHelper.i(TAG, "MalformedURLException", e.getMessage());
        }
        return id;
    }

    @Override
    public String getUrl(String id, List<String> contentFilter, String sortFilter) throws ParsingException {
        return Utils.getHostMyData() + "/" + id;
    }

    @Override
    public boolean onAcceptUrl(final String url) {
        URL urlObj;
        try {
            urlObj = Utils.stringToURL(url);
        } catch (MalformedURLException e) {
            return false;
        }
        String host = urlObj.getHost();
        return host.equalsIgnoreCase(ExtractorConstant.MY_HOST_NO_WWW) || host.equalsIgnoreCase(ExtractorConstant.MY_HOST_WITH_WWW);
    }
}
