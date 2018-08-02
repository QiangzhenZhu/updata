package com.hzdongcheng.update.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.hzdongcheng.components.toolkits.utils.Log4jUtils;
import com.hzdongcheng.update.bean.ActivityCollector;

public class BaseActivity extends AppCompatActivity {
    private Log4jUtils log4jUtils = Log4jUtils.createInstanse(this.getClass());
    private static  String TAG = "BaseActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
        log4jUtils.debug(TAG + " onCreate");
        ActivityCollector.getInstance().addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        log4jUtils.debug(TAG + " onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        log4jUtils.debug(TAG + " onDestory");
        ActivityCollector.getInstance().removeActivity(this);
    }
}
