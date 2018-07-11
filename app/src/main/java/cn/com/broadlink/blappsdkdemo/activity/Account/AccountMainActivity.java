package cn.com.broadlink.blappsdkdemo.activity.Account;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.BaseActivity;
import cn.com.broadlink.blappsdkdemo.common.BLSettings;
import cn.com.broadlink.blappsdkdemo.view.FlowIndicator;
import cn.com.broadlink.blappsdkdemo.view.cycleviewpager.AutoScrollViewPager;
import cn.com.broadlink.blappsdkdemo.view.cycleviewpager.RecyclingPagerAdapter;



public class AccountMainActivity extends BaseActivity {

    private FrameLayout mBannerLayout, mBottomLayout;

    private LinearLayout mLoginLayout, mSignUpLayout;

    private Button mBackBtn, mLoginBtn, mSignUpBtn;

    public LinkedList<AccountBaseFragemt> mFragmentBackStack = new LinkedList<>();

    private AutoScrollViewPager mADViewPager;

    private FlowIndicator mFlowIndicator;

    public AccountBaseFragemt mCurrtFragemnt;

    //底部栏定义
    public enum BottomBar {
        Back, Login, SignUp
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main_layout);

        findView();
        setListener();
        initView();
    }

    private void findView() {
        mADViewPager = (AutoScrollViewPager) findViewById(R.id.ad_viewpager);

        mBottomLayout = (FrameLayout) findViewById(R.id.bottom_layout);
        mBannerLayout = (FrameLayout) findViewById(R.id.banner_layout);

        mLoginLayout = (LinearLayout) findViewById(R.id.login_layout);
        mSignUpLayout = (LinearLayout) findViewById(R.id.signup_layout);

        mFlowIndicator = (FlowIndicator) findViewById(R.id.point_view);

        mBackBtn = (Button) findViewById(R.id.btn_back);
        mLoginBtn = (Button) findViewById(R.id.btn_login);
        mSignUpBtn = (Button) findViewById(R.id.btn_signup);
    }

    private void initView() {

        LoginFragement singUpTypeFragment = new LoginFragement();
        addFragment(singUpTypeFragment, false, BottomBar.SignUp);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mBannerLayout.getLayoutParams();
        params.height = (int) ((401f * BLSettings.P_WIDTH) / 750f);
        mBannerLayout.setLayoutParams(params);

        ArrayList<Integer> imageIdList = new ArrayList<>();

        imageIdList.add(R.drawable.login_banner1);
        imageIdList.add(R.drawable.login_banner2);
        imageIdList.add(R.drawable.login_banner3);

        mADViewPager.setAdapter(new ImagePagerAdapter(AccountMainActivity.this, imageIdList).setInfiniteLoop(true));

        mADViewPager.setInterval(2000);
        mADViewPager.startAutoScroll();
        mADViewPager.setAutoScrollDurationFactor(4);
        mADViewPager.setCurrentItem(Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % imageIdList.size());

        mFlowIndicator.setCount(imageIdList.size());

        mADViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                mFlowIndicator.setSeletion(position % 3);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    //fragment添加/切换
    public void addFragment(AccountBaseFragemt fragment, boolean addBackStack, BottomBar bottomType) {
        fragment.setBottomBar(bottomType);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (addBackStack) {
            //将上一个Fragment放入返回栈中，并且隐藏显示
            if (mCurrtFragemnt != null) {
                mFragmentBackStack.addLast(mCurrtFragemnt);
                fragmentTransaction.hide(mCurrtFragemnt);
            }

            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.add(R.id.content_layout, fragment).commit();
        } else {
            fragmentTransaction.replace(R.id.content_layout, fragment).commit();
        }

        mCurrtFragemnt = fragment;
        showBottomBar(bottomType);
    }

    private void setListener() {

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginFragement loginFragement = new LoginFragement();
                addFragment(loginFragement, false, BottomBar.SignUp);
            }
        });

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        mSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpFragment signUpFragment = new SignUpFragment();
                addFragment(signUpFragment, false, BottomBar.Login);
            }
        });
    }

    public void showBottomBar(BottomBar bottomType) {
        mLoginLayout.setVisibility(bottomType.equals(BottomBar.Login) ? View.VISIBLE : View.GONE);
        mSignUpLayout.setVisibility(bottomType.equals(BottomBar.SignUp) ? View.VISIBLE : View.GONE);
        mBackBtn.setVisibility(bottomType.equals(BottomBar.Back) ? View.VISIBLE : View.GONE);
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
    protected void onPause() {
        super.onPause();
        mADViewPager.stopAutoScroll();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mADViewPager.startAutoScroll();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        back();
    }

    @Override
    protected void back() {
        if (getSupportFragmentManager().getBackStackEntryCount() <= 0)
            AccountMainActivity.this.finish();
        else {
            getSupportFragmentManager().popBackStack();
            //取出队栈的最后一个Fragment
            mCurrtFragemnt = mFragmentBackStack.getLast();
            showBottomBar(mCurrtFragemnt.getBottomBar());
            mFragmentBackStack.removeLast();
        }
    }

    private class ImagePagerAdapter extends RecyclingPagerAdapter {

        private Context context;
        private List<Integer> imageIdList;

        private int size;
        private boolean isInfiniteLoop;

        public ImagePagerAdapter(Context context, List<Integer> imageIdList) {
            this.context = context;
            this.imageIdList = imageIdList;
            this.size = imageIdList.size();
            isInfiniteLoop = false;
        }

        @Override
        public int getCount() {
            return isInfiniteLoop ? Integer.MAX_VALUE : imageIdList.size();
        }

        /**
         * get really position
         *
         * @param position
         * @return
         */
        private int getPosition(int position) {
            return isInfiniteLoop ? position % size : position;
        }

        @Override
        public View getView(int position, View view, ViewGroup container) {
            ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = holder.imageView = new ImageView(context);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.imageView.setImageResource(imageIdList.get(getPosition(position)));
            return view;
        }

        private class ViewHolder {

            ImageView imageView;
        }

        /**
         * @return the isInfiniteLoop
         */
        public boolean isInfiniteLoop() {
            return isInfiniteLoop;
        }

        /**
         * @param isInfiniteLoop the isInfiniteLoop to set
         */
        public ImagePagerAdapter setInfiniteLoop(boolean isInfiniteLoop) {
            this.isInfiniteLoop = isInfiniteLoop;
            return this;
        }
    }

}
