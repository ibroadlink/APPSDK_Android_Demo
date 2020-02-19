package cn.com.broadlink.blappsdkdemo.data.privatedata;

import cn.com.broadlink.blappsdkdemo.service.BLLocalDeviceManager;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;

/**
 * Desc
 *
 * @author JiangYaqiang
 * 2020/1/17 9:58
 */
public class BeanDevicePrivateDataCookie {

    public String name;
    public String token;
    public String pid;
    public String did;
    public BeanDevicePrivateDataCookie gateway;

    public BeanDevicePrivateDataCookie() {
    }
    public BeanDevicePrivateDataCookie(BLDNADevice device) {
        if(device==null) return;

        this.pid = device.getPid();
        this.name = device.getName();
        this.token = device.getKey();
        this.did = device.getDid();

        if(device.getpDid()!= null){
            final BLDNADevice gatewayDevice = BLLocalDeviceManager.getInstance().getCachedDevice(device.getpDid());
            if(gatewayDevice != null){
                this.gateway = new BeanDevicePrivateDataCookie(gatewayDevice);
            }
        }
    }
}
