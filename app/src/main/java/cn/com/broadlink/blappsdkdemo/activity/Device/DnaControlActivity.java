package cn.com.broadlink.blappsdkdemo.activity.Device;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.com.broadlink.base.BLCommonTools;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.data.BLControlActConstans;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.constants.controller.BLControllerErrCode;
import cn.com.broadlink.sdk.constants.controller.BLDeviceTaskType;
import cn.com.broadlink.sdk.data.controller.BLCycleInfo;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;
import cn.com.broadlink.sdk.data.controller.BLPeriodInfo;
import cn.com.broadlink.sdk.data.controller.BLStdData;
import cn.com.broadlink.sdk.data.controller.BLTimerInfo;
import cn.com.broadlink.sdk.param.controller.BLStdControlParam;
import cn.com.broadlink.sdk.result.controller.BLProfileStringResult;
import cn.com.broadlink.sdk.result.controller.BLQueryTaskResult;
import cn.com.broadlink.sdk.result.controller.BLStdControlResult;
import cn.com.broadlink.sdk.result.controller.BLTaskDataResult;

/**
 * Created by zhujunjie on 2016/11/11.
 */

public class DnaControlActivity extends TitleActivity {
    private BLDNADevice mDNADevice;
    private EditText mValInputView;
    private EditText mParamInputView;
    private Button mDnaGetBtn;
    private Button mDnaSetBtn;
    private Button mDnaGetProfileBtn;
    private Button mDnaTaskListBtn;
    private Button mTimerTaskSetBtn;
    private Button mPeriodTaskSetBtn;
    private Button mRandomTaskSetBtn;
    private Button mQueryTaskDataBtn;
    private Button maskDelBtn;
    private Button mFCListBtn;
    private Button mFCConfigBtn;
    private Button mFCQueryBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dnacontrol_layout);
        setTitle(R.string.Device_STD_Control);
        setBackWhiteVisible();

        mDNADevice = getIntent().getParcelableExtra("INTENT_DEV_ID");

        findView();
        setListener();
    }

    private void findView(){
        mValInputView = (EditText) findViewById(R.id.vals_input_view);
        mParamInputView = (EditText) findViewById(R.id.params_input_view);
        mDnaGetBtn = (Button) findViewById(R.id.dna_get_btn);
        mDnaSetBtn = (Button) findViewById(R.id.dna_set_btn);
        mDnaGetProfileBtn = (Button) findViewById(R.id.dna_get_profile_btn);
        mDnaTaskListBtn = (Button) findViewById(R.id.dna_get_tasklist_btn);
        mTimerTaskSetBtn = (Button) findViewById(R.id.dna_set_timer_btn);
        mPeriodTaskSetBtn = (Button) findViewById(R.id.dna_set_period_btn);
        mRandomTaskSetBtn = (Button) findViewById(R.id.dna_set_random_btn);
        mQueryTaskDataBtn = (Button) findViewById(R.id.dna_query_task_data_btn);
        maskDelBtn = (Button) findViewById(R.id.dna_del_period_btn);

        mFCListBtn = findViewById(R.id.fastcon_list_btn);
        mFCConfigBtn = findViewById(R.id.fastcon_config_btn);
        mFCQueryBtn = findViewById(R.id.fastcon_query_btn);
    }

    private void setListener(){
        mDnaGetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String param = mParamInputView.getText().toString();
                new DnaControlGetTask().execute(param);
            }
        });

        mDnaSetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String param = mParamInputView.getText().toString();
                String val = mValInputView.getText().toString();
                new DnaControlSetTask().execute(param, val);
            }
        });

        mDnaGetProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DnaControlGetProfileTask().execute();
            }
        });

        mDnaTaskListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DnaGetTaskListTask().execute();
            }
        });

        mTimerTaskSetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String param = mParamInputView.getText().toString();
                String val = mValInputView.getText().toString();
                new SetTaskTimerTask().execute(param, val);
            }
        });

        mPeriodTaskSetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String param = mParamInputView.getText().toString();
                String val = mValInputView.getText().toString();
                new SetTaskPeriodTask().execute(param, val);
            }
        });

        mRandomTaskSetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String param = mParamInputView.getText().toString();
                new SetTaskRandomTask().execute(param);
            }
        });

        mQueryTaskDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String param = mParamInputView.getText().toString();
                String val = mValInputView.getText().toString();
                new QueryTaskDataTask().execute(param, val);
            }
        });

        maskDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String param = mParamInputView.getText().toString();
                String val = mValInputView.getText().toString();
                new DelTaskPeriodTask().execute(param, val);
            }
        });

        mFCListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String command = "fastcon_no_config";

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("did", mDNADevice.getDid());
                    jsonObject.put("act", 0);
                    jsonObject.put("count", 5);
                    jsonObject.put("index", 0);

                    new FastConCmdTask().execute(command, jsonObject.toJSONString());

                } catch (JSONException e) {
                    BLCommonTools.handleError(e);
                }

            }
        });

        mFCConfigBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String command = "fastcon_no_config";

                    org.json.JSONObject jsonObject = new org.json.JSONObject();
                    jsonObject.put("did", mDNADevice.getDid());
                    jsonObject.put("act",  1);

                    JSONArray didArray = new JSONArray();
                    didArray.put("0000000000000000000034ea3437fc5f");

                    jsonObject.put("devlist", didArray);

                    new FastConCmdTask().execute(command, jsonObject.toString());

                } catch (org.json.JSONException e) {
                    BLCommonTools.handleError(e);
                }
            }
        });

        mFCQueryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String command = "fastcon_no_config";

                    org.json.JSONObject jsonObject = new org.json.JSONObject();
                    jsonObject.put("did", mDNADevice.getDid());
                    jsonObject.put("act",  2);

                    JSONArray didArray = new JSONArray();
                    didArray.put("0000000000000000000034ea3437fc5f");

                    jsonObject.put("devlist", didArray);

                    new FastConCmdTask().execute(command, jsonObject.toString());

                    ArrayList<String> dids = new ArrayList<>();
                    dids.add("0000000000000000000034ea3437fc5f");

                    BLLet.Controller.addWhiteDids(dids);
                } catch (org.json.JSONException e) {
                    BLCommonTools.handleError(e);
                }
            }
        });
    }

    class DnaControlGetTask extends AsyncTask<String, Void, BLStdControlResult> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DnaControlActivity.this);
            progressDialog.setMessage("Getting...");
            progressDialog.show();
        }

        @Override
        protected BLStdControlResult doInBackground(String... params) {
            String param = params[0];

            BLStdControlParam stdControlParam = new BLStdControlParam();
            stdControlParam.setAct(BLControlActConstans.ACT_GET);
            if (param.length() > 0) {
                stdControlParam.getParams().add(param);
            }

            return BLLet.Controller.dnaControl(mDNADevice.getDid(), null, stdControlParam);
        }

        @Override
        protected void onPostExecute(BLStdControlResult result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if(result != null && result.getStatus() == BLControllerErrCode.SUCCESS){
                BLStdData stdData = result.getData();
                BLCommonUtils.toastShow(DnaControlActivity.this, JSON.toJSONString(stdData));
            }
        }
    }

    class DnaControlSetTask extends AsyncTask<String, Void, BLStdControlResult> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DnaControlActivity.this);
            progressDialog.setMessage("Setting...");
            progressDialog.show();
        }

        @Override
        protected BLStdControlResult doInBackground(String... params) {
            String param = params[0];

            BLStdData.Value value = new BLStdData.Value();
            value.setVal(params[1]);

            ArrayList<BLStdData.Value> dnaVals = new ArrayList<>();
            dnaVals.add(value);

            BLStdControlParam stdControlParam = new BLStdControlParam();
            stdControlParam.setAct(BLControlActConstans.ACT_SET);
            stdControlParam.getParams().add(param);
            stdControlParam.getVals().add(dnaVals);

            return BLLet.Controller.dnaControl(mDNADevice.getDid(), null, stdControlParam);
        }

        @Override
        protected void onPostExecute(BLStdControlResult result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if(result != null && result.getStatus() == BLControllerErrCode.SUCCESS){
                BLStdData stdData = result.getData();
                BLCommonUtils.toastShow(DnaControlActivity.this, JSON.toJSONString(stdData));
            }
        }
    }

    class DnaControlGetProfileTask extends AsyncTask<String, Void, BLProfileStringResult> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DnaControlActivity.this);
            progressDialog.setMessage("Getting Profile...");
            progressDialog.show();
        }

        @Override
        protected BLProfileStringResult doInBackground(String... params) {
            return BLLet.Controller.queryProfileByPid(mDNADevice.getPid());
        }

        @Override
        protected void onPostExecute(BLProfileStringResult result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if(result != null && result.getStatus() == BLControllerErrCode.SUCCESS){
                String profileStr = result.getProfile();
                BLCommonUtils.toastShow(DnaControlActivity.this, profileStr);
            }
        }
    }

    class DnaGetTaskListTask extends AsyncTask<String, Void, BLQueryTaskResult> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DnaControlActivity.this);
            progressDialog.setMessage("Getting TaskList...");
            progressDialog.show();
        }

        @Override
        protected BLQueryTaskResult doInBackground(String... params) {
            return BLLet.Controller.queryTask(mDNADevice.getDid(), null);
        }

        @Override
        protected void onPostExecute(BLQueryTaskResult result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if(result != null && result.getStatus() == BLControllerErrCode.SUCCESS){
                BLCommonUtils.toastShow(DnaControlActivity.this, "Success");
            } else {
                BLCommonUtils.toastShow(DnaControlActivity.this, result.getMsg());
            }
        }

    }

    class SetTaskTimerTask extends AsyncTask<String, Void, BLQueryTaskResult> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DnaControlActivity.this);
            progressDialog.setMessage("Getting TaskList...");
            progressDialog.show();
        }

        @Override
        protected BLQueryTaskResult doInBackground(String... params) {
            BLTimerInfo info = new BLTimerInfo();
            info.setEnable(true);
            info.setYear(2018);
            info.setMonth(11);
            info.setDay(23);
            info.setHour(1);
            info.setMin(12);
            info.setSec(0);

            BLStdData stdData = new BLStdData();
            String param = params[0];

            BLStdData.Value value = new BLStdData.Value();
            value.setVal(params[1]);

            ArrayList<BLStdData.Value> dnaVals = new ArrayList<>();
            dnaVals.add(value);

            stdData.getParams().add(param);
            stdData.getVals().add(dnaVals);

            return BLLet.Controller.updateTask(mDNADevice.getDid(), null, BLDeviceTaskType.COMMON_TIMER_TASK, true, info, stdData);
        }

        @Override
        protected void onPostExecute(BLQueryTaskResult result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if(result != null && result.getStatus() == BLControllerErrCode.SUCCESS){
                BLCommonUtils.toastShow(DnaControlActivity.this, "Success");
            } else {
                BLCommonUtils.toastShow(DnaControlActivity.this, result.getMsg());
            }
        }
    }

    class SetTaskPeriodTask extends AsyncTask<String, Void, BLQueryTaskResult> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DnaControlActivity.this);
            progressDialog.setMessage("Getting TaskList...");
            progressDialog.show();
        }

        @Override
        protected BLQueryTaskResult doInBackground(String... params) {
            BLPeriodInfo periodInfo = new BLPeriodInfo();
            periodInfo.setEnable(true);
            periodInfo.setHour(1);
            periodInfo.setMin(12);
            periodInfo.setSec(0);
            List<Integer> list = Arrays.asList(1, 2, 4, 6);
            periodInfo.setRepeat(list);

            BLStdData stdData = new BLStdData();
            String param = params[0];
            BLStdData.Value value = new BLStdData.Value();
            value.setVal(params[1]);
            ArrayList<BLStdData.Value> dnaVals = new ArrayList<>();
            dnaVals.add(value);
            stdData.getParams().add(param);
            stdData.getVals().add(dnaVals);

            return BLLet.Controller.updateTask(mDNADevice.getDid(), null, true, periodInfo, stdData);
        }

        @Override
        protected void onPostExecute(BLQueryTaskResult result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if(result != null && result.getStatus() == BLControllerErrCode.SUCCESS){
                BLCommonUtils.toastShow(DnaControlActivity.this, "Success");
            } else {
                BLCommonUtils.toastShow(DnaControlActivity.this, result.getMsg());
            }
        }
    }

    class SetTaskRandomTask extends AsyncTask<String, Void, BLQueryTaskResult> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DnaControlActivity.this);
            progressDialog.setMessage("Setting Task...");
            progressDialog.show();
        }

        @Override
        protected BLQueryTaskResult doInBackground(String... params) {
            BLCycleInfo info = new BLCycleInfo();
            info.setEnable(true);
            List<Integer> list = Arrays.asList(1, 2, 4, 6, 7);
            info.setRepeat(list);

            info.setStart_hour(1);
            info.setStart_min(12);
            info.setStart_sec(0);
            info.setCmd1duration(10);

            info.setEnd_hour(2);
            info.setEnd_min(30);
            info.setEnd_sec(0);
            info.setCmd2duration(10);

            String param = params[0];

            BLStdData stdData1 = new BLStdData();
            BLStdData.Value value1 = new BLStdData.Value();
            value1.setVal(1);
            ArrayList<BLStdData.Value> dnaVals1 = new ArrayList<>();
            dnaVals1.add(value1);
            stdData1.getParams().add(param);
            stdData1.getVals().add(dnaVals1);

            BLStdData stdData2 = new BLStdData();
            BLStdData.Value value2 = new BLStdData.Value();
            value2.setVal(0);
            ArrayList<BLStdData.Value> dnaVals2 = new ArrayList<>();
            dnaVals2.add(value2);
            stdData2.getParams().add(param);
            stdData2.getVals().add(dnaVals2);

            return BLLet.Controller.updateTask(mDNADevice.getDid(), null, BLDeviceTaskType.RANDOM_TIMER_TASK, true, info, stdData1, stdData2);
        }

        @Override
        protected void onPostExecute(BLQueryTaskResult result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if(result != null && result.getStatus() == BLControllerErrCode.SUCCESS){
                BLCommonUtils.toastShow(DnaControlActivity.this, "Success");
            } else {
                BLCommonUtils.toastShow(DnaControlActivity.this, result.getMsg());
            }
        }
    }

    class QueryTaskDataTask extends AsyncTask<String, Void, BLTaskDataResult> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DnaControlActivity.this);
            progressDialog.setMessage("Query Task...");
            progressDialog.show();
        }

        @Override
        protected BLTaskDataResult doInBackground(String... params) {
            int tasktype = Integer.parseInt(params[0]);
            int index = Integer.parseInt(params[1]);

            return BLLet.Controller.queryTaskData(mDNADevice.getDid(), null, tasktype, index);
        }

        @Override
        protected void onPostExecute(BLTaskDataResult result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if(result != null && result.getStatus() == BLControllerErrCode.SUCCESS){
                BLCommonUtils.toastShow(DnaControlActivity.this, "Success");
            } else {
                BLCommonUtils.toastShow(DnaControlActivity.this, result.getMsg());
            }
        }
    }

    class DelTaskPeriodTask extends AsyncTask<String, Void, BLQueryTaskResult> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DnaControlActivity.this);
            progressDialog.setMessage("Getting TaskList...");
            progressDialog.show();
        }

        @Override
        protected BLQueryTaskResult doInBackground(String... params) {
            int tasktype = Integer.parseInt(params[0]);
            int index = Integer.parseInt(params[1]);
            return BLLet.Controller.delTask(mDNADevice.getDid(), null, tasktype, index);
        }

        @Override
        protected void onPostExecute(BLQueryTaskResult result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if(result != null && result.getStatus() == BLControllerErrCode.SUCCESS){
                BLCommonUtils.toastShow(DnaControlActivity.this, "Success");
            } else {
                BLCommonUtils.toastShow(DnaControlActivity.this, result.getMsg());
            }
        }
    }

    class FastConCmdTask extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DnaControlActivity.this);
            progressDialog.setMessage("Fast control...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String command = params[0];
            String dataStr = params[1];

            return BLLet.Controller.dnaControl(mDNADevice.getDid(), null, dataStr, command, null);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            BLCommonUtils.toastShow(DnaControlActivity.this, result);

        }
    }
}
