package cn.com.broadlink.blappsdkdemo.presenter;

import cn.com.broadlink.sdk.result.controller.BLDeviceConfigResult;

/**
 * Created by YeJin on 2016/5/10.
 */
public interface DevConfigListener {
    /**开始配置**/
    void configStart();

    void configEnd(BLDeviceConfigResult result);
}
