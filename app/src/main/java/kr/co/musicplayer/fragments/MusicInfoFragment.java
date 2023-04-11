package kr.co.musicplayer.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import kr.co.musicplayer.MusicService;
import kr.co.musicplayer.R;
import kr.co.musicplayer.databinding.FragmentMusicInfoBinding;
import kr.co.musicplayer.model.MediaFile;
import kr.co.musicplayer.model.OnDataPass;

public class MusicInfoFragment extends Fragment {

    private FragmentMusicInfoBinding binding;
    protected MusicService musicService;
    private OnDataPass dataPass;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final String ARG_PARAM7 = "param7";
    private static final String ARG_PARAM8 = "param8";

    private String mParam1;
    private String mParam2;
    private String mParam3;
    private int musicDuration;
    private ArrayList<MediaFile> items= new ArrayList<>();
    private int position;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int progress = intent.getIntExtra("progress", 0);
            Log.i("Progresss", progress+"");
            binding.seekBar.setProgress(progress);
        }
    };

    public static MusicInfoFragment newInstance(String param1, String param2, String param3, int param4, ArrayList<MediaFile> items, int position) {
        MusicInfoFragment fragment = new MusicInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putInt(ARG_PARAM4, param4);
        args.putParcelableArrayList(ARG_PARAM7, items);
        args.putInt(ARG_PARAM8, position);
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
            items = getArguments().getParcelableArrayList(ARG_PARAM7);
            position = getArguments().getInt(ARG_PARAM8);
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
        Uri albumArtUri = Uri.parse("content://media/external/audio/albumart/" + items.get(position).getUri());
        Bitmap albumArt = null;

        try {
            albumArt = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), albumArtUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        binding.musicImage.setImageBitmap(albumArt);
        binding.musicComposer.setText(mParam2);
        binding.musicTitle.setText(mParam3);

        int m= musicDuration / 60000;
        int s= (musicDuration % 60000) / 1000;
        String strTime = String.format("%01d:%02d", m, s);

        binding.playTimeMax.setText(strTime);
        seekBar();
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("MEDIA_PLAYER_PROGRESS");
        filter.addAction("UPDATE_PROGRESS");
        getActivity().registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        dataPass= (OnDataPass) context;

        // Service와 바인딩하여 Service 객체 가져오기
        Intent intent = new Intent(getActivity(), MusicService.class);
        getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MyBinder binder = (MusicService.MyBinder) service;
            musicService = binder.getMyServiceAddress();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicService = null;
        }
    };

    public void clickedPreviousOrNext(int position){
        Log.i("MusicListFragment", "clickedPrevious() : " +position);
        passData(items.get(position), position);

        Uri albumArtUri = Uri.parse("content://media/external/audio/albumart/" + items.get(position).getUri());
        Bitmap albumArt = null;

        try {
            albumArt = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), albumArtUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        binding.musicImage.setImageBitmap(albumArt);
        binding.musicComposer.setText(items.get(position).getArtist());
        binding.musicTitle.setText(items.get(position).getTitle());
        binding.playTimeMax.setText(items.get(position).getDuration());
    }

    // 액티비티로 데이터 넘겨주는 메소드
    public void passData(MediaFile item, int position){
        dataPass.onDataPass(item, position, items.size(), items);
    }

    private void seekBar(){

        binding.seekBar.setMax(musicDuration);
        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                int m= progress / 60000;
                int s= (progress % 60000) / 1000;
                String strTime = String.format("%01d:%02d", m, s);
                binding.playTime.setText(strTime);

                if (fromUser){
                    Intent intent = new Intent("MEDIA_PLAYER_SEEK");
                    intent.putExtra("progress", progress);
                    getActivity().sendBroadcast(intent);
                    musicService.mp.seekTo(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (serviceConnection != null) {
            getActivity().unbindService(serviceConnection);
        }
    }

}
