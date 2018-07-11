package cn.com.broadlink.blappsdkdemo.activity.Device;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.intferfacer.DevConfigModel;
import cn.com.broadlink.blappsdkdemo.presenter.DevConfigIModeImpl;
import cn.com.broadlink.blappsdkdemo.presenter.DevConfigListener;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.param.controller.BLDeviceConfigParam;
import cn.com.broadlink.sdk.result.controller.BLAPConfigResult;

/**
 * 设备配置页面
 * Created by YeJin on 2016/5/10.
 */
public class DevConfigActivity extends Activity implements DevConfigListener{

    private EditText mSSIDView;

    private EditText mPasswordView;

    private SharedPreferences mWiFiPreferences;

    private DevConfigModel mDevConfigModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dev_config_layout);
        findView();

        mWiFiPreferences = getSharedPreferences("SHARED_PRE_WIFI_FILE", Context.MODE_PRIVATE);
        mDevConfigModel = new DevConfigIModeImpl();
    }

    private void findView(){
        mSSIDView = (EditText) findViewById(R.id.ssid_view);
        mPasswordView = (EditText) findViewById(R.id.password);
    }

    public void devConfig(View view){
        if(BLCommonUtils.isWifiConnect(DevConfigActivity.this)){
            String ssid = mSSIDView.getText().toString();
            String password = mPasswordView.getText().toString();

            BLDeviceConfigParam configParam = new BLDeviceConfigParam();
            configParam.setSsid(ssid);
            configParam.setPassword(password);
            configParam.setVersion(2);

            SharedPreferences.Editor editor = mWiFiPreferences.edit();
            editor.putString(ssid, password);
            editor.apply();

            mDevConfigModel.startConfig(configParam, this);

        }else{
            BLCommonUtils.toastShow(DevConfigActivity.this, "请连接WIFI");
        }
    }

    public void devConfig3(View view){
        if(BLCommonUtils.isWifiConnect(DevConfigActivity.this)){
            String ssid = mSSIDView.getText().toString();
            String password = mPasswordView.getText().toString();

            BLDeviceConfigParam configParam = new BLDeviceConfigParam();
            configParam.setSsid(ssid);
            configParam.setPassword(password);
            configParam.setVersion(3);

            SharedPreferences.Editor editor = mWiFiPreferences.edit();
            editor.putString(ssid, password);
            editor.apply();

            mDevConfigModel.startConfig(configParam, this);
        }else{
            BLCommonUtils.toastShow(DevConfigActivity.this, "请连接WIFI");
        }
    }

    public void apConfig(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String ssid = "BroadLink_ZJJ";
                String password = "1234567890";

                BLAPConfigResult result = BLLet.Controller.deviceAPConfig(ssid, password, 4, null);
            }
        }).start();
    }

    private ProgressDialog progressDialog;

    @Override
    public void configStart() {
        progressDialog = new ProgressDialog(DevConfigActivity.this);
        progressDialog.setMessage("配置中...");
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mDevConfigModel.cancleConfig();
            }
        });
        progressDialog.show();
    }

    @Override
    public void configend() {
        if(progressDialog != null){
            progressDialog.dismiss();
            BLCommonUtils.toastShow(DevConfigActivity.this, "配置结束");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerBroadcastReceiver();

        if(BLCommonUtils.isWifiConnect(DevConfigActivity.this)){
            initWiFiSSIDView();
        }
    }

    private WifiBroadcastReceiver mWifiBroadcastReceiver;

    public void registerBroadcastReceiver(){
        if(mWifiBroadcastReceiver == null){
            mWifiBroadcastReceiver = new WifiBroadcastReceiver();
            IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
            registerReceiver(mWifiBroadcastReceiver, intentFilter);
        }
    }

    public void unregisterReceiver(){
        if(mWifiBroadcastReceiver != null){
            unregisterReceiver(mWifiBroadcastReceiver);
            mWifiBroadcastReceiver = null;
        }
    }

    class WifiBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (BLCommonUtils.isWifiConnect(DevConfigActivity.this)) {
                initWiFiSSIDView();
            }
        }
    }

    //显示当前手机所连接的SSID
    public void initWiFiSSIDView() {
        String ssid = "";
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        try {
            WifiInfo info = wifi.getConnectionInfo();
            String CurInfoStr = info.toString() + "";
            String CurSsidStr = info.getSSID().toString() + "";
            if (CurInfoStr.contains(CurSsidStr)) {
                ssid = CurSsidStr;
            } else if(CurSsidStr.startsWith("\"") && CurSsidStr.endsWith("\"")){
                ssid = CurSsidStr.substring(1, CurSsidStr.length() - 1);
            } else {
                ssid = CurSsidStr;
            }
        } catch (Exception e) {
        }

        if (BLCommonUtils.isWifiConnect(DevConfigActivity.this) && !TextUtils.isEmpty(ssid)) {
            mSSIDView.setText(ssid);
            mPasswordView.setText(mWiFiPreferences.getString(ssid, null));
        }
    }
}
