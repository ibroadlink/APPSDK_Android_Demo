package cn.com.broadlink.blappsdkdemo.activity.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Desc
 *
 * @author JiangYaqiang
 * 2019/7/8 17:07
 */
public interface BLEReadWriteCallBack {
    
    void onReceiveMsg(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);

    void onWriteMsg(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);

    void onReadMsg(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);

    void onDisconnected();
    
    void onMTUChanged(BluetoothGatt gatt,int mtu);
    
}
