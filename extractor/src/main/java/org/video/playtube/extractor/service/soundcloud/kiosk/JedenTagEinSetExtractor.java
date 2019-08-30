package org.video.playtube.extractor.service.soundcloud.kiosk;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.video.playtube.extractor.Downloader;
import org.video.playtube.extractor.StreamingService;
import org.video.playtube.extractor.exception.ExtractionException;
import org.video.playtube.extractor.exception.ParsingException;
import org.video.playtube.extractor.kiosk.KioskExtractor;
import org.video.playtube.extractor.linkhandler.ListLinkHandler;
import org.video.playtube.extractor.service.soundcloud.SoundcloudParsingHelper;
import org.video.playtube.extractor.stream.StreamInfoItem;
import org.video.playtube.extractor.stream.StreamInfoItemExtractor;
import org.video.playtube.extractor.stream.StreamInfoItemsCollector;
import org.video.playtube.extractor.stream.StreamType;
import org.video.playtube.extractor.util.Localization;
import org.video.playtube.extractor.util.Parser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import static org.video.playtube.extractor.stream.StreamType.AUDIO_STREAM;
public class JedenTagEinSetExtractor extends KioskExtractor<StreamInfoItem> {
    public JedenTagEinSetExtractor(StreamingService streamingService,
                                   ListLinkHandler linkHandler,
                                   String kioskId,
                                   Localization localization) {
        super(streamingService, linkHandler, kioskId, localization);
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        return "jedentageinset";
    }

    @Nonnull
    @Override
    public InfoItemsPage<StreamInfoItem> getInitialPage() throws IOException, ExtractionException {
        return getPage("https://www.jedentageinset.de/page/1");
    }

    @Override
    public String getNextPageUrl() throws IOException, ExtractionException {
        return "https://www.jedentageinset.de/page/2";
    }

    @Override
    public InfoItemsPage<StreamInfoItem> getPage(String pageUrl) throws IOException, ExtractionException {
        try {
            final Downloader d = getDownloader();
            final String rawPage = d.download(pageUrl);
            final Document page = Jsoup.parse(rawPage, JedenTagEinSetLinkHandlerFactory.URL);

            final Elements homeBoxes = page.getElementById("posts_cont")
                    .getElementsByClass("home_box");

            //get links to the set pages
            final List<String> setLinks = new ArrayList<>();
            for (Element homeBox : homeBoxes) {
                setLinks.add(homeBox
                        .getElementsByClass("home_box_cont")
                        .first()
                        .getElementsByTag("a")
                        .first().attr("href"));
            }

            //get soundcloud api url from set links
            final StreamInfoItemExtractor[] extractors = new StreamInfoItemExtractor[setLinks.size()];
            final List<Throwable> errorList = Collections.synchronizedList(new ArrayList<Throwable>());
            final List<Thread> threadHandles = new ArrayList<>(setLinks.size());

            for (int i = 0; i < setLinks.size(); i++) {
                final String setLink = setLinks.get(i);
                final int index = i;
                final Element homeBox = homeBoxes.get(i);
                final Thread t = new Thread(new Runnable() {

                    private URL getSoundCloudFrameUrl(Document setPage) throws MalformedURLException, ParsingException {
                        Elements frames = setPage.getElementsByTag("iframe");
                        for(Element frame : frames) {
                            if(frame.attr("src").startsWith("https://w.soundcloud.com/player/")) {
                                return new URL(frame.attr("src"));
                            }
                        }
                        throw new ParsingException("No soundcloud iframe found");
                    }

                    @Override
                    public void run() {
                        try {
                            final Document setPage = Jsoup.parse(d.download(setLink), setLink);
                            final URL frameUrl = getSoundCloudFrameUrl(setPage);

                            final String apiUrl = Parser.compatParseMap(frameUrl.getQuery()).get("url");
                            final String streamUrl = SoundcloudParsingHelper.resolveUrlWithEmbedPlayer(apiUrl);
                            final String uploadDate = setPage.getElementsByClass("post_date")
                                    .first().getElementsByTag("span").first().html();
                            final String name = setPage.getElementsByClass("single_title")
                                    .first().html();
                            final String thumbnail = homeBox.getElementsByClass("attachment-home-box-image")
                                    .first().attr("src");

                            extractors[index] = new StreamInfoItemExtractor() {
                                @Override
                                public StreamType getStreamType() throws ParsingException {
                                    return AUDIO_STREAM;
                                }

                                @Override
                                public boolean isAd() throws ParsingException {
                                    return false;
                                }

                                @Override
                                public long getDuration() throws ParsingException {
                                    return -1;
                                }

                                @Override
                                public long getViewCount() throws ParsingException {
                                    return -1;
                                }

                                @Override
                                public String getUploaderName() throws ParsingException {
                                    return null;
                                }

                                @Override
                                public String getUploaderUrl() throws ParsingException {
                                    return null;
                                }

                                @Override
                                public String getUploadDate() throws ParsingException {
                                    return uploadDate;
                                }

                                @Override
                                public String getName() throws ParsingException {
                                    return name;
                                }

                                @Override
                                public String getUrl() throws ParsingException {
                                    return streamUrl;
                                }

                                @Override
                                public String getThumbnailUrl() throws ParsingException {
                                    return thumbnail;
                                }
                            };

                        } catch (Exception e) {
                            errorList.add(e);
                        }
                    }
                });
                t.start();
                threadHandles.add(t);
            }

            for (Thread t : threadHandles) {
                t.join();
            }

            StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());
            for(StreamInfoItemExtractor e : extractors) {
                collector.commit(e);
            }

            List<StreamInfoItem> infoItems = collector.getStreamInfoItemList();
            errorList.addAll(collector.getErrors());

            final int nextPageNum =
                    Integer.valueOf(
                            pageUrl.replace("https://www.jedentageinset.de/page/", "")) + 1;



            return new InfoItemsPage<>(infoItems, "https://www.jedentageinset.de/page/" + nextPageNum, errorList);
        } catch (Exception e) {
            throw new ExtractionException(e);
        }
    }

    @Override
    public void onFetchPage(@Nonnull Downloader downloader) throws IOException, ExtractionException {
    }
}

