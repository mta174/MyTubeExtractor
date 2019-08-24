package com.mta.playtube.extractor.services.soundcloud.channel;

import com.grack.nanojson.JsonObject;
import com.mta.playtube.extractor.channel.ChannelInfoItemExtractor;

import static com.mta.playtube.extractor.utils.Utils.replaceHttpWithHttps;

public class SoundcloudChannelInfoItemExtractor implements ChannelInfoItemExtractor {
    private final JsonObject itemObject;

    public SoundcloudChannelInfoItemExtractor(JsonObject itemObject) {
        this.itemObject = itemObject;
    }

    @Override
    public String getName() {
        return itemObject.getString("username");
    }

    @Override
    public String getUrl() {
        return replaceHttpWithHttps(itemObject.getString("permalink_url"));
    }

    @Override
    public String getThumbnailUrl() {
        String avatarUrl = itemObject.getString("avatar_url", "");
        String avatarUrlBetterResolution = avatarUrl.replace("large.jpg", "crop.jpg");
        return avatarUrlBetterResolution;
    }

    @Override
    public long getSubscriberCount() {
        return itemObject.getNumber("followers_count", 0).longValue();
    }

    @Override
    public long getStreamCount() {
        return itemObject.getNumber("track_count", 0).longValue();
    }

    @Override
    public String getDescription() {
        return itemObject.getString("description", "");
    }
}
