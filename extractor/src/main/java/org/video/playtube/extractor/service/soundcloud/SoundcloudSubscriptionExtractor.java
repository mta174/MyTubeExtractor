package org.video.playtube.extractor.service.soundcloud;

import org.video.playtube.extractor.channel.ChannelInfoItem;
import org.video.playtube.extractor.channel.ChannelInfoItemsCollector;
import org.video.playtube.extractor.exception.ExtractionException;
import org.video.playtube.extractor.subscription.SubscriptionExtractor;
import org.video.playtube.extractor.subscription.SubscriptionItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Extract the "followings" from a user in SoundCloud.
 */
public class SoundcloudSubscriptionExtractor extends SubscriptionExtractor {

    public SoundcloudSubscriptionExtractor(SoundcloudService service) {
        super(service, Collections.singletonList(ContentSource.CHANNEL_URL));
    }

    @Override
    public String getRelatedUrl() {
        return "https://soundcloud.com/you";
    }

    @Override
    public List<SubscriptionItem> fromChannelUrl(String channelUrl) throws IOException, ExtractionException {
        if (channelUrl == null) throw new InvalidSourceException("channel url is null");

        String id;
        try {
            id = service.getChannelLHFactory().fromUrl(getUrlFrom(channelUrl)).getId();
        } catch (ExtractionException e) {
            throw new InvalidSourceException(e);
        }
        StringBuffer apiUrl = new StringBuffer();
        apiUrl.append("https://api.soundcloud.com/users/").append(id).append("/followings")
                .append("?client_id=").append(SoundcloudParsingHelper.clientId()).append("&limit=200");
        ChannelInfoItemsCollector collector = new ChannelInfoItemsCollector(service.getServiceId());
        // ± 2000 is the limit of followings on SoundCloud, so this minimum should be enough
        SoundcloudParsingHelper.getUsersFromApiMinItems(2500, collector, apiUrl.toString());

        return toSubscriptionItems(collector.getItems());
    }

    private String getUrlFrom(String channelUrl) {
        channelUrl = channelUrl.replace("http://", "https://").trim();

        if (!channelUrl.startsWith("https://")) {
            if (!channelUrl.contains("soundcloud.com/")) {
                channelUrl = "https://soundcloud.com/" + channelUrl;
            } else {
                channelUrl = "https://" + channelUrl;
            }
        }

        return channelUrl;
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Utils
    //////////////////////////////////////////////////////////////////////////*/

    private List<SubscriptionItem> toSubscriptionItems(List<ChannelInfoItem> items) {
        List<SubscriptionItem> result = new ArrayList<>(items.size());
        for (ChannelInfoItem item : items) {
            result.add(new SubscriptionItem(item.getServiceId(), item.getUrl(), item.getName()));
        }
        return result;
    }
}
