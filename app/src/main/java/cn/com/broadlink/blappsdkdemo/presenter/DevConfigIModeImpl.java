package cn.com.broadlink.blappsdkdemo.presenter;

import android.os.Handler;
import android.util.Log;

import cn.com.broadlink.blappsdkdemo.intferfacer.DevConfigModel;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.param.controller.BLDeviceConfigParam;
import cn.com.broadlink.sdk.result.controller.BLDeviceConfigResult;


public class DevConfigIModeImpl implements DevConfigModel{

    Handler handler = new Handler();

    @Override
    public void startConfig(final BLDeviceConfigParam deviceConfigParam,  final DevConfigListener devConfigListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        devConfigListener.configStart();
                    }
                });

                final BLDeviceConfigResult result = BLLet.Controller.deviceConfig(deviceConfigParam);
                if (result.succeed()) {
                    Log.d("BROADLINK_LET_SDK_LOG", "DeviceIP: " + result.getDevaddr());
                    Log.d("BROADLINK_LET_SDK_LOG", "Did: " + result.getDid());
                    Log.d("BROADLINK_LET_SDK_LOG", "Mac: " + result.getMac());
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        devConfigListener.configEnd(result);
                    }
                });
            }
        }).start();
    }

    @Override
    public void cancelConfig() {
        BLLet.Controller.deviceConfigCancel();
    }
}
