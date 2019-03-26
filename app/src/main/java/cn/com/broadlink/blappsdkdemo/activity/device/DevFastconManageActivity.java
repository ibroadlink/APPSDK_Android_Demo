package cn.com.broadlink.blappsdkdemo.activity.device;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.data.fastcon.constant.BLFastconConstans;
import cn.com.broadlink.blappsdkdemo.data.fastcon.constant.BLFastconGetNewListResult;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseRecyclerAdapter;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseViewHolder;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.divideritemdecoration.BLDividerUtil;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;

public class DevFastconManageActivity extends TitleActivity {

    private EditText mTvResult;
    private Button mBtGetList;
    private Button mBtFastcon;
    private Button mBtGetFastconResult;
    private RecyclerView mRvList;
    
    private BLDNADevice mDNADevice;
    private List<BLFastconGetNewListResult.ItemBean> mDeviceList = new ArrayList<>();
    private DevAdapter mAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_fastcon_manage);
        setBackWhiteVisible();
        setTitle("Fastcon Management");
        
        mDNADevice = getIntent().getParcelableExtra(BLConstants.INTENT_PARCELABLE);
        
        findView();

        initView();

        setListener();
    }

    private void setListener() {
        mBtGetList.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                getNewListCmd();
            }
        });

        mBtFastcon.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                batchConfig();
            }
        });

        mBtGetFastconResult.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                getConfigStatus();
            }
        });

        mAdapter.setOnItemClickListener(new BLBaseRecyclerAdapter.OnClickListener() {
            @Override
            public void onClick(int position, int viewType) {
                config(mDeviceList.get(position).did);
            }
        });
    }

    private void initView() {
        mAdapter = new DevAdapter();
        mRvList.setLayoutManager(new LinearLayoutManager(mActivity));
        mRvList.setAdapter(mAdapter);
        mRvList.addItemDecoration(BLDividerUtil.getDefault(mActivity, mDeviceList));
    }

    private void findView() {
        mTvResult = (EditText) findViewById(R.id.et_result);
        mBtGetList = (Button) findViewById(R.id.bt_get_list);
        mBtFastcon = (Button) findViewById(R.id.bt_fastcon);
        mBtGetFastconResult = (Button) findViewById(R.id.bt_get_fastcon_result);
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
    }

    private void getNewListCmd(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("did", mDNADevice.getDid());
        jsonObject.put("act",  BLFastconConstans.ACT.GET_NEW_LIST);
        jsonObject.put("count", 5);
        jsonObject.put("index", 0);
        new FastConGetNewListTask().execute(BLFastconConstans.ITF_MARST, jsonObject.toJSONString());
    }
    
    private void batchConfig(){

        final ArrayList<String> dids = new ArrayList<>();
        for (BLFastconGetNewListResult.ItemBean item : mDeviceList){
            dids.add(item.did);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("did", mDNADevice.getDid());
        jsonObject.put("act", BLFastconConstans.ACT.BATCH_CONFIG);
        jsonObject.put("devlist", dids);
        
        new FastConCtrlTask().execute(BLFastconConstans.ITF_MARST, jsonObject.toJSONString());
    }
    
    private void config(String did){

        final ArrayList<String> dids = new ArrayList<>();
        dids.add(did);
        
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("did", mDNADevice.getDid());
        jsonObject.put("act", BLFastconConstans.ACT.BATCH_CONFIG);
        jsonObject.put("devlist", dids);
        
        new FastConCtrlTask().execute(BLFastconConstans.ITF_MARST, jsonObject.toJSONString());
    }
    
    private void getConfigStatus(){

        final ArrayList<String> dids = new ArrayList<>();
        for (BLFastconGetNewListResult.ItemBean item : mDeviceList){
            dids.add(item.did);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("did", mDNADevice.getDid());
        jsonObject.put("act", BLFastconConstans.ACT.GET_CONFIG_STATUS);
        jsonObject.put("devlist", dids);
        
        new FastConCtrlTask().execute(BLFastconConstans.ITF_MARST, jsonObject.toJSONString());
    }

    class DevAdapter extends BLBaseRecyclerAdapter<BLFastconGetNewListResult.ItemBean> {

        public DevAdapter() {
            super(mDeviceList, R.layout.item_family_room);
        }

        @Override
        public void onBindViewHolder(BLBaseViewHolder holder, final int position) {
            super.onBindViewHolder(holder, position);
            holder.setText(R.id.tv_name, mBeans.get(position).did);
            holder.setText(R.id.tv_mac, JSON.toJSONString(mBeans.get(position), true));
        }
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

   private class FastConCtrlTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Fastcon controlling...");
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
            dismissProgressDialog();
            showResult(result);
        }
    }
    
    
   private class FastConGetNewListTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Fastcon get list...");
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
            dismissProgressDialog();
            showResult(result);

            final BLFastconGetNewListResult fastconGetNewListResult = JSON.parseObject(result, BLFastconGetNewListResult.class);
            if(fastconGetNewListResult != null && fastconGetNewListResult.data != null && fastconGetNewListResult.data.devlist != null){
                mDeviceList.clear();
                mDeviceList.addAll(fastconGetNewListResult.data.devlist);
                mAdapter.notifyDataSetChanged();
            }
        }
    }
}
