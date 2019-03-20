package com.example.demo.router;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class SimpleRouter {
    private static final String TAG = "simplerouter";
    private static SimpleRouter mInstance;
    private Context mContext;
    private Map<String, Object> routerMap;
    private static final String PAGE_NAME = "pageName";
    private Bundle mBundle;

    private SimpleRouter() {

    }

    public static SimpleRouter getInstance() {

        if (mInstance == null) {

            synchronized (SimpleRouter.class) {
                if (mInstance == null) {
                    mInstance = new SimpleRouter();
                }
            }
        }
        return mInstance;

    }


    public void inject(Application application) {
        routerMap = new HashMap<>();
        try {
            PackageInfo packageInfo = application.getPackageManager().getPackageInfo(
                    application.getPackageName(), PackageManager.GET_ACTIVITIES);
            ActivityInfo[] activityInfos = packageInfo.activities;
            Bundle bundle;
            ActivityInfo metaDataInfo;
            for (ActivityInfo activityInfo : activityInfos) {
                routerMap.put(activityInfo.name, Class.forName(activityInfo.name));
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void navigate(String name) {
        navigate(name, new Bundle());


    }

    public SimpleRouter with(Context context) {
        this.mContext = context;
        return this;
    }

    public void navigate(String name, Bundle bundle) {


        Intent intent = new Intent(mContext, (Class<?>) routerMap.get(name));
        mContext.startActivity(intent, bundle);
    }
}
