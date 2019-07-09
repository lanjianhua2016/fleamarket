package com.ljh.fleamarket.activity.me;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ljh.fleamarket.activity.R;


public class AboutUsActivity extends AppCompatActivity {
    private Button backToMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_about_us);

        backToMe = (Button) findViewById(R.id.backto_me);
        backToMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backIntent = new Intent();
                setResult(RESULT_OK,backIntent);
                finish();
            }
        });
    }
}
