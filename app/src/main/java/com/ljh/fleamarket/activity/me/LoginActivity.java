package com.ljh.fleamarket.activity.me;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ljh.fleamarket.activity.R;
import com.ljh.fleamarket.activity.index.MainActivity;
import com.ljh.fleamarket.bo.ResponseBO;
import com.ljh.fleamarket.bo.UserBO;
import com.ljh.fleamarket.utils.EncoderAndDecoderUtils;
import com.ljh.fleamarket.utils.RequestUtils;


import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button backto_me;
    private Button toregister;
    private Button toresetpassword;
    private Button login;
    private CheckBox rememberPass;
    private View backgroundview;
    private EditText getUsername;
    private EditText getPassword;
    private String userName;
    private String passWord;
    private Boolean isremember;
    private ProgressDialog pd;
    private SharedPreferences sharedPreferences;
    private String TAG = "login";


    final static int MAX_PROGRESS = 100;
    private int[] data = new int[50];
    public int progressStatus = 0;
    public int hasData = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setInit();//初始化方法
    }

    //初始化组件
    private void setInit() {
        backgroundview = findViewById(R.id.loginlayout);
        backto_me = (Button) findViewById(R.id.backto_me);
        toregister = (Button) findViewById(R.id.toregister);
        toresetpassword = (Button) findViewById(R.id.toresetpassword);
        login = (Button) findViewById(R.id.btn_login);
        getUsername = (EditText) findViewById(R.id.et_username);
        getPassword = (EditText) findViewById(R.id.et_password);
        rememberPass = (CheckBox) findViewById(R.id.remember_pass);

        backto_me.setOnClickListener(this);
        toregister.setOnClickListener(this);
        toresetpassword.setOnClickListener(this);
        login.setOnClickListener(this);

        //设置界面背景透明度
        backgroundview.getBackground().setAlpha(50);

        //记住密码
        Log.i(TAG, "开始判断是否存在记住密码");
        sharedPreferences = getSharedPreferences("RememberUserInfo", MODE_PRIVATE);
        isremember = sharedPreferences.getBoolean("remember_password", false);
        if (isremember) {
            String username = sharedPreferences.getString("UserName", "");
            String password = sharedPreferences.getString("PassWord", "");
            getUsername.setText(username);
            getPassword.setText(password);
            rememberPass.setChecked(true);
        } else {
            Log.i(TAG, "没有记住密码！");
        }

        /**
         * 注册成功后将用户名返回到登录界面
         */
//        Intent intent = getIntent();
//        String username_return_from_register = intent.getStringExtra("userName");
//        getUsername.setText(username_return_from_register);


    }

    /**
     * 重写点击事件方法
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //跳转回我的界面
            case R.id.backto_me:
                Intent int_back = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(int_back);
                finish();
                break;

            //跳转到注册界面
            case R.id.toregister:
                Intent int_toregister = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(int_toregister);
                break;

            //跳转到忘记密码重置密码界面
            case R.id.toresetpassword:
                Intent int_toresetpassword = new Intent(LoginActivity.this, ResetpasswordActivity.class);
                startActivity(int_toresetpassword);
                break;

            //跳转到登录界面
            case R.id.btn_login:
                toLogin();
                break;
        }

    }


    /**
     * 登录方法
     */
    private void toLogin() {
        Log.i(TAG, "开始登录。。。");
        //获取用户名密码
        userName = getUsername.getText().toString().trim();//去除空格，获取用户名
        passWord = getPassword.getText().toString().trim();//去除空格，获取密码

        //String url = "http://47.105.174.254:8080/Proj20/login";
        String url="http://47.105.174.254:8080/FleaMarketProj/login";

        UserBO userBO = new UserBO();//用户类
        userBO.setOpType(90002);//设置操作码
        userBO.setUname(userName);
        userBO.setUpassword(passWord);

        Gson gson = new Gson();
        String reqJson = gson.toJson(userBO, UserBO.class);//对象转字符串，用于发送请求
        Log.i(TAG, "reqJson" + reqJson);

        //对reqJson进行编码,后台再进行解码，避免中文乱码
        reqJson = EncoderAndDecoderUtils.enCoder(reqJson);

        //如果选中了记住密码，则把用户名密码保存
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (rememberPass.isChecked()) {
            Log.i("TAG", "开始保存密码");
            editor.putString("UserName", userName);
            editor.putString("PassWord", passWord);
            editor.putBoolean("remember_password", true);
        } else {
            editor.clear();
        }
        editor.apply();
        //判断用户名、密码不能为空
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(LoginActivity.this, "用户名不能为空！", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(passWord)) {
            Toast.makeText(LoginActivity.this, "密码不能为空！", Toast.LENGTH_SHORT).show();
            return;
        } else {
            //等待动画
            pd = new ProgressDialog(this);
            //设置对话框不能用“取消”按钮关闭
//            pd.setCancelable(false);
            //设置对话框显示内容
            pd.setMessage("正在登录...");
            //显示进度条对话框
            pd.show();
            //发送登录请求
            RequestUtils.PostRequestWithOkHttp(url, reqJson, new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i(TAG, "数据获取失败！！" + e.toString());
                    ConnetctFailed();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "数据获取成功！！");
                        Log.i(TAG, "response.code==" + response.code());
                        String responseStr = response.body().string();
                        responseStr = EncoderAndDecoderUtils.deCoder(responseStr);
                        Log.i(TAG, "response.body().String()==" + responseStr);
                        Gson gson = new Gson();
                        final ResponseBO responseBO = gson.fromJson(responseStr, ResponseBO.class);
                        Log.i(TAG, responseBO.toString());
                        Log.i(TAG, "登录后返回的token为：" + responseBO.token);
                        Log.i(TAG, "登录后返回的uid为：" + responseBO.uid);
                        Log.i(TAG, "登录后返回的uname为：" + responseBO.uname);
                        if (responseBO.flag == 200) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, responseBO.message, Toast.LENGTH_SHORT).show();
                                }
                            });
                            byte[] userImg = responseBO.img;
                            SharedPreferences sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            if (userImg != null) {
                                String userHeaderImage = new String(Base64.encode(userImg, Base64.DEFAULT));
                                editor.putString("UserImg", userHeaderImage);
                            }
                            editor.clear();
                            editor.putString("UserToken", responseBO.token);
                            editor.putString("UserId", responseBO.uid);
                            editor.putString("UserName", responseBO.uname);
                            editor.apply();
                            /**
                             * 跳转到主界面
                             */
                            Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                            loginIntent.putExtra("loginInfo", "登录成功");
                            startActivity(loginIntent);
                            finish();
                            pd.dismiss();//等待条消失

                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, responseBO.message, Toast.LENGTH_SHORT).show();
                                    pd.dismiss();//等待条消失
                                }
                            });
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                                pd.dismiss();//等待条消失
                            }
                        });
                    }
                }
            });
        }
    }

    /**
     * 检查网络连接状态
     */
    public  boolean isNetConnected(Context paramContext) {
        NetworkInfo localNetworkInfo = ((ConnectivityManager) paramContext
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if ((localNetworkInfo != null) && (localNetworkInfo.isAvailable()))
            return true;
        return false;
    }
    /**
     * 判断是网络出错还是服务器出错
     */
    public void ConnetctFailed(){
        if (isNetConnected(LoginActivity.this)==false){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this, "网络不给力哦!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this, "服务器出错啦，请稍后再试!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }
    }

}
