package kr.co.musicplayer.activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.IOException;

public class MusicService extends Service {

    MediaPlayer mp= new MediaPlayer();

    @Override
    public void onCreate() {
        //서비스에서 가장 먼저 호출(최초한번)
        //mp.setLooping(false); // 반복재생

        Log.d("Service onCreate", "onCreate");
        super.onCreate();
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //서비스가 실행될 때 실행
        if (intent == null) return Service.START_STICKY;
        else processCommand(intent);

        try {
            mp.reset();
            mp.setDataSource(processCommand(intent));
            mp.prepare();
            mp.start();

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

        Log.d("Service onStartCommand", "onStartCommand, " + processCommand(intent));

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        //서비스가 종료될 때 실행
        mp.stop(); //음악 종료

        Log.d("Service onDestroy", "onDestroy");
        super.onDestroy();
    }

    private String processCommand(Intent intent) {
        String command = intent.getStringExtra("data");

        Intent showIntent = new Intent(getApplicationContext(), MainActivity.class);

        showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TOP);

        showIntent.putExtra("data", command);
        startActivity(showIntent); // Service에서 Activity로 데이터를 전달

        return command;
    }

    // bindService()를 실행할 때 자동 발동하는 메소드
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return new MyBinder();
    }

    class MyBinder extends Binder {
        // 이 MyService 객체의 주소값을 리턴해주는 기능함수
        MusicService getMyServiceAddress(){
            return MusicService.this;
        }
    }

    protected void musicStart(){
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
    }

    protected void musicPause(){
        mp.pause();
    }

}
