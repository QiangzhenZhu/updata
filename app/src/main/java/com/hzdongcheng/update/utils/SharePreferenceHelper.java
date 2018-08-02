package com.hzdongcheng.update.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharePreferenceHelper {
    public static final String IP = "config_ip";
    public static final String PORT = "config_ip";
    public static void put(Context context, String flag,String ip){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(flag,ip);
        editor.apply();
    }

    public static String get(Context context,String flag){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(flag,"");
    }
}
