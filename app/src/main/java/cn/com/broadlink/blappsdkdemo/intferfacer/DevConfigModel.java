package cn.com.broadlink.blappsdkdemo.intferfacer;

import cn.com.broadlink.blappsdkdemo.presenter.DevConfigListener;
import cn.com.broadlink.sdk.param.controller.BLDeviceConfigParam;

/**
 * Created by YeJin on 2016/5/10.
 */
public interface DevConfigModel {
    void startConfig(BLDeviceConfigParam deviceConfigParam, DevConfigListener devConfigListener);

    void cancelConfig();
}
