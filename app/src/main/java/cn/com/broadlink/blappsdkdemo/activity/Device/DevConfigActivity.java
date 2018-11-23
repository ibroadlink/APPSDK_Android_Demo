package cn.com.broadlink.blappsdkdemo.activity.Device;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.intferfacer.DevConfigModel;
import cn.com.broadlink.blappsdkdemo.presenter.DevConfigIModeImpl;
import cn.com.broadlink.blappsdkdemo.presenter.DevConfigListener;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLAPInfo;
import cn.com.broadlink.sdk.data.controller.BLGetAPListResult;
import cn.com.broadlink.sdk.param.controller.BLDeviceConfigParam;
import cn.com.broadlink.sdk.result.controller.BLAPConfigResult;

/**
 * 设备配置页面
 * Created by YeJin on 2016/5/10.
 */
public class DevConfigActivity extends TitleActivity implements DevConfigListener{

    private EditText mSSIDView;
    private EditText mPasswordView;
    private ListView mScanAPListView;
    private APListAdapter mAdapter;
    private List<BLAPInfo> mAPList = new ArrayList<>();

    private SharedPreferences mWiFiPreferences;

    private DevConfigModel mDevConfigModel;
    private BLAPInfo mSelectAPInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.Config_Device);
        setContentView(R.layout.dev_config_layout);
        setBackWhiteVisible();
        findView();

        mWiFiPreferences = getSharedPreferences("SHARED_PRE_WIFI_FILE", Context.MODE_PRIVATE);
        mDevConfigModel = new DevConfigIModeImpl();
    }

    private void findView() {
        mSSIDView = findViewById(R.id.ssid_view);
        mPasswordView =  findViewById(R.id.password);
        mScanAPListView = findViewById(R.id.scan_ap_list);
        mPasswordView.requestFocus();

        mAdapter = new APListAdapter(DevConfigActivity.this, mAPList);
        mScanAPListView.setAdapter(mAdapter);

        mScanAPListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                mSelectAPInfo = mAPList.get(position);
                mSSIDView.setText(mSelectAPInfo.getSsid());
                mPasswordView.setText("");
            }
        });
    }

    public void devConfigByVersion2(View view){
        if (BLCommonUtils.isWifiConnect(DevConfigActivity.this)) {
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

        } else {
            BLCommonUtils.toastShow(DevConfigActivity.this, "Please connect Wi-Fi first!");
        }
    }

    public void devConfigByVersion3(View view){
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
            BLCommonUtils.toastShow(DevConfigActivity.this, "Please connect Wi-Fi first!");
        }
    }

    public void apConfigScan(View view) {
        new ScanAPListTask().execute();
    }

    public void apConfig(View view) {

        if (mSelectAPInfo != null) {
            new APConfigTask().execute();
        } else {
            BLCommonUtils.toastShow(DevConfigActivity.this, "Please select ap first!");
        }
    }

    private ProgressDialog progressDialog;

    @Override
    public void configStart() {
        progressDialog = new ProgressDialog(DevConfigActivity.this);
        progressDialog.setMessage("Config...");
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
            BLCommonUtils.toastShow(DevConfigActivity.this, "Config Over");
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

    //AP 配置
    class APConfigTask extends AsyncTask<Void, Void, BLAPConfigResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DevConfigActivity.this);
            progressDialog.setMessage("Config...");
            progressDialog.show();
        }

        @Override
        protected BLAPConfigResult doInBackground(Void... params) {
            String ssid = mSSIDView.getText().toString();
            String password = mPasswordView.getText().toString();
            int type = mSelectAPInfo.getType();

            return BLLet.Controller.deviceAPConfig(ssid, password, type, null);
        }

        @Override
        protected void onPostExecute(BLAPConfigResult result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            BLCommonUtils.toastShow(DevConfigActivity.this, result.getMsg());
            if (result.succeed()) {
                finish();
            }
        }

    }

    //AP List 扫描
    class ScanAPListTask extends AsyncTask<Void, Void, BLGetAPListResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DevConfigActivity.this);
            progressDialog.setMessage("Scanning...");
            progressDialog.show();
        }

        @Override
        protected BLGetAPListResult doInBackground(Void... params) {
            return BLLet.Controller.deviceAPList(8 * 1000);
        }

        @Override
        protected void onPostExecute(final BLGetAPListResult result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            if (result.succeed()) {
                mAPList.clear();
                mAPList.addAll(result.getList());
                mAdapter.notifyDataSetChanged();
            } else {
                BLCommonUtils.toastShow(DevConfigActivity.this, result.getMsg());
            }
        }
    }

    private class APListAdapter extends ArrayAdapter<BLAPInfo> {
        public APListAdapter(Context context, List<BLAPInfo> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.adapter_device, null);
                viewHolder.ssid = convertView.findViewById(R.id.tv_name);
                viewHolder.type = convertView.findViewById(R.id.tv_mac);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            BLAPInfo info = getItem(position);
            if (info != null) {
                viewHolder.ssid.setText(info.getSsid());

                String type = "NONE";
                switch (info.getType()) {
                    case 0:
                        type = "NONE";
                        break;
                    case 1:
                        type = "WEP";
                        break;
                    case 2:
                        type = "WPA";
                        break;
                    case 3:
                        type = "WPA2";
                        break;
                    case 4:
                        type = "WPA/WPA2 MIXED";
                        break;
                }

                viewHolder.type.setText("Type: " + type);
            }

            return convertView;
        }

        private class ViewHolder{
            TextView ssid;
            TextView type;
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
