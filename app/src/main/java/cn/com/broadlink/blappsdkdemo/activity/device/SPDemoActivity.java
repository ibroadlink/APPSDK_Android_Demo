package cn.com.broadlink.blappsdkdemo.activity.device;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.common.PreferencesUtils;
import cn.com.broadlink.blappsdkdemo.data.BLControlActConstans;
import cn.com.broadlink.blappsdkdemo.data.BLDevProfileInfo;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;
import cn.com.broadlink.sdk.data.controller.BLStdData;
import cn.com.broadlink.sdk.param.controller.BLStdControlParam;
import cn.com.broadlink.sdk.result.controller.BLProfileStringResult;
import cn.com.broadlink.sdk.result.controller.BLStdControlResult;

/**
 * SP系列Demo
 * Created by YeJin on 2016/5/10.
 */
public class SPDemoActivity extends TitleActivity {

    private BLDNADevice mDNADevice;
    private ImageView mIvPwr;
    private TextView mTvPwr;
    private TextView mTvResult;
    private TextView mTvLight;
    private SeekBar mSbLight;
    private Switch mSwtMode;
    private boolean mPwrState = false;
    private BLDevProfileInfo mBlDevProfileInfo;
    private int mBrightness = 50;
    private boolean mIsLight = true;
    private final String PREFERENCE_SP_DEMO_GET_MODE = "sp_demo_get_mode";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_sp_demo);
        setBackWhiteVisible();
        setTitle("Sp Demo");
        
        mDNADevice = getIntent().getParcelableExtra(BLConstants.INTENT_PARCELABLE);

        findView();
        
        initView();
        
        setListener();
    }

    private boolean initView() {
      
        mSwtMode.setChecked(PreferencesUtils.getBoolean(mActivity, PREFERENCE_SP_DEMO_GET_MODE, true));
        
        if(mBlDevProfileInfo == null){
            BLProfileStringResult devProfileResult = BLLet.Controller.queryProfileByPid(mDNADevice.getPid());
            if(devProfileResult!=null && devProfileResult.succeed()){
                mBlDevProfileInfo = JSON.parseObject(devProfileResult.getProfile(), BLDevProfileInfo.class);
                final List<String> params = mBlDevProfileInfo.getSuids().get(0).getIntfsList();
                if(!params.contains("pwr")){
                    BLToastUtils.show("This device script has no 'pwr' interface!");
                    back(); 
                }
                if(!params.contains("brightness")){
                    mIsLight = false;
                    mTvLight.setVisibility(View.GONE);
                    mSbLight.setVisibility(View.GONE);
                }
                
            }else{
                BLToastUtils.show("Please confirm script had been downloaded!");
                back();
                return true;
            }
        }
        return false;
    }

    private void setListener() {
        mSwtMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferencesUtils.putBoolean(mActivity, PREFERENCE_SP_DEMO_GET_MODE, isChecked);
            }
        });
        
        mIvPwr.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                new ControlTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR, !mPwrState);
            }
        });
        
        mSbLight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
               
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                final int progress = seekBar.getProgress();
                new ControlLightTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR, progress);
            }
        });
    }

    private void findView() {
        mIvPwr = (ImageView) findViewById(R.id.iv_pwr);
        mTvPwr = (TextView) findViewById(R.id.tv_pwr);
        mTvLight = (TextView) findViewById(R.id.tv_light);
        mTvResult = (TextView) findViewById(R.id.tv_result);
        mSbLight = (SeekBar) findViewById(R.id.sb_light);
        mSwtMode = (Switch) findViewById(R.id.swt_mode);
    }
    
    private void refreshView(){
        mIvPwr.setImageResource(mPwrState ? R.drawable.sp_on : R.drawable.sp_off);
        mTvPwr.setText(mPwrState ? "ON": "OFF");
        mTvPwr.setTextColor(mPwrState ? Color.GREEN : Color.RED);
        mSbLight.setProgress(mBrightness);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startQuerySPStatusTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopQuerySPStatusTimer();
    }

    /**
     * 查询SP设备状态定时器
     **/
    private Timer mQuerySPStatusTimer;

    private void startQuerySPStatusTimer() {
        if (mQuerySPStatusTimer == null) {
            mQuerySPStatusTimer = new Timer();
            mQuerySPStatusTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    new QueryStateTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
                }
            }, 0, 2000);
        }
    }

    private void stopQuerySPStatusTimer() {
        if (mQuerySPStatusTimer != null) {
            mQuerySPStatusTimer.cancel();
            mQuerySPStatusTimer = null;
        }
    }
    
    class QueryStateTask extends AsyncTask<Void, Void, BLStdControlResult> {
        private boolean isGetModeOn = true;
        
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isGetModeOn = mSwtMode.isChecked();
            //showProgressDialog("Query...");
        }

        @Override
        protected BLStdControlResult doInBackground(Void... params) {

            BLStdControlParam intoStudyParam = new BLStdControlParam();
            intoStudyParam.setAct(BLControlActConstans.ACT_GET);
            if(isGetModeOn){
                intoStudyParam.getParams().add("pwr");
                if(mIsLight){
                    intoStudyParam.getParams().add("brightness");
                }
            }

            final String[] didOrSubDid = BLCommonUtils.parseDidOrSubDid(mDNADevice);
            return BLLet.Controller.dnaControl(didOrSubDid[0], didOrSubDid[1], intoStudyParam);
        }

        @Override
        protected void onPostExecute(BLStdControlResult result) {
            super.onPostExecute(result);
            //dismissProgressDialog();
            if (result != null && result.succeed()) {

                try {
                    final ArrayList<String> params = result.getData().getParams();
                    final int idxPwr = params.indexOf("pwr");
                    final int state = (int) result.getData().getVals().get(idxPwr).get(0).getVal();
                    mPwrState = state == 1;
    
                    if(mIsLight){
                        final int idxLight = params.indexOf("brightness");
                        mBrightness = (int) result.getData().getVals().get(idxLight).get(0).getVal();
                    }
                 
                    refreshView();
                    mTvResult.setText("");
                    return;
                } catch (Exception e) {
                    
                }
            }

            mTvResult.setText(JSON.toJSONString(result));
        }
    }


    class ControlTask extends AsyncTask<Boolean, Void, BLBaseResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Control...");
        }

        @Override
        protected BLBaseResult doInBackground(Boolean... params) {

            BLStdControlParam intoStudyParam = new BLStdControlParam();
            intoStudyParam.setAct(BLControlActConstans.ACT_SET);
            intoStudyParam.getParams().add("pwr");

            ArrayList<BLStdData.Value> irVals = new ArrayList<>();
            BLStdData.Value val = new BLStdData.Value();
            val.setVal(params[0] ? 1 : 0);
            irVals.add(val);
            
            intoStudyParam.getVals().add(irVals);
            
            final String[] didOrSubDid = BLCommonUtils.parseDidOrSubDid(mDNADevice);
            return BLLet.Controller.dnaControl(didOrSubDid[0], didOrSubDid[1], intoStudyParam);
        }

        @Override
        protected void onPostExecute(BLBaseResult result) {
            super.onPostExecute(result);
            dismissProgressDialog();
            if (result != null && result.succeed()) {
                mPwrState = !mPwrState;
                refreshView();
            }else{
                BLCommonUtils.toastErr(result);
            }

        }
    }

    class ControlLightTask extends AsyncTask<Integer, Void, BLBaseResult> {
        private int lightNess = 0;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Control...");
        }

        @Override
        protected BLBaseResult doInBackground(Integer... params) {
            lightNess = params[0];
            
            BLStdControlParam intoStudyParam = new BLStdControlParam();
            intoStudyParam.setAct(BLControlActConstans.ACT_SET);
            intoStudyParam.getParams().add("brightness");

            ArrayList<BLStdData.Value> irVals = new ArrayList<>();
            BLStdData.Value val = new BLStdData.Value();
            val.setVal(lightNess);
            irVals.add(val);
            
            intoStudyParam.getVals().add(irVals);

            final String[] didOrSubDid = BLCommonUtils.parseDidOrSubDid(mDNADevice);
            return BLLet.Controller.dnaControl(didOrSubDid[0], didOrSubDid[1], intoStudyParam);
        }

        @Override
        protected void onPostExecute(BLBaseResult result) {
            super.onPostExecute(result);
            dismissProgressDialog();
            if (result != null && result.succeed()) {
                mBrightness = lightNess;
                refreshView();
            }else{
                BLCommonUtils.toastErr(result);
            }

        }
    }
}
