package cn.com.broadlink.blappsdkdemo.activity.Device;

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
import cn.com.broadlink.blappsdkdemo.activity.TitleActivity;
import cn.com.broadlink.blappsdkdemo.db.BLDeviceInfo;
import cn.com.broadlink.blappsdkdemo.db.BLDeviceInfoDao;
import cn.com.broadlink.blappsdkdemo.service.BLLocalDeviceManager;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;

public class MyDeviceListActivity extends TitleActivity {

    private ListView mDevListView;
    private List<BLDNADevice> mDevices = new ArrayList<>();
    private MyDeviceListActivity.SDKDeviceAdapter mDeviceAdapter;

    private BLLocalDeviceManager mLocalDeviceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_device_list);
        setTitle(R.string.My_Device_List);

        mLocalDeviceManager = BLLocalDeviceManager.getInstance();
        mDevices = mLocalDeviceManager.getDevicesAddInSDK();

        findView();
        setListener();
    }

    private void findView() {
        mDevListView = (ListView) findViewById(R.id.my_dev_list);
        mDeviceAdapter  = new SDKDeviceAdapter(MyDeviceListActivity.this, mDevices);
        mDevListView.setAdapter(mDeviceAdapter);
    }

    private void setListener() {
        mDevListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("INTENT_DEV_ID", mDevices.get(position));
                intent.setClass(MyDeviceListActivity.this, DevMoreActivity.class);
                startActivity(intent);
            }
        });

        mDevListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                final BLDNADevice device = mDevices.get(position);
                AlertDialog.Builder dialog = new AlertDialog.Builder(MyDeviceListActivity.this);
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

    //设备列表适配器
    private class SDKDeviceAdapter extends ArrayAdapter<BLDNADevice> {
        public SDKDeviceAdapter(Context context, List<BLDNADevice> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyDeviceListActivity.SDKDeviceAdapter.ViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new MyDeviceListActivity.SDKDeviceAdapter.ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.sdk_adapter_device, null);
                viewHolder.name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.mac = (TextView) convertView.findViewById(R.id.tv_mac);
                viewHolder.type = (TextView) convertView.findViewById(R.id.tv_type);
                viewHolder.key = (TextView) convertView.findViewById(R.id.tv_key);
                viewHolder.status = (TextView) convertView.findViewById(R.id.tv_status);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (MyDeviceListActivity.SDKDeviceAdapter.ViewHolder) convertView.getTag();
            }

            BLDNADevice device = getItem(position);

            viewHolder.name.setText(device.getName());
            viewHolder.mac.setText("设备Mac: " + device.getMac());
            viewHolder.type.setText("设备类型: " + String.valueOf(device.getType()));
            viewHolder.key.setText("控制Key: " + device.getKey());

            String status;
            switch (device.getState()) {
                case 0:
                    status = "初始化状态";
                    break;
                case 1:
                    status = "局域网";
                    break;
                case 2:
                    status = "远程在线";
                    break;
                case 3:
                    status = "离线";
                    break;
                default:
                    status = "未知";
            }
            viewHolder.status.setText("设备状态: " + status);

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
