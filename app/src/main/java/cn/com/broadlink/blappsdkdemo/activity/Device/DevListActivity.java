package cn.com.broadlink.blappsdkdemo.activity.Device;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.base.BLCommonTools;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.Family.BLSFamilyHTTP;
import cn.com.broadlink.blappsdkdemo.activity.Family.FamilyModuleListActivity;
import cn.com.broadlink.blappsdkdemo.activity.Family.FamilyRoomListActivity;
import cn.com.broadlink.blappsdkdemo.activity.Family.Result.BLSEndpointInfo;
import cn.com.broadlink.blappsdkdemo.activity.Family.Result.BLSQueryRoomListResult;
import cn.com.broadlink.blappsdkdemo.activity.Family.Result.BLSRoomInfo;
import cn.com.broadlink.blappsdkdemo.activity.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.db.BLDeviceInfo;
import cn.com.broadlink.blappsdkdemo.db.BLDeviceInfoDao;
import cn.com.broadlink.blappsdkdemo.service.BLLocalDeviceListener;
import cn.com.broadlink.blappsdkdemo.service.BLLocalDeviceManager;
import cn.com.broadlink.blappsdkdemo.service.BLLocalFamilyManager;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;
import cn.com.broadlink.sdk.result.controller.BLPairResult;

/**
 * 设备列表
 * Created by YeJin on 2016/5/9.
 */
public class DevListActivity extends TitleActivity {

    private ListView mDevListView;
    private List<BLDNADevice> mDevices = new ArrayList<>();

    private DeviceAdapter mDeviceAdapter;
    private BLLocalDeviceManager mLocalDeviceManager;

    private Handler checkHandler = new Handler();
    private Runnable checkRunnable;
    private String mFamilyId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dev_list_layout);
        setTitle(R.string.Probe_Device_List);
        setBackWhiteVisible();

        mLocalDeviceManager = BLLocalDeviceManager.getInstance();
        mDevices = mLocalDeviceManager.getLocalDevices();

        Intent intent = getIntent();
        if (intent != null) {
            mFamilyId = getIntent().getStringExtra("INTENT_FAMILY_ID");
        }

        findView();
        setListener();
    }

    private void findView() {
        mDevListView = findViewById(R.id.dev_list);
        mDeviceAdapter  = new DeviceAdapter(DevListActivity.this, mDevices);
        mDevListView.setAdapter(mDeviceAdapter);
    }

    private void setListener(){
        //短按只是添加设备到 MyDeviceList 内
        mDevListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final BLDNADevice device = mDevices.get(position);
                AlertDialog.Builder dialog = new AlertDialog.Builder(DevListActivity.this);
                dialog.setTitle("Message");
                dialog.setMessage("Add Device " + device.getName() + " into SDK?");
                dialog.setCancelable(false);
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

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
                        }

                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.show();

            }
        });

        //长按添加设备到家庭
        mDevListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long id) {

                if (mFamilyId != null) {
                    final BLDNADevice device = mDevices.get(position);
                    AlertDialog.Builder dialog = new AlertDialog.Builder(DevListActivity.this);
                    dialog.setTitle("Message");
                    dialog.setMessage("Add Device " + device.getName() + " into family?");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

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
                                new AddEndpointTask().execute(position);
                            }

                        }
                    });
                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    dialog.show();
                    return true;
                } else {
                    return false;
                }

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

    private class AddEndpointTask extends AsyncTask<Integer, Void, BLBaseResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected BLBaseResult doInBackground(Integer...integers) {

            Integer index = integers[0];
            BLDNADevice device = mDevices.get(index);
            BLSEndpointInfo endpointInfo = new BLSEndpointInfo(device);
            endpointInfo.setFriendlyName("TestEndPoint");
            endpointInfo.setRoomId("2008426781207206652");

            List<BLSEndpointInfo> infos = new ArrayList<>();
            infos.add(endpointInfo);

            return BLSFamilyHTTP.getInstance().addEndpoint(mFamilyId, infos);
        }

        @Override
        protected void onPostExecute(BLBaseResult result) {
            super.onPostExecute(result);

            if (result != null && result.succeed()) {
                BLCommonUtils.toastShow(DevListActivity.this, "Add EndPoint Success");
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
            if(convertView == null){
                viewHolder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.adapter_device, null);
                viewHolder.name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.mac = (TextView) convertView.findViewById(R.id.tv_mac);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            BLDNADevice device = getItem(position);

            viewHolder.name.setText(device.getName());
            viewHolder.mac.setText(device.getMac());

            return convertView;
        }

        private class ViewHolder{
            TextView name;
            TextView mac;
        }
    }

}
