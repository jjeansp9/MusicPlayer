package kr.co.musicplayer.model;

import android.app.Application;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import kr.co.musicplayer.GlobalApplication;

public class MediaFile implements Parcelable {

    String data;
    String artist;
    String title;
    String duration;
    long uri;

    public MediaFile(String data, String artist, String title, String duration, long uri) {
        this.data = data;
        this.artist = artist;
        this.title = title;
        this.duration = duration;
        this.uri = uri;
    }

    public long getUri() {
        return uri;
    }

    public void setUri(long uri) {
        this.uri = uri;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(data);
        parcel.writeString(artist);
        parcel.writeString(title);
        parcel.writeString(duration);
    }

    protected MediaFile(Parcel in) {
        data = in.readString();
        artist = in.readString();
        title = in.readString();
        duration = in.readString();
    }

    public static final Creator<MediaFile> CREATOR = new Creator<MediaFile>() {
        @Override
        public MediaFile createFromParcel(Parcel in) {
            return new MediaFile(in);
        }

        @Override
        public MediaFile[] newArray(int size) {
            return new MediaFile[size];
        }
    };

}


