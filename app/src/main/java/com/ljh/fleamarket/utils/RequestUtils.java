package com.ljh.fleamarket.utils;

import android.util.Log;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by ASUS on 2019/4/7.
 */

public class RequestUtils {

    /**
     * get请求方法
     */
    public static void GetRequestWithOkHttp(String address,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        //发送请求
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * post请求方法
     */
    public static void PostRequestWithOkHttp(String url, String reqJson,okhttp3.Callback callback) {

        String TAG ="request" ;
        Log.i(TAG, "开始发送请求。。。");
        //创建OkHttpClient实例对象
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()//创建请求体
                .add("reqJson", reqJson)
                .build();

        Request request = new Request.Builder()//发送请求
                .url(url)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(callback);

    }
}
