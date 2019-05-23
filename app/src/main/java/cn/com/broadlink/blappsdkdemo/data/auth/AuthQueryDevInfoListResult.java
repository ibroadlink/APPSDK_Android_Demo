package cn.com.broadlink.blappsdkdemo.data.auth;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.base.BLBaseResult;


/**
 * Created by YeJin on 2016/9/25.
 */
public class AuthQueryDevInfoListResult extends BLBaseResult {
    private List<DevAuthInfo> data =  new ArrayList<>();

    public List<DevAuthInfo> getData() {
        return data;
    }

    public void setData(List<DevAuthInfo> data) {
        this.data = data;
    }
}
