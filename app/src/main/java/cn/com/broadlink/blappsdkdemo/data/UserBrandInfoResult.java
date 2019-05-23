package cn.com.broadlink.blappsdkdemo.data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/8/16 0016.
 */

public class UserBrandInfoResult extends BaseResult {

    private List<UserBrandInfo> ircodeList;

    public List<UserBrandInfo> getIrcodeList() {
        return ircodeList;
    }

    public void setIrcodeList(List<UserBrandInfo> ircodeList) {
        this.ircodeList = ircodeList;
    }

    public static class UserBrandInfo implements Serializable {

        private int irid;
        private String moduleid;
        private double averscore;
        private String brand;
        private String model;
        private String type;
        private String sharetime;
        private String countryid;
        private String cityid;
        private String providerid;
        private String provinceid;
        private String extend;
        private String pid;
        private List<String> picfile;
        private List<UserBrandCodeData> data;
        private String moduleName;
        private String familyName;
        private String roomName;
        private String nickName;
        private int downloadnum;
        private String share;
        private boolean isChecked;
        public int getIrid() {
            return irid;
        }

        public void setIrid(int irid) {
            this.irid = irid;
        }

        public String getModuleid() {
            return moduleid;
        }

        public void setModuleid(String moduleid) {
            this.moduleid = moduleid;
        }

        public double getAverscore() {
            return averscore;
        }

        public void setAverscore(double averscore) {
            this.averscore = averscore;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSharetime() {
            return sharetime;
        }

        public void setSharetime(String sharetime) {
            this.sharetime = sharetime;
        }

        public String getCountryid() {
            return countryid;
        }

        public void setCountryid(String countryid) {
            this.countryid = countryid;
        }

        public String getCityid() {
            return cityid;
        }

        public void setCityid(String cityid) {
            this.cityid = cityid;
        }

        public String getProviderid() {
            return providerid;
        }

        public void setProviderid(String providerid) {
            this.providerid = providerid;
        }

        public String getProvinceid() {
            return provinceid;
        }

        public void setProvinceid(String provinceid) {
            this.provinceid = provinceid;
        }

        public String getExtend() {
            return extend;
        }

        public void setExtend(String extend) {
            this.extend = extend;
        }

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public List<String> getPicfile() {
            return picfile;
        }

        public void setPicfile(List<String> picfile) {
            this.picfile = picfile;
        }

        public List<UserBrandCodeData> getData() {
            return data;
        }

        public void setData(List<UserBrandCodeData> data) {
            this.data = data;
        }

        public String getModuleName() {
            return moduleName;
        }

        public void setModuleName(String moduleName) {
            this.moduleName = moduleName;
        }

        public String getFamilyName() {
            return familyName;
        }

        public void setFamilyName(String familyName) {
            this.familyName = familyName;
        }

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public int getDownloadnum() {
            return downloadnum;
        }

        public void setDownloadnum(int downloadnum) {
            this.downloadnum = downloadnum;
        }

        public String isShare() {
            return share;
        }

        public void setShare(String share) {
            this.share = share;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }
    }

    public static class UserBrandCodeData implements Serializable {

        private int channelId;
        private String function;
        private int index;
        private String name;
        private int orderIndex;
        private int type;
        private String extend;
        private String background;
        private List<CodeListBean> codeList;

        public int getChannelId() {
            return channelId;
        }

        public void setChannelId(int channelId) {
            this.channelId = channelId;
        }

        public String getFunction() {
            return function;
        }

        public void setFunction(String function) {
            this.function = function;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getOrderIndex() {
            return orderIndex;
        }

        public void setOrderIndex(int orderIndex) {
            this.orderIndex = orderIndex;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getExtend() {
            return extend;
        }

        public void setExtend(String extend) {
            this.extend = extend;
        }

        public String getBackground() {
            return background;
        }

        public void setBackground(String background) {
            this.background = background;
        }

        public List<CodeListBean> getCodeList() {
            return codeList;
        }

        public void setCodeList(List<CodeListBean> codeList) {
            this.codeList = codeList;
        }

        public static class CodeListBean implements Serializable {

            private int buttonId;
            private byte[] code;
            private int codeId;
            private int delay;
            private int orderIndex;

            public int getButtonId() {
                return buttonId;
            }

            public void setButtonId(int buttonId) {
                this.buttonId = buttonId;
            }

            public byte[] getCode() {
                return code;
            }

            public void setCode(byte[] code) {
                this.code = code;
            }

            public int getCodeId() {
                return codeId;
            }

            public void setCodeId(int codeId) {
                this.codeId = codeId;
            }

            public int getDelay() {
                return delay;
            }

            public void setDelay(int delay) {
                this.delay = delay;
            }

            public int getOrderIndex() {
                return orderIndex;
            }

            public void setOrderIndex(int orderIndex) {
                this.orderIndex = orderIndex;
            }
        }
    }
}
