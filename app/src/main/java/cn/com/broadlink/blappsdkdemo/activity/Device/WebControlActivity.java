package cn.com.broadlink.blappsdkdemo.activity.Device;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

import org.apache.cordova.ConfigXmlParser;
import org.apache.cordova.CordovaInterfaceImpl;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewImpl;
import org.apache.cordova.engine.SystemWebView;
import org.apache.cordova.engine.SystemWebViewEngine;

import java.util.concurrent.ArrayBlockingQueue;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.common.BLStorageUtils;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;

/**
 * Created by zhujunjie on 2016/11/11.
 */

public class WebControlActivity extends Activity {
    private CordovaWebView cordovaWebView;
    private SystemWebView mSystemWebView;
    private LinearLayout mContentWebLayout;

    public final ArrayBlockingQueue<String> onPageFinishedUrl = new ArrayBlockingQueue<String>(5);
    public BLDNADevice mDNADevice;
    public String START_URL;

    protected CordovaInterfaceImpl cordovaInterface = new CordovaInterfaceImpl(this) {

        @Override
        public Object onMessage(String id, Object data) {
            if ("onPageFinished".equals(id)) {
                onPageFinishedUrl.add((String) data);
            }
            return super.onMessage(id, data);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_control_layout);

        mDNADevice = getIntent().getParcelableExtra("INTENT_DEV_ID");
        START_URL = "file:///" + BLStorageUtils.getH5IndexPath(mDNADevice.getPid());

        // Set up the webview
        ConfigXmlParser parser = new ConfigXmlParser();
        parser.parse(this);

        mContentWebLayout = (LinearLayout) findViewById(R.id.content_web_layout);
        mSystemWebView = (SystemWebView) findViewById(R.id.cordovaWebView);

        mSystemWebView.getSettings().setJavaScriptEnabled(true);
        cordovaWebView = new CordovaWebViewImpl(new SystemWebViewEngine(mSystemWebView));

        cordovaWebView.init(cordovaInterface, parser.getPluginEntries(), parser.getPreferences());
        cordovaWebView.loadUrl(START_URL);
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

        if(cordovaWebView != null){
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


}
