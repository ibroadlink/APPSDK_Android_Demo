package cn.com.broadlink.blappsdkdemo.activity.Device;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.intferfacer.SPControlModel;
import cn.com.broadlink.blappsdkdemo.presenter.SPControlListener;
import cn.com.broadlink.blappsdkdemo.presenter.SPControlModelImpl;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;
import cn.com.broadlink.sdk.result.controller.BLBaseBodyResult;

/**
 * SP系列Demo
 * Created by YeJin on 2016/5/10.
 */
public class SPDemoActivity extends Activity implements SPControlListener{
    private static final int PWR_ON = 1;
    private static final int PWR_OFF = 0;

    private static final String TIMING_TASK_MODEL = "tmrtsk";
    private static final String PER_TASK_MODEL = "pertsk";
    private static final String CYCLE_TASK_MODEL = "cyctsk";
    private static final String RANDOM_TASK_MODEL = "randtsk";

    private BLDNADevice mDNADevice;
    private TextView mSpStatusView;
    private SPControlModel mSPControlModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sp_demo_layout);

        mDNADevice = getIntent().getParcelableExtra("INTENT_DEV_ID");
        mSpStatusView = (TextView) findViewById(R.id.sp_status_view);

        mSPControlModel = new SPControlModelImpl(this);
    }

    public void spOpen(View view){
        mSPControlModel.controlDevPwr(mDNADevice.getDid(), PWR_ON);
    }

    public void spClose(View view){
        mSPControlModel.controlDevPwr(mDNADevice.getDid(), PWR_OFF);
    }

    public void spTimingTaskSet(View view) {
        mSPControlModel.taskDevSet(mDNADevice.getDid(), TIMING_TASK_MODEL, "+0800@null|0@20171031-103030|1");
    }

    public void spPerTaskSet(View view) {
        mSPControlModel.taskDevSet(mDNADevice.getDid(), PER_TASK_MODEL, "1|+0800-150000@160000|1234567|1|1");
    }

    public void spCycleTaskSet(View view) {
        mSPControlModel.taskDevSet(mDNADevice.getDid(), CYCLE_TASK_MODEL, "1|+0800-000000@235959|50|1200|12347");
    }

    public void spRandomTaskSet(View view) {
        mSPControlModel.taskDevSet(mDNADevice.getDid(), RANDOM_TASK_MODEL, "1|+0800-000000@235901|10|12347");
    }

    public void queryElectricityData(View view) {
        new queryDeviceDataTask().execute();
    }
    
    @Override
    public void deviceStatusShow(int pwr) {
        if(pwr == PWR_ON){
            mSpStatusView.setText("Open");
        }else if(pwr == PWR_OFF){
            mSpStatusView.setText("Close");
        }
    }

    @Override
    public void taskSuccess() {
        BLCommonUtils.toastShow(SPDemoActivity.this, "Task Set Success");
    }

    @Override
    public void taskFaile(String msg) {
        BLCommonUtils.toastShow(SPDemoActivity.this, "Task Set Failed :" + msg);
    }

    private ProgressDialog mProgressDialog;

    @Override
    public void controlStart() {
        mProgressDialog = new ProgressDialog(SPDemoActivity.this);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.show();
    }

    @Override
    public void controlEnd() {
        if(mProgressDialog != null){
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void controlSuccess(int pwr) {
        deviceStatusShow(pwr);
//        BLCommonUtils.toastShow(SPDemoActivity.this, "Control Success");
    }

    @Override
    public void controlFail(BLBaseResult result) {
//        BLCommonUtils.toastShow(SPDemoActivity.this, "Control Failed");
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

    /**查询SP设备状态定时器**/
    private Timer mQuerySPStatusTimer;

    private void startQuerySPStatusTimer(){
        if(mQuerySPStatusTimer == null){
            mQuerySPStatusTimer = new Timer();
            mQuerySPStatusTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mSPControlModel.queryDevStatus(mDNADevice.getDid());
                }
            }, 0 , 500);
        }
    }

    private void stopQuerySPStatusTimer(){
        if(mQuerySPStatusTimer != null){
            mQuerySPStatusTimer.cancel();
            mQuerySPStatusTimer = null;
        }
    }

    private class queryDeviceDataTask extends AsyncTask<Void, Void, BLBaseBodyResult> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SPDemoActivity.this);
            progressDialog.setMessage("Querying...");
            progressDialog.show();
        }

        @Override
        protected BLBaseBodyResult doInBackground(Void... params) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
            Date nowDate = new Date();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(nowDate);
            calendar.add(Calendar.DAY_OF_MONTH, -3);
            Date pastDate = calendar.getTime();

            String startTime = format.format(pastDate);
            String endTime = format.format(nowDate);

            return BLLet.Controller.queryDeviceData(mDNADevice.getDid(), null, startTime, endTime, null);
        }

        @Override
        protected void onPostExecute(BLBaseBodyResult result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            if (result.succeed()) {
                Log.d(BLConstants.BROADLINK_LOG_TAG, result.getResponseBody());
            }

        }
    }
}
