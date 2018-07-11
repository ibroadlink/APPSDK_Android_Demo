package cn.com.broadlink.blappsdkdemo.activity.Family;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.broadlink.lib.imageloader.core.assist.FailReason;
import com.broadlink.lib.imageloader.core.display.FadeInBitmapDisplayer;
import com.broadlink.lib.imageloader.core.listener.ImageLoadingListener;
import com.broadlink.lib.imageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import cn.com.broadlink.base.BLCommonTools;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.Device.WebControlActivity;
import cn.com.broadlink.blappsdkdemo.activity.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLImageLoaderUtils;
import cn.com.broadlink.blappsdkdemo.intferfacer.FamilyInterface;
import cn.com.broadlink.blappsdkdemo.service.BLLocalFamilyManager;
import cn.com.broadlink.blappsdkdemo.view.OnSingleItemClickListener;
import cn.com.broadlink.family.params.BLFamilyAllInfo;
import cn.com.broadlink.family.params.BLFamilyDeviceInfo;
import cn.com.broadlink.family.params.BLFamilyModuleInfo;
import cn.com.broadlink.family.params.BLFamilyRoomInfo;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;
import cn.com.broadlink.sdk.result.controller.BLDownloadScriptResult;
import cn.com.broadlink.sdk.result.controller.BLDownloadUIResult;

public class FamilyModuleListActivity extends TitleActivity implements FamilyInterface {

    private ListView mModuleListView;
    private ModuleAdapter mAdapter;
    private BLDNADevice mDNADevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_module_list);
        setTitle(R.string.str_main_device_list);
        setBackWhiteVisible();

        findView();
        setListener();

        mAdapter = new ModuleAdapter(BLLocalFamilyManager.getInstance().getCurrentFamilyAllInfo().getModuleInfos());
        mModuleListView.setAdapter(mAdapter);

        BLLocalFamilyManager.getInstance().setFamilyInterface(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void familyInfoChanged(Boolean isChanged, String familyId, String familyVersion) {
        if (isChanged) {
            showProgressDialog(getResources().getString(R.string.loading));
            BLLocalFamilyManager.getInstance().queryFamilyAllInfo(null);
        }
    }

    @Override
    public void familyAllInfo(BLFamilyAllInfo allInfo) {
        dismissProgressDialog();
        if (allInfo != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void findView() {
        mModuleListView = (ListView) findViewById(R.id.module_listview);
    }

    private void setListener(){
        mModuleListView.setOnItemClickListener(new OnSingleItemClickListener() {
            @Override
            public void doOnClick(AdapterView<?> parent, View view, int position, long id) {
                List<BLFamilyModuleInfo> blFamilyModuleInfoList = BLLocalFamilyManager.getInstance().getCurrentFamilyAllInfo().getModuleInfos();
                BLFamilyModuleInfo info = blFamilyModuleInfoList.get(position);
                toModuleView(info);
            }
        });

        mModuleListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                List<BLFamilyModuleInfo> blFamilyModuleInfoList = BLLocalFamilyManager.getInstance().getCurrentFamilyAllInfo().getModuleInfos();
                BLFamilyModuleInfo moduleInfo = blFamilyModuleInfoList.get(i);
                final String moduleId = moduleInfo.getModuleId();
                String name = moduleInfo.getName();

                AlertDialog.Builder dialog = new AlertDialog.Builder(FamilyModuleListActivity.this);
                dialog.setTitle("Message");
                dialog.setMessage("Delete Module " + name);
                dialog.setCancelable(false);
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        BLLocalFamilyManager.getInstance().delModuleFromFamily(moduleId);
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.show();

                return true;
            }
        });
    }

    private void toModuleView(BLFamilyModuleInfo info) {
        int moduleType = info.getModuleType();

        if (moduleType == 1 || moduleType == 3) {
            // SP 模块和通用模块
            BLFamilyModuleInfo.ModuleDeviceInfo moduleDeviceInfo = info.getModuleDevs().get(0);
            String did = moduleDeviceInfo.getDid();

            List<BLFamilyDeviceInfo> deviceInfoList = BLLocalFamilyManager.getInstance().getCurrentFamilyAllInfo().getDeviceInfos();
            BLFamilyDeviceInfo blFamilyDeviceInfo = null;

            for (BLFamilyDeviceInfo deviceInfo : deviceInfoList) {
                if (did.equalsIgnoreCase(deviceInfo.getDid())) {
                    blFamilyDeviceInfo = deviceInfo;
                    break;
                }
            }

            if (blFamilyDeviceInfo == null) {
                BLCommonTools.error("Can not find device !");
                return;
            }

            mDNADevice = toDNADevice(blFamilyDeviceInfo);
            BLLet.Controller.addDevice(mDNADevice);

            String pid = mDNADevice.getPid();

            if (scriptFileExist(pid) && uiFileExit(pid)) {
                Intent intent = new Intent();
                intent.putExtra("INTENT_DEV_ID", mDNADevice);
                intent.setClass(FamilyModuleListActivity.this, WebControlActivity.class);
                startActivity(intent);
            } else {

                if (!scriptFileExist(pid)) {
                    new DownLoadScriptTask().execute();
                }

                if (!uiFileExit(pid)) {
                    new DownLoadUITask().execute();
                }

                BLCommonUtils.toastShow(FamilyModuleListActivity.this, "正在下载UI包，请稍后再试");
            }


        } else {
            //其他模块类型暂不支持
            BLCommonUtils.toastShow(FamilyModuleListActivity.this, "该类型本Demo暂不支持");
        }

    }

    private BLDNADevice toDNADevice(BLFamilyDeviceInfo familyDeviceInfo) {
        BLDNADevice bldnaDevice = new BLDNADevice();

        bldnaDevice.setDid(familyDeviceInfo.getDid());
        bldnaDevice.setPid(familyDeviceInfo.getPid());
        bldnaDevice.setMac(familyDeviceInfo.getMac());
        bldnaDevice.setName(familyDeviceInfo.getName());
        bldnaDevice.setType(familyDeviceInfo.getType());
        bldnaDevice.setId(familyDeviceInfo.getTerminalId());
        bldnaDevice.setKey(familyDeviceInfo.getAeskey());
        bldnaDevice.setPassword(familyDeviceInfo.getPassword());

        return bldnaDevice;
    }

    private boolean scriptFileExist(String pid){
        /***获取产品脚本本地保存的路径***/
        String scriptFilePath = BLLet.Controller.queryScriptPath(pid);
        Log.e("FileExist" , scriptFilePath);
        File file = new File(scriptFilePath);
        return file.exists();
    }

    private boolean uiFileExit(String pid) {
        String uiFilePath = BLLet.Controller.queryUIPath(pid);
        Log.e("UIExit", uiFilePath);
        File file = new File(uiFilePath);
        return file.exists();
    }

    //脚本下载
    class DownLoadScriptTask extends AsyncTask<Void, Void, BLDownloadScriptResult> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(FamilyModuleListActivity.this);
            progressDialog.setMessage("Script downloading...");
            progressDialog.show();
        }

        @Override
        protected BLDownloadScriptResult doInBackground(Void... params) {
            return BLLet.Controller.downloadScript(mDNADevice.getPid());
        }

        @Override
        protected void onPostExecute(BLDownloadScriptResult result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
        }
    }

    //UI包下载
    class DownLoadUITask extends AsyncTask<Void, Void, BLDownloadUIResult>{
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(FamilyModuleListActivity.this);
            progressDialog.setMessage("UI downloading...");
            progressDialog.show();
        }

        @Override
        protected BLDownloadUIResult doInBackground(Void... params) {
            return BLLet.Controller.downloadUI(mDNADevice.getPid());
        }

        @Override
        protected void onPostExecute(BLDownloadUIResult result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
        }
    }

    private class ModuleAdapter extends ArrayAdapter<BLFamilyModuleInfo>{
        private BLImageLoaderUtils imageLoaderUtils;

        private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

        public ModuleAdapter( List<BLFamilyModuleInfo> objects) {
            super(FamilyModuleListActivity.this, 0, 0, objects);
            imageLoaderUtils = BLImageLoaderUtils.getInstence(FamilyModuleListActivity.this);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.device_family_list_item_layout, null);
                viewHolder.familyIconView = (ImageView) convertView.findViewById(R.id.fiamily_icon_view);
                viewHolder.familyNameView = (TextView) convertView.findViewById(R.id.fiamily_name_view);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            //显示家庭的名称
            viewHolder.familyNameView.setText(getItem(position).getName());
            //显示家庭的图片
            imageLoaderUtils.displayImage(getItem(position).getIconPath(),viewHolder.familyIconView, animateFirstListener);

            return convertView;
        }

        private class ViewHolder{
            ImageView familyIconView;
            TextView familyNameView;
        }

        private class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

            final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                ImageView imageView = (ImageView) view;
                if (loadedImage != null) {
                    boolean firstDisplay = !displayedImages.contains(imageUri);
                    if (firstDisplay) {
                        FadeInBitmapDisplayer.animate(imageView, 500);
                        displayedImages.add(imageUri);
                    }
                }else{
                    imageView.setImageResource(R.drawable.default_family_bg);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                super.onLoadingFailed(imageUri, view, failReason);
                ImageView imageView = (ImageView) view;
                imageView.setImageResource(R.drawable.default_family_bg);
            }
        }
    }

}
