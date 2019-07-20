package com.ljh.fleamarket.bo;

import java.util.Arrays;

public class ResponseBO {
    public int flag;
    public String message;
    public String token;
    public String uid;
    public byte [] img;
    public String uname;
    private UserBO userBO;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public UserBO getUserBO() {
        return userBO;
    }

    public void setUserBO(UserBO userBO) {
        this.userBO = userBO;
    }

    @Override
    public String toString() {
        return "ResponseBO{" +
                "flag=" + flag +
                ", message='" + message + '\'' +
                ", token='" + token + '\'' +
                ", uid='" + uid + '\'' +
                ", img=" + Arrays.toString(img) +
                ", uname='" + uname + '\'' +
                ", userBO=" + userBO +
                '}';
    }
}
