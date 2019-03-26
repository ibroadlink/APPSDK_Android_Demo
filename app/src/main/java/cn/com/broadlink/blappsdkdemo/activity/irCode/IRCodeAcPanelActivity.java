package cn.com.broadlink.blappsdkdemo.activity.irCode;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.util.ArrayList;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.device.DevMyDevListActivity;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.BLListAlert;
import cn.com.broadlink.blirdaconlib.BLIrdaConState;
import cn.com.broadlink.ircode.BLIRCode;
import cn.com.broadlink.ircode.result.BLIrdaConDataResult;
import cn.com.broadlink.ircode.result.BLIrdaConProductResult;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;
import cn.com.broadlink.sdk.data.controller.BLRMCloudAcConstants;
import cn.com.broadlink.sdk.data.controller.BLStdData;
import cn.com.broadlink.sdk.param.controller.BLStdControlParam;
import cn.com.broadlink.sdk.result.controller.BLStdControlResult;

public class IRCodeAcPanelActivity extends TitleActivity implements View.OnClickListener {


    private LinearLayout mLlPwr;
    private TextView mTvPwr;
    private LinearLayout mLlMode;
    private TextView mTvMode;
    private LinearLayout mLlWind;
    private TextView mTvWind;
    private LinearLayout mLlDirect;
    private TextView mTvDirect;
    private EditText mEtTemp;
    private Button mBtGenIrCode;
    private Button mBtSendIrCode;
    private EditText mTvResult;
    private TextView mTvDev;
    private String mSavePath;
    private BLIrdaConProductResult mScriptContent;
    private BLDNADevice mDev = null;
    private String mIrCode;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Ac Panel");
        setContentView(R.layout.activity_ir_ac_panel);
        setBackWhiteVisible();
        
        mSavePath = getIntent().getStringExtra(BLConstants.INTENT_VALUE);
        
        findView();

        setListener();

        initView();
    }

    private void initView() {
        if(mSavePath == null){
            BLToastUtils.show("Script path null!");
            back();
        }
        if(!getScriptContent()){
            BLToastUtils.show("Script content read fail!");
            back();
        }
    }

    private void findView() {

        mLlPwr = (LinearLayout) findViewById(R.id.ll_pwr);
        mTvPwr = (TextView) findViewById(R.id.tv_pwr);
        mLlMode = (LinearLayout) findViewById(R.id.ll_mode);
        mTvMode = (TextView) findViewById(R.id.tv_mode);
        mLlWind = (LinearLayout) findViewById(R.id.ll_wind);
        mTvWind = (TextView) findViewById(R.id.tv_wind);
        mLlDirect = (LinearLayout) findViewById(R.id.ll_direct);
        mTvDirect = (TextView) findViewById(R.id.tv_direct);
        mEtTemp = (EditText) findViewById(R.id.et_temp);
        mBtGenIrCode = (Button) findViewById(R.id.bt_gen_ir);
        mBtSendIrCode = (Button) findViewById(R.id.bt_send_ir);
        mTvResult = (EditText) findViewById(R.id.et_result);
        mTvDev = (TextView) findViewById(R.id.tv_dev);
    }

    private void setListener() {
        mLlPwr.setOnClickListener(this);
        mLlMode.setOnClickListener(this);
        mLlWind.setOnClickListener(this);
        mLlDirect.setOnClickListener(this);
        mLlPwr.setOnClickListener(this);
        mBtGenIrCode.setOnClickListener(this);
        mBtSendIrCode.setOnClickListener(this);
    }
    
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_pwr:
                showListAlert("Select Power", mScriptContent.getIrdaInfo().status, mScriptContent.getIrdaInfo().status_count, mTvPwr);
                break;
                
            case R.id.ll_mode:
                showListAlert("Select Mode", mScriptContent.getIrdaInfo().mode, mScriptContent.getIrdaInfo().mode_count, mTvMode);
                break;
                
            case R.id.ll_wind:
                showListAlert("Select Fan Speed", mScriptContent.getIrdaInfo().windspeed, mScriptContent.getIrdaInfo().windspeed_count, mTvWind);
                break;
                
            case R.id.ll_direct:
                showListAlert("Select Fan Direction", mScriptContent.getIrdaInfo().windirect, mScriptContent.getIrdaInfo().windirect_count, mTvDirect);
                break;
                
            case R.id.bt_gen_ir:
                getACIRCodeData();
                break;
                
            case R.id.bt_send_ir:
                if(mDev == null){
                    BLAlert.showDialog(mActivity, "Go to select a Rm device?", new BLAlert.DialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {
                            BLCommonUtils.toActivityForResult(mActivity, DevMyDevListActivity.class, 101, IRCodeAcPanelActivity.class.getName());
                        }

                        @Override
                        public void onNegativeClick() {

                        }
                    });
                }else{
                    if(TextUtils.isEmpty(mIrCode)){
                        getACIRCodeData();
                    }
                    if(TextUtils.isEmpty(mIrCode)){
                        BLToastUtils.show("Please generate Ir code first!");
                        return;
                    }
                    new DnaControlTask().execute();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101 && resultCode==RESULT_OK){
            mDev = data.getParcelableExtra(BLConstants.INTENT_DEVICE);
            mTvDev.setVisibility(View.VISIBLE);
            mTvDev.setText("Rm Device Info:\n" + JSON.toJSONString(mDev));
        }
    }

    private void showListAlert(String title, int[] params, int count, final TextView view) {
        if(params==null || params.length==0){
            BLToastUtils.show("This Ac Script has no this param!");
            return;
        }
        
        final String[] modeStrs = new String[count];
        for (int i=0; i<count; i++){
            modeStrs[i] = String.valueOf(params[i]);
        }

        BLListAlert.showAlert(mActivity, title, modeStrs, new BLListAlert.OnItemClickLister() {
            @Override
            public void onClick(int whichButton) {
                view.setText(modeStrs[whichButton]);
            }
        });
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
           mScriptContent = BLIRCode.queryACIRCodeInfomation(mSavePath);
            return (mScriptContent != null && mScriptContent.succeed());
        }
    }

    public void getACIRCodeData() {
        if(TextUtils.isEmpty(mEtTemp.getText())){
            BLCommonUtils.toastShow(mActivity, "Please input temp!");
            mEtTemp.requestFocus();
        }
        
        String statusInput = mTvPwr.getText().toString();
        String dirInput = mTvDirect.getText().toString();
        String modeInput = mTvMode.getText().toString();
        String speedInput = mTvWind.getText().toString();
        String tempInput = mEtTemp.getText().toString();

        BLIrdaConState params = new BLIrdaConState();
        params.status = Integer.parseInt(statusInput);
        params.mode = Integer.parseInt(modeInput);
        params.wind_direct = Integer.parseInt(dirInput);
        params.wind_speed = Integer.parseInt(speedInput);
        params.temperature = Integer.parseInt(tempInput);

        BLIrdaConDataResult result = BLIRCode.queryACIRCodeData(mSavePath, BLRMCloudAcConstants.IRDA_KEY_TEMP_ADD, params);
        if (result != null && result.succeed() && result.getIrcode() != null) {
            mIrCode = result.getIrcode();
            showResult(mIrCode);
        }else{
            showResult(result);
        }
    }

    class DnaControlTask extends AsyncTask<Void, Void, BLStdControlResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Controlling...");
        }

        @Override
        protected BLStdControlResult doInBackground(Void... params) {

            ArrayList<BLStdData.Value> dnaVals = new ArrayList<>();
            final ArrayList<String> dnaParams = new ArrayList<>();

                dnaParams.add("irda");
                BLStdData.Value value = new BLStdData.Value();
                value.setVal(mIrCode);
                dnaVals.add(value);

            BLStdControlParam stdControlParam = new BLStdControlParam();
            stdControlParam.setAct("set");
            stdControlParam.getParams().addAll(dnaParams);
            stdControlParam.getVals().add(dnaVals);

            return BLLet.Controller.dnaControl(mDev.getDid(), null, stdControlParam);
        }

        @Override
        protected void onPostExecute(BLStdControlResult result) {
            super.onPostExecute(result);
            dismissProgressDialog();
            showResult(result);
        }
    }
}
