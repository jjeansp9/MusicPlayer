package kr.co.musicplayer.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.IOException;

import kr.co.musicplayer.LogMgr;

public class MusicServices extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener{

    private static final String TAG = "MusicService";

    public MediaPlayer mp= new MediaPlayer();
    //private NotificationMediaStyle notificationMediaStyle;

    public Messenger mMessenger = new Messenger(new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
//            MusicData data = null;
//            switch(msg.what){
//                case Defines.MSG_REGISTER_MUSIC_CLIENT:
//                    LogMgr.d(TAG, "event MSG_REGISTER_MUSIC_CLIENT");
//                    break;
//
//                case Defines.MSG_MUSIC_PLAY:
//                    data = (MusicData) msg.obj;
//                    if (data != null) {
//                        LogMgr.d(TAG, "event MSG_MUSIC_PLAY data : " + data);
//                        musicStart(data);
//                    }
//                    break;
//
//                case Defines.MSG_MUSIC_PAUSE:
//
//                    if (data != null) {
//                        musicPause(data);
//                    }
//
//                    break;
//            }
        }
    });

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

//            if (intent.getAction()!= null){
//                if (!intent.hasExtra(Constants.MUSIC_DATA)) return;
//                MusicData item = Utils.getParcelableExtra(intent, Constants.MUSIC_DATA, MusicData.class);
//                if (intent.getAction().equals(Constants.MUSIC_PLAY)){
//                    LogMgr.e(TAG, "received MUSIC_PLAY");
//                    musicStart(item);
//
//                }else if (intent.getAction().equals(Constants.MUSIC_PAUSE)){
//                    LogMgr.e(TAG, "received MUSIC_PAUSE");
//                    musicPause(item);
//
//                }else if(intent.getAction().equals(Constants.MUSIC_NEXT)){
//                    LogMgr.e(TAG, "received MUSIC_NEXT");
//
//                }else if (intent.getAction().equals(Constants.MUSIC_PREVIOUS)){
//                    LogMgr.e(TAG, "received MUSIC_PREVIOUS");
//                }
//            }
        }
    };

    // 가장먼저 호출 (최초 한번)
    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction("PLAY");
        filter.addAction("PAUSE");
        filter.addAction("PREVIOUS");
        filter.addAction("NEXT");
        registerReceiver(mReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogMgr.d(TAG, "onStartCommand");
        if (intent == null) {
            //musicStop();
            return START_NOT_STICKY;
        }

        //if (!intent.hasExtra(Constants.MUSIC_DATA)) return START_NOT_STICKY;
        //MusicData item = Utils.getParcelableExtra(intent, Constants.MUSIC_DATA, MusicData.class);

//        if (intent.hasExtra(Constants.MUSIC_DATA)) {
//            MusicData fromNoti = Utils.getParcelableExtra(intent, Constants.MUSIC_DATA, MusicData.class);
//
//            if (intent.getAction()!=null && intent.getAction().equals(Constants.MUSIC_PLAY)) musicPause(fromNoti);
//            else if (intent.getAction()!=null && intent.getAction().equals(Constants.MUSIC_PAUSE)) musicStart(fromNoti);
//            else if (intent.getAction()!=null && intent.getAction().equals(Constants.MUSIC_PREVIOUS)) sendBroadcast(new Intent(Constants.MUSIC_PREVIOUS));
//            else if (intent.getAction()!=null && intent.getAction().equals(Constants.MUSIC_NEXT)) sendBroadcast(new Intent(Constants.MUSIC_NEXT));
//
//        } else {
//
//        }

//        if (intent.getAction() != null) {
//            switch (intent.getAction()) {
//                case Constants.MUSIC_PLAY:
//                    musicPause(item);
//                    break;
//                case Constants.MUSIC_PAUSE:
//                    musicStart(item);
//                    break;
//                case Constants.MUSIC_PREVIOUS:
//                    break;
//                case Constants.MUSIC_NEXT:
//                    break;
//            }
//
//        } else {
//            if (item != null) {
//
//                LogMgr.d(TAG, "onStartCommand Data : " + item);
//                setMusic(item);
//                mp.start();
//
//                NotificationCompat.Builder notification = null;
//                if (notificationMediaStyle==null){
//                    notificationMediaStyle = new NotificationMediaStyle();
//                    notification = notificationMediaStyle.createNotification(this, item, Constants.MUSIC_PLAY);
//                }else{
//                    notification = notificationMediaStyle.createNotification(this, item, Constants.MUSIC_PLAY);
//                }
//
//                if (notification != null) startForeground(1, notification.build());
//
//            } else {
//                LogMgr.d(TAG, "onStartCommand Data is null");
//            }
//        }

        return START_STICKY;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    private String musicName = "";

//    private void musicStart(MusicData item) {
//        if (mp == null) return;
//        if (item.fileName != null && !item.fileName.isEmpty()) {
//            if (!musicName.equals(item.fileName)) {
//                setMusic(item);
//
//                NotificationCompat.Builder notification = null;
//                if (notificationMediaStyle==null){
//                    notificationMediaStyle = new NotificationMediaStyle();
//                    notification = notificationMediaStyle.createNotification(this, item, Constants.MUSIC_PLAY);
//                }else{
//                    notification = notificationMediaStyle.createNotification(this, item, Constants.MUSIC_PLAY);
//                }
//
//                if (notification != null) startForeground(1, notification.build());
//
//            } else {
//                notificationMediaStyle.createNotification(this, item, Constants.MUSIC_PLAY);
//            }
//        }
//
//        mp.start();
//        LogMgr.e(TAG, "musicStart() play success");
//    }
//
//    private void musicPause(MusicData item) {
//        if (mp == null) return;
//        if (mp.isPlaying()) {
//            mp.pause();
//            notificationMediaStyle.createNotification(this, item, Constants.MUSIC_PAUSE);
//            LogMgr.e(TAG, "musicStart() pause success");
//        }
//    }
//
//    private void musicStop() {
//
//    }
//
//    private void setMusic(MusicData item) {
//        if (mp.isPlaying()) mp.stop();
//        try {
//            File getFolder = FileUtils.getPathFromInternal(this, item.path);
//            if (getFolder == null) {
//                Toast.makeText(this, R.string.music_folder_empty, Toast.LENGTH_SHORT).show();
//            } else {
//                LogMgr.e(TAG, "onStartCommand getFolder is not null");
//            }
//            File musicFile = new File(getFolder, item.fileName);
//            musicName = item.fileName;
//            if (musicFile.exists()) {
//                LogMgr.e(TAG, "onStartCommand File : " + musicFile.getAbsolutePath());
//                mp.reset();
//                mp.setDataSource(musicFile.getAbsolutePath());
//                mp.prepare();
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    static class NotificationMediaStyle {
//        NotificationCompat.Builder builder= null;
//        NotificationManager notificationManager;
//
//        protected NotificationCompat.Builder createNotification(Context context, MusicData item, String type) {
//
//            PlaybackStateCompat.Builder mStateBuilder = new PlaybackStateCompat.Builder()
//                    .setActions(
//                            PlaybackStateCompat.ACTION_PLAY |
//                                    PlaybackStateCompat.ACTION_PAUSE |
//                                    PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
//                                    PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
//                    )
//                    .setState(PlaybackStateCompat.STATE_PLAYING, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1.0f);
//            MediaMetadataCompat.Builder metadataBuilder = new MediaMetadataCompat.Builder()
//                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "제목")
//                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "아티스트")
//                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "앨범")
//                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 300000); // 재생 시간 (예: 300000ms = 5분)
//
//            MediaSessionCompat mediaSession = new MediaSessionCompat(context, "TAG");
//
//            mediaSession.setMetadata(metadataBuilder.build());
//            mediaSession.setPlaybackState(mStateBuilder.build());
//
//            notificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//            final String CHANNELID = context.getString(R.string.music_service);
//            NotificationChannel channel = new NotificationChannel(
//                    CHANNELID,
//                    CHANNELID,
//                    NotificationManager.IMPORTANCE_LOW);
//            channel.setShowBadge(false); // noti 생성할 때 앱에 푸쉬알림 배지 1 등의 표시 하지않음
//
//            notificationManager.createNotificationChannel(channel);
//            builder= new NotificationCompat.Builder(context, CHANNELID);
//
//            Intent prevIntent = new Intent(context, MusicService.class);
//            prevIntent.setAction(Constants.MUSIC_PREVIOUS);
//
//            Intent playIntent = new Intent(context, MusicService.class);
//            playIntent.putExtra(Constants.MUSIC_DATA, item);
//            playIntent.setAction(type);
//
//            Intent pauseIntent = new Intent(context, MusicService.class);
//            pauseIntent.putExtra(Constants.MUSIC_DATA, item);
//            pauseIntent.setAction(type);
//
//            Intent nextIntent = new Intent(context, MusicService.class);
//            nextIntent.setAction(Constants.MUSIC_NEXT);
//
//            PendingIntent prevPendingIntent = PendingIntent.getService(context, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
//            PendingIntent playPendingIntent = PendingIntent.getService(context, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
//            //PendingIntent pausePendingIntent = PendingIntent.getService(context, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
//            PendingIntent nextPendingIntent = PendingIntent.getService(context, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
//
//            int resId = 0;
//            if (type == Constants.MUSIC_PLAY) resId = R.drawable.ic_pause;
//            else resId = R.drawable.ic_play;
//
//            builder.setContentTitle(item.title)
//                    .setContentText(item.singer)
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .addAction(R.drawable.ic_previous, Constants.MUSIC_PREVIOUS, prevPendingIntent)
//                    .addAction(resId, type, playPendingIntent)
//                    //.addAction(R.drawable.ic_pause, Constants.MUSIC_PAUSE, pausePendingIntent)
//                    .addAction(R.drawable.ic_next, Constants.MUSIC_NEXT, nextPendingIntent)
//                    .setAllowSystemGeneratedContextualActions(false);
//
//            builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
//                    .setMediaSession(mediaSession.getSessionToken())
//                    .setShowActionsInCompactView(0,1,2));
//
//            Notification notification= builder.build();
//            notificationManager.notify(1, notification);
//            return builder;
//        }
//
//        // notification 종료
//        protected void finishNotification(Context context){
//            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//            notificationManager.cancelAll();
//            notificationManager = null;
//        }
//    }
}
