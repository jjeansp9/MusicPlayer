package kr.co.musicplayer.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.ArrayList;

import kr.co.musicplayer.MusicService;
import kr.co.musicplayer.NotificationMediaStyle;
import kr.co.musicplayer.R;
import kr.co.musicplayer.databinding.ActivityMainBinding;
import kr.co.musicplayer.fragments.MusicInfoFragment;
import kr.co.musicplayer.fragments.MusicListFragment;
import kr.co.musicplayer.User;
import kr.co.musicplayer.model.MediaFile;
import kr.co.musicplayer.model.OnDataPass;

public class MainActivity extends AppCompatActivity implements OnDataPass {

    private ActivityMainBinding binding;

    private TextView userName;
    private ImageView userImage;
    private TextView userEmail;

    //    private DrawerLayout drawerLayout;
//    View navBar;

    private ArrayList<Fragment> fragments= new ArrayList<>();
    private FragmentManager fragmentManager= null;
    private boolean[] result= {false,false};

    private User user= new User();

    MusicService musicService;
    Intent intent;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        intent = new Intent(getApplicationContext(), MusicService.class);

        getUserData();

        createFragment();



//        userName= findViewById(R.id.user_name);
//        userName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                userName.setText("ffff");
//            }
//        });

        binding.list.setOnClickListener(v->clickedFragment(0));
        binding.info.setOnClickListener(v->clickedFragment(1));

        //seekBar();

        binding.play.setOnClickListener(v -> musicPlay()); // 음악 재생
        binding.pause.setOnClickListener(v -> musicPause()); // 음악 일시정지

        binding.playPrevious.setOnClickListener(v -> playPreviousMusic());
        binding.playNext.setOnClickListener(v -> playNextMusic());




        // 프래그먼트 추가
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.add(R.id.fragment, new MusicListFragment());
//        fragmentTransaction.add(R.id.fragment, new MusicInfoFragment());
//        fragmentTransaction.commit();



    } // onCreate()

    @Override
    public void onBackPressed() {
        // 뒤로 가기 버튼 이벤트 처리
        // 홈 버튼 기능 수행
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    // 플레이중인 음악의 이전 음악 플레이하기
    private void playPreviousMusic(){

        // 프래그먼트로 데이터 전달
        MusicListFragment myFragment = MusicListFragment.newInstance("position", position);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, myFragment, "position")
                .commit();
    }

    // 플레이중인 음악의 다음 음악 플레이하기
    private void playNextMusic(){

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


    // Service에 데이터 보내기
    private void putDataToService(MediaFile item){

        if(musicService==null){
            // MyService를 시작하기
            intent.putExtra("data", item.getData());
            startService(intent);

            // MyService와 연결(Bind)
            bindService(intent, connection, 0); // 해당코드 입력시 MyService.java에 onBind() 실행
        }else{
            intent.putExtra("data", item.getData());
            startService(intent);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {

        //getDataFromService(intent);

        super.onNewIntent(intent);
    }

//    // Service에서 보낸 데이터 받기
//    private void getDataFromService(Intent intent) {
//
//        String command="";
//        if (intent != null) command = intent.getStringExtra("data");
//
//        try {
//            mp.setDataSource(command);
//            mp.pause();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        binding.play.setVisibility(View.VISIBLE);
//        binding.pause.setVisibility(View.INVISIBLE);
//    }


    // 음악 재생
    private void musicPlay(){
//        NotificationMediaStyle notificationMediaStyle= new NotificationMediaStyle();
//        notificationMediaStyle.craeteNotification(this);

        craeteNotification(this);


        if (musicService!=null){
            musicService.musicStart();

            binding.play.setVisibility(View.INVISIBLE);
            binding.pause.setVisibility(View.VISIBLE);

        }else{
            Toast.makeText(this, "플레이 할 음악을 선택해주세요", Toast.LENGTH_SHORT).show();
        }

    }

    // 음악 일시정지
    private void musicPause(){

        if (musicService!=null){
            musicService.musicPause();
            binding.play.setVisibility(View.VISIBLE);
            binding.pause.setVisibility(View.INVISIBLE);
        }

    }


    // Fragment에서 넘긴 데이터 받아오는 메소드
    @Override
    public void onDataPass(MediaFile item, int position) {

        putDataToService(item);
        this.position = position;

        binding.play.setVisibility(View.INVISIBLE);
        binding.pause.setVisibility(View.VISIBLE);

    }

    private void getUserData(){

        // 구글로 로그인 한 회원의 정보 가져오기
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            user.setName(acct.getDisplayName());
            user.setEmail(acct.getEmail());
            user.setId(acct.getId());
            user.setImage(acct.getPhotoUrl()+"");
        }

        userName= findViewById(R.id.user_name);
        userImage= findViewById(R.id.user_profile_image);
        userEmail= findViewById(R.id.user_email);

        if (user.getImage().equals(""))Glide.with(this).load(user.getImage()).into(userImage);
        else Glide.with(this).load(R.drawable.ic_baseline_account_circle_24).into(userImage);


        userName.setText(user.getName());
        userEmail.setText(user.getEmail());
    }

//    private void seekBar(){
//        mp= MediaPlayer.create(this, R.raw.beethoven_piano_sonata_01);
//
//        binding.seekBar.setVisibility(ProgressBar.VISIBLE);
//        binding.seekBar.setMax(mp.getDuration());
//        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (fromUser){
//                    mp.seekTo(progress);
//                }
//                int m= progress / 60000;
//                int s= (progress % 60000) / 1000;
//                String strTime = String.format("%02d:%02d", m, s);
//                binding.text.setText(strTime);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//            }
//        });
//    }

    // 음악 재생
//    private void musicPlay(){
//        mp.start();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (mp.isPlaying()){
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    binding.seekBar.setProgress(mp.getCurrentPosition());
//                }
//            }
//        }).start();
//        binding.play.setVisibility(View.INVISIBLE);
//        binding.pause.setVisibility(View.VISIBLE);
//
//        int m= mp.getDuration() / 60000;
//        int s= (mp.getDuration() % 60000) / 1000;
//        String strTime = String.format("%02d:%02d", m, s);
//
//        binding.textMax.setText(strTime);
//    }

    private void createFragment(){
        fragments.add(0, new MusicListFragment());
        fragments.add(1, new MusicInfoFragment());

        fragmentManager= getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.fragment_container, fragments.get(0)).commit();
        result[0]= true;
    }

    private void clickedFragment(int num){

        if (num==0){
            binding.info.setVisibility(View.VISIBLE);
            binding.list.setVisibility(View.INVISIBLE);
        }else{
            binding.info.setVisibility(View.INVISIBLE);
            binding.list.setVisibility(View.VISIBLE);
        }

        FragmentTransaction tran= fragmentManager.beginTransaction();

        if (!result[1]){
            tran.add(R.id.fragment_container, fragments.get(1));
            result[1] = true;
        }

        for (int i=0; i<fragments.size(); i++){
            if (fragments.get(i)!=null){ tran.hide(fragments.get(i)); }
        }

        tran.show(fragments.get(num)).commit();
    }

    // TODO notification mediastyle, BroadcastReciver 진행

    public void craeteNotification(Context context){

        // 운영체제로부터 알림(Notification)을 관리하는 관리자 객체 소환
        NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Notification 객체를 생성해주는 Builder 객체 생성
        NotificationCompat.Builder builder= null;

        Log.e("getSystem", context.getSystemService(Context.NOTIFICATION_SERVICE)+", "+getSystemService(Context.NOTIFICATION_SERVICE));

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O){ // 디바이스 버전이 26버전(Oreo버전) 이상이라면

            // 알림채널 객체 생성
            NotificationChannel channel= new NotificationChannel("ch1", "My channel", NotificationManager.IMPORTANCE_HIGH);

            // 알림매니저에게 위 알림채널객체를 시스템에서 인식하도록 생성
            notificationManager.createNotificationChannel(channel);

            builder= new NotificationCompat.Builder(context, "");
        }else{ // 디바이스버전이 26버전 이상이 아니라면
            builder= new NotificationCompat.Builder(context, "");
        }

        // 알림창을 클릭했을 때 실행될 작업( 새로운 화면[SecondActivity] ) 실행 설정
        Intent intent= new Intent(context, MainActivity.class);
//
//        // 지금 당장 Intent가 화면을 실행하는 것이 아니기에 잠시 보류시켜야 함
//        // 보류중인 Intent로 만들어야 함
        PendingIntent pendingIntent= PendingIntent.getActivity(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);

        Intent prevIntent = new Intent(context, MusicService.class);

        Intent playPauseIntent = new Intent(context, MusicService.class);
        playPauseIntent.putExtra("pause", "pause");

        Intent nextIntent = new Intent(context, MusicService.class);


        // 이전, 재생/일시정지, 다음 액션 PendingIntent 생성
        PendingIntent prevPendingIntent = PendingIntent.getService(context, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        PendingIntent playPausePendingIntent = PendingIntent.getService(context, 0, playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        PendingIntent nextPendingIntent = PendingIntent.getService(context, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);


        //Bitmap bm= BitmapFactory.decodeResource(getResources(), R.drawable.newyork);

        builder.setSmallIcon(R.drawable.ic_baseline_music_note_24)
                .setContentTitle("제목")
                .setContentText("아티스트 - 앨범")
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


















