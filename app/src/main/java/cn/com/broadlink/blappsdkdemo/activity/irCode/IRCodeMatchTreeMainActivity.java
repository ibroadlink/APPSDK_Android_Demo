package cn.com.broadlink.blappsdkdemo.activity.irCode;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.data.RmIrTreeResult;
import cn.com.broadlink.blappsdkdemo.service.BLLocalDeviceManager;
import cn.com.broadlink.blappsdkdemo.view.BLListAlert;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseRecyclerAdapter;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseViewHolder;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.divideritemdecoration.BLDividerUtil;
import cn.com.broadlink.ircode.BLIRCode;
import cn.com.broadlink.ircode.result.BLDownLoadIRCodeResult;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;

public class IRCodeMatchTreeMainActivity extends TitleActivity {
    private RecyclerView mRvContent;
    private TextView mTvDevice;
    private Button mBtMatchTree;
    private RmIrTreeResult mIrTreeResult;
    private ArrayList<String> mData = new ArrayList<>();
    private DnaParamAdapter mAdapter;
    private String mSavePath;
    private int mDeviceType;
    private BLDNADevice mDev = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ir_match_tree);
        setBackWhiteVisible();
        setTitle("Match Tree");

        mIrTreeResult = (RmIrTreeResult) getIntent().getSerializableExtra(BLConstants.INTENT_SERIALIZABLE);
        mDeviceType = getIntent().getIntExtra(BLConstants.INTENT_VALUE, BLConstants.BL_IRCODE_DEVICE_AC);
        
        if (mIrTreeResult == null) {
            BLToastUtils.show("Invalid param");
            back();
            return;
        }

        initData();

        findView();

        initView();
        
        mAdapter.setOnItemClickListener(new BLBaseRecyclerAdapter.OnClickListener() {
            @Override
            public void onClick(int position, int viewType) {
                downloadScript(mData.get(position));
            }
        });

        mBtMatchTree.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                
//                // test start 
//                if (mIrTreeResult.getRespbody().getMatchtree() == null || mIrTreeResult.getRespbody().getMatchtree().getCodeList() == null) {
//                    String jsonStr =  "{\n" + "\t\"error\": 0,\n" + "\t\"msg\": \"ok\",\n" + "\t\"respbody\": {\n" + "\t\t\"hotircode\": {\n" + "\t\t\t\"ircodeid\": [\n" + "\t\t\t\t\"200359\",\n" + "\t\t\t\t\"206928\",\n" + "\t\t\t\t\"207448\"\n" + "\t\t\t]\n" + "\t\t},\n" + "\t\t\"matchtree\": {\n" + "\t\t\t\"codeList\": [{\n" + "\t\t\t\t\t\"chirdren\": {\n" + "\t\t\t\t\t\t\"codeList\": [{\n" + "\t\t\t\t\t\t\t\t\"chirdren\": {\n" + "\t\t\t\t\t\t\t\t\t\"key\": \"\"\n" + "\t\t\t\t\t\t\t\t},\n" + "\t\t\t\t\t\t\t\t\"code\": \"JgBmAG43Dg4OKQ4ODg4ODg4ODg4ODg4ODg4ODg4ODg4OKQ4ODg4ODg4ODg4ODg4ODg4ODg4pDg4ODg4ODg4ODg4ODg4ODg4ODikODg4ODikODg4pDg4ODg4pDg4ODg4pDg4OKQ4pDgAFZA==\",\n" + "\t\t\t\t\t\t\t\t\"ircodeid\": \"200629\"\n" + "\t\t\t\t\t\t\t},\n" + "\t\t\t\t\t\t\t{\n" + "\t\t\t\t\t\t\t\t\"chirdren\": {\n" + "\t\t\t\t\t\t\t\t\t\"key\": \"\"\n" + "\t\t\t\t\t\t\t\t},\n" + "\t\t\t\t\t\t\t\t\"code\": \"JgB4AGJiEDEQMRAxEDEQMRAxEFIQMRAxEDEQMRBSEDEQUhAxEFIQgxAAAvJiYhAxEDEQMRAxEDEQMRBSEDEQMRAxEDEQUhAxEFIQMRBSEIMQAALyYmIQMRAxEDEQMRAxEDEQUhAxEDEQMRAxEFIQMRBSEDEQUhCDEAAC8g==\",\n" + "\t\t\t\t\t\t\t\t\"ircodeid\": \"205695\"\n" + "\t\t\t\t\t\t\t}\n" + "\n" + "\t\t\t\t\t\t],\n" + "\t\t\t\t\t\t\"key\": \"menu\"\n" + "\t\t\t\t\t},\n" + "\t\t\t\t\t\"code\": \"JgBmAG43Dg4OKQ4ODg4ODg4ODg4ODg4ODg4ODg4ODg4OKQ4ODg4ODg4ODg4ODg4ODg4ODg4pDg4ODg4ODg4ODg4ODg4ODg4ODikODg4ODikODg4pDg4ODg4pDg4ODg4pDg4OKQ4pDgAFZA==\",\n" + "\t\t\t\t\t\"ircodeid\": \"\"\n" + "\t\t\t\t},\n" + "\t\t\t\t{\n" + "\t\t\t\t\t\"chirdren\": {\n" + "\t\t\t\t\t\t\"key\": \"\"\n" + "\t\t\t\t\t},\n" + "\t\t\t\t\t\"code\": \"JgB4AGJiEDEQMRAxEDEQMRAxEFIQMRAxEDEQMRBSEDEQUhAxEFIQgxAAAvJiYhAxEDEQMRAxEDEQMRBSEDEQMRAxEDEQUhAxEFIQMRBSEIMQAALyYmIQMRAxEDEQMRAxEDEQUhAxEDEQMRAxEFIQMRBSEDEQUhCDEAAC8g==\",\n" + "\t\t\t\t\t\"ircodeid\": \"205695\"\n" + "\t\t\t\t},\n" + "\t\t\t\t{\n" + "\t\t\t\t\t\"chirdren\": {\n" + "\t\t\t\t\t\t\"key\": \"\"\n" + "\t\t\t\t\t},\n" + "\t\t\t\t\t\"code\": \"JgBkAAABJ5QSEhISEjcSNxI3EjcSEhISEjcSEhISEhISEhI3EjcSEhISEhISEhI3EjcSEhI3EjcSNxI3EhISEhISEjcSNxISEhISEhI3EjcSNxISEhISNxI3EjcSAAL/AAEnlBIADAg=\",\n" + "\t\t\t\t\t\"ircodeid\": \"206515\"\n" + "\t\t\t\t},\n" + "\t\t\t\t{\n" + "\t\t\t\t\t\"chirdren\": {\n" + "\t\t\t\t\t\t\"key\": \"\"\n" + "\t\t\t\t\t},\n" + "\t\t\t\t\t\"code\": \"JgAaAAX0BfQF9AX0BfQF9AWhBfQFoQX0BfQFAAVv\",\n" + "\t\t\t\t\t\"ircodeid\": \"204245\"\n" + "\t\t\t\t},\n" + "\t\t\t\t{\n" + "\t\t\t\t\t\"chirdren\": {\n" + "\t\t\t\t\t\t\"key\": \"\"\n" + "\t\t\t\t\t},\n" + "\t\t\t\t\t\"code\": \"JgBhAG9vHBwcUxxTHFMcHBxTHBwcUxwcHBwcUxxTHBwcHBwcHFMcHBxTHBwcUxxTHBwcAATKb28cHBxTHFMcUxwcHFMcHBxTHBwcHBxTHFMcHBwcHBwcUxwcHFMcHBxTHFMcHBw=\",\n" + "\t\t\t\t\t\"ircodeid\": \"207111\"\n" + "\t\t\t\t},\n" + "\t\t\t\t{\n" + "\t\t\t\t\t\"chirdren\": {\n" + "\t\t\t\t\t\t\"key\": \"\"\n" + "\t\t\t\t\t},\n" + "\t\t\t\t\t\"code\": \"JgBhAG9vHBwcHBwcHBwcHBxTHBwcUxwcHBwcUxxTHFMcUxxTHFMcHBxTHBwcUxxTHBwcAATKb28cHBwcHBwcHBwcHFMcHBxTHBwcHBxTHFMcUxxTHFMcUxwcHFMcHBxTHFMcHBw=\",\n" + "\t\t\t\t\t\"ircodeid\": \"206860\"\n" + "\t\t\t\t},\n" + "\t\t\t\t{\n" + "\t\t\t\t\t\"chirdren\": {\n" + "\t\t\t\t\t\t\"key\": \"\"\n" + "\t\t\t\t\t},\n" + "\t\t\t\t\t\"code\": \"JgAaAB0dHR06HR0dHR0dOh0dHR06HR0dHR0dAArS\",\n" + "\t\t\t\t\t\"ircodeid\": \"204383\"\n" + "\t\t\t\t},\n" + "\t\t\t\t{\n" + "\t\t\t\t\t\"chirdren\": {\n" + "\t\t\t\t\t\t\"key\": \"\"\n" + "\t\t\t\t\t},\n" + "\t\t\t\t\t\"code\": \"JgCFAAk9CRoJGgkaCRoJGgkaCRoJGgkaCT0JGgkaCT0JGgkABeQJPQkaCRoJGgkaCT0JPQk9CT0JPQkaCT0JPQkaCT0JAAVICT0JGgkaCRoJGgkaCRoJGgkaCRoJPQkaCRoJPQkaCQAF5Ak9CRoJGgkaCRoJPQk9CT0JPQk9CRoJPQk9CRoJPQk=\",\n" + "\t\t\t\t\t\"ircodeid\": \"200852\"\n" + "\t\t\t\t},\n" + "\t\t\t\t{\n" + "\t\t\t\t\t\"chirdren\": {\n" + "\t\t\t\t\t\t\"key\": \"\"\n" + "\t\t\t\t\t},\n" + "\t\t\t\t\t\"code\": \"JgBGAJSUEhISEhISEjcSEhISEhISEhISEhISEhI3EhISEhISEhISEhISEhISEhISEhISEhISEjcSNxI3EjcSNxI3EjcSNxIABpU=\",\n" + "\t\t\t\t\t\"ircodeid\": \"206974\"\n" + "\t\t\t\t},\n" + "\t\t\t\t{\n" + "\t\t\t\t\t\"chirdren\": {\n" + "\t\t\t\t\t\t\"key\": \"\"\n" + "\t\t\t\t\t},\n" + "\t\t\t\t\t\"code\": \"JgBwAAABFIoRNBE0ERERERERERERERERERERNBERETQRNBE0ETQREREAAsMRNBE0ERERERERERERERERERERNBERETQRNBE0ETQREREAAsMRNBE0ERERERERERERERERERERNBERETQRNBE0ETQREREAAsM=\",\n" + "\t\t\t\t\t\"ircodeid\": \"202709\"\n" + "\t\t\t\t},\n" + "\t\t\t\t{\n" + "\t\t\t\t\t\"chirdren\": {\n" + "\t\t\t\t\t\t\"key\": \"\"\n" + "\t\t\t\t\t},\n" + "\t\t\t\t\t\"code\": \"JgBQAAABJ5QSEhISEhISEhISEhISNxISEjcSNxI3EjcSNxI3EhISNxI3EjcSEhI3EjcSEhI3EhISEhISEjcSEhISEjcSEhI3EgAFJQABJ0oSAAxS\",\n" + "\t\t\t\t\t\"ircodeid\": \"206554\"\n" + "\t\t\t\t},\n" + "\t\t\t\t{\n" + "\t\t\t\t\t\"chirdren\": {\n" + "\t\t\t\t\t\t\"key\": \"\"\n" + "\t\t\t\t\t},\n" + "\t\t\t\t\t\"code\": \"JgBQAAABJ5QSEhISEhISEhISEhISNxISEjcSNxI3EjcSNxI3EhISNxISEhISEhI3EjcSEhISEhISNxI3EjcSEhISEjcSNxI3EgAFJQABJ0oSAAxS\",\n" + "\t\t\t\t\t\"ircodeid\": \"206712\"\n" + "\t\t\t\t}\n" + "\t\t\t],\n" + "\t\t\t\"key\": \"menu\"\n" + "\t\t},\n" + "\t\t\"nobyteircode\": {\n" + "\n" + "\t\t}\n" + "\t},\n" + "\t\"status\": 0,\n" + "\t\"success\": true\n" + "}";
//
//                    mIrTreeResult = JSON.parseObject(jsonStr, RmIrTreeResult.class);
//                }
//                // test end
                
                if (mIrTreeResult.getRespbody().getMatchtree() == null || mIrTreeResult.getRespbody().getMatchtree().getCodeList() == null) {
                    BLToastUtils.show("Match tree is empty!");
                    return;
                }

                if (mDev == null) {
                    final List<BLDNADevice> devicesAddInSDK = BLLocalDeviceManager.getInstance().getDevicesAddInSDK();
                    if (devicesAddInSDK != null && !devicesAddInSDK.isEmpty()) {
                        final String[] dids = new String[devicesAddInSDK.size()];
                        for (int i = 0; i < devicesAddInSDK.size(); i++) {
                            dids[i] = devicesAddInSDK.get(i).getDid();
                        }
                        BLListAlert.showAlert(mActivity, "Select a rm device", dids, new BLListAlert.OnItemClickLister() {
                            @Override
                            public void onClick(int whichButton) {
                                mDev = devicesAddInSDK.get(whichButton);
                                gotoMatchTreePage();
                            }
                        });
                    } else {
                        BLToastUtils.show("Devices added to sdk is empty, add one first!");
                    }
                } else {
                    gotoMatchTreePage();
                }
            }
        });
    }


    private void gotoMatchTreePage() {
        final Intent intent = new Intent(mActivity, IRCodeMatchTreeDetailActivity.class);
        intent.putExtra(BLConstants.INTENT_SERIALIZABLE, mIrTreeResult.getRespbody().getMatchtree());
        intent.putExtra(BLConstants.INTENT_VALUE, mDeviceType);
        intent.putExtra(BLConstants.INTENT_DEVICE, mDev);
        startActivity(intent);
    }
    
    private void initView() {
        mAdapter = new DnaParamAdapter();
        mRvContent.setLayoutManager(new LinearLayoutManager(mActivity));
        mRvContent.setAdapter(mAdapter);
        mRvContent.addItemDecoration(BLDividerUtil.getDefault(mActivity, mData));

        mTvDevice.setText(JSON.toJSONString(mIrTreeResult, true));
    }

    private void initData() {
        final List<String> ircodeid = mIrTreeResult.getRespbody().getHotircode().getIrcodeid();
        if (ircodeid != null && !ircodeid.isEmpty()) {
            mData.addAll(ircodeid);
        }

        final List<String> option = mIrTreeResult.getRespbody().getNobyteircode().getIrcodeid();
        if (option != null && !option.isEmpty()) {
            mData.addAll(option);
        }
    }

    private void findView() {
        mRvContent = (RecyclerView) findViewById(R.id.rv_content);
        mTvDevice = (TextView) findViewById(R.id.tv_device);
        mBtMatchTree = (Button) findViewById(R.id.bt_match_tree);
    }


    class DnaParamAdapter extends BLBaseRecyclerAdapter<String> {

        public DnaParamAdapter() {
            super(mData, R.layout.item_family_room);
        }

        @Override
        public void onBindViewHolder(BLBaseViewHolder holder, final int position) {
            super.onBindViewHolder(holder, position);
            holder.setText(R.id.tv_name, mBeans.get(position));
            holder.setVisible(R.id.tv_mac, false);
        }
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
