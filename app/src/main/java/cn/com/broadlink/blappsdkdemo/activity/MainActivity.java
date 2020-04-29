package cn.com.broadlink.blappsdkdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import cn.com.broadlink.base.fastjson.BLJSON;
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.BuildConfig;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.account.AccountAndSecurityActivity;
import cn.com.broadlink.blappsdkdemo.activity.account.AccountMainActivity;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.activity.ble.BLEMainActivity;
import cn.com.broadlink.blappsdkdemo.activity.check.NetworkCheckActivity;
import cn.com.broadlink.blappsdkdemo.activity.device.DevMainActivity;
import cn.com.broadlink.blappsdkdemo.activity.family.FamilyListActivity;
import cn.com.broadlink.blappsdkdemo.activity.h5.CommonH5Activity;
import cn.com.broadlink.blappsdkdemo.activity.irCode.IRCodeMainActivity;
import cn.com.broadlink.blappsdkdemo.activity.product.ProductCategoryListActivity;
import cn.com.broadlink.blappsdkdemo.activity.push.PushMainActivity;
import cn.com.broadlink.blappsdkdemo.activity.space.SpaceManageActivity;
import cn.com.broadlink.blappsdkdemo.activity.websocket.WebSocketActivity;
import cn.com.broadlink.blappsdkdemo.common.AppExitHelper;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLLog;
import cn.com.broadlink.blappsdkdemo.common.PreferencesUtils;
import cn.com.broadlink.blappsdkdemo.db.dao.BLDeviceInfoDao;
import cn.com.broadlink.blappsdkdemo.db.data.BLDeviceInfo;
import cn.com.broadlink.blappsdkdemo.service.BLLocalDeviceManager;
import cn.com.broadlink.blappsdkdemo.service.BLLocalFamilyManager;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;

public class MainActivity extends TitleActivity {

    private Button mDeviceBtn, mAccountBtn, mFamilyBtn, mIRCodeBtn, mNetworkCheckBtn, mProductManageBtn, mPushManagageBtn, mBLEButton, mWebSocketButton, mSpaceButton, mH5Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_main);
        setTitle(R.string.Main_View);

        addDevice();

        findView();

        setListener();

        initView();
    }

    private void initView() {
        HashMap<String,String> testBean = new HashMap<>();
        testBean.put("1", "111");
        final String text = JSON.toJSONString(testBean);
        //final String text = getString(R.string.str_json_test);
        HashMap<String, String> paramHeaderMap = JSON.parseObject(text, new TypeReference<HashMap<String,String>>(){});
        System.out.println(JSON.toJSONString(paramHeaderMap));
    }

    private void findView() {
        mDeviceBtn = (Button) findViewById(R.id.btn_device_control);
        mAccountBtn = (Button) findViewById(R.id.btn_account_control);
        mFamilyBtn = (Button) findViewById(R.id.btn_family_control);
        mIRCodeBtn = (Button) findViewById(R.id.btn_ircode_control);
        mNetworkCheckBtn = (Button) findViewById(R.id.btn_network_check);
        mProductManageBtn = (Button) findViewById(R.id.btn_product_manage);
        mPushManagageBtn = (Button) findViewById(R.id.btn_push);
        mBLEButton = (Button) findViewById(R.id.btn_ble);
        mWebSocketButton = (Button) findViewById(R.id.btn_web_socket);
        mSpaceButton = (Button) findViewById(R.id.btn_space);
        mH5Button = (Button) findViewById(R.id.btn_h5);
    }

    private void setListener() {

        setLeftButtonOnClickListener(BuildConfig.BUILD_TYPE + BuildConfig.VERSION_NAME, getResources().getColor(R.color.title_838987_color), null, new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                // do nothing
            }
        });
        
        setRightButtonOnClickListener("Reset", getResources().getColor(R.color.bl_yellow_main_color), new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                BLCommonUtils.toActivity(mActivity, ResetActivity.class);
            }
        });

        mDeviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLoginAndFamily(true)) {
                    BLCommonUtils.toActivity(MainActivity.this, DevMainActivity.class);
                }
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

                if (checkLoginAndFamily(false)) {
                    BLCommonUtils.toActivity(MainActivity.this, FamilyListActivity.class);
                }
            }
        });

        mIRCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkLoginAndFamily(true)) {
                    BLCommonUtils.toActivity(MainActivity.this, IRCodeMainActivity.class);
                }
            }
        });

        mNetworkCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BLCommonUtils.toActivity(MainActivity.this, NetworkCheckActivity.class);
            }
        });

        mProductManageBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                BLCommonUtils.toActivity(mActivity, ProductCategoryListActivity.class);
            }
        });

        mPushManagageBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                if (checkLoginAndFamily(false)) {
                    BLCommonUtils.toActivity(MainActivity.this, PushMainActivity.class);
                }
            }
        });

        mBLEButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                BLCommonUtils.toActivity(MainActivity.this, BLEMainActivity.class);
            }
        });

        mWebSocketButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                if (checkLoginAndFamily(false)) {
                    BLCommonUtils.toActivity(MainActivity.this, WebSocketActivity.class);
                }
            }
        });

        mSpaceButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                if (checkLoginAndFamily(false)) {
                    BLCommonUtils.toActivity(MainActivity.this, SpaceManageActivity.class);
                }
            }
        });
        
        mH5Button.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {

                if (checkLoginAndFamily(false)) {
                    String lid = BLLet.getLicenseId();
                    String companyId = BLLet.getCompanyid();
                    String packageName = PreferencesUtils.getString(getApplicationContext(), "packageName", BLConstants.SDK_PACKAGE);
                    String domain = PreferencesUtils.getString(getApplicationContext(), "domain",  String.format("https://%sappservice.ibroadlink.com", lid));

                    final HashMap<String, Object> header = new HashMap<String, Object>();
                    header.put("companyId", companyId);
                    header.put("licenseid", lid);
                    header.put("userid", BLApplication.mBLUserInfoUnits.getUserid());
                    header.put("loginsession", BLApplication.mBLUserInfoUnits.getLoginsession());
                    header.put("language", BLCommonUtils.getLanguage());

                    String url = String.format("%s/appfront/v1/webui/%s", domain, packageName);
                    url = CommonH5Activity.appendUrl(url, header);

                    final Intent intent = new Intent(mActivity, CommonH5Activity.class);
                    intent.putExtra(BLConstants.INTENT_URL, url);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean checkLoginAndFamily(boolean needFamily) {
        final boolean isLogin = BLApplication.mBLUserInfoUnits.checkAccountLogin();
        final boolean isFamilySelect = BLLocalFamilyManager.getInstance().getCurrentFamilyId() != null;
        if (!isLogin) {
            BLCommonUtils.toastShow(MainActivity.this, "Please login first!");
            return false;
        }else if(needFamily && !isFamilySelect){
            BLCommonUtils.toastShow(MainActivity.this, "Please select a family first!");
            return false;
        }
        return true;
    }

    private void addDevice() {
        try {
            BLDeviceInfoDao blDeviceInfoDao = new BLDeviceInfoDao(getHelper());
            List<BLDeviceInfo> deviceInfoList = blDeviceInfoDao.queryDevList();

            if (deviceInfoList != null) {
                for (BLDeviceInfo dev : deviceInfoList) {
                    BLDNADevice dnaDev = dev.cloneBLDNADevice();
                    BLLocalDeviceManager.getInstance().addDeviceIntoSDK(dnaDev);
                    BLLog.d("localDevices", BLJSON.toJSONString(dev, true));
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
