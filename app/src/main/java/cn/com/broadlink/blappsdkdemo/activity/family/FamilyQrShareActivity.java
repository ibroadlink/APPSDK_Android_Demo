package cn.com.broadlink.blappsdkdemo.activity.family;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.zxing.WriterException;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.family.result.BLSFamilyQrResult;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.activity.family.manager.BLSFamilyManager;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLQrCodeUtils;
import cn.com.broadlink.blappsdkdemo.service.BLLocalFamilyManager;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;

public class FamilyQrShareActivity extends TitleActivity {

    private ImageView mIvQrcode;
    private TextView mTvCreateQrcodeFailed;
    private ProgressBar mPbCreatingQrcode;
    private TextView mTvRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_qr_share);
        setBackWhiteVisible();
        setTitle("Share Family");

        initView();
        
        mTvRefresh.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                new GetQrCodeTask().execute();
            }
        });
    }

    private void initView() {
        mIvQrcode = (ImageView) findViewById(R.id.iv_qrcode);
        mTvCreateQrcodeFailed = (TextView) findViewById(R.id.tv_create_qrcode_failed);
        mPbCreatingQrcode = (ProgressBar) findViewById(R.id.pb_creating_qrcode);
        mTvRefresh = (TextView) findViewById(R.id.tv_refresh);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetQrCodeTask().execute();
    }

    class GetQrCodeTask extends AsyncTask<Void, Void, BLSFamilyQrResult>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mPbCreatingQrcode.setVisibility(View.VISIBLE);
            mTvCreateQrcodeFailed.setVisibility(View.GONE);
        }

        @Override
        protected BLSFamilyQrResult doInBackground(Void... voids) {
            return  BLSFamilyManager.getInstance().getFamilyQrCode(BLLocalFamilyManager.getInstance().getCurrentFamilyId());
        }

        @Override
        protected void onPostExecute(BLSFamilyQrResult blsFamilyQrResult) {
            mPbCreatingQrcode.setVisibility(View.GONE);
            if(blsFamilyQrResult != null && blsFamilyQrResult.succeed() && blsFamilyQrResult.getData()!=null){
                try {
                    mIvQrcode.setImageBitmap(BLQrCodeUtils.qrCodeBitmap("family://" + blsFamilyQrResult.getData().getQrcode()));
                } catch (WriterException e) {
                    e.printStackTrace();
                    mTvCreateQrcodeFailed.setVisibility(View.VISIBLE);
                }
            }else{
                mTvCreateQrcodeFailed.setVisibility(View.VISIBLE);
                BLCommonUtils.toastErr(blsFamilyQrResult);
            }
        }
    }
}
