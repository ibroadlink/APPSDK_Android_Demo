package cn.com.broadlink.blappsdkdemo.activity.family;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.data.MatchCountryProvinceInfo;
import cn.com.broadlink.blappsdkdemo.intferfacer.SimpleCallback;
import cn.com.broadlink.blappsdkdemo.service.BLLocalFamilyManager;
import cn.com.broadlink.blappsdkdemo.view.InputTextView;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;

public class FamilyAddActivity extends TitleActivity implements SimpleCallback {

    private InputTextView mFamilyNameView;
    private Button mAddBtn;
    private TextView mtvLocate;
    private MatchCountryProvinceInfo mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_add);
        setTitle(R.string.str_create_family);
        setBackWhiteVisible();

        findView();
        
        setListener();

        BLLocalFamilyManager.getInstance().setFamilyAddInterface(this);
    }

    private void findView() {
        mFamilyNameView = (InputTextView) findViewById(R.id.family_name_view);
        mAddBtn = (Button) findViewById(R.id.btn_add_family);
        mtvLocate = findViewById(R.id.tv_country);
    }

    private void setListener() {
        mFamilyNameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mAddBtn.setEnabled(s.length() > 0);
            }
        });

        mAddBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                showProgressDialog(getResources().getString(R.string.loading));
                String name = mFamilyNameView.getTextString();
                String countryCode = "1"; // China
                String provinceCode = null;
                        
                if(mLocation != null){
                    if( mLocation.getCountryInfo() != null){
                        countryCode = mLocation.getCountryInfo().getCode();
                    }
                    if(mLocation.getProvincesInfo() != null){
                        provinceCode = mLocation.getProvincesInfo().getCode();
                    }
                }
                BLLocalFamilyManager.getInstance().addFamily(name, countryCode, provinceCode);
            }
        });

        mtvLocate.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                BLCommonUtils.toActivityForResult(FamilyAddActivity.this, FamilyCountrySelectActivity.class, 101);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            mLocation = ((MatchCountryProvinceInfo)data.getParcelableExtra(BLConstants.INTENT_VALUE)).copy();
            mtvLocate.setText(mLocation.toString());
        }
    }

    @Override
    public void onResult(boolean isSuccess) {
        dismissProgressDialog();
        if(isSuccess){
            FamilyAddActivity.this.finish();
        }
    }
}
