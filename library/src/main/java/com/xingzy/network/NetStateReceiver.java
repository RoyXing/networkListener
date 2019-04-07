package com.xingzy.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.xingzy.network.annotation.Network;
import com.xingzy.network.listener.NetChangeObserver;
import com.xingzy.network.type.NetType;
import com.xingzy.network.utils.Constants;
import com.xingzy.network.utils.NetworkUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NetStateReceiver extends BroadcastReceiver {

    private NetType netType;
    private NetChangeObserver netChangeObserver;
    private Map<Object, List<MethodManger>> map;

    public NetStateReceiver() {
        netType = NetType.NONE;
        map = new HashMap<>();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }
        if (intent.getAction().equals(Constants.ANDROID_NET_CHANGE_ACTION)) {
            Log.e(Constants.LOG_TAG, "网络发生改变了");
            netType = NetworkUtils.getNetType();
            if (NetworkUtils.isNetworkAvailable()) {
                if (netChangeObserver != null) {
                    netChangeObserver.onConnect(netType);
                }
                Log.e(Constants.LOG_TAG, "网络连接成功");
            } else {
                if (netChangeObserver != null) {
                    netChangeObserver.onDisConnect();
                }
                Log.e(Constants.LOG_TAG, "网络断开连接");
            }
            post(netType);
        }
    }

    private void post(NetType netType) {
        Set<Object> set = map.keySet();
        for (Object o : set) {
            List<MethodManger> methodMangers = map.get(o);
            if (methodMangers != null) {
                for (MethodManger methodManger : methodMangers) {
                    //当前方法的参数类型 是否和传入的参数类型匹配
                    if (methodManger.getType().isAssignableFrom(netType.getClass())) {
                        switch (methodManger.getNetType()) {
                            case AUTO:
                                invoke(methodManger, o, netType);
                                break;
                            case WIFI:
                                if (netType == NetType.WIFI || netType == NetType.NONE) {
                                    invoke(methodManger, o, netType);
                                }
                                break;
                            case CMWAP:
                                if (netType == NetType.CMWAP || netType == NetType.NONE) {
                                    invoke(methodManger, o, netType);
                                }
                                break;
                            case CMNET:
                                if (netType == NetType.CMNET || netType == NetType.NONE) {
                                    invoke(methodManger, o, netType);
                                }
                                break;
                        }
                    }
                }
            }
        }
    }

    private void invoke(MethodManger methodManger, Object o, NetType netType) {
        try {
            methodManger.getMethod().invoke(o, netType);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void setNetChangeObserver(NetChangeObserver netChangeObserver) {
        this.netChangeObserver = netChangeObserver;
    }

    public void registerObserver(Object object) {
        List<MethodManger> methodMangers = map.get(object);
        if (methodMangers == null) {
            methodMangers = findAnnotationMethod(object);
            map.put(object, methodMangers);
        }
    }

    private List<MethodManger> findAnnotationMethod(Object object) {
        List<MethodManger> methodMangers = new ArrayList<>();
        Class<?> clazz = object.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            Network annotation = method.getAnnotation(Network.class);
            if (annotation != null) {
                Type type = method.getReturnType();
                if (!"void".equalsIgnoreCase(type.toString())) {
                    throw new RuntimeException(method.getName() + "方法不能有返回值");
                }
                Class<?>[] types = method.getParameterTypes();
                if (types.length != 1) {
                    throw new RuntimeException(method.getName() + "方法有且只有一个参数");
                }

                method.getTypeParameters();
                MethodManger methodManger = new MethodManger(types[0], annotation.netType(), method);
                methodMangers.add(methodManger);
            }
        }
        return methodMangers;
    }

    public void unRegisterObserver(Object object) {
        if (!map.isEmpty()) {
            map.remove(object);
        }
        Log.e(Constants.LOG_TAG, object.getClass().getName() + "注销成功");
    }

    public void unRegisterAllObserver() {
        if (!map.isEmpty()) {
            map.clear();
        }
        NetworkManger.getDefault().unRegisterReceiver(this);
        map = null;
        Log.e(Constants.LOG_TAG, "注销所有成功");
    }
}
