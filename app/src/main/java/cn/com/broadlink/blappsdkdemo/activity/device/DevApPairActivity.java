package cn.com.broadlink.blappsdkdemo.activity.device;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLKeyboardUtils;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseRecyclerAdapter;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseViewHolder;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.divideritemdecoration.BLDividerUtil;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLAPInfo;
import cn.com.broadlink.sdk.data.controller.BLGetAPListResult;
import cn.com.broadlink.sdk.result.controller.BLAPConfigResult;

/**
 * Ap 配置
 */
public class DevApPairActivity extends TitleActivity {

    private APListAdapter mAdapter;
    private List<BLAPInfo> mAPList = new ArrayList<>();
    private BLAPInfo mSelectAPInfo;
    private RecyclerView mRvContent;
    private ImageView mIvRefresh;
    private LinearLayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Ap Config");
        setContentView(R.layout.activity_dev_ap_config);
        setBackWhiteVisible();
        
        findView();
        
        initView();
        
        setListener();
    }
    
    private void findView() {
        mRvContent = (RecyclerView) findViewById(R.id.rv_content);
        mIvRefresh = findViewById(R.id.iv_refresh);
    }

    private void initView() {
        mAdapter = new APListAdapter(mAPList);
        mRvContent.setAdapter(mAdapter);
        manager = new LinearLayoutManager(DevApPairActivity.this);
        mRvContent.setLayoutManager(manager);
        mRvContent.addItemDecoration(BLDividerUtil.getDefault(mActivity, mAPList));
    }

    private void setListener() {
        mAdapter.setOnItemClickListener(new BLBaseRecyclerAdapter.OnClickListener() {
            @Override
            public void onClick(int position, int viewType) {
                mSelectAPInfo = mAPList.get(position);
                BLAlert.showEditDilog(DevApPairActivity.this, "Please input password", null, new BLAlert.BLEditDialogOnClickListener() {
                    @Override
                    public void onClink(String value) {
                        new APConfigTask().execute(value);
                    }

                    @Override
                    public void onClinkCacel(String value) {

                    }
                }, false);
            }
        });
        
        mIvRefresh.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                new ScanAPListTask().execute();
            }
        });

        setRightButtonOnClickListener("Manual", getResources().getColor(R.color.bl_yellow_main_color), new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                BLCommonUtils.toActivity(DevApPairActivity.this, DevAPManualConfigActivity.class);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new ScanAPListTask().execute();
    }

    //AP 配置
    class APConfigTask extends AsyncTask<String, Void, BLAPConfigResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Config...");
            BLKeyboardUtils.hideKeyboard(mIvRefresh);
        }

        @Override
        protected BLAPConfigResult doInBackground(String... params) {
            String ssid = mSelectAPInfo.getSsid();
            int type = mSelectAPInfo.getType();
            String password = params[0];
            return BLLet.Controller.deviceAPConfig(ssid, password, type, null);
        }

        @Override
        protected void onPostExecute(BLAPConfigResult result) {
            super.onPostExecute(result);
            dismissProgressDialog();
            
            if(result !=null && result.succeed()){
                BLAlert.showAlert(mActivity, null, "Config send success, please check the device's state.", new OnSingleClickListener() {
                    @Override
                    public void doOnClick(View v) {
                        back();
                    }
                });
            }else{
                BLCommonUtils.toastErr(result);
            }
        }

    }

    class ScanAPListTask extends AsyncTask<Void, Void, BLGetAPListResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Scan...");
        }

        @Override
        protected BLGetAPListResult doInBackground(Void... params) {
            return BLLet.Controller.deviceAPList(8 * 1000);
        }

        @Override
        protected void onPostExecute(final BLGetAPListResult result) {
            super.onPostExecute(result);
            dismissProgressDialog();

            if (result.succeed()) {
                mAPList.clear();
                mAPList.addAll(result.getList());
                mAdapter.notifyDataSetChanged();
            } else {
                BLCommonUtils.toastErr(result);
            }
        }
    }

    private class APListAdapter extends BLBaseRecyclerAdapter<BLAPInfo> {
        
        public APListAdapter(List<BLAPInfo> objects) {
            super(objects, R.layout.item_dev_ap);
        }

        @Override
        public void onBindViewHolder(BLBaseViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);

            holder.setText(R.id.tv_name, getItem(position).getSsid());
            holder.setText(R.id.tv_mac, parseTypeName(getItem(position).getType()));
        }

        private String parseTypeName(int typeId){
            String type = "NONE";
            switch (typeId) {
                case 0:
                    type = "NONE";
                    break;
                case 1:
                    type = "WEP";
                    break;
                case 2:
                    type = "WPA";
                    break;
                case 3:
                    type = "WPA2";
                    break;
                case 4:
                    type = "WPA/WPA2 MIXED";
                    break;
            }
            return type;
        }
    }
}
