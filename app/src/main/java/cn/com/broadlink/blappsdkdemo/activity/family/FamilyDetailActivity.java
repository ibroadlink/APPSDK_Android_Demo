package cn.com.broadlink.blappsdkdemo.activity.family;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.broadlink.lib.imageloader.core.assist.FailReason;
import com.broadlink.lib.imageloader.core.listener.SimpleImageLoadingListener;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.family.param.BLSUpdateFamilyInfoParams;
import cn.com.broadlink.blappsdkdemo.activity.family.result.BLSFamilyInfo;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLImageLoaderUtils;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.data.MatchCountryProvinceInfo;
import cn.com.broadlink.blappsdkdemo.intferfacer.FamilyInterface;
import cn.com.broadlink.blappsdkdemo.mvp.presenter.CountryContentProvider;
import cn.com.broadlink.blappsdkdemo.service.BLLocalFamilyManager;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;

public class FamilyDetailActivity extends TitleActivity implements FamilyInterface {

    private LinearLayout mQrLayout, mIconLyaout, mRoomListLayout, mMemberLayout, mDeviceLayout, mSceneLayout, mLinkageLayout;
    private TextView mFamilyNameView, mCountryView, mVersionView;
    private ImageView mFamilyIconView;
    private BLImageLoaderUtils mBlImageLoaderUtils;
    private BLSFamilyInfo blFamilyInfo;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_detail);
        setTitle(R.string.Family_Detail_View);
        setBackWhiteVisible();

        Intent intent = getIntent();
        if (intent != null) {
            String familyId = getIntent().getStringExtra(BLConstants.INTENT_FAMILY_ID);
            BLLocalFamilyManager.getInstance().setCurrentFamilyId(familyId);
        }

        findView();
        
        setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BLLocalFamilyManager.getInstance().setFamilyInterface(this);
        getFamilyAllInfoFromCloud();
    }

    @Override
    public void familyInfoChanged(Boolean isChanged, String familyId, String familyVersion) {
        dismissProgressDialog();
    }

    @Override
    public void familyAllInfo(BLSFamilyInfo allInfo) {
        dismissProgressDialog();

        if (allInfo != null) {
            //Family Info
            blFamilyInfo = allInfo;

            setTitle(blFamilyInfo.getName());
            mFamilyNameView.setText(blFamilyInfo.getName());
            initFamilyIconView();

            //Location Info
            refreshCountryView(blFamilyInfo);
        }
    }

    private void findView() {

        mQrLayout = (LinearLayout) findViewById(R.id.family_qr_layout);
        mFamilyNameView = (TextView) findViewById(R.id.family_name_view);

        mIconLyaout = (LinearLayout) findViewById(R.id.icon_layout);
        mFamilyIconView = (ImageView) findViewById(R.id.fiamily_icon_view);

        mRoomListLayout = (LinearLayout) findViewById(R.id.room_list_layout);
        mCountryView = (TextView) findViewById(R.id.country_view);
        mMemberLayout = (LinearLayout) findViewById(R.id.family_member_layout);

        mDeviceLayout = (LinearLayout) findViewById(R.id.device_list_layout);
        mSceneLayout = (LinearLayout) findViewById(R.id.ll_scene);
        mLinkageLayout = (LinearLayout) findViewById(R.id.ll_linkage);
        mVersionView = findViewById(R.id.tv_version);

        mBlImageLoaderUtils = BLImageLoaderUtils.getInstence(FamilyDetailActivity.this);
    }

    private void setListener(){


        mRoomListLayout.setOnClickListener(new OnSingleClickListener() {

            @Override
            public void doOnClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(BLConstants.INTENT_FAMILY_ID, BLLocalFamilyManager.getInstance().getCurrentFamilyId());
                intent.setClass(FamilyDetailActivity.this, FamilyRoomListActivity.class);
                startActivity(intent);
            }
        });

        mDeviceLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(BLConstants.INTENT_FAMILY_ID, BLLocalFamilyManager.getInstance().getCurrentFamilyId());
                intent.setClass(FamilyDetailActivity.this, FamilyModuleListActivity.class);
                startActivity(intent);
            }
        });

        mQrLayout.setOnClickListener(new OnSingleClickListener() {

            @Override
            public void doOnClick(View v) {
                BLAlert.showEditDilog(FamilyDetailActivity.this, "Input a new name", blFamilyInfo.getName(), new BLAlert.BLEditDialogOnClickListener() {
                    @Override
                    public void onClink(String value) {
                        final BLSUpdateFamilyInfoParams blsUpdateFamilyInfoParams = new BLSUpdateFamilyInfoParams();
                        blsUpdateFamilyInfoParams.setCountryCode(blFamilyInfo.getCountryCode());
                        blsUpdateFamilyInfoParams.setName(value);
                        
                        showProgressDialog(getResources().getString(R.string.loading));
                        BLLocalFamilyManager.getInstance().modifyFamily(null, blsUpdateFamilyInfoParams);
                    }

                    @Override
                    public void onClinkCacel(String value) {

                    }
                }, false);
            }
        });

        mIconLyaout.setOnClickListener(new OnSingleClickListener() {

            @Override
            public void doOnClick(View v) {
                // TODO: 2019/2/18 设置图标
            }
        });

        mMemberLayout.setOnClickListener(new OnSingleClickListener() {

            @Override
            public void doOnClick(View v) {
                final Intent intent = new Intent(FamilyDetailActivity.this, FamilyMemberListActivity.class);
                intent.putExtra(BLConstants.INTENT_FAMILY_ID, BLLocalFamilyManager.getInstance().getCurrentFamilyId());
                intent.putExtra(BLConstants.INTENT_NAME, blFamilyInfo.getMaster());
                startActivity(intent);
            }
        });
        
        mSceneLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                BLToastUtils.show("Coming soon");
            }
        });

        mLinkageLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                BLToastUtils.show("Coming soon");
            }
        });

    }

    private void initFamilyIconView(){
        mBlImageLoaderUtils.displayImage(blFamilyInfo.getIconpath(), mFamilyIconView, new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                if(loadedImage != null){
                    ((ImageView) view).setImageBitmap(loadedImage);
                }else{
                    ((ImageView) view).setImageResource(R.drawable.default_family_bg);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                super.onLoadingFailed(imageUri, view, failReason);
                ((ImageView) view).setImageResource(R.drawable.default_family_bg);
            }
        });
    }

    private void refreshCountryView(BLSFamilyInfo blFamilyInfo){
        if(blFamilyInfo != null){
            final MatchCountryProvinceInfo contryInfoByCode = CountryContentProvider.getInstance().findContryInfoByCode(blFamilyInfo.getCountryCode(), null, null);
            if(contryInfoByCode != null){
                mCountryView.setText(contryInfoByCode.toString());
            }else{
                mCountryView.setText(R.string.str_settings_safety_unsetting);
            }
            mVersionView.setText(blFamilyInfo.getVersion());
        }else{
            mCountryView.setText(R.string.str_settings_safety_unsetting);
        }
    }

    private void getFamilyAllInfoFromCloud() {
        showProgressDialog(getResources().getString(R.string.loading));
        BLLocalFamilyManager.getInstance().queryFamilyAllInfo(null);
    }

}
