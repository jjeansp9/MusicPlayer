package kr.co.musicplayer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import kr.co.musicplayer.R;
import kr.co.musicplayer.databinding.ActivityDrawerBinding;
import kr.co.musicplayer.databinding.ActivityMainBinding;
import kr.co.musicplayer.model.User;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    TextView userName;
    ImageView userImage;
    TextView userEmail;
    private DrawerLayout drawerLayout;
    View navBar;

    User user= new User();

    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getUserData();



//        userName= findViewById(R.id.user_name);
//        userName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                userName.setText("ffff");
//            }
//        });



        seekBar();

        binding.play.setOnClickListener(v -> musicPlay()); // 음악 재생
        binding.pause.setOnClickListener(v -> musicPause()); // 음악 일시정지

    } // onCreate()

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

    private void seekBar(){
        mp= MediaPlayer.create(this, R.raw.beethoven_piano_sonata_01);

        binding.seekBar.setVisibility(ProgressBar.VISIBLE);
        binding.seekBar.setMax(mp.getDuration());
        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    mp.seekTo(progress);
                }
                int m= progress / 60000;
                int s= (progress % 60000) / 1000;
                String strTime = String.format("%02d:%02d", m, s);
                binding.text.setText(strTime);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    // 음악 재생
    private void musicPlay(){
        mp.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mp.isPlaying()){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    binding.seekBar.setProgress(mp.getCurrentPosition());
                }
            }
        }).start();
        binding.play.setVisibility(View.INVISIBLE);
        binding.pause.setVisibility(View.VISIBLE);

        int m= mp.getDuration() / 60000;
        int s= (mp.getDuration() % 60000) / 1000;
        String strTime = String.format("%02d:%02d", m, s);

        binding.textMax.setText(strTime);

        saveFile();
    }

    // 음악 일시정지
    private void musicPause(){
        mp.pause();
        binding.play.setVisibility(View.VISIBLE);
        binding.pause.setVisibility(View.INVISIBLE);
    }

    // 오디오 파일 저장
    private void saveFile() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Media.DISPLAY_NAME, "beethoven_piano_sonata_01.mp3");
        values.put(MediaStore.Audio.Media.ALBUM, "beethoven_piano_sonata_01.mp3");
        values.put(MediaStore.Audio.Media.ALBUM_ARTIST, R.raw.beethoven_piano_sonata_01);
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/*");
        //MediaStore.Audio.Media.IS_MUSIC

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Audio.Media.IS_PENDING, 1);
        }

        ContentResolver contentResolver = getContentResolver();
        Uri item = contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);

        try {
            ParcelFileDescriptor pdf = contentResolver.openFileDescriptor(item, "w", null);

            if (pdf == null) {
                Log.d("asdf", "null");
            } else {
                String str = "heloo";
                byte[] strToByte = str.getBytes();
                FileOutputStream fos = new FileOutputStream(pdf.getFileDescriptor());
                fos.write(strToByte);
                fos.close();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.clear();
                    values.put(MediaStore.Audio.Media.IS_PENDING, 0);
                    contentResolver.update(item, values, null, null);
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


















