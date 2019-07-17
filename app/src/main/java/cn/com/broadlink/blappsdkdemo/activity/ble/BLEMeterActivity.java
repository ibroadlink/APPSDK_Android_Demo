package cn.com.broadlink.blappsdkdemo.activity.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.base.BLCommonTools;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.activity.ble.bean.AddressInfo;
import cn.com.broadlink.blappsdkdemo.activity.ble.bean.BaseInfo;
import cn.com.broadlink.blappsdkdemo.activity.ble.util.BLEDataParser;
import cn.com.broadlink.blappsdkdemo.activity.ble.util.BLEManager;
import cn.com.broadlink.blappsdkdemo.activity.ble.util.BLEReadWriteCallBack;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;

/**
 * 电表
 *
 * @author JiangYaqiang
 * 2019/7/5 10:48
 */
public class BLEMeterActivity extends TitleActivity {

    private RelativeLayout mActivityMain;
    private EditText mEtResult;
    private Button mBtGetId;
    private Button mBtCharge;
    private Button mBtCheck;
    private Button mBtGetParam;
    private BluetoothDevice mDevice;
    private BluetoothGatt mGatt;
    private TextView mTvError;
    private TextView mTvId;
    private List<BluetoothGattService> mGattServices = new ArrayList<>();
    private BluetoothGattCharacteristic mCharacterNotify = null;
    private BluetoothGattCharacteristic mCharacterWrite = null;
    private String mSsid = null;
    private int mMtu;
    //private StringBuilder mCachedData = new StringBuilder(128);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ble_meter);
        setBackWhiteVisible();
        setTitle("Samsung Meter");

        initData();

        findView();
       
        setListener();
        
        initView();
    }

    private void findView() {
        mActivityMain = (RelativeLayout) findViewById(R.id.activity_main);
        mEtResult = (EditText) findViewById(R.id.et_result);
        mBtGetId = (Button) findViewById(R.id.bt_get_id);
        mBtCharge = (Button) findViewById(R.id.bt_charge);
        mBtCheck = (Button) findViewById(R.id.bt_check);
        mBtGetParam = (Button) findViewById(R.id.bt_get_param);
        mTvError = (TextView) findViewById(R.id.tv_error);
        mTvId = (TextView) findViewById(R.id.tv_id);
    }

    private void initData() {
        mDevice = getIntent().getParcelableExtra(BLConstants.INTENT_PARCELABLE);
        if (mDevice == null) {
            BLToastUtils.show("Bluetooth Device Is Null");
            back();
        }
        
        mMtu = getIntent().getIntExtra(BLConstants.INTENT_VALUE, -1);
        
        mGatt = BLEManager.getInstance().getCachedConnection(mDevice.getAddress());
        if (mGatt == null) {
            BLToastUtils.show("Bluetooth Device Not Connected");
            back();
            return;
        }

        mGattServices = mGatt.getServices();

        for (BluetoothGattService mGattService : mGattServices) {
            final List<BluetoothGattCharacteristic> characteristics = mGattService.getCharacteristics();
            if (characteristics != null) {
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

        if (mCharacterNotify != null) {
            final boolean result = mGatt.setCharacteristicNotification(mCharacterNotify, true);
            if (!result) {
                BLToastUtils.show("Register Notify Fail!!");
            }
        }

        BLEManager.getInstance().addCallback(mDevice.getAddress(), new BLEReadWriteCallBack() {

            @Override
            public void onReceiveMsg(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {

                final byte[] value = characteristic.getValue();
                final BaseInfo baseInfo = BLEDataParser.parseBytes(value);
              
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (baseInfo != null) {
                            if (baseInfo instanceof AddressInfo) {
                                mSsid = ((AddressInfo) baseInfo).getAddress();
                                mTvId.setText(String.format("ID: %s", mSsid));
                            }

                            mEtResult.setText(JSON.toJSONString(baseInfo, true));
                        } else {
                            mEtResult.setText(BLCommonTools.bytes2HexString(value));
                        }
                    }
                });
            }

            @Override
            public void onWriteMsg(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                if (status != BluetoothGatt.GATT_SUCCESS) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            BLToastUtils.show("Write Fail!");
                        }
                    });

                }
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
                mMtu = mtu;
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
        mBtGetId.performClick();
    }

    private void setListener() {

        mBtGetId.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                mEtResult.setText("");
                final byte[] bytes = BLEDataParser.genGetAddress();
                write(bytes);
            }
        });

        mBtGetParam.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                if (mSsid == null) {
                    BLToastUtils.show("Get Meter Id First.");
                    return;
                }
                mEtResult.setText("");
                final byte[] bytes = BLEDataParser.genInquiryBytes(mSsid);
                write(bytes);
            }
        });

        mBtCharge.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                if (mSsid == null) {
                    BLToastUtils.show("Get Meter Id First.");
                    return;
                }

                BLAlert.showEditDilog(mActivity, "Input Token", null, new BLAlert.BLEditDialogOnClickListener() {
                    @Override
                    public void onClink(String value) {
                        mEtResult.setText("");
                        final byte[] bytes = BLEDataParser.genRechargeBytes(value, mSsid);
                        write(bytes);
                    }

                    @Override
                    public void onCancel(String value) {

                    }
                }, false);
            }
        });

        mBtCheck.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                if (mSsid == null) {
                    BLToastUtils.show("Get Meter Id First.");
                    return;
                }

                final byte[] bytes = BLEDataParser.genBalanceBytes(mSsid);
                write(bytes);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEManager.getInstance().removeCallback(mDevice.getAddress());
    }

    private void write(byte[] cmd) {
        if (mCharacterWrite == null) {
            BLToastUtils.show("No Write Character Found!");
            return;
        }

        mCharacterWrite.setValue(cmd);
        final boolean result = mGatt.writeCharacteristic(mCharacterWrite);
        if (!result) {
            BLToastUtils.show("Write Fail!");
        }
    }
}
