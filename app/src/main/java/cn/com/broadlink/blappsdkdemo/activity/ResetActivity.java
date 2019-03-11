package cn.com.broadlink.blappsdkdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.common.PreferencesUtils;
import cn.com.broadlink.blappsdkdemo.service.BLLocalFamilyManager;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;

public class ResetActivity extends TitleActivity {

    private EditText mEtPackage;
    private EditText mEtLicense;
    private Button mBtCommit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        setTitle("Reset Package & License");
        setBackWhiteVisible();

        initView();

        mBtCommit.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                if(TextUtils.isEmpty(mEtPackage.getText()) || TextUtils.isEmpty(mEtLicense.getText())){
                    BLToastUtils.show("Please complete input");
                    return;
                }
                    
                BLApplication.mBLUserInfoUnits.loginOut();
                BLLocalFamilyManager.getInstance().setCurrentFamilyInfo(null);
                PreferencesUtils.putString(mActivity, "packageName", mEtPackage.getText().toString());
                PreferencesUtils.putString(mActivity, "license", mEtLicense.getText().toString());
                
                Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        mEtPackage = (EditText) findViewById(R.id.et_package);
        mEtLicense = (EditText) findViewById(R.id.et_license);
        mBtCommit = (Button) findViewById(R.id.bt_commit);
        
        String packageName = PreferencesUtils.getString(this, "packageName", "cn.com.broadlink.econtrol.plus");
        String license = PreferencesUtils.getString(this, "license", "0PlPqgTGPZt7CwNNz4lWVm7qQZqm8AdPyooafIrN9QX5UING6RYrag2V2nFqWRIQrFDxVgAAAADoWWz5UyBiHvQQIPyIUhi4XFSykPGAqniglnoIUWhvuHCgxWxDEyF0yb0xHzyz6V5BLR6D0KoiI6QqjWxRKs8JsEkbxXTfoUSQjDzWcfVjcA4AAADzeX7wfU+3ndxs2/3yXOnJrFAlYyFEltcuD9SloQA7kInW0ynCvel2PMHSm6RgRp/xNYhi5LPROx4fnr746yHD");

        mEtPackage.setText(packageName);
        mEtLicense.setText(license);
    }
}
