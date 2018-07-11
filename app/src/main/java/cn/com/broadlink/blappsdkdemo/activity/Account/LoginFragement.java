package cn.com.broadlink.blappsdkdemo.activity.Account;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import cn.com.broadlink.account.BLAccount;
import cn.com.broadlink.base.BLAppSdkErrCode;
import cn.com.broadlink.base.BLLoginResult;
import cn.com.broadlink.blappsdkdemo.BLApplcation;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.view.BLPasswordEditView;
import cn.com.broadlink.blappsdkdemo.view.BLProgressDialog;
import cn.com.broadlink.blappsdkdemo.view.InputTextView;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;

/**
 * Created by YeJin on 2017/4/19.
 */

public class LoginFragement extends AccountBaseFragemt{

    private InputTextView mAccoutView;
    private Button mLoginBtn, mForgotBtn;
    private BLPasswordEditView mPwdEdit;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment_layout, container, false);

        findView(view);

        setListener();

        initView();

        return view;
    }

    private void findView(View view){
        mAccoutView = (InputTextView) view.findViewById(R.id.account_view);

        mLoginBtn = (Button) view.findViewById(R.id.btn_login);
        mForgotBtn = (Button) view.findViewById(R.id.btn_forget);
        mPwdEdit = (BLPasswordEditView) view.findViewById(R.id.pwd_edit);
    }

    private void setListener(){

        mAccoutView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                mLoginBtn.setEnabled((checkInputNameAndPassword(mAccoutView.getTextString(), mPwdEdit.getEtPwd().getText().toString())));
            }
        });
        
        mPwdEdit.setOnTextChangeListener(new BLPasswordEditView.BLOnTextChangedListener() {
            @Override
            public void onTextChanged(Editable s) {
                mLoginBtn.setEnabled((checkInputNameAndPassword(mAccoutView.getTextString(), s.toString())));
            }
        });


        //登陆按钮事件
        mLoginBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                String email = mAccoutView.getTextString();
                String password = mPwdEdit.getEtPwd().getText().toString();

                new LoginTask().execute(email, password);
            }
        });

        mForgotBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                toActivity(AccountForgotPasswordActivity.class);
            }
        });
    }

    private boolean checkInputNameAndPassword(String account, String password){
        return  ((account.length() > 0 && (BLCommonUtils.isEmail(account) || BLCommonUtils.isPhone(account)))
                && password.length() >= 6);
    }

    private void initView(){
        mAccoutView.setTextHint(R.string.str_account_hint_account);
        mAccoutView.getEditText().setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
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
            mBLProgressDialog = BLProgressDialog.createDialog(getActivity(), R.string.str_account_loggingin);
            mBLProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            loginResult = null;

            userName = params[0];
            userPassword = params[1];

            loginResult = BLAccount.login(userName, userPassword);
            if (loginResult != null) {
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean hasData) {
            super.onPostExecute(hasData);
            if (mBLProgressDialog == null || getActivity() == null) return;

            mBLProgressDialog.dismiss();
            if (loginResult != null && loginResult.getError() == BLAppSdkErrCode.SUCCESS && hasData != null) {

                //存储登陆成功返回的 userId 和 loginSession ，便于下次直接登陆使用
                BLApplcation.getmBLUserInfoUnits().login(loginResult.getUserid(), loginResult.getLoginsession(),
                        loginResult.getNickname(), loginResult.getIconpath(), loginResult.getLoginip(),
                        loginResult.getLogintime(), loginResult.getSex(), null, loginResult.getPhone(), loginResult.getEmail(), loginResult.getBirthday());

                getActivity().finish();
            } else if (loginResult != null && loginResult.getError() != BLAppSdkErrCode.SUCCESS) {
                BLCommonUtils.toastShow(getActivity(), loginResult.getMsg());
            } else {
                BLCommonUtils.toastShow(getActivity(), R.string.str_err_network);
            }
        }
    }

    private <T> void toActivity(Class<T> clazz) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), clazz);
        startActivity(intent);
    }
}
