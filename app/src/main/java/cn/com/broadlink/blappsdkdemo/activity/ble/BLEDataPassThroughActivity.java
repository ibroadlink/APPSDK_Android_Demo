package cn.com.broadlink.blappsdkdemo.activity.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cn.com.broadlink.base.BLCommonTools;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.activity.ble.util.BLEManager;
import cn.com.broadlink.blappsdkdemo.activity.ble.util.BLEReadWriteCallBack;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseRecyclerAdapter;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseViewHolder;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.divideritemdecoration.BLDividerUtil;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.layoutmanager.BLLinearLayoutManager;

/**
 * 蓝牙透传控制
 *
 * @author JiangYaqiang
 * 2019/7/5 10:48
 */
public class BLEDataPassThroughActivity extends TitleActivity {

    private Button mBtClear;
    private EditText mEtInput;
    private EditText mEtOutput;
    private Button mBtCommit;
    private RecyclerView mRvContent;
    private TextView mTvError;
    private BluetoothDevice mDevice;
    private BluetoothGatt mGatt;
    private List<BluetoothGattService> mGattServices = new ArrayList<>();
    private MyAdapter mAdapter;
    private BluetoothGattCharacteristic mCharacterNotify = null;
    private BluetoothGattCharacteristic mCharacterWrite = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ble_data_passthrough);
        setBackWhiteVisible();
        setTitle("BLE Data PassThrough");

        initData();

        findView();

        initView();

        setListener();
    }

    private void findView() {
        mBtClear = (Button) findViewById(R.id.bt_clear);
        mEtInput = (EditText) findViewById(R.id.et_input);
        mEtOutput = (EditText) findViewById(R.id.et_output);
        mBtCommit = (Button) findViewById(R.id.bt_commit);
        mRvContent = (RecyclerView) findViewById(R.id.rv_content);
        mTvError = (TextView) findViewById(R.id.tv_error);
    }

    private void initData() {
        mDevice = getIntent().getParcelableExtra(BLConstants.INTENT_PARCELABLE);
        if(mDevice == null){
            BLToastUtils.show("Bluetooth Device Is Null");
            back();
            return;
        }

        mGatt = BLEManager.getInstance().getCachedConnection(mDevice.getAddress());
        if(mGatt == null){
            BLToastUtils.show("Bluetooth Device Not Connected");
            back();
            return;
        }
        
        mGattServices = mGatt.getServices();
        
        for (BluetoothGattService mGattService : mGattServices) {
            final List<BluetoothGattCharacteristic> characteristics = mGattService.getCharacteristics();
            if(characteristics != null){
                for (BluetoothGattCharacteristic characteristic : characteristics) {
                    int charaProp = characteristic.getProperties();
                    if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
                        mCharacterWrite = characteristic;
                    }
                    if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                        mCharacterNotify = characteristic;
                    }
                }
            }
        }
        
        if(mCharacterNotify != null){
            final boolean result = mGatt.setCharacteristicNotification(mCharacterNotify, true);
            if(result){
                BLToastUtils.show("Register Notify Success");
            }
        }

        BLEManager.getInstance().addCallback(mDevice.getAddress(), new BLEReadWriteCallBack(){

            @Override
            public void onReceiveMsg(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                final byte[] data = characteristic.getValue();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mEtOutput.setText(BLCommonTools.bytes2HexString(data));
                    }
                });
            }

            @Override
            public void onWriteMsg(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BLToastUtils.show("Write Success");
                    }
                });
            }

            @Override
            public void onReadMsg(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

            }
            
            @Override
            public void onDisconnected() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTvError.setVisibility(View.VISIBLE);
                        mTvError.setBackgroundColor(Color.RED);
                        mTvError.setText("BLE Disconnected");
                    }
                });

            }

            @Override
            public void onMTUChanged(BluetoothGatt gatt, final int mtu) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTvError.setVisibility(View.VISIBLE);
                        mTvError.setBackgroundColor(Color.GREEN);
                        mTvError.setText("MTU Changed to " + mtu);
                    }
                });
            }
        });
    }

    private void initView() {
        mAdapter = new MyAdapter();
        mRvContent.setLayoutManager(new BLLinearLayoutManager(mActivity));
        mRvContent.setAdapter(mAdapter);
        mRvContent.addItemDecoration(BLDividerUtil.getDefault(mActivity, mGattServices));
    }

    private void setListener() {
        mBtClear.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                mEtInput.setText("");
                mEtOutput.setText("");
            }
        });
        
        mBtCommit.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                write();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEManager.getInstance().removeCallback(mDevice.getAddress());
    }

    class MyAdapter extends BLBaseRecyclerAdapter<BluetoothGattService> {

        public MyAdapter() {
            super(mGattServices, R.layout.item_ble_dev);
        }

        @Override
        public void onBindViewHolder(BLBaseViewHolder holder, final int position) {
            super.onBindViewHolder(holder, position);
            final BluetoothGattService bluetoothDevice = mBeans.get(position);
            final UUID uuid = bluetoothDevice.getUuid();
            holder.setText(R.id.tv_name,  String.format("UUID: %s", uuid));

            final List<BluetoothGattCharacteristic> characteristics = bluetoothDevice.getCharacteristics();
            final StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < characteristics.size(); i++) {
                if(i!=0){
                    stringBuffer.append("\n");
                }
                stringBuffer.append(getDesc(characteristics.get(i)));
            }
            
            holder.setText(R.id.tv_mac, stringBuffer.toString());
        }
        
        private String getDesc(BluetoothGattCharacteristic characteristic){
            List<String> propNameList = new ArrayList<>();
            int charaProp = characteristic.getProperties();
            if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                propNameList.add("Read");
            }
            if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
                propNameList.add("Write");
            }
            if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
                propNameList.add("Write No Response");
            }
            if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                propNameList.add("Notify");
            }
            if ((charaProp & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
                propNameList.add("Indicate");
            }

            return characteristic.getUuid() +" - " + JSON.toJSONString(propNameList);
        }
    }
    
    private void write(){
        if(mCharacterWrite == null){
            BLToastUtils.show("No Write Character Found!");
            return;
        }
        if(TextUtils.isEmpty(mEtInput.getText())){
            BLToastUtils.show("Input Is Null!");
            return;
        }

        mCharacterWrite.setValue(BLCommonTools.parseStringToByte(mEtInput.getText().toString()));
        final boolean result = mGatt.writeCharacteristic(mCharacterWrite);
        if (!result) {
            BLToastUtils.show("Write Fail!");
        }
    }

}
