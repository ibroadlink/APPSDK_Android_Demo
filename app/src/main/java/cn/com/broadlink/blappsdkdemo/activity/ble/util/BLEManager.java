package cn.com.broadlink.blappsdkdemo.activity.ble.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import java.util.HashMap;
import java.util.Map;

import cn.com.broadlink.blappsdkdemo.BLApplication;

/**
 * 蓝牙管理类
 *
 * @author JiangYaqiang
 * 2019/7/5 11:11
 */
public class BLEManager {
    public static final int REQUEST_CODE_BLE = 111;
    private static volatile BLEManager instance;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;
    private BLApplication mAppContext;
    private Map<String, BluetoothGatt> mCachedConnectionList = new HashMap<>(); // 缓存多个蓝牙设备的连接状态
    private Map<String, BLEReadWriteCallBack> mCachedReadWriteCallbackList = new HashMap<>(); // 缓存多个蓝牙连接的回调

    private BLEManager() {

    }

    public static final BLEManager getInstance() {
        if (instance == null) {
            synchronized (BLEManager.class) {
                if (instance == null) {
                    instance = new BLEManager();
                }
            }
        }
        return instance;
    }

    public void init(BLApplication application) {
        mAppContext = application;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (isBLE()) {
            mBluetoothManager = (BluetoothManager) mAppContext.getSystemService(Context.BLUETOOTH_SERVICE);
        }
    }

    public boolean isBLE() {
        return mAppContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    public boolean open() {
        assert mBluetoothAdapter != null;
        // 询问打开蓝牙
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mAppContext.getTopActivity().startActivityForResult(enableBtIntent, REQUEST_CODE_BLE);
            return false;
        }
        return true;
    }

    public void startScan(BluetoothAdapter.LeScanCallback callback) {
        mBluetoothAdapter.startLeScan(callback);
    }

    public void stopScan(BluetoothAdapter.LeScanCallback callback) {
        mBluetoothAdapter.stopLeScan(callback);
    }

    public void connect(BluetoothDevice device, BluetoothGattCallback callback) {
        assert device != null;

        device.connectGatt(mAppContext, false, callback);
    }

    public void disconnect(BluetoothDevice device) {
        if(device == null)return;
        
        final BluetoothGatt cachedConnection = getCachedConnection(device.getAddress());
        if(cachedConnection != null){
            cachedConnection.disconnect();
            removeConnectionCache(device.getAddress());
        }
    }
    
    public void addConnectionCache(String address, BluetoothGatt connection){
        mCachedConnectionList.put(address, connection);
    }
    
    public void removeConnectionCache(String address){
        mCachedConnectionList.remove(address);
    }
    
    public BluetoothGatt getCachedConnection(String address){
        return mCachedConnectionList.get(address);
    }
    
    public void addCallback(String address, BLEReadWriteCallBack connection){
        mCachedReadWriteCallbackList.put(address, connection);
    }
    
    public void removeCallback(String address){
        mCachedReadWriteCallbackList.remove(address);
    }
    
    public BLEReadWriteCallBack getCallback(String address){
        return mCachedReadWriteCallbackList.get(address);
    }
    

    public int getConnectState(BluetoothDevice bleDevice) {
        if (bleDevice != null) {
            return mBluetoothManager.getConnectionState(bleDevice, BluetoothProfile.GATT);
        } else {
            return BluetoothProfile.STATE_DISCONNECTED;
        }
    }

    public boolean isConnected(BluetoothDevice bleDevice) {
        return getConnectState(bleDevice) == BluetoothProfile.STATE_CONNECTED;
    }

}
