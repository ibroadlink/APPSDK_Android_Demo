package cn.com.broadlink.blappsdkdemo.activity.device;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.activity.h5.DeviceH5Activity;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.data.BLControlActConstans;
import cn.com.broadlink.blappsdkdemo.data.BLDevProfileInfo;
import cn.com.broadlink.blappsdkdemo.data.BLDevProfileInftsValueInfo;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.BLListAlert;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseRecyclerAdapter;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseViewHolder;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;
import cn.com.broadlink.sdk.data.controller.BLStdData;
import cn.com.broadlink.sdk.param.controller.BLStdControlParam;
import cn.com.broadlink.sdk.result.controller.BLDownloadScriptResult;
import cn.com.broadlink.sdk.result.controller.BLDownloadUIResult;
import cn.com.broadlink.sdk.result.controller.BLProfileStringResult;
import cn.com.broadlink.sdk.result.controller.BLQueryResoureVersionResult;
import cn.com.broadlink.sdk.result.controller.BLStdControlResult;

public class DevDnaStdControlActivity extends TitleActivity implements View.OnClickListener {

    private RecyclerView mRvParams;
    private Button mBtAddParam;
    private Button mSelectParam;
    private Button mBtPasteParams;
    private Button mBtGet;
    private Button mBtSet;
    private Button mQueryScriptVer;
    private Button mBtScriptDownload;
    private Button mQueryUiVer;
    private Button mBtUiDownload;
    private Button mQueryProfile;
    private Button mBtWebContorl;
    private EditText mTvInput;
    private BLDNADevice mDNADevice;

    private List<DnaParam> mDnaParams = new ArrayList<>();
    private DnaParamAdapter mAdapter;
    private BLDevProfileInfo mBlDevProfileInfo;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_dna_std_control);
        setBackWhiteVisible();
        setTitle("Dna Std Control");
        
        findView();

        initView();

        setListener();
    }

    private void setListener() {
        mBtAddParam.setOnClickListener(this);
        mBtPasteParams.setOnClickListener(this);
        mBtGet.setOnClickListener(this);
        mBtSet.setOnClickListener(this);
        mQueryScriptVer.setOnClickListener(this);
        mBtScriptDownload.setOnClickListener(this);
        mQueryUiVer.setOnClickListener(this);
        mBtUiDownload.setOnClickListener(this);
        mQueryProfile.setOnClickListener(this);
        mBtWebContorl.setOnClickListener(this);
        mBtAddParam.setOnClickListener(this);
        mSelectParam.setOnClickListener(this);
    }

    private void initView() {
        mDNADevice = getIntent().getParcelableExtra(BLConstants.INTENT_PARCELABLE);
        mAdapter = new DnaParamAdapter();
        mRvParams.setLayoutManager(new LinearLayoutManager(mActivity));
        mRvParams.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_add_param:
                showAddParamDialog();
                break;
                
            case R.id.bt_select_param:
                showAddParamDialogV2();
                break;
                
            case R.id.bt_paste_params:
                showPasteParamDialog();
                break;
                
            case R.id.bt_get:
                new DnaControlTask().execute(BLControlActConstans.ACT_GET);
                break;
                
            case R.id.bt_set:
                new DnaControlTask().execute(BLControlActConstans.ACT_SET);
                break;
                
            case R.id.query_script_ver:
                new QueryScriptVersionTask().execute();
                break;
                
            case R.id.bt_script_download:
                new DownLoadScriptTask().execute();
                break;
                
            case R.id.query_ui_ver:
                new QueryUIVersionTask().execute();
                break;
                
            case R.id.bt_ui_download:
                new DownLoadUITask().execute();
                break;
                
            case R.id.query_profile:
                queryDevProfile();
                break;
                
            case R.id.bt_web_contorl:
                webControl();
                break;
        }
    }

    private void findView() {
        mRvParams = (RecyclerView) findViewById(R.id.rv_params);
        mBtAddParam = (Button) findViewById(R.id.bt_add_param);
        mSelectParam = (Button) findViewById(R.id.bt_select_param);
        mBtPasteParams = (Button) findViewById(R.id.bt_paste_params);
        mBtGet = (Button) findViewById(R.id.bt_get);
        mBtSet = (Button) findViewById(R.id.bt_set);
        mQueryScriptVer = (Button) findViewById(R.id.query_script_ver);
        mBtScriptDownload = (Button) findViewById(R.id.bt_script_download);
        mQueryUiVer = (Button) findViewById(R.id.query_ui_ver);
        mBtUiDownload = (Button) findViewById(R.id.bt_ui_download);
        mQueryProfile = (Button) findViewById(R.id.query_profile);
        mBtWebContorl = (Button) findViewById(R.id.bt_web_contorl);
        mTvInput = (EditText) findViewById(R.id.et_input);
    }

    public void webControl() {
        if (scriptFileExist() && uiFileExit()) {
            Intent intent = new Intent();
            intent.putExtra(BLConstants.INTENT_DEVICE, mDNADevice);
            intent.setClass(mActivity, DeviceH5Activity.class);
            startActivity(intent);
        } else {
            BLToastUtils.show("Script/Ui not exit, please download first!");
        }
    }
    
    private void showAddParamDialog(){
        final LinearLayout dialog = (LinearLayout) getLayoutInflater().inflate(R.layout.view_dialog_dna_std_param, null);
        final EditText etParam = dialog.findViewById(R.id.et_param);
        final EditText etVal = dialog.findViewById(R.id.et_val);

        BLAlert.showCustomViewDilog(mActivity, dialog, "Ok", "Cancel", new BLAlert.DialogOnClickListener() {
            @Override
            public void onPositiveClick() {
                if(TextUtils.isEmpty(etParam.getText()) || TextUtils.isEmpty(etVal.getText())){
                    BLToastUtils.show("Should not be null");
                }else{
                    mDnaParams.add(new DnaParam(etParam.getText().toString(), etVal.getText().toString()));
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNegativeClick() {

            }
        });
    }
    
    private void showAddParamDialogV2(){
        mAdapter.flushData();
        
        if(mBlDevProfileInfo == null){
            BLProfileStringResult devProfileResult = BLLet.Controller.queryProfileByPid(mDNADevice.getPid());
            if(devProfileResult!=null && devProfileResult.succeed()){
                mBlDevProfileInfo = JSON.parseObject(devProfileResult.getProfile(), BLDevProfileInfo.class);
            }else{
                BLToastUtils.show("Please confirm script had been downloaded!");
                return;
            }
        }
        final List<String> params = mBlDevProfileInfo.getSuids().get(0).getIntfsList();
        final String[] paramArray = params.toArray(new String[params.size()]);

        BLListAlert.showAlert(mActivity, null, paramArray, new BLListAlert.OnItemClickLister() {
            @Override
            public void onClick(int whichButton) {

                final List<BLDevProfileInftsValueInfo> intfValue = mBlDevProfileInfo.getSuids().get(0).getIntfValue(paramArray[whichButton]);
                if(intfValue != null && intfValue.size()>0){
                    final List<Integer> in = intfValue.get(0).getIn();
                    mDnaParams.add(new DnaParam(paramArray[whichButton], in));
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }
    
    private void showPasteParamDialog(){
        BLAlert.showEditDilog(mActivity, "Paste the std command string, this will cover the current params", null, new BLAlert.BLEditDialogOnClickListener() {
            @Override
            public void onClink(String value) {
                parseParams(value);
            }

            @Override
            public void onClinkCacel(String value) {

            }
        }, false);
    }
    
    private void parseParams(String stdStr){
        try {
            final JSONObject dnaCtrlCommand = JSON.parseObject(stdStr);
            final JSONArray params = dnaCtrlCommand.getJSONArray("params");
            final JSONArray vals = dnaCtrlCommand.getJSONArray("vals");
            
            if(dnaCtrlCommand == null || params.size()!=vals.size()){
                BLToastUtils.show("Command invalid!");
            }else{
                mDnaParams.clear();
                for (int i=0; i< params.size();i++){
                    final String itemParam = params.get(i).toString();
                    final JSONArray itemVals = (JSONArray) vals.get(i);
                    final JSONObject itemVal = (JSONObject) itemVals.get(0);
                    String val = itemVal.getString("val");
                    if(val==null){
                        val = String.valueOf(itemVal.getInteger("val"));
                    }
                    mDnaParams.add(new DnaParam(itemParam, val));
                }
                mAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
            BLToastUtils.show("Json parse fail!");
        }
    }
    
    private boolean scriptFileExist(){
        String scriptFilePath = BLLet.Controller.queryScriptPath(mDNADevice.getPid());
        Log.e("FileExist" , scriptFilePath);
        File file = new File(scriptFilePath);
        return file.exists();
    }

    private boolean uiFileExit() {
        String uiFilePath = BLLet.Controller.queryUIPath(mDNADevice.getPid());
        Log.e("UIExit", uiFilePath);
        File file = new File(uiFilePath);
        return file.exists();
    }

    private void showResult(Object result) {
        if(result == null){
            mTvInput.setText("Return null");
        }else{
            if(result instanceof String){
                mTvInput.setText((String)result);
            }else{
                mTvInput.setText(JSON.toJSONString(result, true)); 
            }
            
        }
    }

    private void queryDevProfile(){
        if(mBlDevProfileInfo != null){
            showResult(mBlDevProfileInfo);
            return;
        }
        
        BLProfileStringResult devProfileResult = BLLet.Controller.queryProfileByPid(mDNADevice.getPid());
        if(devProfileResult!=null && devProfileResult.succeed()){
            mBlDevProfileInfo = JSON.parseObject(devProfileResult.getProfile(), BLDevProfileInfo.class);
            showResult(mBlDevProfileInfo);
        }else{
            showResult(devProfileResult);
        }
    }
    
    
    class DnaParam {
        public String param;
        public String val;
        public List<Integer> in = new ArrayList<>();

        public DnaParam(String param, String val) {
            this.param = param;
            this.val = val;
        }
        public DnaParam(String param, List<Integer> in) {
            this.param = param;
            this.in.clear();
            if(in != null){
                this.in.addAll(in);
            }
        }

        public DnaParam() {
        }
    }
    
    class DnaParamAdapter extends BLBaseRecyclerAdapter<DnaParam>{

        public DnaParamAdapter() {
            super(mDnaParams, R.layout.item_dev_dna_std_param);
        }
        
        
        public void flushData(){
            for (int i=0; i< getItemCount(); i++){
                final BLBaseViewHolder holder = (BLBaseViewHolder) mRvParams.getChildViewHolder(mRvParams.getChildAt(i));
                final EditText etVal = holder.get(R.id.et_val);
                if(etVal != null && !TextUtils.isEmpty(etVal.getText())){
                    getItem(i).val = etVal.getText().toString();
                }
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(BLBaseViewHolder holder, final int position) {
            super.onBindViewHolder(holder, position);
            
            final Button btDel = holder.get(R.id.bt_del);
            final EditText etParam = holder.get(R.id.et_param);
            final EditText etVal = holder.get(R.id.et_val);
            
            etParam.setText(mBeans.get(position).param);
            etVal.setText(mBeans.get(position).val);

            btDel.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void doOnClick(View v) {
                    flushData();
                    mBeans.remove(position);
                    notifyDataSetChanged();
                }
            });

            
            etVal.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(etVal.getTag() != null){
                        return true;
                    }
                    
                    List<Integer>  in = mBeans.get(position).in;
                    if(in.get(0) == 1){ // 枚举
                        final StringBuffer sb = new StringBuffer();
                        final String[] selection = new String[in.size()-1];
                        for (int i=1;i<in.size();i++){
                            if(i!=1){
                                sb.append("、");
                            }
                            sb.append(in.get(i));
                            selection[i-1] = String.valueOf(in.get(i));
                        }
                        showResult(sb.toString());

                        BLListAlert.showAlert(mActivity, null, selection, new BLListAlert.OnItemClickLister() {
                            @Override
                            public void onClick(int whichButton) {
                                etVal.setText(selection[whichButton]);
                                etVal.setTag(null);
                                mBeans.get(position).val = selection[whichButton];
                            }
                        }, new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                etVal.setTag(null);
                            }
                        });
                        etVal.setTag(1);
                        return true;
                        
                    }else if(in.get(0) == 2){ // 连续
                        final StringBuffer sb = new StringBuffer();
                        sb.append("Min: ").append(in.get(1)).append("\n");
                        sb.append("Max: ").append(in.get(2)).append("\n");
                        sb.append("Step: ").append(in.get(3)).append("\n");
                        sb.append("Multiple: ").append(in.get(4)).append("\n");
                        showResult(sb.toString());
                    }else{ // 简单
                        showResult("String");
                    }
                    return false;
                }
            });
        }
    }

    class DnaControlTask extends AsyncTask<String, Void, BLStdControlResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          showProgressDialog("Controlling...");
        }

        @Override
        protected BLStdControlResult doInBackground(String... params) {
            mAdapter.flushData();
            
            String setOrGet = params[0];

            BLStdControlParam stdControlParam = new BLStdControlParam();
            stdControlParam.setAct(setOrGet);
            final ArrayList<String> dnaParams = new ArrayList<>();
            
            for(DnaParam item : mDnaParams){
                dnaParams.add(item.param);
                ArrayList<BLStdData.Value> dnaVals = new ArrayList<>();
                BLStdData.Value value = new BLStdData.Value();
                if(item.val == null){
                    value.setVal("0");
                }else{
                    try {
                        final int intVal = Integer.parseInt(item.val);
                        value.setVal(intVal);
                    } catch (NumberFormatException e) {
                        value.setVal(item.val);
                    }
                }
                dnaVals.add(value);
                stdControlParam.getVals().add(dnaVals);
            }
            stdControlParam.getParams().addAll(dnaParams);
            
            String devDid = TextUtils.isEmpty(mDNADevice.getpDid()) ? mDNADevice.getDid() : mDNADevice.getpDid();
           String subDid = TextUtils.isEmpty(mDNADevice.getpDid())  ? null : mDNADevice.getDid();
            
            return BLLet.Controller.dnaControl(devDid, subDid, stdControlParam);
        }

        @Override
        protected void onPostExecute(BLStdControlResult result) {
            super.onPostExecute(result);
            dismissProgressDialog();
            showResult(result);
        }
    }


    //脚本版本查询
    class QueryScriptVersionTask extends AsyncTask<Void, Void, BLQueryResoureVersionResult>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Query Script Version...");
        }

        @Override
        protected BLQueryResoureVersionResult doInBackground(Void... params) {
            return BLLet.Controller.queryScriptVersion(mDNADevice.getPid());
        }

        @Override
        protected void onPostExecute(BLQueryResoureVersionResult result) {
            super.onPostExecute(result);
            dismissProgressDialog();
            showResult(result);
        }
    }

    //UI版本查询
    class QueryUIVersionTask extends AsyncTask<Void, Void, BLQueryResoureVersionResult>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Query UI Version...");
        }

        @Override
        protected BLQueryResoureVersionResult doInBackground(Void... params) {
            return BLLet.Controller.queryUIVersion(mDNADevice.getPid());
        }

        @Override
        protected void onPostExecute(BLQueryResoureVersionResult result) {
            super.onPostExecute(result);
            dismissProgressDialog();
            showResult(result);
        }
    }

    //脚本下载
    class DownLoadScriptTask extends AsyncTask<Void, Void, BLDownloadScriptResult>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Downloading Script...");
        }

        @Override
        protected BLDownloadScriptResult doInBackground(Void... params) {
            String pid = mDNADevice.getPid();
            return BLLet.Controller.downloadScript(pid);
        }

        @Override
        protected void onPostExecute(BLDownloadScriptResult result) {
            super.onPostExecute(result);
            dismissProgressDialog();
            showResult(result);
        }
    }

    //UI包下载
    class DownLoadUITask extends AsyncTask<Void, Void, BLDownloadUIResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Downloading UI...");
        }

        @Override
        protected BLDownloadUIResult doInBackground(Void... params) {
            String pid = mDNADevice.getPid();
            return BLLet.Controller.downloadUI(pid);
        }

        @Override
        protected void onPostExecute(BLDownloadUIResult result) {
            super.onPostExecute(result);
            dismissProgressDialog();
            showResult(result);
        }
    }

}
