package cn.com.broadlink.blappsdkdemo.activity.device;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.data.BLControlActConstans;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;
import cn.com.broadlink.sdk.data.controller.BLStdData;
import cn.com.broadlink.sdk.param.controller.BLStdControlParam;
import cn.com.broadlink.sdk.result.controller.BLStdControlResult;

/**
 * SP系列Demo
 * Created by YeJin on 2016/5/10.
 */
public class SPDemoActivity extends TitleActivity {

    private BLDNADevice mDNADevice;
    private ImageView mIvPwr;
    private TextView mTvPwr;
    private boolean mPwrState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_sp_demo);
        setBackWhiteVisible();
        setTitle("Sp Demo");
        
        mDNADevice = getIntent().getParcelableExtra(BLConstants.INTENT_PARCELABLE);
        
        findView();

        setListener();
    }

    private void setListener() {
        mIvPwr.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                new ControlTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR, !mPwrState);
            }
        });
    }

    private void findView() {
        mIvPwr = (ImageView) findViewById(R.id.iv_pwr);
        mTvPwr = (TextView) findViewById(R.id.tv_pwr);
    }
    
    private void refreshView(){
        mIvPwr.setImageResource(mPwrState ? R.drawable.sp_on : R.drawable.sp_off);
        mTvPwr.setText(mPwrState ? "ON": "OFF");
        mTvPwr.setTextColor(mPwrState ? Color.GREEN : Color.RED);
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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showProgressDialog("Query...");
        }

        @Override
        protected BLStdControlResult doInBackground(Void... params) {

            BLStdControlParam intoStudyParam = new BLStdControlParam();
            intoStudyParam.setAct(BLControlActConstans.ACT_GET);
            //intoStudyParam.getParams().add("pwr");

            return BLLet.Controller.dnaControl(mDNADevice.getDid(), null, intoStudyParam);
        }

        @Override
        protected void onPostExecute(BLStdControlResult result) {
            super.onPostExecute(result);
            //dismissProgressDialog();
            if (result != null && result.succeed()) {

                final int state = (int) result.getData().getVals().get(0).get(0).getVal();
                mPwrState = state == 1;
                
                refreshView();
            }else{
                //BLCommonUtils.toastErr(result);
            }
            
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

            return BLLet.Controller.dnaControl(mDNADevice.getDid(), null, intoStudyParam);
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
}
