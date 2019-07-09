package com.ljh.fleamarket.bo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ASUS on 2019/4/2.
 */

public class ResponseBuy {
    private int flag;
    private String message;
    private String token;
    private List <Goods> goodsList = new ArrayList<Goods>();

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

    public List<Goods> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<Goods> goodsList) {
        this.goodsList = goodsList;
    }

    @Override
    public String toString() {
        return "ResponseBuy{" +
                "flag=" + flag +
                ", message='" + message + '\'' +
                ", token='" + token + '\'' +
                ", goodsList=" + goodsList +
                '}';
    }
}
