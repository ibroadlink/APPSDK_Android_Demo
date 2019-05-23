package cn.com.broadlink.blappsdkdemo.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by YeJin on 2017/3/30.
 */

public class CityInfo implements Parcelable {
    
    private String city;
    
    private String code;

    public CityInfo() {}

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    protected CityInfo(Parcel in) {
        this.city = in.readString();
        this.code = in.readString();
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.city);
        dest.writeString(this.code);
    }

    public static final Parcelable.Creator<CityInfo> CREATOR = new Parcelable.Creator<CityInfo>() {
        @Override
        public CityInfo createFromParcel(Parcel source) {return new CityInfo(source);}

        @Override
        public CityInfo[] newArray(int size) {return new CityInfo[size];}
    };
}
