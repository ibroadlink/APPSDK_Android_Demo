package cn.com.broadlink.blappsdkdemo.activity.family;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.alibaba.fastjson.JSON;

import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.blappsdkdemo.view.scans.activity.CaptureBaseActivity;
import cn.com.broadlink.blsfamily.BLSFamily;
import cn.com.broadlink.blsfamily.bean.BLSBaseDataResult;
import cn.com.broadlink.blsfamily.bean.family.BLSFamilyInfo;
import cn.com.broadlink.blsfamily.bean.family.BLSFamilyJoinByQrData;

public class FamilyScanQrActivity extends CaptureBaseActivity {

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setTitle("Scan to add family");
        setBackWhiteVisible();
    }

    @Override
    public void onDealQR(String result) {
        new GetFamilyByQrCodeTask().execute(result);
    }

    class GetFamilyByQrCodeTask extends AsyncTask<String, Void, BLSBaseDataResult<BLSFamilyInfo>> {
        String qrCode;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Loading...");
        }

        @Override
        protected BLSBaseDataResult<BLSFamilyInfo> doInBackground(String... voids) {
            qrCode = voids[0];
            return  BLSFamily.Family.getInfoByQrCode(qrCode);
        }

        @Override
        protected void onPostExecute(BLSBaseDataResult<BLSFamilyInfo> blsFamilyQrResult) {
           dismissProgressDialog();
            if(blsFamilyQrResult != null && blsFamilyQrResult.succeed() && blsFamilyQrResult.getData()!=null){
                BLAlert.showDialog(FamilyScanQrActivity.this, "Do you want to join this family?", JSON.toJSONString(blsFamilyQrResult.getData(), true),
                        new BLAlert.DialogOnClickListener() {
                            @Override
                            public void onPositiveClick() {
                                new JoinFamilyTask().execute(qrCode);
                            }

                            @Override
                            public void onNegativeClick() {

                            }
                        });
            }else{
                BLCommonUtils.toastErr(blsFamilyQrResult);
            }
        }
    }



    class JoinFamilyTask extends AsyncTask<String, Void,  BLSBaseDataResult<BLSFamilyJoinByQrData>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Join...");
        }

        @Override
        protected  BLSBaseDataResult<BLSFamilyJoinByQrData> doInBackground(String... voids) {
            return  BLSFamily.Family.joinByQrCode(voids[0]);
        }

        @Override
        protected void onPostExecute( BLSBaseDataResult<BLSFamilyJoinByQrData> blsFamilyQrResult) {
            dismissProgressDialog();
            if(blsFamilyQrResult != null && blsFamilyQrResult.succeed() && blsFamilyQrResult.getData()!=null){
                BLAlert.showAlert(FamilyScanQrActivity.this, "Join success", JSON.toJSONString(blsFamilyQrResult.getData(), true),
                        new OnSingleClickListener() {
                            @Override
                            public void doOnClick(View v) {
                                back();
                            }
                        });
            }else{
                BLCommonUtils.toastErr(blsFamilyQrResult);
            }
        }
    }



}
