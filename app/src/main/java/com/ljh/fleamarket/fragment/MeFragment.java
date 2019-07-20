package com.ljh.fleamarket.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.ljh.fleamarket.activity.R;
import com.ljh.fleamarket.activity.me.LoginActivity;
import com.ljh.fleamarket.activity.me.AboutUsActivity;
import com.ljh.fleamarket.activity.me.MyBuyActivity;
import com.ljh.fleamarket.activity.me.CollectionActivity;
import com.ljh.fleamarket.activity.me.MySaleActivity;
import com.ljh.fleamarket.activity.me.SettingActivity;
import com.ljh.fleamarket.activity.me.UserInfoActivty;

import static android.content.Context.MODE_PRIVATE;
import static android.text.TextUtils.isEmpty;


public class MeFragment extends Fragment implements View.OnClickListener {

    private View mySaleLayout;
    private View myBuyLayout;
    private View myCollectionLayout;
    private View myAboutUsLayout;
    private View mySettingLayout;
    private Button toLogin;
    private LinearLayout userInfo;
    private LinearLayout loginView;
    public ImageView userHeadportrait;
    private TextView userName;
    private TextView userId;

    private static final int VISIBLE = 0;
    private static final int INVISIBILITY = 4;
    private static final int GONE = 8;

    private SharedPreferences sharedPreferences;
    private String username;
    private String uerId;

    //public static  MeFragment meFragment;

    // 只用来指定此fragment的布局
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //  return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        //meFragment = this;
        return view;

    }


    //视图控件请在这里实例化
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
        username = sharedPreferences.getString("UserName", "");
        uerId = sharedPreferences.getString("UserId", "");
        Log.i("login", "login username is:" + username);
        Log.i("login", "login uerId is:" + uerId);

        userHeadportrait = getActivity().findViewById(R.id.user_headportrait);//显示头像
        userName = getActivity().findViewById(R.id.user_name);//显示用户名
        userId = getActivity().findViewById(R.id.user_id);//显示用户ID

        userInfo = getActivity().findViewById(R.id.user_info);
        userInfo.setVisibility(userInfo.GONE);
//        VISIBLE:0  意思是可见的
//        INVISIBLE:4 意思是不可见的，但还占着原来的空间
//        GONE:8  意思是不可见的，不占用原来的布局空间
        loginView = getActivity().findViewById(R.id.loginview);
        loginView.setOnClickListener(this);
        loginView.setVisibility(loginView.GONE);

        String loginInfo = getArguments().getString("loginInfo");

        if (isEmpty(loginInfo)) {
            loginView.setVisibility(loginView.VISIBLE);
        } else {
            userInfo.setVisibility(userInfo.VISIBLE);
            userName.setText(username);
            userId.setText(uerId);

            //从SP中获取字符串并转为字节数组
            String getUserHeadStr = sharedPreferences.getString("UserImg", "");
            if (getUserHeadStr.length() == 0 || getUserHeadStr.isEmpty()) {
                userHeadportrait.setImageResource(R.drawable.heizi);
            } else {
                byte[] userHeadByte = Base64.decode(getUserHeadStr.getBytes(), Base64.DEFAULT);
                //将字节数组转为bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(userHeadByte, 0, userHeadByte.length, null);
                userHeadportrait.setImageBitmap(bitmap);
            }

            Log.i("loginInfo", loginInfo);

            //跳转到用户信息界面
            userInfo.setOnClickListener(this);

        }

        //跳转到登录界面
        toLogin = (Button) getActivity().findViewById(R.id.tologin);
        toLogin.setOnClickListener(this);

        //跳转到我的摊位界面
        mySaleLayout = getActivity().findViewById(R.id.mySale_layout);
        mySaleLayout.setOnClickListener(this);

        //跳转到我的求购界面
        myBuyLayout = getActivity().findViewById(R.id.myBuy_layout);
        myBuyLayout.setOnClickListener(this);

        //跳转到我的收藏界面
        myCollectionLayout = getActivity().findViewById(R.id.myCollection_layout);
        myCollectionLayout.setOnClickListener(this);

        //跳转到关于我们界面
        myAboutUsLayout = getActivity().findViewById(R.id.myaboutus_layout);
        myAboutUsLayout.setOnClickListener(this);

        //跳转到设置界面
        mySettingLayout = getActivity().findViewById(R.id.mysetting_layout);
        mySettingLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        //判断token是否为空，为空则表示未登录状态，提示用户登录
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
        String userToken = sharedPreferences.getString("UserToken", "");//获取用户token
        switch (view.getId()) {
            case R.id.loginview:
                Intent intentToLogin = new Intent(getActivity(), LoginActivity.class);
                startActivity(intentToLogin);
                getActivity().onBackPressed();//将当前的fragment销毁，避免按返回键重新回到未登录的状态
                break;
            case R.id.tologin:
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().onBackPressed();//将当前的fragment销毁，避免按返回键重新回到未登录的状态
                break;
            case R.id.mySale_layout:
                if (userToken == "") {
                    Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_SHORT).show();
                    Intent toLogin = new Intent(getActivity(), LoginActivity.class);
                    startActivity(toLogin);
                    getActivity().onBackPressed();//销毁当前的fragment
                } else {
                    Intent toMySaleIntent = new Intent(getActivity(), MySaleActivity.class);
                    startActivity(toMySaleIntent);
                }
                break;
            case R.id.myBuy_layout:
                if (userToken == "") {
                    Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_SHORT).show();
                    Intent toLogin = new Intent(getActivity(), LoginActivity.class);
                    startActivity(toLogin);
                    getActivity().onBackPressed();//销毁当前的fragment
                } else {
                    Intent toMyBuyIntent = new Intent(getActivity(), MyBuyActivity.class);
                    startActivity(toMyBuyIntent);
                }
                break;
            case R.id.myCollection_layout:
                if (userToken == "") {
                    Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_SHORT).show();
                    Intent toLogin = new Intent(getActivity(), LoginActivity.class);
                    startActivity(toLogin);
                    getActivity().onBackPressed();//销毁当前的fragment
                } else {
                    Intent toMyCollectionIntent = new Intent(getActivity(), CollectionActivity.class);
                    startActivity(toMyCollectionIntent);
                }
                break;
            case R.id.myaboutus_layout:
                Intent toMyAboutUsIntent = new Intent(getActivity(), AboutUsActivity.class);
                startActivity(toMyAboutUsIntent);
                break;
            case R.id.mysetting_layout:
                Intent toMySettingIntent = new Intent(getActivity(), SettingActivity.class);
                startActivity(toMySettingIntent);
                break;
            case R.id.user_info:
                Intent toUserInfoIntent = new Intent(getActivity(), UserInfoActivty.class);
                startActivity(toUserInfoIntent);
                break;
            default:
                break;
        }
    }
}

