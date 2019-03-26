package cn.com.broadlink.blappsdkdemo.activity.h5;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import cn.com.broadlink.base.BLCommonTools;
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLApiUrlConstants;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.data.RequestTimestampResult;
import cn.com.broadlink.blappsdkdemo.data.auth.DeviceAuthParam;
import cn.com.broadlink.blappsdkdemo.data.auth.DeviceAuthResult;
import cn.com.broadlink.blappsdkdemo.data.auth.UserHeadParam;
import cn.com.broadlink.blappsdkdemo.presenter.BLFamilyTimestampPresenter;
import cn.com.broadlink.blappsdkdemo.service.BLLocalFamilyManager;
import cn.com.broadlink.blappsdkdemo.utils.http.BLHttpErrCode;
import cn.com.broadlink.blappsdkdemo.utils.http.BLHttpPostAccessor;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.BLProgressDialog;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;

/**
 * 设备授权页面
 * Created by YeJin on 2016/9/9.
 */
public class DeviceAuthActivity extends TitleActivity {

    private FrameLayout mIconBGLayout;
    private ImageView mModuleIconView;
    private TextView mAuthDevView;
    private ListView mAuthListView;
    private Button mAuthBtnView;
    private BLDNADevice mDeviceInfo;
    private String mDevAuthStr;
    private String mAuthExtendInfo;
    private List<AuthInfo> mAuthList = new ArrayList<>();
    private AuthAdapter mAuthAdapter;
    private BLProgressDialog mBLprogressDialog;
    private Dialog mBLErrorDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_auth_layout);
        setBackWhiteVisible();
        setTitle("Auth");

        mDeviceInfo = getIntent().getParcelableExtra(BLConstants.INTENT_DEVICE);
        mDevAuthStr = getIntent().getStringExtra(BLConstants.INTENT_VALUE);

        findView();

        setListener();

        initData();

        initView();
    }

    private void findView(){
        mIconBGLayout = (FrameLayout) findViewById(R.id.icon_bg_layout);

        mModuleIconView = (ImageView) findViewById(R.id.module_icon_view);

        mAuthDevView = (TextView) findViewById(R.id.device_auth_view);

        mAuthListView = (ListView) findViewById(R.id.auth_listview);

        mAuthBtnView = (Button) findViewById(R.id.btn_auth);
    }

    private void setListener(){
        mAuthListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAuthList.get(position).selected = !mAuthList.get(position).selected;
                mAuthAdapter.notifyDataSetChanged();
            }
        });

        mAuthBtnView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                toAuth();
            }
        });
    }

    private void toAuth() {
        if(isAuth()){
            final DevAuthTask devAuthTask = new DevAuthTask();
            devAuthTask.execute();
            //设置10s超时
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        devAuthTask.get(10000, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        devAuthTask.stopHttpAccessor();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(mBLprogressDialog.isShowing()){
                                    mBLprogressDialog.dismiss();
                                }
                                showFailDialog();
                            }
                        });
                        e.printStackTrace();
                    }
                }
            }).start();
        }else{
            BLCommonUtils.toastShow(DeviceAuthActivity.this, R.string.str_device_select_authorized);
        }
    }

    private boolean isAuth(){
        for(AuthInfo authInfo : mAuthList){
            if(authInfo.selected) return true;
        }

        return false;
    }

    private class DevAuthTask extends AsyncTask<Void, Void, DeviceAuthResult> {
        BLHttpPostAccessor httpAccessor;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!isFinishing()){
                mBLprogressDialog.show();
            }
        }

        @Override
        protected DeviceAuthResult doInBackground(Void... params) {
            DeviceAuthParam deviceAuthParam = new DeviceAuthParam();
            deviceAuthParam.setDid(mDeviceInfo.getDid());
            deviceAuthParam.setExtend(mAuthExtendInfo);
            deviceAuthParam.setPid(mDeviceInfo.getPid());
            deviceAuthParam.setLid(BLLet.getLicenseId());

            for(AuthInfo authInfo : mAuthList){
                if(authInfo.selected) deviceAuthParam.getServerlist().add(authInfo.key);
            }

            RequestTimestampResult timestampResult = BLFamilyTimestampPresenter.getServerTimestamp(DeviceAuthActivity.this,false);
            if (timestampResult != null && timestampResult.getError() == BLHttpErrCode.SUCCESS) {
                String json = JSON.toJSONString(deviceAuthParam);

                byte[] loginBytes = BLCommonTools.aesNoPadding(BLCommonTools.parseStringToByte(timestampResult.getKey()), json);

                UserHeadParam baseHeadParam = new UserHeadParam(timestampResult.getTimestamp(),
                        BLCommonTools.md5(json + BLConstants.STR_BODY_ENCRYPT
                                + timestampResult.getTimestamp()
                                + BLApplication.mBLUserInfoUnits.getUserid()));
                baseHeadParam.setLoginsession(BLApplication.mBLUserInfoUnits.getLoginsession());
                baseHeadParam.setUserid(BLApplication.mBLUserInfoUnits.getUserid());
                baseHeadParam.setFamilyid(BLLocalFamilyManager.getInstance().getCurrentFamilyId());

                httpAccessor =  new BLHttpPostAccessor(DeviceAuthActivity.this);
                httpAccessor.setToastError(false);
                
                final String authUrl = BLApiUrlConstants.AppManager.DEVICE_AUTH();
                return httpAccessor.execute(authUrl, baseHeadParam, loginBytes, DeviceAuthResult.class);
            }
            return null;
        }

        @Override
        protected void onPostExecute(DeviceAuthResult blBaseResult) {
            super.onPostExecute(blBaseResult);
            mBLprogressDialog.dismiss();
            if(blBaseResult != null && blBaseResult.succeed() && blBaseResult.getTicket() != null){
                Intent intent = new Intent();
                intent.putExtra(BLConstants.INTENT_VALUE, blBaseResult.getTicket());
                setResult(RESULT_OK, intent);
                back();
            }else{
                showFailDialog();
            }
        }

        public void stopHttpAccessor(){
            if(httpAccessor != null){
                httpAccessor.stop();
                httpAccessor = null;
            }
        }
    }

    public void showFailDialog(){
        if(!isFinishing()){
            if(mBLErrorDialog != null && mBLErrorDialog.isShowing()){
                return;
            }

            mBLErrorDialog = BLAlert.showDilog(DeviceAuthActivity.this, getString(R.string.str_common_hint), getString(R.string.str_auth_fail_tip),
                    getString(R.string.str_common_try_again), getString(R.string.str_common_cancel), new BLAlert.DialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {
                            toAuth();
                        }

                        @Override
                        public void onNegativeClick() {
                            
                        }
                    });
        }
    }

    private void initData(){
        if(!TextUtils.isEmpty(mDevAuthStr)){
            try {
                JSONObject jsonObject = new JSONObject(mDevAuthStr);
                mAuthExtendInfo = jsonObject.optString("extend");
                JSONObject serviceObject = jsonObject.optJSONObject("servicelist");
                Iterator<String> keyIterator = serviceObject.keys();
                while (keyIterator.hasNext()){
                    String key =  keyIterator.next();
                    JSONObject descObject = serviceObject.optJSONObject(key);
                    AuthInfo authInfo = new AuthInfo();
                    authInfo.key = key;
                    try{
                        authInfo.title = descObject.optString("title");
                    }catch (Exception e){}
                    try{
                        authInfo.desc = descObject.optString("desc");
                    }catch (Exception e){}
                    mAuthList.add(authInfo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mBLprogressDialog = BLProgressDialog.createDialog(DeviceAuthActivity.this,getString(R.string.str_on_auth));

    }

    private void initView(){
        mAuthBtnView.setEnabled(!mAuthList.isEmpty());

        mAuthAdapter = new AuthAdapter(DeviceAuthActivity.this, mAuthList);
        mAuthListView.setAdapter(mAuthAdapter);

        mAuthDevView.setText(getString(R.string.str_device_authorize, mDeviceInfo.getName()));
        //BLImageLoaderUtils.getInstence(DeviceAuthActivity.this).displayImage(mDeviceInfo.getIconPath(), mModuleIconView, null);
    }

    private class AuthInfo{
        public String key;

        public String title;

        public String desc;

        public boolean selected = true;
    }

    private class AuthAdapter extends ArrayAdapter<AuthInfo> {

        public AuthAdapter(Context context, List<AuthInfo> objects) {
            super(context, 0, 0, objects);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.item_auth_list, null);
                viewHolder.selectView = (CheckBox) convertView.findViewById(R.id.select_view);
                viewHolder.authNameView = (TextView) convertView.findViewById(R.id.auth_name_view);
                viewHolder.descView = (TextView) convertView.findViewById(R.id.auth_desc_view);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.authNameView.setText(getItem(position).title);
//            viewHolder.descView.setText(getItem(position).desc);
            viewHolder.selectView.setChecked(getItem(position).selected);
            viewHolder.selectView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    getItem(position).selected = isChecked;
                }
            });
            return convertView;
        }

        private class ViewHolder{
            CheckBox selectView;
            TextView authNameView;
            TextView descView;
        }
    }
}
