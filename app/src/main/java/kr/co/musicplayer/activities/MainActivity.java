package kr.co.musicplayer.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.ArrayList;

import kr.co.musicplayer.R;
import kr.co.musicplayer.databinding.ActivityMainBinding;
import kr.co.musicplayer.fragments.MusicInfoFragment;
import kr.co.musicplayer.fragments.MusicListFragment;
import kr.co.musicplayer.User;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private TextView userName;
    private ImageView userImage;
    private TextView userEmail;
//    private DrawerLayout drawerLayout;
//    View navBar;

    private ArrayList<Fragment> fragments= new ArrayList<>();
    private FragmentManager fragmentManager= null;
    private int num= 0;
    private boolean[] result= {false,false};

    private User user= new User();

    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

        //binding.play.setOnClickListener(v -> musicPlay()); // 음악 재생
        binding.pause.setOnClickListener(v -> musicPause()); // 음악 일시정지

    } // onCreate()

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
//
//    }

    // 음악 일시정지

    private void musicPause(){
        mp.pause();
        binding.play.setVisibility(View.VISIBLE);
        binding.pause.setVisibility(View.INVISIBLE);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        // MediaPlayer 해지
        if(mp != null) {
            mp.release();
            mp = null;
        }
    }
}


















