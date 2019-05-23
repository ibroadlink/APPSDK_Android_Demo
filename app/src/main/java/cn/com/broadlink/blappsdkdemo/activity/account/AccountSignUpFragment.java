package cn.com.broadlink.blappsdkdemo.activity.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.BaseFragment;


/**
 * Created by YeJin on 2017/4/19.
 */

public class AccountSignUpFragment extends BaseFragment {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private String[] mTabStrs;

    private List<BaseFragment> mTableFragmentList = new ArrayList<>();

    private MyPagerAdapter mMyPagerAdapter;

    private AccountSignUpByPhoneFragment mSignUpByPhonelFragmnet;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSignUpByPhonelFragmnet = new AccountSignUpByPhoneFragment();
        mTableFragmentList.clear();
        mTableFragmentList.add(new AccountSignUpByEmailFragmnet());
        mTableFragmentList.add(mSignUpByPhonelFragmnet);

        mMyPagerAdapter = new MyPagerAdapter(getFragmentManager(), mTableFragmentList);
    }

    @Override
    public View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_signup, container, false);

        findView(view);

        initView();

        return view;
    }

    private void findView(View view){
        mViewPager = (ViewPager) view.findViewById(R.id.sign_boby_layout);

        mTabLayout = (TabLayout) view.findViewById(R.id.sign_tabs_layout);
    }

    private void initView(){
        mTabStrs = new String[]{getString(R.string.str_eamil), getString(R.string.str_phone)};

        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.addTab(mTabLayout.newTab().setText(mTabStrs[0]));
        mTabLayout.addTab(mTabLayout.newTab().setText(mTabStrs[1]));


        mViewPager.setAdapter(mMyPagerAdapter);//给ViewPager设置适配器
        mTabLayout.setupWithViewPager(mViewPager);//将TabLayout和ViewPager关联起来。
        mTabLayout.setTabsFromPagerAdapter(mMyPagerAdapter);//给Tabs设置适配器
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mSignUpByPhonelFragmnet.onActivityResult(requestCode, resultCode, data);
    }

    //ViewPager适配器
    class MyPagerAdapter extends FragmentStatePagerAdapter {

        private List<BaseFragment> mTableList = new ArrayList<>();

        public MyPagerAdapter(FragmentManager fm, List<BaseFragment> roomlist) {
            super(fm);
            this.mTableList = roomlist;
        }

        @Override
        public BaseFragment getItem(int position) {
            return mTableList.get(position);
        }

        @Override
        public int getCount() {
            return mTableList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabStrs[position];
        }
    }
}