package cn.com.broadlink.blappsdkdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.broadlink.account.BLAccount;
import cn.com.broadlink.base.BLLoginResult;
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.family.manager.BLSFamilyManager;
import cn.com.broadlink.blappsdkdemo.activity.base.BaseActivity;
import cn.com.broadlink.blappsdkdemo.service.BLLocalFamilyManager;
import cn.com.broadlink.sdk.BLLet;

import cn.com.broadlink.blappsdkdemo.common.BLFileUtils;

/**
 * 启动页面
 * Created by YeJin on 2016/5/9.
 */
public class LoadingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        new Timer().schedule(new TimerTask(){

            @Override
            public void run() {
                copyAssertJSToAPP();
                toActivity();
            }
        }, 1000);
    }

    private void toActivity(){

        if(!TextUtils.isEmpty(BLApplication.mBLUserInfoUnits.getUserid())
               && !TextUtils.isEmpty(BLApplication.mBLUserInfoUnits.getLoginsession())) {

           //本地登录
           BLLoginResult loginResult = new BLLoginResult();
           loginResult.setUserid(BLApplication.mBLUserInfoUnits.getUserid());
           loginResult.setLoginsession(BLApplication.mBLUserInfoUnits.getLoginsession());
           loginResult.setIconpath(BLApplication.mBLUserInfoUnits.getIconpath());
           loginResult.setNickname(BLApplication.mBLUserInfoUnits.getNickname());
           loginResult.setSex(BLApplication.mBLUserInfoUnits.getSex());
           loginResult.setLoginip(BLApplication.mBLUserInfoUnits.getLoginip());
           loginResult.setLogintime(BLApplication.mBLUserInfoUnits.getLogintime());

           BLAccount.localLogin(loginResult);
           BLSFamilyManager.getInstance().setUserid(BLApplication.mBLUserInfoUnits.getUserid());
           BLSFamilyManager.getInstance().setLoginsession(BLApplication.mBLUserInfoUnits.getLoginsession());
           BLLocalFamilyManager.getInstance().setCurrentFamilyInfo(BLApplication.mBLUserInfoUnits.getCachedFamilyInfo());
        }

        // 已登录用户直接跳转到主页面
        Intent intent = new Intent();
        intent.setClass(LoadingActivity.this, MainActivity.class);
        startActivity(intent);

        finish();
    }

    //将assert目录下的文件拷贝到本地文件夹
    public void copyAssertJSToAPP() {
        String trgPath = BLLet.Controller.queryUIPath() + File.separator + "cordova.js";
        String srcPath = "js/cordova.js";
        BLFileUtils.copyAssertToSDCard(LoadingActivity.this, srcPath , trgPath);
    }
}
