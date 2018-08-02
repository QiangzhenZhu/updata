package com.hzdongcheng.update.bean;

import java.util.ArrayList;
import java.util.List;

public class ServicesTaskCollector {
    private static final String TAG = "ServicesTaskCollector";
    private static ServicesTaskCollector collector = null;
    private List<ServicesTask> taskList;
    private ServicesTaskCollector(){
        taskList = new ArrayList<>();
    }

    public static ServicesTaskCollector getInstance(){
        if (collector == null){
            synchronized (ServicesTaskCollector.class){
                if (collector == null){
                    collector = new ServicesTaskCollector();
                }
            }

        }
        return collector;
    }

    public void addServicesTask(ServicesTask servicesTask){
        if (servicesTask != null){
            taskList.add(servicesTask);
        }
    }

    public ServicesTask GetServicesTask(String packageName){
        for (ServicesTask task: taskList
             ) {
            if (task.getPackName().equals(packageName)) {
                return task;
            }
        }
        throw new RuntimeException(TAG+" 错误：找不到与packageName相同包名的ServisesTask实例" );
    }

    public void removeServiccesTask(String packageName){
        for (ServicesTask task: taskList
                ) {
            if (task.getPackName().equals(packageName)) {
                taskList.remove(task);
            }
        }

    }


}
