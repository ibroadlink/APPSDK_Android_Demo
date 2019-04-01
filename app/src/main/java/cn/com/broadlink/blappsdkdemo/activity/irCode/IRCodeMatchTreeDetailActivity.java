package cn.com.broadlink.blappsdkdemo.activity.irCode;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.base.BLCommonTools;
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.data.RmIrTreeResult;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseRecyclerAdapter;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseViewHolder;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.divideritemdecoration.BLDividerUtil;
import cn.com.broadlink.ircode.BLIRCode;
import cn.com.broadlink.ircode.result.BLDownLoadIRCodeResult;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;
import cn.com.broadlink.sdk.data.controller.BLStdData;
import cn.com.broadlink.sdk.param.controller.BLStdControlParam;
import cn.com.broadlink.sdk.result.controller.BLStdControlResult;

public class IRCodeMatchTreeDetailActivity extends TitleActivity {
    private RecyclerView mRvContent;
    private TextView mTvDevice;
    private TextView mTvInfo;
    private RmIrTreeResult.IrTree mIrTreeResult;
    private ArrayList<RmIrTreeResult.IrCode> mData = new ArrayList<>();
    private DnaParamAdapter mAdapter;
    private String mSavePath;
    private int mDeviceType;
    private BLDNADevice mDev = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ir_match_tree_detail);
        setBackWhiteVisible();
        setTitle("Match Tree Detail");

        mIrTreeResult = (RmIrTreeResult.IrTree) getIntent().getSerializableExtra(BLConstants.INTENT_SERIALIZABLE);
        mDeviceType = getIntent().getIntExtra(BLConstants.INTENT_VALUE, BLConstants.BL_IRCODE_DEVICE_AC);
        mDev = getIntent().getParcelableExtra(BLConstants.INTENT_DEVICE);
        
        if (mIrTreeResult == null || mDev == null) {
            BLToastUtils.show("Invalid param");
            back();
            return;
        }

        initData();

        findView();

        initView();

        setListener();

    }

    private void setListener() {
        mAdapter.setOnItemClickListener(new BLBaseRecyclerAdapter.OnClickListener() {
            @Override
            public void onClick(int position, int viewType) {
                
                final RmIrTreeResult.IrCode irCode = mData.get(position);
                
                if( irCode.getCode()==null || irCode.getCode().length==0){
                    if(irCode.getChirdren().getCodeList()!=null){
                        gotoMatchTreePage( irCode.getChirdren());
                    }else{
                        if(!TextUtils.isEmpty(irCode.getIrcodeid())){
                            downloadScript(irCode.getIrcodeid());
                        }else{
                            BLToastUtils.show("No Ircode Content!");
                        }
                    }
                }else{
                    new DnaControlTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR, irCode);
                }
            }
        });
    }

    private void initView() {
        mAdapter = new DnaParamAdapter();
        mRvContent.setLayoutManager(new LinearLayoutManager(mActivity));
        mRvContent.setAdapter(mAdapter);
        mRvContent.addItemDecoration(BLDividerUtil.getDefault(mActivity, mData));

        mTvDevice.setText(JSON.toJSONString(mIrTreeResult, true));
        
        final SpannableString tip = new SpannableString("Function Name: " + mIrTreeResult.getKey() + "\n\n" + "Click to send ircode:");
        tip.setSpan(new ForegroundColorSpan(Color.RED), 15, 15+ mIrTreeResult.getKey().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tip.setSpan(new AbsoluteSizeSpan(40,true), 15, 15+ mIrTreeResult.getKey().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvInfo.setText(tip);
    }

    private void initData() {
        final List<RmIrTreeResult.IrCode> ircodeid = mIrTreeResult.getCodeList();
        if (ircodeid != null && !ircodeid.isEmpty()) {
            mData.addAll(ircodeid);
        }
    }

    private void findView() {
        mRvContent = (RecyclerView) findViewById(R.id.rv_content);
        mTvDevice = (TextView) findViewById(R.id.tv_device);
        mTvInfo = (TextView) findViewById(R.id.tv_info);
    }

    private void downloadScript(String irCodeId){
       String mSavePath = BLLet.Controller.queryIRCodePath() + File.separator + irCodeId;
        new DownLoadScriptTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR, irCodeId, mSavePath);
    }

    private void goToNextActivity() {
        if (mDeviceType == BLConstants.BL_IRCODE_DEVICE_AC) {
            BLCommonUtils.toActivity(mActivity, IRCodeAcPanelActivity.class, mSavePath);
        }else{
            BLCommonUtils.toActivity(mActivity, IRCodeTvOrBoxPanelActivity.class, mSavePath);
        }
    }
    
    private void gotoMatchTreePage(RmIrTreeResult.IrTree data) {
        final Intent intent = new Intent(mActivity, IRCodeMatchTreeDetailActivity.class);
        intent.putExtra(BLConstants.INTENT_SERIALIZABLE, data);
        intent.putExtra(BLConstants.INTENT_VALUE, mDeviceType);
        intent.putExtra(BLConstants.INTENT_DEVICE, mDev);
        startActivity(intent);
    }

    class DnaParamAdapter extends BLBaseRecyclerAdapter<RmIrTreeResult.IrCode> {

        public DnaParamAdapter() {
            super(mData, R.layout.item_family_room);
        }

        @Override
        public void onBindViewHolder(BLBaseViewHolder holder, final int position) {
            super.onBindViewHolder(holder, position);
            final String ircodeid = mBeans.get(position).getIrcodeid();
            holder.setText(R.id.tv_name, TextUtils.isEmpty(ircodeid) ? "ircode has children " + position : ircodeid);
            holder.setVisible(R.id.tv_mac, false);
        }
    }

    /**
     * 发送红码
     */
    class DnaControlTask extends AsyncTask<RmIrTreeResult.IrCode, Void, BLStdControlResult> {
        private RmIrTreeResult.IrCode irCode = null;
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Controlling...");
        }

        @Override
        protected BLStdControlResult doInBackground(RmIrTreeResult.IrCode... params) {
            irCode = params[0];
            
            ArrayList<BLStdData.Value> dnaVals = new ArrayList<>();
            final ArrayList<String> dnaParams = new ArrayList<>();

            dnaParams.add("irda");
            BLStdData.Value value = new BLStdData.Value();
            value.setVal(BLCommonTools.bytes2HexString(irCode.getCode()));
            dnaVals.add(value);

            BLStdControlParam stdControlParam = new BLStdControlParam();
            stdControlParam.setAct("set");
            stdControlParam.getParams().addAll(dnaParams);
            stdControlParam.getVals().add(dnaVals);

            return BLLet.Controller.dnaControl(mDev.getDid(), null, stdControlParam);
        }

        @Override
        protected void onPostExecute(BLStdControlResult result) {
            super.onPostExecute(result);
            dismissProgressDialog();
            
            if(result!=null && result.succeed()){
                BLAlert.showDialog(mActivity, "Is Tv/Ac Device worked?", new BLAlert.DialogOnClickListener() {
                    @Override
                    public void onPositiveClick() {
                        if(irCode.getChirdren().getCodeList()==null){
                            downloadScript(irCode.getIrcodeid());
                        }else{
                            gotoMatchTreePage(irCode.getChirdren());
                        }
                    }

                    @Override
                    public void onNegativeClick() {

                    }
                });
            }else{
                BLCommonUtils.toastErr(result);
            }
        }
    }


    /**
     * 下载红码脚本
     */
    class DownLoadScriptTask extends AsyncTask<String, Void, BLDownLoadIRCodeResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Downloading...");
        }

        @Override
        protected BLDownLoadIRCodeResult doInBackground(String... strings) {
            return BLIRCode.downloadIRCodeScriptById("", strings[0], strings[1]);
        }

        @Override
        protected void onPostExecute(BLDownLoadIRCodeResult blDownloadScriptResult) {
            super.onPostExecute(blDownloadScriptResult);
            dismissProgressDialog();
            if(blDownloadScriptResult!= null && blDownloadScriptResult.succeed() && blDownloadScriptResult.getSavePath()!=null){
                mSavePath = blDownloadScriptResult.getSavePath();
                goToNextActivity();
            }else{
                BLCommonUtils.toastErr(blDownloadScriptResult);
            }
        }
    }
}
