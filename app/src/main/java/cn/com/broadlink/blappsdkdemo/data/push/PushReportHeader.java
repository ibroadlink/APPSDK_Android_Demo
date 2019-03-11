package cn.com.broadlink.blappsdkdemo.data.push;


import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.data.BaseHeadParam;
import cn.com.broadlink.blappsdkdemo.mvp.model.PushModel;

/**
 * Created by YeJin on 2017/7/12.
 */

public class PushReportHeader extends BaseHeadParam {
    private String appid;
    private String loginsession;
    
    public String getLoginsession() {
        return loginsession;
    }
    
    public void setLoginsession(String loginsession) {
        this.loginsession = loginsession;
    }
    
    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public PushReportHeader() {
        super();
        setAppid(PushModel.APPID);
        setLoginsession(BLApplication.mBLUserInfoUnits.getLoginsession());
    }
}
