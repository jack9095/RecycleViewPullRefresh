package com.retrofit.wangfei.recycleviewpullrefresh;

import android.app.Application;
import android.content.Context;


/**
 * Created by Android Studio
 * User: wangfei
 * Date: 2016-04-14
 * Time: 9:57
 * QQ: 929728742
 * Description:
 */
public class MyApplication extends Application {
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = (MyApplication) getApplicationContext();
    }

    // 获取ApplicationContext
    public static Context getContext() {
        return instance;
    }
}
