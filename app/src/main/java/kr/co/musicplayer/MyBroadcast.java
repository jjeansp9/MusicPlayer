package kr.co.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("PLAY")){
            Toast.makeText(context, "play", Toast.LENGTH_SHORT).show();


        }else if (intent.getAction().equals("PAUSE")){
            Toast.makeText(context, "pause", Toast.LENGTH_SHORT).show();
        }


    }


}
