package cn.com.broadlink.blappsdkdemo.activity.account;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.broadlink.lib.imageloader.core.listener.SimpleImageLoadingListener;

import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLBitmapUtils;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLImageLoaderUtils;
import cn.com.broadlink.blappsdkdemo.common.BLUserInfoUnits;
import cn.com.broadlink.blappsdkdemo.service.BLLocalFamilyManager;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;


public class AccountAndSecurityActivity extends TitleActivity {

    private ImageView mUserIconView;

    private TextView mNickNameView;
    private TextView mGenderView;
    private TextView mBirthdayView;
    private TextView mPhoneNumView;
    private TextView mEmailView;
    private TextView mTvModifyPwd;
    private Button mLoginOutButton;

    private BLImageLoaderUtils mBlImageLoaderUtils;
    private RelativeLayout mPhoneLayout, mEmailLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_and_security);
        setTitle(R.string.str_settings_account_and_security);
        setBackWhiteVisible();

        mBlImageLoaderUtils = BLImageLoaderUtils.getInstence(AccountAndSecurityActivity.this);

        findView();
        
        setListener();
        
        initView();
    }

    private void findView() {
        mUserIconView = (ImageView) findViewById(R.id.user_icon_view);
        mNickNameView = (TextView) findViewById(R.id.user_nick_view);
        mGenderView = (TextView) findViewById(R.id.user_gender_view);
        mBirthdayView = (TextView) findViewById(R.id.user_birthday_view);
        mPhoneLayout = (RelativeLayout) findViewById(R.id.phone_num_layout);
        mPhoneNumView = (TextView) findViewById(R.id.phone_num_tv);
        mEmailLayout = (RelativeLayout) findViewById(R.id.email_address_layout);
        mEmailView = (TextView) findViewById(R.id.email_address_tv);
        mTvModifyPwd = (TextView) findViewById(R.id.tv_right);
        mLoginOutButton = (Button) findViewById(R.id.btn_loginout);
    }

    private void setListener() {

        mLoginOutButton.setOnClickListener(new OnSingleClickListener() {

            @Override
            public void doOnClick(View v) {
                BLApplication.mBLUserInfoUnits.loginOut();
                BLLocalFamilyManager.getInstance().setCurrentFamilyInfo(null);
                
                Intent intent = new Intent();
                intent.setClass(AccountAndSecurityActivity.this, AccountMainActivity.class);
                startActivity(intent);
                AccountAndSecurityActivity.this.finish();
            }

        });

        mTvModifyPwd.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                BLCommonUtils.toActivity(AccountAndSecurityActivity.this, AccountPasswordChangeActivity.class);
            }
        });
        
        mPhoneLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                if(!TextUtils.isEmpty( BLApplication.mBLUserInfoUnits.getPhone())){
                    BLCommonUtils.toActivity(mActivity, AccountModifyPhoneOrEmailActivity.class, BLApplication.mBLUserInfoUnits.getPhone());
                }
            }
        });
        
        mEmailLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                if(!TextUtils.isEmpty( BLApplication.mBLUserInfoUnits.getEmail())){
                    BLCommonUtils.toActivity(mActivity, AccountModifyPhoneOrEmailActivity.class, BLApplication.mBLUserInfoUnits.getEmail());
                }
            }
        });

    }

    private void initView() {
        BLUserInfoUnits blUserInfoUnits = BLApplication.mBLUserInfoUnits;

        String iconPath = blUserInfoUnits.getIconpath();
        if (!TextUtils.isEmpty(iconPath)) {
            mBlImageLoaderUtils.displayImage(iconPath, mUserIconView, new SimpleImageLoadingListener() {

                @Override
                public void onLoadingComplete(String arg0, View view, Bitmap bitmap) {
                    ((ImageView) view).setImageBitmap(BLBitmapUtils.toImageCircle(bitmap));
                }

            });
        }

        try {
            mNickNameView.setText(blUserInfoUnits.getNickname().equals("") ? getString(R.string.str_settings_safety_unsetting) : blUserInfoUnits.getNickname());

            mGenderView.setText(blUserInfoUnits.getSex().equals("") ? getString(R.string.str_settings_safety_unsetting) : (blUserInfoUnits.getSex().equals("male") ?
                    getString(R.string.sex_male) : getString(R.string.sex_female)));

            mBirthdayView.setText(blUserInfoUnits.getBirthday().equals("") ? getString(R.string.str_settings_safety_unsetting) : blUserInfoUnits.getBirthday().substring(0, 10));
        } catch (Exception e) {
            mNickNameView.setText("");
            mGenderView.setText("");
            mBirthdayView.setText("");
        }


        if (!TextUtils.isEmpty(blUserInfoUnits.getPhone())) {
            mPhoneNumView.setText(blUserInfoUnits.getPhone());
        }

        if (!TextUtils.isEmpty(blUserInfoUnits.getEmail())) {
            mEmailView.setText(blUserInfoUnits.getEmail());
        }
    }

}