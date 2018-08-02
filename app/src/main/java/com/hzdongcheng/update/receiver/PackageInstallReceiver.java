package com.hzdongcheng.update.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.hzdongcheng.components.toolkits.utils.Log4jUtils;
import com.hzdongcheng.update.bean.ServicesTask;
import com.hzdongcheng.update.bean.ServicesTaskCollector;

public class PackageInstallReceiver extends BroadcastReceiver {
    private static final String TAG = "PackageInstallReceiver";
    private Log4jUtils log4jUtils;

    public PackageInstallReceiver() {
        log4jUtils = Log4jUtils.createInstanse(PackageManager.class);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //接收安装广播
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
            String packageName = intent.getDataString().split(":")[1];
            log4jUtils.debug("PackageInstallReceiver: install " + packageName);
            try {
                ServicesTask servicesTask = ServicesTaskCollector.getInstance().GetServicesTask(packageName);
                if (servicesTask != null && servicesTask.getForceStart()) {
                    Intent i = context.getPackageManager().getLaunchIntentForPackage(packageName);
                    if (i != null) {
                        context.startActivity(i);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "onReceive: " + e.getMessage());
                log4jUtils.error("PackageInstallReceiver: " + e.getMessage());
            }


            /*if (servicesTask != null && servicesTask.getForceStartMain()){
                Intent i = context.getPackageManager().getLaunchIntentForPackage("com.hzdongcheng.parcellocker");
                if (i != null) {
                    context.startActivity(i);
                }
            }*/


        }
        //接收卸载广播
        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
            String packageName = intent.getDataString().split(":")[1];
            log4jUtils.debug("PackageInstallReceiver: remove " + packageName);


        }


    }
}
