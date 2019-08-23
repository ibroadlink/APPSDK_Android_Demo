package cn.com.broadlink.blappsdkdemo.activity.device;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;

import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLMultDidUtils;
import cn.com.broadlink.blappsdkdemo.common.BLPidConstants;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.BLListAlert;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseRecyclerAdapter;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseViewHolder;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.divideritemdecoration.BLDividerUtil;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;
import cn.com.broadlink.sdk.result.controller.BLSubDevListResult;

public class DevGroupDevListActivity extends TitleActivity {

    private EditText mTvResult;
    private RecyclerView mRvList;
    private EditText mEtPid;
    private Button mBtGet;
    
    private BLDNADevice mDNADevice;
    private ArrayList<BLDNADevice> mSubDeviceList = new ArrayList<>();
    private ArrayList<BLDNADevice> mAllSubList = new ArrayList<>();
    private SubDevAdapter mAdapter;
    private String mPid = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_group_dev_list);
        setBackWhiteVisible();
        setTitle("Gateway Group Device");

        mDNADevice = getIntent().getParcelableExtra(BLConstants.INTENT_PARCELABLE);

        findView();

        initView();

        setListener();

        queryGroupDevList();
    }

    private void findView() {
        mTvResult = (EditText) findViewById(R.id.et_result);
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
        mEtPid = (EditText) findViewById(R.id.et_pid);
        mBtGet = (Button) findViewById(R.id.bt_get);
    }

    private void initView() {
        mAdapter = new SubDevAdapter();
        mRvList.setLayoutManager(new LinearLayoutManager(mActivity));
        mRvList.setAdapter(mAdapter);
        mRvList.addItemDecoration(BLDividerUtil.getDefault(mActivity, mSubDeviceList));
        
        mEtPid.setText(BLPidConstants.PID_GROUP);
    }


    private void setListener() {

        setRightButtonOnClickListener("Add", getResources().getColor(R.color.bl_yellow_main_color), new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {

                final Intent intent = new Intent(mActivity, DevGroupConfigEditActivity.class);
                intent.putExtra(BLConstants.INTENT_PARCELABLE, mDNADevice);
                intent.putParcelableArrayListExtra(BLConstants.INTENT_ARRAY, mAllSubList);
                intent.putExtra(BLConstants.INTENT_ID, mPid);
                startActivityForResult(intent, 101);
            }
        });
        
        mBtGet.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                queryGroupDevList();
            }
        });

        mAdapter.setOnItemClickListener(new BLBaseRecyclerAdapter.OnClickListener() {
            @Override
            public void onClick(final int position, int viewType) {
                final String[] menu = {"Control", "Edit"};
                BLListAlert.showAlert(mActivity, null, menu, new BLListAlert.OnItemClickLister() {
                    @Override
                    public void onClick(int whichButton) {

                        switch (whichButton) {
                            case 0:
                                BLCommonUtils.toActivity(mActivity, DevDnaStdControlActivity.class, mSubDeviceList.get(whichButton));
                                break;

                            case 1:
                                final Intent intent = new Intent(mActivity, DevGroupConfigEditActivity.class);
                                intent.putExtra(BLConstants.INTENT_PARCELABLE, mDNADevice);
                                intent.putExtra(BLConstants.INTENT_DEVICE, mSubDeviceList.get(position));
                                intent.putParcelableArrayListExtra(BLConstants.INTENT_ARRAY, mAllSubList);
                                startActivityForResult(intent, 101);
                                break;
                        }
                    }
                });
            }
        });

        mAdapter.setOnItemLongClickListener(new BLBaseRecyclerAdapter.OnLongClickListener() {
            @Override
            public boolean onLongClick(final int position, int viewType) {
                BLAlert.showDialog(mActivity, "Confirm to delete this sub device?", new BLAlert.DialogOnClickListener() {
                    @Override
                    public void onPositiveClick() {
                        new DelSubDevTask().execute(position);
                    }

                    @Override
                    public void onNegativeClick() {

                    }
                });
                return true;
            }
        });
    }


    private boolean checkPid() {
        final boolean ret = !TextUtils.isEmpty(mEtPid.getText());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (ret) {
                    if (mEtPid.getText().length() == 32) {
                        mPid = mEtPid.getText().toString();
                    } else {
                        mPid = BLCommonUtils.deviceType2Pid(mEtPid.getText().toString());
                    }
                }else{
                    mPid = null;
                }
                Log.d("mPid", JSON.toJSONString(mPid, true));
            }
        });
        return ret;
    }

    private void queryGroupDevList() {
        checkPid();
        new GetAddedSubDevsTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
    }

    private void showResult(Object result) {
        if (result == null) {
            mTvResult.setText("Return null");
        } else {
            if (result instanceof String) {
                mTvResult.setText((String) result);
            } else {
                mTvResult.setText(JSON.toJSONString(result, true));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101){
            queryGroupDevList();
        }
    }

    class SubDevAdapter extends BLBaseRecyclerAdapter<BLDNADevice> {

        public SubDevAdapter() {
            super(mSubDeviceList, R.layout.item_family_room);
        }

        @Override
        public void onBindViewHolder(BLBaseViewHolder holder, final int position) {
            super.onBindViewHolder(holder, position);
            holder.setText(R.id.tv_name, "Pid: " + mBeans.get(position).getPid());
            holder.setText(R.id.tv_mac, "Did: " + mBeans.get(position).getDid());
        }
    }

    /**
     * 查询已添加的子设备列表
     **/
    private class GetAddedSubDevsTask extends AsyncTask<String, Void, BLSubDevListResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Get Added List...");
        }

        @Override
        protected BLSubDevListResult doInBackground(String... params) {

            int index = 0;
            final int QUERY_MAX_COUNT = 5;
            final String gatewayDid = mDNADevice.getDeviceId();
            BLSubDevListResult result = BLLet.Controller.devSubDevListQuery(gatewayDid, index, QUERY_MAX_COUNT);

            if (result != null && result.succeed() && result.getData() != null && result.getData().getList() != null && !result.getData().getList().isEmpty()) {
                int total = result.getData().getTotal();
                int currListSize = result.getData().getList().size();
                if (currListSize < total) {
                    while (true) {
                        index = result.getData().getList().size();
                        BLSubDevListResult queryResult = null;
                        //循环最多查询3次，成功之后跳出循环
                        for (int i = 0; i < 3; i++) {
                            queryResult = BLLet.Controller.devSubDevListQuery(gatewayDid, index, QUERY_MAX_COUNT);
                            if (queryResult != null && queryResult.succeed()) {
                                break;
                            }
                        }

                        if (queryResult != null && queryResult.succeed()) {
                            if (queryResult.getData() != null && queryResult.getData().getList() != null && !result.getData().getList().isEmpty()) {
                                result.getData().getList().addAll(queryResult.getData().getList());
                                result.getData().setIndex(queryResult.getData().getIndex());

                                if (result.getData().getList().size() >= total) {
                                    return result;
                                }
                            } else {
                                return queryResult;
                            }
                        } else {
                            return queryResult;
                        }
                    }
                }
            }
            return result;

        }

        @Override
        protected void onPostExecute(BLSubDevListResult blBaseResult) {
            super.onPostExecute(blBaseResult);
            dismissProgressDialog();
            showResult(blBaseResult);

            if (blBaseResult != null && blBaseResult.succeed()) {
                mSubDeviceList.clear();
                mAllSubList.clear();
                
                if (blBaseResult.getData() != null) {
                    for (BLDNADevice dev : blBaseResult.getData().getList()) {
                        dev.setpDid(mDNADevice.getDid());
                        if ((!TextUtils.isEmpty(mPid) && dev.getPid().equalsIgnoreCase(mPid)) || (TextUtils.isEmpty(mPid) && BLPidConstants.isFastconGroup(dev.getPid()))) {
                            mSubDeviceList.add(dev);
                        } else {
                            mAllSubList.add(dev);
                        }
                        BLLet.Controller.addDevice(dev);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    }


    /**
     * 删除子设备
     **/
    private class DelSubDevTask extends AsyncTask<Integer, Void, BLBaseResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Delete...");
        }

        @Override
        protected BLBaseResult doInBackground(Integer... params) {
            return BLMultDidUtils.subDevDel(mDNADevice.getDid(), mSubDeviceList.get(params[0]).getDid());
        }

        @Override
        protected void onPostExecute(BLBaseResult blBaseResult) {
            super.onPostExecute(blBaseResult);
            dismissProgressDialog();
            showResult(blBaseResult);
            if (blBaseResult != null && blBaseResult.succeed()) {
                new GetAddedSubDevsTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
            }
        }
    }

}
