package cn.com.broadlink.blappsdkdemo.activity.Family;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.broadlink.lib.imageloader.core.assist.FailReason;
import com.broadlink.lib.imageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.Family.Result.BLSFamilyInfo;
import cn.com.broadlink.blappsdkdemo.activity.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLImageLoaderUtils;
import cn.com.broadlink.blappsdkdemo.intferfacer.FamilyInterface;
import cn.com.broadlink.blappsdkdemo.service.BLLocalFamilyManager;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;

public class FamilyDetailActivity extends TitleActivity implements FamilyInterface {

    private BLSFamilyInfo blFamilyInfo;

    private LinearLayout mQrLayout, mIconLyaout, mRoomListLayout, mMemberLayout, mDeviceLayout;

    private ImageView mFamilyIconView;
    private BLImageLoaderUtils mBlImageLoaderUtils;

    private TextView mFamilyNameView, mRoomCountView, mCountryView, mMemberView, mDeviceCountView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_detail);
        setTitle(R.string.Family_Detail_View);
        setBackWhiteVisible();

        Intent intent = getIntent();
        if (intent != null) {
            String familyId = getIntent().getStringExtra("INTENT_FAMILY_ID");
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
        mRoomCountView = (TextView) findViewById(R.id.room_count_view);

        mCountryView = (TextView) findViewById(R.id.country_view);

        mMemberLayout = (LinearLayout) findViewById(R.id.family_member_layout);
        mMemberView = (TextView) findViewById(R.id.family_member_view);

        mDeviceLayout = (LinearLayout) findViewById(R.id.device_list_layout);
        mDeviceCountView = (TextView) findViewById(R.id.device_count_view);

        mBlImageLoaderUtils = BLImageLoaderUtils.getInstence(FamilyDetailActivity.this);
    }

    private void setListener(){

        mQrLayout.setOnClickListener(new OnSingleClickListener() {

            @Override
            public void doOnClick(View v) {

            }
        });

        mIconLyaout.setOnClickListener(new OnSingleClickListener() {

            @Override
            public void doOnClick(View v) {

            }
        });

        mMemberView.setOnClickListener(new OnSingleClickListener() {

            @Override
            public void doOnClick(View v) {

            }
        });

        mRoomListLayout.setOnClickListener(new OnSingleClickListener() {

            @Override
            public void doOnClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("INTENT_FAMILY_ID", BLLocalFamilyManager.getInstance().getCurrentFamilyId());
                intent.setClass(FamilyDetailActivity.this, FamilyRoomListActivity.class);
                startActivity(intent);
            }
        });

        mDeviceLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("INTENT_FAMILY_ID", BLLocalFamilyManager.getInstance().getCurrentFamilyId());
                intent.setClass(FamilyDetailActivity.this, FamilyModuleListActivity.class);
                startActivity(intent);
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
            mCountryView.setText(blFamilyInfo.getCountryCode());
            if(blFamilyInfo.getProvinceCode() != null){
                mCountryView.append(" ");
                mCountryView.append(blFamilyInfo.getProvinceCode());
            }
            if(blFamilyInfo.getCityCode() != null){
                mCountryView.append(" ");
                mCountryView.append(blFamilyInfo.getCityCode());
            }
        }else{
            mCountryView.setText(R.string.str_settings_safety_unsetting);
        }
    }

    private void getFamilyAllInfoFromCloud() {
        showProgressDialog(getResources().getString(R.string.loading));
        BLLocalFamilyManager.getInstance().queryFamilyAllInfo(null);
    }

}
