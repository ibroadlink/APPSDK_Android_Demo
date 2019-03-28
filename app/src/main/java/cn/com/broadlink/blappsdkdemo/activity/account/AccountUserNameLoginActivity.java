package cn.com.broadlink.blappsdkdemo.activity.account;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import cn.com.broadlink.account.BLAccount;
import cn.com.broadlink.base.BLAppSdkErrCode;
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
import cn.com.broadlink.blappsdkdemo.view.BLProgressDialog;
import cn.com.broadlink.blappsdkdemo.view.InputTextView;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;

public class AccountUserNameLoginActivity extends TitleActivity {

    private InputTextView mEtUsername;
    private InputTextView mEtPassword;
    private Button mBtLogin;
    private TextView mTvForget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_username_login);
        setBackWhiteVisible();
        setTitle(R.string.account_username_password_login);
        
        initView();

        setListener();
    }

    private void initView() {
        mEtUsername = (InputTextView) findViewById(R.id.et_username);
        mEtPassword = (InputTextView) findViewById(R.id.et_password);
        mBtLogin = (Button) findViewById(R.id.bt_login);
        mTvForget = (TextView) findViewById(R.id.tv_forget);
    }
    
    private void setListener() {
        mBtLogin.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                String email = mEtUsername.getTextString();
                String password = mEtPassword.getTextString();

                if(!checkInputNameAndPassword(email, password)){
                    BLToastUtils.show(R.string.account_username_or_password_err);
                    return;
                }

                new LoginTask().execute(email, password);
            }
        });
        
        mTvForget.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                BLCommonUtils.toActivity(AccountUserNameLoginActivity.this, AccountForgotPasswordActivity.class);
            }
        });

    }
    private boolean checkInputNameAndPassword(String account, String password){
        return  ((account.length() > 0 && (BLCommonUtils.isEmail(account) || BLCommonUtils.isPhone(account))) && password.length() >= 6);
    }

    /**
     * 用户登录异步线程
     ***/
    private class LoginTask extends AsyncTask<String, Void, Boolean> {
        private BLProgressDialog mBLProgressDialog;

        private String userName;
        private String userPassword;

        private BLLoginResult loginResult;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mBLProgressDialog = BLProgressDialog.createDialog(AccountUserNameLoginActivity.this, R.string.str_account_loggingin);
            mBLProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            loginResult = null;

            userName = params[0];
            userPassword = params[1];

            loginResult = BLAccount.login(userName, userPassword);
            if (loginResult != null) {
                
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
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean hasData) {
            super.onPostExecute(hasData);
            if (mBLProgressDialog == null) return;

            mBLProgressDialog.dismiss();
            if (loginResult != null && loginResult.getError() == BLAppSdkErrCode.SUCCESS && hasData != null) {
                
                setResult(RESULT_OK);
                AccountUserNameLoginActivity.this.finish();
            } else if (loginResult != null && loginResult.getError() != BLAppSdkErrCode.SUCCESS) {
                BLCommonUtils.toastShow(AccountUserNameLoginActivity.this, loginResult.getMsg());
            } else {
                BLCommonUtils.toastShow(AccountUserNameLoginActivity.this, R.string.str_err_network);
            }
        }
    }
}
