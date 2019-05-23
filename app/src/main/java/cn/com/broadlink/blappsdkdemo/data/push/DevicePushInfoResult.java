package cn.com.broadlink.blappsdkdemo.data.push;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.base.BLBaseResult;


/**
 * Created by YeJin on 2017/7/13.
 */

public class DevicePushInfoResult extends BLBaseResult {
    private List<DevicePushInfo> devinfo = new ArrayList<>();

    public List<DevicePushInfo> getDevinfo() {
        return devinfo;
    }

    public void setDevinfo(List<DevicePushInfo> devinfo) {
        this.devinfo = devinfo;
    }
}
