package com.mta.playtube.extractor.services.soundcloud.listdata;

import com.mta.playtube.extractor.exceptions.ParsingException;
import com.mta.playtube.extractor.linkhandler.ListLinkHandlerFactory;
import com.mta.playtube.extractor.utils.LogHelper;
import com.mta.playtube.extractor.utils.Utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class SoundcloudListDataLinkHandlerFactory extends ListLinkHandlerFactory {
    private static final String TAG = LogHelper.makeLogTag(SoundcloudListDataLinkHandlerFactory.class.getSimpleName());
    private static final SoundcloudListDataLinkHandlerFactory instance = new SoundcloudListDataLinkHandlerFactory();
    public static SoundcloudListDataLinkHandlerFactory getInstance() {
        return instance;
    }


    @Override
    public String getId(String url) throws ParsingException {
        String id = "";
        try {
            URL urlObj = Utils.stringToURL(url);
            String path = urlObj.getPath();
            String query = urlObj.getQuery();
            if (!path.startsWith("/charts")) {
                throw new ParsingException("the URL not match");
            }
            id = urlObj.getQuery();
        } catch (MalformedURLException e) {
            LogHelper.i(TAG, "MalformedURLException", e.getMessage());
        }
        return id;
    }

    @Override
    public String getUrl(String id, List<String> contentFilter, String sortFilter) throws ParsingException {
        return  "https://api-v2.soundcloud.com/charts?" + id;
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
        return host.equalsIgnoreCase("api-v2.soundcloud.com") || host.equalsIgnoreCase("www.api-v2.soundcloud.com");
    }

}
