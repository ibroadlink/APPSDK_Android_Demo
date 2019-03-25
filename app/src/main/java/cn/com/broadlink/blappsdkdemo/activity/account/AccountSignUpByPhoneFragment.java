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
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.com.broadlink.account.BLAccount;
import cn.com.broadlink.base.BLAppSdkErrCode;
import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.BaseFragment;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.BLProgressDialog;
import cn.com.broadlink.blappsdkdemo.view.InputTextView;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;

/**
 * Created by YeJin on 2017/4/20.
 */

public class AccountSignUpByPhoneFragment extends BaseFragment {

    private LinearLayout mPhoneNumLayout;

    private TextView mErrorHintView;

    private InputTextView mPhoneView;

    private Button mCountryCodeBtn, mNextBtn;

    private String mCountryCode = "86";

    @Override
    public View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up_by_phone, container, false);

        findView(view);

        setListener();

        initView();

        return view;
    }

    private void findView(View view) {
        mPhoneNumLayout = (LinearLayout) view.findViewById(R.id.phone_num_layout);
        mErrorHintView = (TextView) view.findViewById(R.id.err_hint_view);
        mPhoneView = (InputTextView) view.findViewById(R.id.phone_view);
        mCountryCodeBtn = (Button) view.findViewById(R.id.btn_country_code);
        mNextBtn = (Button) view.findViewById(R.id.btn_next);
    }

    private void setListener() {
        mPhoneView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(mErrorHintView.getText().toString())) {
                    mErrorHintView.setText(null);
                    mPhoneNumLayout.setBackgroundResource(R.drawable.input_bg_round_tran_gray);
                }

                mNextBtn.setEnabled((s.length() > 0 && BLCommonUtils.isPhone(mPhoneView.getTextString())));
            }
        });

        mNextBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                String phone = mPhoneView.getTextString();
                new GetVerifyCodeTask().execute(phone);
            }
        });

        mCountryCodeBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                BLAlert.showEditDilog(getActivity(), "Input Country Code",  mCountryCode,
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

    private void initView() {
        mPhoneView.setTextHint(R.string.str_settings_safety_phone_number);
        mPhoneView.getEditText().setInputType(InputType.TYPE_CLASS_PHONE);
        mCountryCodeBtn.setText("+" + mCountryCode);
    }

    //获取手机验证码
    private class GetVerifyCodeTask extends AsyncTask<String, Void, BLBaseResult> {
        private String account;
        private BLProgressDialog blProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            blProgressDialog = BLProgressDialog.createDialog(getActivity(), null);
            blProgressDialog.show();
        }

        @Override
        protected BLBaseResult doInBackground(String... params) {
            //短信验证码
            account = params[0];
            return BLAccount.sendRegVCode(account, "+" + mCountryCode);
        }

        @Override
        protected void onPostExecute(BLBaseResult result) {
            super.onPostExecute(result);
            blProgressDialog.dismiss();
            if (result != null && result.getError() == BLAppSdkErrCode.SUCCESS) {
                Bundle bundle = new Bundle();
                bundle.putString(BLConstants.INTENT_CODE, mCountryCode);
                bundle.putString(BLConstants.INTENT_VALUE, account);
                AccountSignUpInfoCompleteFragment fragmnet = new AccountSignUpInfoCompleteFragment();
                fragmnet.setArguments(bundle);

                ((AccountSignUpActivity) getActivity()).addFragment(fragmnet, true);
            } else if (result != null) {
                mPhoneNumLayout.setBackgroundResource(R.drawable.input_bg_round_tran_red);
                BLCommonUtils.toastErr(result);
            } else {
                BLCommonUtils.toastShow(getActivity(), R.string.str_err_network);
            }
        }
    }
}