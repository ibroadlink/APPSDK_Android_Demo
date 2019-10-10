package cn.com.broadlink.blappsdkdemo.activity.device;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.base.fastjson.BLJSON;
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLPidConstants;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.data.BLDevProfileInfo;
import cn.com.broadlink.blappsdkdemo.intferfacer.BaseCallback;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.BLListAlert;
import cn.com.broadlink.blappsdkdemo.view.BLMultListAlert;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseRecyclerAdapter;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseViewHolder;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.divideritemdecoration.BLDividerUtil;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;
import cn.com.broadlink.sdk.data.controller.BLGroupSubConfigInfo;
import cn.com.broadlink.sdk.data.controller.BLGroupSubDeviceInfo;
import cn.com.broadlink.sdk.result.controller.BLProfileStringResult;

public class DevGroupDevEditActivity extends TitleActivity {

    private RecyclerView mRvList;
    private LinearLayout mLlName;
    private LinearLayout mLlTarget;
    private TextView mTvTarget;
    private Button mBtAdd;

    private List<BLDNADevice> mSubDeviceList = new ArrayList<>();
    private List<BLGroupSubDeviceInfo> mGroupSubDevList = new ArrayList<>();
    private SubDevAdapter mAdapter;
    private String mPid;
    private Map<String, BLDevProfileInfo> mDevProfileMap = new HashMap<>();
    private BLGroupSubConfigInfo mSubConfigInfo;
    private int mPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_group_dev_edit);
        setBackWhiteVisible();
        setTitle("Group Keys Edit");

        mSubDeviceList = getIntent().getParcelableArrayListExtra(BLConstants.INTENT_ARRAY);
        mPid = getIntent().getStringExtra(BLConstants.INTENT_ID);
        mSubConfigInfo = getIntent().getParcelableExtra(BLConstants.INTENT_MODULE);
        mPosition = getIntent().getIntExtra(BLConstants.INTENT_VALUE, 0);
        
        if(mSubConfigInfo == null){
            mSubConfigInfo = new BLGroupSubConfigInfo();
        }

        findView();

        initView();

        setListener();
    }


    private void findView() {
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
        mLlName = (LinearLayout) findViewById(R.id.ll_name);
        mLlTarget = (LinearLayout) findViewById(R.id.ll_target);
        mTvTarget = (TextView) findViewById(R.id.tv_target);
        mBtAdd = (Button) findViewById(R.id.bt_add);
    }

    private void initView() {
        mAdapter = new SubDevAdapter();
        mRvList.setLayoutManager(new LinearLayoutManager(mActivity));
        mRvList.setAdapter(mAdapter);
        mRvList.addItemDecoration(BLDividerUtil.getDefault(mActivity, mGroupSubDevList));

        mLlName.setVisibility(View.GONE);
        mTvTarget.setText(mSubConfigInfo.getTarget());

        mGroupSubDevList.addAll(mSubConfigInfo.getList());
    }

    private void setListener() {

        setRightButtonOnClickListener("Save", getResources().getColor(R.color.bl_yellow_main_color), new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                
                if(TextUtils.isEmpty(mTvTarget.getText())){
                    BLToastUtils.show("Select target first.");
                    return;
                }

                if (mGroupSubDevList == null || mGroupSubDevList.isEmpty()) {
                    BLToastUtils.show("Add at least a sub device.");
                    return;
                }

                mSubConfigInfo.setTarget(mTvTarget.getText().toString());
                mSubConfigInfo.setList(mGroupSubDevList);

                final Intent intent = new Intent();
                intent.putExtra(BLConstants.INTENT_PARCELABLE, mSubConfigInfo);
                intent.putExtra(BLConstants.INTENT_VALUE, mPosition);
                intent.putExtra(BLConstants.INTENT_ID, mPid);
                setResult(RESULT_OK, intent);
                back();
            }
        });

        
        mLlTarget.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                if(TextUtils.isEmpty(mPid)){
                    BLAlert.showEditDilog(mActivity, "Please input pid", TextUtils.isEmpty(mPid) ? BLPidConstants.PID_GROUP : mPid, new BLAlert.BLEditDialogOnClickListener() {
                        @Override
                        public void onClink(String value) {
                            
                            mPid = value;
                            
                            selectTarget(mPid, new BaseCallback<List<String>>() {
                                @Override
                                public void onResult(List<String> params) {

                                    final String[] paramArray = params.toArray(new String[params.size()]);
                                    BLListAlert.showAlert(mActivity, null, paramArray, new BLListAlert.OnItemClickLister() {
                                        @Override
                                        public void onClick(int whichButton) {
                                            mTvTarget.setText(paramArray[whichButton]);
                                        }
                                    });
                                }
                            });
                        }

                        @Override
                        public void onCancel(String value) { }
                    }, false);
                }else{
                    selectTarget(mPid, new BaseCallback<List<String>>() {
                        @Override
                        public void onResult(List<String> params) {

                            final String[] paramArray = params.toArray(new String[params.size()]);
                            BLListAlert.showAlert(mActivity, null, paramArray, new BLListAlert.OnItemClickLister() {
                                @Override
                                public void onClick(int whichButton) {
                                    mTvTarget.setText(paramArray[whichButton]);
                                }
                            });
                        }
                    });
                }
            }
        });

        mAdapter.setOnItemLongClickListener(new BLBaseRecyclerAdapter.OnLongClickListener() {
            @Override
            public boolean onLongClick(final int position, int viewType) {
                BLAlert.showDialog(mActivity, "Confirm to delete this sub device?", new BLAlert.DialogOnClickListener() {
                    @Override
                    public void onPositiveClick() {
                        mGroupSubDevList.remove(position);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onNegativeClick() {

                    }
                });
                return true;
            }
        });

        mBtAdd.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                
                if(mSubDeviceList == null || mSubDeviceList.isEmpty()){
                 BLToastUtils.show("No sub device found, add one first.");   
                 return;
                }
                List<String> subDeviceDidList = new ArrayList<>();
                if(subDeviceDidList.isEmpty()){
                    for (BLDNADevice item : mSubDeviceList) {
                        subDeviceDidList.add(item.getName() + "\n" + item.getDid());
                    }
                }

                String[] menu = subDeviceDidList.toArray(new String[subDeviceDidList.size()]);
                BLListAlert.showAlert(mActivity, null, menu, new BLListAlert.OnItemClickLister() {
                    @Override
                    public void onClick(final int whichButton) {
                        selectTarget(mSubDeviceList.get(whichButton).getPid(), new BaseCallback<List<String>>() {
                            @Override
                            public void onResult(List<String> params) {

                                BLMultListAlert.showAlert(mActivity, "Select Keys", params, new BLMultListAlert.OnItemSelectedLister() {
                                    @Override
                                    public void onClick(List<String> selectedList) {
                                        final BLGroupSubDeviceInfo groupSubDeviceInfo = new BLGroupSubDeviceInfo();
                                        groupSubDeviceInfo.setDid(mSubDeviceList.get(whichButton).getDid());
                                        final ArrayList<String> keyList = new ArrayList<>();
                                        keyList.addAll(selectedList);
                                        groupSubDeviceInfo.setKey(keyList);
                                        mGroupSubDevList.add(groupSubDeviceInfo);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
        
    }

    class SubDevAdapter extends BLBaseRecyclerAdapter<BLGroupSubDeviceInfo> {

        public SubDevAdapter() {
            super(mGroupSubDevList, R.layout.item_family_room);
        }

        @Override
        public void onBindViewHolder(BLBaseViewHolder holder, final int position) {
            super.onBindViewHolder(holder, position);
            holder.setText(R.id.tv_name, "Did: " + mBeans.get(position).getDid());
            holder.setText(R.id.tv_mac,"Key:" + BLJSON.toJSONString(mBeans.get(position).getKey()));
        }
    }

    
    private void selectTarget(String pid, BaseCallback<List<String>> callback){
        if(scriptFileExist(pid)){
            showAddParamDialog(pid, callback); 
        }else{
            new DownLoadResTask(pid, callback).executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
        }
    }


    private boolean scriptFileExist(String pid){
        String scriptFilePath = BLLet.Controller.queryScriptPath(pid);
        Log.e("scriptFileExist" , scriptFilePath);
        File file = new File(scriptFilePath);
        return file.exists();
    }

    private void showAddParamDialog(String pid, final BaseCallback<List<String>> callback){

        if(!mDevProfileMap.containsKey(pid)){
            BLProfileStringResult devProfileResult = BLLet.Controller.queryProfileByPid(pid);
            if(devProfileResult!=null && devProfileResult.succeed()){
                final BLDevProfileInfo blDevProfileInfo = JSON.parseObject(devProfileResult.getProfile(), BLDevProfileInfo.class);
                if(blDevProfileInfo != null){
                    mDevProfileMap.put(pid, blDevProfileInfo);
                }
            }else{
                BLToastUtils.show("Please confirm script had been downloaded!");
                return;
            }
        }
        final List<String> params = mDevProfileMap.get(pid).getSuids().get(0).getIntfsList();

        if(callback != null){
            callback.onResult(params);
        }
    }

    //脚本下载
    class DownLoadResTask extends AsyncTask<String, Void, BLBaseResult> {
        private ProgressDialog progressDialog;
        private String pid;
        private BaseCallback<List<String>> callback;

        public DownLoadResTask(String pid, BaseCallback<List<String>> callback) {
            this.pid = pid;
            this.callback = callback;
        }
        
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
            
            if (!scriptFileExist(pid)){
                result = BLLet.Controller.downloadScript(pid);
                if(result==null || !result.succeed()){
                    return result;
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(BLBaseResult result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            BLCommonUtils.toastErr(result);

            if (scriptFileExist(pid)) {
                showAddParamDialog(pid, callback);
            }
        }
    }
}
