package cn.com.broadlink.blappsdkdemo.common;

import android.annotation.SuppressLint;
import android.app.Application;

import java.lang.reflect.InvocationTargetException;

/**
 * 作者：EchoJ on 2019/2/14 17:33 <br>
 * 邮箱：echojiangyq@gmail.com <br>
 * 描述：
 */ 
public final class BLAppUtils {
    private static Application sApplication;

    public static void init(final Application app) {
        if (sApplication == null) {
            BLAppUtils.sApplication = app;
        }
    }
    
    public static Application getApp() {
        if (sApplication != null) return sApplication;
        try {
            @SuppressLint("PrivateApi")
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Object at = activityThread.getMethod("currentActivityThread").invoke(null);
            Object app = activityThread.getMethod("getApplication").invoke(at);
            if (app == null) {
                throw new NullPointerException("u should init first");
            }
            init((Application) app);
            return sApplication;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new NullPointerException("u should init first");
    }
}
