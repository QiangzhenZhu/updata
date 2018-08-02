package com.hzdongcheng.update.bean;

import android.content.pm.ResolveInfo;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 主动更新时用到的基类
 */
public class ApplicationUpdateInfo implements Parcelable{
    private String name;
    private String dowloadUrl;
    private String md5;

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    private ResolveInfo resolveInfo;
    private String updateDesc;
    private String updateStatues;

    public String getUpdateDesc() {
        return updateDesc;
    }

    public void setUpdateDesc(String updateDesc) {
        this.updateDesc = updateDesc;
    }

    public String getUpdateStatues() {
        return updateStatues;
    }

    public void setUpdateStatues(String updateStatues) {
        this.updateStatues = updateStatues;
    }

    public String getUpdateInfo() {
        return updateDesc;
    }

    public void setUpdateInfo(String updateInfo) {
        this.updateDesc = updateInfo;
    }


    public ResolveInfo getResolveInfo() {
        return resolveInfo;
    }

    public void setResolveInfo(ResolveInfo resolveInfo) {
        this.resolveInfo = resolveInfo;
    }


    public ApplicationUpdateInfo(){

    }
    public ApplicationUpdateInfo(String name, String dowloadUrl){
        this.name = name;
        this.dowloadUrl = dowloadUrl;
    }

    protected ApplicationUpdateInfo(Parcel in) {
        name = in.readString();
        dowloadUrl = in.readString();
        updateDesc = in.readString();
    }

    public static final Creator<ApplicationUpdateInfo> CREATOR = new Creator<ApplicationUpdateInfo>() {
        @Override
        public ApplicationUpdateInfo createFromParcel(Parcel in) {
            return new ApplicationUpdateInfo(in);
        }

        @Override
        public ApplicationUpdateInfo[] newArray(int size) {
            return new ApplicationUpdateInfo[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDowloadUrl() {
        return dowloadUrl;
    }

    public void setDowloadUrl(String dowloadUrl) {
        this.dowloadUrl = dowloadUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void readFromParcel(Parcel dest){
        this.name = dest.readString();

        this.dowloadUrl = dest.readString();

    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(dowloadUrl);
        parcel.writeString(updateDesc);
    }
}
