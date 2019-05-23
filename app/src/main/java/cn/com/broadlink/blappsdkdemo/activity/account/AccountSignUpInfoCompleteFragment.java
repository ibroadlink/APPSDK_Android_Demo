package cn.com.broadlink.blappsdkdemo.activity.account;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.TimerTask;

import cn.com.broadlink.account.BLAccount;
import cn.com.broadlink.account.params.BLRegistParam;
import cn.com.broadlink.base.BLAppSdkErrCode;
import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.base.BLLoginResult;
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.BaseFragment;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.view.BLPasswordEditView;
import cn.com.broadlink.blappsdkdemo.view.BLProgressDialog;
import cn.com.broadlink.blappsdkdemo.view.InputTextView;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;

/**
 * Created by YeJin on 2017/4/20.
 */

public class AccountSignUpInfoCompleteFragment extends BaseFragment {

    private String mCountryCode;
    private String mAccount;

    private InputTextView mVCodeView;
    private Button mNextBtn;
    private BLPasswordEditView mPwdEdit;
    private TextView mSendHintView;

    private final int MAX_DELAY_TIME = 60;
    private int mDelayTime = MAX_DELAY_TIME;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){
            mCountryCode = bundle.getString(BLConstants.INTENT_CODE);
            mAccount = bundle.getString(BLConstants.INTENT_VALUE);
        }
    }

    @Override
    public View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_sign_up_complete, container, false);

        findView(view);

        initView();

        setListener();

        startDelayTimer();

        return view;
    }

    private void findView(View view){
        mSendHintView = (TextView) view.findViewById(R.id.send_hint_view);

        mVCodeView = (InputTextView) view.findViewById(R.id.vcode_view);
        mPwdEdit = (BLPasswordEditView) view.findViewById(R.id.pwd_edit);

        mNextBtn = (Button) view.findViewById(R.id.btn_next);
    }

    private void setListener(){
        mVCodeView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                refreshNextBtn();
            }
        });

        mPwdEdit.setOnTextChangeListener(new BLPasswordEditView.BLOnTextChangedListener() {
            @Override
            public void onTextChanged(Editable s) {
                refreshNextBtn();
            }
        });

        mNextBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                String vCode = mVCodeView.getTextString();
                String password = mPwdEdit.getEtPwd().getText().toString();
                new RegisterTask().execute(vCode, password);
            }
        });
    }

    private void refreshNextBtn(){
        String password = mPwdEdit.getEtPwd().getText().toString();
        if (password.length() >= 6 && (mVCodeView.getTextString().length() > 3)) {
            mNextBtn.setEnabled(true);
        } else {
            mNextBtn.setEnabled(false);
        }
    }

    private void initView(){
        mVCodeView.setTextHint(R.string.str_verification_code);
    }

    //刷新发送验证码倒计时
    private void refreshDelayView(){
        String hintStr = getString(R.string.str_send_verification_code_format, mAccount);
        String verStr = mDelayTime < 0 ? getString(R.string.str_request_new_one) : getString(R.string.str_second_format, mDelayTime);
        int startIndex = hintStr.length();
        int endIndex = startIndex + verStr.length();

        SpannableStringBuilder spannable = new SpannableStringBuilder(hintStr + verStr);
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.bl_yellow_color)),startIndex,endIndex
                , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        if(mDelayTime <= 0){
            spannable.setSpan(new TextClick(), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        mSendHintView.setMovementMethod(LinkMovementMethod.getInstance());
        mSendHintView.setText(spannable);
    }

    private class TextClick extends ClickableSpan {

        @Override
        public void onClick(View widget) {
            if(getActivity() != null) getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSendHintView.setHighlightColor(getResources().getColor(android.R.color.transparent));
                    new GetVerifyCodeTask().execute();
                }
            });
        }

        @Override
        public void updateDrawState(TextPaint ds) {}
    }

    //重新发送倒计时
    java.util.Timer mTimer;
    private void startDelayTimer(){
        stopDeleyTimer();
        mDelayTime = MAX_DELAY_TIME;
        mTimer =  new java.util.Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mDelayTime --;
                if(mDelayTime < 0){
                    stopDeleyTimer();
                }
                if(getActivity() != null) getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshDelayView();
                    }
                });
            }
        }, 0, 1000);
    }

    private void stopDeleyTimer(){
        if(mTimer != null){
            mTimer.cancel();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopDeleyTimer();
    }

    //获取手机验证码
    private class GetVerifyCodeTask extends AsyncTask<Void, Void, BLBaseResult> {
        BLProgressDialog blProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            blProgressDialog = BLProgressDialog.createDialog(getActivity(), null);
            blProgressDialog.show();
        }

        @Override
        protected BLBaseResult doInBackground(Void... params) {
            if(!TextUtils.isEmpty(mCountryCode)){
                //短信验证码
                return BLAccount.sendRegVCode(mAccount,  mCountryCode);
            }else{
                //邮箱验证码
                return BLAccount.sendRegVCode(mAccount);
            }
        }

        @Override
        protected void onPostExecute(BLBaseResult result) {
            super.onPostExecute(result);
            blProgressDialog.dismiss();
            if (result != null && result.getError() == BLAppSdkErrCode.SUCCESS) {
                startDelayTimer();
            } else if (result != null) {
                BLCommonUtils.toastShow(getActivity(), result.getMsg());
            } else {
                BLCommonUtils.toastShow(getActivity(), R.string.str_err_network);
            }
        }
    }

    private class RegisterTask extends AsyncTask<String, Void, BLLoginResult> {
        private BLProgressDialog blProgressDialog;
        private String uerPassword;

        //创建默认家庭是否成功
        private String createFamilyId = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            blProgressDialog = BLProgressDialog.createDialog(getActivity(), null);
            blProgressDialog.show();
        }

        @Override
        protected BLLoginResult doInBackground(String... params) {
            uerPassword = params[1];
            BLRegistParam registParam = new BLRegistParam();
            registParam.setSex("male");
            registParam.setCode(params[0]);
            if(mCountryCode != null){
                registParam.setCountrycode(mCountryCode);
            }
            registParam.setNickname(mAccount);
            registParam.setPhoneOrEmail(mAccount);
            registParam.setPassword(uerPassword);

            boolean isPhoneType = BLCommonUtils.isPhone(mAccount);
            
            BLLoginResult result = BLAccount.regist(registParam, null);
            if (result != null && result.getError() == BLAppSdkErrCode.SUCCESS) {
                BLApplication.mBLUserInfoUnits.login(result.getUserid(), result.getLoginsession(),
                        mAccount, result.getIconpath(), result.getLoginip(),
                        result.getLogintime(), result.getSex(), null, isPhoneType ? mAccount : null, isPhoneType ? null : mAccount, result.getBirthday());
            }
            return result;
        }

        @Override
        protected void onPostExecute(BLLoginResult result) {
            super.onPostExecute(result);
            blProgressDialog.dismiss();
            if (result != null && result.getError() == BLAppSdkErrCode.SUCCESS) {
                BLToastUtils.show("Sign up success, auto login.");
                BLCommonUtils.toActivity(getActivity(), AccountAndSecurityActivity.class);
                getActivity().finish();
            } else if (result != null) {
                BLCommonUtils.toastShow(getActivity(), result.getMsg());
            } else {
                BLCommonUtils.toastShow(getActivity(), R.string.str_err_network);
            }
        }
    }
}
