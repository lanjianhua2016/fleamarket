package com.ljh.fleamarket.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPreferencesUtils {
    private static final String FILE_NAME = "parttime";
    private static final String VALUE_NAME = "guide";

    //获取
    public static boolean getWelcomeGuideBoolean(Context context) {
        return context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).getBoolean(VALUE_NAME, false);
    }

    //写入
    public static void putWelcomeGuideBoolean(Context context, Boolean isFirst) {
        SharedPreferences.Editor editor = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(VALUE_NAME, isFirst);
        editor.commit();
    }
}
