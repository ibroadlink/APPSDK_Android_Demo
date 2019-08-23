package cn.com.broadlink.blappsdkdemo.activity.device;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.base.fastjson.BLJSON;
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLMultDidUtils;
import cn.com.broadlink.blappsdkdemo.data.BLDevProfileInfo;
import cn.com.broadlink.blappsdkdemo.db.dao.BLDeviceInfoDao;
import cn.com.broadlink.blappsdkdemo.db.data.BLDeviceInfo;
import cn.com.broadlink.blappsdkdemo.service.BLLocalDeviceManager;
import cn.com.broadlink.blappsdkdemo.service.BLLocalFamilyManager;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseRecyclerAdapter;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseViewHolder;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.divideritemdecoration.BLDividerUtil;
import cn.com.broadlink.family.BLFamily;
import cn.com.broadlink.family.result.BLPrivateDataIdResult;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;
import cn.com.broadlink.sdk.data.controller.BLGroupDeviceInfo;
import cn.com.broadlink.sdk.data.controller.BLGroupSubConfigInfo;
import cn.com.broadlink.sdk.result.controller.BLPairResult;
import cn.com.broadlink.sdk.result.controller.BLQueryGroupDeviceResult;
import cn.com.broadlink.sdk.result.controller.BLSubdevResult;

public class DevGroupConfigEditActivity extends TitleActivity {

    private RecyclerView mRvList;
    private LinearLayout mLlName;
    private TextView mTvName;
    private LinearLayout mLlTarget;
    private Button mBtAdd;

    private BLDNADevice mDNADevice;
    private BLDNADevice mSubDevice;
    private ArrayList<BLDNADevice> mSubDeviceList = new ArrayList<>();
    private List<BLGroupSubConfigInfo> mGroupSubDevList = new ArrayList<>();
    private SubDevAdapter mAdapter;
    private String mPid;
    private BLDevProfileInfo mBlDevProfileInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_group_dev_edit);
        setBackWhiteVisible();
        setTitle("Group Device Edit");

        mDNADevice = getIntent().getParcelableExtra(BLConstants.INTENT_PARCELABLE);
        mSubDevice = getIntent().getParcelableExtra(BLConstants.INTENT_DEVICE);
        mSubDeviceList = getIntent().getParcelableArrayListExtra(BLConstants.INTENT_ARRAY);
        mPid = getIntent().getStringExtra(BLConstants.INTENT_ID);
        
        if(mSubDevice != null){
            mPid = mSubDevice.getPid();
        }

        findView();

        initView();

        setListener();
    }


    private void findView() {
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
        mLlName = (LinearLayout) findViewById(R.id.ll_name);
        mTvName = (TextView) findViewById(R.id.tv_name);
        mLlTarget = (LinearLayout) findViewById(R.id.ll_target);
        mBtAdd = (Button) findViewById(R.id.bt_add);
    }

    private void initView() {
        mAdapter = new SubDevAdapter();
        mRvList.setLayoutManager(new LinearLayoutManager(mActivity));
        mRvList.setAdapter(mAdapter);
        mRvList.addItemDecoration(BLDividerUtil.getDefault(mActivity, mGroupSubDevList));
        
        mLlTarget.setVisibility(View.GONE);

        if (mSubDevice != null) {
            mTvName.setText(mSubDevice.getName());
            new QueryGroupBindInfoTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
        }else{
            mTvName.setText("TestGroupSub");
        }
    }

    private void setListener() {

        setRightButtonOnClickListener("Save", getResources().getColor(R.color.bl_yellow_main_color), new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                
                if (mSubDevice == null) { // 新增
                    mSubDevice = new BLDNADevice();
                    mSubDevice.setPid(mPid);
                    mSubDevice.setName(mTvName.getText().toString());
                    new AddSubDevTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR, true);
                    
                }else{ // 修改
                    mSubDevice.setName(mTvName.getText().toString());
                    new AddSubDevTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR, false);
                }
            }
        });

        mLlName.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                final String name = mTvName.getText()==null ? null : mTvName.getText().toString();
                BLAlert.showEditDilog(mActivity, "Please input name", name, new BLAlert.BLEditDialogOnClickListener() {
                    @Override
                    public void onClink(String value) {
                        mTvName.setText(value);
                    }

                    @Override
                    public void onCancel(String value) {

                    }
                }, false);
            }
        });

        mBtAdd.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {

                final Intent intent = new Intent(mActivity, DevGroupDevEditActivity.class);
                intent.putParcelableArrayListExtra(BLConstants.INTENT_ARRAY, mSubDeviceList);
                intent.putExtra(BLConstants.INTENT_ID, mPid);
                startActivityForResult(intent, 101);
            }
        });

        mAdapter.setOnItemClickListener(new BLBaseRecyclerAdapter.OnClickListener() {
            @Override
            public void onClick(int position, int viewType) {
                final Intent intent = new Intent(mActivity, DevGroupDevEditActivity.class);
                intent.putParcelableArrayListExtra(BLConstants.INTENT_ARRAY, mSubDeviceList);
                intent.putExtra(BLConstants.INTENT_ID, mPid);
                intent.putExtra(BLConstants.INTENT_MODULE, mGroupSubDevList.get(position));
                intent.putExtra(BLConstants.INTENT_VALUE, position);
                startActivityForResult(intent, 102);
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && data != null){
            BLGroupSubConfigInfo parcelableExtra = data.getParcelableExtra(BLConstants.INTENT_PARCELABLE);
            mPid = data.getStringExtra(BLConstants.INTENT_ID);
            
            if(parcelableExtra == null){
                return;
            }
            
            if ((requestCode==101)) { // 新增
                mGroupSubDevList.add(parcelableExtra);
            }else if((requestCode==102)) { // 编辑
               int position = getIntent().getIntExtra(BLConstants.INTENT_VALUE, 0);
                final BLGroupSubConfigInfo blGroupSubConfigInfo = mGroupSubDevList.get(position);
                if(blGroupSubConfigInfo != null){
                    blGroupSubConfigInfo.setTarget(parcelableExtra.getTarget());
                    blGroupSubConfigInfo.getList().clear();
                    blGroupSubConfigInfo.getList().addAll(parcelableExtra.getList());
                }else{
                    mGroupSubDevList.add(parcelableExtra);
                }
            }
            
            mAdapter.notifyDataSetChanged();
        }
    }

    class SubDevAdapter extends BLBaseRecyclerAdapter<BLGroupSubConfigInfo> {

        public SubDevAdapter() {
            super(mGroupSubDevList, R.layout.item_family_room);
        }

        @Override
        public void onBindViewHolder(BLBaseViewHolder holder, final int position) {
            super.onBindViewHolder(holder, position);
            holder.setText(R.id.tv_name, "Target: " + mBeans.get(position).getTarget());
            holder.setText(R.id.tv_mac, BLJSON.toJSONString(mBeans.get(position), true)); 
        }
    }


    /**
     * 添加子设备
     **/
    private class AddSubDevTask extends AsyncTask<Boolean, Void, BLBaseResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Add...");
        }

        @Override
        protected BLBaseResult doInBackground(Boolean... params) {
            // 申请did
            if (TextUtils.isEmpty(mSubDevice.getDid())) {
                BLFamily.setCurrentFamilyId(BLLocalFamilyManager.getInstance().getCurrentFamilyId());
                final BLPrivateDataIdResult familyPrivateDataId = BLFamily.getFamilyPrivateDataId();
                if(familyPrivateDataId == null || !familyPrivateDataId.succeed()){
                    return familyPrivateDataId; 
                }
                mSubDevice.setDid(familyPrivateDataId.getDataId());
            }
            
            // 先删除再添加
            if(!params[0]){
                BLMultDidUtils.subDevDel(mDNADevice.getDid(), mSubDevice.getDid());
                BLLocalDeviceManager.getInstance().removeDeviceFromSDK(mSubDevice);
            }

            // 添加子设备
            mSubDevice.setOwnerId(BLLocalFamilyManager.getInstance().getCurrentFamilyId());
            final BLSubdevResult blSubdevResult = BLLet.Controller.subDevAdd(mDNADevice.getDeviceId(), mSubDevice);
            if (blSubdevResult != null && blSubdevResult.succeed()) {
                if (mSubDevice.getMac() != null) {

                    BLPairResult pairResult = BLLet.Controller.pair(mSubDevice);
                    if (pairResult.succeed()) {
                        mSubDevice.setId(pairResult.getId());
                        mSubDevice.setKey(pairResult.getKey());
                    }

                    try {
                        BLDeviceInfoDao blDeviceInfoDao = new BLDeviceInfoDao(getHelper());
                        BLDeviceInfo deviceInfo = new BLDeviceInfo(mSubDevice);
                        List<BLDeviceInfo> list = new ArrayList<>();
                        list.add(deviceInfo);
                        blDeviceInfoDao.insertData(list);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    BLLocalDeviceManager.getInstance().addDeviceIntoSDK(mSubDevice);
                }

            }
            return blSubdevResult;
        }

        @Override
        protected void onPostExecute(BLBaseResult blBaseResult) {
            super.onPostExecute(blBaseResult);
            dismissProgressDialog();

            if (blBaseResult != null && blBaseResult.succeed()) {
                new BindGroupTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
            } else {
                BLCommonUtils.toastErr(blBaseResult);
            }
        }
    }


    /**
     * 查询group
     **/
    private class QueryGroupBindInfoTask extends AsyncTask<Void, Void, BLQueryGroupDeviceResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Query Bind Info...");
        }

        @Override
        protected BLQueryGroupDeviceResult doInBackground(Void... params) {
            return BLLet.Controller.queryGroupDeviceBindInfo(mDNADevice.getDeviceId(), mSubDevice.getDid());
        }

        @Override
        protected void onPostExecute(BLQueryGroupDeviceResult blBaseResult) {
            super.onPostExecute(blBaseResult);
            dismissProgressDialog();

            if (blBaseResult != null && blBaseResult.succeed()) {
                mGroupSubDevList.clear();
                mGroupSubDevList.addAll(blBaseResult.getConfig());
                mAdapter.notifyDataSetChanged();
            } else {
                BLCommonUtils.toastErr(blBaseResult);
            }
        }
    }

    /**
     * 添加group
     **/
    private class BindGroupTask extends AsyncTask<Void, Void, BLQueryGroupDeviceResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Add Bind Info...");
        }

        @Override
        protected BLQueryGroupDeviceResult doInBackground(Void... params) {
            final BLGroupDeviceInfo groupDeviceInfo = new BLGroupDeviceInfo();
            groupDeviceInfo.setDid(mSubDevice.getDid());
            groupDeviceInfo.setName(mSubDevice.getName());
            groupDeviceInfo.setPid(mSubDevice.getPid());
            groupDeviceInfo.getConfig().addAll(mGroupSubDevList);
            return BLLet.Controller.bindGroupDevice(mDNADevice.getDeviceId(), groupDeviceInfo);
        }

        @Override
        protected void onPostExecute(BLQueryGroupDeviceResult blBaseResult) {
            super.onPostExecute(blBaseResult);
            dismissProgressDialog();
            BLCommonUtils.toastErr(blBaseResult);
            
            if (blBaseResult != null && blBaseResult.succeed()) {
                setResult(RESULT_OK);
                back();
            } 
        }
    }
}
