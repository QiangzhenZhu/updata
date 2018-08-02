package com.hzdongcheng.update;

import android.app.Application;
import android.os.Environment;

import com.hzdongcheng.components.toolkits.utils.Log4jUtils;

import java.util.PriorityQueue;

public class MyApplication extends Application {
    private String SDCARD_PATH;
    private String HOME_PATH = "/hzdongcheng";
    @Override
    public void onCreate() {
        super.onCreate();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) { //存在SD卡
            SDCARD_PATH = Environment.getExternalStorageDirectory().toString();
        } else {
            SDCARD_PATH = Environment.getDataDirectory().toString();
        }

        Log4jUtils.initLog4jInAndroid(SDCARD_PATH + HOME_PATH + "/logs/applicationmanager/log");
        Log4jUtils log4jUtils = Log4jUtils.createInstanse(this.getClass());
        log4jUtils.info("**********************************");
        log4jUtils.info("**********应用程序已启动************");
        log4jUtils.info("**********************************");
    }
}
