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

import cn.com.broadlink.base.fastjson.BLJSON;
import cn.com.broadlink.base.fastjson.JSONObject;
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.common.PreferencesUtils;
import cn.com.broadlink.blappsdkdemo.service.BLLocalFamilyManager;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.sdk.BLLet;

public class ResetActivity extends TitleActivity {

    private EditText mEtPackage;
    private EditText mEtDomain;
    private EditText mEtLicense;
    private EditText mEtPair;
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
                    mEtDomain.setVisibility(View.VISIBLE);
                    mEtLicense.setVisibility(View.VISIBLE);
                    mEtPair.setVisibility(View.VISIBLE);
                }else{
                    mEtPackage.setVisibility(View.GONE);
                    mEtDomain.setVisibility(View.GONE);
                    mEtLicense.setVisibility(View.GONE);
                    mEtPair.setVisibility(View.GONE);
                }
            }
        });

        mBtCommit.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                String packageName = null;
                String license = null;
                String domain = null;
                String pair = null;
                
                final int checkedRadioButtonId = mRgServerList.getCheckedRadioButtonId();
                int selection = 0;
                switch (checkedRadioButtonId) {
                    case R.id.rb_baidu:
                        packageName = BLConstants.SDK_PACKAGE_BAIDU;
                        license = BLConstants.SDK_LICENSE_BAIDU;
                        selection = 0;
                        break;
                        
                    case R.id.rb_international_china:
                        packageName = BLConstants.SDK_PACKAGE;
                        license = BLConstants.SDK_LICENSE;
                        selection = 1;
                        break;
                        
                    default:
                        if (TextUtils.isEmpty(mEtPackage.getText()) || TextUtils.isEmpty(mEtLicense.getText())) {
                            BLToastUtils.show("Please complete input");
                            return;
                        }
                        
                        if(!TextUtils.isEmpty(mEtPair.getText())){
                            try {
                                final JSONObject jsonObject = BLJSON.parseObject(mEtPair.getText().toString());
                                jsonObject.put("companyid", BLLet.getLicenseId());
                                pair = jsonObject.toString();
                            } catch (Exception e) {
                                BLToastUtils.show("Pair server profile should be json string.");
                                return;
                            }
                        }
                        
                        packageName = mEtPackage.getText().toString();
                        license = mEtLicense.getText().toString();
                        domain = mEtPackage.getText() == null ? null : mEtDomain.getText().toString();
                        selection = 2;
                }

                BLApplication.mBLUserInfoUnits.loginOut();
                BLLocalFamilyManager.getInstance().setCurrentFamilyInfo(null);
                PreferencesUtils.putString(mActivity, "packageName", packageName);
                PreferencesUtils.putString(mActivity, "license", license);
                PreferencesUtils.putString(mActivity, "domain", domain);
                PreferencesUtils.putString(mActivity, "pair", pair);
                PreferencesUtils.putBoolean(mActivity, "cluster", mSwtCluster.isChecked());
                PreferencesUtils.putInt(mActivity, "selection", selection);

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
        String domain = PreferencesUtils.getString(this, "domain", null);
        boolean useCluster = PreferencesUtils.getBoolean(this, "cluster", true);
        int selection = PreferencesUtils.getInt(this, "selection", -1);
        String pairServer = PreferencesUtils.getString(this, "pair",  selection==-1 ? BLConstants.PAIR_SERVER_PROFILE : null); // 没设置过就用默认值，设置过之后即使是null也用设置的值

        mEtPackage.setText(packageName);
        mEtLicense.setText(license);
        mEtDomain.setText(domain);
        mSwtCluster.setChecked(useCluster);
        
        try {
            mEtPair.setText(BLJSON.parseObject(pairServer).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        switch (selection) {
            case -1:
            case 0:
                mRbInternationalChina.setChecked(true);
                mEtPackage.setVisibility(View.GONE);
                mEtDomain.setVisibility(View.GONE);
                mEtLicense.setVisibility(View.GONE);
                mEtPair.setVisibility(View.GONE);
                break;
            case 1:
                mRbBaidu.setChecked(true);
                mEtPackage.setVisibility(View.GONE);
                mEtLicense.setVisibility(View.GONE);
                mEtDomain.setVisibility(View.GONE);
                mEtPair.setVisibility(View.GONE);
                break;
            case 2:
                mRbCustom.setChecked(true);
                mEtPackage.setVisibility(View.VISIBLE);
                mEtLicense.setVisibility(View.VISIBLE);
                mEtDomain.setVisibility(View.VISIBLE);
                mEtPair.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void findView() {
        mEtPackage = (EditText) findViewById(R.id.et_package);
        mEtDomain = (EditText) findViewById(R.id.et_domain);
        mEtLicense = (EditText) findViewById(R.id.et_license);
        mEtPair = (EditText) findViewById(R.id.et_pair_cfg);
        mBtCommit = (Button) findViewById(R.id.bt_commit);
        mSwtCluster = (Switch) findViewById(R.id.swt_cluster);
        mRgServerList = (RadioGroup) findViewById(R.id.rg_server_list);
        mRbBaidu = (RadioButton) findViewById(R.id.rb_baidu);
        mRbInternationalChina = (RadioButton) findViewById(R.id.rb_international_china);
        mRbCustom = (RadioButton) findViewById(R.id.rb_custom);
    }
}
