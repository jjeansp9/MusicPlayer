package kr.co.musicplayer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GoogleUser {

    @SerializedName("account")
    @Expose
    private String account;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("image")
    @Expose
    private String image;

    public String getAccount() {return account;}
    public void setAccount(String account) {this.account = account;}

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }


    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getImage() {return image;}
    public void setImage(String image) {this.image = image;}
}

