package cn.com.broadlink.blappsdkdemo.activity.account;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import java.util.LinkedList;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.BaseFragment;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;

public class AccountSignUpActivity extends TitleActivity {

    public LinkedList<BaseFragment> mFragmentBackStack = new LinkedList<>();
    public BaseFragment mCurrtFragemnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_login_main);
        setTitle(R.string.account_sign_up);
        setBackWhiteVisible();
        initView();
    }

    private void initView() {
        AccountSignUpFragment singUpTypeFragment = new AccountSignUpFragment();
        addFragment(singUpTypeFragment, false);
    }

    //fragment添加/切换
    public void addFragment(BaseFragment fragment, boolean addBackStack) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (addBackStack) {
            //将上一个Fragment放入返回栈中，并且隐藏显示
            if (mCurrtFragemnt != null) {
                mFragmentBackStack.addLast(mCurrtFragemnt);
                fragmentTransaction.hide(mCurrtFragemnt);
            }

            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.add(R.id.fl_content, fragment).commit();
        } else {
            fragmentTransaction.replace(R.id.fl_content, fragment).commit();
        }

        mCurrtFragemnt = fragment;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP
                || event.getAction() == MotionEvent.ACTION_CANCEL) {
            closeInputMethod();
        }
        return super.dispatchTouchEvent(event);
    }


    @Override
    public void onBackPressed() {
        back();
    }

    @Override
    protected void back() {
        if (getSupportFragmentManager().getBackStackEntryCount() <= 0)
            AccountSignUpActivity.this.finish();
        else {
            getSupportFragmentManager().popBackStack();
            //取出队栈的最后一个Fragment
            mCurrtFragemnt = mFragmentBackStack.getLast();
            mFragmentBackStack.removeLast();
        }
    }
}
