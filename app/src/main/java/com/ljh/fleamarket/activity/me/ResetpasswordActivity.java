package com.ljh.fleamarket.activity.me;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.ljh.fleamarket.activity.R;


public class ResetpasswordActivity extends AppCompatActivity {
    private Button backto_login2;
    private View backgroundview2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword);

        backto_login2 = (Button)findViewById(R.id.backto_login2);
        backto_login2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        backgroundview2 = findViewById(R.id.resetpasswordlayout);
        backgroundview2.getBackground().setAlpha(100);
    }
}
