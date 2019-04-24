package cn.com.broadlink.blappsdkdemo.data.link;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * 属性触发事件条件
 * Created by YeJin on 2016/9/15.
 */
public class LinkageConditionTimeInfo implements Parcelable {

    private String weekdays = "1234567";

    private List<String> validperiod = new ArrayList<>();

    private int timezone;

    public String getWeekdays() {
        return weekdays;
    }

    public void setWeekdays(String weekdays) {
        this.weekdays = weekdays;
    }

    public List<String> getValidperiod() {
        return validperiod;
    }

    public void setValidperiod(List<String> validperiod) {
        this.validperiod = validperiod;
    }

    public int getTimezone() {
        return timezone;
    }

    public void setTimezone(int timezone) {
        this.timezone = timezone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.weekdays);
        dest.writeStringList(this.validperiod);
        dest.writeInt(this.timezone);
    }

    public LinkageConditionTimeInfo() {
    }

    protected LinkageConditionTimeInfo(Parcel in) {
        this.weekdays = in.readString();
        this.validperiod = in.createStringArrayList();
        this.timezone = in.readInt();
    }

    public static final Creator<LinkageConditionTimeInfo> CREATOR = new Creator<LinkageConditionTimeInfo>() {
        @Override
        public LinkageConditionTimeInfo createFromParcel(Parcel source) {
            return new LinkageConditionTimeInfo(source);
        }

        @Override
        public LinkageConditionTimeInfo[] newArray(int size) {
            return new LinkageConditionTimeInfo[size];
        }
    };
}
