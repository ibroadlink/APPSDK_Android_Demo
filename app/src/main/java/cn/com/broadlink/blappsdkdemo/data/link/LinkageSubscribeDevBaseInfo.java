package cn.com.broadlink.blappsdkdemo.data.link;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 联动订阅设备信息
 * Created by YeJin on 2016/9/14.
 */
public class LinkageSubscribeDevBaseInfo implements Parcelable {
    private String did;

    private int didtype;

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public int getDidtype() {
        return didtype;
    }

    public void setDidtype(int didtype) {
        this.didtype = didtype;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.did);
        dest.writeInt(this.didtype);
    }

    public LinkageSubscribeDevBaseInfo() {
    }

    protected LinkageSubscribeDevBaseInfo(Parcel in) {
        this.did = in.readString();
        this.didtype = in.readInt();
    }

    public static final Creator<LinkageSubscribeDevBaseInfo> CREATOR = new Creator<LinkageSubscribeDevBaseInfo>() {
        @Override
        public LinkageSubscribeDevBaseInfo createFromParcel(Parcel source) {
            return new LinkageSubscribeDevBaseInfo(source);
        }

        @Override
        public LinkageSubscribeDevBaseInfo[] newArray(int size) {
            return new LinkageSubscribeDevBaseInfo[size];
        }
    };
}
