package cn.com.broadlink.blappsdkdemo.activity.account;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import cn.com.broadlink.base.BLBaseHttpAccessor;
import cn.com.broadlink.base.BLTrustManagerV2;
import cn.com.broadlink.base.fastjson.BLJSON;
import cn.com.broadlink.base.fastjson.JSONObject;
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.activity.h5.CommonH5Activity;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.common.PreferencesUtils;
import cn.com.broadlink.blappsdkdemo.view.BLProgressDialog;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;

/**
 * Desc
 *
 * @author JiangYaqiang
 * 2019/12/5 10:10
 */
public class TestAgreementSignedActivity extends TitleActivity {

    private EditText mEtPlatformId;
    private EditText mEtResult;
    private Button mBtScanStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_agreement_signed);
        setBackWhiteVisible();
        setTitle("Agreement Signed");
        initView();
        setListener();
    }

    private void initView() {
        mEtPlatformId = (EditText) findViewById(R.id.et_platform_id);
        mEtResult = (EditText) findViewById(R.id.et_result);
        mBtScanStart = (Button) findViewById(R.id.bt_scan_start);

        String platformId = PreferencesUtils.getString(mApplication, "PlatformId", null);
        mEtPlatformId.setText(platformId);
    }

    private void setListener() {
        mBtScanStart.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                gotoH5Page();
            }
        });
    }

    private void showResult(final EditText view, final Object result, final boolean append) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("\nHH:mm:ss ");
                final String date = sdf.format(new Date());

                if (append) {
                    view.append(date);
                } else {
                    view.setText(date);
                }

                if (result == null) {
                    view.append("Return null");
                } else {
                    if (result instanceof String) {
                        view.append((String) result);
                    } else {
                        view.append(JSON.toJSONString(result, true));
                    }
                }
            }
        });
    }
    
    private void gotoH5Page(){
        final Editable text = mEtPlatformId.getText();
        if(TextUtils.isEmpty(text)){
            BLToastUtils.show("Platform id is null.");
            return;
        }

        PreferencesUtils.putString(mApplication, "PlatformId", text.toString());
        new GetCodeTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
    }

    //获取手机验证码
    private class GetCodeTask extends AsyncTask<String, Void, String> {
        private BLProgressDialog blProgressDialog;
        private String platformId = PreferencesUtils.getString(mApplication, "PlatformId", null);
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            blProgressDialog = BLProgressDialog.createDialog(mActivity, "Sending...");
            blProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
           
            if(platformId != null){
                final HashMap<String, String> headerMap = new HashMap<>();
                headerMap.put("Content-Type", "application/json");
                headerMap.put("logintype", "session");

                final JSONObject body = new JSONObject();
                body.put("userid", BLApplication.mBLUserInfoUnits.getUserid());
                body.put("loginsession", BLApplication.mBLUserInfoUnits.getLoginsession());
                

               String url = "https://app-service-chn-ee08f451.ibroadlink.com/oauth/v2/login/info?response_type=code&client_id=da0511c2ac6114acd8bd12ef64e818dc&redirect_uri=https://pitangui.amazon.com/api/skill/link/M1TJ2VD8XAW3VZ";
               return  BLBaseHttpAccessor.post(url, headerMap, body.toString().getBytes(), 10 * 1000, BLTrustManagerV2.getInstance());
            }
            return "PlatformId is null";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            blProgressDialog.dismiss();
            showResult(mEtResult, result, false);

            final JSONObject resultJson = BLJSON.parseObject(result);
            if(resultJson != null && resultJson.containsKey("code")){
                String url = String.format("https://app-service-chn-8616c306.ibroadlink.com/appfront/v1/webui/giot/#/?code=%s&plateformid=%s", resultJson.getString("code"), platformId);
                final Intent intent = new Intent(mActivity, CommonH5Activity.class);
                intent.putExtra(BLConstants.INTENT_TITLE, "Agreement Signed");
                intent.putExtra(BLConstants.INTENT_URL, url);
                startActivity(intent);
            }
        }
    }
}
