package cn.com.broadlink.blappsdkdemo.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Desc
 *
 * @author JiangYaqiang
 * 2019/4/2 11:01
 */
public class BLStressTestCmdBean implements Parcelable {

    public String cmd;
    public String data;
    public String did;
    public String sDid;
    public int interval;
    public int delay;
    public int sendCount;

    public BLStressTestCmdBean() {}

    public BLStressTestCmdBean(String did, String sDid, String cmd, String data, int interval, int delay, int sendCount) {
        this.cmd = cmd;
        this.data = data;
        this.did = did;
        this.sDid = sDid;
        this.interval = interval;
        this.delay = delay;
        this.sendCount = sendCount;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cmd);
        dest.writeString(this.data);
        dest.writeString(this.did);
        dest.writeString(this.sDid);
        dest.writeInt(this.interval);
        dest.writeInt(this.delay);
        dest.writeInt(this.sendCount);
    }

    protected BLStressTestCmdBean(Parcel in) {
        this.cmd = in.readString();
        this.data = in.readString();
        this.did = in.readString();
        this.sDid = in.readString();
        this.interval = in.readInt();
        this.delay = in.readInt();
        this.sendCount = in.readInt();
    }

    public static final Creator<BLStressTestCmdBean> CREATOR = new Creator<BLStressTestCmdBean>() {
        @Override
        public BLStressTestCmdBean createFromParcel(Parcel source) {return new BLStressTestCmdBean(source);}

        @Override
        public BLStressTestCmdBean[] newArray(int size) {return new BLStressTestCmdBean[size];}
    };
}
