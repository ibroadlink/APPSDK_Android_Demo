package cn.com.broadlink.blappsdkdemo.activity.account;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.com.broadlink.account.BLAccount;
import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.BLInputCountdownView;
import cn.com.broadlink.blappsdkdemo.view.InputTextView;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;

/**
 * 更改邮箱或者手机
 *
 * @author JiangYaqiang
 * 2019/3/25 16:53
 */
public class AccountModifyPhoneOrEmailActivity extends TitleActivity {

    private TextView mTvTip;
    private InputTextView mEtEmail;
    private InputTextView mEtPassword;
    private LinearLayout mLlPhone;
    private Button mBtnCountryCode;
    private InputTextView mPhoneView;
    private BLInputCountdownView mVVerifyCode;
    private Button mBtCommit;

    private boolean mIsPhone = false;
    private String mAccount;
    private String mCountryCode = "86";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_account_modify_phone_or_email);
        setBackWhiteVisible();
        setTitle("Modify Phone Or Email");

        mAccount = getIntent().getStringExtra(BLConstants.INTENT_VALUE);
        if (TextUtils.isEmpty(mAccount)) {
            BLToastUtils.show("Account should not be null!");
            back();
            return;
        }

        initData();

        findView();

        initView();

        setListener();
    }

    private void findView() {
        mTvTip = (TextView) findViewById(R.id.tv_tip);
        mEtEmail = (InputTextView) findViewById(R.id.et_email);
        mEtPassword = (InputTextView) findViewById(R.id.et_password);
        mLlPhone = (LinearLayout) findViewById(R.id.ll_phone);
        mBtnCountryCode = (Button) findViewById(R.id.btn_country_code);
        mPhoneView = (InputTextView) findViewById(R.id.phone_view);
        mVVerifyCode = (BLInputCountdownView) findViewById(R.id.v_verify_code);
        mBtCommit = (Button) findViewById(R.id.bt_login);
    }

    private void initData() {
        mIsPhone = BLCommonUtils.isPhone(mAccount);
    }

    private void initView() {
        if (mIsPhone) {
            mEtEmail.setVisibility(View.GONE);
        } else {
            mLlPhone.setVisibility(View.GONE);
        }

        mTvTip.setText("Current account is: " + mAccount);
    }

    private void setListener() {
        mVVerifyCode.setOnReSendClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View view) {
                final String newAccount = mIsPhone ? mPhoneView.getTextString() : mEtEmail.getTextString();

                if (TextUtils.isEmpty(newAccount)) {
                    BLToastUtils.show("Input new phone/email first!");
                    return;
                }
                new GetEmailVerifyCodeTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR, newAccount);
            }
        });

        mBtnCountryCode.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                BLAlert.showEditDilog(mActivity, "Input Country Code", mCountryCode, new BLAlert.BLEditDialogOnClickListener() {
                    @Override
                    public void onClink(String value) {
                        try {
                            final int countryCode = Integer.parseInt(value);
                            mCountryCode = String.valueOf(countryCode);
                            mBtnCountryCode.setText("+" + mCountryCode);
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

        mBtCommit.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {

                final String newAccount = mIsPhone ? mPhoneView.getTextString() : mEtEmail.getTextString();
                final String password = mEtPassword.getTextString();
                final String verifyCode = mVVerifyCode.getText();

                if (TextUtils.isEmpty(newAccount) || TextUtils.isEmpty(password) || TextUtils.isEmpty(verifyCode)) {
                    BLToastUtils.show("Complete input first!");
                    return;
                }

                new ModifyPhoneOrEmailTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR, newAccount, verifyCode, password);
            }
        });
    }


    class GetEmailVerifyCodeTask extends AsyncTask<String, Void, BLBaseResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Get Verify Code...");
        }

        @Override
        protected BLBaseResult doInBackground(String... params) {
            
            return mIsPhone ? BLAccount.sendModifyVCode(params[0], mCountryCode) : BLAccount.sendModifyVCode(params[0]);
            
        }

        @Override
        protected void onPostExecute(BLBaseResult result) {
            super.onPostExecute(result);

            dismissProgressDialog();
            if (result != null && result.succeed()) {
                mVVerifyCode.startCount();
                BLToastUtils.show("Send Success");
            } else {
                BLCommonUtils.toastErr(result);
            }
        }
    }
    

    class ModifyPhoneOrEmailTask extends AsyncTask<String, Void, BLBaseResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Modify...");
        }

        @Override
        protected BLBaseResult doInBackground(String... params) {
            if (mIsPhone) {
                return BLAccount.modifyPhone(params[0], mCountryCode, params[1], params[2], params[2]);
            } else {
                return  BLAccount.modifyEmail(params[0], params[1], params[2], params[2]);
            }
        }

        @Override
        protected void onPostExecute(BLBaseResult result) {
            super.onPostExecute(result);
            dismissProgressDialog();
            
            if (result != null && result.succeed()) {
                BLAlert.showAlert(mActivity, null, "Modify phone/email success, login with your new account.", new OnSingleClickListener() {
                    @Override
                    public void doOnClick(View v) {
                        BLApplication.mBLUserInfoUnits.loginOut();

                        Intent intent = new Intent();
                        intent.setClass(mActivity, AccountMainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        back();
                    }
                });
            } else {
                BLCommonUtils.toastErr(result);
            }
        }
    }
}
