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

    private ArrayList<MediaFile> items= new ArrayList<>();
    private MediaFile mediaFile= new MediaFile("", "", "", "");

    private boolean isPrepared = false;
    private Handler mHandler = new Handler();

    // Fragment로부터 전달받은 SeekBar를 저장하는 변수
    private SeekBar seekBar;

    private int currentDuration;

    private void updateProgress() {
        if (mp != null && mp.isPlaying()) {
            int currentPosition = mp.getCurrentPosition();
            Intent intent = new Intent("MEDIA_PLAYER_PROGRESS");
            intent.putExtra("progress", currentPosition);
            sendBroadcast(intent);
            mHandler.postDelayed(mUpdateTimeTask, 1000);
        }
    }

    // MediaPlayer의 재생 시간이 변경될 때마다 호출되는 Runnable 객체
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if (mp != null && isPrepared) {
                int currentPosition = mp.getCurrentPosition();
                seekBar.setProgress(currentPosition);
            }
            mHandler.postDelayed(this, 1000);
        }
    };


    @Override
    public void onCreate() {
        //서비스에서 가장 먼저 호출(최초한번)
        //mp.setLooping(false); // 반복재생
        notificationMediaStyle= new NotificationMediaStyle();

        Log.d("Service onCreate", "onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = intent.getAction();
        Log.i("Service onStartCommand", "Service onStartCommand : " + (action!=null?action:"none action"));
        if (action!=null){

        }

        if (intent.getAction()!= null && intent.getAction().equals(NotificationMediaStyle.ACTION_PLAY)) musicStart();
        else if (intent.getAction()!= null && intent.getAction().equals(NotificationMediaStyle.ACTION_PAUSE)) musicPause();
        else if (intent.getAction()!= null && intent.getAction().equals(NotificationMediaStyle.ACTION_PREVIOUS)) sendBroadcast(new Intent("PREVIOUS"));
        else if (intent.getAction()!= null && intent.getAction().equals(NotificationMediaStyle.ACTION_NEXT)) sendBroadcast(new Intent("NEXT"));
        else{
            try {
                if (processCommand(intent).getData() != null){
                    mp.reset();
                    mp.setDataSource(processCommand(intent).getData());
                    mp.prepare();
                    mp.start();

                    notificationMediaStyle.craeteNotification(this, processCommand(intent).getArtist(), processCommand(intent).getTitle(), 0);

                    mediaFile.setArtist(processCommand(intent).getArtist());
                    mediaFile.setTitle(processCommand(intent).getTitle());

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
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        updateProgress();
                    }
                }
            }).start();
        }

        Log.d("Service onStartCommand", "onStartCommand, " + processCommand(intent));

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //서비스가 종료될 때 실행
        mp.stop(); //음악 종료
        if (notificationMediaStyle!=null){
            notificationMediaStyle.finishNotification(getApplication());
            notificationMediaStyle=null;
        }

        // 강제종료시 다시 서비스 시작
        Intent intent = new Intent(getApplicationContext(), MusicService.class);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, pendingIntent);


        Log.d("Service onDestroy", "onDestroy");

    }

    private MediaFile processCommand(Intent intent) {

        String data = intent.getStringExtra("data");
        String title = intent.getStringExtra("title");
        String artist = intent.getStringExtra("artist");

        if (items.size()==0) items.add(new MediaFile(data, artist, title, ""));
        else items.set(0, new MediaFile(data, artist, title, ""));


        Intent showIntent = new Intent(getApplicationContext(), MainActivity.class);

        showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //showIntent.putExtra("data", command);
        startActivity(showIntent); // Service에서 Activity로 데이터를 전달

        return items.get(0);
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
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    updateProgress();
                }
            }
        }).start();

        sendBroadcast(new Intent("PLAY"));
        notificationMediaStyle.craeteNotification(this, mediaFile.getArtist(), mediaFile.getTitle(), 0);
        return "Start";
    }

    public String musicPause(){
        mp.pause();

        sendBroadcast(new Intent("PAUSE"));
        notificationMediaStyle.craeteNotification(this, mediaFile.getArtist(), mediaFile.getTitle(), 1);

        return "Pause";
    }

}




















