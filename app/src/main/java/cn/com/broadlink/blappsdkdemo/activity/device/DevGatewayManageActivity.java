package cn.com.broadlink.blappsdkdemo.activity.device;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseRecyclerAdapter;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseViewHolder;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.divideritemdecoration.BLDividerBuilder;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.divideritemdecoration.BLDividerItemDecoration;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.divideritemdecoration.Divider;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;
import cn.com.broadlink.sdk.result.controller.BLSubDevListResult;

public class DevGatewayManageActivity extends TitleActivity {

    private TextView mTvResult;
    private Button mBtScanStart;
    private Button mBtScanStop;
    private Button mBtGetScanNewList;
    private Button mBtGetList;
    private RecyclerView mRvList;
    
    private BLDNADevice mDNADevice;
    private List<BLDNADevice> mSubDeviceList = new ArrayList<>();
    private SubDevAdapter mAdapter;
    private boolean mIsNewSubListType = true;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_gateway_manage);
        setBackWhiteVisible();
        setTitle("Gateway Management");
        
        mDNADevice = getIntent().getParcelableExtra(BLConstants.INTENT_PARCELABLE);
        
        findView();

        initView();

        setListener();
    }

    private void setListener() {
        mBtScanStart.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                new StartScanTask().execute();
            }
        });

        mBtScanStop.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                new StopScanTask().execute();
            }
        });

        mBtGetScanNewList.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {

                BLAlert.showEditDilog(mActivity, "Please input the target sub device's pid", null, new BLAlert.BLEditDialogOnClickListener() {
                    @Override
                    public void onClink(String value) {
                        new GetNewSubDevsTask().execute(value);
                    }

                    @Override
                    public void onClinkCacel(String value) {

                    }
                }, false);
            }
        });

        mBtGetList.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                new GetAddedSubDevsTask().execute();
            }
        });

        mAdapter.setOnItemClickListener(new BLBaseRecyclerAdapter.OnClickListener() {
            @Override
            public void onClick(int position, int viewType) {
                if(mIsNewSubListType){
                    new AddSubDevTask().execute(position);
                }else{
                    BLCommonUtils.toActivity(mActivity, DevDnaStdControlActivity.class, mSubDeviceList.get(position));
                }
            }
        });

        mAdapter.setOnItemLongClickListener(new BLBaseRecyclerAdapter.OnLongClickListener() {
            @Override
            public boolean onLongClick(final int position, int viewType) {
                if(!mIsNewSubListType){
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
                
                return false;
            }
        });
    }

    private void initView() {
        mAdapter = new SubDevAdapter();
        mRvList.setLayoutManager(new LinearLayoutManager(mActivity));
        mRvList.setAdapter(mAdapter);
        mRvList.addItemDecoration(new BLDividerItemDecoration(mActivity){

            @Nullable
            @Override
            public Divider getDivider(int itemPosition) {
                BLDividerBuilder builder = new BLDividerBuilder();
                if(itemPosition != mAdapter.getItemCount()-1){
                    builder.setBottomSideLine(true, getResources().getColor(R.color.gray), 1, 0, 0);
                }
                return builder.create();
            }
        });
    }

    private void showResult(Object result) {
        if(result == null){
            mTvResult.setText("Return null");
        }else{
            if(result instanceof String){
                mTvResult.setText((String)result);
            }else{
                mTvResult.setText(JSON.toJSONString(result, true));
            }

        }
    }
    
    private void findView() {
        mTvResult = (TextView) findViewById(R.id.tv_result);
        mBtScanStart = (Button) findViewById(R.id.bt_scan_start);
        mBtScanStop = (Button) findViewById(R.id.bt_scan_stop);
        mBtGetScanNewList = (Button) findViewById(R.id.bt_get_scan_new_list);
        mBtGetList = (Button) findViewById(R.id.bt_get_list);
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
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

    private class StartScanTask extends AsyncTask<String, Void, BLBaseResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          showProgressDialog("Scan...");
        }

        @Override
        protected BLBaseResult doInBackground(String... params) {
            return BLLet.Controller.subDevScanStart(mDNADevice.getDid(), null);
        }

        @Override
        protected void onPostExecute(BLBaseResult blBaseResult) {
            super.onPostExecute(blBaseResult);
            dismissProgressDialog();
            showResult(blBaseResult);
        }
    }
    private class StopScanTask extends AsyncTask<String, Void, BLBaseResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          showProgressDialog("Stop Scan...");
        }

        @Override
        protected BLBaseResult doInBackground(String... params) {
            return BLLet.Controller.subDevScanStop(mDNADevice.getDid());
        }

        @Override
        protected void onPostExecute(BLBaseResult blBaseResult) {
            super.onPostExecute(blBaseResult);
            dismissProgressDialog();
            showResult(blBaseResult);
        }
    }
    
    
    private class GetNewSubDevsTask extends AsyncTask<String, Void, BLSubDevListResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          showProgressDialog("Get Scanned List...");
        }

        @Override
        protected BLSubDevListResult doInBackground(String... params) {
            return BLLet.Controller.devSubDevNewListQuery(mDNADevice.getDid(), params[0],0, 5);
        }

        @Override
        protected void onPostExecute(BLSubDevListResult blBaseResult) {
            super.onPostExecute(blBaseResult);
            dismissProgressDialog();
            showResult(blBaseResult);

            if (blBaseResult != null && blBaseResult.succeed() && blBaseResult.getData() != null) {
                mIsNewSubListType = true;
                mSubDeviceList.clear();
                mSubDeviceList.addAll(blBaseResult.getData().getList());
                
                mAdapter.notifyDataSetChanged();
            }
        }
    }
    
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
            final String getwayDid = mDNADevice.getDid();
            BLSubDevListResult result = BLLet.Controller.devSubDevListQuery(getwayDid, index, QUERY_MAX_COUNT);

            if (result != null && result.succeed() && result.getData() != null && result.getData().getList() != null && !result.getData().getList().isEmpty()) {
                int total = result.getData().getTotal();
                int currListSize = result.getData().getList().size();
                if(currListSize < total){
                    while (true){
                        index = result.getData().getList().size();
                        BLSubDevListResult queryResult = null;
                        //循环最多查询3次，成功之后跳出循环
                        for (int i = 0; i < 3; i++) {
                            queryResult = BLLet.Controller.devSubDevListQuery(getwayDid, index, QUERY_MAX_COUNT);
                            if (queryResult != null && queryResult.succeed()) {
                                break;
                            }
                        }

                        if (queryResult != null && queryResult.succeed()) {
                            if (queryResult.getData() != null && queryResult.getData().getList() != null && !result.getData().getList().isEmpty()) {
                                result.getData().getList().addAll(queryResult.getData().getList());
                                result.getData().setIndex(queryResult.getData().getIndex());

                                if(result.getData().getList().size() >= total){
                                    return result;
                                }
                            }else{
                                return queryResult;
                            }
                        }else{
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

            if (blBaseResult != null && blBaseResult.succeed() && blBaseResult.getData() != null) {
                mIsNewSubListType = false;
                mSubDeviceList.clear();

                for (BLDNADevice dev  : blBaseResult.getData().getList() ) {
                    dev.setpDid(mDNADevice.getDid());
                    mSubDeviceList.add(dev);
                    BLLet.Controller.addDevice(dev);
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    }
    
    
    private class AddSubDevTask extends AsyncTask<Integer, Void, BLBaseResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          showProgressDialog("Add...");
        }

        @Override
        protected BLBaseResult doInBackground(Integer... params) {
            
            return BLLet.Controller.subDevAdd(mDNADevice.getDid(), mSubDeviceList.get(params[0]));
        }

        @Override
        protected void onPostExecute(BLBaseResult blBaseResult) {
            super.onPostExecute(blBaseResult);
            dismissProgressDialog();
            showResult(blBaseResult);
        }
    }
    
    
    private class DelSubDevTask extends AsyncTask<Integer, Void, BLBaseResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          showProgressDialog("Add...");
        }

        @Override
        protected BLBaseResult doInBackground(Integer... params) {
            return BLLet.Controller.subDevDel(mDNADevice.getDid(), mSubDeviceList.get(params[0]).getDid());
        }

        @Override
        protected void onPostExecute(BLBaseResult blBaseResult) {
            super.onPostExecute(blBaseResult);
            dismissProgressDialog();
            showResult(blBaseResult);
        }
    }

}
