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

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import kr.co.musicplayer.activities.MainActivity;
import kr.co.musicplayer.fragments.MusicInfoFragment;

public class MyBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("MyBroadcast","onReceive > "+ (TextUtils.isEmpty(intent.getAction())?"nonAction":intent.getAction()));
        MainActivity mainActivity = (MainActivity) context; // MainActivity 인스턴스 가져오기

        if (intent.getAction()!= null){
            if (intent.getAction().equals("PLAY")){


                // View 조작하기
                mainActivity.findViewById(R.id.play).setVisibility(View.INVISIBLE);
                mainActivity.findViewById(R.id.pause).setVisibility(View.VISIBLE);

            }else if (intent.getAction().equals("PAUSE")){

                // View 조작하기
                mainActivity.findViewById(R.id.play).setVisibility(View.VISIBLE);
                mainActivity.findViewById(R.id.pause).setVisibility(View.INVISIBLE);

            }else if(intent.getAction().equals("PREVIOUS")){
                mainActivity.playPreviousMusic();

            }else if (intent.getAction().equals("NEXT")){
                mainActivity.playNextMusic();
            }
        }

    }
}






















