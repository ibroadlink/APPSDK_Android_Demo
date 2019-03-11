package cn.com.broadlink.blappsdkdemo.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YeJin on 2017/3/30.
 */

public class ProvincesInfo implements Parcelable {
    
    private String province;
    
    private String code;
    
    private List<CityInfo> citys = new ArrayList<>();

    public ProvincesInfo() {}

    public List<CityInfo> getCitys() {
        return citys;
    }

    public void setCitys(List<CityInfo> citys) {
        this.citys = citys;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    @Override
    public int describeContents() { return 0; }

    protected ProvincesInfo(Parcel in) {
        this.province = in.readString();
        this.code = in.readString();
        this.citys = in.createTypedArrayList(CityInfo.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.province);
        dest.writeString(this.code);
        dest.writeTypedList(this.citys);
    }

    public static final Creator<ProvincesInfo> CREATOR = new Creator<ProvincesInfo>() {
        @Override
        public ProvincesInfo createFromParcel(Parcel source) {return new ProvincesInfo(source);}

        @Override
        public ProvincesInfo[] newArray(int size) {return new ProvincesInfo[size];}
    };
}
