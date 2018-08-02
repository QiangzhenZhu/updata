package com.hzdongcheng.update.utils;

import android.content.Context;
import android.util.Log;

import com.hzdongcheng.components.toolkits.utils.Log4jUtils;
import com.hzdongcheng.components.toolkits.utils.PropertiesUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ApkVerifyHelper {
    private static final String TAG = "ApkVerifyHelper";


    public interface IApkVerfy{
        void onSuccess();
        void onError(Exception e);
    }

    public static void apkVerifyWithSHA(Context context, final Log4jUtils log4jUtils, final String baseSHA, final String apkPath, final IApkVerfy iApkVerfy) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    log4jUtils.debug(TAG+" run");
                    MessageDigest dexDigest = MessageDigest.getInstance("MD5");
                    byte[] bytes = new byte[1024];
                    int byteCount;
                    FileInputStream fis = new FileInputStream(new File(apkPath)); // 读取apk文件
                    while ((byteCount = fis.read(bytes)) != -1) {
                        dexDigest.update(bytes, 0, byteCount);
                    }
                    BigInteger bigInteger = new BigInteger(1, dexDigest.digest()); // 计算apk文件的哈希值
                    String sha = bigInteger.toString(16);
                    fis.close();
                    if (sha.equals(baseSHA)) { // 将得到的哈希值与原始的哈希值进行比较校验
                        iApkVerfy.onSuccess();
                        log4jUtils.error("MD5比对成功：ApkMD5：" + sha
                                 +"\n"
                                 +"baseMd5："
                                 +baseSHA);

                    }else {
                        iApkVerfy.onError(new RuntimeException("MD5比对失败"));
                        log4jUtils.error("MD5比对失败：ApkMD5" + sha
                                +"\n"
                                +"baseMd5："
                                +baseSHA);
                    }

                } catch (NoSuchAlgorithmException e) {
                    log4jUtils.error(TAG + "MD5对比失败："+e.getMessage());
                    iApkVerfy.onError(e);
                } catch (FileNotFoundException e) {
                    iApkVerfy.onError(e);
                    log4jUtils.error(TAG + "MD5对比失败："+e.getMessage());
                } catch (IOException e) {
                    iApkVerfy.onError(e);
                    log4jUtils.error(TAG + "MD5对比失败："+e.getMessage());
                }
            }
        }).start();
    }
}
