package cn.com.broadlink.blappsdkdemo.data.push;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户设置选定的喜好推送方式（favortype）
 * Created by YeJin on 2017/7/13.
 */

public class PlatformPushInfoParam {
    private String userid;

    private List<PlatformPushInfo> managetypeinfo = new ArrayList<>();

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public List<PlatformPushInfo> getManagetypeinfo() {
        return managetypeinfo;
    }

    public void setManagetypeinfo(List<PlatformPushInfo> managetypeinfo) {
        this.managetypeinfo = managetypeinfo;
    }
}
