package com.xingzy.network.core;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.xingzy.network.MethodManger;
import com.xingzy.network.type.NetType;
import com.xingzy.network.utils.Constants;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NetworkCallbackImpl extends ConnectivityManager.NetworkCallback {

    private Map<Object, List<MethodManger>> map;

    public NetworkCallbackImpl() {
        map = new HashMap<>();
    }

    @Override
    public void onAvailable(Network network) {
        super.onAvailable(network);
        Log.e(Constants.LOG_TAG, "网络已连接");
    }

    @Override
    public void onLost(Network network) {
        super.onLost(network);
        Log.e(Constants.LOG_TAG, "网络已断开");
        post(NetType.NONE);
    }

    @Override
    public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities);
        if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            NetType netType = NetType.AUTO;
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.e(Constants.LOG_TAG, "网络变更，类型为wifi");
                netType = NetType.WIFI;
            } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.e(Constants.LOG_TAG, "网络变更，类型为手机自带网络");
                netType = NetType.CMWAP;
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
            com.xingzy.network.annotation.Network annotation = method.getAnnotation(com.xingzy.network.annotation.Network.class);
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
        map = null;
        Log.e(Constants.LOG_TAG, "注销所有成功");
    }
}
