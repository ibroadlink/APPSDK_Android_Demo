package cn.com.broadlink.blappsdkdemo.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import cn.com.broadlink.blappsdkdemo.activity.family.result.BLSFamilyInfo;
import cn.com.broadlink.blappsdkdemo.activity.family.manager.BLSFamilyManager;

/**
 * 用户登录信息返回保存
 * Created by YeJin on 2016/5/9.
 */
public class BLUserInfoUnits {

    private SharedPreferences mSharedPreferences;

    /**用户ID**/
    private String userid;

    /**用户session**/
    private String loginsession;

    /**昵称**/
    private String nickname;

    /**头像地址**/
    private String iconpath;

    /**登录ID**/
    private String loginip;

    /**登录时间**/
    private String logintime;

    /**性别**/
    private String sex;

    private String flag;

    private String phone;

    private String email;

    private String birthday;

    public BLUserInfoUnits(Context context){
        mSharedPreferences = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        this.userid = mSharedPreferences.getString("userid", null);
        this.loginsession = mSharedPreferences.getString("loginsession", null);
        this.nickname = mSharedPreferences.getString("nickname", null);
        this.iconpath = mSharedPreferences.getString("iconpath", null);
        this.loginip = mSharedPreferences.getString("loginip", null);
        this.logintime = mSharedPreferences.getString("logintime", null);
        this.sex = mSharedPreferences.getString("sex", null);
        this.flag = mSharedPreferences.getString("flag", null);
        this.phone = mSharedPreferences.getString("phone", null);
        this.email = mSharedPreferences.getString("email", null);
        this.birthday = mSharedPreferences.getString("birthday", null);
    }

    public void login(String userid, String loginsession, String nickname, String iconpath, String loginip,
                      String logintime, String sex, String flag, String phone, String email, String birthday){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("userid", userid);
        editor.putString("loginsession", loginsession);
        editor.putString("nickname", nickname);
        editor.putString("iconpath", iconpath);
        editor.putString("loginip", loginip);
        editor.putString("logintime", logintime);
        editor.putString("sex", sex);
        editor.putString("flag", flag);
        editor.putString("phone", phone);
        editor.putString("email", email);
        editor.putString("birthday", birthday);
        editor.commit();
        this.userid = userid;
        this.loginsession = loginsession;
        this.nickname = nickname;
        this.iconpath = iconpath;
        this.loginip = loginip;
        this.logintime = logintime;
        this.sex = sex;
        this.flag = flag;
        this.phone = phone;
        this.email = email;
        this.birthday = birthday;

        BLSFamilyManager.getInstance().setUserid(userid);
        BLSFamilyManager.getInstance().setLoginsession(loginsession);
    }

    public void loginOut(){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("userid", null);
        editor.putString("loginsession", null);
        editor.putString("nickname", null);
        editor.putString("iconpath", null);
        editor.putString("loginip", null);
        editor.putString("logintime", null);
        editor.putString("sex", null);
        editor.putString("flag", null);
        editor.putString("phone", null);
        editor.putString("email", null);
        editor.putString("birthday", null);
        editor.commit();
        this.userid = null;
        this.loginsession = null;
        this.nickname = null;
        this.iconpath = null;
        this.loginip = null;
        this.logintime = null;
        this.sex = null;
        this.flag = null;
        this.phone = null;
        this.email = null;
        this.birthday = null;

        BLSFamilyManager.getInstance().setUserid(null);
        BLSFamilyManager.getInstance().setLoginsession(null);
    }

    public Boolean checkAccountLogin() {
        if (TextUtils.isEmpty(this.userid) || TextUtils.isEmpty(this.loginsession)) {
            return false;
        } else {
            return true;
        }
    }
    
    public void cacheFamilyInfo(BLSFamilyInfo familyInfo){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("family", JSON.toJSONString(familyInfo));
        editor.commit();
    }
    
    public BLSFamilyInfo getCachedFamilyInfo(){
        final String family = mSharedPreferences.getString("family", null);
        return JSON.parseObject(family, BLSFamilyInfo.class);
    }

    public String getUserid() {
        return userid;
    }

    public String getLoginsession() {
        return loginsession;
    }

    public String getNickname() {
        return nickname;
    }

    public String getIconpath() {
        return iconpath;
    }

    public String getLoginip() {
        return loginip;
    }

    public String getLogintime() {
        return logintime;
    }

    public String getSex() {
        return sex;
    }

    public String getFlag() {
        return flag;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
