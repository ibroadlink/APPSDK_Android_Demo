package cn.com.broadlink.blappsdkdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.common.PreferencesUtils;
import cn.com.broadlink.blappsdkdemo.service.BLLocalFamilyManager;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;

public class ResetActivity extends TitleActivity {

    private EditText mEtPackage;
    private EditText mEtLicense;
    private Button mBtCommit;
    private Switch mSwtCluster;
    private RadioGroup mRgServerList;
    private RadioButton mRbBaidu;
    private RadioButton mRbInternationalChina;
    private RadioButton mRbCustom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        setTitle("Reset Package & License");
        setBackWhiteVisible();

        findView();

        initView();

        setListener();
    }

    private void setListener() {
        mRgServerList.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i==R.id.rb_custom){
                    mEtPackage.setVisibility(View.VISIBLE);
                    mEtLicense.setVisibility(View.VISIBLE);
                }else{
                    mEtPackage.setVisibility(View.GONE);
                    mEtLicense.setVisibility(View.GONE);
                }
            }
        });

        mBtCommit.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                String packageName = null;
                String license = null;
                
                final int checkedRadioButtonId = mRgServerList.getCheckedRadioButtonId();
                switch (checkedRadioButtonId) {
                    case R.id.rb_baidu:
                        packageName = BLConstants.SDK_PACKAGE_BAIDU;
                        license = BLConstants.SDK_LICENSE_BAIDU;
                        break;
                        
                    case R.id.rb_international_china:
                        packageName = BLConstants.SDK_PACKAGE;
                        license = BLConstants.SDK_LICENSE;
                        break;
                        
                    default:
                        if (TextUtils.isEmpty(mEtPackage.getText()) || TextUtils.isEmpty(mEtLicense.getText())) {
                            BLToastUtils.show("Please complete input");
                            return;
                        }
                        packageName = mEtPackage.getText().toString();
                        license = mEtLicense.getText().toString();
                }

                BLApplication.mBLUserInfoUnits.loginOut();
                BLLocalFamilyManager.getInstance().setCurrentFamilyInfo(null);
                PreferencesUtils.putString(mActivity, "packageName", packageName);
                PreferencesUtils.putString(mActivity, "license", license);
                PreferencesUtils.putBoolean(mActivity, "cluster", mSwtCluster.isChecked());

                //重新初始化sdk
                mApplication.sdkInit();
                
                Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent); 
                
            }
        });
    }

    private void initView() {

        String packageName = PreferencesUtils.getString(this, "packageName", BLConstants.SDK_PACKAGE);
        String license = PreferencesUtils.getString(this, "license", BLConstants.SDK_LICENSE);
        boolean useCluster = PreferencesUtils.getBoolean(this, "cluster", true);

        mEtPackage.setText(packageName);
        mEtLicense.setText(license);
        mSwtCluster.setChecked(useCluster);
        
        // international china cluster
        if(packageName.equalsIgnoreCase(BLConstants.SDK_PACKAGE) && license.equalsIgnoreCase(BLConstants.SDK_LICENSE)){
            mRbInternationalChina.setChecked(true);
            mEtPackage.setVisibility(View.GONE);
            mEtLicense.setVisibility(View.GONE);
        }else if(packageName.equalsIgnoreCase(BLConstants.SDK_PACKAGE_BAIDU) && license.equalsIgnoreCase(BLConstants.SDK_LICENSE_BAIDU)){
            mRbBaidu.setChecked(true);
            mEtPackage.setVisibility(View.GONE);
            mEtLicense.setVisibility(View.GONE);
        }else{
            mRbCustom.setChecked(true);
            mEtPackage.setVisibility(View.VISIBLE);
            mEtLicense.setVisibility(View.VISIBLE);
        }

    }

    private void findView() {
        mEtPackage = (EditText) findViewById(R.id.et_package);
        mEtLicense = (EditText) findViewById(R.id.et_license);
        mBtCommit = (Button) findViewById(R.id.bt_commit);
        mSwtCluster = (Switch) findViewById(R.id.swt_cluster);
        mRgServerList = (RadioGroup) findViewById(R.id.rg_server_list);
        mRbBaidu = (RadioButton) findViewById(R.id.rb_baidu);
        mRbInternationalChina = (RadioButton) findViewById(R.id.rb_international_china);
        mRbCustom = (RadioButton) findViewById(R.id.rb_custom);
    }
}
