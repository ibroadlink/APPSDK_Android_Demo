package cn.com.broadlink.blappsdkdemo.activity.family.result;

import cn.com.broadlink.base.BLBaseResult;

public class BLSFamilyInfoResult extends BLBaseResult {

    private BLSFamilyInfoData data;

    public BLSFamilyInfoData getData() {
        return data;
    }

    public void setData(BLSFamilyInfoData data) {
        this.data = data;
    }

    public class BLSFamilyInfoData {
        private BLSFamilyInfo familyInfo;

        public BLSFamilyInfo getFamilyInfo() {
            return familyInfo;
        }

        public void setFamilyInfo(BLSFamilyInfo familyInfo) {
            this.familyInfo = familyInfo;
        }
    }

}
