package cn.com.broadlink.blappsdkdemo.data.timer;


import android.os.Parcel;

import cn.com.broadlink.sdk.data.controller.BLStdData;

/**
 * {
 * "type": "comm",
 * "time": "00_06_15_22_02_*_2019",
 * "name": "comm",
 * "cmd": {
 * "params": ["pwr"],
 * "vals": [
 * [{
 * "val": 1,
 * "idx": 1
 * }]
 * ]
 * },
 * "id": -1,
 * "en": 1
 * }
 */
public class BLTimerInfoV2 extends BLBaseTimerInfoV2 {
    public static final Creator<BLTimerInfoV2> CREATOR = new Creator<BLTimerInfoV2>() {
        @Override
        public BLTimerInfoV2 createFromParcel(Parcel source) {return new BLTimerInfoV2(source);}

        @Override
        public BLTimerInfoV2[] newArray(int size) {return new BLTimerInfoV2[size];}
    };
    private String time;
    private BLStdData cmd;

    public BLTimerInfoV2() {}

    protected BLTimerInfoV2(Parcel in) {
        super(in);
        this.time = in.readString();
        this.cmd = in.readParcelable(BLStdData.class.getClassLoader());
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public BLStdData getCmd() {
        return cmd;
    }

    public void setCmd(BLStdData cmd) {
        this.cmd = cmd;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.time);
        dest.writeParcelable(this.cmd, flags);
    }
}
