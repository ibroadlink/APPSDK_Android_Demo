package cn.com.broadlink.blappsdkdemo.activity.account;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import cn.com.broadlink.account.BLAccount;
import cn.com.broadlink.base.BLAppSdkErrCode;
import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.BaseFragment;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.view.BLProgressDialog;
import cn.com.broadlink.blappsdkdemo.view.InputTextView;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;

/**
 * Created by YeJin on 2017/4/20.
 */

public class AccountSignUpByEmailFragmnet extends BaseFragment{

    private InputTextView mEmailView;

    private Button mNextBtn;

    private TextView mErrorHintView;

    @Override
    public View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_sign_up_by_eamil, container, false);

        findView(view);

        setListener();

        initView();

        return view;
    }

    private void findView(View view){
        mEmailView = (InputTextView) view.findViewById(R.id.account_email_view);

        mNextBtn = (Button) view.findViewById(R.id.btn_login);

        mErrorHintView = (TextView) view.findViewById(R.id.err_hint_view);
    }

    private void setListener(){
        mEmailView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(!TextUtils.isEmpty(mErrorHintView.getText().toString())){
                    mErrorHintView.setText(null);
                    mEmailView.setBackgroundResource(R.drawable.input_bg_round_tran_gray);
                }

                if(s.length() > 0 && BLCommonUtils.isEmail(mEmailView.getTextString())){
                    mNextBtn.setEnabled(true);
                }else{
                    mNextBtn.setEnabled(false);
                }
            }
        });

        mNextBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                final String email = mEmailView.getTextString();
                new GetVerifyCodeTask().execute(email);
            }
        });
    }

    private void initView(){
        mEmailView.setTextHint(R.string.str_settings_safety_email);
        mEmailView.getEditText().setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    }

    //获取手机验证码
    class GetVerifyCodeTask extends AsyncTask<String, Void, BLBaseResult> {
        BLProgressDialog blProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            blProgressDialog = BLProgressDialog.createDialog(getActivity(), null);
            blProgressDialog.show();
        }

        @Override
        protected BLBaseResult doInBackground(String... params) {
            BLBaseResult result = BLAccount.sendRegVCode(params[0]);
            return result;
        }

        @Override
        protected void onPostExecute(BLBaseResult result) {
            super.onPostExecute(result);
            blProgressDialog.dismiss();
            if (result != null && result.getError() == BLAppSdkErrCode.SUCCESS) {
                String email = mEmailView.getTextString();

                Bundle bundle = new Bundle();
                bundle.putString(BLConstants.INTENT_VALUE, email);
                AccountSignUpInfoCompleteFragment fragmnet = new AccountSignUpInfoCompleteFragment();
                fragmnet.setArguments(bundle);

                ((AccountSignUpActivity) getActivity()).addFragment(fragmnet, false);
            } else if (result != null) {
                mEmailView.setBackgroundResource(R.drawable.input_bg_round_tran_red);
                mErrorHintView.setText(result.getMsg());
                BLCommonUtils.toastShow(getActivity(), result.getMsg());
            } else {
                BLCommonUtils.toastShow(getActivity(), R.string.str_err_network);
            }
        }
    }

}
