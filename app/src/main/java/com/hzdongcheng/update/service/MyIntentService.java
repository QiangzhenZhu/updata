package com.hzdongcheng.update.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.hzdongcheng.components.toolkits.utils.Log4jUtils;
import com.hzdongcheng.update.activity.Main2Activity;
import com.hzdongcheng.update.bean.ActivityCollector;
import com.hzdongcheng.update.bean.ApplicationUpdateInfo;
import com.hzdongcheng.update.bean.ServicesTask;
import com.hzdongcheng.update.bean.ServicesTaskCollector;
import com.hzdongcheng.update.utils.ApkVerifyHelper;
import com.hzdongcheng.update.utils.GetPackageInfoHelper;
import com.hzdongcheng.update.utils.InstallHelper;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import javax.security.auth.login.LoginException;

import okhttp3.Call;

public class MyIntentService extends Service {
    public static final String DOWLOAD_URL = "dowload_url";
    public static final String MD5 = "md5";
    public static final String FORCE_START = "force_start";
    public static final String PACKAGE_NAME = "package_name";
    private static final String TAG = "MyIntentService";
    private static int count = 0;//count值用来确定同时执行多个任务时，合适取消显示 应用升级界面
    private Log4jUtils log4jUtils;
    private String dowloadUrl;
    private String md5;
    private Boolean force_start;
    private String packageName;
    private ServicesTask servicesTask;

    public MyIntentService() {
        log4jUtils = Log4jUtils.createInstanse(this.getClass());
        log4jUtils.info(TAG+" onCreate");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        count++;
        Intent i = new Intent(MyIntentService.this, Main2Activity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        dowloadUrl = intent.getStringExtra(DOWLOAD_URL);
        md5 = intent.getStringExtra(MD5);
        force_start = intent.getBooleanExtra(FORCE_START,false);
        packageName = intent.getStringExtra(PACKAGE_NAME);
        servicesTask = new ServicesTask(packageName,dowloadUrl,md5,force_start);
        log4jUtils.debug("收到的Intent信息：\n"+"MD5: "+md5
                            +"\n"+"dowloadUrl："+dowloadUrl
                            +"\n"+"packageName："+packageName
                            +"\n"+"force_start："+force_start
        );
        ServicesTaskCollector.getInstance().addServicesTask(servicesTask);
        OkHttpUtils.get()
                .url(servicesTask.getDowloadUrl())
                .build()
                .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(),servicesTask.getDowloadUrl().split("/")[servicesTask.getDowloadUrl().split("/").length - 1] )//
                {
                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);

                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        servicesStopSelf();
                        log4jUtils.error(TAG+" 安装包下载失败 "+e.getMessage());

                    }

                    @Override
                    public void onResponse(final File response, int id) {
                        log4jUtils.info(TAG+" 安装包下载完成");
                        GetPackageInfoHelper.getPackageInfo(MyIntentService.this,response.getPath());
                        log4jUtils.debug(TAG+" 解析安装包信息："+"PackageName: "+GetPackageInfoHelper.apkPackageName
                                +" VersionCode: " + GetPackageInfoHelper.versionCode + " VersionName: "
                                + GetPackageInfoHelper.versionName);
                        ServicesTask task = null;
                        try{
                             task = ServicesTaskCollector.getInstance().GetServicesTask(GetPackageInfoHelper.apkPackageName);
                             ApkVerifyHelper.apkVerifyWithSHA(MyIntentService.this, log4jUtils, task.getMd5(), response.getPath(), new ApkVerifyHelper.IApkVerfy() {
                                @Override
                                public void onSuccess() {
                                    install(response.getPath());
                                }

                                @Override
                                public void onError(Exception e) {
                                    servicesStopSelf();
                                }
                             });
                        }catch (Exception e){
                            servicesStopSelf();
                            log4jUtils.error(e.getMessage());
                        }


                    }
                });
        return super.onStartCommand(intent, flags, startId);
    }





    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log4jUtils.debug(TAG+" onDestory");
        ActivityCollector.getInstance().removeAll();

    }

    public void install(String apkPath){
        InstallHelper.execute(MyIntentService.this,apkPath, log4jUtils, new InstallHelper.IInstallHelper() {
            @Override
            public void onSuccess() {
                servicesStopSelf();
            }

            @Override
            public void onError(Exception e) {
                servicesStopSelf();
            }
        });
    }

    public void servicesStopSelf(){
        count --;
        if (count == 0){
            stopSelf();
        }
    }
}



