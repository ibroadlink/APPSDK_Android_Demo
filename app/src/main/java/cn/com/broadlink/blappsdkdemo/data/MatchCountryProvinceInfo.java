package cn.com.broadlink.blappsdkdemo.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 国家城市信息
 * Created by YeJin on 2017/3/30.
 */

public class MatchCountryProvinceInfo implements Parcelable {
    
    private CountryInfo countryInfo;
    
    private ProvincesInfo provincesInfo;
    
    private CityInfo mCityInfo;

    public MatchCountryProvinceInfo() {}

    public CityInfo getCityInfo() {
        return mCityInfo;
    }

    public void setCityInfo(CityInfo mCityInfo) {
        this.mCityInfo = mCityInfo;
    }

    public CountryInfo getCountryInfo() {
        return countryInfo;
    }

    public void setCountryInfo(CountryInfo countryInfo) {
        this.countryInfo = countryInfo;
    }

    public ProvincesInfo getProvincesInfo() {
        return provincesInfo;
    }

    public void setProvincesInfo(ProvincesInfo provincesInfo) {
        this.provincesInfo = provincesInfo;
    }
  
    @Override
    public int describeContents() { return 0; }

    protected MatchCountryProvinceInfo(Parcel in) {
        this.countryInfo = in.readParcelable(CountryInfo.class.getClassLoader());
        this.provincesInfo = in.readParcelable(ProvincesInfo.class.getClassLoader());
        this.mCityInfo = in.readParcelable(CityInfo.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.countryInfo, flags);
        dest.writeParcelable(this.provincesInfo, flags);
        dest.writeParcelable(this.mCityInfo, flags);
    }

    public static final Creator<MatchCountryProvinceInfo> CREATOR = new Creator<MatchCountryProvinceInfo>() {
        @Override
        public MatchCountryProvinceInfo createFromParcel(Parcel source) {return new MatchCountryProvinceInfo(source);}

        @Override
        public MatchCountryProvinceInfo[] newArray(int size) {return new MatchCountryProvinceInfo[size];}
    };

    public MatchCountryProvinceInfo copy() {
        MatchCountryProvinceInfo copyInfo = new MatchCountryProvinceInfo();
        if (countryInfo != null) {
            CountryInfo copyCountryInfo = new CountryInfo();
            copyCountryInfo.setCode(countryInfo.getCode());
            copyCountryInfo.setCountry(countryInfo.getCountry());
            copyInfo.setCountryInfo(copyCountryInfo);
        }

        if (provincesInfo != null) {
            ProvincesInfo copyProvincesInfo = new ProvincesInfo();
            copyProvincesInfo.setCode(provincesInfo.getCode());
            copyProvincesInfo.setProvince(provincesInfo.getProvince());
            copyInfo.setProvincesInfo(copyProvincesInfo);
        }

        if (mCityInfo != null) {
            CityInfo copyProvincesInfo = new CityInfo();
            copyProvincesInfo.setCode(mCityInfo.getCode());
            copyProvincesInfo.setCity(mCityInfo.getCity());
            copyInfo.setCityInfo(copyProvincesInfo);
        }

        return copyInfo;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder(100);
        if(countryInfo != null){
            stringBuilder.append(countryInfo.getCountry());
        }
        if(provincesInfo != null){
            stringBuilder.append("-");
            stringBuilder.append(provincesInfo.getProvince());
        }
        if(mCityInfo != null){
            stringBuilder.append("-");
            stringBuilder.append(mCityInfo.getCity());
        }
        return stringBuilder.toString();
    }
}
