package com.ssy.router_core;

import android.app.Application;
import android.arch.core.executor.DefaultTaskExecutor;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

import dalvik.system.DexFile;

public class ClassUtil {


    public static Set<String> getFileNameByPackageName(Application application, final String packageName) throws PackageManager.NameNotFoundException, InterruptedException {
        final Set<String> classNames = new HashSet<>();
        List<String> paths = getSourcePath(application);
        //使用同步计数器判断均处理完成
        final CountDownLatch countDownLatch = new CountDownLatch(paths.size());
        ThreadPoolExecutor threadPoolExecutor = DefaultPoolExecutor.newDefaultPoolExecutor(paths.size());
        for (final String path : paths) {
            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    DexFile dexFile = null;
                    try {

                        //加载 apk 中的dex 并遍历获得所有包名为{packageName}的类
                        Enumeration<String> dexEntries = dexFile.entries();
                        while (dexEntries.hasMoreElements()) {
                            String className = dexEntries.nextElement();
                            if (!TextUtils.isEmpty(className) && className.startsWith(packageName)) {
                                classNames.add(className);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (null != dexFile) {
                            try {
                                dexFile.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        countDownLatch.countDown();
                    }

                }
            });
        }
        countDownLatch.wait();
        return classNames;
    }

    /**
     * 获得程序所有的APK（instant run 会产生很多split apk）
     *
     * @param context
     * @return
     */
    private static List<String> getSourcePath(Context context) throws PackageManager.NameNotFoundException {
        ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
        List<String> sourcePaths = new ArrayList<>();
        sourcePaths.add(applicationInfo.sourceDir);
        //instant run
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (null != applicationInfo.splitSourceDirs) {
                sourcePaths.addAll(Arrays.asList(applicationInfo.splitSourceDirs));
            }
        }
        return sourcePaths;
    }
}
