package kr.co.musicplayer.activities;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.navercorp.nid.oauth.OAuthLoginCallback;
import com.navercorp.nid.profile.NidProfileCallback;
import com.navercorp.nid.profile.data.NidProfileResponse;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kr.co.musicplayer.model.User;
import kr.co.musicplayer.databinding.ActivityLoginTestBinding;



// 카카오 로그인

// 인증(Authentication) , 인가(Authorization)

// Access token - 사용자 인증, 카카오 API 호출권한 부여 [ 만료시간 : ios 12시간, javascript 2시간, REST API 6시간 ]
// Refresh token - 액세스 토큰 재발급에 사용. 매번 카카오계정 정보를 입력하거나 소셜로그인하는 인증절차를 거치지않아도 재발급 가능 [ 만료시간 : 2달이며, 1달남은 시점부터 갱신가능 ]
// ID token - 카카오 로그인 사용자의 인증정보를 제공하는 토큰 [ 만료시간 : Access token과 동일 ]

// 로그인 과정 단계

// 로그인 : 로그인요청 -> 인가코드 받기요청 -> 인증 및 동의요청 -> 로그인 및 동의 -> 인가코드 발급 -> 인가코드로 토큰발급요청 -> 토큰발급 -> 로그인완료, 토큰정보조회 및 검증


public class LoginTestActivity extends AppCompatActivity {

    private ActivityLoginTestBinding binding;

    private String ClientId= "RSc0aWDT5SRD1erXQkAt"; // 네이버 로그인 식별 아이디
    private String ClientSecret= "UJFjvIuPXW"; // 네이버 로그인 식별 패스워드
    private NidOAuthLogin nidOAuthLogin= new NidOAuthLogin();

    private UserApi userApi= ApiFactory.INSTANCE.getKapi().create(UserApi.class);

    GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 10;

    User user= new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityLoginTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Log.d("keyHash", " KeyHash :" + Utility.INSTANCE.getKeyHash(this)); // 카카오 SDK용 키해시 값

        NaverIdLoginSDK.INSTANCE.initialize(this, ClientId, ClientSecret, "MusicPlayer");

        kakaoUserInfo();
        kakaoUpdateToken();

        naverLogin();
        naverUpdateToken();

        googleLogin();


        // 카카오 로그인 관련
        binding.kakaoLogin.setOnClickListener(v -> kakaoLogin()); // 카카오 로그인 버튼
        binding.kakaoLogout.setOnClickListener(v -> kakaoLogout()); // 카카오 로그아웃 버튼
        binding.kakaoTokenInfo.setOnClickListener(v -> kakaoTokenInfo()); // 카카오 토큰정보 버튼
        binding.kakaoUnlink.setOnClickListener(v -> kakaoUnlink()); // 카카오 회원탈퇴 버튼

        // 네이버 로그인 관련
        binding.naverLogin.setOnClickListener(v -> naverLogin()); // 네이버 로그인 버튼
        binding.naverLogout.setOnClickListener(v -> naverLogout()); // 네이버 로그아웃 버튼
        binding.naverInfo.setOnClickListener(v -> naverInfo()); // 네이버 정보 버튼
        binding.naverUnlink.setOnClickListener(v -> naverUnlink()); // 네이버 회원탈퇴 버튼

        // 구글 로그인 관련
        binding.googleLogin.setOnClickListener(v -> googleLogin()); // 구글 로그인 버튼
        binding.googleLogout.setOnClickListener(v -> googleLogout()); // 구글 로그아웃 버튼
        binding.googleInfo.setOnClickListener(v -> googleInfo()); // 구글 정보 버튼
        binding.googleUnlink.setOnClickListener(v -> googleUnlink()); // 구글 회원탈퇴 버튼

    }

    // 카카오 로그인
    private void kakaoLogin(){

        // 카카오계정으로 로그인 공통 callback 구성
        Function2<OAuthToken, Throwable, Unit> callback= (oAuthToken, throwable) -> {

                if (throwable != null){
                    Toast.makeText(LoginTestActivity.this, "카카오 로그인 실패", Toast.LENGTH_SHORT).show();
                }else{
                    kakaoUserInfo();
                    Toast.makeText(LoginTestActivity.this, "카카오 로그인 성공", Toast.LENGTH_SHORT).show();
                    Log.d("kakaoToken", oAuthToken.getAccessToken());
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

                    Glide.with(LoginTestActivity.this).load(user.getKakaoAccount().getProfile().getProfileImageUrl()).into(binding.kakaoImage);
                    binding.kakaoName.setText(user.getKakaoAccount().getProfile().getNickname());
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
                    Toast.makeText(LoginTestActivity.this, "이미 로그아웃하였거나, 로그인을 하지 않았습니다.", Toast.LENGTH_SHORT).show();
                }else{
                    Log.i("kakaoLogout", "로그아웃 성공. SDK에서 토큰 삭제됨");
                    Glide.with(LoginTestActivity.this).load("").into(binding.kakaoImage);
                    binding.kakaoName.setText("");
                    Toast.makeText(LoginTestActivity.this, "카카오 로그아웃 성공", Toast.LENGTH_SHORT).show();
                }
                return null;
            }
        });
    }


    // 카카오 회원 식별을 위한 토큰정보, 만료시간
    private void kakaoTokenInfo(){

        UserApiClient.getInstance().accessTokenInfo((accessTokenInfo, throwable) -> {

            if (throwable != null){
                Log.e("kakaoTokenInfo", "카카오 토큰 정보 보기 실패", throwable);
                Toast.makeText(LoginTestActivity.this, "카카오 토큰 정보 보기 실패", Toast.LENGTH_SHORT).show();

            }else if(accessTokenInfo != null){
                Log.i("kakaoTokenInfo", "카카오 토큰 정보 보기 성공" + "\n회원번호 : " + accessTokenInfo.getId() + "\n만료시간 : " + accessTokenInfo.getExpiresIn());

                AlertDialog.Builder builder = new AlertDialog.Builder(LoginTestActivity.this);
                builder.setCancelable(true);
                builder.setTitle("회원식별을 위한 토큰정보, 만료시간");
                builder.setMessage("회원번호 : " + accessTokenInfo.getId() + "\n" +"만료시간 : " + accessTokenInfo.getExpiresIn()+"초");
                builder.show();
            }
            return null;

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

    private Unit callback(AccessTokenInfo tokenInfo, Throwable error){

        return null;
    }

    // 카카오 회원탈퇴
    private void kakaoUnlink(){
        UserApiClient.getInstance().unlink(new Function1<Throwable, Unit>() {
            @Override
            public Unit invoke(Throwable throwable) {

                if (throwable != null){
                    Log.e("kakaoUnlink", "카카오 회원탈퇴 실패", throwable);
                    Toast.makeText(LoginTestActivity.this, "로그인을 한 상태에서 진행해주세요", Toast.LENGTH_SHORT).show();
                }else{
                    Log.i("kakaoUnlink", "카카오 회원탈퇴 성공");
                    Toast.makeText(LoginTestActivity.this, "회원탈퇴 성공", Toast.LENGTH_SHORT).show();
                    Glide.with(LoginTestActivity.this).load("").into(binding.kakaoImage);
                    binding.kakaoName.setText("");
                }

                return null;
            }
        });
    }


//    NaverIdLoginSDK.initialize() 메서드가 여러 번 실행돼도
//    기존에 저장된 접근 토큰(access token)과 갱신 토큰(refresh token)은 삭제되지 않습니다.

//    기존에 저장된 접근 토큰과 갱신 토큰을 삭제하려면
//    NaverIdLoginSDK.logout() 메서드나 NidOAuthLogin().callDeleteTokenApi() 메서드를 호출합니다.

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

                            Glide.with(LoginTestActivity.this).load(nidProfileResponse.getProfile().getProfileImage()).into(binding.naverImage);
                            binding.naverName.setText(nidProfileResponse.getProfile().getNickname());
                            Toast.makeText(LoginTestActivity.this, "네이버 로그인 성공", Toast.LENGTH_SHORT).show();
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

    // 네이버 로그아웃
    private void naverLogout(){

        if (NaverIdLoginSDK.INSTANCE.getAccessToken() == null){
            Toast.makeText(this, "이미 로그아웃하였거나, 로그인을 하지 않았습니다.", Toast.LENGTH_SHORT).show();
        }else{
            NaverIdLoginSDK.INSTANCE.logout();

            Glide.with(LoginTestActivity.this).load("").into(binding.naverImage);
            binding.naverName.setText("");

            Toast.makeText(this, "네이버 로그아웃 성공", Toast.LENGTH_SHORT).show();
        }
    }

    // 네이버 토큰 만료되면 갱신 [ 만료시간 1시간이며 만료될 때마다 토큰 재발급받음 ]
    private void naverUpdateToken(){
        nidOAuthLogin.callRefreshAccessTokenApi(this, new OAuthLoginCallback() {
            @Override
            public void onSuccess() {
                NaverIdLoginSDK.INSTANCE.getAccessToken(); // 토큰 재발급
            }

            @Override
            public void onFailure(int i, @NonNull String s) {

            }

            @Override
            public void onError(int i, @NonNull String s) {

            }
        });
    }

    // 네이버 회원정보
    private void naverInfo(){

        nidOAuthLogin.callProfileApi(new NidProfileCallback<NidProfileResponse>() {
            @Override
            public void onSuccess(NidProfileResponse nidProfileResponse) {

                Log.i("naverInfo", "네이버 ID : " + nidProfileResponse.getProfile().getId());
                Log.i("naverInfo", "네이버 닉네임 : " + nidProfileResponse.getProfile().getNickname());
                Log.i("naverInfo", "네이버 프로필이미지 : " + nidProfileResponse.getProfile().getProfileImage());
                Log.i("naverInfo", "네이버 이메일 : " + nidProfileResponse.getProfile().getEmail());
                Log.i("naverInfo", "네이버 성별 : " + nidProfileResponse.getProfile().getGender());
                Log.i("naverInfo", "네이버 연령대 : " + nidProfileResponse.getProfile().getAge());

                AlertDialog.Builder builder = new AlertDialog.Builder(LoginTestActivity.this);
                builder.setCancelable(true);
                builder.setTitle("회원정보");
                builder.setMessage(
                        "네이버 ID : " + nidProfileResponse.getProfile().getId() + "\n"
                                +"네이버 닉네임 : " + nidProfileResponse.getProfile().getNickname() + "\n"
                                +"네이버 프로필이미지 : " + nidProfileResponse.getProfile().getProfileImage() + "\n"
                                +"네이버 이메일 : " + nidProfileResponse.getProfile().getEmail() + "\n"
                                +"네이버 성별 : " + nidProfileResponse.getProfile().getGender() + "\n"
                                +"네이버 연령대 : " + nidProfileResponse.getProfile().getAge()
                );
                builder.show();

                Glide.with(LoginTestActivity.this).load(nidProfileResponse.getProfile().getProfileImage()).into(binding.naverImage);
                binding.naverName.setText(nidProfileResponse.getProfile().getNickname());
            }

            @Override
            public void onFailure(int i, @NonNull String s) {
                Toast.makeText(LoginTestActivity.this, "로그인을 한 상태에서 확인이 가능합니다", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int i, @NonNull String s) {
                Toast.makeText(LoginTestActivity.this, "onError", Toast.LENGTH_SHORT).show();
            }
        });

        Log.i("tokenTime", NaverIdLoginSDK.INSTANCE.getExpiresAt()+"");
    }

    // 네이버 회원탈퇴
    private void naverUnlink(){

        nidOAuthLogin.callDeleteTokenApi(this, new OAuthLoginCallback() {
            @Override
            public void onSuccess() {
                //서버에서 토큰 삭제에 성공한 상태입니다.
                Glide.with(LoginTestActivity.this).load("").into(binding.naverImage);
                binding.naverName.setText("");

                Toast.makeText(LoginTestActivity.this, "네이버 회원탈퇴 성공", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i, @NonNull String s) {
                // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
                // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
                Toast.makeText(LoginTestActivity.this, "로그인을 한 상태에서 진행해주세요", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(int i, @NonNull String s) {
                // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
                // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
                Toast.makeText(LoginTestActivity.this, "네이버 회원탈퇴 에러", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
    }

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

            user.setAccount(account.getAccount()+"");
            user.setId(account.getId());
            user.setEmail(account.getEmail());
            user.setName(account.getDisplayName());
            user.setImage(account.getPhotoUrl()+"");

            Glide.with(this).load(account.getPhotoUrl()).into(binding.googleImage);
            binding.googleName.setText(account.getDisplayName());

            Toast.makeText(LoginTestActivity.this, "구글 로그인 완료", Toast.LENGTH_SHORT).show();

            // Signed in successfully, show authenticated UI.
            //updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }

    // 구글 로그아웃
    private void googleLogout(){
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Glide.with(LoginTestActivity.this).load("").into(binding.googleImage);
                        binding.googleName.setText("");
                        Toast.makeText(LoginTestActivity.this, "구글 로그아웃 완료", Toast.LENGTH_SHORT).show();
                    }
                });
        user.setAccount("");
        user.setId("");
        user.setEmail("");
        user.setName("");
        user.setImage("");
    }

    private void googleInfo(){


        AlertDialog.Builder builder = new AlertDialog.Builder(LoginTestActivity.this);
        builder.setCancelable(true);
        builder.setTitle("회원정보");
        builder.setMessage(
                "회원 계정 : " + user.getAccount() + "\n"
                        +"회원 아이디 : " + user.getId() + "\n"
                        + "회원 이메일 : " + user.getEmail() + "\n"
                        + "회원 이름 : " + user.getName() + "\n"
                        + "회원 사진 : " + user.getImage());
        builder.show();
    }

    // 구글 회원탈퇴
    private void googleUnlink(){
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Glide.with(LoginTestActivity.this).load("").into(binding.googleImage);
                        binding.googleName.setText("");
                        Toast.makeText(LoginTestActivity.this, "구글 회원탈퇴 완료", Toast.LENGTH_SHORT).show();
                    }
                });
        user.setAccount("");
        user.setId("");
        user.setEmail("");
        user.setName("");
        user.setImage("");
    }

}





















