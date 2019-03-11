package cn.com.broadlink.blappsdkdemo.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by YeJin on 2017/3/30.
 */

public class CountryInfo implements Parcelable{

    private String country;

    private String code;

    private String pinyin;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.country);
        dest.writeString(this.code);
        dest.writeString(this.pinyin);
    }

    public CountryInfo() {
    }

    protected CountryInfo(Parcel in) {
        this.country = in.readString();
        this.code = in.readString();
        this.pinyin = in.readString();
    }

    public static final Creator<CountryInfo> CREATOR = new Creator<CountryInfo>() {
        @Override
        public CountryInfo createFromParcel(Parcel source) {
            return new CountryInfo(source);
        }

        @Override
        public CountryInfo[] newArray(int size) {
            return new CountryInfo[size];
        }
    };

}
