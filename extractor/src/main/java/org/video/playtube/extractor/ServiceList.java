package org.video.playtube.extractor;

import org.video.playtube.extractor.service.soundcloud.SoundcloudService;
import org.video.playtube.extractor.service.youtube.YoutubeService;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;


/**
 * A list of supported service.
 */
public final class ServiceList {
    private ServiceList() {
        //no instance
    }

    public static final YoutubeService YouTube = new YoutubeService(0);
    public static final SoundcloudService SoundCloud = new SoundcloudService(1);
    //public static final MediaCCCService MediaCCC = new MediaCCCService(2);

    /**
     * When creating a new service, put this service in the end of this list,
     * and give it the next free id.
     */
    private static final List<StreamingService> SERVICES = unmodifiableList(
            asList(
                    YouTube,
                    SoundCloud
                    //MediaCCC = new MediaCCCService(2)
            ));
    private static final List<StreamingService> YT_SERVICES = unmodifiableList(
            asList(
                    YouTube
                    //MediaCCC = new MediaCCCService(2)
            ));
    /**
     * Get all the supported service.
     *
     * @return a unmodifiable list of all the supported service
     */
    public static List<StreamingService> all() {
        return SERVICES;
    }
    public static List<StreamingService> getYtService() {
        return YT_SERVICES;
    }
}
