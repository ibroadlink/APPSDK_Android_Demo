package cn.com.broadlink.blappsdkdemo.activity.device;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import cn.com.broadlink.base.BLAppSdkErrCode;
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.data.BLControlActConstans;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.BLListAlert;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseRecyclerAdapter;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseViewHolder;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.divideritemdecoration.BLDividerUtil;
import cn.com.broadlink.ircode.BLIRCode;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;
import cn.com.broadlink.sdk.data.controller.BLStdData;
import cn.com.broadlink.sdk.param.controller.BLStdControlParam;
import cn.com.broadlink.sdk.result.controller.BLStdControlResult;

public class DevRmIrControlActivity extends TitleActivity {
    
    private EditText mEtInput;
    private EditText mEtUnitCode;
    private Button mBtStudy;
    private Button mBtSend;
    private Button mBtWave2unit;
    private Button mBtUnit2wave;
    private Button mBtSetTimer;
    private Button mBtGetTimer;
    private RecyclerView mRvContent;
    private BLDNADevice mDNADevice;
    private StringAdapter mAdapter;
    private ArrayList<String> mTimers = new ArrayList<>();
    private String mSample = 
            "2600240100012894151215121536161215121512151215121536163516121536151215121512161116111612151213141512153616111612151214131413151215361612153615121512153615121600029016111512151216121437151215121611171016121512131415121536161116121314131413141314131413141314131414131413141413141314141314371438130005240001299216121512163516111413141314141314153613391314123913141413141314141314121513141314161116361314121315141314131413141339123913381314131414371413140002921413141314141314131414131314131512141314141314131414131413141413141314141314131413141314131512141314141314131414133813141314133913000d0500000000";
    
    /**RM 定时任务**/
    public static final String ITF_RM_TIMER = "rmtimer";
    /**RM index**/
    public static final String ITF_RM_TIMER_INDEX = "index";
    /**RM count**/
    public static final String ITF_RM_TIMER_COUNT = "count";
    /**RM 定时任务删除**/
    public static final String ITF_RM_TIMER_DEL = "delrmtimer";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_rm_ir_control);
        setBackWhiteVisible();
        setTitle("Rm Control Demo");

        mDNADevice = getIntent().getParcelableExtra(BLConstants.INTENT_PARCELABLE);

        findView();

        initView();

        setListener();
    }

    private void initView() {
        mEtInput.setText(mSample);
        
        mAdapter = new StringAdapter();
        mRvContent.setLayoutManager(new LinearLayoutManager(mActivity));
        mRvContent.setAdapter(mAdapter);
        mRvContent.addItemDecoration(BLDividerUtil.getDefault(mActivity, mTimers));
    }

    private void setListener() {
        setRightButtonOnClickListener("Clear", getResources().getColor(R.color.bl_yellow_main_color), new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                mEtInput.setText(null);
            }
        });
        
        mBtStudy.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                new StudyIrTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
            }
        });
        
        mBtSend.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                if(!TextUtils.isEmpty(mEtInput.getText()) && !TextUtils.isEmpty(mEtUnitCode.getText())){
                    BLListAlert.showAlert(mActivity, null, new String[]{"WaveCode", "UnitCode"}, new BLListAlert.OnItemClickLister() {
                        @Override
                        public void onClick(int whichButton) {
                            String code = "";
                            switch (whichButton){
                                case 0:
                                    code = mEtInput.getText().toString();
                                    break;
                                    
                                case 1:
                                    code = mEtUnitCode.getText().toString();
                                    break;
                            }
                            new SendIrTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR, code);
                        }
                    });
                }else if(!TextUtils.isEmpty(mEtInput.getText())){
                    new SendIrTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR, mEtInput.getText().toString());
                }else if(!TextUtils.isEmpty(mEtUnitCode.getText())){
                    new SendIrTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR, mEtUnitCode.getText().toString());
                }else{
                    BLCommonUtils.toastShow(mActivity, "Please learn or input ircode first!");
                }
            }
        });
        
        mBtWave2unit.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                if(!TextUtils.isEmpty(mEtInput.getText())){
                    final String unitCode = BLIRCode.waveCodeChangeToUnitCode(mEtInput.getText().toString());
                    mEtUnitCode.setText(unitCode);
                }else{
                    BLCommonUtils.toastShow(mActivity, "Please learn or input ircode first!");
                }
            }
        });

        mBtUnit2wave.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                if(!TextUtils.isEmpty(mEtUnitCode.getText())){
                    final String unitCode = BLIRCode.unitCodeChangeToWaveCode(mEtUnitCode.getText().toString());
                    mEtInput.setText(unitCode);
                }else{
                    BLCommonUtils.toastShow(mActivity, "Please learn or input ircode first!");
                }
            }
        });
        
        mBtGetTimer.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                new QueryTimerListTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
            }
        });
        
        mBtSetTimer.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                if(TextUtils.isEmpty(mEtInput.getText())){
                    BLToastUtils.show("Please input ircode first!");
                    mEtInput.requestFocus();
                    return;
                }
                
                
                BLAlert.showEditDilog(mActivity, "SetUp Timer", "0|1|1|1|null|name|1", new BLAlert.BLEditDialogOnClickListener() {
                    @Override
                    public void onClink(String value) {
                        new AddTimerTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR, value, mEtInput.getText().toString());
                    }

                    @Override
                    public void onClinkCacel(String value) {

                    }
                }, false);
            }
        });
        
        mAdapter.setOnItemClickListener(new BLBaseRecyclerAdapter.OnClickListener() {
            @Override
            public void onClick(final int position, int viewType) {
                BLAlert.showDialog(mActivity, "Confirm to delete?", new BLAlert.DialogOnClickListener() {
                    @Override
                    public void onPositiveClick() {
                        try {
                            final String timerStr = mTimers.get(position);
                            final String index = timerStr.split("\\|")[0];
                            new DelTimerTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR, Integer.parseInt(index));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNegativeClick() {

                    }
                });
            }
        });
    }

    private void findView() {
        mEtInput = (EditText) findViewById(R.id.et_input);
        mEtUnitCode = (EditText) findViewById(R.id.et_unit_code);
        mBtStudy = (Button) findViewById(R.id.bt_study);
        mBtSend = (Button) findViewById(R.id.bt_send);
        mBtWave2unit = (Button) findViewById(R.id.bt_wave2unit);
        mBtUnit2wave = (Button) findViewById(R.id.bt_unit2wave);
        mBtSetTimer = (Button) findViewById(R.id.bt_set_timer);
        mBtGetTimer = (Button) findViewById(R.id.bt_get_timer);
        mRvContent = (RecyclerView) findViewById(R.id.rv_content);
    }


    //学习红外
    private class StudyIrTask extends AsyncTask<Void, Void, BLStdControlResult>{
        private ProgressDialog progressDialog;
        private boolean mQueryIr;
        private int mQueryTime = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mActivity);
            progressDialog.setMessage("Study...");
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
                    mEtInput.setText(irCodeStr);
                }catch (Exception e){}
            }else{
                BLCommonUtils.toastErr(stdControlResult);
            }
        }
    }

    //发送红外
    private class SendIrTask extends AsyncTask<String, Void, BLStdControlResult>{
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mActivity);
            progressDialog.setMessage("Send...");
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
                BLCommonUtils.toastShow(mActivity, "Send Success");
            } else {
                BLCommonUtils.toastShow(mActivity, "Send Failed " + stdControlResult.getMsg());
            }
        }
    }

    //查询定时列表
    private class QueryTimerListTask extends AsyncTask<String, Void, BLStdControlResult>{
        ArrayList<String> timers = new ArrayList<>();
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Query Timer List...");
        }

        @Override
        protected BLStdControlResult doInBackground(String... params) {
            return  getTimer(0);
        }

        @Nullable
        private BLStdControlResult getTimer(int index) {
            BLStdControlParam intoStudyParam = new BLStdControlParam();
            intoStudyParam.setAct(BLControlActConstans.ACT_GET);
            intoStudyParam.getParams().add(ITF_RM_TIMER);
            intoStudyParam.getParams().add(ITF_RM_TIMER_INDEX);
            intoStudyParam.getParams().add(ITF_RM_TIMER_COUNT);

            BLStdData.Value<String> timerVal = new BLStdData.Value<>();
            timerVal.setVal("0");
            ArrayList<BLStdData.Value> timerValList = new ArrayList<>();
            timerValList.add(timerVal);
            intoStudyParam.getVals().add(timerValList);

            BLStdData.Value<Integer> indexVal = new BLStdData.Value<>();
            indexVal.setVal(index);
            ArrayList<BLStdData.Value> indexValList = new ArrayList<>();
            indexValList.add(indexVal);
            intoStudyParam.getVals().add(indexValList);

            BLStdData.Value<Integer> countVal = new BLStdData.Value<>();
            countVal.setVal(5);
            ArrayList<BLStdData.Value> countValList = new ArrayList<>();
            countValList.add(countVal);
            intoStudyParam.getVals().add(countValList);

            final BLStdControlResult stdControlResult = BLLet.Controller.dnaControl(mDNADevice.getDid(), null, intoStudyParam);
            if(stdControlResult != null && stdControlResult.getStatus() == BLAppSdkErrCode.SUCCESS && stdControlResult.getData()!=null){
                if(stdControlResult.getData().getVals().size()==3){
                    final ArrayList<BLStdData.Value> values = stdControlResult.getData().getVals().get(0);
                    int total = (int) stdControlResult.getData().getVals().get(2).get(0).getVal();
                    for (BLStdData.Value item : values){
                        timers.add((String) item.getVal());
                    }
                    int indexQuery = timers.size();
                    if(indexQuery<total){
                        return  getTimer(indexQuery);
                    }
                }
            }
            return stdControlResult;
        }

        @Override
        protected void onPostExecute(BLStdControlResult stdControlResult) {
            super.onPostExecute(stdControlResult);
            dismissProgressDialog();
            
            if(stdControlResult != null && stdControlResult.getStatus() == BLAppSdkErrCode.SUCCESS && stdControlResult.getData()!=null){
                mTimers.clear();
                mTimers.addAll(timers);
                mAdapter.notifyDataSetChanged();
            } else {
                BLCommonUtils.toastErr(stdControlResult);
            }
        }
    }


    private class AddTimerTask extends AsyncTask<String, Void, BLStdControlResult>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Add Timer...");
        }

        @Override
        protected BLStdControlResult doInBackground(String... params) {

            BLStdControlParam intoStudyParam = new BLStdControlParam();
            intoStudyParam.setAct(BLControlActConstans.ACT_SET);
            intoStudyParam.getParams().add(ITF_RM_TIMER);
            
            String timerParam = params[0];
            String timerIrdata = params[1];
            String valStr = String.format("%s|%d:500|%s", timerParam, timerIrdata.length()/2, timerIrdata);

            BLStdData.Value<String> controlValueInfo = new BLStdData.Value<String>();
            controlValueInfo.setVal(valStr);
            
            ArrayList<BLStdData.Value> pwrValList = new ArrayList<BLStdData.Value>();
            pwrValList.add(controlValueInfo);
            
            intoStudyParam.getVals().add(pwrValList);
                    
            return BLLet.Controller.dnaControl(mDNADevice.getDid(), null, intoStudyParam);
        }

        @Override
        protected void onPostExecute(BLStdControlResult stdControlResult) {
            super.onPostExecute(stdControlResult);
            dismissProgressDialog();

            if(stdControlResult != null && stdControlResult.getStatus() == BLAppSdkErrCode.SUCCESS){
                new QueryTimerListTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
            } else {
                BLCommonUtils.toastErr(stdControlResult);
            }
        }
    }



    private class DelTimerTask extends AsyncTask<Integer, Void, BLStdControlResult>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Del Timer...");
        }

        @Override
        protected BLStdControlResult doInBackground(Integer... params) {

            BLStdControlParam intoStudyParam = new BLStdControlParam();
            intoStudyParam.setAct(BLControlActConstans.ACT_SET);
            intoStudyParam.getParams().add(ITF_RM_TIMER_DEL);

            BLStdData.Value<Integer> controlValueInfo = new BLStdData.Value<Integer>();
            controlValueInfo.setVal(params[0]);

            ArrayList<BLStdData.Value> pwrValList = new ArrayList<BLStdData.Value>();
            pwrValList.add(controlValueInfo);

            intoStudyParam.getVals().add(pwrValList);

            return BLLet.Controller.dnaControl(mDNADevice.getDid(), null, intoStudyParam);
        }

        @Override
        protected void onPostExecute(BLStdControlResult stdControlResult) {
            super.onPostExecute(stdControlResult);
            dismissProgressDialog();

            if(stdControlResult != null && stdControlResult.getStatus() == BLAppSdkErrCode.SUCCESS){
                new QueryTimerListTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
            } else {
                BLCommonUtils.toastErr(stdControlResult);
            }
        }
    }


    class StringAdapter extends BLBaseRecyclerAdapter<String> {

        public StringAdapter() {
            super(mTimers, R.layout.item_family_room);
        }

        @Override
        public void onBindViewHolder(BLBaseViewHolder holder, final int position) {
            super.onBindViewHolder(holder, position);
            holder.setText(R.id.tv_name, mBeans.get(position));
            holder.setVisible(R.id.tv_mac, false);
        }
    }


}
