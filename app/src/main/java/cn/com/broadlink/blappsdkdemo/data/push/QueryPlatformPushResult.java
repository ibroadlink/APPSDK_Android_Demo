package cn.com.broadlink.blappsdkdemo.data.push;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.base.BLBaseResult;


/**
 * Created by YeJin on 2017/7/13.
 */

public class QueryPlatformPushResult extends BLBaseResult {
    private List<PushTypeInfo> tousertypelist = new ArrayList<>();

    public List<PushTypeInfo> getTousertypelist() {
        return tousertypelist;
    }

    public QueryPlatformPushResult() {
    }
}
