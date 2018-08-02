package com.hzdongcheng.update.utils;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.IPackageManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.hzdongcheng.components.toolkits.utils.Log4jUtils;

import java.io.File;
import java.lang.reflect.Method;

public class InstallHelper {
    private static final String TAG = "InstallHelper";
    public interface IInstallHelper{
        void onSuccess();
        void onError(Exception e);
    }
    public static void execute(Context context, String apkPath, final Log4jUtils log4jUtils, final IInstallHelper helper) {
        File apkFile = new File(apkPath);
        try {
            @SuppressLint("PrivateApi")
            Class<?> clazz = Class.forName("android.os.ServiceManager");
            Method method_getService = clazz.getMethod("getService",
                    String.class);
            IBinder bind = (IBinder) method_getService.invoke(null, "package");

            IPackageManager iPm = IPackageManager.Stub.asInterface(bind);
            IPackageInstallObserver installObserver = new IPackageInstallObserver.Stub() {
                @Override
                public void packageInstalled(String packageName, int returnCode) throws RemoteException {
                    //这个方法回调后不代表程序安装成功，这里仅作为一种通知处理，安装是否成功，已PackageInstallReceiver里是否监听到到消息为准；
                    helper.onSuccess();
                    log4jUtils.debug(TAG+ "："+packageName+"  returnCode："+returnCode);
                }
            };
            GetPackageInfoHelper.getPackageInfo(context,apkPath);
            log4jUtils.debug("PackageName: "+GetPackageInfoHelper.apkPackageName
            +" VersionCode: " + GetPackageInfoHelper.versionCode + " VersionName: "
                    + GetPackageInfoHelper.versionName);
            iPm.installPackage(Uri.fromFile(apkFile), installObserver, 2,
                    apkFile.getPath());
        } catch (Exception e) {
            log4jUtils.error(TAG+ " 安装失败："+e.getMessage());
            Log.e(TAG, "execute: "+e);
            helper.onError(e);
        }
    }

}
