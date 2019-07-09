package com.ljh.fleamarket.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.ljh.fleamarket.activity.R;


public abstract class Dialogchoosephoto extends Dialog implements View.OnClickListener{
    private Activity activity;
    private RelativeLayout btnPickBySelect, btnPickByTake;

    private String TAG = "test";

    public Dialogchoosephoto(Activity activity) {
        super(activity, R.style.ActionSheetDialogStyle);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.headportrait);

        btnPickBySelect = (RelativeLayout) findViewById(R.id.btnPickBySelect);
        btnPickByTake = (RelativeLayout) findViewById(R.id.btnPickByTake);

        btnPickBySelect.setOnClickListener(this);
        btnPickByTake.setOnClickListener(this);

        setViewLocation();
        setCanceledOnTouchOutside(true);//外部点击取消
    }

    /**
     * 设置dialog位于屏幕底部
     */
    private void setViewLocation(){
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;

        Window window = this.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.x = 0;
        lp.y = height;
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        // 设置显示位置
        onWindowAttributesChanged(lp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnPickBySelect:
                Log.i(TAG,"in dialog： click select from 相册");
                btnPickBySelect();
                this.cancel();
                break;
            case R.id.btnPickByTake:
                Log.i(TAG,"in dialog：click take by 相机");
                btnPickByTake();
                this.cancel();
                break;
        }
    }

    public abstract void btnPickBySelect();
    public abstract void btnPickByTake();
}
