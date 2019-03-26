package cn.com.broadlink.blappsdkdemo.activity.irCode;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.ircode.BLIRCode;
import cn.com.broadlink.ircode.result.BLDownLoadIRCodeResult;
import cn.com.broadlink.ircode.result.BLIrdaConProductResult;
import cn.com.broadlink.ircode.result.BLResponseResult;
import cn.com.broadlink.sdk.BLLet;

public class IRCodeAcPairActivity extends TitleActivity {


    private EditText mEtInput;
    private Button mBtOnKeyRec;
    private Button mBtDownloadScript;
    private Button mBtGetScriptInfo;
    private Button mBtGetIrCode;
    private EditText mTvResult;
    private String mTextIrCode = 
            "2600ca008d950c3b0f1410380e3a0d160e160d3b0d150e150e3910150d160d3a0f36101411380d150f3a0e390d3910370f150f38103a0d3a0e1211140f1411121038101310150f3710380e390e150f160d160e1410140f131113101310380e3b0f351137123611ad8e9210370f1511370e390f140f1410380f1311130f39101211130f390f380f150f390f1310380f3810380f380f141038103710380f1411121014101310380f14101310380f3810381013101311121014101211131014101310370f3910361138103710000d05";
    private String mDownloadUrl;
    private String mScriptRandkey;
    private String mScriptName;
    private String mSavePath;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Ac Pair");
        setContentView(R.layout.activity_ir_ac_pair);
        setBackWhiteVisible();

        findView();

        initView();
        
        setListener();
    }

    private void findView() {
        mEtInput = (EditText) findViewById(R.id.et_input);
        mBtOnKeyRec = (Button) findViewById(R.id.bt_on_key_rec);
        mBtDownloadScript = (Button) findViewById(R.id.bt_download_script);
        mBtGetScriptInfo = (Button) findViewById(R.id.bt_get_script_info);
        mBtGetIrCode = (Button) findViewById(R.id.bt_get_ir_code);
        mTvResult = (EditText) findViewById(R.id.et_result);
    }

    private void initView() {
        mEtInput.setText(mTextIrCode);
    }

    private void setListener() {
        mBtOnKeyRec.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                if(TextUtils.isEmpty(mEtInput.getText())){
                    BLToastUtils.show("Please input ircode first!");
                    mEtInput.requestFocus();
                    return;
                }
                new RecognizeTask().execute(mEtInput.getText().toString());
            }
        });
        
        mBtDownloadScript.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                if (TextUtils.isEmpty(mDownloadUrl) || TextUtils.isEmpty(mScriptRandkey) || TextUtils.isEmpty(mScriptName) || TextUtils.isEmpty(mSavePath)) {
                    BLToastUtils.show("Please recognize first!");
                    return;
                }
                new DownLoadScriptTask().execute(mDownloadUrl, mSavePath, mScriptRandkey);
            }
        });

        mBtGetScriptInfo.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                getScriptContent();
            }
        });

        mBtGetIrCode.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                if (!getScriptContent()) {
                    return;
                }
                BLCommonUtils.toActivity(mActivity, IRCodeAcPanelActivity.class, mSavePath);
            }
        });

    }

    private boolean  getScriptContent() {
        if (mSavePath == null) {
            BLCommonUtils.toastShow(mActivity, "Please download script first!");
            return false;
        }

        File file = new File(mSavePath);
        if (!file.exists()) {
            BLCommonUtils.toastShow(mActivity, "Please download script first!");
            return false;
        } else {
            BLIrdaConProductResult result = BLIRCode.queryACIRCodeInfomation(mSavePath);
            showResult(result);
            return (result != null && result.succeed());
        }
    }


    private void showResult(Object result) {
        if(result == null){
            mTvResult.setText("Return null");
        }else{
            if(result instanceof String){
                mTvResult.setText((String)result);
            }else{
                mTvResult.setText(JSON.toJSONString(result, true));
            }
        }
    }


    class RecognizeTask extends AsyncTask<String, Void, BLResponseResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Recognizing...");
        }

        @Override
        protected BLResponseResult doInBackground(String... strings) {
            return BLIRCode.recognizeACIRCode(strings[0]);
        }

        @Override
        protected void onPostExecute(BLResponseResult blBaseBodyResult) {
            super.onPostExecute(blBaseBodyResult);
            dismissProgressDialog();
            showResult(blBaseBodyResult);
            
            try {
                JSONObject jResult = new JSONObject(blBaseBodyResult.getResponseBody());
                JSONArray infos = jResult.optJSONArray("downloadinfo");

                JSONObject info = infos.getJSONObject(0);
                mScriptName = info.optString("name", null);
                mDownloadUrl = info.optString("downloadurl", null);
                mScriptRandkey = info.optString("fixkey", null);
                mSavePath = BLLet.Controller.queryIRCodePath() + File.separator + mScriptName;

            } catch (Exception e) {

            }
        }
    }

    class DownLoadScriptTask extends AsyncTask<String, Void, BLDownLoadIRCodeResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Downloading...");
        }

        @Override
        protected BLDownLoadIRCodeResult doInBackground(String... strings) {
            return BLIRCode.downloadIRCodeScript(strings[0], strings[1], strings[2]);
        }

        @Override
        protected void onPostExecute(BLDownLoadIRCodeResult blDownloadScriptResult) {
            super.onPostExecute(blDownloadScriptResult);
            dismissProgressDialog();
            showResult(blDownloadScriptResult);
        }
    }
}
