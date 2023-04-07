package kr.co.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import kr.co.musicplayer.activities.MainActivity;
import kr.co.musicplayer.model.MediaFile;

public class MusicService extends Service {

    private MediaPlayer mp= new MediaPlayer();
    private NotificationMediaStyle notificationMediaStyle;

    ArrayList<MediaFile> items= new ArrayList<>();
    MediaFile mediaFile= new MediaFile("", "", "", "");

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

        if (intent.getAction()!= null){
            Log.i("getAction", intent.getAction());

            if (intent.getAction().equals(NotificationMediaStyle.ACTION_PLAY)) musicStart();
            else if (intent.getAction().equals(NotificationMediaStyle.ACTION_PAUSE)) musicPause();

        }else{
            try {
                mp.reset();
                mp.setDataSource(processCommand(intent).getData());
                mp.prepare();
                mp.start();

                notificationMediaStyle.craeteNotification(this, processCommand(intent).getArtist(), processCommand(intent).getTitle(), 0);

                mediaFile.setArtist(processCommand(intent).getArtist());
                mediaFile.setTitle(processCommand(intent).getTitle());


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
                    }
                }
            }).start();
        }

        //서비스가 실행될 때 실행
//        if (intent == null) return Service.START_STICKY;
//        else processCommand(intent);
//

        Log.d("Service onStartCommand", "onStartCommand, " + processCommand(intent));

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        //서비스가 종료될 때 실행
        mp.stop(); //음악 종료
        notificationMediaStyle.finishNotification(this);

        Log.d("Service onDestroy", "onDestroy");
        super.onDestroy();
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

//        String pause = intent.getStringExtra("pause");
//        Log.i("pauses", pause);

        return items.get(0);
    }

    // bindService()를 실행할 때 자동 발동하는 메소드
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return new MyBinder();
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

    public void musicPrevious(){

    }

    public void musicNext(){

    }

}




















