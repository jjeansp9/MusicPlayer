package kr.co.musicplayer;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.UserManagerCompat;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.model.ClientError;
import com.kakao.sdk.common.util.Utility;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.AccessTokenInfo;
import com.kakao.sdk.user.model.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kr.co.musicplayer.databinding.ActivityMainBinding;

// #############################

// 카카오 로그인

// 인증(Authentication) , 인가(Authorization)

// Access token - 사용자 인증, 카카오 API 호출권한 부여 [ 만료시간 : ios 12시간, javascript 2시간, REST API 6시간 ]
// Refresh token - 액세스 토큰 재발급에 사용. 매번 카카오계정 정보를 입력하거나 소셜로그인하는 인증절차를 거치지않아도 재발급 가능 [ 만료시간 : 2달이며, 1달남은 시점부터 갱신가능 ]
// ID token - 카카오 로그인 사용자의 인증정보를 제공하는 토큰 [ 만료시간 : Access token과 동일 ]


// 로그인 과정 단계

// 로그인 : 로그인요청 -> 인가코드 받기요청 -> 인증 및 동의요청 -> 로그인 및 동의 -> 인가코드 발급 -> 인가코드로 토큰발급요청 -> 토큰발급 -> 로그인완료, 토큰정보조회 및 검증


// 1. 농우본수원갈비
//     https://blog.naver.com/rolo_ggo/223034276486
//     1인분 한우 75,000원 / 양념갈비  34.000원 평점 3.9
//
// 2. 횡성한우나주집
//     https://blog.naver.com/kkoyang77/222465889389
//     1인분 50,000원 ~ 60,000원 평점 3.5
//
// 3. 영통 하나참치
//     https://m.blog.naver.com/gukjung/222524847432
//     무한리필 1인당 27,000원~ 47,000원 평점 4.0
//
// 4. 양갈비(양자리)
//     https://blog.naver.com/wonderful6688/222960106777
//     1인분 23,000원~ 38,000원 평점 4.4
//
// 5. 영통 불꽃상회
//     https://leesaram.tistory.com/147
//     1인분 25,000원~ 40,000원 평점 4.1

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Log.d("keyHash", " KeyHash :" + Utility.INSTANCE.getKeyHash(this)); // 카카오 SDK용 키해시 값

        updateKakaoLogin();

        // 카카오 로그인관련
        binding.login.setOnClickListener(v -> kakaoLogin()); // 카카오 로그인 버튼
        binding.logout.setOnClickListener(v -> kakaoLogout()); // 카카오 로그아웃 버튼
        binding.tokenInfo.setOnClickListener(v -> kakaoTokenInfo()); // 카카오 토큰정보 버튼
        binding.kakaoUnlink.setOnClickListener(v -> kakaoUnlink()); // 카카오 회원탈퇴 버튼

    }

    // 카카오 로그인
    private void kakaoLogin(){

        // 카카오계정으로 로그인 공통 callback 구성
        // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우
        Function2<OAuthToken, Throwable, Unit> callback= new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {

                updateKakaoLogin();

                if (oAuthToken.getIdToken()!= null){
                    Log.d("token1", oAuthToken.getIdToken());
                }else if(oAuthToken.getAccessToken()!=null){
                    Log.d("token2", oAuthToken.getAccessToken());
                }
                return null;
            }
        };

        // 카카오톡이 휴대폰에 설치 되어있는지 확인
        if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(this)){
            UserApiClient.getInstance().loginWithKakaoTalk(this, callback);

        }else{ // 설치되어 있지 않은지 확인
            UserApiClient.getInstance().loginWithKakaoAccount(this, callback);
        }
    }


    private void updateKakaoLogin(){

        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {

                if (user != null) {
                    Log.i("kakaoLogin", "카카오 고유ID : " + user.getId());
                    Log.i("kakaoLogin", "카카오 닉네임 : " + user.getKakaoAccount().getProfile().getNickname());
                    Log.i("kakaoLogin", "카카오 이메일 : " + user.getKakaoAccount().getEmail());
                    Log.i("kakaoLogin", "카카오 성별 : " + user.getKakaoAccount().getGender());
                    Log.i("kakaoLogin", "카카오 연령대 : " + user.getKakaoAccount().getAgeRange());
                    Log.i("kakaoLogin", "카카오 프로필사진 : " + user.getKakaoAccount().getProfile().getProfileImageUrl());

                    Glide.with(MainActivity.this).load(user.getKakaoAccount().getProfile().getProfileImageUrl()).into(binding.kakaoImage);
                    binding.kakaoName.setText(user.getKakaoAccount().getProfile().getNickname());

                }else{
                    Log.e("kakaoLogin", "카카오계정으로 로그인 실패", throwable);
                }

                return null;
            }
        });
    }

    // 카카오 로그아웃
    private void kakaoLogout(){
        UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
            @Override
            public Unit invoke(Throwable throwable) {

                if (throwable != null){
                    Log.e("kakaoLogout", "로그아웃 실패. SDK에서 토큰 삭제됨", throwable);
                }else{
                    Log.i("kakaoLogout", "로그아웃 성공. SDK에서 토큰 삭제됨");
                    Glide.with(MainActivity.this).load("").into(binding.kakaoImage);
                    binding.kakaoName.setText("");
                }
                return null;
            }
        });
    }


    // 카카오 회원 식별을 위한 토큰정보, 만료시간
    private void kakaoTokenInfo(){
        UserApiClient.getInstance().accessTokenInfo(new Function2<AccessTokenInfo, Throwable, Unit>() {
            @Override
            public Unit invoke(AccessTokenInfo accessTokenInfo, Throwable throwable) {

                if (throwable != null){
                    Log.e("kakaoTokenInfo", "카카오 토큰 정보 보기 실패", throwable);
                    Toast.makeText(MainActivity.this, "카카오 토큰 정보 보기 실패", Toast.LENGTH_SHORT).show();
                }else if(accessTokenInfo != null){
                    Log.i("kakaoTokenInfo", "카카오 토큰 정보 보기 성공" + "\n회원번호 : " + accessTokenInfo.getId() + "\n만료시간 : " + accessTokenInfo.getExpiresIn());

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setCancelable(true);
                    builder.setTitle("회원식별을 위한 토큰정보, 만료시간");
                    builder.setMessage("회원번호 : " + accessTokenInfo.getId() + "\n" +"만료시간 : " + accessTokenInfo.getExpiresIn()+"초");
                    builder.show();
                }
                return null;
            }
        });
    }

    // 카카오 회원탈퇴
    private void kakaoUnlink(){
        UserApiClient.getInstance().unlink(new Function1<Throwable, Unit>() {
            @Override
            public Unit invoke(Throwable throwable) {

                if (throwable != null){
                    Log.e("kakaoUnlink", "카카오 회원탈퇴 실패", throwable);
                    Toast.makeText(MainActivity.this, "회원탈퇴 실패", Toast.LENGTH_SHORT).show();
                }else{
                    Log.i("kakaoUnlink", "카카오 회원탈퇴 성공");
                    Toast.makeText(MainActivity.this, "회원탈퇴 성공", Toast.LENGTH_SHORT).show();
                    Glide.with(MainActivity.this).load("").into(binding.kakaoImage);
                    binding.kakaoName.setText("");
                }

                return null;
            }
        });
    }



}





















