package org.video.playtube.extractor;


import org.video.playtube.extractor.exception.ExtractionException;
import org.video.playtube.extractor.util.Localization;

import java.util.List;

/**
 * Provides access to streaming service supported by PlayTube.
 */
public class PlayTube {
    private static Downloader downloader = null;
    private static Localization localization = null;

    private PlayTube() {
    }

    public static void init(Downloader d, Localization l) {
        downloader = d;
        localization = l;
    }

    public static Downloader getDownloader() {
        return downloader;
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Utils
    //////////////////////////////////////////////////////////////////////////*/

    public static List<StreamingService> getServices() {
        return ServiceList.all();
    }
    public static List<StreamingService> getYtServices() {
        return ServiceList.getYtService();
    }

    public static StreamingService getService(int serviceId) throws ExtractionException {
        for (StreamingService service : ServiceList.all()) {
            if (service.getServiceId() == serviceId) {
                return service;
            }
        }
        throw new ExtractionException("There's no service with the id = \"" + serviceId + "\"");
    }

    public static StreamingService getService(String serviceName) throws ExtractionException {
        for (StreamingService service : ServiceList.all()) {
            if (service.getServiceInfo().getName().equals(serviceName)) {
                return service;
            }
        }
        throw new ExtractionException("There's no service with the name = \"" + serviceName + "\"");
    }

    public static StreamingService getServiceByUrl(String url) throws ExtractionException {
        for (StreamingService service : ServiceList.all()) {
            if (service.getLinkTypeByUrl(url) != StreamingService.LinkType.NONE) {
                return service;
            }
        }
        throw new ExtractionException("No service can handle the url = \"" + url + "\"");
    }

    public static int getIdOfService(String serviceName) {
        try {
            //noinspection ConstantConditions
            return getService(serviceName).getServiceId();
        } catch (ExtractionException ignored) {
            return -1;
        }
    }

    public static String getNameOfService(int id) {
        try {
            //noinspection ConstantConditions
            return getService(id).getServiceInfo().getName();
        } catch (Exception e) {
            System.err.println("Service id not known");
            return "<unknown>";
        }
    }

    public static void setLocalization(Localization localization) {
        PlayTube.localization = localization;
    }

    public static Localization getPreferredLocalization() {
        return localization;
    }
}
