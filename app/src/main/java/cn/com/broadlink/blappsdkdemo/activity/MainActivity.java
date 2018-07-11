package cn.com.broadlink.blappsdkdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import cn.com.broadlink.blappsdkdemo.BLApplcation;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.Account.AccountAndSecurityActivity;
import cn.com.broadlink.blappsdkdemo.activity.Account.AccountMainActivity;
import cn.com.broadlink.blappsdkdemo.activity.Device.DeviceMainActivity;
import cn.com.broadlink.blappsdkdemo.activity.Family.FamilyListActivity;
import cn.com.broadlink.blappsdkdemo.activity.IRCode.IRCodeOptActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;

public class MainActivity extends TitleActivity {

    private Button mDeviceBtn, mAccountBtn, mFamilyBtn, mIRCodeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.Main_View);

        findView();
        setListener();
    }

    private void findView() {
        mDeviceBtn = (Button) findViewById(R.id.btn_device_control);
        mAccountBtn = (Button) findViewById(R.id.btn_account_control);
        mFamilyBtn = (Button) findViewById(R.id.btn_family_control);
        mIRCodeBtn = (Button) findViewById(R.id.btn_ircode_control);
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

                if (BLApplcation.getmBLUserInfoUnits().checkAccountLogin()) {
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

                if (BLApplcation.getmBLUserInfoUnits().checkAccountLogin()) {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, IRCodeOptActivity.class);
                    startActivity(intent);
                } else {
                    BLCommonUtils.toastShow(MainActivity.this, "Please Login First!");
                }
            }
        });

    }

}
