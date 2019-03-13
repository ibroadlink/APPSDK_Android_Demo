package cn.com.broadlink.blappsdkdemo.activity.h5;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.ConfigXmlParser;
import org.apache.cordova.CordovaInterfaceImpl;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewImpl;
import org.apache.cordova.engine.SystemWebView;
import org.apache.cordova.engine.SystemWebViewEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.BaseActivity;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLStorageUtils;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.service.BLLocalDeviceManager;
import cn.com.broadlink.blappsdkdemo.service.BLLocalFamilyManager;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.BLProgressDialog;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;
import cn.com.broadlink.sdk.result.controller.BLDownloadScriptResult;
import cn.com.broadlink.sdk.result.controller.BLDownloadUIResult;

import static cn.com.broadlink.blappsdkdemo.common.BLStorageUtils.H5_CUSTOM_PAGE;

/**
 * Created by zhujunjie on 2016/11/11.
 */

public class DeviceH5Activity extends BaseActivity {
    /**
     * 执行
     **/
    public static final int TYPE_SELECT_SCENE_EXECUTE_DEV = 0;
    /**
     * 触发
     **/
    public static final int TYPE_LINKAGE_TRIGGER_DEV = 1;
    /***条件**/
    public static final int TYPE_LINKAGE_CONDITION_DEV = 2;
    private static final int RESULTCODE_DEVICE_AUTH = 10;
    private static final int RESULTCODE_SELECT_LINKCAGE_PARAM = 6;
    public final ArrayBlockingQueue<String> onPageFinishedUrl = new ArrayBlockingQueue<String>(5);

    private CordovaWebView cordovaWebView;
    private SystemWebView mSystemWebView;
    private LinearLayout mContentWebLayout;
    private int mType;
    private CallbackContext mCurrentH5Callbacker;
    private CallbackContext mPropertyPageH5Callbacker;
    private CallbackContext mDeviceLinkageParamSelectCallback;
    
    public BLDNADevice mBlDeviceInfo;
    public String mLoadUrl, mDeviceUrlParam;
    protected CordovaInterfaceImpl cordovaInterface;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_control);

        initData();

        findView();

        initView();
    }

    private void initData() {
        mType = getIntent().getIntExtra(BLConstants.INTENT_TYPE, -1);
        mBlDeviceInfo = getIntent().getParcelableExtra(BLConstants.INTENT_DEVICE);
        mDeviceUrlParam = getIntent().getStringExtra(BLConstants.INTENT_PARAM);
        mLoadUrl  = getIntent().getStringExtra(BLConstants.INTENT_URL);
    }

    private void initView() {

        cordovaInterface = new CordovaInterfaceImpl(this) {

            @Override
            public Object onMessage(String id, Object data) {
                if ("onPageFinished".equals(id)) {
                    try {
                        onPageFinishedUrl.add((String) data);
                    } catch (java.lang.IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
                return super.onMessage(id, data);
            }
        };
        
        ConfigXmlParser parser = new ConfigXmlParser();
        parser.parse(this);
        
        mSystemWebView.getSettings().setJavaScriptEnabled(true);
        cordovaWebView = new CordovaWebViewImpl(new SystemWebViewEngine(mSystemWebView));
        cordovaWebView.init(cordovaInterface, parser.getPluginEntries(), parser.getPreferences());

        // 如果没有登录或者选择家庭，则不能进行H5控制
        if (!checkFamilyLogin()) {
            return;
        }

        // 只是加载url，跟设备无关
        if(!TextUtils.isEmpty(mLoadUrl)){
            cordovaWebView.loadUrl(mLoadUrl);
            return;
        }

        String h5Path = loadH5Url();
        String luaPath = BLStorageUtils.getScriptAbsolutePath(mBlDeviceInfo.getPid());

        //UI包不存在的时候，下载UI包
        if (TextUtils.isEmpty(h5Path) || !new File(h5Path).exists() || !new File(luaPath).exists()) {
            downLoadDevUiScript();
        } else {
            cordovaWebView.loadUrl(mLoadUrl);
        }
    }

    private void findView() {
        mContentWebLayout = (LinearLayout) findViewById(R.id.content_web_layout);
        mSystemWebView = (SystemWebView) findViewById(R.id.cordovaWebView);
    }

    private boolean checkFamilyLogin() {
        if (BLLocalFamilyManager.getInstance().getCurrentFamilyInfo() == null || BLApplication.mBLUserInfoUnits.getUserid() == null) {
            BLAlert.showAlert(mActivity, null, "Please login and add this device to a family!", new OnSingleClickListener() {
                @Override
                public void doOnClick(View v) {
                    back();
                }
            });
            return false;
        }
        return true;
    }

    private String loadH5Url() {
        String h5Path;
        if (mType == TYPE_SELECT_SCENE_EXECUTE_DEV) {
            h5Path = BLStorageUtils.getH5DeviceParamPath(mBlDeviceInfo.getPid());
        } else {
            h5Path = BLStorageUtils.getH5IndexPath(mBlDeviceInfo.getPid());
        }

        mLoadUrl = "file:///" + h5Path;
        if (!TextUtils.isEmpty(mDeviceUrlParam)) {
            mLoadUrl = mLoadUrl + mDeviceUrlParam;
        }
        return h5Path;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSystemWebView.onPause();
        if (cordovaWebView != null) {
            cordovaWebView.handlePause(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSystemWebView.onResume();
        if (cordovaWebView != null) {
            cordovaWebView.handleResume(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (cordovaWebView != null) {
            cordovaWebView.handleDestroy();
        }

        mContentWebLayout.removeView(mSystemWebView);
        mSystemWebView.removeAllViews();
        mSystemWebView.destroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (cordovaWebView != null) {
            cordovaWebView.handleStart();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (cordovaWebView != null) {
            cordovaWebView.handleStop();
        }
    }


    public void openAppPropertyPage(CallbackContext callbackContext) {
        mPropertyPageH5Callbacker = callbackContext;
        Intent intent = new Intent();
        intent.putExtra(BLConstants.INTENT_DEVICE, mBlDeviceInfo);
        intent.setClass(DeviceH5Activity.this, CommonModuleMoreActivity.class);
        startActivity(intent);
    }

    public void toDevAuthActivity(String jsonStr, CallbackContext callbackContext) {
        mCurrentH5Callbacker = callbackContext;
        Intent intent = new Intent();
        intent.putExtra(BLConstants.INTENT_DEVICE, mBlDeviceInfo);
        intent.putExtra(BLConstants.INTENT_VALUE, jsonStr);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setClass(DeviceH5Activity.this, DeviceAuthActivity.class);
        startActivityForResult(intent, RESULTCODE_DEVICE_AUTH);
    }

    //下载设备UI和脚本
    private void downLoadDevUiScript() {
        new DownLoadResourceTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
    }

    public void openLinkagePage(CallbackContext callbackContext, String did, ArrayList<String> pidList, ArrayList<String> protocolList){
        mDeviceLinkageParamSelectCallback = callbackContext;
        if(!TextUtils.isEmpty(did)){
            final BLDNADevice cachedDevice = BLLocalDeviceManager.getInstance().getCachedDevice(did);
            if(cachedDevice != null){
                if (selectSelectCmdAndExitSceneHtml(cachedDevice.getPid())) {
                    Intent intent = new Intent();
                    intent.putExtra(BLConstants.INTENT_DEVICE, cachedDevice);
                    intent.putExtra(BLConstants.INTENT_PARAM, H5_CUSTOM_PAGE);
                    intent.setClass(mActivity, DeviceH5Activity.class);
                }
            }
        }else{

        }
    }

    //本地UI包有选择场景的html提供
    private boolean selectSelectCmdAndExitSceneHtml(String pid){
        String h5Path = BLStorageUtils.getH5DeviceParamPath(pid);
        return h5Path != null && new File(h5Path).exists();
    }
    
    
    class DownLoadResourceTask extends AsyncTask<Void, Void, Boolean> {
        BLProgressDialog dismissProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dismissProgressDialog = BLProgressDialog.createDialog(DeviceH5Activity.this);
            dismissProgressDialog.setMessage("Downloading script and ui...");
            dismissProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String pid = mBlDeviceInfo.getPid();
            final BLDownloadScriptResult blDownloadScriptResult = BLLet.Controller.downloadScript(pid);
            if (blDownloadScriptResult == null || !blDownloadScriptResult.succeed()) {
                return false;
            }
            final BLDownloadUIResult blDownloadUIResult = BLLet.Controller.downloadUI(pid);
            if (blDownloadUIResult == null || !blDownloadUIResult.succeed()) {
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            dismissProgressDialog.dismiss();
            if (result) {
                loadH5Url();
                cordovaWebView.loadUrl(mLoadUrl);
            } else {
                BLToastUtils.show("Download script or ui fail!");
            }
        }
    }
}
