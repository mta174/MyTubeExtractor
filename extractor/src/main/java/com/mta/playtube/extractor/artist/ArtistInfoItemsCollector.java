package com.mta.playtube.extractor.artist;

import com.mta.playtube.extractor.InfoItemsCollector;
import com.mta.playtube.extractor.exceptions.ParsingException;

public class ArtistInfoItemsCollector extends InfoItemsCollector<ArtistInfoItem, ArtistInfoItemExtractor> {

    public ArtistInfoItemsCollector(int serviceId) {
        super(serviceId);
    }

    @Override
    public ArtistInfoItem extract(ArtistInfoItemExtractor extractor) throws ParsingException {

        String name = extractor.getName();
        int serviceId = getServiceId();
        String url = extractor.getUrl();

        ArtistInfoItem resultItem = new ArtistInfoItem(serviceId, url, name);

        try {
            resultItem.setArtistId(extractor.getArtistId());
        } catch (Exception e) {
            addError(e);
        }
        try {
            resultItem.setArtistName(extractor.getArtistName());
        } catch (Exception e) {
            addError(e);
        }
        /*try {
            resultItem.setTotal(extractor.getTotal());
        } catch (Exception e) {
            addError(e);
        }*/
        return resultItem;
    }
}
