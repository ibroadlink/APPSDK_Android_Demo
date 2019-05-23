package cn.com.broadlink.blappsdkdemo.data.auth;

import java.util.ArrayList;

import cn.com.broadlink.base.BLBaseResult;


/**
 * Created by Administrator on 2017/2/18 0018.
 */

public class AuthQueryDevAuthListResult extends BLBaseResult {

    private ArrayList<AuthorityInfo> result;

    public ArrayList<AuthorityInfo> getResult() {
        return result;
    }

    public void setResult(ArrayList<AuthorityInfo> result) {
        this.result = result;
    }
}
