package cn.com.broadlink.blappsdkdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import java.sql.SQLException;
import java.util.List;

import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.account.AccountAndSecurityActivity;
import cn.com.broadlink.blappsdkdemo.activity.account.AccountMainActivity;
import cn.com.broadlink.blappsdkdemo.activity.check.NetworkCheckActivity;
import cn.com.broadlink.blappsdkdemo.activity.device.DevMainActivity;
import cn.com.broadlink.blappsdkdemo.activity.family.FamilyListActivity;
import cn.com.broadlink.blappsdkdemo.activity.irCode.IRCodeMainActivity;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.activity.product.ProductCategoryListActivity;
import cn.com.broadlink.blappsdkdemo.common.AppExitHelper;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.db.data.BLDeviceInfo;
import cn.com.broadlink.blappsdkdemo.db.dao.BLDeviceInfoDao;
import cn.com.broadlink.blappsdkdemo.mvp.presenter.BLPushServicePresenter;
import cn.com.broadlink.blappsdkdemo.service.BLLocalDeviceManager;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;

public class MainActivity extends TitleActivity {

    private Button mDeviceBtn, mAccountBtn, mFamilyBtn, mIRCodeBtn, mNetworkCheckBtn, mProductManageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_main);
        setTitle(R.string.Main_View);

        addDevice();
        
        findView();
        
        setListener();

        BLPushServicePresenter.getInstance(mActivity).reportPushServiceToken();
    }

    private void findView() {
        mDeviceBtn = (Button) findViewById(R.id.btn_device_control);
        mAccountBtn = (Button) findViewById(R.id.btn_account_control);
        mFamilyBtn = (Button) findViewById(R.id.btn_family_control);
        mIRCodeBtn = (Button) findViewById(R.id.btn_ircode_control);
        mNetworkCheckBtn = (Button) findViewById(R.id.btn_network_check);
        mProductManageBtn = (Button) findViewById(R.id.btn_product_manage);
    }

    private void setListener(){
        
        setRightButtonOnClickListener("Reset", getResources().getColor(R.color.bl_yellow_main_color), new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                BLCommonUtils.toActivity(mActivity, ResetActivity.class);
            }
        });

        mDeviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, DevMainActivity.class);
                startActivity(intent);
            }
        });

        mAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userId = BLApplication.mBLUserInfoUnits.getUserid();
                String loginSession = BLApplication.mBLUserInfoUnits.getLoginsession();

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

                if (BLApplication.mBLUserInfoUnits.checkAccountLogin()) {
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

                if (BLApplication.mBLUserInfoUnits.checkAccountLogin()) {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, IRCodeMainActivity.class);
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

        mProductManageBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                BLCommonUtils.toActivity(mActivity, ProductCategoryListActivity.class);
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        AppExitHelper.exit();
    }
}
