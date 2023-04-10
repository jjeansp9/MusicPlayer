package kr.co.musicplayer.model;

import java.util.ArrayList;

public interface OnDataPass {
    void onDataPass(MediaFile item, int position, int itemsNumber, ArrayList<MediaFile> items);
}
