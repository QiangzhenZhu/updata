package com.hzdongcheng.update.activity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hzdongcheng.components.toolkits.utils.Log4jUtils;
import com.hzdongcheng.update.R;
import com.hzdongcheng.update.adapter.LauncherAdapter;
import com.hzdongcheng.update.bean.ApplicationUpdateInfo;
import com.hzdongcheng.update.utils.ApkVerifyHelper;
import com.hzdongcheng.update.utils.InstallHelper;
import com.hzdongcheng.update.utils.SharePreferenceHelper;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import org.apache.log4j.chainsaw.Main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    RecyclerView recyclerView;
    List<ApplicationUpdateInfo> mApplicationUpdateInfos;
    ImageButton mBtnBack;
    ImageButton mConfig;
    LauncherAdapter adapter;
    TextView mChechUpAll;
    TextView mTvCheckInfo;
    RecyclerView.LayoutManager layoutManager;
    Log4jUtils log4jUtils;
    List<ActivityManager.RunningAppProcessInfo> taskList;
    ProgressBar progressBar;
    AlertDialog alertDialog;
    boolean flag = false;
    //测试用来计数
    static int temp = 0;
    static int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log4jUtils = Log4jUtils.createInstanse(this.getClass());
        log4jUtils.info("**********MainActivity onCreate************");
        setContentView(R.layout.activity_main);
        initDate();
        initView();

    }

    public void initDate(){
        mApplicationUpdateInfos = new ArrayList<>();
        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager pm = getPackageManager();
        List<ResolveInfo> infos = pm.queryIntentActivities(startupIntent,0);
        //过滤掉无关应用；
        for (ResolveInfo re :infos) {
            if (re.activityInfo.packageName.contains("com.hzdongcheng")){
                ApplicationUpdateInfo applicationUpdateInfo = new ApplicationUpdateInfo();
                applicationUpdateInfo.setResolveInfo(re);
                mApplicationUpdateInfos.add(applicationUpdateInfo);
            }
        }

        //获取正在运行的应用进程，判断哪个程序是否在后台运行；
        ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
        taskList = am.getRunningAppProcesses();
    }

    public void initView(){
        mBtnBack = findViewById(R.id.ib_back);
        mConfig = findViewById(R.id.ib_config);
        mTvCheckInfo = findViewById(R.id.tv_check_info);
        recyclerView = findViewById(R.id.rv);
        progressBar = findViewById(R.id.pg_update);
        progressBar.setVisibility(View.GONE);
        mChechUpAll = findViewById(R.id.tv_check_up_all);
        mChechUpAll.setVisibility(View.GONE);
        //switchVisiblility();

        mConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_dialog_config,null);
                final EditText etApkUrl = dialogView.findViewById(R.id.et_apk_url);
                alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getResources().getString(R.string.string_dialog_config_title))
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String etUrl = etApkUrl.getText().toString().trim();
                                if (!TextUtils.isEmpty(etUrl)){
                                    if (Patterns.WEB_URL.matcher(etUrl).matches() || URLUtil.isValidUrl(etUrl)) {
                                        SharePreferenceHelper.put(MainActivity.this,SharePreferenceHelper.IP,etUrl);
                                        dowloadApplication(etUrl);
                                    }else {
                                        Toast.makeText(MainActivity.this,getResources().getText(R.string.string_dialog_config_url_error),Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    Toast.makeText(MainActivity.this,getResources().getText(R.string.string_dialog_url_empty),Toast.LENGTH_SHORT).show();
                                }

                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                alertDialog.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .setView(dialogView)
                        .create();
                alertDialog.show();


            }
        });
        mChechUpAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                log4jUtils.info("**********全部检查更新*************");
                switchVisiblility();
                update();

            }
        });
        //退出应用
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        adapter = new LauncherAdapter(MainActivity.this, mApplicationUpdateInfos,taskList);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
    //交替隐藏显示ProgressBar和检查更新按钮
    public void switchVisiblility(){
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
            mChechUpAll.setVisibility(View.VISIBLE);
        }else {
            progressBar.setVisibility(View.VISIBLE);
            mChechUpAll.setVisibility(View.GONE);
        }
    }

    public void update(){
        for (ApplicationUpdateInfo applicationUpdateInfo : mApplicationUpdateInfos
             ) {
                //刷新RecycleView列表,将错误信息置空
                applicationUpdateInfo.setUpdateStatues("");
                adapter.notifyDataSetChanged();
                mTvCheckInfo.setText("开始检查应用：" + applicationUpdateInfo.getResolveInfo().loadLabel(getPackageManager()));
                checkUpApplication(applicationUpdateInfo);
        }
    }

    /**
     * 访问网络，去检查应用的更新信息
     * @param applicationUpdateInfo
     */
    public void checkUpApplication( final ApplicationUpdateInfo applicationUpdateInfo){
        String url = "http://www.csdn.net/";
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        mTvCheckInfo.setText("网络访问失败，请重试："+e);
                        log4jUtils.error("*******检查更新失败"+ e);
                        //
                        count++;
                        if (mApplicationUpdateInfos.size() == count){
                            switchVisiblility();
                            mTvCheckInfo.setText("");
                            count = 0;
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        flag = true;
                        log4jUtils.debug("已检查更新");
                        if (temp == 0){
                            applicationUpdateInfo.setMd5("1564163655163");
                            dowloadApplication("http://shouji.360tpcdn.com/180601/aa0d64f00eca71148bd53c0c909f5d48/com.idol.android_176.apk", applicationUpdateInfo);
                        }else if (temp == 1){
                            applicationUpdateInfo.setMd5("1564163655163");
                            dowloadApplication("http://shouji.360tpcdn.com/180709/8d89147cadaddbda38dc5ba7f5c3c8a9/com.qihoo360.mobilesafe_261.apk", applicationUpdateInfo);
                        }else {
                            applicationUpdateInfo.setMd5("1564163655163");
                            dowloadApplication("http://shouji.360tpcdn.com/180521/ced0a9aaa4a5cf449e06ffb41ab88481/com.qihoo.haosou_828.apk", applicationUpdateInfo);

                        }
                        temp ++;

                    }
                });
    }

    /**
     * 下载应用更新包
     * @param url
     * @param applicationUpdateInfo
     */
    public void dowloadApplication(final String url,final ApplicationUpdateInfo applicationUpdateInfo){

        OkHttpUtils.get()//
                .url(url)//
                .build()//
                .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), url.split("/")[url.split("/").length - 1])//
                {
                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);
                        /*log4jUtils.info("*******" + applicationUpdateInfo.getName() + "********");
                        log4jUtils.info("**************MainActivity 安装包开始下载 "+"*******");
                        log4jUtils.info("**************MainActivity 下载地址："+ applicationUpdateInfo.getDowloadUrl()+"******8");
*/
                        if (temp++ % 40 == 0 && temp >= 40) {
                            mTvCheckInfo.setText("正在下载：" + applicationUpdateInfo.getResolveInfo().loadLabel(getPackageManager()) +" " +String.format("%.0f", progress*100)+"%");
                        }
                        }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(TAG, "onError: "+e );
                        log4jUtils.error("**************MainActivity 安装包下载失败 "+e+"*******");
                        mTvCheckInfo.setText(applicationUpdateInfo.getResolveInfo().loadLabel(getPackageManager())+"下载失败请重试" );
                        //刷新列表，将错误信息显示到列表上
                        applicationUpdateInfo.setUpdateStatues("下载失败：" + e);
                        adapter.notifyDataSetChanged();

                        count++;
                        Log.d(TAG, "onError: "+count);
                        if (mApplicationUpdateInfos.size() == count){
                            switchVisiblility();
                            mTvCheckInfo.setText("");
                            count = 0;
                        }
                    }

                    @Override
                    public void onResponse(final File response, int id) {
                        log4jUtils.info("**************MainActivity 安装包下载完成 ****");
                        mTvCheckInfo.setText("正在比对MD5"+ applicationUpdateInfo.getResolveInfo().loadLabel(getPackageManager()) );
                        ApkVerifyHelper.apkVerifyWithSHA(MainActivity.this, log4jUtils, applicationUpdateInfo.getMd5(), response.getPath(), new ApkVerifyHelper.IApkVerfy() {
                            @Override
                            public void onSuccess() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        install(MainActivity.this,applicationUpdateInfo,response.getPath());
                                        Log.d(TAG, "MD5 compare onSuccess: ");
                                    }
                                });


                            }

                            @Override
                            public void onError(final Exception e) {
                                Log.d(TAG, "MD5 compare onError: ");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //刷新状态列表
                                        applicationUpdateInfo.setUpdateStatues("MD5校验错误：" + e);
                                        adapter.notifyDataSetChanged();

                                        count++;
                                        if (mApplicationUpdateInfos.size() == count){
                                            switchVisiblility();
                                            mTvCheckInfo.setText("");
                                            count = 0;
                                        }
                                    }
                                });

                            }
                        });




                    }
                });
    }

    public void dowloadApplication(String url){
        final String apkName =  url.split("/")[url.split("/").length - 1];
        OkHttpUtils.get()//
                .url(url)//
                .build()//
                .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(),apkName)//
                {
                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);
                        /*log4jUtils.info("*******" + applicationUpdateInfo.getName() + "********");
                        log4jUtils.info("**************MainActivity 安装包开始下载 "+"*******");
                        log4jUtils.info("**************MainActivity 下载地址："+ applicationUpdateInfo.getDowloadUrl()+"******8");
*/
                        if (temp++ % 40 == 0 && temp >= 40) {
                            mTvCheckInfo.setText("正在下载：" + apkName +" " +String.format("%.0f", progress*100)+"%");
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(TAG, "onError: "+e );
                        log4jUtils.error("**************MainActivity 安装包下载失败 "+e+"*******");
                        mTvCheckInfo.setText(apkName+"下载失败请重试"+e );
                        Log.d(TAG, "onError: "+apkName);

                    }

                    @Override
                    public void onResponse(final File response, int id) {
                        log4jUtils.info("**************MainActivity 安装包下载完成 ****");
                       install(MainActivity.this,apkName,response.getPath());




                    }
                });

    }

    public void install(Context context,final ApplicationUpdateInfo applicationUpdateInfo,String apkPath){
        mTvCheckInfo.setText("正在安装"+ applicationUpdateInfo.getResolveInfo().loadLabel(getPackageManager()) );
        InstallHelper.execute(MainActivity.this,apkPath, log4jUtils, new InstallHelper.IInstallHelper() {
            @Override
            public void onSuccess() {
                log4jUtils.debug("MainActivity 安装成功："+ applicationUpdateInfo.getResolveInfo().loadLabel(getPackageManager()));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTvCheckInfo.setText("安装成功："+ applicationUpdateInfo.getResolveInfo().loadLabel(getPackageManager()));
                        applicationUpdateInfo.setUpdateStatues("安装成功："+ applicationUpdateInfo.getResolveInfo().loadLabel(getPackageManager()));
                        adapter.notifyDataSetChanged();
                        count ++;
                        if (mApplicationUpdateInfos.size() == count){
                            switchVisiblility();
                            mTvCheckInfo.setText("");
                            count = 0;
                        }
                    }
                });

            }

            @Override
            public void onError(Exception e) {
                log4jUtils.debug("MainActivity 安装失败："+ applicationUpdateInfo.getResolveInfo().loadLabel(getPackageManager())+ e);
                applicationUpdateInfo.setUpdateStatues("安装失败：" + e);
                mTvCheckInfo.setText("安装失败："+ applicationUpdateInfo.getResolveInfo().loadLabel(getPackageManager())+" "+e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        adapter.notifyDataSetChanged();
                        count++;
                        if (mApplicationUpdateInfos.size() == count){
                            switchVisiblility();
                            mTvCheckInfo.setText("");
                            count = 0;
                        }
                    }
                });

            }
        });
    }

    public void install(Context context, final String apkName, String apkPath){
        mTvCheckInfo.setText("正在安装"+ apkName );
        log4jUtils.debug("MainActivity 正在安装："+ apkName);

        InstallHelper.execute(MainActivity.this,apkPath, log4jUtils, new InstallHelper.IInstallHelper() {
            @Override
            public void onSuccess() {
                log4jUtils.debug("MainActivity 安装成功："+ apkName);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTvCheckInfo.setText("安装成功："+ apkName);
                    }
                });

            }

            @Override
            public void onError(final Exception e) {
                log4jUtils.debug("MainActivity 安装失败："+ apkName+ e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTvCheckInfo.setText("安装失败: "+apkName +e);
                    }
                });

            }
        });
    }


}
