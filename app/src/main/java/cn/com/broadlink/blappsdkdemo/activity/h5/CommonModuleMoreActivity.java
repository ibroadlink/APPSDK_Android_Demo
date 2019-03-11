package cn.com.broadlink.blappsdkdemo.activity.h5;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.sql.SQLException;

import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.activity.family.manager.BLSFamilyManager;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.db.data.BLDeviceInfo;
import cn.com.broadlink.blappsdkdemo.db.dao.BLDeviceInfoDao;
import cn.com.broadlink.blappsdkdemo.service.BLLocalDeviceManager;
import cn.com.broadlink.blappsdkdemo.service.BLLocalFamilyManager;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;
import cn.com.broadlink.sdk.result.controller.BLSubdevResult;
import cn.com.broadlink.sdk.result.controller.BLUpdateDeviceResult;


public class CommonModuleMoreActivity extends TitleActivity {

    private LinearLayout mLlName;
    private TextView mTvName;
    private LinearLayout mBtnRoom;
    private TextView mTvRoom;
    private Button mBtDel;
    private BLDNADevice mDNADevice;
    private TextView mTvDevInfo;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h5_dev_property);
        setTitle("Property");
        setBackWhiteVisible();
        
        mDNADevice = getIntent().getParcelableExtra(BLConstants.INTENT_DEVICE);
        
        findView();
        
        setListener();

        initView();
    }

    private void initView() {
        mTvName.setText(mDNADevice.getName());
        mTvDevInfo.setText(mDNADevice.toJSONString());
    }

    private void findView() {
        mLlName = (LinearLayout) findViewById(R.id.ll_name);
        mTvName = (TextView) findViewById(R.id.tv_name);
        mBtnRoom = (LinearLayout) findViewById(R.id.btn_room);
        mTvRoom = (TextView) findViewById(R.id.tv_room);
        mBtDel = (Button) findViewById(R.id.bt_del);
        mTvDevInfo = (TextView) findViewById(R.id.tv_dev_info);
    }

    private void setListener() {
        mLlName.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                BLAlert.showEditDilog(mActivity, "Please input a new name", mDNADevice.getName(), new BLAlert.BLEditDialogOnClickListener() {
                    @Override
                    public void onClink(String value) {
                        new ModifyNameTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR, value);
                    }

                    @Override
                    public void onClinkCacel(String value) {

                    }
                }, false);
            }
        });

        mBtDel.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                BLAlert.showDialog(mActivity, "Confirm to delete?", new BLAlert.DialogOnClickListener() {
                    @Override
                    public void onPositiveClick() {
                        new DelDevTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
                    }

                    @Override
                    public void onNegativeClick() {

                    }
                });
            }
        });
    }

    private class ModifyNameTask extends AsyncTask<String, Void, BLUpdateDeviceResult> {
        String name;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Modify Name...");
        }

        @Override
        protected BLUpdateDeviceResult doInBackground(String... strings) {
            name = strings[0];
            return BLLet.Controller.updateDeviceInfo(mDNADevice.getDid(), name, mDNADevice.isLock() );
        }

        @Override
        protected void onPostExecute(BLUpdateDeviceResult result) {
            super.onPostExecute(result);
            dismissProgressDialog();
            if (result != null && result.succeed() ) {
                mTvName.setText(name);
            }else{
                BLCommonUtils.toastErr(result);
            }
        }
    }

    private class DelDevTask extends AsyncTask<Integer, Void, BLBaseResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Delete..");
        }

        @Override
        protected BLBaseResult doInBackground(Integer... params) {
            if(!TextUtils.isEmpty(mDNADevice.getpDid())){
                final BLSubdevResult blSubdevResult = BLLet.Controller.subDevDel(mDNADevice.getpDid(), mDNADevice.getDid());
                if(blSubdevResult == null || !blSubdevResult.succeed()){
                    return blSubdevResult;
                }
            }
            final BLBaseResult blBaseResult = BLSFamilyManager.getInstance().delEndpoint(BLLocalFamilyManager.getInstance().getCurrentFamilyId(), mDNADevice.getDid());
            
            BLLocalDeviceManager.getInstance().removeDeviceFromSDK(mDNADevice.getDid());
            try {
                BLDeviceInfoDao blDeviceInfoDao = new BLDeviceInfoDao(getHelper());
                BLDeviceInfo deviceInfo = new BLDeviceInfo(mDNADevice);
                blDeviceInfoDao.deleteDevice(deviceInfo);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return blBaseResult;
        }

        @Override
        protected void onPostExecute(BLBaseResult blBaseResult) {
            super.onPostExecute(blBaseResult);
            dismissProgressDialog();
            if(blBaseResult != null && blBaseResult.succeed()){
                BLToastUtils.show("Delete success!");
                back();
            }else{
                BLCommonUtils.toastErr(blBaseResult);
            }
        }
    }

}
