package com.mta.playtube.extractor.services.soundcloud.kiosk;

import com.mta.playtube.extractor.linkhandler.ListLinkHandlerFactory;
import com.mta.playtube.extractor.utils.ExtractorConstant;
import com.mta.playtube.extractor.utils.Parser;
import java.util.List;

public class SoundcloudChartsLinkHandlerFactory extends ListLinkHandlerFactory {
    private final String TOP_URL_PATTERN = "^https?://(www\\.|m\\.)?soundcloud.com/charts(/top)?/?([#?].*)?$";
    private final String URL_PATTERN = "^https?://(www\\.|m\\.)?soundcloud.com/charts(/top|/new)?/?([#?].*)?$";


    @Override
    public String getId(String url) {
        if (Parser.isMatch(TOP_URL_PATTERN, url.toLowerCase())) {
            return ExtractorConstant.KIOS_TOP50;
        } else {
            return ExtractorConstant.KIOS_NEW_HOT;
        }
    }

    @Override
    public String getUrl(String id, List<String> contentFilter, String sortFilter) {
        if (id.equals(ExtractorConstant.KIOS_TOP50)) {
            return "https://soundcloud.com/charts/top";
        } else {
            return "https://soundcloud.com/charts/new";
        }
    }

    @Override
    public boolean onAcceptUrl(final String url) {
        return Parser.isMatch(URL_PATTERN, url.toLowerCase());
    }
}
