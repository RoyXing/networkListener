package com.xingzy.network;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkRequest;
import android.os.Build;

import com.xingzy.network.core.NetworkCallbackImpl;
import com.xingzy.network.listener.NetChangeObserver;
import com.xingzy.network.utils.Constants;

public class NetworkManger {

    private static volatile NetworkManger instance;

    private Application application;

    private NetStateReceiver receiver;

    private ConnectivityManager.NetworkCallback callback;

    private NetworkManger() {
    }

    public void setListener(NetChangeObserver observer) {
        receiver.setNetChangeObserver(observer);
    }

    public static NetworkManger getDefault() {
        if (instance == null) {
            synchronized (NetStateReceiver.class) {
                if (instance == null) {
                    instance = new NetworkManger();
                }
            }
        }
        return instance;
    }

    @SuppressLint("MissingPermission")
    public void init(Application application) {
        if (application == null)
            return;
        this.application = application;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            callback = new NetworkCallbackImpl();
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            NetworkRequest request = builder.build();
            ConnectivityManager manager = (ConnectivityManager) NetworkManger.getDefault().getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (manager != null) {
                manager.registerNetworkCallback(request, callback);
            }
        } else {
            receiver = new NetStateReceiver();
            //动态广播注册
            IntentFilter filter = new IntentFilter();
            filter.addAction(Constants.ANDROID_NET_CHANGE_ACTION);
            application.registerReceiver(receiver, filter);
        }
    }

    public Application getApplication() {
        if (application == null) {
            throw new RuntimeException("Application need init");
        }
        return application;
    }

    public void registerObserver(Object object) {
        if (receiver != null)
            receiver.registerObserver(object);
        if (callback != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((NetworkCallbackImpl) callback).registerObserver(object);
            }
        }
    }

    public void unRegisterObserver(Object object) {
        if (receiver != null)
            receiver.unRegisterObserver(object);
        if (callback != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((NetworkCallbackImpl) callback).registerObserver(object);
            }
        }
    }

    public void unRegisterAllObserver() {
        if (receiver != null)
            receiver.unRegisterAllObserver();
        if (callback != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((NetworkCallbackImpl) callback).unRegisterAllObserver();
            }
        }

    }

    void unRegisterReceiver(NetStateReceiver netStateReceiver) {
        if (receiver != null)
            application.unregisterReceiver(receiver);
    }
}
