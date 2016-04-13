package com.wxx.columnmanage;

import android.app.Application;

/**
 * Created by Administrator on 2016/4/12.
 */
public class MyApplication extends Application {

    private static MyApplication instance;
    private SQLHelper sqlHelper;

    /** 获取Application */
    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    /** 获取数据库Helper */
    public SQLHelper getSQLHelper() {
        if (sqlHelper == null) {
            sqlHelper = new SQLHelper(instance);
        }
        return sqlHelper;
    }
}
