package kr.co.musicplayer.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import kr.co.musicplayer.R;
import kr.co.musicplayer.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}