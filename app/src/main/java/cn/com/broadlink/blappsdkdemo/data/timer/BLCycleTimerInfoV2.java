package cn.com.broadlink.blappsdkdemo.data.timer;

import android.os.Parcel;
import android.os.Parcelable;

public class BLCycleTimerInfoV2 extends BLBaseTimerInfoV2 implements Parcelable {
    public static final Creator<BLCycleTimerInfoV2> CREATOR = new Creator<BLCycleTimerInfoV2>() {
        @Override
        public BLCycleTimerInfoV2 createFromParcel(Parcel source) {return new BLCycleTimerInfoV2(source);}

        @Override
        public BLCycleTimerInfoV2[] newArray(int size) {return new BLCycleTimerInfoV2[size];}
    };
    private String cmd1;
    private String cmd2;
    private String time1;
    private String time2;
    private String stime;
    private String etime;

    public BLCycleTimerInfoV2() {}

    protected BLCycleTimerInfoV2(Parcel in) {
        this.cmd1 = in.readString();
        this.cmd2 = in.readString();
        this.time1 = in.readString();
        this.time2 = in.readString();
        this.stime = in.readString();
        this.etime = in.readString();
    }


    public String getCmd1() {
        return cmd1;
    }

    public void setCmd1(String cmd1) {
        this.cmd1 = cmd1;
    }

    public String getCmd2() {
        return cmd2;
    }

    public void setCmd2(String cmd2) {
        this.cmd2 = cmd2;
    }

    public String getTime1() {
        return time1;
    }

    public void setTime1(String time1) {
        this.time1 = time1;
    }

    public String getTime2() {
        return time2;
    }

    public void setTime2(String time2) {
        this.time2 = time2;
    }

    public String getStime() {
        return stime;
    }

    public void setStime(String stime) {
        this.stime = stime;
    }

    public String getEtime() {
        return etime;
    }

    public void setEtime(String etime) {
        this.etime = etime;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cmd1);
        dest.writeString(this.cmd2);
        dest.writeString(this.time1);
        dest.writeString(this.time2);
        dest.writeString(this.stime);
        dest.writeString(this.etime);
    }
}
