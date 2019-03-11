package cn.com.broadlink.blappsdkdemo.activity.family;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.BaseFragment;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.data.CountryInfo;
import cn.com.broadlink.blappsdkdemo.data.MatchCountryProvinceInfo;
import cn.com.broadlink.blappsdkdemo.mvp.presenter.CountryContentProvider;
import cn.com.broadlink.blappsdkdemo.view.OnSingleItemClickListener;

/**
 * 国家选择
 * Created by YeJin on 2017/3/29.
 */

public class FamilyCountrySelectFragment extends BaseFragment{
    private LinearLayout mGPSLocationLayout;
    private TextView mLocationView;
    private ListView mCountryListView;
    private ProgressBar mLoadingProgressBar;
    private ImageView mLocationPointView;

    private CountryAdapter mCountryAdapter;
    private List<CountryInfo> mCountryList = new ArrayList<>();
    private CountryContentProvider mCountryContentProvider;
    private MatchCountryProvinceInfo mMatchCountryProvinceInfo;
    private MatchCountryProvinceInfo mLocationMatchCountryProvinceInfo;
    private String mLocationCountry, mLocationProvince, mLocationCity;
    private boolean mInitCountrySuccess = false;
    private FamilyCountrySelectActivity mCountrySelectActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCountrySelectActivity = (FamilyCountrySelectActivity) getActivity();
        mCountryContentProvider = CountryContentProvider.getInstance();

        Bundle bundle = getArguments();
        if(bundle != null){
            mMatchCountryProvinceInfo = bundle.getParcelable(BLConstants.INTENT_VALUE);
        }
    }

    @Override
    public View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_country_select, container, false);

        findView(view);

        setListener();

        initView();

        return view;
    }

    private void findView(View view){
        mLoadingProgressBar = (ProgressBar) view.findViewById(R.id.loading_view);
        mLocationPointView = (ImageView) view.findViewById(R.id.point_view);
        mGPSLocationLayout = (LinearLayout) view.findViewById(R.id.gps_location_layout);
        mLocationView = (TextView) view.findViewById(R.id.address_view);
        mCountryListView = (ListView) view.findViewById(R.id.country_listview);
    }

    private void setListener(){
        mCountryListView.setOnItemClickListener(new OnSingleItemClickListener() {

            @Override
            public void doOnClick(AdapterView<?> parent, View view, int position, long id) {
                CountryInfo countryInfo = mCountryList.get(position);
                mMatchCountryProvinceInfo = new MatchCountryProvinceInfo();
                mMatchCountryProvinceInfo.setCountryInfo(countryInfo);

                mClickedIndex = position;
                mCountryAdapter.notifyDataSetChanged();
                ((FamilyCountrySelectActivity) getActivity()).saveCountryInfo(mMatchCountryProvinceInfo);
            }
        });
    }

    private void initView(){
        mCountryAdapter = new CountryAdapter(getActivity(), mCountryList);
        mCountryListView.setAdapter(mCountryAdapter);

        mCountryContentProvider.initData(new CountryContentProvider.OnCountryDataLoadLister() {
            @Override
            public void onStartLoadData() {

            }

            @Override
            public void onLoadSuccess(List<CountryInfo> reult) {
                mInitCountrySuccess = true;

                mCountryList.clear();
                mCountryList.addAll(reult);

                for (int i=0; i<mCountryList.size(); i++) {
                    if(mMatchCountryProvinceInfo != null && mMatchCountryProvinceInfo.getCountryInfo().getCode().equals(mCountryList.get(i).getCode())){
                        mClickedIndex = mSelectedIndex = i;
                    }
                }
                mCountryAdapter.notifyDataSetChanged();

                matchCountryProvinceInfo();
            }
        });
    }


    private void matchCountryProvinceInfo(){
        if(mLocationCountry != null){
            mLocationMatchCountryProvinceInfo = mCountryContentProvider.findContryInfo(mLocationCountry, mLocationProvince, mLocationCity);

            if(mLocationMatchCountryProvinceInfo != null){
                refreshCountryView();
            }
        }
    }


    private void refreshCountryView(){
        if(mLocationMatchCountryProvinceInfo != null){
            mLocationView.setText(mLocationMatchCountryProvinceInfo.getCountryInfo().getCountry());
            if(mLocationMatchCountryProvinceInfo.getProvincesInfo() != null && !TextUtils.isEmpty(mLocationMatchCountryProvinceInfo.getProvincesInfo().getProvince())){
                mLocationView.append(" ");
                mLocationView.append(mLocationMatchCountryProvinceInfo.getProvincesInfo().getProvince());
            }
            if(mLocationMatchCountryProvinceInfo.getCityInfo() != null && !TextUtils.isEmpty(mLocationMatchCountryProvinceInfo.getCityInfo().getCity())){
                mLocationView.append(" ");
                mLocationView.append(mLocationMatchCountryProvinceInfo.getCityInfo().getCity());
            }
        }
    }


    private int mSelectedIndex = -1; // 已选择地区
    private int mClickedIndex = -1;  // 点击哪个item，打个勾
    private class CountryAdapter extends ArrayAdapter<CountryInfo> {

        public CountryAdapter(Context context, List<CountryInfo> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new ViewHolder();
                convertView = getActivity().getLayoutInflater().inflate(R.layout.item_account_country_list, null);
                viewHolder.addressView = (TextView) convertView.findViewById(R.id.address_view);
                viewHolder.arrowView = (TextView) convertView.findViewById(R.id.arrow_view);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.addressView.setText(getItem(position).getCountry());

            boolean arrow = mCountryContentProvider.containProvinceList(getItem(position).getCode());
//            viewHolder.arrowView.setCompoundDrawablesWithIntrinsicBounds(0, 0, arrow ? R.drawable.arrow : (mClickedIndex==position && mClickedIndex!=mSelectedIndex ? R.drawable
//                    .icon_list_checkmark : 0) ,
//                    0);

            if(mSelectedIndex == position){
                viewHolder.arrowView.setText(R.string.str_selected_location);
            }else{
                viewHolder.arrowView.setText(null);
            }
            
            if(position==getCount()-1){ // 清除标志
                mClickedIndex = -1;
            }
            
            return convertView;
        }

        private class ViewHolder{
            TextView addressView;
            TextView arrowView;
        }
    }


}
