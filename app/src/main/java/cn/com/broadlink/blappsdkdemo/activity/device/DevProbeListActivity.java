package cn.com.broadlink.blappsdkdemo.activity.device;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.family.manager.BLSFamilyManager;
import cn.com.broadlink.blappsdkdemo.activity.family.FamilyModuleListActivity;
import cn.com.broadlink.blappsdkdemo.activity.family.result.BLSEndpointInfo;
import cn.com.broadlink.blappsdkdemo.activity.family.result.BLSQueryRoomListResult;
import cn.com.broadlink.blappsdkdemo.activity.family.result.BLSRoomInfo;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.db.data.BLDeviceInfo;
import cn.com.broadlink.blappsdkdemo.db.dao.BLDeviceInfoDao;
import cn.com.broadlink.blappsdkdemo.service.BLLocalDeviceListener;
import cn.com.broadlink.blappsdkdemo.service.BLLocalDeviceManager;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.BLListAlert;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;
import cn.com.broadlink.sdk.result.controller.BLPairResult;

/**
 * 设备列表
 * Created by YeJin on 2016/5/9.
 */
public class DevProbeListActivity extends TitleActivity {

    private ListView mDevListView;
    private List<BLDNADevice> mDevices = new ArrayList<>();
    private List<BLDNADevice> mAdd2SdkDevs = new ArrayList<>();

    private DeviceAdapter mDeviceAdapter;
    private BLLocalDeviceManager mLocalDeviceManager;

    private Handler checkHandler = new Handler();
    private Runnable checkRunnable;
    private String mFamilyId = null;
    private String mFromWhere = null;
    private List<BLSRoomInfo> blsRoomInfos = new ArrayList<>();
    private int mSelection = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_prob_list);
        setBackWhiteVisible();
        setTitle(R.string.Probe_Device_List);
        
        initData();
        
        findView();

        setListener();

    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            mFamilyId = getIntent().getStringExtra(BLConstants.INTENT_FAMILY_ID);
            mFromWhere = getIntent().getStringExtra(BLConstants.INTENT_CLASS);
        }

        mLocalDeviceManager = BLLocalDeviceManager.getInstance();
        final boolean isAdd2Family = FamilyModuleListActivity.class.getSimpleName().equals(mFromWhere);
        if(!isAdd2Family){
            mAdd2SdkDevs = mLocalDeviceManager.getDevicesAddInSDK();
            for (BLDNADevice item : mLocalDeviceManager.getLocalDevices()) {
                if (!isContain(item)) {
                    mDevices.add(item);
                }
            }
        }else{
            mDevices = mLocalDeviceManager.getLocalDevices();
        }
    }

    private boolean isContain(BLDNADevice dev) {
        if (dev == null) return false;

        for (BLDNADevice item : mAdd2SdkDevs) {
            if (item.getDid().equalsIgnoreCase(dev.getDid())) {
                return true;
            }
        }
        return false;
    }

    private void findView() {
        mDevListView = findViewById(R.id.dev_list);
        mDeviceAdapter = new DeviceAdapter(DevProbeListActivity.this, mDevices);
        mDevListView.setAdapter(mDeviceAdapter);
    }

    private void setListener() {
        //短按只是添加设备到 MyDeviceList 内
        mDevListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                add2SdkOrFamily(position);
            }
        });

        mLocalDeviceManager.setBlLocalDeviceListener(new BLLocalDeviceListener() {
            @Override
            public void deviceChange() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDevices = mLocalDeviceManager.getLocalDevices();
                        mDeviceAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        checkRunnable = new Runnable() {
            @Override
            public void run() {

                mDevices = mLocalDeviceManager.getLocalDevices();
                mDeviceAdapter.notifyDataSetChanged();
                checkHandler.postDelayed(this, 5 * 1000);
            }
        };
        checkHandler.postDelayed(checkRunnable, 5 * 1000);
    }

    private void add2SdkOrFamily(final int position) {
        mSelection = position;
        final BLDNADevice device = mDevices.get(position);
        final boolean isAdd2Family = FamilyModuleListActivity.class.getSimpleName().equals(mFromWhere);
        final String message = "Add device " + device.getName() + (isAdd2Family ? " into Family?" : " into SDK?");
        BLAlert.showDialog(DevProbeListActivity.this, message, new BLAlert.DialogOnClickListener() {
            @Override
            public void onPositiveClick() {
                BLPairResult pairResult = BLLet.Controller.pair(device);
                if (pairResult.succeed()) {
                    device.setId(pairResult.getId());
                    device.setKey(pairResult.getKey());

                    try {
                        BLDeviceInfoDao blDeviceInfoDao = new BLDeviceInfoDao(getHelper());
                        BLDeviceInfo deviceInfo = new BLDeviceInfo(device);
                        List<BLDeviceInfo> list = new ArrayList<>();
                        list.add(deviceInfo);
                        blDeviceInfoDao.insertData(list);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    mLocalDeviceManager.addDeviceIntoSDK(device);

                 
                    if(isAdd2Family){ // 添加设备到家庭
                        if(blsRoomInfos.size()>0){
                            selectRoom2Add();
                        }else{
                            new QueryRoomListTask().execute();
                        }
                    }else{
                        BLToastUtils.show("Add device to sdk success!");
                    }
                }else{
                    BLCommonUtils.toastErr(pairResult);
                }
            }

            @Override
            public void onNegativeClick() {

            }
        });
    }


    private class QueryRoomListTask extends AsyncTask<Void, Void, BLSQueryRoomListResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Query Room List...");
        }

        @Override
        protected BLSQueryRoomListResult doInBackground(Void... strings) {

            return BLSFamilyManager.getInstance().queryRoomList(mFamilyId);
        }

        @Override
        protected void onPostExecute(BLSQueryRoomListResult result) {
            super.onPostExecute(result);
            dismissProgressDialog();
            
            if (result != null && result.succeed() && result.getData() != null) {
                blsRoomInfos.clear();

                if (result.getData().getRoomList() != null) {
                    blsRoomInfos.addAll(result.getData().getRoomList());
                    if(blsRoomInfos.size()>0){
                        selectRoom2Add();
                        return;
                    }else{
                        BLToastUtils.show("Please add a room first!");
                    }
                }
            }
            
            BLCommonUtils.toastErr(result);
        }
    }

    private void selectRoom2Add() {
        final ArrayList<String> roomList = new ArrayList<>();
        for (BLSRoomInfo item : blsRoomInfos){
            roomList.add(item.getName() + "-" + item.getRoomid());
        }
        final String[] items = roomList.toArray(new String[0]);
        
        BLListAlert.showAlert(DevProbeListActivity.this, "Select room", items, new BLListAlert.OnItemClickLister() {
            @Override
            public void onClick(int whichButton) {
                new AddEndpointTask(blsRoomInfos.get(whichButton).getRoomid()).execute(mSelection);
            }
        });
    }


    private class AddEndpointTask extends AsyncTask<Integer, Void, BLBaseResult> {
        String roomId;

        public AddEndpointTask(String roomId) {
            this.roomId = roomId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Adding to family...");
        }

        @Override
        protected BLBaseResult doInBackground(Integer... integers) {

            Integer index = integers[0];
            BLDNADevice device = mDevices.get(index);
            BLSEndpointInfo endpointInfo = new BLSEndpointInfo(device);
            endpointInfo.setFriendlyName("TestEndPoint-" + device.getName());
            endpointInfo.setRoomId(roomId);

            List<BLSEndpointInfo> infos = new ArrayList<>();
            infos.add(endpointInfo);

            return BLSFamilyManager.getInstance().addEndpoint(mFamilyId, infos);
        }

        @Override
        protected void onPostExecute(BLBaseResult result) {
            super.onPostExecute(result);
            dismissProgressDialog();
            
            if (result != null && result.succeed()) {
                BLToastUtils.show("Add device to family success!");
            }else{
                BLCommonUtils.toastErr(result);
            }
        }
    }

    //设备列表适配器
    private class DeviceAdapter extends ArrayAdapter<BLDNADevice> {
        public DeviceAdapter(Context context, List<BLDNADevice> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.item_dev_ap, null);
                viewHolder.name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.mac = (TextView) convertView.findViewById(R.id.tv_mac);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            BLDNADevice device = getItem(position);

            viewHolder.name.setText(device.getName());
            viewHolder.mac.setText(JSON.toJSONString(device, true));

            return convertView;
        }

        private class ViewHolder {
            TextView name;
            TextView mac;
        }
    }

}
