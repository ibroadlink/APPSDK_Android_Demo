package cn.com.broadlink.blappsdkdemo.activity.device;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cn.com.broadlink.base.BLCommonTools;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;
import cn.com.broadlink.sdk.result.controller.BLPassthroughResult;

public class DevDataPassThroughActivity extends TitleActivity {

    private Button mBtClear;
    private EditText mEtInput;
    private EditText mEtOutput;
    private Button mBtCommit;
    private BLDNADevice mDNADevice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_data_passthrough);
        setBackWhiteVisible();
        setTitle("Pass Through Control");
        initView();

        mDNADevice = getIntent().getParcelableExtra(BLConstants.INTENT_PARCELABLE);

        setListener();
    }

    private void setListener() {

        mBtCommit.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                if(TextUtils.isEmpty(mEtInput.getText())){
                    BLToastUtils.show("Please input some command first!");
                    mEtInput.requestFocus();
                }else{
                    new PassThoughTask().execute(mEtInput.getText().toString().replace(" ",""));
                }
            }
        });
        
        setRightButtonOnClickListener("Clear", getResources().getColor(R.color.bl_yellow_main_color), new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                mEtInput.setText(null);
            }
        });
    }

    private void initView() {
        mBtClear = (Button) findViewById(R.id.bt_clear);
        mEtInput = (EditText) findViewById(R.id.et_input);
        mEtOutput = (EditText) findViewById(R.id.et_output);
        mBtCommit = (Button) findViewById(R.id.bt_commit);
    }

    class PassThoughTask extends AsyncTask<String, Void, BLPassthroughResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Requesting...");
        }

        @Override
        protected BLPassthroughResult doInBackground(String... params) {
            byte[] data = BLCommonTools.parseStringToByte(params[0]);
            return BLLet.Controller.dnaPassthrough(mDNADevice.getDid(), null, data);
        }

        @Override
        protected void onPostExecute(BLPassthroughResult result) {
            super.onPostExecute(result);
            dismissProgressDialog();
            if(result==null || !result.succeed()){
                BLCommonUtils.toastErr(result);                
            }else{
                mEtOutput.setText(BLCommonTools.bytes2HexString(result.getData()));
            }
        }
    }
}
