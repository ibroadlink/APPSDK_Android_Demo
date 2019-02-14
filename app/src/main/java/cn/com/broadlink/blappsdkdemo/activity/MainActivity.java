package cn.com.broadlink.blappsdkdemo.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import java.sql.SQLException;
import java.util.List;

import cn.com.broadlink.blappsdkdemo.BLApplcation;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.Account.AccountAndSecurityActivity;
import cn.com.broadlink.blappsdkdemo.activity.Account.AccountMainActivity;
import cn.com.broadlink.blappsdkdemo.activity.Check.NetworkCheckActivity;
import cn.com.broadlink.blappsdkdemo.activity.Device.DeviceMainActivity;
import cn.com.broadlink.blappsdkdemo.activity.Family.FamilyListActivity;
import cn.com.broadlink.blappsdkdemo.activity.IRCode.IRCodeOptActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.db.BLDeviceInfo;
import cn.com.broadlink.blappsdkdemo.db.BLDeviceInfoDao;
import cn.com.broadlink.blappsdkdemo.service.BLLocalDeviceManager;
import cn.com.broadlink.blappsdkdemo.view.BLProgressDialog;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;

public class MainActivity extends TitleActivity {

    private Button mDeviceBtn, mAccountBtn, mFamilyBtn, mIRCodeBtn, mNetworkCheckBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.Main_View);

        addDevice();
        findView();
        setListener();
    }

    private void findView() {
        mDeviceBtn = (Button) findViewById(R.id.btn_device_control);
        mAccountBtn = (Button) findViewById(R.id.btn_account_control);
        mFamilyBtn = (Button) findViewById(R.id.btn_family_control);
        mIRCodeBtn = (Button) findViewById(R.id.btn_ircode_control);
        mNetworkCheckBtn = (Button) findViewById(R.id.btn_network_check);
    }

    private void setListener(){

        mDeviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, DeviceMainActivity.class);
                startActivity(intent);
            }
        });

        mAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userId = BLApplcation.mBLUserInfoUnits.getUserid();
                String loginSession = BLApplcation.mBLUserInfoUnits.getLoginsession();

                Intent intent = new Intent();
                if (TextUtils.isEmpty(userId) && TextUtils.isEmpty(loginSession)) {
                    intent.setClass(MainActivity.this, AccountMainActivity.class);
                } else {
                    intent.setClass(MainActivity.this, AccountAndSecurityActivity.class);
                }
                startActivity(intent);
            }
        });

        mFamilyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (BLApplcation.mBLUserInfoUnits.checkAccountLogin()) {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, FamilyListActivity.class);
                    startActivity(intent);
                } else {
                    BLCommonUtils.toastShow(MainActivity.this, "Please Login First!");
                }
            }
        });

        mIRCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (BLApplcation.mBLUserInfoUnits.checkAccountLogin()) {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, IRCodeOptActivity.class);
                    startActivity(intent);
                } else {
                    BLCommonUtils.toastShow(MainActivity.this, "Please Login First!");
                }
            }
        });

        mNetworkCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, NetworkCheckActivity.class);
                startActivity(intent);
            }
        });
    }

    private void addDevice() {
        try {
            BLDeviceInfoDao blDeviceInfoDao = new BLDeviceInfoDao(getHelper());
            List<BLDeviceInfo> deviceInfoList = blDeviceInfoDao.queryDevList();

            if (deviceInfoList != null) {
                for (BLDeviceInfo dev : deviceInfoList) {
                    BLDNADevice dnaDev = dev.cloneBLDNADevice();
                    BLLocalDeviceManager.getInstance().addDeviceIntoSDK(dnaDev);
                }
            }

//            BLDNADevice deviceInfo = new BLDNADevice();
//            deviceInfo.setDid("00000000000000000000780f7716bfa8");
//            deviceInfo.setPid("0000000000000000000000002a4e0000");
//            deviceInfo.setMac("78:0f:77:16:bf:a8");
//            deviceInfo.setName("AUX");
//            deviceInfo.setType(20010);
//            deviceInfo.setKey("3801147427445811224671461bf25133");
//            deviceInfo.setId(1);
//            deviceInfo.setPassword(1646382889);
//
//            BLLocalDeviceManager.getInstance().addDeviceIntoSDK(deviceInfo);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
