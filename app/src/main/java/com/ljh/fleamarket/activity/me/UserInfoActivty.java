package com.ljh.fleamarket.activity.me;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ljh.fleamarket.activity.R;
import com.ljh.fleamarket.adapter.MyUserRecyclerAdapter;
import com.ljh.fleamarket.bo.ResponseBO;
import com.ljh.fleamarket.bo.UserBO;
import com.ljh.fleamarket.utils.EncoderAndDecoderUtils;
import com.ljh.fleamarket.utils.RequestUtils;


import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class UserInfoActivty extends AppCompatActivity {
    private Button backToMe;
    private TextView upDateUserInfo;
    private RecyclerView userInfoView;
    private UserBO userBO;

    public static Activity userInfoActivty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_activty);
        userInfoActivty = this;
        initActivity();
    }

    private void initActivity() {
        backToMe = (Button) findViewById(R.id.backto_me);
        upDateUserInfo = (TextView) findViewById(R.id.update_userinfo);
        userInfoView = (RecyclerView) findViewById(R.id.userinfo_layout);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);//设置布局，默认也是垂直布局
        userInfoView.setLayoutManager(linearLayoutManager);//设置布局管理器

        backToMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToMeIntent = new Intent();
                setResult(RESULT_OK,backToMeIntent);
                finish();
            }
        });

        upDateUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toUpDateUserInfoIntent = new Intent(UserInfoActivty.this,UpDateUserInfoActivity.class);
                Bundle bundle = new Bundle();
                //判断空值？？？？？？
                bundle.putSerializable("user",userBO);
                toUpDateUserInfoIntent.putExtras(bundle);
                startActivity(toUpDateUserInfoIntent);
                //finish();
            }
        });

        DisplayUserInfo();
    }

    private void DisplayUserInfo() {
        String url = "http://47.105.174.254:8080/FleaMarketProj/user_info";
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
        String userToken = sharedPreferences.getString("UserToken", "");
        UserBO user = new UserBO();
        user.setOpType(60001);//60001表示查找展示用户信息，60002表示修改用户信息
        user.setToken(userToken);
        Gson gson = new Gson();
        String reqJson = gson.toJson(user, UserBO.class);//对象转字符串，用于发送请求
        reqJson = EncoderAndDecoderUtils.enCoder(reqJson);
        //等待动画
        final ProgressDialog pd = new ProgressDialog(this);
//        pd.setCancelable(false);//设置对话框不能用“取消”按钮关闭
        pd.setMessage("正在刷新...");//设置对话框显示内容
        //显示进度条对话框
        pd.show();
        RequestUtils.PostRequestWithOkHttp(url, reqJson, new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("userInfo", "数据获取失败！！" + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.i("userInfo", "数据获取成功！！");
                    Log.i("userInfo", "response.code==" + response.code());

                    final String responseStr = response.body().string();
                    Log.i("userInfo", "response.body().String()==" + responseStr);
                    Gson gson = new Gson();
                    final ResponseBO responseBO = gson.fromJson(responseStr, ResponseBO.class);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (responseBO.flag == 200) {
                                //获取解析后生成userVO
                                userBO = responseBO.getUserBO();
                                Log.i("userInfo","解析数据完成");
                                Log.i("userInfo", "run: "+userBO.toString());
                                //创建适配器
                                MyUserRecyclerAdapter myUserRecyclerAdapter = new MyUserRecyclerAdapter(UserInfoActivty.this,userBO);
                                Log.i("userInfo","创建适配器完成");
                                //加载适配器
                                userInfoView.setAdapter(myUserRecyclerAdapter);
                                Log.i("userInfo","加载适配器完成");
                                Toast.makeText(UserInfoActivty.this, responseBO.message, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(UserInfoActivty.this, responseBO.message, Toast.LENGTH_SHORT).show();
                            }
                            pd.dismiss();//等待条消失
                        }
                    });
                }
            }
        });
    }


}