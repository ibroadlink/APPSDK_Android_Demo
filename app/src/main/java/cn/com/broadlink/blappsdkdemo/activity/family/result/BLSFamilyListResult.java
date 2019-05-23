package cn.com.broadlink.blappsdkdemo.activity.family.result;

import java.util.List;

import cn.com.broadlink.base.BLBaseResult;

public class BLSFamilyListResult extends BLBaseResult {

    private BLSFamilyListInfo data;

    public BLSFamilyListInfo getData() {
        return data;
    }

    public void setData(BLSFamilyListInfo data) {
        this.data = data;
    }

    public class BLSFamilyListInfo {
        private List<BLSFamilyInfo> familyList;

        public List<BLSFamilyInfo> getFamilyList() {
            return familyList;
        }

        public void setFamilyList(List<BLSFamilyInfo> familyList) {
            this.familyList = familyList;
        }
    }

}
