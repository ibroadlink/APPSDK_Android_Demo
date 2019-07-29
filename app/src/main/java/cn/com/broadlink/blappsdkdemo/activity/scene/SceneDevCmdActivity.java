package cn.com.broadlink.blappsdkdemo.activity.scene;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.base.fastjson.BLJSON;
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLLog;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.data.BLDevProfileInfo;
import cn.com.broadlink.blappsdkdemo.data.BLDevProfileInftsValueInfo;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.BLListAlert;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseRecyclerAdapter;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseViewHolder;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.divideritemdecoration.BLDividerUtil;
import cn.com.broadlink.blsfamily.bean.endpoint.BLSEndpointInfo;
import cn.com.broadlink.blsfamily.bean.scene.BLSSceneContentInfo;
import cn.com.broadlink.blsfamily.bean.scene.BLSSceneDevItemInfo;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLStdData;
import cn.com.broadlink.sdk.result.controller.BLProfileStringResult;

public class SceneDevCmdActivity extends TitleActivity implements View.OnClickListener {

    private RecyclerView mRvParams;
    private Button mSelectParam;
    private EditText mTvInput;
    private Button mGetProfile;
    private BLSEndpointInfo mDNADevice;

    private List<DnaParam> mDnaParams = new ArrayList<>();
    private DnaParamAdapter mAdapter;
    private BLDevProfileInfo mBlDevProfileInfo;
    private BLSSceneDevItemInfo mSceneContentInfo;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_dev_cmd);
        setBackWhiteVisible();
        setTitle("Scene Dev Cmd");

        initData();

        findView();

        initView();

        setListener();
    }

    private void initData() {
        mSceneContentInfo = getIntent().getParcelableExtra(BLConstants.INTENT_PARCELABLE);
        mDNADevice = getIntent().getParcelableExtra(BLConstants.INTENT_DEVICE);

        assert mDNADevice != null;

        if(mSceneContentInfo == null){
            mSceneContentInfo = new BLSSceneDevItemInfo();
            mSceneContentInfo.setEndpointId(mDNADevice.getEndpointId());
            mSceneContentInfo.setGatewayId(mDNADevice.getGatewayId());
        }

        final BLSSceneContentInfo contentInfo = mSceneContentInfo.getContentInfo();
        if (contentInfo != null) {
            final List<BLStdData> cmdParamList = contentInfo.getCmdParamList();
            if(cmdParamList != null && cmdParamList.size()>0){
                final BLStdData blStdData = cmdParamList.get(0);
                final ArrayList<String> params = blStdData.getParams();
                for (int i = 0; i < params.size(); i++) {
                    final DnaParam dnaParam = new DnaParam();
                    dnaParam.param = params.get(i);
                    dnaParam.val =  String.valueOf(blStdData.getVals().get(i).get(0).getVal());
                    mDnaParams.add(dnaParam);
                }
            }
        }
    }
    
    private void findView() {
        mRvParams = (RecyclerView) findViewById(R.id.rv_params);
        mSelectParam = (Button) findViewById(R.id.bt_select_param);
        mGetProfile = (Button) findViewById(R.id.bt_profile);
        mTvInput = (EditText) findViewById(R.id.et_input);
    }
    
    private void initView() {
        mAdapter = new DnaParamAdapter();
        mRvParams.setLayoutManager(new LinearLayoutManager(mActivity));
        mRvParams.setAdapter(mAdapter);
        mRvParams.addItemDecoration(BLDividerUtil.getDefault(mActivity, mDnaParams));
    }

    private void setListener() {
        mSelectParam.setOnClickListener(this);
        mGetProfile.setOnClickListener(this);


        setRightButtonOnClickListener("Save", getResources().getColor(R.color.bl_yellow_main_color), new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                final BLStdData stdData = getStdData();
                final ArrayList<BLStdData> blStdData = new ArrayList<>();
                blStdData.add(stdData);

                BLSSceneContentInfo contentInfo = mSceneContentInfo.getContentInfo();
                if(contentInfo == null){
                    contentInfo = new BLSSceneContentInfo();
                }
                contentInfo.setCmdParamList(blStdData);

                mSceneContentInfo.setContent(BLJSON.toJSONString(contentInfo));

                BLLog.d("scene_cmd", "mSceneDevItemInfo" + BLJSON.toJSONString(mSceneContentInfo, true));
                final Intent intent = new Intent();
                intent.putExtra(BLConstants.INTENT_PARCELABLE, mSceneContentInfo);
                setResult(RESULT_OK, intent);
                back();
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_select_param:
                    showAddParamDialogV2();
                break;
                
            case R.id.bt_profile:
                queryDevProfile();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!scriptFileExist() ||  !uiFileExit()) {
            new DownLoadResTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
        }
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
    
    private void showAddParamDialogV2(){
        mAdapter.flushData();

        if (!queryDevProfile()) {
            return;
        }
        
        final List<String> params = mBlDevProfileInfo.getSuids().get(0).getIntfsList();
        ArrayList<String> filterdParamList = new ArrayList<>();
        
        for (String param : params) {
            final List<BLDevProfileInftsValueInfo> intfValue = mBlDevProfileInfo.getSuids().get(0).getIntfValue(param);
            if(intfValue != null && intfValue.size()>0){
                final int ifttt = intfValue.get(0).getIfttt();
                final int act = intfValue.get(0).getAct();
                if(ifttt>0 || act>1){
                    filterdParamList.add(param);
                }
            }
        }
        
        if(filterdParamList.size()==0){
            BLToastUtils.show("No param can support scene.");
            return;
        }
        
        final String[] paramArray = filterdParamList.toArray(new String[filterdParamList.size()]);

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

    private boolean queryDevProfile(){
        if(mBlDevProfileInfo != null){
            showResult(mBlDevProfileInfo);
            return true;
        }

        BLProfileStringResult devProfileResult = BLLet.Controller.queryProfileByPid(mDNADevice.getProductId());
        if(devProfileResult!=null && devProfileResult.succeed()){
            mBlDevProfileInfo = JSON.parseObject(devProfileResult.getProfile(), BLDevProfileInfo.class);
            showResult(mBlDevProfileInfo);
            return true;
        }else{
            showResult(devProfileResult);
            BLToastUtils.show("Please confirm script had been downloaded!");
            return false;
        }
    }

    private BLStdData getStdData() {
        mAdapter.flushData();
        
        BLStdData stdControlParam = new BLStdData();
        stdControlParam.setAct("set");
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
                    try { 
                        final JSONObject jsonObject = JSON.parseObject(item.val);
                        value.setVal(jsonObject);
                    } catch (JSONException e1) {
                        value.setVal(item.val);
                    }
                }
            }
            dnaVals.add(value);
            stdControlParam.getVals().add(dnaVals);
        }
        stdControlParam.getParams().addAll(dnaParams);
        return stdControlParam;
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

                    if (mBeans.get(position).in == null || mBeans.get(position).in.size()==0) {
                        BLLog.d("scene_cmd", mBeans.get(position).param + "in is null" );
                        if (!queryDevProfile()) {
                            return false;
                        }
                        final List<BLDevProfileInftsValueInfo> intfValue = mBlDevProfileInfo.getSuids().get(0).getIntfValue(mBeans.get(position).param);
                        if(intfValue != null && intfValue.size()>0){
                            final List<Integer> in = intfValue.get(0).getIn();
                            mBeans.get(position).in.addAll(in);
                        }
                        BLLog.d("scene_cmd", mBeans.get(position).param + "in is " +  mBeans.get(position).in);
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
    
    
    private boolean scriptFileExist(){
        String scriptFilePath = BLLet.Controller.queryScriptPath(mDNADevice.getProductId());
        Log.e("FileExist" , scriptFilePath);
        File file = new File(scriptFilePath);
        return file.exists();
    }

    private boolean uiFileExit() {
        String uiFilePath = BLLet.Controller.queryUIPath(mDNADevice.getProductId());
        Log.e("UIExit", uiFilePath);
        File file = new File(uiFilePath);
        return file.exists();
    }
    
    //脚本和ui包下载
    class DownLoadResTask extends AsyncTask<String, Void, BLBaseResult> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mActivity);
            progressDialog.setMessage("Resource downloading...");
            progressDialog.show();
        }

        @Override
        protected BLBaseResult doInBackground(String... params) {
            BLBaseResult result = null;

            if (!scriptFileExist()){
                result = BLLet.Controller.downloadScript(mDNADevice.getProductId());
                if(result==null || !result.succeed()){
                    return result;
                }
            }

            if (!uiFileExit()){
                result = BLLet.Controller.downloadUI(mDNADevice.getProductId());
            }
            return result;
        }

        @Override
        protected void onPostExecute(BLBaseResult result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            BLCommonUtils.toastErr(result);
            if (scriptFileExist() && uiFileExit()) {
                queryDevProfile();
            }else{
                BLAlert.showDialog(mActivity, "Script Or Ui Download Fail, Retry ?", new BLAlert.DialogOnClickListener() {
                    @Override
                    public void onPositiveClick() {
                        new DownLoadResTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
                    }

                    @Override
                    public void onNegativeClick() {
                        back();
                        setResult(RESULT_CANCELED);
                    }
                }); 
            }
        }
    }


}
