package com.hzdongcheng.update.bean;

public class ServicesTask {
    private String packName;
    private String dowloadUrl;
    private Boolean forceStart;
    private String md5;
    public ServicesTask(){
    }

    public ServicesTask(String packName, String dowloadUrl, String md5, Boolean forceStart) {
        this.packName = packName;
        this.dowloadUrl = dowloadUrl;
        this.forceStart = forceStart;
        this.md5 = md5;

    }

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public String getDowloadUrl() {
        return dowloadUrl;
    }

    public void setDowloadUrl(String dowloadUrl) {
        this.dowloadUrl = dowloadUrl;
    }

    public Boolean getForceStart() {
        return forceStart;
    }

    public void setForceStart(Boolean forceStart) {
        this.forceStart = forceStart;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
