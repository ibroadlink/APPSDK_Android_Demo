package cn.com.broadlink.blappsdkdemo.data.link;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.blappsdkdemo.common.BLProfileTools;
import cn.com.broadlink.blappsdkdemo.data.BLDevProfileInfo;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;

/**
 * 联动信息
 * Created by YeJin on 2016/9/13.
 */
public class LinkageInfo implements Parcelable {
    public static final int ENABLE = 1;
    public static final int UNABLE = 0;

    /**时间触发**/
    public static final int RULE_TYPE_TIMER = 0;
    /**属性触发*/
    public static final int RULE_TYPE_CHARACTERISTIC = 1;
    /**位置触发**/
    public static final int RULE_TYPE_LOCATION = 2;

    /**联动设置来源 APP**/
    public static final String SOURCE_APP  = "APP";

    /**联动设置来源 通知模板**/
    public static final String SOURCE_NOTIFY = "notify_";


    /**
     * 家庭ID
     **/
    private String familyid;
    /**
     * 联动类型 0：时间 1:特性 2 位置
     **/
    private int ruletype;
    /**
     * 联动id
     */
    private String ruleid;
    /***
     * 名称
     **/
    private String rulename;
    /**
     * 特性触发
     **/
    private String characteristicinfo;
    /**
     * 位置触发
     **/
    private String locationinfo;
    /**
     * 场景ID
     **/
    private List<String> moduleid = new ArrayList<>();

    private int enable = ENABLE;

    private int delay;
    /**
     * 联动设置来源 APP 设置 或者 是IHG H5设置
     * 值为 null 或者 APP 表示有APP 设置的联动
     * 值位notify_xxx(xxx是模板id)的是 通知的联动
     **/
    private String source;

    private LinkageDevicesInfo linkagedevices;

    private List<LinkageSubscribeDevBaseInfo> subscribe;

    public boolean isLinkEnable(){
        return enable == ENABLE;
    }

    public String getFamilyid() {
        return familyid;
    }

    public void setFamilyid(String familyid) {
        this.familyid = familyid;
    }

    public String getRuleid() {
        return ruleid;
    }

    public void setRuleid(String ruleid) {
        this.ruleid = ruleid;
    }

    public String getRulename() {
        return rulename;
    }

    public void setRulename(String rulename) {
        this.rulename = rulename;
    }

    public String getCharacteristicinfo() {
        return characteristicinfo;
    }

    public void setCharacteristicinfo(String characteristicinfo) {
        this.characteristicinfo = characteristicinfo;
    }

    public String getLocationinfo() {
        return locationinfo;
    }

    public void setLocationinfo(String locationinfo) {
        this.locationinfo = locationinfo;
    }

    public List<String> getModuleid() {
        return moduleid;
    }

    public void setModuleid(List<String> moduleid) {
        this.moduleid = moduleid;
    }

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public LinkageDevicesInfo getLinkagedevices() {
        return linkagedevices;
    }

    public void setLinkagedevices(LinkageDevicesInfo linkagedevices) {
        this.linkagedevices = linkagedevices;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.familyid);
        dest.writeInt(this.ruletype);
        dest.writeString(this.ruleid);
        dest.writeString(this.rulename);
        dest.writeString(this.characteristicinfo);
        dest.writeString(this.locationinfo);
        dest.writeStringList(this.moduleid);
        dest.writeInt(this.enable);
        dest.writeInt(this.delay);
        dest.writeList(this.subscribe);
        dest.writeParcelable(this.linkagedevices,flags);
    }

    public LinkageInfo() {
    }

    protected LinkageInfo(Parcel in) {
        this.familyid = in.readString();
        this.ruletype = in.readInt();
        this.ruleid = in.readString();
        this.rulename = in.readString();
        this.characteristicinfo = in.readString();
        this.locationinfo = in.readString();
        this.moduleid = in.createStringArrayList();
        this.enable = in.readInt();
        this.delay = in.readInt();
        this.subscribe = new ArrayList<LinkageSubscribeDevBaseInfo>();
        in.readList(this.subscribe, LinkageSubscribeDevBaseInfo.class.getClassLoader());
        this.linkagedevices = in.readParcelable(LinkageDevicesInfo.class.getClassLoader());
    }

    public static final Creator<LinkageInfo> CREATOR = new Creator<LinkageInfo>() {
        @Override
        public LinkageInfo createFromParcel(Parcel source) {
            return new LinkageInfo(source);
        }

        @Override
        public LinkageInfo[] newArray(int size) {
            return new LinkageInfo[size];
        }
    };

    public void insertDevInfo(List<LinkageSubscribeDevBaseInfo> devList, BLDNADevice deviceInfo, BLDNADevice pDevInfo){
        if(!existDevInfo(devList, deviceInfo.getDid())){
            BLDevProfileInfo profileInfo = BLProfileTools.queryProfileInfoByDid(deviceInfo.getDeviceId());
            if(profileInfo != null){
                int subscribable = profileInfo.getSubscribable();
                if(subscribable == 1){
                    LinkageSubscribeDevBaseInfo baseInfo = new LinkageSubscribeDevBaseInfo();
                    baseInfo.setDid(deviceInfo.getDid());
                    baseInfo.setDidtype(pDevInfo != null ? 2 : 1);
                    devList.add(baseInfo);
                }
            }
        }

        //添加父设备
        if(pDevInfo != null){
            if(!existDevInfo(devList, pDevInfo.getDid())){
                BLDevProfileInfo profileInfo = BLProfileTools.queryProfileInfoByDid(pDevInfo.getDeviceId());
                if(profileInfo != null){
                    int subscribable = profileInfo.getSubscribable();
                    if(subscribable == 1){
                        LinkageSubscribeDevBaseInfo baseInfo = new LinkageSubscribeDevBaseInfo();
                        baseInfo.setDid(pDevInfo.getDid());
                        baseInfo.setDidtype(1);
                        devList.add(baseInfo);
                    }
                }
            }
        }
    }

    private boolean existDevInfo(List<LinkageSubscribeDevBaseInfo> devList, String did){
        for (LinkageSubscribeDevBaseInfo devBaseInfo : devList) {
            if(devBaseInfo.getDid().equals(did)){
                return true;
            }
        }
        return false;
    }

    public int getRuletype() {
        return ruletype;
    }

    public void setRuletype(int ruletype) {
        this.ruletype = ruletype;
    }

    public List<LinkageSubscribeDevBaseInfo> getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(List<LinkageSubscribeDevBaseInfo> subscribe) {
        this.subscribe = subscribe;
    }


    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}

