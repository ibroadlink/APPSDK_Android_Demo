package cn.com.broadlink.blappsdkdemo.data.push;


import cn.com.broadlink.blappsdkdemo.mvp.model.PushModel;

/**
 * Created by YeJin on 2017/7/12.
 */

public class DevicePushInfo {
    private String did;

    private String action;

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isPushEnable() {
        return action.equals(PushModel.PUSH_ACTION_DEV_FOLLOW);
    }
}
