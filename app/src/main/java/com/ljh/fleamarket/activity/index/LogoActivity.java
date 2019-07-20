package com.ljh.fleamarket.activity.index;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.ljh.fleamarket.activity.R;
import com.ljh.fleamarket.utils.SharedPreferencesUtils;


public class LogoActivity extends AppCompatActivity {

    private Handler myhandler = new Handler();
    private ImageView iv_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        iv_logo = (ImageView) findViewById(R.id.iv_logo);

        startAlphaAnimation();
        myhandler.postDelayed(r,3000);

    }

    public void startAlphaAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
        alphaAnimation.setDuration(3000);//开始动画
        iv_logo.startAnimation(alphaAnimation);
    }

    Runnable r = new Runnable() {
        @Override
        public void run() {
            if (SharedPreferencesUtils.getWelcomeGuideBoolean(LogoActivity.this)) {//第二次启动
                Intent intent_logo = new Intent();
                intent_logo.setClass(LogoActivity.this, MainActivity.class);
                startActivity(intent_logo);
                finish();
            } else {//第一次启动
                SharedPreferencesUtils.putWelcomeGuideBoolean(LogoActivity.this, true);
                Intent intent_logo = new Intent();
                intent_logo.setClass(LogoActivity.this, WelcomeActivity.class);
                startActivity(intent_logo);
                finish();
            }
        }
    };

    @Override
    protected void onDestroy() {
        if (myhandler != null) {
            myhandler.removeCallbacks(r);
            myhandler = null;
        }
        super.onDestroy();
    }

}
