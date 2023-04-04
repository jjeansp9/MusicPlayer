package kr.co.musicplayer.activities;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.util.Utility;
import com.kakao.sdk.network.ApiCallback;
import com.kakao.sdk.network.ApiFactory;
import com.kakao.sdk.user.UserApi;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.AccessTokenInfo;
import com.navercorp.nid.NaverIdLoginSDK;
import com.navercorp.nid.oauth.NidOAuthLogin;
import com.navercorp.nid.profile.NidProfileCallback;
import com.navercorp.nid.profile.data.NidProfileResponse;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kr.co.musicplayer.databinding.ActivityLoginBinding;
import kr.co.musicplayer.User;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    private String ClientId= "RSc0aWDT5SRD1erXQkAt"; // 네이버 로그인 식별 아이디
    private String ClientSecret= "UJFjvIuPXW"; // 네이버 로그인 식별 패스워드
    private NidOAuthLogin nidOAuthLogin= new NidOAuthLogin();

    private UserApi userApi= ApiFactory.INSTANCE.getKapi().create(UserApi.class);

    GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 10;

    User user=new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Log.d("keyHash", " KeyHash :" + Utility.INSTANCE.getKeyHash(this)); // 카카오 SDK용 키해시 값

        NaverIdLoginSDK.INSTANCE.initialize(this, ClientId, ClientSecret, "MusicPlayer");

        kakaoUserInfo();
        kakaoUpdateToken();

        binding.kakaoLogin.setOnClickListener(v -> kakaoLogin()); // 카카오 로그인 버튼
        binding.naverLogin.setOnClickListener(v -> naverLogin()); // 네이버 로그인 버튼
        binding.googleLogin.setOnClickListener(v -> googleLogin()); // 구글 로그인 버튼

        request();
    }

    // 카카오 로그인
    private void kakaoLogin(){

        // 카카오계정으로 로그인 공통 callback 구성
        Function2<OAuthToken, Throwable, Unit> callback= (oAuthToken, throwable) -> {

            if (throwable != null){
                Toast.makeText(LoginActivity.this, "카카오 로그인 실패", Toast.LENGTH_SHORT).show();
            }else{
                kakaoUserInfo();
                Toast.makeText(LoginActivity.this, "카카오 로그인 성공", Toast.LENGTH_SHORT).show();
                Log.d("kakaoToken", oAuthToken.getAccessToken());

                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
            return null;
        };

        // 카카오톡이 휴대폰에 설치 되어있으면 카톡으로 바로 접근해서 로그인 [ 권장 ]
        if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(this)){
            UserApiClient.getInstance().loginWithKakaoTalk(this, callback);

        }else{ // 설치되어 있지 않은 경우 카카오 사이트로 접속해서 로그인
            UserApiClient.getInstance().loginWithKakaoAccount(this, callback);
        }
    }

    // 카카오 회원 정보
    private void kakaoUserInfo(){
        UserApiClient.getInstance().me(new Function2<com.kakao.sdk.user.model.User, Throwable, Unit>() {
            @Override
            public Unit invoke(com.kakao.sdk.user.model.User user, Throwable throwable) {

                if (user != null) {
                    Log.i("kakaoLogin", "카카오 고유ID : " + user.getId());
                    Log.i("kakaoLogin", "카카오 닉네임 : " + user.getKakaoAccount().getProfile().getNickname());
                    Log.i("kakaoLogin", "카카오 이메일 : " + user.getKakaoAccount().getEmail());
                    Log.i("kakaoLogin", "카카오 성별 : " + user.getKakaoAccount().getGender());
                    Log.i("kakaoLogin", "카카오 연령대 : " + user.getKakaoAccount().getAgeRange());
                    Log.i("kakaoLogin", "카카오 프로필사진 : " + user.getKakaoAccount().getProfile().getProfileImageUrl());

//                    Glide.with(LoginTestActivity.this).load(user.getKakaoAccount().getProfile().getProfileImageUrl()).into(binding.kakaoImage);
//                    binding.kakaoName.setText(user.getKakaoAccount().getProfile().getNickname());
                }

                return null;
            }
        });
    }

    // 카카오 토큰 갱신
    private void kakaoUpdateToken(){
        userApi.accessTokenInfo()
                .enqueue(new ApiCallback<AccessTokenInfo>() {
                    @Override
                    public void onComplete(@Nullable AccessTokenInfo model, @Nullable Throwable throwable) {
                        callback(model, throwable);
                        Log.i("kakaoTokenUpdate", model+"," + throwable);
                    }
                });
    }
    private Unit callback(AccessTokenInfo tokenInfo, Throwable error){return null;}


    // 네이버 로그인
    private void naverLogin(){
        NaverIdLoginSDK.INSTANCE.authenticate(this, launcher);

    }

    private ActivityResultLauncher<Intent> launcher= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            switch (result.getResultCode()){
                case RESULT_OK: {
                    Log.i("naverLogin", "네이버 토큰 : " + NaverIdLoginSDK.INSTANCE.getAccessToken());
                    Log.i("naverLogin", "네이버 Refresh토큰 : " + NaverIdLoginSDK.INSTANCE.getRefreshToken());
                    Log.i("naverLogin", "네이버 만료시간 : " + NaverIdLoginSDK.INSTANCE.getExpiresAt());
                    Log.i("naverLogin", "네이버 토큰타입 : " + NaverIdLoginSDK.INSTANCE.getTokenType());
                    Log.i("naverLogin", "네이버 상태 : " + NaverIdLoginSDK.INSTANCE.getState());

                    nidOAuthLogin.callProfileApi(new NidProfileCallback<NidProfileResponse>() {
                        @Override
                        public void onSuccess(NidProfileResponse nidProfileResponse) {

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();

//                            Glide.with(LoginActivity.this).load(nidProfileResponse.getProfile().getProfileImage()).into(binding.naverImage);
//                            binding.naverName.setText(nidProfileResponse.getProfile().getNickname());
                            Toast.makeText(LoginActivity.this, "네이버 로그인 성공", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int i, @NonNull String s) {
                        }

                        @Override
                        public void onError(int i, @NonNull String s) {
                        }
                    });

                }
                case RESULT_CANCELED:{
                    Log.e("naverError", "에러 : " + NaverIdLoginSDK.INSTANCE.getLastErrorCode().getCode() +", "+ NaverIdLoginSDK.INSTANCE.getLastErrorDescription());

                }
            }
        }
    });

    // 구글 로그인
    private void googleLogin(){
        GoogleSignInOptions gso= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient= GoogleSignIn.getClient(this, gso);

        Intent signInIntent= mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task= GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            Log.i("googleID", account.getId());
            Log.i("googleAccount", account.getAccount()+"");
            Log.i("googleEmail", account.getEmail());
            Log.i("googleDisplayName", account.getDisplayName());
            Log.i("googleGivenName", account.getGivenName());
            Log.i("googleFamilyName", account.getFamilyName());
            Log.i("googlePhotoUrl", account.getPhotoUrl()+"");
//            Log.i("googleLogin", account.getIdToken());

            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();



//            Glide.with(this).load(account.getPhotoUrl()).into(binding.googleImage);
//            binding.googleName.setText(account.getDisplayName());

            Toast.makeText(LoginActivity.this, "구글 로그인 완료", Toast.LENGTH_SHORT).show();

            // Signed in successfully, show authenticated UI.
            //updateUI(account);
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }

    private void request(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 마시멜로우 버전과 같거나 이상이라면
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "외부 저장소 사용을 위해 읽기/쓰기 필요", Toast.LENGTH_SHORT).show();
                }

                requestPermissions(new String[]
                                {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                        2);  //마지막 인자는 체크해야될 권한 갯수

            } else {
                //Toast.makeText(this, "권한 승인되었음", Toast.LENGTH_SHORT).show();
            }
        }
    }

}