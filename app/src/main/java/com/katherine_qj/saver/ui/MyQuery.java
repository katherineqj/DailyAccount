package com.katherine_qj.saver.ui;

import com.katherine_qj.saver.model.UploadInfo;

import cn.bmob.v3.BmobQuery;

/**
 * Created by katherineqj on 2018/1/27.
 */

public class MyQuery {

    private int task;
    public BmobQuery<UploadInfo> query;

    public int getTask() {
        return task;
    }

    public void setTask(int task) {
        this.task = task;
    }
}
