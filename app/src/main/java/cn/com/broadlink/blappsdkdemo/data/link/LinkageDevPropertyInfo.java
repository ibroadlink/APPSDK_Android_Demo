package cn.com.broadlink.blappsdkdemo.data.link;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import cn.com.broadlink.blappsdkdemo.R;

/**
 * Created by YeJin on 2016/9/13.
 */
public class LinkageDevPropertyInfo implements Parcelable {
    //设备名称
    private String dev_name;
    //设备类型,实例化后设备did
    private String idev_did;
    //参数
    private String ikey;
    //参数名称
    private String ikey_name;
    //参数值
    private int ref_value;
    //参数值名称
    private String ref_value_name;
    // 比较方式 0:上升到 1：下降到 2：大于 3:小于 4:变为
    private int trend_type;
    //设备类型 0：设备 1：云端数据
    private int type;

    private int keeptime;

    public String getDev_name() {
        return dev_name;
    }

    public void setDev_name(String dev_name) {
        this.dev_name = dev_name;
    }

    public String getIdev_did() {
        return idev_did;
    }

    public void setIdev_did(String idev_did) {
        this.idev_did = idev_did;
    }

    public String getIkey() {
        return ikey;
    }

    public void setIkey(String ikey) {
        this.ikey = ikey;
    }

    public String getIkey_name() {
        return ikey_name;
    }

    public void setIkey_name(String ikey_name) {
        this.ikey_name = ikey_name;
    }

    public int getRef_value() {
        return ref_value;
    }

    public void setRef_value(int ref_value) {
        this.ref_value = ref_value;
    }

    public String getRef_value_name() {
        return ref_value_name;
    }

    public void setRef_value_name(String ref_value_name) {
        this.ref_value_name = ref_value_name;
    }

    public int getTrend_type() {
        return trend_type;
    }

    public void setTrend_type(int trend_type) {
        this.trend_type = trend_type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.dev_name);
        dest.writeString(this.idev_did);
        dest.writeString(this.ikey);
        dest.writeString(this.ikey_name);
        dest.writeInt(this.ref_value);
        dest.writeString(this.ref_value_name);
        dest.writeInt(this.trend_type);
        dest.writeInt(this.type);
    }

    public LinkageDevPropertyInfo() {
    }

    protected LinkageDevPropertyInfo(Parcel in) {
        this.dev_name = in.readString();
        this.idev_did = in.readString();
        this.ikey = in.readString();
        this.ikey_name = in.readString();
        this.ref_value = in.readInt();
        this.ref_value_name = in.readString();
        this.trend_type = in.readInt();
        this.type = in.readInt();
    }

    public static final Creator<LinkageDevPropertyInfo> CREATOR = new Creator<LinkageDevPropertyInfo>() {
        @Override
        public LinkageDevPropertyInfo createFromParcel(Parcel source) {
            return new LinkageDevPropertyInfo(source);
        }

        @Override
        public LinkageDevPropertyInfo[] newArray(int size) {
            return new LinkageDevPropertyInfo[size];
        }
    };

    public String getString(Context context){
        int trend_type = getTrend_type();
        String trend_str = "";
        switch (trend_type) {//比较方式 0:上升到 1：下降到 2：大于 3:小于 4:变为
            case 0:
                trend_str = context.getString(R.string.str_change_up);
                break;
            case 1:
                trend_str = context.getString(R.string.str_change_down);
                break;
            case 2:
                trend_str = context.getString(R.string.str_change_greater);
                break;
            case 3:
                trend_str = context.getString(R.string.str_change_less);
                break;
            case 4:
                trend_str = context.getString(R.string.str_change_to);
                break;
        }

        return String.format("%1$s %2$s %3$s", getDev_name(), trend_str, getRef_value_name());
    }

    public int getKeeptime() {
        return keeptime;
    }

    public void setKeeptime(int keeptime) {
        this.keeptime = keeptime;
    }
}
