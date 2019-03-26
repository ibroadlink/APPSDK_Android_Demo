package cn.com.broadlink.blappsdkdemo.activity.device;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.check.DeviceCheckActivity;
import cn.com.broadlink.blappsdkdemo.activity.irCode.IRCodeAcPanelActivity;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.db.data.BLDeviceInfo;
import cn.com.broadlink.blappsdkdemo.db.dao.BLDeviceInfoDao;
import cn.com.broadlink.blappsdkdemo.service.BLLocalDeviceManager;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;

public class DevMyDevListActivity extends TitleActivity {

    private ListView mDevListView;
    private List<BLDNADevice> mDevices = new ArrayList<>();
    private DevMyDevListActivity.SDKDeviceAdapter mDeviceAdapter;

    private BLLocalDeviceManager mLocalDeviceManager;
    private int mDeviceCheck = 0;
    private String mFromWhere = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_my_device_list);
        setTitle(R.string.My_Device_List);
        setBackWhiteVisible();

        initData();

        findView();
        
        setListener();
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            mDeviceCheck = getIntent().getIntExtra("INTENT_IS_CHECK", 0);
            mFromWhere = getIntent().getStringExtra(BLConstants.INTENT_CLASS);
        }

        mLocalDeviceManager = BLLocalDeviceManager.getInstance();
        mDevices = mLocalDeviceManager.getDevicesAddInSDK();
    }

    private void findView() {
        mDevListView = (ListView) findViewById(R.id.my_dev_list);
        mDeviceAdapter  = new SDKDeviceAdapter(DevMyDevListActivity.this, mDevices);
        mDevListView.setAdapter(mDeviceAdapter);
    }

    private void setListener() {
        if(!IRCodeAcPanelActivity.class.getName().equals(mFromWhere)){
            setRightButtonOnClickListener(R.string.str_common_add,
                    getResources().getColor(R.color.bl_yellow_main_color),
                    new OnSingleClickListener() {

                        @Override
                        public void doOnClick(View v) {
                            Intent intent = new Intent();
                            intent.setClass(DevMyDevListActivity.this, DevProbeListActivity.class);
                            startActivity(intent);
                        }
                    });
        }
       

        mDevListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (mDeviceCheck == 0) {
                    if(IRCodeAcPanelActivity.class.getName().equals(mFromWhere)){ // Choose a rm device
                        final Intent intent = new Intent();
                        intent.putExtra(BLConstants.INTENT_DEVICE, mDevices.get(position));
                        setResult(RESULT_OK, intent);
                        back();
                    }else{
                        Intent intent = new Intent();
                        intent.putExtra(BLConstants.INTENT_DEVICE, mDevices.get(position));
                        intent.setClass(DevMyDevListActivity.this, DevMainMenuActivity.class);
                        startActivity(intent); 
                    }
                   
                } else {
                    Intent intent = new Intent();
                    intent.putExtra(BLConstants.INTENT_DEVICE, mDevices.get(position));
                    intent.setClass(DevMyDevListActivity.this, DeviceCheckActivity.class);
                    startActivity(intent);
                }
            }
        });

        mDevListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                final BLDNADevice device = mDevices.get(position);
                AlertDialog.Builder dialog = new AlertDialog.Builder(DevMyDevListActivity.this);
                dialog.setTitle("Message");
                dialog.setMessage("Remove Device " + device.getName() + " from SDK?");
                dialog.setCancelable(false);
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mLocalDeviceManager.removeDeviceFromSDK(device.getDid());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    BLDeviceInfoDao blDeviceInfoDao = new BLDeviceInfoDao(getHelper());
                                    BLDeviceInfo deviceInfo = new BLDeviceInfo(device);
                                    blDeviceInfoDao.deleteDevice(deviceInfo);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                                mDevices = mLocalDeviceManager.getDevicesAddInSDK();
                                mDeviceAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.show();

                return true;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        mDevices = mLocalDeviceManager.getDevicesAddInSDK();
        mDeviceAdapter.notifyDataSetChanged();
    }

    //设备列表适配器
    private class SDKDeviceAdapter extends ArrayAdapter<BLDNADevice> {
        public SDKDeviceAdapter(Context context, List<BLDNADevice> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DevMyDevListActivity.SDKDeviceAdapter.ViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new DevMyDevListActivity.SDKDeviceAdapter.ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.item_dev_device_info, null);
                viewHolder.name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.mac = (TextView) convertView.findViewById(R.id.tv_mac);
                viewHolder.type = (TextView) convertView.findViewById(R.id.tv_type);
                viewHolder.key = (TextView) convertView.findViewById(R.id.tv_key);
                viewHolder.status = (TextView) convertView.findViewById(R.id.tv_status);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (DevMyDevListActivity.SDKDeviceAdapter.ViewHolder) convertView.getTag();
            }

            BLDNADevice device = getItem(position);

            viewHolder.name.setText(device.getName());
            //viewHolder.mac.setText(JSON.toJSONString(device, true));
            
            viewHolder.mac.setText("Did: " + device.getDid());
            viewHolder.type.setText(String.format("Pid: %s (%d)", device.getPid(), device.getType()));
            viewHolder.key.setText("Key: " + device.getKey());

            String status;
            switch (device.getState()) {
                case 0:
                    status = "Init";
                    break;
                case 1:
                    status = "Lan";
                    break;
                case 2:
                    status = "Remote Online";
                    break;
                case 3:
                    status = "Remote Offline";
                    break;
                default:
                    status = "Unknown";
            }
            viewHolder.status.setText("Device Status: " + status);

            return convertView;
        }

        private class ViewHolder{
            TextView name;
            TextView mac;
            TextView type;
            TextView key;
            TextView status;
        }
    }
}
