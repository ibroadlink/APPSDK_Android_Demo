package cn.com.broadlink.blappsdkdemo.activity.family.result;

import cn.com.broadlink.base.BLBaseResult;

public class BLSFamilyQrResult extends BLBaseResult {

    private DataQrCode data;

    public DataQrCode getData() {
        return data;
    }

    public void setData(DataQrCode data) {
        this.data = data;
    }
}
