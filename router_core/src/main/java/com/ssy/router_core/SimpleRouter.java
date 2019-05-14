package com.ssy.router_core;

import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

public class SimpleRouter {

    private static final String SDK_NAME = "SimpleRouter";
    private static final String TAG = "SimpleRouter";
    private static Application mContext;
    private static String ROUTE_ROOT_PAKCAGE;
    private static final String SEPARATOR = "_";
    private static final String SUFFIX_ROOT = "Root";

    public static void init(Application application) {
        mContext = application;
        ROUTE_ROOT_PAKCAGE = Utils.getPackageName(mContext);
        try {
            loadInfo();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "初始化失败!", e);
        }


    }

    /**
     * 分组表制作
     */
    private static void loadInfo() throws PackageManager.NameNotFoundException, InterruptedException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        //获得所有 APT 生成的路由类全类名（路由表）
        Set<String> routerMap = ClassUtil.getFileNameByPackageName(mContext, ROUTE_ROOT_PAKCAGE);
        for (String className : routerMap) {
            if (className.startsWith(ROUTE_ROOT_PAKCAGE + "." + SDK_NAME + SEPARATOR + SUFFIX_ROOT)) {
                //root 中注册的是分组信息 将分组信息加入仓库中
                ((IRouteRoot) Class.forName(className).getConstructor().newInstance()).loadInto(Warehouse.groupsIndex);
            }
        }
        for (Map.Entry<String, Class<? extends IRouteGroup>> stringClassEntry : Warehouse.groupsIndex.entrySet()) {
            Log.d(TAG, "Root映射表[ " + stringClassEntry.getKey() + " : " + stringClassEntry.getValue() + "]");
        }

    }


}
