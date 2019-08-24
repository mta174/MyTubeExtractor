package com.mta.playtube.extractor.services.youtube.linkHandler;

import com.mta.playtube.extractor.exceptions.FoundAdException;
import com.mta.playtube.extractor.exceptions.ParsingException;
import com.mta.playtube.extractor.linkhandler.LinkHandlerFactory;
import com.mta.playtube.extractor.utils.ExtractorConstant;
import com.mta.playtube.extractor.utils.LogHelper;
import com.mta.playtube.extractor.utils.Utils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/*
 * Created by Christian Schabesberger on 02.02.16.
 *
 * Copyright (C) Christian Schabesberger 2018 <chris.schabesberger@mailbox.org>
 * YoutubeStreamLinkHandlerFactory.java is part of NewPipe.
 *
 * NewPipe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NewPipe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NewPipe.  If not, see <http://www.gnu.org/licenses/>.
 */

public class YoutubeStreamLinkHandlerFactory extends LinkHandlerFactory {

    private static final YoutubeStreamLinkHandlerFactory instance = new YoutubeStreamLinkHandlerFactory();

    private YoutubeStreamLinkHandlerFactory() {
    }

    public static YoutubeStreamLinkHandlerFactory getInstance() {
        return instance;
    }

    private  String key;

    private static String assertIsID(String id) throws ParsingException {
        if (id == null || !id.matches("[a-zA-Z0-9_-]{11}")) {
            throw new ParsingException("The given string is not a Youtube-Video-ID");
        }
        //LogHelper.i("assertIsID", id);
        return id;
    }

    @Override
    public String getUrl(String id) {
        if(Utils.isNullOrEmpty(key)) {
            return "https://www.youtube.com/watch?v=" + id;
        }
        else {
            return ExtractorConstant.YOUTUBE_URL + id;
        }
    }

    @Override
    public String getId(String urlString) throws ParsingException, IllegalArgumentException {
        String id = "";
        URL urlObj;
        try {
            urlObj = Utils.stringToURL(urlString);
            String host = urlObj.getHost();
            String path = urlObj.getPath();
            String query = urlObj.getQuery();
            key = Utils.getQueryValue(urlObj, "key");
            if(Utils.isNullOrEmpty(key)) {
                try {
                    URI uri = new URI(urlString);
                    String scheme = uri.getScheme();
                    if (scheme != null && (scheme.equals("vnd.youtube") || scheme.equals("vnd.youtube.launch"))) {
                        String schemeSpecificPart = uri.getSchemeSpecificPart();
                        if (schemeSpecificPart.startsWith("//")) {
                            urlString = "https:" + schemeSpecificPart;
                        } else {
                            return assertIsID(schemeSpecificPart);
                        }
                    }
                } catch (URISyntaxException ignored) {
                    LogHelper.i("YoutubeStreamLink", "URISyntaxException", ignored.getMessage());
                }
                // remove leading "/" of URL-path if URL-path is given
                if (!path.isEmpty()) {
                    path = path.substring(1);
                }

                if (!Utils.isHTTP(urlObj) || !(YoutubeParsingHelper.isYoutubeURL(urlObj) ||
                        YoutubeParsingHelper.isYoutubeServiceURL(urlObj) || YoutubeParsingHelper.isHooktubeURL(urlObj) ||
                        YoutubeParsingHelper.isInvidioURL(urlObj))) {
                    if (host.equalsIgnoreCase("googleads.g.doubleclick.net")) {
                        throw new FoundAdException("Error found ad: " + urlString);
                    }

                    throw new ParsingException("The url is not a Youtube-URL");
                }

                if (YoutubePlaylistLinkHandlerFactory.getInstance().acceptUrl(urlString)) {
                    throw new ParsingException("Error no suitable url: " + urlString);
                }

                // using uppercase instead of lowercase, because toLowercase replaces some unicode characters
                // with their lowercase ASCII equivalent. Using toLowercase could result in faultily matching unicode urls.
                switch (host.toUpperCase()) {
                    case "WWW.YOUTUBE-NOCOOKIE.COM": {
                        if (path.startsWith("embed/")) {
                            id = path.split("/")[1];
                            return assertIsID(id);
                        }

                        break;
                    }

                    case "YOUTUBE.COM":
                    case "WWW.YOUTUBE.COM":
                    case "M.YOUTUBE.COM": {
                        if (path.equals("attribution_link")) {
                            String uQueryValue = Utils.getQueryValue(urlObj, "u");

                            URL decodedURL;
                            try {
                                decodedURL = Utils.stringToURL("http://www.youtube.com" + uQueryValue);
                            } catch (MalformedURLException e) {
                                throw new ParsingException("Error no suitable url: " + urlString);
                            }

                            String viewQueryValue = Utils.getQueryValue(decodedURL, "v");
                            return assertIsID(viewQueryValue);
                        }

                        if (path.startsWith("embed/")) {
                            id = path.split("/")[1];
                            return assertIsID(id);
                        }

                        String viewQueryValue = Utils.getQueryValue(urlObj, "v");
                        return assertIsID(viewQueryValue);
                    }

                    case "YOUTU.BE": {
                        String viewQueryValue = Utils.getQueryValue(urlObj, "v");
                        if (viewQueryValue != null) {
                            return assertIsID(viewQueryValue);
                        }
                        return assertIsID(path);
                    }

                    case "HOOKTUBE.COM": {
                        if (path.startsWith("v/")) {
                            id = path.substring("v/".length());
                            return assertIsID(id);
                        }
                        if (path.startsWith("watch/")) {
                            id = path.substring("watch/".length());

                            return assertIsID(id);
                        }
                        // there is no break-statement here on purpose so the next code-block gets also run for hooktube
                    }

                    case "WWW.INVIDIO.US":
                    case "INVIDIO.US": { // code-block for hooktube.com and invidio.us
                        if (path.equals("watch")) {
                            String viewQueryValue = Utils.getQueryValue(urlObj, "v");
                            if (viewQueryValue != null) {
                                return assertIsID(viewQueryValue);
                            }
                        }
                        if (path.startsWith("embed/")) {
                            id = path.substring("embed/".length());
                            return assertIsID(id);
                        }
                        break;
                    }
                }
            }
            else {
                if (!path.startsWith("/youtube/") && !path.startsWith("/v3/")) {
                    throw new ParsingException("the URL not match");
                }
                id = path.substring(12) + "?" +  query;
                return id;
            }
        } catch (MalformedURLException e) {
            LogHelper.i("YoutubeStreamLink", "MalformedURLException", e.getMessage());
        }

        throw new ParsingException("Error no suitable url: " + urlString);
    }

    @Override
    public boolean onAcceptUrl(final String url) throws FoundAdException {
        try {
            getId(url);
            return true;
        } catch (FoundAdException fe) {
            throw fe;
        } catch (ParsingException e) {
            return false;
        }
    }

    public  String getKey() {
        return key;
    }
}
