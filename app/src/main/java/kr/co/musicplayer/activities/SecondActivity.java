package kr.co.musicplayer.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import kr.co.musicplayer.MusicService;
import kr.co.musicplayer.R;

public class SecondActivity extends AppCompatActivity {
    MusicService musicService;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        if(musicService==null){
            intent = new Intent(getApplicationContext(), MusicService.class);
            // MyService를 시작하기
//            intent.putExtra("data", item.getData());
//            intent.putExtra("title", item.getTitle());
//            intent.putExtra("artist", item.getArtist());
//            intent.putExtra("uri", item.getUri());
            startService(intent);

            // MyService와 연결(Bind)
            bindService(intent, connection, 0); // 해당코드 입력시 MyService.java에 onBind() 실행
        }
    }

    // MusicService와 연결
    ServiceConnection connection= new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.MyBinder binder= (MusicService.MyBinder) iBinder;
            musicService= binder.getMyServiceAddress();

            Log.i("onServiceConnected", "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
}