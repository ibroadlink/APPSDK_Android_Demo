package cn.com.broadlink.blappsdkdemo.activity.irCode;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import cn.com.broadlink.base.BLApiUrls;
import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.BLListAlert;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.ircode.BLIRCode;
import cn.com.broadlink.ircode.result.BLResponseResult;

public class IRCodeMainActivity extends TitleActivity {

    private Button mBtAir;
    private Button mBtTv;
    private Button mBtBox;
    private Button mBtLimit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ir_code_main);
        setBackWhiteVisible();
        setTitle("IR Code Main");
        
        findView();
        
        setListener();
    }

    private void findView() {
        mBtAir = (Button) findViewById(R.id.bt_air);
        mBtTv = (Button) findViewById(R.id.bt_tv);
        mBtBox = (Button) findViewById(R.id.bt_box);
        mBtLimit = (Button) findViewById(R.id.bt_limit);
    }
    private void setListener() {

        mBtAir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BLListAlert.showAlert(mActivity, null, new String[]{"One Key Recognize", "By Brand"}, new BLListAlert.OnItemClickLister() {
                    @Override
                    public void onClick(int whichButton) {
                        switch (whichButton){
                            case 0:
                                BLCommonUtils.toActivity(mActivity, IRCodeAcPairActivity.class);
                                break;
                                
                            case 1:
                                BLCommonUtils.toActivity(mActivity, IRCodeBrandListActivity.class,  BLConstants.BL_IRCODE_DEVICE_AC);
                                break;

                        }
                    }
                });
            }
        });

        mBtTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNextPage(BLConstants.BL_IRCODE_DEVICE_TV);
            }
        });

        mBtBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNextPage(BLConstants.BL_IRCODE_DEVICE_TV_BOX);
            }
        });

        mBtLimit.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                new RequestCaptchaTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
            }
        });

    }

    private void gotoNextPage(final int PanelTypeId){
        BLListAlert.showAlert(mActivity, null, new String[]{"Choose Brand Model", "Match Tree"}, new BLListAlert.OnItemClickLister() {
            @Override
            public void onClick(int whichButton) {
                final Intent intent = new Intent(mActivity, IRCodeBrandListActivity.class);
                intent.putExtra(BLConstants.INTENT_TYPE, whichButton==1);
                intent.putExtra(BLConstants.INTENT_VALUE, PanelTypeId);
                startActivity(intent);
            }
        });
    }

    /**
     * 请求验证码
     */
    class RequestCaptchaTask extends AsyncTask<String, Void, BLResponseResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Loading...");
        }

        @Override
        protected BLResponseResult doInBackground(String... strings) {
            return BLIRCode.requestCaptchaId("");
        }

        @Override
        protected void onPostExecute(BLResponseResult result) {
            super.onPostExecute(result);
            dismissProgressDialog();
            if(result!= null && result.succeed() && result.getResponseBody()!=null){
                try {
                    final String responseBody = result.getResponseBody();
                    final JSONObject jsonObject = JSON.parseObject(responseBody);
                    final String captchaid = jsonObject.getString("captchaid");

                    final LinearLayout dialog = (LinearLayout) getLayoutInflater().inflate(R.layout.view_dialog_captcha, null);
                    final ImageView ivIcon = dialog.findViewById(R.id.iv_icon);
                    final EditText etVal = dialog.findViewById(R.id.et_val);
                    Glide.with(mActivity).load(String.format("%s/%s.png", BLApiUrls.IRCode.URL_IRCODE_CAPTCHA_V3(), captchaid)).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(ivIcon);
                    
                    BLAlert.showCustomViewDilog(mActivity, dialog, "Ok", "Cancel", new BLAlert.DialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {
                            if(TextUtils.isEmpty(etVal.getText())){
                                BLToastUtils.show("Should not be null");
                            }else{
                                new freeLimitTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR, captchaid, etVal.getText().toString());
                            }
                        }

                        @Override
                        public void onNegativeClick() {

                        }
                    });
                    
                   
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }else{
                BLCommonUtils.toastErr(result);
            }
        }
    }


    /**
     * 通过验证码增加使用次数 
     */
    class freeLimitTask extends AsyncTask<String, Void, BLBaseResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Loading...");
        }

        @Override
        protected BLBaseResult doInBackground(String... strings) {
            
            
            return BLIRCode.freeLimit(strings[0], strings[1]);
        }

        @Override
        protected void onPostExecute(BLBaseResult result) {
            super.onPostExecute(result);
            dismissProgressDialog();
            BLCommonUtils.toastErr(result);
        }
    }



}
