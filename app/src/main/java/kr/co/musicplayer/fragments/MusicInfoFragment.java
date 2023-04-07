package kr.co.musicplayer.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import kr.co.musicplayer.MusicService;
import kr.co.musicplayer.R;
import kr.co.musicplayer.databinding.FragmentMusicInfoBinding;
import kr.co.musicplayer.model.MediaFile;

public class MusicInfoFragment extends Fragment {

    private FragmentMusicInfoBinding binding;

    protected MusicService musicService;
    private boolean isServiceBound = false;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    private String mParam1;
    private String mParam2;
    private String mParam3;

    Intent intent;

    public static MusicInfoFragment newInstance(String param1, String param2, String param3) {
        MusicInfoFragment fragment = new MusicInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
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
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= FragmentMusicInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.i("MusicInfoFragment onViewCreated", "MusicInfoFragment onViewCreated : " + mParam1+", "+mParam2);
        binding.musicComposer.setText(mParam2);
        binding.musicTitle.setText(mParam3);

        Log.e("ggggg", musicService+"");
    }

    // MusicService와 연결
    ServiceConnection connection= new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.MyBinder binder = (MusicService.MyBinder) iBinder;
            musicService = binder.getMyServiceAddress();

            Log.e("ggggg", musicService+"");
            seekBar();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private void seekBar(){

        binding.seekBar.setVisibility(ProgressBar.VISIBLE);
        binding.seekBar.setMax(musicService.mp.getDuration());

        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    musicService.mp.seekTo(progress);
                }
                int m= progress / 60000;
                int s= (progress % 60000) / 1000;
                String strTime = String.format("%02d:%02d", m, s);
                binding.text.setText(strTime);

                int ms= musicService.mp.getDuration() / 60000;
                int ss= (musicService.mp.getDuration() % 60000) / 1000;
                String strTimes = String.format("%02d:%02d", ms, ss);

                binding.textMax.setText(strTimes);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(getActivity(), MusicService.class);
        getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isServiceBound) {
            getActivity().unbindService(connection);
            isServiceBound = false;
        }
    }

//    private void musicPlay(){
//        mp.start();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (mp.isPlaying()){
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }
//        }).start();


//    }

}
