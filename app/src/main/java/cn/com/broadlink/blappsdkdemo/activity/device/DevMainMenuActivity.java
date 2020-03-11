package cn.com.broadlink.blappsdkdemo.activity.device;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;

import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.base.fastjson.BLJSON;
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.MainActivity;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.activity.ihgBulbWall.IhgBulbWallMainActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLMultDidUtils;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.db.dao.BLDeviceInfoDao;
import cn.com.broadlink.blappsdkdemo.db.data.BLDeviceInfo;
import cn.com.broadlink.blappsdkdemo.service.BLLocalDeviceManager;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.InputTextView;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.constants.controller.BLDevCmdConstants;
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
    private Button mBtSp;
    private Button mBtServeTime;
    private Button mBtQueryConnectServer;
    private Button mBtFwLog;
    private Button mBtReset;
    private Button mBtIhgBulb;
    private Button mBtFastconSubGroup;
    private Button mBtFastconBridge;
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
        mBtSp.setOnClickListener(this);
        mBtServeTime.setOnClickListener(this);
        mBtQueryConnectServer.setOnClickListener(this);
        mBtFwLog.setOnClickListener(this);
        mBtReset.setOnClickListener(this);
        mBtIhgBulb.setOnClickListener(this);
        mBtFastconSubGroup.setOnClickListener(this);
        mBtFastconBridge.setOnClickListener(this);
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
        mBtSp = (Button) findViewById(R.id.bt_sp);
        mBtServeTime = (Button) findViewById(R.id.bt_query_sever_time);
        mBtQueryConnectServer = (Button) findViewById(R.id.bt_server);
        mBtFwLog = (Button) findViewById(R.id.bt_fw_log);
        mBtReset = (Button) findViewById(R.id.bt_reset);
        mBtIhgBulb = (Button) findViewById(R.id.bt_ihg_bulb_wall);
        mBtFastconSubGroup = (Button) findViewById(R.id.bt_fastcon_sub_group);
        mBtFastconBridge = (Button) findViewById(R.id.bt_fastcon_bridge);
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
                            public void onCancel(String value) {
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
                showUpgradeDialog();
                break;
                
            case R.id.bt_server:
                new QueryDeviceConnectServerListTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
                break;
                
            case R.id.bt_rm:
                BLCommonUtils.toActivity(mActivity, DevRmIrControlActivity.class, mDNADevice);
                break;
                
            case R.id.bt_sp:
                BLCommonUtils.toActivity(mActivity, DevSpControlActivity.class, mDNADevice);
                break;
                
            case R.id.bt_fw_log:
                BLCommonUtils.toActivity(mActivity, DevFirmwareLogActivity.class, mDNADevice);
                break;
                
            case R.id.bt_reset:
                new ResetTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
                break;
                
            case R.id.bt_ihg_bulb_wall:
                BLCommonUtils.toActivity(mActivity, IhgBulbWallMainActivity.class, mDNADevice);
                break;
                
            case R.id.bt_fastcon_sub_group:
                BLCommonUtils.toActivity(mActivity, DevGroupDevListActivity.class, mDNADevice);
                break;
            case R.id.bt_fastcon_bridge:
                BLCommonUtils.toActivity(mActivity, DevFastconBridgeActivity.class, mDNADevice);
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



    private void showUpgradeDialog(){

        final LinearLayout dialog = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_subdev_firmware_upgrade, null);
        final InputTextView etUrl = dialog.findViewById(R.id.et_url);
        final InputTextView etSsid = dialog.findViewById(R.id.et_ssid);
        final InputTextView etPwd = dialog.findViewById(R.id.et_pwd);

        BLAlert.showCustomViewDilog(mActivity, dialog, "Ok", "Cancel", new BLAlert.DialogOnClickListener() {
            @Override
            public void onPositiveClick() {
                final String url = etUrl.getTextString();
                final String ssid = etSsid.getTextString();
                final String pwd = etPwd.getTextString();

                if(TextUtils.isEmpty(url)){
                    BLToastUtils.show("Url is null");
                    return;
                }
                if(!TextUtils.isEmpty(ssid)){
                    new UpdateFirmwareV2Task().execute(url, ssid, pwd);
                }else{
                    new UpdateFirmwareV2Task().execute(url);
                }
            }

            @Override
            public void onNegativeClick() {

            }
        });
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
            return BLLet.Controller.queryDeviceTime(mDNADevice.getDeviceId());
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
            return BLLet.Controller.queryDeviceRemoteState(mDNADevice.getDeviceId());
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
            if (mDNADevice.getpDid() != null){
                final String[] cachedDeviceId = BLMultDidUtils.getCachedDeviceId(mDNADevice);
                return BLLet.Controller.devSubDevVersion(cachedDeviceId[0], cachedDeviceId[1], null);
            }
            
//            return BLLet.Controller.queryFirmwareVersion(mDNADevice.getDeviceId());

//            String devJson = "{\n" + "  \"key\" : \"0000000000000000000000700e0887b5\",\n" + "  \"id\" : 1,\n" + "  \"state\" : 2,\n" + "  \"refreshStateFlag\" : 0,\n" + "  " +
//                    "\"password\" : 0,\n" + "  \"lastStateRefreshTime\" : 605526735.65963697,\n" + "  \"type\" : 25157,\n" + "  \"addFlag\" : 0,\n" + "  \"pid\" : " +
//                    "\"00000000000000000000000045620000\",\n" + "  \"newConfig\" : 0,\n" + "  \"deviceFlag\" : 0,\n" + "  \"mac\" : \"34:e3:34:00:15:be\",\n" + "  \"did\" : " +
//                    "\"0000000000000000000034e3340015be\",\n" + "  \"lock\" : 0\n" + "}";
//            final BLDNADevice bldnaDevice = BLJSON.parseObject(devJson, BLDNADevice.class);
//            BLLet.Controller.addDevice(bldnaDevice);
//            return BLLet.Controller.queryFirmwareVersionOnServer(bldnaDevice.getDeviceId(), null);
            
              return BLLet.Controller.queryFirmwareVersionOnServer(mDNADevice.getDeviceId(), null);
        }

        @Override
        protected void onPostExecute(BLBaseResult result) {
            super.onPostExecute(result);
            dismissProgressDialog();
            setResult(result);
        }
    }
    
    //升级固件版本
    class UpdateFirmwareV2Task extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Update Firmware...");
        }

        @Override
        protected String doInBackground(String... params) {
            
            final String[] cachedDeviceId = BLMultDidUtils.getCachedDeviceId(mDNADevice);
            if(cachedDeviceId[1]==null){
                final BLBaseResult result = BLLet.Controller.updateFirmware(cachedDeviceId[0], params[0]);
                return BLJSON.toJSONString(result);  
            }else{
                JSONObject urlObject = new JSONObject();
                try {
                    urlObject.put("did", mDNADevice.getDid());
                    urlObject.put("fwurl", params[0]);
                    if(params.length==3){
                        urlObject.put("ssid", params[1]);
                        urlObject.put("psk", params[2]);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return BLLet.Controller.dnaControl(cachedDeviceId[0], cachedDeviceId[1], urlObject.toString(),  BLDevCmdConstants.DEV_SUBDEV_UPGRADE, null);
            }
        }

        @Override
        protected void onPostExecute(String result) {
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
            return BLLet.Controller.queryDeviceConnectServerInfo(mDNADevice.getDeviceId());
        }

        @Override
        protected void onPostExecute(BLBaseResult result) {
            super.onPostExecute(result);
            dismissProgressDialog();
            setResult(result);
        }
    }

    //复位设备
    class ResetTask extends AsyncTask<Void, Void, String> {
        String[] didOrSubDid = new String[2];
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Reset...");
            didOrSubDid = BLCommonUtils.parseDidOrSubDid(mDNADevice);
        }

        @Override
        protected String doInBackground(Void... params) {
            if(didOrSubDid[1]==null){
                return BLMultDidUtils.dnaControl(didOrSubDid[0], null, "{}", "dev_reset", null);
            }else{
                return BLMultDidUtils.dnaControl(didOrSubDid[0], didOrSubDid[1], "a5a55a5a92c2e803020000007b7d", "fastcon_client_control", null);
            }

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dismissProgressDialog();
            final BLBaseResult baseResult = JSON.parseObject(result, BLBaseResult.class);
            if(baseResult != null && baseResult.succeed()){
                BLLocalDeviceManager.getInstance().removeDeviceFromSDK(mDNADevice);
                try {
                    BLDeviceInfoDao blDeviceInfoDao = new BLDeviceInfoDao(getHelper());
                    BLDeviceInfo deviceInfo = new BLDeviceInfo(mDNADevice);
                    blDeviceInfoDao.deleteDevice(deviceInfo);
                } catch (SQLException e) {
                    e.printStackTrace();
                } 
                
                BLAlert.showAlert(mActivity, null, "Reset success, device has been removed from sdk, you need to config it manually", new OnSingleClickListener() {
                    @Override
                    public void doOnClick(View v) {
                        BLCommonUtils.toActivity(mActivity, MainActivity.class);
                    }
                });
            }
            setResult(result);
        }
    }
}
