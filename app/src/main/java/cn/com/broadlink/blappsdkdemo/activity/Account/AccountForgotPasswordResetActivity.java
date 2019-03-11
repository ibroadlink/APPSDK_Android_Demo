package cn.com.broadlink.blappsdkdemo.activity.account;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import cn.com.broadlink.account.BLAccount;
import cn.com.broadlink.base.BLAppSdkErrCode;
import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.base.BLLoginResult;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.MainActivity;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.view.BLPasswordEditView;
import cn.com.broadlink.blappsdkdemo.view.BLProgressDialog;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;


/**
 * 项目名称：BLEControlAppV4
 * <br>类名称：AccountResetPasswordActivity
 * <br>类描述： 忘记密码页面 ，重置页面
 * <br>创建人：YeJing
 * <br>创建时间：2015-5-19 上午11:45:27
 * <br>修改人：Administrator
 * <br>修改时间：2015-5-19 上午11:45:27
 * <br>修改备注：
 */
public class AccountForgotPasswordResetActivity extends TitleActivity {

    private String mUserName;
    private String mPhoneNum;
    private String mVerifyId;
    private String mTipWhenSucc;
    private String mEmail;
    private String mCountryCode = "0086";
    private LinearLayout mNextLayout;
    private EditText mVerifyCodeView;
    private BLPasswordEditView mPwdEdit;

    private Button mVerifyCodeBtn, mNextBtn;
    private TextView mTimeHintView;
    private Timer mVerfiyCodeDelayTimer;
    private static final int MAX_TIME = 60;
    private int mDelayTime = MAX_TIME;
    private enum TASK_TYPE{ FORGET_PWD, RESET_PWD, SET_PWD }
    private TASK_TYPE mTaskType;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_forgot_password_rest);
        setBackWhiteVisible();

        initData();
        
        findView();

        initView();
        
        setListener();
    }

    private void initData() {
        mUserName = getIntent().getStringExtra(BLConstants.INTENT_NAME);
        mPhoneNum = getIntent().getStringExtra(BLConstants.INTENT_PHONE);
        mEmail = getIntent().getStringExtra(BLConstants.INTENT_EMAIL);
        mVerifyId = getIntent().getStringExtra(BLConstants.INTENT_ID);
        mTipWhenSucc = getIntent().getStringExtra(BLConstants.INTENT_EXTEND);
        mCountryCode = getIntent().getStringExtra(BLConstants.INTENT_CODE);

        setTitle(TextUtils.isEmpty(mTipWhenSucc) ? R.string.str_account_reset_password : R.string.title_set_pwd);
        
        if(!TextUtils.isEmpty(mTipWhenSucc)){
            mTaskType = TASK_TYPE.SET_PWD;
        }else if(mPhoneNum == null){
            mTaskType = TASK_TYPE.FORGET_PWD;
        }else{
            mTaskType = TASK_TYPE.RESET_PWD;
        }
    }

    private void findView() {
        mNextLayout = (LinearLayout) findViewById(R.id.next_layout);
        mNextBtn = (Button) findViewById(R.id.next_btn);
       
        mPwdEdit = (BLPasswordEditView)findViewById(R.id.pwd_edit);
        mVerifyCodeView = (EditText) findViewById(R.id.verify_code);

        mVerifyCodeBtn = (Button) findViewById(R.id.reget_verity_code);
        mTimeHintView = (TextView) findViewById(R.id.time_hint_view);
    }

    private void initView() {
        if(mTaskType== TASK_TYPE.SET_PWD || mTaskType== TASK_TYPE.FORGET_PWD){
            mNextLayout.setVisibility(View.GONE);
            mTimeHintView.setVisibility(View.VISIBLE);
        }else{
            mNextLayout.setVisibility(View.VISIBLE);
            mTimeHintView.setVisibility(View.GONE);
        }
    }

    private void setListener() {

        mVerifyCodeBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                new RequestVerifyCodeTask().execute();
            }
        });

        if (mTaskType== TASK_TYPE.SET_PWD || mTaskType== TASK_TYPE.FORGET_PWD) {
            setRightButtonOnClickListener(R.string.str_common_next,
                    getResources().getColor(R.color.bl_yellow_main_color),
                    new OnSingleClickListener() {

                        @Override
                        public void doOnClick(View v) {
                            processResetPwd();
                        }
                    });
            startDelayTimer();
        } else {
            mNextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processResetPwd();
                }
            });
        }
    }

    private boolean checkEditTextViews(){
       EditText mPasswordView = mPwdEdit.getEtPwd();
        if(mPasswordView.getText()==null || mPasswordView.getText().length()==0){
            BLCommonUtils.toastShow(AccountForgotPasswordResetActivity.this, R.string.str_account_hint_password);
            mPasswordView.requestFocus();
            return false;
        }else{
            String password = mPasswordView.getText().toString();
            if(!isInputRight(password)) return false;
        }
        
       if(mVerifyCodeView.getText()==null || mVerifyCodeView.getText().length()==0){
            BLCommonUtils.toastShow(AccountForgotPasswordResetActivity.this, R.string.str_account_signup_verify_code);
           mVerifyCodeView.requestFocus();
            return false;
        }
        
        return true;
    }

    //检测2次输入的密码是否正确
    private boolean isInputRight(String password1) {
        if (TextUtils.isEmpty(password1)) {
            BLCommonUtils.toastShow(AccountForgotPasswordResetActivity.this, R.string.str_account_hint_password);
            return false;
        }

        if (password1.length() < 6 || password1.length() > 20) {
            BLCommonUtils.toastShow(AccountForgotPasswordResetActivity.this, R.string.str_account_hint_password_length_from_six_to_twenty);
            return false;
        }

        return true;
    }
    
    private void processResetPwd() {
        if(checkEditTextViews()){
            new RestPasswordTask().execute(mPwdEdit.getEtPwd().getText().toString(), mVerifyCodeView.getText().toString());
        }
    }

    class RequestVerifyCodeTask extends AsyncTask<String, Void, BLBaseResult> {
        BLProgressDialog blProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            blProgressDialog = BLProgressDialog.createDialog(AccountForgotPasswordResetActivity.this, null);
            blProgressDialog.show();
        }

        @Override
        protected BLBaseResult doInBackground(String... params) {
            if (mTaskType== TASK_TYPE.SET_PWD) {
                if(!TextUtils.isEmpty(mEmail)){
                    return BLAccount.sendModifyVCode(mEmail);
                }else{
                    return BLAccount.sendModifyVCode(mPhoneNum, mCountryCode);
                }
            }else if (mTaskType== TASK_TYPE.FORGET_PWD) {
                return BLAccount.sendRetrieveVCode(mUserName);
            } else {
                return BLAccount.sendFastLoginPasswordVCode(mPhoneNum, "0086");
            }
        }

        @Override
        protected void onPostExecute(BLBaseResult result) {
            super.onPostExecute(result);
            blProgressDialog.dismiss();
            if (result != null && result.getError() == BLAppSdkErrCode.SUCCESS) {
                startDelayTimer();
            } else if (result != null) {
                BLCommonUtils.toastShow(AccountForgotPasswordResetActivity.this, result.getMsg());
            } else {
                BLCommonUtils.toastShow(AccountForgotPasswordResetActivity.this, R.string.str_err_network);
            }

        }
    }


    //重置密码
    private class RestPasswordTask extends AsyncTask<String, Void, Boolean> {
        private BLProgressDialog mBLProgressDialog;
        private String userPassword;
        private String vcode;
        private BLLoginResult mLoginResult;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            switch (mTaskType){
                case FORGET_PWD:
                    mBLProgressDialog = BLProgressDialog.createDialog(AccountForgotPasswordResetActivity.this, R.string.str_account_loggingin);
                    break;
                case RESET_PWD:
                    mBLProgressDialog = BLProgressDialog.createDialog(AccountForgotPasswordResetActivity.this, R.string.str_settings_safety_modify_password);
                    break;
                case SET_PWD:
                    mBLProgressDialog = BLProgressDialog.createDialog(AccountForgotPasswordResetActivity.this, R.string.title_set_pwd);
                    break;
            }
            mBLProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            userPassword = params[0];
            vcode = params[1];

            if (mTaskType == TASK_TYPE.SET_PWD) {
                BLBaseResult blBaseResult;
                if(!TextUtils.isEmpty(mEmail)){
                    blBaseResult = BLAccount.modifyEmail(mEmail, vcode, "", userPassword);
                }else{
                    blBaseResult = BLAccount.modifyPhone(mPhoneNum, mCountryCode, vcode, "", userPassword);
                }

                if (blBaseResult != null) {
                    mLoginResult = new BLLoginResult();
                    mLoginResult.setMsg(blBaseResult.getMsg());
                    mLoginResult.setError(blBaseResult.getError());
                    if(blBaseResult.getError() == BLAppSdkErrCode.SUCCESS){
                        return true;
                    }
                }
                return false;
                
            } else if (mTaskType == TASK_TYPE.FORGET_PWD) {
                mLoginResult = BLAccount.retrievePassword(mUserName, vcode, userPassword);

                if (mLoginResult != null && mLoginResult.getError() == BLAppSdkErrCode.SUCCESS) {
                    return true;
                }
                return false;
            } else {
                BLBaseResult blBaseResult = BLAccount.setFastLoginPassword(mPhoneNum, "0086", vcode, userPassword);
                if (blBaseResult != null && blBaseResult.getError() == BLAppSdkErrCode.SUCCESS) {
                    mLoginResult = new BLLoginResult();
                    mLoginResult.setMsg(blBaseResult.getMsg());
                    mLoginResult.setError(blBaseResult.getError());
                    return true;
                } else {
                    return false;
                }
            }
        }

        @Override
        protected void onPostExecute(final Boolean result) {
            super.onPostExecute(result);
            mBLProgressDialog.dismiss();

            if (mLoginResult != null && mLoginResult.getError() == BLAppSdkErrCode.SUCCESS && result != null) {

                if(mTaskType == TASK_TYPE.SET_PWD){
                    toActivity(MainActivity.class);
                }
                else if (mTaskType == TASK_TYPE.FORGET_PWD) {
                    toActivity(MainActivity.class);
                } 
                else{
                    BLCommonUtils.toastShow(AccountForgotPasswordResetActivity.this, R.string.str_settings_safety_modify_password_sucess);
                    back();
                }
            } else if (mLoginResult != null && mLoginResult.getError() != BLAppSdkErrCode.SUCCESS) {
                BLCommonUtils.toastShow(AccountForgotPasswordResetActivity.this, mLoginResult.getMsg());
            } else {
                BLCommonUtils.toastShow(AccountForgotPasswordResetActivity.this, R.string.str_err_network);
            }
        }

        private <T> void toActivity(Class<T> clazz) {
            Intent intent = new Intent();
            intent.setClass(AccountForgotPasswordResetActivity.this, clazz);
            startActivity(intent);
        }
    }

    private void startDelayTimer() {
        if (mVerfiyCodeDelayTimer == null) {
            mVerfiyCodeDelayTimer = new Timer();
            mVerfiyCodeDelayTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            mVerifyCodeBtn.setClickable(false);
                            mVerifyCodeBtn.setTextColor(getResources().getColor(R.color.gray));
                            mTimeHintView.setVisibility(View.VISIBLE);
                            mDelayTime -= 1;
                            mTimeHintView.setText(getString(R.string.str_account_signup_waiting_for_verify_code, mDelayTime));
                            if (mDelayTime == 0) {
                                stopDeleyTimer();
                            }
                        }
                    });
                }
            }, 0, 1000);
        }
    }

    private void stopDeleyTimer() {
        if (mVerfiyCodeDelayTimer != null) {
            mVerfiyCodeDelayTimer.cancel();
            mVerfiyCodeDelayTimer.purge();
            mVerfiyCodeDelayTimer = null;
        }
        mDelayTime = MAX_TIME;
        mVerifyCodeBtn.setClickable(true);
        mVerifyCodeBtn.setTextColor(getResources().getColor(R.color.rmac_notify));
        mTimeHintView.setVisibility(View.GONE);
    }
}
