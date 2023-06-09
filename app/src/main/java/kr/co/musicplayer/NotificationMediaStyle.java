package kr.co.musicplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import java.io.FileNotFoundException;
import java.io.IOException;

import kr.co.musicplayer.activities.MainActivity;
import kr.co.musicplayer.model.MediaFile;

public class NotificationMediaStyle {
    NotificationCompat.Builder builder= null;
    NotificationManager notificationManager;
    NotificationChannel channel;

    public static String ACTION_PLAY="PLAY";
    public static String ACTION_PAUSE="PAUSE";
    public static String ACTION_PREVIOUS="PREVIOUS";
    public static String ACTION_NEXT="NEXT";

    protected void craeteNotification(Context context, String artist, String title, long uri, int num){

        notificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O){ // 디바이스 버전이 26버전(Oreo버전) 이상이라면

            // 알림채널 객체 생성
            channel= new NotificationChannel("ch1", "My channel", NotificationManager.IMPORTANCE_LOW);
            channel.setShowBadge(false);

            notificationManager.createNotificationChannel(channel);
            builder= new NotificationCompat.Builder(context, "ch1");
        }else{ // 디바이스버전이 26버전 이상이 아니라면
            builder= new NotificationCompat.Builder(context, "");
        }
        Intent prevIntent = new Intent(context, MusicService.class);
        prevIntent.setAction(ACTION_PREVIOUS);

        Intent playIntent = new Intent(context, MusicService.class);
        playIntent.setAction(ACTION_PLAY);

        Intent pauseIntent = new Intent(context, MusicService.class);
        pauseIntent.setAction(ACTION_PAUSE);

        Intent nextIntent = new Intent(context, MusicService.class);
        nextIntent.setAction(ACTION_NEXT);

        // 이전, 재생/일시정지, 다음 액션 PendingIntent 생성
        PendingIntent prevPendingIntent = PendingIntent.getService(context, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        PendingIntent playPendingIntent = PendingIntent.getService(context, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        PendingIntent pausePendingIntent = PendingIntent.getService(context, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        PendingIntent nextPendingIntent = PendingIntent.getService(context, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);

        Uri albumArtUri = Uri.parse("content://media/external/audio/albumart/" + uri);
        Bitmap albumArt = null;

        try {
            albumArt = MediaStore.Images.Media.getBitmap(context.getContentResolver(), albumArtUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        builder.setSmallIcon(R.drawable.ic_baseline_music_note_24)
                .setLargeIcon(albumArt)
                .setContentTitle(title)
                .setContentText(artist)
                .addAction(R.drawable.ic_baseline_fast_rewind_24, "", prevPendingIntent) // #0
                .addAction(R.drawable.ic_baseline_play_arrow_24, "Play", playPendingIntent)  // #1
                .addAction(R.drawable.ic_baseline_pause_24, "pause", pausePendingIntent)  // #2
                .addAction(R.drawable.ic_baseline_fast_forward_24, "Next", nextPendingIntent);  // #3

        if (num == 0) {
            builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0,2,3));

        }else if (num == 1){
            builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0,1,3));
        }

        Notification notification= builder.build();

        // 매니저에게 알림(Notification)을 요청
        notificationManager.notify(1, notification);
    }

    // notification 종료
    protected void finishNotification(Context context){
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

}





















