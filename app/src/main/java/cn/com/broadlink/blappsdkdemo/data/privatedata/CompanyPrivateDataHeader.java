package cn.com.broadlink.blappsdkdemo.data.privatedata;


import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.data.BaseHeadParam;
import cn.com.broadlink.sdk.BLLet;

/**
 * Created by YeJin on 2017/7/12.
 */

public class CompanyPrivateDataHeader extends BaseHeadParam {
    public String companyid;
    public String messageId;
    public String loginsession;
    public CompanyPrivateDataHeader() {
        super();
        companyid = BLLet.getCompanyid();
        messageId = "CompanyPrivateData" + System.currentTimeMillis();
        loginsession = BLApplication.mBLUserInfoUnits.getLoginsession();
    }
}
