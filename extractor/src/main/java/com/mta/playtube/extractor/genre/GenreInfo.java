package com.mta.playtube.extractor.genre;

import com.mta.playtube.extractor.ListExtractor;
import com.mta.playtube.extractor.ListInfo;
import com.mta.playtube.extractor.NewPipe;
import com.mta.playtube.extractor.StreamingService;
import com.mta.playtube.extractor.exceptions.ExtractionException;
import com.mta.playtube.extractor.exceptions.ParsingException;
import com.mta.playtube.extractor.linkhandler.ListLinkHandler;
import com.mta.playtube.extractor.utils.ExtractorHelper;

import java.io.IOException;

public class GenreInfo extends ListInfo<GenreInfoItem> {
    public GenreInfo(int serviceId, ListLinkHandler linkHandler, String name) throws ParsingException {
        super(serviceId, linkHandler, name);
    }

    public static GenreInfo getInfo(String url) throws IOException, ExtractionException {
        return getInfo(NewPipe.getServiceByUrl(url), url);
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
