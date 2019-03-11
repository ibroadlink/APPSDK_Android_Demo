package cn.com.broadlink.blappsdkdemo.data.timer;

import android.os.Parcel;
import android.os.Parcelable;

import cn.com.broadlink.blappsdkdemo.data.timer.constant.BLTimerConstants;

public class BLBaseTimerInfoV2 implements Parcelable {
    protected String type = BLTimerConstants.TYPE.COMM;
    protected int id;
    protected int en = 1;
    protected String name = BLTimerConstants.TYPE.COMM;

    public BLBaseTimerInfoV2() {}

    protected BLBaseTimerInfoV2(Parcel in) {
        this.type = in.readString();
        this.id = in.readInt();
        this.en = in.readInt();
        this.name = in.readString();
    }

    public static final Creator<BLBaseTimerInfoV2> CREATOR = new Creator<BLBaseTimerInfoV2>() {
        @Override
        public BLBaseTimerInfoV2 createFromParcel(Parcel in) {
            return new BLBaseTimerInfoV2(in);
        }

        @Override
        public BLBaseTimerInfoV2[] newArray(int size) {
            return new BLBaseTimerInfoV2[size];
        }
    };

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEn() {
        return en;
    }

    public void setEn(int en) {
        this.en = en;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeInt(this.id);
        dest.writeInt(this.en);
        dest.writeString(this.name);
    }
}
