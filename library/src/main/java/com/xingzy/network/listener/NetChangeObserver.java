package com.xingzy.network.listener;

import com.xingzy.network.type.NetType;

public interface NetChangeObserver {

    //网络连接
    void onConnect(NetType netType);

    //断开连接
    void onDisConnect();
}
