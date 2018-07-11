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
import cn.com.broadlink.blappsdkdemo.activity.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLImageLoaderUtils;
import cn.com.broadlink.blappsdkdemo.intferfacer.FamilyInterface;
import cn.com.broadlink.blappsdkdemo.service.BLLocalDeviceManager;
import cn.com.broadlink.blappsdkdemo.service.BLLocalFamilyManager;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.family.BLFamily;
import cn.com.broadlink.family.params.BLFamilyAllInfo;
import cn.com.broadlink.family.params.BLFamilyDeviceInfo;
import cn.com.broadlink.family.params.BLFamilyInfo;
import cn.com.broadlink.family.params.BLFamilyModuleInfo;
import cn.com.broadlink.family.params.BLFamilyRoomInfo;
import cn.com.broadlink.family.result.BLAllFamilyInfoResult;
import cn.com.broadlink.family.result.BLModuleControlResult;

import cn.com.broadlink.sdk.data.controller.BLDNADevice;

public class FamilyDetailActivity extends TitleActivity implements FamilyInterface {

    private BLFamilyInfo blFamilyInfo;
    private List<BLFamilyRoomInfo> blFamilyRoomInfoList = new ArrayList<>();
    private List<BLFamilyModuleInfo> blFamilyModuleInfoList = new ArrayList<>();

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
    public void familyAllInfo(BLFamilyAllInfo allInfo) {
        dismissProgressDialog();

        if (allInfo != null) {
            //Family Info
            blFamilyInfo = allInfo.getFamilyInfo();

            setTitle(blFamilyInfo.getFamilyName());
            mFamilyNameView.setText(blFamilyInfo.getFamilyName());
            initFamilyIconView();

            //Room Info
            blFamilyRoomInfoList.clear();
            blFamilyRoomInfoList.addAll(allInfo.getRoomInfos());
            int roomCount = blFamilyRoomInfoList.size();
            mRoomCountView.setText(getString(R.string.str_settings_place_number_of_room, roomCount+""));

            //Module Info
            blFamilyModuleInfoList.clear();
            blFamilyModuleInfoList.addAll(allInfo.getModuleInfos());
            int ModuleCount = blFamilyModuleInfoList.size();
            mDeviceCountView.setText(String.valueOf(ModuleCount));

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
                intent.setClass(FamilyDetailActivity.this, FamilyRoomListActivity.class);
                startActivity(intent);
            }
        });

        mDeviceLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                Intent intent = new Intent();
                intent.setClass(FamilyDetailActivity.this, FamilyModuleListActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initFamilyIconView(){
        mBlImageLoaderUtils.displayImage(blFamilyInfo.getFamilyIconPath(), mFamilyIconView, new SimpleImageLoadingListener(){
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

    private void refreshCountryView(BLFamilyInfo blFamilyInfo){
        if(blFamilyInfo != null){
            mCountryView.setText(blFamilyInfo.getFamilyCountry());
            if(blFamilyInfo.getFamilyProvince() != null){
                mCountryView.append(" ");
                mCountryView.append(blFamilyInfo.getFamilyProvince());
            }
            if(blFamilyInfo.getFamilyCity() != null){
                mCountryView.append(" ");
                mCountryView.append(blFamilyInfo.getFamilyCity());
            }
        }else{
            mCountryView.setText(R.string.str_settings_safety_unsetting);
        }
    }

    private void getFamilyAllInfoFromCloud() {
        showProgressDialog(getResources().getString(R.string.loading));
        BLLocalFamilyManager.getInstance().queryFamilyAllInfo(null);
    }

//    private List<BLFamilyModuleInfo> blFamilyModuleInfoList = new ArrayList<>();
//    public void createNewModule(View view) {
//        new CreateModuleTask().execute();
//    }
//
//    class DelModuleTask extends AsyncTask<String, Void, BLModuleControlResult>{
//        ProgressDialog progressDialog;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            progressDialog = new ProgressDialog(FamilyDetailActivity.this);
//            progressDialog.setMessage(getResources().getString(R.string.loading));
//            progressDialog.show();
//        }
//
//        @Override
//        protected BLModuleControlResult doInBackground(String... strings) {
//            String moduleId = strings[0];
//            String familyId = strings[1];
//            String familyVersion = strings[2];
//
//            return BLFamily.delModuleFromFamily(moduleId, familyId, familyVersion);
//        }
//
//        @Override
//        protected void onPostExecute(BLModuleControlResult result) {
//            super.onPostExecute(result);
//            progressDialog.dismiss();
//
//            if (result.succeed()) {
//                //We just query only one family
//                getFamilyAllInfoFromCloud();
//            } else {
//                BLCommonUtils.toastShow(FamilyDetailActivity.this, "DelModuleTask failed! Error: " + String.valueOf(result.getStatus()));
//            }
//        }
//    }
//
//    class CreateModuleTask extends AsyncTask<String, Void, BLModuleControlResult>{
//        ProgressDialog progressDialog;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            progressDialog = new ProgressDialog(FamilyDetailActivity.this);
//            progressDialog.setMessage(getResources().getString(R.string.loading));
//            progressDialog.show();
//        }
//
//        @Override
//        protected BLModuleControlResult doInBackground(String... strings) {
//            String roomId = "";
//            if (blFamilyRoomInfoList != null && blFamilyRoomInfoList.size() > 0) {
//                BLFamilyRoomInfo roomInfo = blFamilyRoomInfoList.get(0);
//                roomId = roomInfo.getRoomId();
//            }
//
//
//            BLFamilyModuleInfo blFamilyModuleInfo = new BLFamilyModuleInfo();
//            blFamilyModuleInfo.setFamilyId(mFamilyId);
//            blFamilyModuleInfo.setRoomId(roomId);
//            blFamilyModuleInfo.setName("Test Module");
//            blFamilyModuleInfo.setFlag(0);
//            blFamilyModuleInfo.setOrder(1);
//            blFamilyModuleInfo.setModuleType(1);    // 模块类型 SP
//            blFamilyModuleInfo.setFollowDev(1);     // 模块下挂载设备数 1
//
//            BLFamilyModuleInfo.ModuleDeviceInfo moduleDeviceInfo = null;
//            BLFamilyDeviceInfo blFamilyDeviceInfo = null;
//            BLDNADevice bldnaDevice = null;
//
//            List<BLDNADevice> localDevices = mLocalDeviceManager.getDevicesAddInSDK();
//            if (localDevices != null && localDevices.size() > 0) {
//                bldnaDevice = localDevices.get(0);
//            }
//
//            if (bldnaDevice != null) {
//                moduleDeviceInfo = new BLFamilyModuleInfo.ModuleDeviceInfo();
//                moduleDeviceInfo.setDid(bldnaDevice.getDid());
//                moduleDeviceInfo.setSdid(null);
//                moduleDeviceInfo.setOrder(1);
//                moduleDeviceInfo.setContent(null);
//
//                List<BLFamilyModuleInfo.ModuleDeviceInfo> moduleDeviceInfoList = new ArrayList<>();
//                moduleDeviceInfoList.add(moduleDeviceInfo);
//                blFamilyModuleInfo.setModuleDevs(moduleDeviceInfoList);
//
//                blFamilyDeviceInfo = new BLFamilyDeviceInfo();
//                blFamilyDeviceInfo.setFamilyId(mFamilyId);
//                blFamilyDeviceInfo.setRoomId(roomId);
//                blFamilyDeviceInfo.setPid(bldnaDevice.getPid());
//                blFamilyDeviceInfo.setDid(bldnaDevice.getDid());
//                blFamilyDeviceInfo.setName(bldnaDevice.getName());
//                blFamilyDeviceInfo.setMac(bldnaDevice.getMac());
//                blFamilyDeviceInfo.setTerminalId(bldnaDevice.getId());
//                blFamilyDeviceInfo.setAeskey(bldnaDevice.getKey());
//                blFamilyDeviceInfo.setPassword((int) bldnaDevice.getPassword());
//                blFamilyDeviceInfo.setType(bldnaDevice.getType());
//            }
//
//            return BLFamily.addModuleToFamily(blFamilyModuleInfo, blFamilyInfo, blFamilyDeviceInfo, null);
//        }
//
//        @Override
//        protected void onPostExecute(BLModuleControlResult result) {
//            super.onPostExecute(result);
//            progressDialog.dismiss();
//
//            if (result.succeed()) {
//                //We just query only one family
//                getFamilyAllInfoFromCloud();
//            } else {
//                BLCommonUtils.toastShow(FamilyDetailActivity.this, "CreateModuleTask failed! Error: " + String.valueOf(result.getStatus()));
//            }
//        }
//    }
//
//    private class FamilyModuleAdapter extends ArrayAdapter<BLFamilyModuleInfo> {
//        public FamilyModuleAdapter(Context context, List<BLFamilyModuleInfo> objects) {
//            super(context, 0, objects);
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            FamilyDetailActivity.FamilyModuleAdapter.ViewHolder viewHolder;
//            if(convertView == null){
//                viewHolder = new FamilyDetailActivity.FamilyModuleAdapter.ViewHolder();
//                convertView = getLayoutInflater().inflate(R.layout.adapter_family_module, null);
//                viewHolder.moduleName = (TextView) convertView.findViewById(R.id.module_name);
//                viewHolder.moduleId = (TextView) convertView.findViewById(R.id.module_id);
//                viewHolder.roomName = (TextView) convertView.findViewById(R.id.module_Belong_room);
//                convertView.setTag(viewHolder);
//            } else {
//                viewHolder = (FamilyDetailActivity.FamilyModuleAdapter.ViewHolder) convertView.getTag();
//            }
//
//            BLFamilyModuleInfo moduleInfo = getItem(position);
//
//            viewHolder.moduleName.setText(moduleInfo.getName());
//            viewHolder.moduleId.setText(moduleInfo.getModuleId());
//
//            String roomId = moduleInfo.getRoomId();
//            for (BLFamilyRoomInfo roomInfo: blFamilyRoomInfoList) {
//                if (roomId.equals(roomInfo.getRoomId())) {
//                    viewHolder.roomName.setText(roomInfo.getName());
//                    break;
//                }
//            }
//
//            return convertView;
//        }
//
//        private class ViewHolder{
//            TextView moduleName;
//            TextView moduleId;
//            TextView roomName;
//        }
//    }
}
