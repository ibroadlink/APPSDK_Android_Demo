package cn.com.broadlink.blappsdkdemo.data;

import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.BuildConfig;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.sdk.BLLet;

public class BaseHeadParam {

    private String mobileinfo;

    private String userid;

    private String timestamp;

    private String token;

    private String language;

    private String system = "android";

    private String appPlatform = "android";

    private String appVersion = BuildConfig.VERSION_NAME;

    private String locate;

    private String licenseid;

    //家庭中设置的国家地区代码
    private String countryCode;

    private String identity;

    public BaseHeadParam() {
        initBaseHeadParam();
    }
    
    public BaseHeadParam(String timestamp, String token) {
        this.timestamp = timestamp;
        this.token = token;
        initBaseHeadParam();
    }

    private void initBaseHeadParam(){
        this.locate = BLCommonUtils.getCountry();
        this.language = BLCommonUtils.getLanguage();
        this.userid = BLApplication.mBLUserInfoUnits.getUserid();
        this.mobileinfo = android.os.Build.MODEL;
        this.licenseid = BLLet.getLicenseId();
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getLocate() {
        return locate;
    }

    public void setLocate(String locate) {
        this.locate = locate;
    }

    public String getLicenseid() {
        return licenseid;
    }

    public void setLicenseid(String licenseid) {
        this.licenseid = licenseid;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getAppPlatform() {
        return appPlatform;
    }

    public void setAppPlatform(String appPlatform) {
        this.appPlatform = appPlatform;
    }

    public String getMobileinfo() {
        return mobileinfo;
    }

    public void setMobileinfo(String mobileinfo) {
        this.mobileinfo = mobileinfo;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }
}
