package cn.com.broadlink.blappsdkdemo.activity.IRCode;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;

import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.result.controller.BLBaseBodyResult;
import cn.com.broadlink.sdk.result.controller.BLDownloadScriptResult;
import cn.com.broadlink.sdk.result.controller.BLIRCodeInfoResult;


public class IRCodeRecognizeActivity extends Activity {

    private TextView mIrCodeView;

    private String mDownloadUrl;
    private String mScriptRandkey;
    private String mScriptName;
    private String mSavePath;

    private String mScriptInfo = null;

    //美的空调(Media AC)
    private int mDeviceType = BLConstants.BL_IRCODE_DEVICE_AC;
    private String ircodeHexString = "2600ca008d950c3b0f1410380e3a0d160e160d3b0d150e150e3910150d160d3a0f36101411380d150f3a0e390d3910370f150f38103a0d3a0e1211140f1411121038101310150f3710380e390e150f160d160e1410140f131113101310380e3b0f351137123611ad8e9210370f1511370e390f140f1410380f1311130f39101211130f390f380f150f390f1310380f3810380f380f141038103710380f1411121014101310380f14101310380f3810381013101311121014101211131014101310370f3910361138103710000d05";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ircode_recognize);

        mIrCodeView = (TextView) findViewById(R.id.ircode_data_view);
        mIrCodeView.setText(ircodeHexString);

        Intent intent = getIntent();
        mDownloadUrl = intent.getStringExtra("DownloadURL");

        if (mDownloadUrl != null) {
            mScriptRandkey = intent.getStringExtra("ScriptRandkey");
            mScriptName = intent.getStringExtra("ScriptName");
            mDeviceType = intent.getIntExtra("DeviceType", BLConstants.BL_IRCODE_DEVICE_AC);
            mSavePath = BLLet.Controller.queryIRCodePath() + File.separator + mScriptName;

            mIrCodeView.setText("");
            Button recognizeBtn = (Button) findViewById(R.id.recognize_btn);
            recognizeBtn.setVisibility(View.GONE);
        }
    }

    public void recognizeIRCode(View view){
        recognizeIRCode(ircodeHexString);
    }

    public void downloadIRCode(View view){
        if (mDownloadUrl == null) {
            BLCommonUtils.toastShow(IRCodeRecognizeActivity.this, "Please get download url first!");
        } else {
            new DownLoadIRCodeScriptTask().execute(mDownloadUrl, mSavePath, mScriptRandkey);
        }
    }

    public void getIRCodeScriptBaseInfo(View view) {
        queryIRCodeBaseInfo(mSavePath, mDeviceType);
    }

    public void getIRCodeData(View view) {
        queryIRCodeData(mSavePath, mDeviceType);
    }

    private void recognizeIRCode(String ircode) {
        new IRCodeRecognizeActivity.RecongnizeIRCodeTask().execute(ircode);
    }

    private boolean queryIRCodeBaseInfo(String path, int ircode_dev_type) {
        if (mSavePath == null) {
            BLCommonUtils.toastShow(IRCodeRecognizeActivity.this, "Please recognizeIRCode script first!");
            return false;
        }

        File file = new File(mSavePath);
        if (!file.exists()) {
            BLCommonUtils.toastShow(IRCodeRecognizeActivity.this, "Please download script first!");
        } else {
            BLIRCodeInfoResult result = BLLet.IRCode.queryIRCodeInfomation(path, ircode_dev_type);
            if (result.succeed()) {
                mScriptInfo = result.getInfomation();
                mIrCodeView.setText(mScriptInfo);
                return true;
            } else {
                BLCommonUtils.toastShow(IRCodeRecognizeActivity.this, "getIRCodeScriptBaseInfo failed! Error: " + String.valueOf(result.getStatus()) + " Msg:" + result.getMsg());
            }
        }
        return false;
    }

    private void queryIRCodeData(String path, int ircode_dev_type) {
        if (mScriptInfo == null) {
            if (!queryIRCodeBaseInfo(path, ircode_dev_type))
                return;
        }

        Intent intent = new Intent();
        if (ircode_dev_type == BLConstants.BL_IRCODE_DEVICE_AC) {
            intent.putExtra("ScriptPath", mSavePath);
            intent.putExtra("ScriptInfo", mScriptInfo);
            intent.setClass(IRCodeRecognizeActivity.this, IRCodeACOperateActivity.class);
        } else {
            intent.putExtra("ScriptPath", mSavePath);
            intent.putExtra("ScriptInfo", mScriptInfo);
            intent.putExtra("DeviceType", mDeviceType);
            intent.setClass(IRCodeRecognizeActivity.this, IRCodeTCOperateActivity.class);
        }

        startActivity(intent);
    }

    class RecongnizeIRCodeTask extends AsyncTask<String, Void, BLBaseBodyResult> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(IRCodeRecognizeActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
        }

        @Override
        protected BLBaseBodyResult doInBackground(String... strings) {
            return BLLet.IRCode.recognizeIRCode(strings[0]);
        }

        @Override
        protected void onPostExecute(BLBaseBodyResult blBaseBodyResult) {
            super.onPostExecute(blBaseBodyResult);
            progressDialog.dismiss();

            if (blBaseBodyResult.succeed()) {
                BLCommonUtils.toastShow(IRCodeRecognizeActivity.this, blBaseBodyResult.getResponseBody());
                Log.d(BLConstants.BROADLINK_LOG_TAG, blBaseBodyResult.getResponseBody());
                try {
                    JSONObject jResult = new JSONObject(blBaseBodyResult.getResponseBody());
                    JSONArray infos = jResult.optJSONArray("downloadinfo");

                    JSONObject info = infos.getJSONObject(0);
                    mScriptName = info.optString("name", null);
                    mDownloadUrl = info.optString("downloadurl", null);
                    mScriptRandkey = info.optString("randkey", null);

                    mSavePath = BLLet.Controller.queryIRCodePath() + File.separator + mScriptName;
                    mIrCodeView.setText("Name:" + mScriptName + "\nDownload:" + mDownloadUrl + "\nRandkey:" + mScriptRandkey);

                } catch (Exception e) {

                }
            } else {
                BLCommonUtils.toastShow(IRCodeRecognizeActivity.this, "RecongnizeIRCode failed! Error: " + String.valueOf(blBaseBodyResult.getStatus()));
            }
        }
    }

    class DownLoadIRCodeScriptTask extends AsyncTask<String, Void, BLDownloadScriptResult> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(IRCodeRecognizeActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
        }

        @Override
        protected BLDownloadScriptResult doInBackground(String... strings) {
            return BLLet.IRCode.downloadIRCodeScript(strings[0], strings[1], strings[2]);
        }

        @Override
        protected void onPostExecute(BLDownloadScriptResult blDownloadScriptResult) {
            super.onPostExecute(blDownloadScriptResult);
            progressDialog.dismiss();

            if (blDownloadScriptResult.succeed()) {
                mIrCodeView.setText("Download Success.\nSave Path:" + blDownloadScriptResult.getSavePath());
            } else {
                BLCommonUtils.toastShow(IRCodeRecognizeActivity.this, "RecongnizeIRCode failed! Error: " + String.valueOf(blDownloadScriptResult.getStatus()));
            }
        }
    }
}
