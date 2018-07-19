package cn.com.broadlink.blappsdkdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.broadlink.account.BLAccount;
import cn.com.broadlink.base.BLLoginResult;
import cn.com.broadlink.blappsdkdemo.BLApplcation;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.db.BLDeviceInfo;
import cn.com.broadlink.blappsdkdemo.db.BLDeviceInfoDao;
import cn.com.broadlink.blappsdkdemo.service.BLLocalDeviceManager;
import cn.com.broadlink.sdk.BLLet;

import cn.com.broadlink.blappsdkdemo.common.BLFileUtils;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;

/**
 * 启动页面
 * Created by YeJin on 2016/5/9.
 */
public class LoadingActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_layout);

        new Timer().schedule(new TimerTask(){

            @Override
            public void run() {
                copyAssertJSToAPP();
                toActivity();
            }
        }, 1000);
    }

    private void toActivity(){

        if(!TextUtils.isEmpty(BLApplcation.mBLUserInfoUnits.getUserid())
               && !TextUtils.isEmpty(BLApplcation.mBLUserInfoUnits.getLoginsession())) {

           //本地登录
           BLLoginResult loginResult = new BLLoginResult();
           loginResult.setUserid(BLApplcation.mBLUserInfoUnits.getUserid());
           loginResult.setLoginsession(BLApplcation.mBLUserInfoUnits.getLoginsession());
           loginResult.setIconpath(BLApplcation.mBLUserInfoUnits.getIconpath());
           loginResult.setNickname(BLApplcation.mBLUserInfoUnits.getNickname());
           loginResult.setSex(BLApplcation.mBLUserInfoUnits.getSex());
           loginResult.setLoginip(BLApplcation.mBLUserInfoUnits.getLoginip());
           loginResult.setLogintime(BLApplcation.mBLUserInfoUnits.getLogintime());

           BLAccount.localLogin(loginResult);
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
