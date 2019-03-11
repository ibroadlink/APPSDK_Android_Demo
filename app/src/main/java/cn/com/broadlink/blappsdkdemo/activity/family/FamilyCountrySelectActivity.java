package cn.com.broadlink.blappsdkdemo.activity.family;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.data.MatchCountryProvinceInfo;


/**
 * 国家地区选择
 * Created by YeJin on 2017/3/29.
 */
public class FamilyCountrySelectActivity extends TitleActivity {

    private FamilyCountrySelectFragment mCountrySelectFragment;
    private MatchCountryProvinceInfo mMatchCountryProvinceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.str_locate_title));
        setBackWhiteVisible();
        
        mMatchCountryProvinceInfo = getIntent().getParcelableExtra(BLConstants.INTENT_VALUE);

        initView();
    }

    private void initView() {
        mCountrySelectFragment = new FamilyCountrySelectFragment();

        //传递参数
        if(mMatchCountryProvinceInfo != null){
            Bundle bundle = new Bundle();
            bundle.putParcelable(BLConstants.INTENT_VALUE, mMatchCountryProvinceInfo.copy());
            mCountrySelectFragment.setArguments(bundle);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.body, mCountrySelectFragment).commit();
    }


    @Override
    protected void back() {
        if(getSupportFragmentManager().getBackStackEntryCount() <= 0)
            super.back();
        else
            getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void saveCountryInfo(MatchCountryProvinceInfo selectInfo){
        if((selectInfo != null && selectInfo.getCountryInfo() != null)// 选择的信息不为空
                && ( mMatchCountryProvinceInfo == null  // 依次检查国省市是否一致
                        || mMatchCountryProvinceInfo.getCountryInfo() == null
                        || !mMatchCountryProvinceInfo.getCountryInfo().getCode().equalsIgnoreCase(selectInfo.getCountryInfo().getCode())
                        || mMatchCountryProvinceInfo.getProvincesInfo() != null && selectInfo.getProvincesInfo() == null
                        || mMatchCountryProvinceInfo.getProvincesInfo() == null && selectInfo.getProvincesInfo() != null
                        || (mMatchCountryProvinceInfo.getProvincesInfo() != null && selectInfo.getProvincesInfo() != null && !mMatchCountryProvinceInfo.getProvincesInfo().getCode()
                .equalsIgnoreCase(selectInfo.getProvincesInfo().getCode()))
                        ||mMatchCountryProvinceInfo.getCityInfo() == null && selectInfo.getCityInfo()!=null
                        ||mMatchCountryProvinceInfo.getCityInfo() != null && selectInfo.getCityInfo()==null
                        || (mMatchCountryProvinceInfo.getCityInfo() != null && selectInfo.getCityInfo()!=null && !mMatchCountryProvinceInfo.getCityInfo().getCode()
                .equalsIgnoreCase(selectInfo.getCityInfo().getCode()))
                )){

            final Intent intent = new Intent();
            intent.putExtra(BLConstants.INTENT_VALUE, selectInfo);
            setResult(RESULT_OK, intent);
            back();
        }else{
            super.back();
        }
    }

}
