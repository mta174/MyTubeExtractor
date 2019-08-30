package org.video.playtube.extractor.genre;

import org.video.playtube.extractor.ListExtractor;
import org.video.playtube.extractor.ListInfo;
import org.video.playtube.extractor.PlayTube;
import org.video.playtube.extractor.StreamingService;
import org.video.playtube.extractor.exception.ExtractionException;
import org.video.playtube.extractor.exception.ParsingException;
import org.video.playtube.extractor.linkhandler.ListLinkHandler;
import org.video.playtube.extractor.util.ExtractorHelper;

import java.io.IOException;

public class GenreInfo extends ListInfo<GenreInfoItem> {
    public GenreInfo(int serviceId, ListLinkHandler linkHandler, String name) throws ParsingException {
        super(serviceId, linkHandler, name);
    }

    public static GenreInfo getInfo(String url) throws IOException, ExtractionException {
        return getInfo(PlayTube.getServiceByUrl(url), url);
    }

    public static GenreInfo getInfo(StreamingService service, String url) throws IOException, ExtractionException {
        GenreExtractor extractor = service.getGenreExtractor(url);
        extractor.fetchPage();
        return getInfo(extractor);
    }


    public static GenreInfo getInfo(GenreExtractor extractor) throws IOException, ExtractionException {

        GenreInfo info = new GenreInfo(extractor.getServiceId(), extractor.getLinkHandler(), extractor.getName());

        try {
            info.setOriginalUrl(extractor.getOriginalUrl());
        } catch (Exception e) {
            info.addError(e);
        }
        try {
            info.setVersion(extractor.getVersion());
        } catch (Exception e) {
            info.addError(e);
        }
        try {
            info.setName(extractor.getName());
        } catch (Exception e) {
            info.addError(e);
        }

        final ListExtractor.InfoItemsPage<GenreInfoItem> itemsPage = ExtractorHelper.getItemsPageOrLogError(info, extractor);
        info.setRelatedItems(itemsPage.getItems());
        info.setNextPageUrl(itemsPage.getNextPageUrl());
        return info;
    }

    private String Version;
    private String Name;

    @Override
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getVersion() {
        return Version;
    }

    public void setVersion(String version) {
        Version = version;
    }

}
