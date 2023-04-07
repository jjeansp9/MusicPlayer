package kr.co.musicplayer;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class User {

    private String account;
    private String id;
    private String email;
    private String name;
    private String image;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    // 카카오,네이버,구글 중 아무거나 로그인하면 해당 메소드가 발동하여 디바이스에 ID, platform 값 저장
    public void saveUserId(Context context, String userId, String platform){
        SharedPreferences pref = context.getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("id", userId);
        editor.putString("platform", platform);
        editor.commit();
    }



    // 디바이스에 저장된 ID, platform 값 가져오는 메소드
    public String[] loadUserId(Context context){
        SharedPreferences pref= context.getSharedPreferences("user", MODE_PRIVATE);
        String loadPlatformId= pref.getString("id", null);
        String loadPlatform= pref.getString("platform", null);

        String[] result= {loadPlatformId, loadPlatform};

        Log.i("User.class", "loadUserId() : "+loadPlatformId + ", " + loadPlatform);
        return result;
    }

    // 디바이스에 저장된 ID, platform 값 제거
    public void removeUserId(Context context){
        SharedPreferences preferences = context.getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("id"); // 제거할 키 값 입력
        editor.remove("platform"); // 제거할 키 값 입력
        editor.apply();
    }
}



















