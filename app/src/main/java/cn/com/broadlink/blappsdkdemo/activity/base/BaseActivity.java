package cn.com.broadlink.blappsdkdemo.activity.base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.activity.LoadingActivity;
import cn.com.broadlink.blappsdkdemo.db.DatabaseHelper;


public class BaseActivity extends FragmentActivity {

    public BLApplication mApplication;

    private volatile DatabaseHelper mHelper;
    
    protected BaseActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mApplication = (BLApplication) getApplication();
        mActivity = this;
        mApplication.mActivityList.add(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mHelper != null) {
            OpenHelperManager.releaseHelper();
            mHelper = null;
        }
        mApplication.mActivityList.remove(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private AlertDialog mDialog;

    public boolean inRangeOfView(View view, MotionEvent ev) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        if (ev.getX() < x || ev.getX() > (x + view.getWidth()) || ev.getY() < y || ev.getY() > (y + view.getHeight())) {
            return false;
        }
        return true;
    }

    protected void back() {
        finish();
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config,res.getDisplayMetrics() );
        return res;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //系统回收activity的时候重新从loading启动，避免各种未处理的crash
        Intent intent = new Intent(BaseActivity.this, LoadingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public DatabaseHelper getHelper() {
        if (mHelper == null) {
            mHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return mHelper;
    }

    /**关闭手机键盘**/
    public void closeInputMethod() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (BaseActivity.this.getCurrentFocus() != null) {
            if (BaseActivity.this.getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(BaseActivity.this.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
