package cn.com.broadlink.blappsdkdemo.activity.device;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;

import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;
import cn.com.broadlink.sdk.result.controller.BLDeviceTimeResult;

public class DevMainMenuActivity extends TitleActivity implements View.OnClickListener {

    private EditText mTvDevInfo;
    private EditText mTvResult;
    private Button mBtPassthrough;
    private Button mBtStd;
    private Button mBtTimer;
    private Button mBtGateway;
    private Button mBtFastcon;
    private Button mBtQueryState;
    private Button mBtQueryFirmware;
    private Button mBtUpdateFirmware;
    private Button mBtRm;
    private Button mBtServeTime;
    private Button mBtQueryConnectServer;
    private Button mBtStressTest;

    private BLDNADevice mDNADevice;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_main_menu);
        setBackWhiteVisible();
        setTitle("Device Main Menu");
        
        mDNADevice = getIntent().getParcelableExtra(BLConstants.INTENT_DEVICE);
        
        findView();

        initView();

        setListener();
    }

    private void setListener() {
        mBtPassthrough.setOnClickListener(this);
        mBtStd.setOnClickListener(this);
        mBtTimer.setOnClickListener(this);
        mBtGateway.setOnClickListener(this);
        mBtFastcon.setOnClickListener(this);
        mBtQueryState.setOnClickListener(this);
        mBtQueryFirmware.setOnClickListener(this);
        mBtUpdateFirmware.setOnClickListener(this);
        mBtRm.setOnClickListener(this);
        mBtServeTime.setOnClickListener(this);
        mBtQueryConnectServer.setOnClickListener(this);
        mBtStressTest.setOnClickListener(this);
    }

    private void initView() {
        mTvDevInfo.setText(mDNADevice.toJSONString());
    }

    private void findView() {
        mTvDevInfo = (EditText) findViewById(R.id.tv_dev_info);
        mTvResult = (EditText) findViewById(R.id.et_result);
        mBtPassthrough = (Button) findViewById(R.id.bt_passthrough);
        mBtStd = (Button) findViewById(R.id.bt_std);
        mBtTimer = (Button) findViewById(R.id.bt_timer);
        mBtGateway = (Button) findViewById(R.id.bt_gateway);
        mBtFastcon = (Button) findViewById(R.id.bt_fastcon);
        mBtQueryState = (Button) findViewById(R.id.bt_query_state);
        mBtQueryFirmware = (Button) findViewById(R.id.bt_query_firmware);
        mBtUpdateFirmware = (Button) findViewById(R.id.bt_update_firmware);
        mBtRm = (Button) findViewById(R.id.bt_rm);
        mBtServeTime = (Button) findViewById(R.id.bt_query_sever_time);
        mBtQueryConnectServer = (Button) findViewById(R.id.bt_server);
        mBtStressTest = (Button) findViewById(R.id.bt_stress_test);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_passthrough:
                BLCommonUtils.toActivity(mActivity, DevDataPassThroughActivity.class, mDNADevice);
                break;
                
            case R.id.bt_std:
                BLCommonUtils.toActivity(mActivity, DevDnaStdControlActivity.class, mDNADevice);
                break;
                
            case R.id.bt_timer:
                BLAlert.showEditDilog(mActivity, null, "If you mean to manage sub device's timer, input subDid", null, "subDid", "Sub device", "Gageway device",
                        new BLAlert.BLEditDialogOnClickListener() {
                            @Override
                            public void onClink(String value) {
                                if(TextUtils.isEmpty(value) || value.length() != 32){
                                    BLToastUtils.show("Did invalid!");
                                    return;
                                }
                                
                                final Intent intent = new Intent(mActivity, DevTimerManageActivity.class);
                                intent.putExtra(BLConstants.INTENT_ID, value);
                                intent.putExtra(BLConstants.INTENT_PARCELABLE, mDNADevice);
                                startActivity(intent);
                            }

                            @Override
                            public void onClinkCacel(String value) {
                                BLCommonUtils.toActivity(mActivity, DevTimerManageActivity.class, mDNADevice);
                            }
                        }, false);
               
                break;
                
            case R.id.bt_gateway:
                BLCommonUtils.toActivity(mActivity, DevGatewayManageActivity.class, mDNADevice);
                break;
                
            case R.id.bt_fastcon:
                BLCommonUtils.toActivity(mActivity, DevFastconManageActivity.class, mDNADevice);
                break;
                
            case R.id.bt_query_state:
                new QueryDeviceStatusTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
                break;
                
            case R.id.bt_query_sever_time:
                new QueryDeviceTimeTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
                break;
                
            case R.id.bt_query_firmware:
                new QueryFirmwareVersionTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
                break;
                
            case R.id.bt_update_firmware:
                BLAlert.showEditDilog(mActivity, "Please input firmware update Url", null, new BLAlert.BLEditDialogOnClickListener() {
                    @Override
                    public void onClink(String value) {
                        if(!BLCommonUtils.isURL(value)){
                            BLToastUtils.show("Please input a valid url!");  
                        }else{
                            new UpdateFirmwareTask().execute(value);
                        }
                    }

                    @Override
                    public void onClinkCacel(String value) {

                    }
                }, false);
                
                break;
                
            case R.id.bt_server:
                new QueryDeviceConnectServerListTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
                break;
                
            case R.id.bt_rm:
                BLCommonUtils.toActivity(mActivity, DevRmIrControlActivity.class, mDNADevice);
                break;
                
            case R.id.bt_stress_test:
                BLCommonUtils.toActivity(mActivity, DevStressTestActivity.class, mDNADevice);
                break;
        }
    }

    public void setResult(Object result){
        if(result == null){
            mTvResult.setText("Return null!");
        }else{
            if(result instanceof  String){
                mTvResult.setText((String)result);
            }else{
                mTvResult.setText(JSON.toJSONString(result, true));
            }  
        }
    }
    
    public String transStatusCode(int state){
        String status;
        switch (state) {
            case 0:
                status = "Init";
                break;
            case 1:
                status = "Lan";
                break;
            case 2:
                status = "Remote Online";
                break;
            case 3:
                status = "Remote Offline";
                break;
            default:
                status = "Unknown";
        }
        return status;
    }

    //服务器设备时间查询
    class QueryDeviceTimeTask extends AsyncTask<Void, Void, BLDeviceTimeResult>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Query Device Time...");
        }

        @Override
        protected BLDeviceTimeResult doInBackground(Void... params) {
            return BLLet.Controller.queryDeviceTime(mDNADevice.getDid());
        }

        @Override
        protected void onPostExecute(BLDeviceTimeResult result) {
            super.onPostExecute(result);
            dismissProgressDialog();
            setResult(JSON.toJSONString(result, true));
        }
    }

    //脚本版本查询
    class QueryDeviceStatusTask extends AsyncTask<Void, Void, Integer>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Query Device Status...");
        }

        @Override
        protected Integer doInBackground(Void... params) {
            return BLLet.Controller.queryDeviceRemoteState(mDNADevice.getDid());
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            dismissProgressDialog();
            setResult(transStatusCode(result) + " - " + result);
        }
    }

    
    //查询固件版本
    class QueryFirmwareVersionTask extends AsyncTask<Void, Void, BLBaseResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Query Firmware Version...");
        }

        @Override
        protected BLBaseResult doInBackground(Void... params) {
            return BLLet.Controller.queryFirmwareVersion(mDNADevice.getDid());
        }

        @Override
        protected void onPostExecute(BLBaseResult result) {
            super.onPostExecute(result);
            dismissProgressDialog();
            setResult(result);
        }
    }
    
    //查询固件版本
    class UpdateFirmwareTask extends AsyncTask<String, Void, BLBaseResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Update Firmware...");
        }

        @Override
        protected BLBaseResult doInBackground(String... params) {
            return BLLet.Controller.updateFirmware(mDNADevice.getDid(), params[0]);
        }

        @Override
        protected void onPostExecute(BLBaseResult result) {
            super.onPostExecute(result);
            dismissProgressDialog();
            setResult(result);
        }
    }


    //查询设备连接服务器
    class QueryDeviceConnectServerListTask extends AsyncTask<Void, Void, BLBaseResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Query Device Connect Server List...");
        }

        @Override
        protected BLBaseResult doInBackground(Void... params) {
            return BLLet.Controller.queryDeviceConnectServerInfo(mDNADevice.getDid());
        }

        @Override
        protected void onPostExecute(BLBaseResult result) {
            super.onPostExecute(result);
            dismissProgressDialog();
            setResult(result);
        }
    }
}
