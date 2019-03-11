package cn.com.broadlink.blappsdkdemo.activity.device;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import cn.com.broadlink.base.BLAppSdkErrCode;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.data.BLControlActConstans;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;
import cn.com.broadlink.sdk.data.controller.BLStdData;
import cn.com.broadlink.sdk.param.controller.BLStdControlParam;
import cn.com.broadlink.sdk.result.controller.BLStdControlResult;

public class RMDemoActivity extends Activity{
    private BLDNADevice mDNADevice;

    private TextView mIrCodeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_rm_demo);

        mDNADevice = getIntent().getParcelableExtra(BLConstants.INTENT_DEVICE);

        mIrCodeView = (TextView) findViewById(R.id.ircode_view);
    }

    public void studyIr(View view){
        new StudyIrTask().execute();
    }

    public void sendIr(View view){
        String irCodeStr = mIrCodeView.getText().toString();
        if(!TextUtils.isEmpty(irCodeStr)){
            new SendIrTask().execute(irCodeStr);
        }else{
            BLCommonUtils.toastShow(RMDemoActivity.this, "Please learn ircode first!");
        }
    }

    //学习红外
    private class StudyIrTask extends AsyncTask<Void, Void, BLStdControlResult>{
        private ProgressDialog progressDialog;
        private boolean mQueryIr;
        private int mQueryTime = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(RMDemoActivity.this);
            progressDialog.setMessage("learning...");
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mQueryIr = false;
                }
            });
            progressDialog.show();
        }

        @Override
        protected BLStdControlResult doInBackground(Void... params) {
            /**发送 RM 进入学习命令**/
            BLStdControlParam intoStudyParam = new BLStdControlParam();
            intoStudyParam.setAct(BLControlActConstans.ACT_SET);
            intoStudyParam.getParams().add("irdastudy");
            BLStdControlResult intoStudyResult = BLLet.Controller.dnaControl(mDNADevice.getDid(), null, intoStudyParam);
            //判断是否进入学习成功
            if(intoStudyResult != null && intoStudyResult.getStatus() == BLAppSdkErrCode.SUCCESS){
                mQueryIr = true;

                while (mQueryIr){
                    //隔间500ms 查询一次，是否学习成功
                    SystemClock.sleep(500);

                    mQueryTime += 500;

                    //超过60s超时，则不再继续查询
                    if(mQueryTime > 60 * 1000){
                        mQueryIr = false;
                    }

                    /**进入学习成功之后，等待RM学习，查询RM是否学习到红外**/
                    BLStdControlParam queryIrStudyParam = new BLStdControlParam();
                    queryIrStudyParam.setAct(BLControlActConstans.ACT_GET);
                    queryIrStudyParam.getParams().add("irda");
                    BLStdControlResult queryRtudyResult = BLLet.Controller.dnaControl(mDNADevice.getDid(), null, queryIrStudyParam);
                    if(queryRtudyResult != null && queryRtudyResult.getStatus() == BLAppSdkErrCode.SUCCESS){
                        mQueryIr = false;
                        return queryRtudyResult;
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(BLStdControlResult stdControlResult) {
            super.onPostExecute(stdControlResult);
            progressDialog.dismiss();
            if(stdControlResult != null && stdControlResult.getStatus() == BLAppSdkErrCode.SUCCESS){

                try{
                    String irCodeStr = (String) stdControlResult.getData().getVals().get(0).get(0).getVal();
                    mIrCodeView.setText(irCodeStr);
                }catch (Exception e){}
            }
        }
    }

    //发送红外
    private class SendIrTask extends AsyncTask<String, Void, BLStdControlResult>{
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(RMDemoActivity.this);
            progressDialog.setMessage("发送中...");
            progressDialog.show();
        }

        @Override
        protected BLStdControlResult doInBackground(String... params) {
            //设置要发送的红外指令
            BLStdData.Value value = new BLStdData.Value();
            value.setVal(params[0]);

            ArrayList<BLStdData.Value> irVals = new ArrayList<>();
            irVals.add(value);

            /**发送学习到的命令**/
            BLStdControlParam intoStudyParam = new BLStdControlParam();
            intoStudyParam.setAct(BLControlActConstans.ACT_SET);
            intoStudyParam.getParams().add("irda");
            intoStudyParam.getVals().add(irVals);

            return BLLet.Controller.dnaControl(mDNADevice.getDid(), null, intoStudyParam);
        }

        @Override
        protected void onPostExecute(BLStdControlResult stdControlResult) {
            super.onPostExecute(stdControlResult);
            progressDialog.dismiss();
            if(stdControlResult != null && stdControlResult.getStatus() == BLAppSdkErrCode.SUCCESS){
                BLCommonUtils.toastShow(RMDemoActivity.this, "Send Success");
            } else {
                BLCommonUtils.toastShow(RMDemoActivity.this, "Send Failed " + stdControlResult.getMsg());
            }
        }
    }
}
