package cn.com.broadlink.blappsdkdemo.activity.plugin;

import android.os.Bundle;
import android.widget.TextView;

import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLLog;
import cn.com.broadlink.sdk.BLLet;

public class VersionInfoActivity extends TitleActivity {

    private TextView mTvAppVersion;
    private TextView mTvSdkVersion;
    private TextView mTvSdkLid;
    private TextView mTvSdkUid;
    private TextView mTvCloudName;
    private TextView mTvCloudAddress;
    private TextView mTvCloudVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version_info);
        setTitle("Version Info");
        setBackWhiteVisible();
        initView();

        mTvAppVersion.setText(BLCommonUtils.getVersionInfo(mApplication));
        mTvSdkVersion.setText(BLLet.getSDKVersion());
        mTvCloudName.setText("International China Cluster");
        mTvCloudAddress.setText("appservice.ibroadlink.com");
        mTvCloudVersion.setText("5.2.1");
        mTvSdkLid.setText(BLLet.getLicenseId());
        mTvSdkUid.setText(BLApplication.mBLUserInfoUnits.getUserid());

        BLLog.d("version-lid", BLLet.getLicenseId());
        BLLog.d("version-uid", BLApplication.mBLUserInfoUnits.getUserid());
    }

    private void initView() {
        mTvAppVersion = (TextView) findViewById(R.id.tv_app_version);
        mTvSdkVersion = (TextView) findViewById(R.id.tv_sdk_version);
        mTvSdkLid = (TextView) findViewById(R.id.tv_sdk_lid);
        mTvSdkUid = (TextView) findViewById(R.id.tv_sdk_uid);
        mTvCloudName = (TextView) findViewById(R.id.tv_cloud_name);
        mTvCloudAddress = (TextView) findViewById(R.id.tv_cloud_address);
        mTvCloudVersion = (TextView) findViewById(R.id.tv_cloud_version);
    }
}
