package cn.com.broadlink.blappsdkdemo.activity.product;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.activity.h5.DeviceH5Activity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.data.ProductInfoResult;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;

public class ProductConfigDetailActivity extends TitleActivity {

    private ImageView mIvConfig;
    private TextView mTvConfig;
    private ImageView mIvReset;
    private TextView mTvReset;
    private TextView mTvFail;
    private TextView mTvBeforeConfig;
    private ProductInfoResult.ProductDninfo mProductinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_config_detail);
        setTitle("Config Detail");
        setBackWhiteVisible();

        mProductinfo = (ProductInfoResult.ProductDninfo) getIntent().getSerializableExtra(BLConstants.INTENT_VALUE);
        
        findView();

        initView();

        setListener();

    }

    private void setListener() {
        mTvFail.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(BLConstants.INTENT_URL, BLCommonUtils.dnaKitIconUrl(mProductinfo.getCfgfailedurl()));
                intent.setClass(mActivity, DeviceH5Activity.class);
                startActivity(intent);
            }
        });

        mTvBeforeConfig.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(BLConstants.INTENT_URL, BLCommonUtils.dnaKitIconUrl(mProductinfo.getBeforecfgpurl()));
                intent.setClass(mActivity, DeviceH5Activity.class);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        try {
            Glide.with(mActivity).load(BLCommonUtils.dnaKitIconUrl(mProductinfo.getConfigpic())).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mIvConfig);
            Glide.with(mActivity).load(BLCommonUtils.dnaKitIconUrl(mProductinfo.getResetpic())).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mIvReset);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mTvConfig.setText(mProductinfo.getConfigtext());
        mTvReset.setText(mProductinfo.getResettext());

        mTvFail.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
        mTvBeforeConfig.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
    }

    private void findView() {
        mIvConfig = (ImageView) findViewById(R.id.iv_config);
        mTvConfig = (TextView) findViewById(R.id.tv_config);
        mIvReset = (ImageView) findViewById(R.id.iv_reset);
        mTvReset = (TextView) findViewById(R.id.tv_reset);
        mTvFail = (TextView) findViewById(R.id.tv_fail);
        mTvBeforeConfig = (TextView) findViewById(R.id.tv_before_config);
    }
}
