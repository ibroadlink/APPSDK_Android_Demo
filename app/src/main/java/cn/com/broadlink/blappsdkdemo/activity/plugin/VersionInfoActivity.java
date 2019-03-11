package cn.com.broadlink.blappsdkdemo.activity.plugin;

import android.os.Bundle;
import android.widget.TextView;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.sdk.BLLet;

public class VersionInfoActivity extends TitleActivity {

    private TextView mTvAppVersion;
    private TextView mTvSdkVersion;
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
    }

    private void initView() {
        mTvAppVersion = (TextView) findViewById(R.id.tv_app_version);
        mTvSdkVersion = (TextView) findViewById(R.id.tv_sdk_version);
        mTvCloudName = (TextView) findViewById(R.id.tv_cloud_name);
        mTvCloudAddress = (TextView) findViewById(R.id.tv_cloud_address);
        mTvCloudVersion = (TextView) findViewById(R.id.tv_cloud_version);
    }
}
