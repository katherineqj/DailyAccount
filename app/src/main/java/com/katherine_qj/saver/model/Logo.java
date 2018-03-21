package com.katherine_qj.saver.model;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by katherineqj on 2017/11/22.
 */

public class Logo extends BmobObject {

    private BmobFile file;

    public Logo(BmobFile file) {
        this.file = file;
    }

    public Logo(String tableName, BmobFile file) {
        super(tableName);
        this.file = file;
    }

    public BmobFile getFile() {
        return file;
    }

    public void setFile(BmobFile file) {
        this.file = file;
    }
}
