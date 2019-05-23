package cn.com.broadlink.blappsdkdemo.mvp.presenter;

import android.os.AsyncTask;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import cn.com.broadlink.blappsdkdemo.common.BLAppUtils;
import cn.com.broadlink.blappsdkdemo.common.BLFileUtils;
import cn.com.broadlink.blappsdkdemo.common.BLHanzi2PingyinUtil;
import cn.com.broadlink.blappsdkdemo.data.CityInfo;
import cn.com.broadlink.blappsdkdemo.data.CountryInfo;
import cn.com.broadlink.blappsdkdemo.data.MatchCountryProvinceInfo;
import cn.com.broadlink.blappsdkdemo.data.ProvincesInfo;


/**
 * Created by YeJin on 2017/3/30.
 */
public class CountryContentProvider {
    private static final int COUNTRY_FILE_VERSION = 1;

    private static CountryContentProvider mCountryContentProvider;

    private DownLoadCountryFileTask mDownLoadCountryFileTask;

    private List<CountryInfo> mCountryList = new ArrayList<>();

    private HashMap<String, List<ProvincesInfo>> mCountryProvincesMap = new HashMap<>();

    private CountryContentProvider(){}

    public static CountryContentProvider getInstance(){
        if(mCountryContentProvider == null){
            mCountryContentProvider = new CountryContentProvider();
        }
        return mCountryContentProvider;
    }

    public void initData(OnCountryDataLoadLister onCountryDataLoadLister) {
        if(mCountryList != null && !mCountryList.isEmpty()){
            if(onCountryDataLoadLister != null){
                //onCountryDataLoadLister.onLoadSuccess(initData());
                onCountryDataLoadLister.onLoadSuccess(mCountryList);
            }
        }else if(mDownLoadCountryFileTask == null){
            mDownLoadCountryFileTask = new DownLoadCountryFileTask(onCountryDataLoadLister);
            mDownLoadCountryFileTask.execute();
        }
    }


    public List<ProvincesInfo> queryProvinceList(String countryId) {
        return mCountryProvincesMap.get(countryId);
    }

    public List<CityInfo> queryCityList(String countryId, String provinceId) {
        final List<ProvincesInfo> provincesInfos = mCountryProvincesMap.get(countryId);
        for (ProvincesInfo item : provincesInfos){
            if(item.getCode().equals(provinceId)){
                return item.getCitys();
            }
        }
        return null;
    }

    public boolean containProvinceList(String countryId) {
        return mCountryProvincesMap.containsKey(countryId);
    }

    public boolean containCityList(String countryId, String provinceId) {
        final List<ProvincesInfo> list = mCountryProvincesMap.get(countryId);
        for (ProvincesInfo item : list) {
            if(item.getCode().equals(provinceId)){
                return (item.getCitys() !=null && !item.getCitys().isEmpty());
            }
        }
        return false;
    }

    private class DownLoadCountryFileTask extends AsyncTask<Void, Void, String>{
        OnCountryDataLoadLister onCountryDataLoadLister;

        public DownLoadCountryFileTask(OnCountryDataLoadLister onCountryDataLoadLister){
            this.onCountryDataLoadLister = onCountryDataLoadLister;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(onCountryDataLoadLister != null){
                onCountryDataLoadLister.onStartLoadData();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
//            String countryJsonResult = BLFileUtils.readAssetsFile(BLAppUtils.getApp(), BLPhoneUtils.getLanguage().toLowerCase() + "_url");
//            if(TextUtils.isEmpty(countryJsonResult)){
            String  countryJsonResult = BLFileUtils.readAssetsFile(BLAppUtils.getApp(), "en-us_url");
//            }
            try {
                JSONObject jsonObject =  new JSONObject(countryJsonResult);
                int status = jsonObject.optInt("status",-1);
                int version = jsonObject.optInt("version",-1);
                JSONArray data = jsonObject.optJSONArray("data");
                if(status == 0 && data != null && data.length() > 0 && version == COUNTRY_FILE_VERSION){
                    return countryJsonResult;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return countryJsonResult;
        }

        @Override
        protected void onPostExecute(String countryJsonResult) {
            super.onPostExecute(countryJsonResult);

            if(countryJsonResult != null){
                resetCountryData(countryJsonResult);
            }

            Collections.sort(mCountryList, new PinyinComparator());
            if(onCountryDataLoadLister != null){
                //onCountryDataLoadLister.onLoadSuccess(initData());
                onCountryDataLoadLister.onLoadSuccess(mCountryList);
            }
            mDownLoadCountryFileTask = null;
        }
    }

    private HashMap<String, ArrayList<CountryInfo>>  initData(){
        HashMap<String, ArrayList<CountryInfo>> reult = new LinkedHashMap<>();

        String crtLetter = "A";
        ArrayList<CountryInfo> arrayList = new ArrayList<>();
        ArrayList<CountryInfo> emPList = new ArrayList<>();
        for (CountryInfo countryInfo : mCountryList) {
            if(countryInfo.getPinyin() != null){
                if(countryInfo.getPinyin().toUpperCase().startsWith(crtLetter)){
                    arrayList.add(countryInfo);
                }else{
                    reult.put(crtLetter, arrayList);
                    arrayList = new ArrayList<>();
                    crtLetter = countryInfo.getPinyin().toUpperCase().substring(0, 1).toUpperCase();
                }
            }else{
                arrayList.add(countryInfo);
            }
        }

        if(!emPList.isEmpty()){
            reult.put("*", emPList);
        }

        return reult;
    }

    private synchronized void resetCountryData(String countryJsonResult){
        if(countryJsonResult != null){
            try {
                JSONObject resultJsonOb = new JSONObject(countryJsonResult);
                int status = resultJsonOb.optInt("status");
                if(status != 0){
                    return;
                }

                mCountryProvincesMap.clear();
                mCountryList.clear();
                JSONArray jsonArray = resultJsonOb.optJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject countryObject = jsonArray.optJSONObject(i);
                    CountryInfo countInfo = new CountryInfo();
                    countInfo.setCountry(countryObject.optString("country"));
                    countInfo.setCode(countryObject.optString("code"));
                    countInfo.setPinyin(BLHanzi2PingyinUtil.getInstance().pinyin(countInfo.getCountry()));
                    mCountryList.add(countInfo);

                    JSONArray childrenJsonArray = countryObject.optJSONArray("children");
                    if(childrenJsonArray == null) continue;

                    List<ProvincesInfo> proList = new ArrayList<>();
                    for (int i1 = 0; i1 < childrenJsonArray.length(); i1++) {
                        JSONObject childrenJsonObject = childrenJsonArray.optJSONObject(i1);
                        ProvincesInfo provincesInfo = new ProvincesInfo();
                        provincesInfo.setCode(childrenJsonObject.optString("code"));
                        provincesInfo.setProvince(childrenJsonObject.optString("province"));

                        JSONArray cityJsonArray = childrenJsonObject.optJSONArray("subchildren");
                        if(cityJsonArray == null) {
                            proList.add(provincesInfo);
                            mCountryProvincesMap.put(countInfo.getCode(), proList);
                            continue;
                        }

                        provincesInfo.getCitys().clear();
                        for (int k=0; k<cityJsonArray.length(); k++){
                            final CityInfo cityInfo = new CityInfo();
                            final JSONObject o = cityJsonArray.optJSONObject(k);
                            cityInfo.setCity(o.optString("city"));
                            cityInfo.setCode(o.optString("code"));
                            provincesInfo.getCitys().add(cityInfo);
                        }
                        proList.add(provincesInfo);
                        mCountryProvincesMap.put(countInfo.getCode(), proList);
                    }
                }
            }catch (JSONException e){}
        }
    }

    public interface OnCountryDataLoadLister{
        void onStartLoadData();

       // void onLoadSuccess(HashMap<String, ArrayList<CountryInfo>> reult);
        void onLoadSuccess(List<CountryInfo> reult);
    }

    public class PinyinComparator implements Comparator<CountryInfo> {
        public int compare(CountryInfo o1, CountryInfo o2) {
            if(o1.getPinyin() != null){
                return o1.getPinyin().compareTo(o2.getPinyin());
            }
            return 0;
        }
    }

    public MatchCountryProvinceInfo findContryInfoByCode(String countryCode, String provincesCode, String city){
        if(mCountryList == null || mCountryList.isEmpty()) return null;
        for (int i = 0; i < mCountryList.size(); i++) {
            CountryInfo countryInfo = mCountryList.get(i);
            if(countryInfo.getCode().equals(countryCode)){
                List<ProvincesInfo> provinceList = mCountryProvincesMap.get(countryInfo.getCode());
                if(TextUtils.isEmpty(provincesCode) || provinceList == null){
                    MatchCountryProvinceInfo countryProviceInfo = new MatchCountryProvinceInfo();
                    countryProviceInfo.setCountryInfo(countryInfo);
                    return countryProviceInfo;
                }else{
                    for (int i1 = 0; i1 < provinceList.size(); i1++) {
                        ProvincesInfo provice = provinceList.get(i1);
                        if (provice.getCode().equals(provincesCode)) {
                            MatchCountryProvinceInfo countryProviceInfo = new MatchCountryProvinceInfo();
                            countryProviceInfo.setCountryInfo(countryInfo);
                            countryProviceInfo.setProvincesInfo(provice);

                            for (CityInfo item : provice.getCitys()){
                                if(item.getCode().equals(city)){
                                    countryProviceInfo.setCityInfo(item);
                                }
                            }

                            return countryProviceInfo;
                        }
                    }
                }
            }
        }
        return null;
    }

    public MatchCountryProvinceInfo findContryInfo(String country, String provinces, String city){
        if(mCountryList == null || mCountryList.isEmpty()) return null;
        for (int i = 0; i < mCountryList.size(); i++) {
            CountryInfo countryInfo = mCountryList.get(i);
            if(countryInfo.getCountry().equals(country)){
                List<ProvincesInfo> provinceList = mCountryProvincesMap.get(countryInfo.getCode());
                if(provinces == null || provinceList == null){
                    MatchCountryProvinceInfo countryProviceInfo = new MatchCountryProvinceInfo();
                    countryProviceInfo.setCountryInfo(countryInfo);
                    return countryProviceInfo;
                }else{
                    for (int i1 = 0; i1 < provinceList.size(); i1++) {
                        ProvincesInfo provice = provinceList.get(i1);
                        if (provice.getProvince().equals(provinces)) {

                            MatchCountryProvinceInfo countryProviceInfo = new MatchCountryProvinceInfo();
                            countryProviceInfo.setCountryInfo(countryInfo);
                            countryProviceInfo.setProvincesInfo(provice);

                            for (CityInfo item : provice.getCitys()){
                                if(item.getCity().equals(city)){
                                    countryProviceInfo.setCityInfo(item);
                                }
                            }

                            return countryProviceInfo;
                        }
                    }
                }
            }
        }
        return null;
    }

    public ArrayList<CountryInfo> searchCountry(String searchKey){
        ArrayList<CountryInfo> dataList = new ArrayList<>();
        if(!TextUtils.isEmpty(searchKey)){
            for (CountryInfo data : mCountryList) {
                if(((data.getPinyin() != null && data.getPinyin().toUpperCase().contains(searchKey.toUpperCase()))
                        ||  data.getCountry() != null && data.getCountry().toUpperCase().contains(searchKey.toUpperCase()))){
                    dataList.add(data);
                }
            }
        }

        return dataList;
    }
}
