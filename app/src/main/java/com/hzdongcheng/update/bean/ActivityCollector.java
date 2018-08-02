package com.hzdongcheng.update.bean;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import com.hzdongcheng.update.activity.Main2Activity;
import com.hzdongcheng.update.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class ActivityCollector {
    private static ActivityCollector activityCollector = null;
    private List<Activity> activityList;

    private ActivityCollector(){
        activityList = new ArrayList<>();
    }

    public static ActivityCollector getInstance() {
        if (activityCollector == null) {
            synchronized (ActivityCollector.class) {
                if (activityCollector == null) {
                    activityCollector = new ActivityCollector();
                }
            }
        }
        return activityCollector;
    }

    public List<Activity> getActivityList(){
        return activityList;
    }

    public void addActivity(Activity activity){
        activityList.add(activity);
    }

    public void removeActivity(Activity activity){
        activityList.remove(activity);
    }

    public Activity getActivity(Context context, String name){
        PackageManager manager = context.getPackageManager();
        for (Activity activity : activityList) {
            if (activity.getLocalClassName().equals(name)){
                return activity;
            }
        }
        return null;
    }

    public void removeAll(){
        for (Activity activity : activityList) {
            if (activity != null){
                activity.finish();
            }
        }
    }

}
