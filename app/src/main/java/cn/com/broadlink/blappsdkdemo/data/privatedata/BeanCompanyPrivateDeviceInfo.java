package cn.com.broadlink.blappsdkdemo.data.privatedata;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.base.fastjson.BLJSON;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.service.BLLocalDeviceManager;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;

/**
 * Desc
 *
 * @author JiangYaqiang
 * 2020/1/17 9:50
 */
public class BeanCompanyPrivateDeviceInfo {

    public String endpointId;
    public String friendlyName;
    public String gatewayId;
    public String mac;
    public String productId;
    public String icon;
    public String roomId;
    public int order;
    public String cookie;
    public String vGroup;
    public String extend;
    public int subdevicenum;
    public String extern;
    public int devicetypeFlag;
    public List<BeanDevicePrivateDataGroup> groupdevice = new ArrayList<>();
    public BeanCompanyPrivateDeviceInfo gatewayInfo;

    public BeanCompanyPrivateDeviceInfo() {
    }
    public BeanCompanyPrivateDeviceInfo(BLDNADevice device) {
        if(device==null) return;
        
        this.endpointId = device.getDid();
        this.friendlyName = device.getName();
        this.gatewayId = device.getpDid();
        this.mac = device.getMac();
        this.extend = device.getExtend();
        this.devicetypeFlag = device.getDeviceFlag();
        this.cookie = BLCommonUtils.Base64(BLJSON.toJSONString(new BeanDevicePrivateDataCookie(device)));

        if(this.gatewayId != null){
            final BLDNADevice gatewayDevice = BLLocalDeviceManager.getInstance().getCachedDevice(this.gatewayId);
            if(gatewayDevice != null){
                this.gatewayInfo = new BeanCompanyPrivateDeviceInfo(gatewayDevice);
            }
        }
    }
}
