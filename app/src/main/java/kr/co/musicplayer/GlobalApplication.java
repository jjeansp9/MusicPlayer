package kr.co.musicplayer;

import android.app.Application;
import android.util.Log;

import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.common.util.Utility;

public class GlobalApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // 카카오 개발자사이트에서 [ 내 애플리케이션 ] 탭에 들어가면 나의 네이티브앱 키 를 확인할 수 있음
        KakaoSdk.init(this, "6e39aaaf95f145ccd0142bbb4d99d18e");
    }
}
