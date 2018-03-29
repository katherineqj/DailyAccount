package com.katherine_qj.saver.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by katherineqj on 2018/2/3.
 */
public class Feedback extends BmobObject {

    String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
