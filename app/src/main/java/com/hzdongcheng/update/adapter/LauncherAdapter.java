package com.hzdongcheng.update.adapter;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzdongcheng.update.R;
import com.hzdongcheng.update.bean.ApplicationUpdateInfo;

import java.util.List;

public class LauncherAdapter extends RecyclerView.Adapter<LauncherAdapter.ViewHolder> {
    private List<ApplicationUpdateInfo> mApplicationUpdateInfos;
    private List<ActivityManager.RunningAppProcessInfo> taskInfos;
    private PackageManager pm;
    private Context mContext;
    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mIconImage;
        private TextView mApplicationName;
        private TextView mApplicationStatues;
        private TextView mCurrentVersion;
        private TextView mApplicationUpdateStatus;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mIconImage = itemView.findViewById(R.id.iv_item_launcher_icon);
            mApplicationName = itemView.findViewById(R.id.tv_item_launcher_name);
            mCurrentVersion = itemView.findViewById(R.id.tv_item_current_version);
            mApplicationStatues = itemView.findViewById(R.id.tv_item_application_running_status);
            mApplicationUpdateStatus = itemView.findViewById(R.id.tv_item_applicaiton_update_status);
        }
    }

    public LauncherAdapter(Context context, List<ApplicationUpdateInfo> infos, List<ActivityManager.RunningAppProcessInfo> taskInfos){
        this.mApplicationUpdateInfos = infos;
        pm = context.getPackageManager();
        mContext = context;
        this.taskInfos = taskInfos;
    }
    @NonNull
    @Override
    public LauncherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recycleview_layout,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final LauncherAdapter.ViewHolder viewHolder, final int i) {
        ResolveInfo resolveInfo = mApplicationUpdateInfos.get(i).getResolveInfo();
        PackageManager packageManager = mContext.getPackageManager();
        PackageInfo packageInfo = new PackageInfo();
        try {
            packageInfo = packageManager.getPackageInfo(resolveInfo.activityInfo.packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        viewHolder.mApplicationName.setText(resolveInfo.loadLabel(packageManager) + "");
        viewHolder.mIconImage.setImageDrawable(resolveInfo.loadIcon(packageManager));
        viewHolder.mCurrentVersion.setText(String.format(mContext.getResources().getString(R.string.item_rv_curent_version), packageInfo.versionName));
        //判断是否在后台运行
        viewHolder.mApplicationStatues.setText( Html.fromHtml("<font color='#888888'>状态：</font><font color='#EE3B3B'>未运行</font>"));
        viewHolder.mApplicationStatues.setTextColor(mContext.getResources().getColor(R.color.process_stopping));
        viewHolder.mApplicationUpdateStatus.setText(mApplicationUpdateInfos.get(i).getUpdateStatues());
        for (ActivityManager.RunningAppProcessInfo taskInfo : taskInfos) {
            if (taskInfo.processName.equals(resolveInfo.activityInfo.packageName)) {
                viewHolder.mApplicationStatues.setText(Html.fromHtml("<font color='#888888'>状态：</font><font color='#3ddb39'>运行中</font>"));
                viewHolder.mApplicationStatues.setTextColor(mContext.getResources().getColor(R.color.process_running));
                break;
            }

        }
    }
    @Override
    public int getItemCount() {
        return mApplicationUpdateInfos.size();
    }

}
