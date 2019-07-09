package com.ljh.fleamarket.activity.me;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ljh.fleamarket.activity.R;


public class SettingActivity extends AppCompatActivity {

    private Button backToMe;
    private TextView tv_ResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_setting);

        backToMe = (Button) findViewById(R.id.backto_me);
        backToMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backIntent = new Intent();
                setResult(RESULT_OK,backIntent);
                finish();
            }
        });

        tv_ResetPassword = (TextView) findViewById(R.id.tv_reset_password);
        tv_ResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toResetPassword = new Intent(SettingActivity.this,ResetpasswordActivity.class);
                startActivity(toResetPassword);
            }
        });
    }
}
