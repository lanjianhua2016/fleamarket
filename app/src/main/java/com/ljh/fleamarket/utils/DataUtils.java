package com.ljh.fleamarket.utils;

import android.app.Application;


public class DataUtils extends Application {

    private boolean intentPermission;

    public boolean isIntentPermission() {
        return intentPermission;
    }

    public void setIntentPermission(boolean intentPermission) {
        this.intentPermission = intentPermission;
    }
}
