package cn.com.broadlink.blappsdkdemo.activity.account;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.BaseFragment;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;


/**
 * Created by YeJin on 2017/4/19.
 */

public class AccountSignUpTypeFragment extends BaseFragment {
    private Button mSignUoBtn;

    private AccountSignUpActivity mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (AccountSignUpActivity) getActivity();
    }

    @Override
    public View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_sign_up_type, container, false);

        findView(view);

        setListener();

        return view;
    }

    private void findView(View view){
        mSignUoBtn = (Button) view.findViewById(R.id.btn_signup);
    }

    private void setListener(){
        mSignUoBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                AccountSignUpFragment signUpFragment = new AccountSignUpFragment();
                mActivity.addFragment(signUpFragment, false);
            }
        });
    }
}
