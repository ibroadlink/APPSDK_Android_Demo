package cn.com.broadlink.blappsdkdemo.data.link;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2018/3/8 0008.
 */

public class LinkageDevicesInfo implements Parcelable {

    private String extern;

    private String linkagetype;

    private String did;

    public LinkageDevicesInfo(){

    }

    public LinkageDevicesInfo(Parcel in) {
        this.extern = in.readString();
        this.linkagetype = in.readString();
        this.did = in.readString();
    }

    public static final Creator<LinkageDevicesInfo> CREATOR = new Creator<LinkageDevicesInfo>() {
        @Override
        public LinkageDevicesInfo createFromParcel(Parcel in) {
            return new LinkageDevicesInfo(in);
        }

        @Override
        public LinkageDevicesInfo[] newArray(int size) {
            return new LinkageDevicesInfo[size];
        }
    };

    public String getExtern() {
        return extern;
    }

    public void setExtern(String extern) {
        this.extern = extern;
    }

    public String getLinkagetype() {
        return linkagetype;
    }

    public void setLinkagetype(String linkagetype) {
        this.linkagetype = linkagetype;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(extern);
        parcel.writeString(linkagetype);
        parcel.writeString(did);
    }
}
