package com.katherine_qj.saver.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by katherineqj on 2018/1/27.
 */
public class APK extends BmobObject {

    private String name;
    private String fileUrl;
    private Integer version;
    private Boolean tooOld;
    private String info;

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getTooOld() {
        return tooOld;
    }

    public void setTooOld(Boolean tooOld) {
        this.tooOld = tooOld;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
