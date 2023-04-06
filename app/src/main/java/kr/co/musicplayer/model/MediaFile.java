package kr.co.musicplayer.model;

import android.app.Application;
import android.net.Uri;

import java.io.Serializable;

import kr.co.musicplayer.GlobalApplication;

public class MediaFile {

    String data;
    String artist;
    String title;
    String duration;

    public MediaFile(String data, String artist, String title, String duration) {
        this.data = data;
        this.artist = artist;
        this.title = title;
        this.duration = duration;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
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


