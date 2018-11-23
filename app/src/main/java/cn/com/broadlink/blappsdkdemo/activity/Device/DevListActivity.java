package cn.com.broadlink.blappsdkdemo.activity.Device;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.TitleActivity;
import cn.com.broadlink.blappsdkdemo.db.BLDeviceInfo;
import cn.com.broadlink.blappsdkdemo.db.BLDeviceInfoDao;
import cn.com.broadlink.blappsdkdemo.service.BLLocalDeviceListener;
import cn.com.broadlink.blappsdkdemo.service.BLLocalDeviceManager;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dev_list_layout);
        setTitle(R.string.Probe_Device_List);
        setBackWhiteVisible();

        mLocalDeviceManager = BLLocalDeviceManager.getInstance();
        mDevices = mLocalDeviceManager.getLocalDevices();

        findView();
        setListener();
    }

    private void findView() {
        mDevListView = findViewById(R.id.dev_list);
        mDeviceAdapter  = new DeviceAdapter(DevListActivity.this, mDevices);
        mDevListView.setAdapter(mDeviceAdapter);
    }

    private void setListener(){
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
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.show();

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
