package kr.co.musicplayer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.kakao.sdk.user.UserApiClient;
import com.navercorp.nid.oauth.NidOAuthLogin;
import com.navercorp.nid.oauth.OAuthLoginCallback;
import com.navercorp.nid.profile.NidProfileCallback;
import com.navercorp.nid.profile.data.NidProfileResponse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kr.co.musicplayer.MusicService;
import kr.co.musicplayer.MyBroadcast;
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

    private MediaFile mMediaFile= new MediaFile("","","","", R.drawable.ic_baseline_image_24);
    private ArrayList<MediaFile> items= new ArrayList<>();

    //    private DrawerLayout drawerLayout;
//    View navBar;

    private NidOAuthLogin nidOAuthLogin= new NidOAuthLogin();

    private User users= new User();

    MusicService musicService;
    Intent intent;
    int position= 0;
    int musicNumber;
    int getFragmentNum=0;

    private MyBroadcast myBroadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] loadUserInfo= users.loadUserId(this); // 디바이스에 저장된 유저데이터 가져오기

        binding= ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        getUserData(loadUserInfo);

        myBroadcast= new MyBroadcast();

        userName= findViewById(R.id.user_name);
        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userName.setText("ffff");
            }
        });

        showFragment(MusicListFragment.newInstance("position", 20), 0, position);

        findViewById(R.id.un_link).setOnClickListener(v -> unLinkUser(loadUserInfo)); // 회원탈퇴 버튼


        // 음악리스트 화면으로 이동
        binding.list.setOnClickListener(v-> showFragment(MusicListFragment.newInstance("position", 20), 0, position));



        // 플레이중인 음악 정보를 보는 화면으로 이동
        binding.musicImage.setOnClickListener(v-> {
            if (musicService!= null){
                showFragment(MusicInfoFragment.newInstance(
                        "position", mMediaFile.getArtist(),
                        mMediaFile.getTitle(),
                        musicService.mp.getDuration(),
                        musicService.mp.getCurrentPosition(),
                        musicNumber, items, position), 1, position);

            }else {
                Toast.makeText(this, "플레이 할 음악을 선택해주세요", Toast.LENGTH_SHORT).show();
            }});



        binding.play.setOnClickListener(v -> musicPlay()); // 음악 재생
        binding.pause.setOnClickListener(v -> musicPause()); // 음악 일시정지
        binding.playPrevious.setOnClickListener(v -> playPreviousMusic()); // 이전 음악 플레이
        binding.playNext.setOnClickListener(v -> playNextMusic()); // 다음 음악 플레이

        registerBroadcast();

    } // onCreate()

    // Fragment에서 넘긴 데이터 받아오는 메소드
    @Override
    public void onDataPass(MediaFile item, int position, int itemsNumber, ArrayList<MediaFile> items) {

        mMediaFile = item;
        this.items= items;
        this.position = position;

        putDataToService(item);
        Log.i("MainActivity", "onDataPass() : " +position + ", items : " + items.size());

        if (items.size() != 0){
            Uri albumArtUri = Uri.parse("content://media/external/audio/albumart/" + items.get(position).getUri());
            Bitmap albumArt = null;

            try {
                albumArt = MediaStore.Images.Media.getBitmap(getContentResolver(), albumArtUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            binding.musicImage.setImageBitmap(albumArt);
        }

        binding.play.setVisibility(View.INVISIBLE);
        binding.pause.setVisibility(View.VISIBLE);

        binding.musicArtist.setText(item.getArtist());
        binding.musicTitle.setText(item.getTitle());

        musicNumber= itemsNumber;
    }

    // Service에 데이터 보내기
    private void putDataToService(MediaFile item){

        if(musicService==null){
            intent = new Intent(getApplicationContext(), MusicService.class);
            // MyService를 시작하기
            intent.putExtra("data", item.getData());
            intent.putExtra("title", item.getTitle());
            intent.putExtra("artist", item.getArtist());
            intent.putExtra("uri", item.getUri());
            startService(intent);

            // MyService와 연결(Bind)
            bindService(intent, connection, 0); // 해당코드 입력시 MyService.java에 onBind() 실행
        }else{
            intent.putExtra("data", item.getData());
            intent.putExtra("title", item.getTitle());
            intent.putExtra("artist", item.getArtist());
            intent.putExtra("uri", item.getUri());
            startService(intent);

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

    // 클릭한 버튼마다 각각의 프래그먼트 보여주기
    private void showFragment(Fragment fragment, int num, int position){

        if (num==0) {

            if (items.size() != 0){
                Uri albumArtUri = Uri.parse("content://media/external/audio/albumart/" + items.get(position).getUri());
                Bitmap albumArt = null;

                try {
                    albumArt = MediaStore.Images.Media.getBitmap(getContentResolver(), albumArtUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                binding.musicImage.setImageBitmap(albumArt);
            }else{
                binding.musicImage.setImageResource(R.drawable.ic_baseline_image_24);
            }

            binding.list.setVisibility(View.INVISIBLE);
            binding.musicImage.setVisibility(View.VISIBLE);
            getFragmentNum=0;

        }else{
            if (items.size() != 0){
                Uri albumArtUri = Uri.parse("content://media/external/audio/albumart/" + items.get(position).getUri());
                Bitmap albumArt = null;

                try {
                    albumArt = MediaStore.Images.Media.getBitmap(getContentResolver(), albumArtUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                binding.musicImage.setImageBitmap(albumArt);
            }else{
                binding.musicImage.setImageResource(R.drawable.ic_baseline_image_24);
            }

            binding.list.setVisibility(View.VISIBLE);
            binding.musicImage.setVisibility(View.INVISIBLE);
            getFragmentNum=1;

        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment, "position")
                .commit();



    }

    private void registerBroadcast() {

        IntentFilter filter = new IntentFilter();
        filter.addAction("PLAY");
        filter.addAction("PAUSE");
        filter.addAction("PREVIOUS");
        filter.addAction("NEXT");
        registerReceiver(myBroadcast, filter);
    }

    @Override
    public void onBackPressed() {
        // 뒤로 가기 버튼 이벤트 처리
        // 홈 버튼 기능 수행
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    @Override
    protected void onNewIntent(Intent intent) {

        //getDataFromService(intent);

        super.onNewIntent(intent);
    }


    // 음악 재생
    private void musicPlay(){

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

    // 유저정보를 불러오는 메소드
    private void getUserData(String[] loadUserInfo){

        if (loadUserInfo[0]!=null){

            if (loadUserInfo[1].equals("kakao")){
                UserApiClient.getInstance().me(new Function2<com.kakao.sdk.user.model.User, Throwable, Unit>() {
                    @Override
                    public Unit invoke(com.kakao.sdk.user.model.User user, Throwable throwable) {

                        if (user != null) {
                            setViewForUser(
                                    user.getKakaoAccount().getProfile().getNickname(),
                                    user.getKakaoAccount().getEmail(),
                                    user.getKakaoAccount().getProfile().getProfileImageUrl()+"");
                        }
                        return null;
                    }
                });

            }else if (loadUserInfo[1].equals("naver")){
                nidOAuthLogin.callProfileApi(new NidProfileCallback<NidProfileResponse>() {
                    @Override
                    public void onSuccess(NidProfileResponse nidProfileResponse) {
                        setViewForUser(
                                nidProfileResponse.getProfile().getNickname(),
                                nidProfileResponse.getProfile().getEmail(),
                                nidProfileResponse.getProfile().getProfileImage()
                                );
                    }

                    @Override
                    public void onFailure(int i, @NonNull String s) {
                    }

                    @Override
                    public void onError(int i, @NonNull String s) {
                        Toast.makeText(MainActivity.this, "onError", Toast.LENGTH_SHORT).show();
                    }
                });

            }else if (loadUserInfo[1].equals("google")){
                // 구글로 로그인 한 회원의 정보 가져오기
                GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

                if (acct != null) {
                    setViewForUser(
                            acct.getDisplayName(),
                            acct.getEmail(),
                            acct.getPhotoUrl()+"");
                }
            }
        } // if ID값이 있다면

    } // getUserData()


    // View들의 상태를 설정하는 메소드
    private void setViewForUser(String name, String email, String imageUrl){
        users.setName(name);
        users.setEmail(email);
        users.setImage(imageUrl);

        userName= findViewById(R.id.user_name);
        userImage= findViewById(R.id.user_profile_image);
        userEmail= findViewById(R.id.user_email);

        if (users.getImage().equals("") || users.getImage().equals("null")) Glide.with(this).load(R.drawable.ic_baseline_account_circle_24).into(userImage);

        else Glide.with(this).load(imageUrl).into(userImage);

        userName.setText(users.getName());
        userEmail.setText(users.getEmail());
    }

    private void unLinkUser(String[] loadUserInfo){


        if (loadUserInfo[1].equals("kakao")){ // 카카오 탈퇴
            UserApiClient.getInstance().unlink(new Function1<Throwable, Unit>() {
                @Override
                public Unit invoke(Throwable throwable) {

                    if (throwable != null){

                    }else{
                        users.removeUserId(MainActivity.this);
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                        setViewForUser("", "", "");
                    }
                    return null;
                }
            });

        }else if (loadUserInfo[1].equals("naver")){ // 네이버 탈퇴
            nidOAuthLogin.callDeleteTokenApi(this, new OAuthLoginCallback() {
                @Override
                public void onSuccess() {
                    users.removeUserId(MainActivity.this);
                    //서버에서 토큰 삭제에 성공한 상태입니다.
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                    setViewForUser("", "", "");
                }

                @Override
                public void onFailure(int i, @NonNull String s) {
                    // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
                    // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.

                }

                @Override
                public void onError(int i, @NonNull String s) {
                    // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
                    // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
                    Toast.makeText(MainActivity.this, "회원탈퇴 에러", Toast.LENGTH_SHORT).show();
                }
            });

        }else if (loadUserInfo[1].equals("google")){ // 구글 탈퇴
            GoogleSignInClient mGoogleSignInClient;

            GoogleSignInOptions gso= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            mGoogleSignInClient= GoogleSignIn.getClient(this, gso);

            mGoogleSignInClient.revokeAccess()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            users.removeUserId(MainActivity.this);
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();
                            setViewForUser("", "", "");
                        }
                    });
            users.setAccount("");
            users.setId("");
            users.setEmail("");
            users.setName("");
            users.setImage("");
        }

        Toast.makeText(MainActivity.this, "회원탈퇴 성공", Toast.LENGTH_SHORT).show();
    }

    // 플레이중인 음악의 이전 음악 플레이하기
    public void playPreviousMusic(){
        Log.i("MainActivity", "playPreviousMusic() : " +position + ", " + musicNumber);

        if (getFragmentNum==0){
            MusicListFragment musicListFragment= (MusicListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (musicListFragment!=null){

                if (musicService!=null){
                    if (0 == position){
                        musicListFragment.clickedPreviousOrNext(musicNumber-1);
                    }else{
                        musicListFragment.clickedPreviousOrNext(position-1);
                    }

                }else{
                    Toast.makeText(this, "플레이 할 음악을 선택해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        }else if (getFragmentNum==1){
            MusicInfoFragment musicInfoFragment= (MusicInfoFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (musicInfoFragment!=null){

                if (musicService!=null){
                    if (0 == position){
                        musicInfoFragment.clickedPreviousOrNext(musicNumber-1);
                    }else{
                        musicInfoFragment.clickedPreviousOrNext(position-1);
                    }

                }else{
                    Toast.makeText(this, "플레이 할 음악을 선택해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    public void playNextMusic(){

        if (getFragmentNum==0){
            MusicListFragment musicListFragment= (MusicListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);

            if (musicListFragment!=null){

                if (musicService!=null){
                    if (position+1 ==  musicNumber){
                        musicListFragment.clickedPreviousOrNext(0);
                    }else{
                        musicListFragment.clickedPreviousOrNext(position+1);
                    }

                }else{
                    Toast.makeText(this, "플레이 할 음악을 선택해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        }else if (getFragmentNum==1){
            MusicInfoFragment musicInfoFragment= (MusicInfoFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);

            if (musicInfoFragment!=null){

                if (musicService!=null){
                    if (position+1 ==  musicNumber){
                        musicInfoFragment.clickedPreviousOrNext(0);
                    }else{
                        musicInfoFragment.clickedPreviousOrNext(position+1);
                    }

                }else{
                    Toast.makeText(this, "플레이 할 음악을 선택해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (connection != null) {
            unbindService(connection);
        }
    }

}


















