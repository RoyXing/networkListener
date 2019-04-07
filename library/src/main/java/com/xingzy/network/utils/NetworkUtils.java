package com.xingzy.network.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

import com.xingzy.network.NetworkManger;
import com.xingzy.network.type.NetType;

public class NetworkUtils {

    @SuppressLint("MissingPermission")
    public static boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) NetworkManger.getDefault()
                .getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) return false;

//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
//            Network[] allNetworks = manager.getAllNetworks();
//            if (allNetworks!=null){
//                for (Network allNetwork : allNetworks) {
//                    if (allNetwork.)
//                }
//            }
//        } else {
        NetworkInfo[] allNetworkInfo = manager.getAllNetworkInfo();
        if (allNetworkInfo != null) {
            for (NetworkInfo networkInfo : allNetworkInfo) {
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
//        }
        return false;
    }

    @SuppressLint("MissingPermission")
    public static NetType getNetType() {
        ConnectivityManager manager = (ConnectivityManager) NetworkManger.getDefault().getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return NetType.NONE;
        }
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return NetType.NONE;
        }
        int type = networkInfo.getType();
        if (type == ConnectivityManager.TYPE_MOBILE) {
            if (networkInfo.getExtraInfo().toLowerCase().equals("cmnet")) {
                return NetType.CMNET;
            } else {
                return NetType.CMWAP;
            }
        } else if (type == ConnectivityManager.TYPE_WIFI) {
            return NetType.WIFI;
        }
        return NetType.NONE;
    }

    public static void openSetting(Context context, int requestCode) {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName("com.android.settings",
                "com.android.setting.WirelessSettings");
        intent.setComponent(componentName);
        intent.setAction("android.intent.action.VIEW");
        ((Activity) context).startActivityForResult(intent, requestCode);
    }


}
