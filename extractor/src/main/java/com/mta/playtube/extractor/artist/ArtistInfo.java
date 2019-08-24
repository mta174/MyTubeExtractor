package com.mta.playtube.extractor.artist;

import com.mta.playtube.extractor.ListExtractor;
import com.mta.playtube.extractor.ListInfo;
import com.mta.playtube.extractor.NewPipe;
import com.mta.playtube.extractor.StreamingService;
import com.mta.playtube.extractor.exceptions.ExtractionException;
import com.mta.playtube.extractor.exceptions.ParsingException;
import com.mta.playtube.extractor.linkhandler.ListLinkHandler;
import com.mta.playtube.extractor.utils.ExtractorHelper;
import com.mta.playtube.extractor.stream.StreamInfoItem;

import java.io.IOException;

public class ArtistInfo extends ListInfo<StreamInfoItem> {
    private static final String TAG = ArtistInfo.class.getSimpleName();
    public ArtistInfo(int serviceId, ListLinkHandler linkHandler, String name) throws ParsingException {
        super(serviceId, linkHandler, name);
    }

    public static ArtistInfo getInfo(String url) throws IOException, ExtractionException {
        return getInfo(NewPipe.getServiceByUrl(url), url);
    }

    public static ArtistInfo getInfo(StreamingService service, String url) throws IOException, ExtractionException {
        ArtistExtractor extractor = service.getArtistExtractor(url);
        extractor.fetchPage();
        return getInfo(extractor);
    }

    public static ArtistInfo getInfo(ArtistExtractor extractor) throws ExtractionException {
        final ArtistInfo info = new ArtistInfo(extractor.getServiceId(), extractor.getLinkHandler(), extractor.getName());

        try {
            info.setOriginalUrl(extractor.getOriginalUrl());
        } catch (Exception e) {
            info.addError(e);
        }
        try {
            info.setArtistId(extractor.getArtistId());
        } catch (Exception e) {
            info.addError(e);
        }
        try {
            info.setArtistName(extractor.getArtistName());
        } catch (Exception e) {
            info.addError(e);
        }
        /*try {
            info.setTotal(extractor.getTotal());
        } catch (Exception e) {
            info.addError(e);
        }*/
        try {
            info.setThumbnail(extractor.getThumbnail());
        } catch (Exception e) {
            info.addError(e);
        }

        final ListExtractor.InfoItemsPage<StreamInfoItem> itemsPage = ExtractorHelper.getItemsPageOrLogError(info, extractor);
        info.setRelatedItems(itemsPage.getItems());
        info.setNextPageUrl(itemsPage.getNextPageUrl());
        return info;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    /*public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }*/

    private String version;
    private String artistId;
    private String artistName;
    private String thumbnail;
    //private int total = 0;
}
