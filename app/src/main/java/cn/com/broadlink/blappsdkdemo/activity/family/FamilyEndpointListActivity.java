package cn.com.broadlink.blappsdkdemo.activity.family;


import android.app.ProgressDialog;
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

import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.activity.device.DevProbeListActivity;
import cn.com.broadlink.blappsdkdemo.activity.h5.DeviceH5Activity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLImageLoaderUtils;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.blappsdkdemo.view.OnSingleItemClickListener;
import cn.com.broadlink.blsfamily.BLSFamily;
import cn.com.broadlink.blsfamily.bean.BLSBaseDataResult;
import cn.com.broadlink.blsfamily.bean.endpoint.BLSEndpointInfo;
import cn.com.broadlink.blsfamily.bean.endpoint.BLSEndpointListData;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;

public class FamilyEndpointListActivity extends TitleActivity {

    private ListView mModuleListView;
    private ModuleAdapter mAdapter;
    private BLDNADevice mDNADevice;
    private String mFamilyId = null;
    private List<BLSEndpointInfo> blsEndpointInfos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_module_list);
        setTitle(R.string.str_main_device_list);
        setBackWhiteVisible();

        findView();
        
        setListener();

        mAdapter = new ModuleAdapter(blsEndpointInfos);
        mModuleListView.setAdapter(mAdapter);

        Intent intent = getIntent();
        if (intent != null) {
            mFamilyId = getIntent().getStringExtra(BLConstants.INTENT_FAMILY_ID);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateFamilyEndpoints();
    }

    private void findView() {
        mModuleListView = (ListView) findViewById(R.id.module_listview);
    }

    private void setListener(){
        setRightButtonOnClickListener(R.string.str_common_add,
                getResources().getColor(R.color.bl_yellow_main_color),
                new OnSingleClickListener() {

                    @Override
                    public void doOnClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra(BLConstants.INTENT_FAMILY_ID, mFamilyId);
                        intent.putExtra(BLConstants.INTENT_CLASS, FamilyEndpointListActivity.class.getSimpleName());
                        intent.setClass(FamilyEndpointListActivity.this, DevProbeListActivity.class);
                        startActivity(intent);
                    }
                });


        mModuleListView.setOnItemClickListener(new OnSingleItemClickListener() {
            @Override
            public void doOnClick(AdapterView<?> parent, View view, int position, long id) {
                BLSEndpointInfo info = blsEndpointInfos.get(position);
                toEndpointView(info);
            }
        });

        mModuleListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                final BLSEndpointInfo info = blsEndpointInfos.get(position);
                String name = info.getFriendlyName();
                BLAlert.showDialog(FamilyEndpointListActivity.this, "Delete EndPoint " + name, new BLAlert.DialogOnClickListener() {
                    @Override
                    public void onPositiveClick() {
                        new DelEndpointTask().execute(info.getEndpointId());
                    }

                    @Override
                    public void onNegativeClick() {

                    }
                });
                
                return true;
            }
        });
    }

    private void updateFamilyEndpoints() {
        new QueryEndpointListTask().execute(mFamilyId);
    }

    private class QueryEndpointListTask extends AsyncTask<String, Void, BLSBaseDataResult<BLSEndpointListData> > {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Loading...");
        }

        @Override
        protected BLSBaseDataResult<BLSEndpointListData> doInBackground(String... strings) {
            String familyId = strings[0];

            return BLSFamily.Endpoint.getList(familyId);
        }

        @Override
        protected void onPostExecute(BLSBaseDataResult<BLSEndpointListData>  result) {
            super.onPostExecute(result);
            dismissProgressDialog();
            if (result != null && result.succeed() && result.getData() != null) {
                blsEndpointInfos.clear();

                if (result.getData().getEndpoints() != null) {
                    blsEndpointInfos.addAll(result.getData().getEndpoints());
                }else{
                    BLCommonUtils.toastErr(result);
                }

                mAdapter.notifyDataSetChanged();
            }else{
                BLCommonUtils.toastErr(result);
            }
        }
    }

    private void toEndpointView(BLSEndpointInfo info) {

        mDNADevice = info.toDnadeviceInfo();
        BLLet.Controller.addDevice(mDNADevice);
        String pid = mDNADevice.getPid();

        if (scriptFileExist(pid) && uiFileExit(pid)) {
            gotoNextPage(mDNADevice);
        } else {
            if (!scriptFileExist(pid) || !uiFileExit(pid)) {
                new DownLoadResTask().execute(pid);
            }
        }
    }

    private boolean scriptFileExist(String pid){
        String scriptFilePath = BLLet.Controller.queryScriptPath(pid);
        Log.e("scriptFileExist" , scriptFilePath);
        File file = new File(scriptFilePath);
        return file.exists();
    }

    private boolean uiFileExit(String pid) {
        String uiFilePath = BLLet.Controller.queryUIPath(pid);
        Log.e("uiFileExit", uiFilePath);
        File file = new File(uiFilePath);
        return file.exists();
    }

    private void gotoNextPage(BLDNADevice mDNADevice) {
        Intent intent = new Intent();
        intent.putExtra(BLConstants.INTENT_DEVICE, mDNADevice);
        intent.setClass(mActivity, DeviceH5Activity.class);
        startActivity(intent);
    }
    
    //脚本和ui包下载
    class DownLoadResTask extends AsyncTask<String, Void, BLBaseResult> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mActivity);
            progressDialog.setMessage("Resource downloading...");
            progressDialog.show();
        }

        @Override
        protected BLBaseResult doInBackground(String... params) {
            BLBaseResult result = null;

            if (!scriptFileExist(params[0])){
                result = BLLet.Controller.downloadScript(params[0]);
                if(result==null || !result.succeed()){
                    return result;
                }
            }

            if (!uiFileExit(params[0])){
                result = BLLet.Controller.downloadUI(params[0]);
            }
            return result;
        }

        @Override
        protected void onPostExecute(BLBaseResult result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            BLCommonUtils.toastErr(result);

            String pid = mDNADevice.getPid();
            if (scriptFileExist(pid) && uiFileExit(pid)) {
                gotoNextPage(mDNADevice);
            }
        }
    }
    
    class DelEndpointTask extends AsyncTask<String, Void, BLBaseResult>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Deleting...");
        }

        @Override
        protected BLBaseResult doInBackground(String... Strings) {
            String endpointId = Strings[0];

            return BLSFamily.Endpoint.delete(mFamilyId, endpointId);
        }

        @Override
        protected void onPostExecute(BLBaseResult result) {
            super.onPostExecute(result);
           dismissProgressDialog();
           BLCommonUtils.toastErr(result);
            updateFamilyEndpoints();
        }
    }

    private class ModuleAdapter extends ArrayAdapter<BLSEndpointInfo> {
        private BLImageLoaderUtils imageLoaderUtils;
        private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

        public ModuleAdapter( List<BLSEndpointInfo> objects) {
            super(FamilyEndpointListActivity.this, 0, 0, objects);
            imageLoaderUtils = BLImageLoaderUtils.getInstence(FamilyEndpointListActivity.this);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.item_family_dev, null);
                viewHolder.familyIconView = (ImageView) convertView.findViewById(R.id.fiamily_icon_view);
                viewHolder.familyNameView = (TextView) convertView.findViewById(R.id.fiamily_name_view);
                viewHolder.tvDid = (TextView) convertView.findViewById(R.id.tv_did);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.familyNameView.setText(getItem(position).getFriendlyName());
            viewHolder.tvDid.setText(getItem(position).getEndpointId());
            imageLoaderUtils.displayImage(getItem(position).getIcon(),viewHolder.familyIconView, animateFirstListener);

            return convertView;
        }

        private class ViewHolder{
            ImageView familyIconView;
            TextView familyNameView;
            TextView tvDid;
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
