package cn.com.broadlink.blappsdkdemo.activity.ble;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.base.BLCommonTools;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.activity.ble.util.BLEManager;
import cn.com.broadlink.blappsdkdemo.activity.ble.util.BLEReadWriteCallBack;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLLog;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.BLProgressDialog;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseRecyclerAdapter;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseViewHolder;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.divideritemdecoration.BLDividerUtil;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.layoutmanager.BLLinearLayoutManager;

/**
 * 蓝牙主页
 *
 * @author JiangYaqiang
 * 2019/7/5 10:48
 */
public class BLEMainActivity extends TitleActivity {

    public static final String TAG = "BL_BLE";
    private static final int MSG_REQUEST_MTU = 1;
    private static final int MSG_RECONNECT = 2;
    private static final int MSG_DISCONNECT = 3;
    private static final int MSG_UPDATA_SHOW_TEXT = 4;
    private static final int MSG_TOAST = 5;
    
    private EditText mEtResult;
    private Switch mSwtScan;
    private Button mBtDataPassThrough;
    private Button mBtMeter;
    private Button mBtDisconnect;
    private RecyclerView mRvContent;
    private BluetoothAdapter.LeScanCallback mScanCallback;
    private List<BluetoothDevice> mBLEDevList = new ArrayList<>();
    private List<Integer> mBLESsidList = new ArrayList<>();
    private MyAdapter mAdapter;
    private BLProgressDialog mProgressDialog;
    private String mSelectedAddress = null;
    private BluetoothGattCallback mGattCallBack;
    private volatile boolean mIsMtuChanged = false;
    private Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ble_main);
        setBackWhiteVisible();
        setTitle("BLE Main");

        initData();

        findView();

        initView();

        setListener();
        
    }

    private void findView() {
        mEtResult = (EditText) findViewById(R.id.et_result);
        mSwtScan = (Switch) findViewById(R.id.swt_scan);
        mBtDataPassThrough = (Button) findViewById(R.id.bt_data_pass_through);
        mBtMeter = (Button) findViewById(R.id.bt_meter);
        mBtDisconnect = (Button) findViewById(R.id.bt_disconnect);
        mRvContent = (RecyclerView) findViewById(R.id.rv_content);
    }

    @SuppressLint("HandlerLeak")
    private void initData() {

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                
                switch (msg.what) {
                    
                    // 尝试修改mtu，可一次收发对于20字节数据
                    case MSG_REQUEST_MTU:
                        BluetoothGatt gatt = (BluetoothGatt) msg.obj;
                        boolean res = gatt.requestMtu(247);
                        BLLog.d(BLEMainActivity.TAG, "requestMtu: " + res);
                        SystemClock.sleep(2000);
                        if(!mIsMtuChanged){
                            mHandler.sendMessage(mHandler.obtainMessage(MSG_RECONNECT, gatt));
                        }
                        break;
                        
                    //  连接蓝牙   
                    case MSG_RECONNECT:
                        BluetoothGatt gatt1 = (BluetoothGatt) msg.obj;
                        BLEManager.getInstance().connect(gatt1.getDevice(), mGattCallBack);
                        break;
                        
                    // 断开蓝牙
                    case MSG_DISCONNECT:
                        mHandler.removeMessages(MSG_REQUEST_MTU);
                        mHandler.removeMessages(MSG_RECONNECT);
                        BLEManager.getInstance().disconnect(getDevice());
                        break;
                        
                    // 更新显示
                    case MSG_UPDATA_SHOW_TEXT:
                        String msgStr= (String) msg.obj;
                        mEtResult.setText(msgStr);
                        forceAdapterChange();
                        break;
                        
                    //toast
                    case MSG_TOAST:
                        String msgStr1= (String) msg.obj;
                        BLToastUtils.show(msgStr1);
                        break;
                }
            }
        };
        
        mGattCallBack = new BluetoothGattCallback() {

            @Override
            public void onConnectionStateChange(final BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                BLLog.d(TAG, "onConnectionStateChange: " + gatt.getDevice().getName() + newState);

                if (newState == BluetoothGatt.STATE_CONNECTED && gatt.getDevice().getAddress().equalsIgnoreCase(mSelectedAddress)) {
                    
                    dismissDialog();
                    BLLog.d(TAG, "设备连接: " + gatt.getDevice().getName() + " - " + gatt.getDevice().getBondState());
                    BLEManager.getInstance().startScan(mScanCallback);
                    BLEManager.getInstance().addConnectionCache(gatt.getDevice().getAddress(), gatt);
                    gatt.discoverServices(); // 发现服务
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_REQUEST_MTU, gatt), 2000); // 2秒后修改mtu
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATA_SHOW_TEXT, JSON.toJSONString(gatt.getDevice())));
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_TOAST, gatt.getDevice().getName() + "Connected"));

                } else if (newState == BluetoothGatt.STATE_DISCONNECTED && gatt.getDevice().getAddress().equalsIgnoreCase(mSelectedAddress)) {
                    dismissDialog();
                    BLLog.d(TAG, "设备断开: " + gatt.getDevice().getName() + " - " + gatt.getDevice().getBondState());
                    BLEManager.getInstance().startScan(mScanCallback);
                    BLEManager.getInstance().removeConnectionCache(gatt.getDevice().getAddress());
                    gatt.close(); // 记得加上这个步骤，否则会出现，发一次收到多个回复的问题
                    mIsMtuChanged = false;
                    
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATA_SHOW_TEXT, ""));
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_TOAST, gatt.getDevice().getName() + "Disconnected"));
                    mSelectedAddress = null;
                    
                    final BLEReadWriteCallBack callback = BLEManager.getInstance().getCallback(gatt.getDevice().getAddress());
                    if (callback != null) {
                        callback.onDisconnected();
                    }
                } else {
                    BLToastUtils.show("Connection newState: " + newState);
                }
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);
                BLLog.d(TAG, "读取数据: " + BLCommonTools.bytes2HexString(characteristic.getValue()));
                final BLEReadWriteCallBack callback = BLEManager.getInstance().getCallback(gatt.getDevice().getAddress());
                if (callback != null) {
                    callback.onReadMsg(gatt, characteristic, status);
                }
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
                BLLog.d(TAG, "写入数据: " + BLCommonTools.bytes2HexString(characteristic.getValue()));
                final BLEReadWriteCallBack callback = BLEManager.getInstance().getCallback(gatt.getDevice().getAddress());
                if (callback != null) {
                    callback.onWriteMsg(gatt, characteristic, status);
                }
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                BLLog.d(TAG, "收到数据: " + BLCommonTools.bytes2HexString(characteristic.getValue()));
                final BLEReadWriteCallBack callback = BLEManager.getInstance().getCallback(gatt.getDevice().getAddress());
                if (callback != null) {
                    callback.onReceiveMsg(gatt, characteristic);
                }
            }

            @Override
            public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
                super.onMtuChanged(gatt, mtu, status);
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    BLLog.d(TAG, "MTU 已变更为: " + mtu);
                    mIsMtuChanged = true;
                    if(!BLEMainActivity.this.isFinishing()){
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_TOAST, "MTU 已变更为: " + mtu));
                    }
                    
                    final BLEReadWriteCallBack callback = BLEManager.getInstance().getCallback(gatt.getDevice().getAddress());
                    if (callback != null) {
                        callback.onMTUChanged(gatt, mtu);
                    }
                } else {
                    BLLog.d(TAG, String.format("MTU 变更失败 mtu[%d], status[%d]", mtu, status));
                }
            }
        };
    }

    private void initView() {

        mAdapter = new MyAdapter();
        mRvContent.setLayoutManager(new BLLinearLayoutManager(mActivity));
        mRvContent.setAdapter(mAdapter);
        mRvContent.addItemDecoration(BLDividerUtil.getWithBottom(mActivity, mBLEDevList));
        
       BLEManager.getInstance().init(mApplication);
        mScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                
                if(TextUtils.isEmpty(device.getName()))return;

                if(!mBLEDevList.contains(device)){
                    BLLog.d(TAG, "扫描到设备: " + device.getName() + " - " + JSON.toJSONString(device, true));
                    mBLEDevList.add(device);
                    mBLESsidList.add(rssi);
                    forceAdapterChange();
                }
            }
        };
        
        if(!BLEManager.getInstance().isBLE()){
            BLAlert.showAlert(mActivity, "", "Your phone BLE not supported", new OnSingleClickListener() {
                @Override
                public void doOnClick(View v) {
                    back();
                }
            });
        }

        mRvContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (BLEManager.getInstance().open()) {
                    BLEManager.getInstance().startScan(mScanCallback);
                }
            }
        }, 1000);
        
    }

    private void showDialog(String msg){
        if(mProgressDialog == null){
            mProgressDialog = BLProgressDialog.createDialog(mActivity, msg);
        }else{
            mProgressDialog.setMessage(msg);
        }
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }
    
    private void dismissDialog(){
        if(mProgressDialog != null && !BLEMainActivity.this.isFinishing()){
            mProgressDialog.dismiss();
        }
    }
    
    private void setListener() {
        mSwtScan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                BLLog.d(TAG, "onCheckedChanged " + isChecked);
                
                if(isChecked){
                    mBLEDevList.clear();
                    mBLESsidList.clear();
                    mSelectedAddress = null;
                    forceAdapterChange();
                    
                    if (BLEManager.getInstance().open()) {
                        BLEManager.getInstance().startScan(mScanCallback);
                    }
                    
                }else{
                    BLEManager.getInstance().stopScan(mScanCallback);
                }
            }
        });

        mBtDisconnect.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                mHandler.sendMessage(mHandler.obtainMessage(MSG_DISCONNECT, BLEManager.getInstance().getCachedConnection(mSelectedAddress)));
            }
        });

        mAdapter.setOnItemClickListener(new BLBaseRecyclerAdapter.OnClickListener() {
            @Override
            public void onClick(final int position, int viewType) {
                
                if(mBLEDevList.get(position).getAddress().equalsIgnoreCase(mSelectedAddress) && mIsMtuChanged){
                    return;
                }
                
                BLEManager.getInstance().stopScan(mScanCallback);
                mSelectedAddress = mBLEDevList.get(position).getAddress();
                forceAdapterChange();

                showDialog("Connecting...");
                BLEManager.getInstance().connect(mBLEDevList.get(position), mGattCallBack);
            }
        });
        
        
        mBtDataPassThrough.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                final BluetoothDevice device = getDevice();
                if(device == null){
                    BLToastUtils.show("You should connect a bluetooth device first!");
                    return;
                }
                mSwtScan.setChecked(false);
                BLCommonUtils.toActivity(mActivity, BLEDataPassThroughActivity.class, device);
            }
        });
        
        mBtMeter.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                final BluetoothDevice device = getDevice();
                if(device == null){
                    BLToastUtils.show("You should connect a bluetooth device first!");
                    return;
                }
                mSwtScan.setChecked(false);
                BLCommonUtils.toActivity(mActivity, BLEMeterActivity.class, device);
            }
        });
    }

    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BLEManager.REQUEST_CODE_BLE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "BLE opened success", Toast.LENGTH_SHORT).show();
                BLEManager.getInstance().startScan(mScanCallback);
                
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "BLE open fail, ,no BLE permission", Toast.LENGTH_SHORT).show();
                back();
            }
        }
    }
    
    private void forceAdapterChange(){
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(MSG_REQUEST_MTU);
        mHandler.removeMessages(MSG_RECONNECT);
        mHandler.removeMessages(MSG_DISCONNECT);
    }

    private BluetoothDevice getDevice(){
        if(mSelectedAddress == null) return null;
        
        for (BluetoothDevice bluetoothDevice : mBLEDevList) {
            if(bluetoothDevice.getAddress().equalsIgnoreCase(mSelectedAddress)){
                return bluetoothDevice;
            }
        }
        return null;
    }

    class MyAdapter extends BLBaseRecyclerAdapter<BluetoothDevice> {

        public MyAdapter() {
            super(mBLEDevList, R.layout.item_ble_dev);
        }

        @Override
        public void onBindViewHolder(BLBaseViewHolder holder, final int position) {
            super.onBindViewHolder(holder, position);
            final BluetoothDevice bluetoothDevice = mBeans.get(position);
            holder.setText(R.id.tv_name,  String.format("%s  %ddBm", bluetoothDevice.getName(),mBLESsidList.get(position)));
            holder.setText(R.id.tv_mac, bluetoothDevice.getAddress());
            holder.setBackgroundColor(R.id.rl_root, mBeans.get(position).getAddress().equalsIgnoreCase(mSelectedAddress) ? Color.LTGRAY : Color.TRANSPARENT);
        }
    }

}
