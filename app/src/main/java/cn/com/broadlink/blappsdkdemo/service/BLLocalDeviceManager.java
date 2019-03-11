package cn.com.broadlink.blappsdkdemo.service;

import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.broadlink.base.BLCommonTools;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;
import cn.com.broadlink.sdk.interfaces.controller.BLDeviceScanListener;
import cn.com.broadlink.sdk.interfaces.controller.BLDeviceStateChangedListener;
import cn.com.broadlink.sdk.result.controller.BLPairResult;

/**
 * Created by zhujunjie on 2017/8/8.
 */

public class BLLocalDeviceManager {
    private HashMap<String, BLDNADevice> mMapDevice = new HashMap<>();
    private List<BLDNADevice> devicesProbeInLan = new ArrayList<>();

    private HashMap<String, BLDNADevice> mMapDeviceInSDK = new HashMap<>();
    private List<BLDNADevice> devicesAddInSDK = new ArrayList<>();
    private Handler checkHandler = new Handler();
    private Runnable checkRunnable;

    private BLLocalDeviceListener blLocalDeviceListener = null;

    private static BLLocalDeviceManager localDeviceManager = null;

    private BLLocalDeviceManager() {

        BLLet.Controller.startProbe(2000);
        BLLet.Controller.setOnDeviceScanListener(new BLDeviceScanListener() {
            @Override
            public void onDeviceUpdate(BLDNADevice bldnaDevice, boolean isNew) {
                addDeviceInList(bldnaDevice);
            }
        });

        BLLet.Controller.setOnDeviceStateChangedListener(new BLDeviceStateChangedListener() {
            @Override
            public void onChanged(String did, int state) {
                BLCommonTools.debug("Device did=" + did + " State=" + state);
            }
        });

        checkRunnable = new Runnable() {
            @Override
            public void run() {
                checkLocalDeviceList();

                checkHandler.postDelayed(this, 5 * 1000);
            }
        };
        checkHandler.postDelayed(checkRunnable, 5 * 1000);
    }

    public static BLLocalDeviceManager getInstance() {
        synchronized (BLLocalDeviceManager.class) {
            if (localDeviceManager == null) {
                localDeviceManager = new BLLocalDeviceManager();
            }
        }

        return localDeviceManager;
    }

    public void setBlLocalDeviceListener(BLLocalDeviceListener listener) {
        blLocalDeviceListener = listener;
    }

    public List<BLDNADevice> getLocalDevices() {
        devicesProbeInLan.clear();
        // 获取所有的设备
        synchronized (mMapDevice) {
            for (String key : mMapDevice.keySet()) {
                devicesProbeInLan.add(mMapDevice.get(key));
            }
        }
        return devicesProbeInLan;
    }

    public void addDeviceIntoSDK(BLDNADevice device) {
        BLLet.Controller.addDevice(device);
        synchronized (mMapDeviceInSDK) {
            mMapDeviceInSDK.put(device.getDid(), device);
        }
    }

    public void removeDeviceFromSDK(String did) {
        BLLet.Controller.removeDevice(did);
        synchronized (mMapDeviceInSDK) {
            mMapDeviceInSDK.remove(did);
        }
    }

    public List<BLDNADevice> getDevicesAddInSDK() {
        devicesAddInSDK.clear();
        synchronized (mMapDeviceInSDK) {
            for (String key : mMapDeviceInSDK.keySet()) {
                devicesAddInSDK.add(mMapDeviceInSDK.get(key));
            }
        }
        return devicesAddInSDK;
    }
    
    
    public BLDNADevice getCachedDevice(String did) {
        devicesAddInSDK.clear();
        synchronized (mMapDeviceInSDK) {
            for (String key : mMapDeviceInSDK.keySet()) {
                final BLDNADevice device = mMapDeviceInSDK.get(key);
                devicesAddInSDK.add(device);
                if(device.getDid().equalsIgnoreCase(did)){
                    return device;
                }
            }
        }
        return null;
    }
    private void checkLocalDeviceList() {

        long curTime = System.currentTimeMillis();
        HashMap<String, BLDNADevice> mapDevice = new HashMap<String, BLDNADevice>(mMapDevice.size());
        synchronized (mMapDevice) {
            mapDevice.putAll(mMapDevice);
        }

        for (String key : mapDevice.keySet()) {
            BLDNADevice device = mapDevice.get(key);
            if (curTime - device.getFreshStateTime() > 15 * 1000) {
                removeDeviceFromList(key);
            }
        }
    }

    private void addDeviceInList(BLDNADevice bldnaDevice) {
        if (!mMapDevice.containsKey(bldnaDevice.getDid())) {

            //设备Pair
            BLPairResult pairResult = BLLet.Controller.pair(bldnaDevice);
            if (pairResult.succeed()) {
                //只有成功才能添加

                bldnaDevice.setId(pairResult.getId());
                bldnaDevice.setKey(pairResult.getKey());

                synchronized (mMapDevice) {
                    mMapDevice.put(bldnaDevice.getDid(), bldnaDevice);
                }
                if (blLocalDeviceListener != null) {
                    blLocalDeviceListener.deviceChange();
                }
                Log.d(BLConstants.BROADLINK_LOG_TAG, "add device did:" + bldnaDevice.getDid());

            }

        } else {
            synchronized (mMapDevice) {
                mMapDevice.put(bldnaDevice.getDid(), bldnaDevice);
            }
        }
    }

    private void removeDeviceFromList(String did) {
        synchronized (mMapDevice) {
            mMapDevice.remove(did);
        }
        if (blLocalDeviceListener != null) {
            blLocalDeviceListener.deviceChange();
        }
        Log.d(BLConstants.BROADLINK_LOG_TAG, "remove device did:" + did);
    }

}
