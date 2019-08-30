package org.video.playtube.extractor.service.youtube.extractor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.video.playtube.extractor.Downloader;
import org.video.playtube.extractor.InfoItem;
import org.video.playtube.extractor.StreamingService;
import org.video.playtube.extractor.exception.ExtractionException;
import org.video.playtube.extractor.exception.ParsingException;
import org.video.playtube.extractor.search.InfoItemsSearchCollector;
import org.video.playtube.extractor.search.SearchExtractor;
import org.video.playtube.extractor.linkhandler.SearchQueryHandler;
import org.video.playtube.extractor.util.Localization;
import org.video.playtube.extractor.util.Parser;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;


public class YoutubeSearchExtractor extends SearchExtractor {
    private static final String TAG = YoutubeSearchExtractor.class.getSimpleName();
    private Document doc;

    public YoutubeSearchExtractor(StreamingService service, SearchQueryHandler linkHandler, Localization localization) {
        super(service, linkHandler, localization);
    }

    @Override
    public void onFetchPage(@Nonnull Downloader downloader) throws IOException, ExtractionException {
        final String site;
        final String url = getUrl();
        //String url = builder.build().toString();
        //if we've been passed a valid language code, append it to the URL
        site = downloader.download(url, getLocalization());
        doc = Jsoup.parse(site, url);
    }

    @Override
    public String getUrl() throws ParsingException {
        return super.getUrl() + "&gl="+ getLocalization().getCountry();
    }

    @Override
    public String getSearchSuggestion() {
        final Element el = doc.select("div[class*=\"spell-correction\"]").first();
        if (el != null) {
            return el.select("a").first().text();
        } else {
            return "";
        }
    }

    @Nonnull
    @Override
    public InfoItemsPage<InfoItem> getInitialPage() throws ExtractionException {
        return new InfoItemsPage<>(collectItems(doc), getNextPageUrl());
    }

    @Override
    public String getNextPageUrl() throws ExtractionException {
        return getUrl() + "&page=" + 2;
    }

    @Override
    public InfoItemsPage<InfoItem> getPage(String pageUrl) throws IOException, ExtractionException {
        String site = getDownloader().download(pageUrl);
        doc = Jsoup.parse(site, pageUrl);

        return new InfoItemsPage<>(collectItems(doc), getNextPageUrlFromCurrentUrl(pageUrl));
    }

    private String getNextPageUrlFromCurrentUrl(String currentUrl)
            throws MalformedURLException, UnsupportedEncodingException {
        final int pageNr = Integer.parseInt(
                Parser.compatParseMap(new URL(currentUrl).getQuery()).get("page"));

        return currentUrl.replace("&page=" + pageNr, "&page=" + Integer.toString(pageNr + 1));
    }

    private InfoItemsSearchCollector collectItems(Document doc) throws NothingFoundException  {
        InfoItemsSearchCollector collector = getInfoItemSearchCollector();

        Element list = doc.select("ol[class=\"item-section\"]").first();

        for (Element item : list.children()) {
            /* First we need to determine which kind of item we are working with.
               Youtube depicts five different kinds of items on its search result page. These are
               regular videos, playlists, channels, two types of video suggestions, and a "no video
               found" item. Since we only want videos, we need to filter out all the others.
               An example for this can be seen here:
               https://www.youtube.com/results?search_query=asdf&page=1

               We already applied a filter to the url, so we don't need to care about channels and
               playlists now.
            */

            Element el;

            if ((el = item.select("div[class*=\"search-message\"]").first()) != null) {
                throw new NothingFoundException(el.text());

                // video item type
            } else if ((el = item.select("div[class*=\"yt-lockup-video\"]").first()) != null) {
                collector.commit(new YoutubeStreamInfoItemExtractor(el));
            } else if ((el = item.select("div[class*=\"yt-lockup-channel\"]").first()) != null) {
                collector.commit(new YoutubeChannelInfoItemExtractor(el));
            } else if ((el = item.select("div[class*=\"yt-lockup-playlist\"]").first()) != null &&
                    item.select(".yt-pl-icon-mix").isEmpty()) {
                collector.commit(new YoutubePlaylistInfoItemExtractor(el));
            }
        }

        return collector;
    }
}
