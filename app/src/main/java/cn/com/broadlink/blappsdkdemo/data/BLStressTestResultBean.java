package cn.com.broadlink.blappsdkdemo.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Desc
 *
 * @author JiangYaqiang
 * 2019/4/2 11:01
 */
public class BLStressTestResultBean implements Parcelable {

    public int id;
    public int totalCount = 0;
    public int failCount = 0;

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.totalCount);
        dest.writeInt(this.failCount);
    }

    public BLStressTestResultBean() {}

    public BLStressTestResultBean(int id) {
        this.id = id;
    }

    protected BLStressTestResultBean(Parcel in) {
        this.id = in.readInt();
        this.totalCount = in.readInt();
        this.failCount = in.readInt();
    }

    public static final Creator<BLStressTestResultBean> CREATOR = new Creator<BLStressTestResultBean>() {
        @Override
        public BLStressTestResultBean createFromParcel(Parcel source) {return new BLStressTestResultBean(source);}

        @Override
        public BLStressTestResultBean[] newArray(int size) {return new BLStressTestResultBean[size];}
    };
}
