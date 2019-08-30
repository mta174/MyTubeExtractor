package org.video.playtube.extractor.artist;

import org.video.playtube.extractor.InfoItem;

public class ArtistInfoItem extends InfoItem {

    private String artistId;
    private String artistName;
    //private int total = 0;

    public ArtistInfoItem(int serviceId, String url, String name) {
        super(InfoType.ARTIST, serviceId, url, name);
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

    /*public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }*/
}
