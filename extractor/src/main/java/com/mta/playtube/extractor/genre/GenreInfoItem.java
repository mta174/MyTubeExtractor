package com.mta.playtube.extractor.genre;

import com.mta.playtube.extractor.InfoItem;

public class GenreInfoItem  extends InfoItem {
    public GenreInfoItem(int serviceId, String url, String name) {
        super(InfoType.GENRE, serviceId, url, name);
    }

    private String GenreUrl;
    private String GenreName;
    private String Thumbnail;

    public String getGenreUrl() {
        return GenreUrl;
    }

    public void setGenreUrl(String genreUrl) {
        GenreUrl = genreUrl;
    }

    public String getGenreName() {
        return GenreName;
    }

    public void setGenreName(String genreName) {
        GenreName = genreName;
    }

    public String getThumbnail() {
        return Thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        Thumbnail = thumbnail;
    }
}
