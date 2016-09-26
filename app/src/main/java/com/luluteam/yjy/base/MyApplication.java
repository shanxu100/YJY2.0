package com.luluteam.yjy.base;

import android.app.Application;

import com.apm.APMInstance;

/**
 * Created by Guan on 2016/8/2.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        APMInstance instance=APMInstance.getInstance();
        instance.start(this);

    }
}
