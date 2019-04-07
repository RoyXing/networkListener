package com.xingzy;

import android.app.Application;

import com.xingzy.network.NetworkManger;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        NetworkManger.getDefault().init(this);
    }
}
