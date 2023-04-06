package kr.co.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import kr.co.musicplayer.activities.MainActivity;

public class MyBroadcast extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("MyBroadcast","onReceive > "+ (TextUtils.isEmpty(intent.getAction())?"nonAction":intent.getAction()));

        if (intent.getAction()!= null){
            if (intent.getAction().equals("PLAY")){

                Toast.makeText(context, "play", Toast.LENGTH_SHORT).show();

//                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                RelativeLayout layout= (RelativeLayout) vi.inflate(R.layout.activity_main, null);
//
//                layout.findViewById(R.id.play).setVisibility(View.VISIBLE);
//                layout.findViewById(R.id.pause).setVisibility(View.INVISIBLE);

            }else if (intent.getAction().equals("PAUSE")){

                Toast.makeText(context, "pause", Toast.LENGTH_SHORT).show();

//                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                RelativeLayout layout= (RelativeLayout) vi.inflate(R.layout.activity_main, null);
//
//                layout.findViewById(R.id.play).setVisibility(View.INVISIBLE);
//                layout.findViewById(R.id.pause).setVisibility(View.VISIBLE);
            }
        }



    }


}
