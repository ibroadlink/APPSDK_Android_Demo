package cn.com.broadlink.blappsdkdemo.mvp.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.broadlink.blappsdkdemo.data.CityInfo;
import cn.com.broadlink.blappsdkdemo.data.CountryInfo;
import cn.com.broadlink.blappsdkdemo.data.MatchCountryProvinceInfo;
import cn.com.broadlink.blappsdkdemo.data.ProvincesInfo;
import cn.com.broadlink.blappsdkdemo.intferfacer.OnCountryDataDownLoadListener;


/**
 * 国家地区管理
 * Created by YeJin on 2017/3/30.
 */

public interface CountryDataModel {
    int SUCCESS = 0;

    int DOWANLOAD_FAIL = 1;

    HashMap<String, List<ProvincesInfo>> mCountryProvincesMap = new HashMap<>();

    List<CountryInfo> mCountryList = new ArrayList<>();

    //通过国家地区查询对应的id
    MatchCountryProvinceInfo matchCountryProvinceByName(String country, String provinces, String city);

    //通过国家地区查询对应的id
    MatchCountryProvinceInfo matchCountryProvinceByCode(String countryCode, String provincesCode, String city);

    //初始化成功之后才可以查询
    List<CountryInfo> queryCountrylist();

    List<ProvincesInfo> queryProvinceList(String countryId);
    
    List<CityInfo> queryCityList(String countryId, String provinceId);

    // 初始化国家地区数据
    void initData(OnCountryDataDownLoadListener countryDataLoadListener);

}
