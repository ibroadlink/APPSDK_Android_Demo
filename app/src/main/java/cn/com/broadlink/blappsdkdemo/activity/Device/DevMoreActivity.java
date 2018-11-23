package cn.com.broadlink.blappsdkdemo.activity.Device;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;

import java.io.File;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;

import cn.com.broadlink.blappsdkdemo.data.BLDevProfileInfo;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.constants.controller.BLControllerErrCode;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;

import cn.com.broadlink.sdk.result.controller.BLDeviceTimeResult;
import cn.com.broadlink.sdk.result.controller.BLDownloadScriptResult;
import cn.com.broadlink.sdk.result.controller.BLDownloadUIResult;
import cn.com.broadlink.sdk.result.controller.BLProfileStringResult;
import cn.com.broadlink.sdk.result.controller.BLQueryResoureVersionResult;
import cn.com.broadlink.sdk.result.controller.BLResourceVersion;


public class DevMoreActivity extends TitleActivity {

    /**RM 分类**/
    private static final String CATEGORY_RM = "1.1.5";

    /**SP 分类**/
    private static final String CATEGORY_SP = "4.1.50";

    private BLDNADevice mDNADevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dev_more_layout);
        setTitle(R.string.Device_More_View);
        setBackWhiteVisible();

        mDNADevice = getIntent().getParcelableExtra("INTENT_DEV_ID");
    }

    //查询设备在线状态
    public void queryDeviceStatusOnServer(View view) {
        new QueryDeviceStatusTask().execute();
    }

    //查询设备在服务器时间
    public void queryDeviceServerTime(View view) {
        new QueryDeviceTimeTask().execute();
    }

    //脚本版本查询
    public void scriptVersion(View view){
        new QueryScriptVersionTask().execute();
    }

    //脚本下载
    public void scriptDown(View view){
        new DownLoadScriptTask().execute();
    }

    //UI版本查询
    public void uiVersion(View view){
        new QueryUIVersionTask().execute();
    }

    //UI下载
    public void uiDown(View view){
        new DownLoadUITask().execute();
    }

    //使用下载脚本控制设备
    public void dnaControl(View view) {
        if (scriptFileExist()) {
            Intent intent = new Intent();
            intent.putExtra("INTENT_DEV_ID", mDNADevice);
            intent.setClass(DevMoreActivity.this, DnaControlActivity.class);
            startActivity(intent);
        } else {
            BLCommonUtils.toastShow(DevMoreActivity.this, "Script not exit, please download first!");
        }
    }

    //使用下载UI包控制设备
    public void webControl(View view) {
        if (scriptFileExist() && uiFileExit()) {
            Intent intent = new Intent();
            intent.putExtra("INTENT_DEV_ID", mDNADevice);
            intent.setClass(DevMoreActivity.this, WebControlActivity.class);
            startActivity(intent);
        } else {
            BLCommonUtils.toastShow(DevMoreActivity.this, "Script/Ui not exit, please download first!");
        }
    }

    //SP控制
    public void spControl(View view) {
        if(scriptFileExist()) {
            BLDevProfileInfo profileInfo = queryDevProfile();
            if(profileInfo.getSrvs().get(0).equals(CATEGORY_SP)){
                Intent intent = new Intent();
                intent.putExtra("INTENT_DEV_ID", mDNADevice);
                intent.setClass(DevMoreActivity.this, SPDemoActivity.class);
                startActivity(intent);
            }else{
                BLCommonUtils.toastShow(DevMoreActivity.this, "This device is not sp device!");
            }
        } else {
            BLCommonUtils.toastShow(DevMoreActivity.this, "Script not exit, please download first!");
        }
    }

    //RM控制
    public void rmControl(View view){
        if(scriptFileExist()){
            BLDevProfileInfo profileInfo = queryDevProfile();
            if(profileInfo.getSrvs().get(0).equals(CATEGORY_RM)){
                Intent intent = new Intent();
                intent.putExtra("INTENT_DEV_ID", mDNADevice);
                intent.setClass(DevMoreActivity.this, RMDemoActivity.class);
                startActivity(intent);
            }else{
                BLCommonUtils.toastShow(DevMoreActivity.this, "This device is not rm device!");
            }
        }else{
            BLCommonUtils.toastShow(DevMoreActivity.this, "Script not exit, please download first!");
        }
    }

    //网关设备控制
    public void gatewayControl(View view){
        if(scriptFileExist()){

            Intent intent = new Intent();
            intent.putExtra("INTENT_DEV_ID", mDNADevice);
            intent.setClass(DevMoreActivity.this, GateWayActivity.class);
            startActivity(intent);

        } else {
            BLCommonUtils.toastShow(DevMoreActivity.this, "Script not exit, please download first!");
        }
    }

    /**查询设备profile**/
    private BLDevProfileInfo queryDevProfile(){
        BLProfileStringResult devProfileResult = BLLet.Controller.queryProfileByPid(mDNADevice.getPid());
        if(devProfileResult != null && devProfileResult.getStatus() == BLControllerErrCode.SUCCESS){
            String profileStr = devProfileResult.getProfile();
            Log.i("dev profile", profileStr + "");

            return JSON.parseObject(profileStr, BLDevProfileInfo.class);
        }

        return null;
    }

    private boolean scriptFileExist(){
        /***获取产品脚本本地保存的路径***/
        String scriptFilePath = BLLet.Controller.queryScriptPath(mDNADevice.getPid());
        Log.e("FileExist" , scriptFilePath);
        File file = new File(scriptFilePath);
        return file.exists();
    }

    private boolean uiFileExit() {
        String uiFilePath = BLLet.Controller.queryUIPath(mDNADevice.getPid());
        Log.e("UIExit", uiFilePath);
        File file = new File(uiFilePath);
        return file.exists();
    }

    //服务器设备时间查询
    class QueryDeviceTimeTask extends AsyncTask<Void, Void, BLDeviceTimeResult>{
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DevMoreActivity.this);
            progressDialog.setMessage("Requesting...");
            progressDialog.show();
        }

        @Override
        protected BLDeviceTimeResult doInBackground(Void... params) {
            return BLLet.Controller.queryDeviceTime(mDNADevice.getDid());
        }

        @Override
        protected void onPostExecute(BLDeviceTimeResult result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            BLCommonUtils.toastShow(DevMoreActivity.this, "Device Time ：" + result.getTime());
        }
    }

    //脚本版本查询
    class QueryDeviceStatusTask extends AsyncTask<Void, Void, Integer>{
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DevMoreActivity.this);
            progressDialog.setMessage("Requesting...");
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            return BLLet.Controller.queryDeviceRemoteState(mDNADevice.getDid());
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            BLCommonUtils.toastShow(DevMoreActivity.this, "Device Status ：" + String.valueOf(result));
        }
    }

    //脚本版本查询
    class QueryScriptVersionTask extends AsyncTask<Void, Void, BLQueryResoureVersionResult>{
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DevMoreActivity.this);
            progressDialog.setMessage("Script Requesting...");
            progressDialog.show();
        }

        @Override
        protected BLQueryResoureVersionResult doInBackground(Void... params) {
            return BLLet.Controller.queryScriptVersion(mDNADevice.getPid());
        }

        @Override
        protected void onPostExecute(BLQueryResoureVersionResult result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if(result != null && result.getStatus() == BLControllerErrCode.SUCCESS){
                if (result.getVersions() != null) {
                    BLResourceVersion version = result.getVersions().get(0);
                    BLCommonUtils.toastShow(DevMoreActivity.this, "Script Version ：" + version.getVersion());
                } else {
                    BLCommonUtils.toastShow(DevMoreActivity.this, "Script Version is null");
                }
            }
        }
    }

    //UI版本查询
    class QueryUIVersionTask extends AsyncTask<Void, Void, BLQueryResoureVersionResult>{
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DevMoreActivity.this);
            progressDialog.setMessage("UI Requesting...");
            progressDialog.show();
        }

        @Override
        protected BLQueryResoureVersionResult doInBackground(Void... params) {
            return BLLet.Controller.queryUIVersion(mDNADevice.getPid());
        }

        @Override
        protected void onPostExecute(BLQueryResoureVersionResult result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if(result != null && result.getStatus() == BLControllerErrCode.SUCCESS){
                BLResourceVersion version = result.getVersions().get(0);
                BLCommonUtils.toastShow(DevMoreActivity.this, "UI Version ：" + version.getVersion());
            }
        }
    }

    //脚本下载
    class DownLoadScriptTask extends AsyncTask<Void, Void, BLDownloadScriptResult>{
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DevMoreActivity.this);
            progressDialog.setMessage("Script downloading...");
            progressDialog.show();
        }

        @Override
        protected BLDownloadScriptResult doInBackground(Void... params) {
            String pid = mDNADevice.getPid();
            return BLLet.Controller.downloadScript(pid);
        }

        @Override
        protected void onPostExecute(BLDownloadScriptResult result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if(result != null && result.getStatus() == BLControllerErrCode.SUCCESS){
                Log.e("DownLoad" , result.getSavePath());
                BLCommonUtils.toastShow(DevMoreActivity.this, "Script Path ：" + result.getSavePath());
            }
        }
    }

    //UI包下载
    class DownLoadUITask extends AsyncTask<Void, Void, BLDownloadUIResult>{
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DevMoreActivity.this);
            progressDialog.setMessage("UI downloading...");
            progressDialog.show();
        }

        @Override
        protected BLDownloadUIResult doInBackground(Void... params) {
            String pid = mDNADevice.getPid();
            return BLLet.Controller.downloadUI(pid);
        }

        @Override
        protected void onPostExecute(BLDownloadUIResult result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if(result != null && result.getStatus() == BLControllerErrCode.SUCCESS){
                Log.e("DownLoad" , result.getSavePath());
                BLCommonUtils.toastShow(DevMoreActivity.this, "UI Path ：" + result.getSavePath());
            }
        }
    }

}
