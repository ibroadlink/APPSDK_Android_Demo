package cn.com.broadlink.blappsdkdemo.activity.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;

public class AccountMainActivity extends TitleActivity {

    private Button mBtUsernamePwd;
    private Button mBtPhoneVerify;
    private Button mBtSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manage);
        setBackWhiteVisible();
        setTitle(R.string.account_title_manage);
        
        initView();

        setListener();
    }

    private void initView() {
        mBtUsernamePwd = (Button) findViewById(R.id.bt_username_pwd);
        mBtPhoneVerify = (Button) findViewById(R.id.bt_phone_verify);
        mBtSignUp = (Button) findViewById(R.id.bt_sign_up);
    }

    private void setListener() {
        mBtUsernamePwd.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                BLCommonUtils.toActivityForResult(AccountMainActivity.this, AccountUserNameLoginActivity.class, 101);
            }
        });
        
        mBtPhoneVerify.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                BLCommonUtils.toActivityForResult(AccountMainActivity.this, AccountVerifyCodeLoginActivity.class, 101);
            }
        });
        
        mBtSignUp.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                BLCommonUtils.toActivity(AccountMainActivity.this, AccountSignUpActivity.class);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101 && resultCode == RESULT_OK){
            BLCommonUtils.toActivity(AccountMainActivity.this, AccountAndSecurityActivity.class);
            AccountMainActivity.this.finish();
        }
    }
}
