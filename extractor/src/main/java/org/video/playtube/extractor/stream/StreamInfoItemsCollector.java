package org.video.playtube.extractor.stream;

import org.video.playtube.extractor.InfoItem;
import org.video.playtube.extractor.InfoItemsCollector;
import org.video.playtube.extractor.exception.FoundAdException;
import org.video.playtube.extractor.exception.ParsingException;

import java.util.List;
import java.util.Vector;

public class StreamInfoItemsCollector extends InfoItemsCollector<StreamInfoItem, StreamInfoItemExtractor> {

    public StreamInfoItemsCollector(int serviceId) {
        super(serviceId);
    }

    @Override
    public StreamInfoItem extract(StreamInfoItemExtractor extractor) throws ParsingException {
        if (extractor.isAd()) {
            throw new FoundAdException("Found ad");
        }

        // important information
        int serviceId = getServiceId();
        String url = extractor.getUrl();
        String name = extractor.getName();
        StreamType streamType = extractor.getStreamType();

        StreamInfoItem resultItem = new StreamInfoItem(serviceId, url, name, streamType);


        // optional information
        try {
            resultItem.setDuration(extractor.getDuration());
        } catch (Exception e) {
            addError(e);
        }
        try {
            resultItem.setUploaderName(extractor.getUploaderName());
        } catch (Exception e) {
            addError(e);
        }
        try {
            resultItem.setUploadDate(extractor.getUploadDate());
        } catch (Exception e) {
            addError(e);
        }
        try {
            resultItem.setViewCount(extractor.getViewCount());
        } catch (Exception e) {
            addError(e);
        }
        try {
            resultItem.setThumbnailUrl(extractor.getThumbnailUrl());
        } catch (Exception e) {
            addError(e);
        }
        try {
            resultItem.setUploaderUrl(extractor.getUploaderUrl());
        } catch (Exception e) {
            addError(e);
        }
        return resultItem;
    }

    @Override
    public void commit(StreamInfoItemExtractor extractor) {
        try {
            addItem(extract(extractor));
        } catch (FoundAdException ae) {
            //System.out.println("AD_WARNING: " + ae.getMessage());
        } catch (Exception e) {
            addError(e);
        }
    }

    public List<StreamInfoItem> getStreamInfoItemList() {
        List<StreamInfoItem> siiList = new Vector<>();
        for(InfoItem ii : super.getItems()) {
            if(ii instanceof StreamInfoItem) {
                siiList.add((StreamInfoItem) ii);
            }
        }
        return siiList;
    }
}
