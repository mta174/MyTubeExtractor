package com.mta.playtube.extractor.genre;

import com.mta.playtube.extractor.InfoItem;
import com.mta.playtube.extractor.InfoItemsCollector;
import com.mta.playtube.extractor.exceptions.ParsingException;

import java.util.List;
import java.util.Vector;

public class GenreInfoItemsCollector extends InfoItemsCollector<GenreInfoItem, GenreInfoItemExtractor> {
    public GenreInfoItemsCollector(int serviceId) {
        super(serviceId);
    }

    @Override
    public GenreInfoItem extract(GenreInfoItemExtractor extractor) throws ParsingException {

        // important information
        int serviceId = getServiceId();
        String url = extractor.getUrl();
        String name = extractor.getName();
        GenreInfoItem resultItem = new GenreInfoItem(serviceId, url, name);

        // optional information
        try {
            resultItem.setGenreUrl(extractor.getGenreUrl());
        } catch (Exception e) {
            addError(e);
        }
        try {
            resultItem.setGenreUrl(extractor.getGenreUrl());
        } catch (Exception e) {
            addError(e);
        }
        try {
            resultItem.setGenreName(extractor.getGenreName());
        } catch (Exception e) {
            addError(e);
        }
        try {
            resultItem.setThumbnailUrl(extractor.getThumbnailUrl());
        } catch (Exception e) {
            addError(e);
        }
        try {
            resultItem.setThumbnail(extractor.getThumbnail());
        } catch (Exception e) {
            addError(e);
        }
        return resultItem;
    }

    @Override
    public void commit(GenreInfoItemExtractor extractor) {
        try {
            addItem(extract(extractor));
        } catch (Exception e) {
            addError(e);
        }
    }

    public List<GenreInfoItem> getGenreInfoItemList() {
        List<GenreInfoItem> siiList = new Vector<>();
        for(InfoItem ii : super.getItems()) {
            if(ii instanceof GenreInfoItem) {
                siiList.add((GenreInfoItem) ii);
            }
        }
        return siiList;
    }
}
