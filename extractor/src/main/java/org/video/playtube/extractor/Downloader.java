package org.video.playtube.extractor;

import java.io.IOException;
import java.util.Map;

import org.video.playtube.extractor.exception.ReCaptchaException;
import org.video.playtube.extractor.util.Localization;

public interface Downloader {

    /**
     * Download the text file at the supplied URL as in download(String), but set
     * the HTTP header field "Accept-Language" to the supplied string.
     *
     * @param siteUrl  the URL of the text file to return the contents of
     * @param localization the language and country (usually a 2-character code for each)
     * @return the contents of the specified text file
     * @throws IOException
     */
    String download(String siteUrl, Localization localization) throws IOException, ReCaptchaException;

    String downloadCustomize(String siteUrl, Localization localization) throws IOException;

    /**
     * Download the text file at the supplied URL as in download(String), but set
     * the HTTP header field "Accept-Language" to the supplied string.
     *
     * @param siteUrl          the URL of the text file to return the contents of
     * @param customProperties set request header properties
     * @return the contents of the specified text file
     * @throws IOException
     */
    String download(String siteUrl, Map<String, String> customProperties) throws IOException, ReCaptchaException;

    String downloadCustomize(String siteUrl, Map<String, String> customProperties) throws IOException;

    /**
     * Download (via HTTP) the text file located at the supplied URL, and return its
     * contents. Primarily intended for downloading web pages.
     *
     * @param siteUrl the URL of the text file to download
     * @return the contents of the specified text file
     * @throws IOException
     */
    String download(String siteUrl) throws IOException, ReCaptchaException;

    DownloadResponse get(String siteUrl, DownloadRequest request) throws IOException, ReCaptchaException;

    DownloadResponse get(String siteUrl) throws IOException, ReCaptchaException;

    DownloadResponse post(String siteUrl, DownloadRequest request) throws IOException, ReCaptchaException;

    String downloadCustomize(String siteUrl) throws IOException;

    DownloadResponse getCustomize(String siteUrl, DownloadRequest request) throws IOException;

    DownloadResponse getCustomize(String siteUrl) throws IOException;

    DownloadResponse postCustomize(String siteUrl, DownloadRequest request) throws IOException;
}
