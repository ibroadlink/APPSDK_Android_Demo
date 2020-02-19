package cn.com.broadlink.blappsdkdemo.data.privatedata;

import cn.com.broadlink.base.fastjson.BLJSON;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;

/**
 * Desc
 *
 * @author JiangYaqiang
 * 2020/1/17 9:50
 */
public class ParamCompanyPrivateAdd {
    public String mtag = "device_info_v1";
    public String companyPerm = "rw";
    public String data;

    public ParamCompanyPrivateAdd() {
    }
    
    public ParamCompanyPrivateAdd(BLDNADevice device) {
        this.mtag = String.format("device_info_v1_%s", device.getDid());
        final BeanDevicePrivateDataCookie beanCompanyPrivateDeviceInfo = new BeanDevicePrivateDataCookie(device);
        this.data = BLJSON.toJSONString(beanCompanyPrivateDeviceInfo);
    }
    
}
