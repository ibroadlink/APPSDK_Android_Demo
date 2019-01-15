package cn.com.broadlink.blappsdkdemo.activity.Device;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.base.BLAppSdkErrCode;
import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.data.BLControlActConstans;
import cn.com.broadlink.sdk.BLLet;

import cn.com.broadlink.sdk.data.controller.BLDNADevice;
import cn.com.broadlink.sdk.data.controller.BLStdData;
import cn.com.broadlink.sdk.param.controller.BLStdControlParam;

import cn.com.broadlink.sdk.result.controller.BLStdControlResult;
import cn.com.broadlink.sdk.result.controller.BLSubDevListInfo;
import cn.com.broadlink.sdk.result.controller.BLSubDevListResult;

public class GateWayActivity extends Activity {

    private BLDNADevice mDNADevice;
    private BLDNADevice mSubDevice;
    private List<BLDNADevice> mSubDeviceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gate_way);

        mDNADevice = getIntent().getParcelableExtra("INTENT_DEV_ID");
    }

    public void subdevStartScan(View view) {
        new GateWayActivity.StartScanTask().execute();
    }

    public void getNewSubdevList(View view) {
        new GateWayActivity.GetNewSubDevsTask().execute();
    }

    public void addSubDevice(View view) {
        new GateWayActivity.AddSubDevTask().execute();
    }

    public void deleteSubDev(View view) {
        new GateWayActivity.DeleteSubDevTask().execute();
    }

    public void querySubDeviceList(View view) {
        new GateWayActivity.QuerySubDevListTask().execute();
    }

    public void getTcPairStatus(View view) {
        new GateWayActivity.GetTcPairStatusTask().execute();
    }

    public void setTcPairStatus(View view) {
        new GateWayActivity.SetTcPairTask().execute();
    }

    public void setTcPower(View view) {
        new GateWayActivity.SetTcPowerTask().execute();
    }

    //开始扫描子设备
    private class StartScanTask extends AsyncTask<String, Void, BLBaseResult> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(GateWayActivity.this);
            progressDialog.setMessage("扫描中...");
            progressDialog.show();
        }

        @Override
        protected BLBaseResult doInBackground(String... params) {
            return BLLet.Controller.subDevScanStart(mDNADevice.getDid(), null);
        }

        @Override
        protected void onPostExecute(BLBaseResult blBaseResult) {
            super.onPostExecute(blBaseResult);
            progressDialog.dismiss();
            if(blBaseResult != null && blBaseResult.getStatus() == BLAppSdkErrCode.SUCCESS){
                BLCommonUtils.toastShow(GateWayActivity.this, "Send Success");
            } else {
                BLCommonUtils.toastShow(GateWayActivity.this, "Send Failed " + blBaseResult.getMsg());
            }
        }
    }

    //获取待添加子设备
    private class GetNewSubDevsTask extends AsyncTask<String, Void, BLSubDevListResult> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(GateWayActivity.this);
            progressDialog.setMessage("获取中...");
            progressDialog.show();
        }

        @Override
        protected BLSubDevListResult doInBackground(String... params) {
            return BLLet.Controller.devSubDevNewListQuery(mDNADevice.getDid(), "00000000000000000000000011510000",0, 5);
        }

        @Override
        protected void onPostExecute(BLSubDevListResult blBaseResult) {
            super.onPostExecute(blBaseResult);
            progressDialog.dismiss();
            if(blBaseResult != null && blBaseResult.getStatus() == BLAppSdkErrCode.SUCCESS){
                BLCommonUtils.toastShow(GateWayActivity.this, "Get Success");

                BLSubDevListInfo subDevListInfo = blBaseResult.getData();
                if (subDevListInfo.getList() != null && !subDevListInfo.getList().isEmpty()) {
                    mSubDevice = subDevListInfo.getList().get(0);
                    mSubDevice.setpDid(mDNADevice.getDid());
                    Log.d(BLConstants.BROADLINK_LOG_TAG, "Subdev did: " + mSubDevice.getDid());
                }
            } else {
                BLCommonUtils.toastShow(GateWayActivity.this, "Get Failed " + blBaseResult.getMsg());
            }
        }
    }

    //添加子设备
    private class AddSubDevTask extends AsyncTask<String, Void, BLBaseResult> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(GateWayActivity.this);
            progressDialog.setMessage("添加中...");
            progressDialog.show();
        }

        @Override
        protected BLBaseResult doInBackground(String... params) {
            return BLLet.Controller.subDevAdd(mDNADevice.getDid(), mSubDevice);
        }

        @Override
        protected void onPostExecute(BLBaseResult blBaseResult) {
            super.onPostExecute(blBaseResult);
            progressDialog.dismiss();
            if(blBaseResult != null && blBaseResult.getStatus() == BLAppSdkErrCode.SUCCESS){
                BLCommonUtils.toastShow(GateWayActivity.this, "Add Success");
            } else {
                BLCommonUtils.toastShow(GateWayActivity.this, "Add Failed " + blBaseResult.getMsg());
            }
        }
    }

    //删除子设备
    private class DeleteSubDevTask extends AsyncTask<String, Void, BLBaseResult> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(GateWayActivity.this);
            progressDialog.setMessage("删除中...");
            progressDialog.show();
        }

        @Override
        protected BLBaseResult doInBackground(String... params) {
            if (mSubDevice != null) {
                return BLLet.Controller.subDevDel(mDNADevice.getDid(), mSubDevice.getDid());
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(BLBaseResult result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if(result != null && result.getStatus() == BLAppSdkErrCode.SUCCESS) {
                BLCommonUtils.toastShow(GateWayActivity.this, "Query Success");
            } else {
                BLCommonUtils.toastShow(GateWayActivity.this, "Query Failed " + result.getMsg());
            }
        }
    }

    //查询子设备列表
    private class QuerySubDevListTask extends AsyncTask<String, Void, BLSubDevListResult> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(GateWayActivity.this);
            progressDialog.setMessage("查询中...");
            progressDialog.show();
        }

        @Override
        protected BLSubDevListResult doInBackground(String... params) {
            return BLLet.Controller.devSubDevListQuery(mDNADevice.getDid(), 0, 5);
        }

        @Override
        protected void onPostExecute(BLSubDevListResult result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if(result != null && result.getStatus() == BLAppSdkErrCode.SUCCESS) {
                mSubDeviceList.clear();

                if (result.getData() != null) {
                    for (BLDNADevice dev  : result.getData().getList() ) {
                        dev.setpDid(mDNADevice.getDid());
                        mSubDeviceList.add(dev);
                    }
                }

                if (mSubDeviceList.size() > 0) {
                    mSubDevice = mSubDeviceList.get(0);
                    BLLet.Controller.addDevice(mSubDevice);
                } else {
                    mSubDevice = null;
                }

                BLCommonUtils.toastShow(GateWayActivity.this, "Query Success " + String.valueOf(mSubDeviceList.size()));
            } else {
                BLCommonUtils.toastShow(GateWayActivity.this, "Query Failed " + result.getMsg());
            }
        }
    }

    //虚拟子设备控制， 需要特定的脚本
    //TC 设备
    //{"vals":[[{"val":1,"idx":1}]],"did":"xxxxxx","password":"ab0d4322","act":"get","prop":"stdctrl","params":["switch_pair_status"]}
    private class GetTcPairStatusTask extends AsyncTask<String, Void, BLStdControlResult> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(GateWayActivity.this);
            progressDialog.setMessage("获取中...");
            progressDialog.show();
        }

        @Override
        protected BLStdControlResult doInBackground(String... params) {
            BLStdData.Value value = new BLStdData.Value();
            value.setVal(1);

            ArrayList<BLStdData.Value> pwrVals = new ArrayList<>();
            pwrVals.add(value);

            BLStdControlParam ctrlParam = new BLStdControlParam();
            ctrlParam.setAct(BLControlActConstans.ACT_GET);
            ctrlParam.setPassword("ab0d4322");
            ctrlParam.setDid(mSubDevice.getDid());
            ctrlParam.setSrv("1.10.1");
            ctrlParam.getParams().add("switch_pair_status");
            ctrlParam.getVals().add(pwrVals);

            return BLLet.Controller.dnaControl(mDNADevice.getDid(), mSubDevice.getDid(), ctrlParam);
        }

        @Override
        protected void onPostExecute(BLStdControlResult result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if(result != null && result.getStatus() == BLAppSdkErrCode.SUCCESS){
                BLCommonUtils.toastShow(GateWayActivity.this, "Query Success");

            } else {
                BLCommonUtils.toastShow(GateWayActivity.this, "Query Failed " + result.getMsg());
            }
        }
    }

    //虚拟子设备控制， 需要特定的脚本
    //TC 设备
    //{"vals":[[{"val":1,"idx":1}]],"did":"xxxxxx","password":"ab0d4322","act":"set","prop":"stdctrl","params":["switch_pair"]}
    private class SetTcPairTask extends AsyncTask<String, Void, BLStdControlResult> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(GateWayActivity.this);
            progressDialog.setMessage("获取中...");
            progressDialog.show();
        }

        @Override
        protected BLStdControlResult doInBackground(String... params) {
            BLStdData.Value value = new BLStdData.Value();
            value.setVal(1);

            ArrayList<BLStdData.Value> pwrVals = new ArrayList<>();
            pwrVals.add(value);

            BLStdControlParam ctrlParam = new BLStdControlParam();
            ctrlParam.setAct(BLControlActConstans.ACT_SET);
            ctrlParam.setPassword("ab0d4322");
            ctrlParam.setDid(mSubDevice.getDid());
            ctrlParam.setSrv("1.10.1");
            ctrlParam.getParams().add("switch_pair");
            ctrlParam.getVals().add(pwrVals);

            return BLLet.Controller.dnaControl(mDNADevice.getDid(), mSubDevice.getDid(), ctrlParam);
        }

        @Override
        protected void onPostExecute(BLStdControlResult result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if(result != null && result.getStatus() == BLAppSdkErrCode.SUCCESS){
                BLCommonUtils.toastShow(GateWayActivity.this, "Set TC pair Success");

            } else {
                BLCommonUtils.toastShow(GateWayActivity.this, "Set TC pair Failed " + result.getMsg());
            }
        }
    }

    //虚拟子设备控制， 需要特定的脚本
    //TC 设备
    //{"vals":[[{"val":1,"idx":1}]],"did":"xxxx","password":"ab0d4322","act":"set","prop":"stdctrl","params":["power1"]}
    private class SetTcPowerTask extends AsyncTask<String, Void, BLStdControlResult> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(GateWayActivity.this);
            progressDialog.setMessage("设置中...");
            progressDialog.show();
        }

        @Override
        protected BLStdControlResult doInBackground(String... params) {
            BLStdData.Value value = new BLStdData.Value();
            value.setVal(1);

            ArrayList<BLStdData.Value> pwrVals = new ArrayList<>();
            pwrVals.add(value);

            BLStdControlParam ctrlParam = new BLStdControlParam();
            ctrlParam.setAct(BLControlActConstans.ACT_SET);
            ctrlParam.setPassword("ab0d4322");
            ctrlParam.setDid(mSubDevice.getDid());
            ctrlParam.getParams().add("power1");
            ctrlParam.setSrv("1.10.1");
            ctrlParam.getVals().add(pwrVals);

            return BLLet.Controller.dnaControl(mDNADevice.getDid(), mSubDevice.getDid(), ctrlParam);
        }

        @Override
        protected void onPostExecute(BLStdControlResult result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if(result != null && result.getStatus() == BLAppSdkErrCode.SUCCESS){
                BLCommonUtils.toastShow(GateWayActivity.this, "Set TC power Success");

            } else {
                BLCommonUtils.toastShow(GateWayActivity.this, "Set TC power Failed " + result.getMsg());
            }
        }
    }

}
