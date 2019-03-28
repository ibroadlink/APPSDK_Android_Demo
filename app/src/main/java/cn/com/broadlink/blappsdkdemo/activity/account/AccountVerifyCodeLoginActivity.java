package cn.com.broadlink.blappsdkdemo.activity.account;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.List;

import cn.com.broadlink.account.BLAccount;
import cn.com.broadlink.base.BLAppSdkErrCode;
import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.base.BLLoginResult;
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.activity.family.manager.BLSFamilyManager;
import cn.com.broadlink.blappsdkdemo.activity.family.result.BLSFamilyInfo;
import cn.com.broadlink.blappsdkdemo.activity.family.result.BLSFamilyListResult;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.service.BLLocalFamilyManager;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.BLInputCountdownView;
import cn.com.broadlink.blappsdkdemo.view.BLProgressDialog;
import cn.com.broadlink.blappsdkdemo.view.InputTextView;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;

public class AccountVerifyCodeLoginActivity extends TitleActivity {

    private InputTextView mPhoneView;
    private BLInputCountdownView mVVerifyCode;
    private Button mBtLogin;
    private Button mCountryCodeBtn;
    private String mCountryCode = "86";
    private LinearLayout mPhoneNumLayout;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_verify_code_login);
        setBackWhiteVisible();
        setTitle("Account Manage");

        initView();

        setListener();
    }

    private void initView() {
        mCountryCodeBtn = (Button) findViewById(R.id.btn_country_code);
        mPhoneView = (InputTextView) findViewById(R.id.phone_view);
        mVVerifyCode = (BLInputCountdownView) findViewById(R.id.v_verify_code);
        mBtLogin = (Button) findViewById(R.id.bt_login);
        mPhoneNumLayout = (LinearLayout) findViewById(R.id.phone_num_layout);
        
        mPhoneView.setTextHint(R.string.str_settings_safety_phone_number);
        mPhoneView.getEditText().setInputType(InputType.TYPE_CLASS_PHONE);
        mCountryCodeBtn.setText("+" + mCountryCode);
    }

    private void setListener() {

        mVVerifyCode.setVisiableListener(new BLInputCountdownView.OnVisibleChangeListener() {
            @Override
            public void onCallback(boolean aBoolean) {
                mBtLogin.setEnabled(aBoolean);
            }
        });

        mVVerifyCode.setOnReSendClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View view) {
                new GetVerifyCodeTask().execute(mPhoneView.getTextString());
            }
        });

        mBtLogin.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                new FastLoginTask().execute(mPhoneView.getTextString(), mVVerifyCode.getText());
            }
        });

        mCountryCodeBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                BLAlert.showEditDilog(AccountVerifyCodeLoginActivity.this, "Input Country Code",  mCountryCode,
                        new BLAlert.BLEditDialogOnClickListener() {
                            @Override
                            public void onClink(String value) {
                                try {
                                    final int countryCode = Integer.parseInt(value);
                                    mCountryCode = String.valueOf(countryCode);
                                    mCountryCodeBtn.setText("+" + mCountryCode);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                    BLToastUtils.show("Country code should be numbers!");
                                }
                            }

                            @Override
                            public void onClinkCacel(String value) {

                            }
                        }, true);
            }
        });
    }

    //获取手机验证码
    private class GetVerifyCodeTask extends AsyncTask<String, Void, BLBaseResult> {
        private String account;
        private BLProgressDialog blProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            blProgressDialog = BLProgressDialog.createDialog(AccountVerifyCodeLoginActivity.this, "Sending...");
            blProgressDialog.show();
        }

        @Override
        protected BLBaseResult doInBackground(String... params) {
            //短信验证码
            account = params[0];
            return BLAccount.sendFastLoginVCode(account, "+" + mCountryCode);
        }

        @Override
        protected void onPostExecute(BLBaseResult result) {
            super.onPostExecute(result);
            blProgressDialog.dismiss();
            if (result != null && result.getError() == BLAppSdkErrCode.SUCCESS) {
                mPhoneNumLayout.setBackgroundResource(R.drawable.input_bg_round_tran_gray);
                mVVerifyCode.startCount();
                BLToastUtils.show("Send Success");
            } else if (result != null) {
                mPhoneNumLayout.setBackgroundResource(R.drawable.input_bg_round_tran_red);
                BLCommonUtils.toastErr(result);
            } else {
                BLToastUtils.show(R.string.str_err_network);
            }
        }
    }



    //用手机和验证码登录
    private class FastLoginTask extends AsyncTask<String, Void, BLLoginResult> {
        private String account;
        private String verifyCode;
        private BLProgressDialog blProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            blProgressDialog = BLProgressDialog.createDialog(AccountVerifyCodeLoginActivity.this, R.string.str_account_loggingin);
            blProgressDialog.show();
        }

        @Override
        protected BLLoginResult doInBackground(String... params) {
            account = params[0];
            verifyCode = params[1];
            final BLLoginResult loginResult = BLAccount.fastLogin(account, "+" + mCountryCode, verifyCode);
            if (loginResult!=null && loginResult.succeed()) {
                
                //存储登陆成功返回的 userId 和 loginSession ，便于下次直接登陆使用
                BLApplication.mBLUserInfoUnits.login(loginResult.getUserid(), loginResult.getLoginsession(),
                        loginResult.getNickname(), loginResult.getIconpath(), loginResult.getLoginip(),
                        loginResult.getLogintime(), loginResult.getSex(), null, loginResult.getPhone(), loginResult.getEmail(), loginResult.getBirthday());
                
                // 默认取得家庭列表第一个家庭
                BLSFamilyListResult result = BLSFamilyManager.getInstance().queryFamilyList();
                if (result != null && result.succeed() && result.getData() != null) {
                    final List<BLSFamilyInfo> familyList = result.getData().getFamilyList();
                    if (familyList != null && familyList.size() > 0) {
                        BLLocalFamilyManager.getInstance().setCurrentFamilyInfo(familyList.get(0));
                    }
                }
            }
            return loginResult;
        }

        @Override
        protected void onPostExecute(BLLoginResult loginResult) {
            super.onPostExecute(loginResult);
            blProgressDialog.dismiss();
            
            if (loginResult != null && loginResult.getError() == BLAppSdkErrCode.SUCCESS) {

                setResult(RESULT_OK);
                AccountVerifyCodeLoginActivity.this.finish();
            } else if (loginResult != null && loginResult.getError() != BLAppSdkErrCode.SUCCESS) {
                BLCommonUtils.toastErr(loginResult);
            } else {
                BLToastUtils.show(R.string.str_err_network);
            }
        }
    }
}
