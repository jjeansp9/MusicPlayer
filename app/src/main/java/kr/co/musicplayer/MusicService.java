package kr.co.musicplayer;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import kr.co.musicplayer.activities.MainActivity;
import kr.co.musicplayer.fragments.MusicInfoFragment;
import kr.co.musicplayer.fragments.MusicListFragment;
import kr.co.musicplayer.model.MediaFile;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener{

    public MediaPlayer mp= new MediaPlayer();
    private NotificationMediaStyle notificationMediaStyle;
    private MediaFile mediaFile= new MediaFile("", "", "", "", R.drawable.ic_baseline_image_24);
    private boolean isPrepared = false;
    private Handler mHandler = new Handler();
    // Fragment로부터 전달받은 SeekBar를 저장하는 변수.
    private SeekBar seekBar;
    private int currentDuration;

    private void updateProgress() {
        if (mp != null && mp.isPlaying()) {
            int currentPosition = mp.getCurrentPosition();
            Intent intent = new Intent("MEDIA_PLAYER_PROGRESS");
            intent.putExtra("progress", currentPosition);
            sendBroadcast(intent);
            mHandler.postDelayed(mUpdateTimeTask, 100);
        }
    }

    // MediaPlayer의 재생 시간이 변경될 때마다 호출되는 Runnable 객체
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if (mp != null && isPrepared) {
                int currentPosition = mp.getCurrentPosition();
                seekBar.setProgress(currentPosition);
            }
            mHandler.postDelayed(this, 100);
        }
    };

    @Override
    public void onCreate() {
        //서비스에서 가장 먼저 호출(최초한번)
        notificationMediaStyle= new NotificationMediaStyle();
        Log.d("Service onCreate", "onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null){
            MusicStop();
            return START_NOT_STICKY;
        }else{
            String data = intent.getStringExtra("data");
            String title = intent.getStringExtra("title");
            String artist = intent.getStringExtra("artist");
            long uri = intent.getLongExtra("uri", 0);

            if (intent.getAction()!=null && intent.getAction().equals(NotificationMediaStyle.ACTION_PLAY)) musicStart();
            else if (intent.getAction()!=null && intent.getAction().equals(NotificationMediaStyle.ACTION_PAUSE)) musicPause();
            else if (intent.getAction()!=null && intent.getAction().equals(NotificationMediaStyle.ACTION_PREVIOUS)) sendBroadcast(new Intent("PREVIOUS"));
            else if (intent.getAction()!=null && intent.getAction().equals(NotificationMediaStyle.ACTION_NEXT)) sendBroadcast(new Intent("NEXT"));

            try {
                if (data != null){

                    mp.reset();
                    mp.setDataSource(data);
                    mp.prepare();
                    mp.start();

                    if (notificationMediaStyle==null){
                        notificationMediaStyle = new NotificationMediaStyle();
                        notificationMediaStyle.craeteNotification(this, artist, title, uri,0);
                    }else{
                        notificationMediaStyle.craeteNotification(this, artist, title, uri, 0);
                    }

                    mediaFile.setArtist(artist);
                    mediaFile.setTitle(title);

                    sendBroadcast(new Intent("PLAY"));
                }

            } catch (IOException e) {
                Toast.makeText(this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (mp.isPlaying()){
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        updateProgress();
                    }
                }
            }).start();
            playingMusic();
        }

        Log.d("Service onStartCommand", "onStartCommand : ");
        return START_STICKY;
    }

    private void playingMusic(){
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                sendBroadcast(new Intent("NEXT"));
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Service onDestroy", "onDestroy");
    }

    // bindService()를 실행할 때 자동 발동하는 메소드
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        currentDuration= 0;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        if (currentDuration > 0) {
            mediaPlayer.seekTo(currentDuration);
        }
    }

    public class MyBinder extends Binder {
        // 이 MyService 객체의 주소값을 리턴해주는 기능함수
        public MusicService getMyServiceAddress(){
            return MusicService.this;
        }
    }

    public String musicStart(){
        mp.start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mp.isPlaying()){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    updateProgress();
                }
            }
        }).start();

        playingMusic();
        sendBroadcast(new Intent("PLAY"));
        notificationMediaStyle.craeteNotification(this, mediaFile.getArtist(), mediaFile.getTitle(), mediaFile.getUri(), 0);
        return "Start";
    }

    public String musicPause(){
        mp.pause();
        sendBroadcast(new Intent("PAUSE"));
        notificationMediaStyle.craeteNotification(this, mediaFile.getArtist(), mediaFile.getTitle(), mediaFile.getUri(), 1);
        return "Pause";
    }

    public void MusicStop(){
        mp.stop();
        if (notificationMediaStyle!=null){
            notificationMediaStyle.finishNotification(getApplication());
            notificationMediaStyle=null;
        }
    }
}




















