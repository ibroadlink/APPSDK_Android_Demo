package cn.com.broadlink.blappsdkdemo.data.push;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YeJin on 2017/7/12.
 */

public class EditDevicePushParam {
    private String userid;

    private List<DevicePushInfo> manageinfo =  new ArrayList<>();

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public List<DevicePushInfo> getManageinfo() {
        return manageinfo;
    }

    public void setManageinfo(List<DevicePushInfo> manageinfo) {
        this.manageinfo = manageinfo;
    }
}
