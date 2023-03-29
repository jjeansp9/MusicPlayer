package kr.co.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

// #############################
// 카카오 로그인

// Access token - 사용지 인증, 카카오 API 호출권한 부여 [ 만료시간 : ios 12시간, javascript 2시간, REST API 6시간 ]
// Refresh token - 액세스 토큰 재발급에 사용. 매번 카카오계정 정보를 입력하거나 소셜로그인하는 인증절차를 거치지않아도 재발급 가능 [ 만료시간 : 2달이며, 1달남은 시점부터 갱신가능 ]
// ID token - 카카오 로그인 사용자의 인증정보를 제공하는 토큰 [ 만료시간 : Access token과 동일

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}