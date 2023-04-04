package kr.co.musicplayer.model;

import android.net.Uri;

public class MediaFile {

    String artist;
    String title;
    String duration;

    public MediaFile(String artist, String title, String duration) {
        this.artist = artist;
        this.title = title;
        this.duration = duration;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}


