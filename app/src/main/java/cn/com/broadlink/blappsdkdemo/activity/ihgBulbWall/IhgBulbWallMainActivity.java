package cn.com.broadlink.blappsdkdemo.activity.ihgBulbWall;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.wildma.pictureselector.PictureSelector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.view.tablayout.BLTabLayout;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;

/**
 * @author JiangYaqiang
 * 2019/6/25 17:21
 */
public class IhgBulbWallMainActivity extends TitleActivity {
    
    private BLTabLayout mTlTop;
    private ViewPager mVpCotent;
    private IhgBulbWallManageFragment mIhgBulbWallManageFragment;
    private IhgBulbWallControlFragment mIhgBulbWallControlFragment;
    private IhgBulbWallSettingsFragment mIhgBulbWallSettingsFragment;

    private String[] mTabStrs;
    private List<Fragment> mTableFragmentList = new ArrayList<>();
    private MyPagerAdapter mMyPagerAdapter;

    protected BLDNADevice mDNADevice;
    protected IhgBulbInfo mIhgBulbInfo;
    protected IhgBulbWallManager mIhgBulbWallManager;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ihg_bulbwall_main);
        setBackWhiteVisible();
        setTitle("Ihg Bulb Wall Demo");

        initData();

        findView();

        initView();

        setListener();
    }

    private void findView() {
        mTlTop = (BLTabLayout) findViewById(R.id.tl_top);
        mVpCotent = (ViewPager) findViewById(R.id.vp_cotent);
    }

    private void initData() {
        mDNADevice = getIntent().getParcelableExtra(BLConstants.INTENT_PARCELABLE);
        mIhgBulbInfo = new IhgBulbInfo(IhgBulbWallConstants.BULB_COUNT);
        mIhgBulbWallManager = new IhgBulbWallManager(mActivity);
    }

    private void initView() {
        mIhgBulbWallManageFragment = IhgBulbWallManageFragment.newInstance();
        mIhgBulbWallControlFragment = IhgBulbWallControlFragment.newInstance();
        mIhgBulbWallSettingsFragment = IhgBulbWallSettingsFragment.newInstance();

        mTableFragmentList.clear();
        mTableFragmentList.add(mIhgBulbWallControlFragment);
        mTableFragmentList.add(mIhgBulbWallManageFragment);
        mTableFragmentList.add(mIhgBulbWallSettingsFragment);

        mMyPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), mTableFragmentList);

        mTabStrs = new String[]{"Control", "Manage", "Settings"};

        mTlTop.addTab(mTlTop.newTab().setText(mTabStrs[0]));
        mTlTop.addTab(mTlTop.newTab().setText(mTabStrs[1]));
        mTlTop.addTab(mTlTop.newTab().setText(mTabStrs[2]));

        mVpCotent.setAdapter(mMyPagerAdapter);
        mTlTop.setupWithViewPager(mVpCotent);
        mTlTop.setTabsFromPagerAdapter(mMyPagerAdapter);
    }

    private void setListener() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!scriptFileExist(mDNADevice.getPid())) {
            new DownLoadResTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR, mDNADevice.getPid());
        }else{
            getAllStatus();
        }
    }

    private void getAllStatus() {
        mIhgBulbWallManager.getAllStatus(mDNADevice, new IhgBulbWallManager.IhgBulbCallBack() {
            @Override
            public void onResult(String result) {
                mIhgBulbInfo = mIhgBulbWallManager.parseStatus(result);
                mIhgBulbWallManageFragment.refreshView();
                mIhgBulbWallSettingsFragment.refreshView();
            }
        });
    }

    private boolean scriptFileExist(String pid){
        String scriptFilePath = BLLet.Controller.queryScriptPath(pid);
        Log.e("scriptFileExist" , scriptFilePath);
        File file = new File(scriptFilePath);
        return file.exists();
    }


    //脚本和ui包下载
    class DownLoadResTask extends AsyncTask<String, Void, BLBaseResult> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mActivity);
            progressDialog.setMessage("Resource downloading...");
            progressDialog.show();
        }

        @Override
        protected BLBaseResult doInBackground(String... params) {
            BLBaseResult result = null;

            if (!scriptFileExist(params[0])){
                result = BLLet.Controller.downloadScript(params[0]);
                if(result==null || !result.succeed()){
                    return result;
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(BLBaseResult result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            BLCommonUtils.toastErr(result);
            if (result != null && result.succeed()) {
                getAllStatus();
            }
        }
    }
    
    class MyPagerAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> mTableList;

        public MyPagerAdapter(FragmentManager fm, List<Fragment> roomlist) {
            super(fm);
            this.mTableList = roomlist;
        }

        @Override
        public Fragment getItem(int position) {
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PictureSelector.SELECT_REQUEST_CODE) {
            if(mIhgBulbWallControlFragment != null){
                mIhgBulbWallControlFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
    
    protected void onMacOrRgbListChanged(){
        mIhgBulbWallManageFragment.refreshView();
    }
}
