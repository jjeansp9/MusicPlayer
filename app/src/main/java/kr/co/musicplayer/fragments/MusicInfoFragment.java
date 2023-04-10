package kr.co.musicplayer.fragments;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import kr.co.musicplayer.MusicService;
import kr.co.musicplayer.R;
import kr.co.musicplayer.databinding.FragmentMusicInfoBinding;
import kr.co.musicplayer.model.MediaFile;
import kr.co.musicplayer.model.OnDataPass;

public class MusicInfoFragment extends Fragment {

    private FragmentMusicInfoBinding binding;

    protected MusicService musicService;
    private boolean isServiceBound = false;

    private OnDataPass dataPass;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final String ARG_PARAM5 = "param5";
    private static final String ARG_PARAM6 = "param6";
    private static final String ARG_PARAM7 = "param7";

    private String mParam1;
    private String mParam2;
    private String mParam3;
    private int musicDuration;
    private int musicCurrentDuration;
    private int musicNumber;
    private ArrayList<MediaFile> items= new ArrayList<>();


    public static MusicInfoFragment newInstance(String param1, String param2, String param3, int param4, int param5, int param6, ArrayList<MediaFile> items) {
        MusicInfoFragment fragment = new MusicInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putInt(ARG_PARAM4, param4);
        args.putInt(ARG_PARAM5, param5);
        args.putInt(ARG_PARAM6, param6);
        args.putParcelableArrayList(ARG_PARAM7, items);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
            musicDuration = getArguments().getInt(ARG_PARAM4);
            musicCurrentDuration = getArguments().getInt(ARG_PARAM5);
            musicNumber = getArguments().getInt(ARG_PARAM6);
            items = getArguments().getParcelableArrayList(ARG_PARAM7);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= FragmentMusicInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.i("MusicInfoFragment onViewCreated", "MusicInfoFragment onViewCreated : " + mParam1+", "+mParam2 + ", " + items.size());
        binding.musicComposer.setText(mParam2);
        binding.musicTitle.setText(mParam3);

        int m= musicDuration / 60000;
        int s= (musicDuration % 60000) / 1000;
        String strTime = String.format("%01d:%02d", m, s);

        binding.playTimeMax.setText(strTime);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        dataPass= (OnDataPass) context;
    }

    public void clickedPreviousOrNext(int position){
        Log.i("MusicListFragment", "clickedPrevious() : " +position);
        passData(items.get(position), position);
    }

    // 액티비티로 데이터 넘겨주는 메소드
    public void passData(MediaFile item, int position){
        dataPass.onDataPass(item, position, items.size(), items);
    }

//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        dataPass= (OnDataPass) context;
//    }

//    public void clickedPreviousOrNext(int position){
//        Log.i("MusicListFragment", "clickedPrevious() : " +position);
//        passData(items.get(position), position);
//    }
//
//    // 액티비티로 데이터 넘겨주는 메소드
//    public void passData(MediaFile item, int position){
//        dataPass.onDataPass(item, position, musicNumber);
//    }


//    private void seekBar(){
//
//        binding.seekBar.setVisibility(ProgressBar.VISIBLE);
//        binding.seekBar.setMax(musicDuration);
//        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (fromUser){
//
//                    musicService.mp.seekTo(progress);
//                }
//                int m= progress / 60000;
//                int s= (progress % 60000) / 1000;
//                String strTime = String.format("%02d:%02d", m, s);
//                binding.playTime.setText(strTime);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//            }
//        });
//    }

}
