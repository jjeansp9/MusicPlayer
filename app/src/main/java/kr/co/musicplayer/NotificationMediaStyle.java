package kr.co.musicplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import kr.co.musicplayer.activities.MainActivity;
import kr.co.musicplayer.model.MediaFile;

public class NotificationMediaStyle {

    public void craeteNotification(Context context, String artist, String title){

        Log.e("???", artist+ title);

        // 운영체제로부터 알림(Notification)을 관리하는 관리자 객체 소환
        NotificationManager notificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Notification 객체를 생성해주는 Builder 객체 생성
        NotificationCompat.Builder builder= null;

        Log.e("getSystem", context.getSystemService(Context.NOTIFICATION_SERVICE)+"");

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O){ // 디바이스 버전이 26버전(Oreo버전) 이상이라면

            // 알림채널 객체 생성
            NotificationChannel channel= new NotificationChannel("ch1", "My channel", NotificationManager.IMPORTANCE_LOW);
            channel.setShowBadge(false);


            // 알림매니저에게 위 알림채널객체를 시스템에서 인식하도록 생성
            notificationManager.createNotificationChannel(channel);

            builder= new NotificationCompat.Builder(context, "ch1");
        }else{ // 디바이스버전이 26버전 이상이 아니라면
            builder= new NotificationCompat.Builder(context, "");
        }

        // 알림창을 클릭했을 때 실행될 작업( 새로운 화면[SecondActivity] ) 실행 설정
        Intent intent= new Intent(context, MainActivity.class);

        PendingIntent pendingIntent= PendingIntent.getActivity(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);

        Intent prevIntent = new Intent(context, MusicService.class);
        prevIntent.setAction("previous");
        //prevIntent.putExtra("music", "previous");
        Intent playPauseIntent = new Intent(context, MusicService.class);
        playPauseIntent.putExtra("music", "play");
        Intent nextIntent = new Intent(context, MusicService.class);
        nextIntent.putExtra("music", "next");

        // 이전, 재생/일시정지, 다음 액션 PendingIntent 생성
        PendingIntent prevPendingIntent = PendingIntent.getService(context, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        PendingIntent playPausePendingIntent = PendingIntent.getService(context, 0, playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        PendingIntent nextPendingIntent = PendingIntent.getService(context, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);

        //Bitmap bm= BitmapFactory.decodeResource(getResources(), R.drawable.newyork);

        builder.setSmallIcon(R.drawable.ic_baseline_music_note_24)
                .setContentTitle(title)
                .setContentText(artist)
                .addAction(R.drawable.ic_baseline_fast_rewind_24, "", prevPendingIntent) // #0
                .addAction(R.drawable.ic_baseline_play_arrow_24, "Play/Pause", playPausePendingIntent)  // #1
                .addAction(R.drawable.ic_baseline_fast_forward_24, "Next", nextPendingIntent)  // #2
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0,1,2));


        Notification notification= builder.build();

        // 매니저에게 알림(Notification)을 요청
        notificationManager.notify(1, notification);
    }
}