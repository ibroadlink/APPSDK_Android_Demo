package cn.com.broadlink.blappsdkdemo.activity.family.result;

import java.util.List;

import cn.com.broadlink.base.BLBaseResult;

public class BLSFamilyUpdateResult extends BLBaseResult {

    private String version;
    private BLSFamilyAddInfo data;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public BLSFamilyAddInfo getData() {
        return data;
    }

    public void setData(BLSFamilyAddInfo data) {
        this.data = data;
    }

    public class BLSFamilyAddInfo {
        private String familyid;
        private String name;
        private String iconpath;
        private List<BLSRoomInfo> roominfo;

        public List<BLSRoomInfo> getRoominfo() {
            return roominfo;
        }

        public void setRoominfo(List<BLSRoomInfo> roominfo) {
            this.roominfo = roominfo;
        }

        public String getIconpath() {
            return iconpath;
        }

        public void setIconpath(String iconpath) {
            this.iconpath = iconpath;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFamilyid() {
            return familyid;
        }

        public void setFamilyid(String familyid) {
            this.familyid = familyid;
        }
    }
}
