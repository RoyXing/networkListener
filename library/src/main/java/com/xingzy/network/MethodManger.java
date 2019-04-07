package com.xingzy.network;

import com.xingzy.network.type.NetType;

import java.lang.reflect.Method;

public class MethodManger {

    private Class<?> type;
    private NetType netType;
    private Method method;

    public MethodManger(Class<?> type, NetType netType, Method method) {
        this.type = type;
        this.netType = netType;
        this.method = method;
    }

    public Class<?> getType() {
        return type;
    }


    public NetType getNetType() {
        return netType;
    }

    public Method getMethod() {
        return method;
    }

}

