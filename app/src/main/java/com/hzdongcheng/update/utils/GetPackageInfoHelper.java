package com.hzdongcheng.update.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class GetPackageInfoHelper {
    public static String apkPackageName;
    public static String versionName;
    public static int versionCode;
    public static String md5;


    public static void getPackageInfo(Context context,String apsPath){
        PackageManager pm = context.getPackageManager();
        if (pm != null){
            PackageInfo pi = pm.getPackageArchiveInfo(apsPath,PackageManager.GET_ACTIVITIES);
            apkPackageName = pi.packageName;
            versionName = pi.versionName;
            versionCode = pi.versionCode;
        }
    }
}
