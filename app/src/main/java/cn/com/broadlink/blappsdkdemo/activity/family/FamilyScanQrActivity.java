package cn.com.broadlink.blappsdkdemo.activity.family;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.alibaba.fastjson.JSON;

import cn.com.broadlink.blappsdkdemo.activity.family.result.BLSFamilyCreateByQrResult;
import cn.com.broadlink.blappsdkdemo.activity.family.result.BLSFamilyInfoQrResult;
import cn.com.broadlink.blappsdkdemo.activity.family.result.DataQrCode;
import cn.com.broadlink.blappsdkdemo.activity.family.manager.BLSFamilyManager;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.blappsdkdemo.view.scans.activity.CaptureBaseActivity;

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

    class GetFamilyByQrCodeTask extends AsyncTask<String, Void, BLSFamilyInfoQrResult> {
        String qrCode;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Loading...");
        }

        @Override
        protected BLSFamilyInfoQrResult doInBackground(String... voids) {
            qrCode = voids[0];
            final DataQrCode dataQrCode = new DataQrCode();
            dataQrCode.setQrcode(qrCode);
            return  BLSFamilyManager.getInstance().getFamilyInfoByQrCode(dataQrCode);
        }

        @Override
        protected void onPostExecute(BLSFamilyInfoQrResult blsFamilyQrResult) {
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



    class JoinFamilyTask extends AsyncTask<String, Void, BLSFamilyCreateByQrResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Join...");
        }

        @Override
        protected BLSFamilyCreateByQrResult doInBackground(String... voids) {
            final DataQrCode dataQrCode = new DataQrCode();
            dataQrCode.setQrcode(voids[0]);
            return  BLSFamilyManager.getInstance().joinFamilyByQrCode(dataQrCode);
        }

        @Override
        protected void onPostExecute(BLSFamilyCreateByQrResult blsFamilyQrResult) {
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
