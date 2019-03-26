package cn.com.broadlink.blappsdkdemo.activity.device;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.result.controller.BLAPConfigResult;

public class DevAPManualConfigActivity extends TitleActivity {

    private EditText mEtSsid;
    private EditText mEtPwd;
    private TextView mTvTip;
    private Spinner mSpType;
    private Button mBtCommit;
    private EditText mTvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_apmanual_config);
        setTitle("Ap Manual Config");
        setBackWhiteVisible();

        findView();

        initView();
    }

    private void findView() {
        mEtSsid = (EditText) findViewById(R.id.et_ssid);
        mEtPwd = (EditText) findViewById(R.id.et_pwd);
        mTvTip = (TextView) findViewById(R.id.tv_tip);
        mSpType = (Spinner) findViewById(R.id.sp_type);
        mBtCommit = (Button) findViewById(R.id.bt_commit);
        mTvResult = (EditText) findViewById(R.id.et_result);
    }

    private void initView() {
        String[] typeStringArray = {"NONE", "WEP", "WPA", "WPA2", "WPA/WPA2 MIXED"};
        ArrayAdapter spinnerAdapter = new ArrayAdapter(DevAPManualConfigActivity.this, R.layout.support_simple_spinner_dropdown_item, typeStringArray);
        mSpType.setAdapter(spinnerAdapter);
        mSpType.setSelection(4);

        mBtCommit.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                if(mEtSsid.getText()!=null && !TextUtils.isEmpty(mEtSsid.getText())){
                    String ssid = mEtSsid.getText().toString();
                    String pwd = null;
                    if(mEtPwd.getText()!=null && !TextUtils.isEmpty(mEtPwd.getText())){
                        pwd = mEtPwd.getText().toString();
                    }
                    int type = mSpType.getSelectedItemPosition();

                    new APConfigTask(ssid, pwd, type).execute();
                }else{
                    BLToastUtils.show("Please input SSID first!");
                    mEtSsid.requestFocus();
                }
            }
        });
    }


    //AP 配置
    class APConfigTask extends AsyncTask<Void, Void, BLAPConfigResult> {
        String ssid, pwd;
        int type;

        public APConfigTask(String ssid, String pwd, int type) {
            this.ssid = ssid;
            this.pwd = pwd;
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Config...");
        }

        @Override
        protected BLAPConfigResult doInBackground(Void... params) {
            return BLLet.Controller.deviceAPConfig(ssid, pwd, type, null);
        }

        @Override
        protected void onPostExecute(BLAPConfigResult result) {
            super.onPostExecute(result);
            dismissProgressDialog();
            mTvResult.setText(JSON.toJSONString(result, true));
        }

    }
    
}
