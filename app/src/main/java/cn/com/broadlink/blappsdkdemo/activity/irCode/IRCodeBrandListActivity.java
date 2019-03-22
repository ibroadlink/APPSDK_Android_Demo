package cn.com.broadlink.blappsdkdemo.activity.irCode;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLLog;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.data.BLIRCodeArea;
import cn.com.broadlink.blappsdkdemo.data.CloudAcBrandResponse;
import cn.com.broadlink.blappsdkdemo.data.RmIrTreeResult;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseRecyclerAdapter;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseViewHolder;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.divideritemdecoration.BLDividerUtil;
import cn.com.broadlink.ircode.BLIRCode;
import cn.com.broadlink.ircode.result.BLDownLoadIRCodeResult;
import cn.com.broadlink.ircode.result.BLResponseResult;
import cn.com.broadlink.sdk.BLLet;

public class IRCodeBrandListActivity extends TitleActivity {


    private RecyclerView mRvContent;
    private ImageView mIvRefresh;
    private int mDeviceType;
    private String mSubAreaId = null;
    private List<BLIRCodeArea> mAreas = new ArrayList<>();
    private DevAdapter mAdapter;

    private String mBrandID = null;
    private String mDownloadUrl = null;
    private String mScriptRandkey = null;
    private String mScriptName = null;
    private Boolean mIsLeaf = false;
    private String mProviderId = null;
    private String mSavePath = null;
    private boolean mIsMatchTree = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Add By Brand");
        setContentView(R.layout.activity_recyclerview);
        setBackWhiteVisible();
        
        mDeviceType = getIntent().getIntExtra(BLConstants.INTENT_VALUE, BLConstants.BL_IRCODE_DEVICE_AC);
        mIsMatchTree = getIntent().getBooleanExtra(BLConstants.INTENT_TYPE, false);
        
        if (mDeviceType == BLConstants.BL_IRCODE_DEVICE_TV_BOX) {
            mSubAreaId = "0"; // 默认国内
        }

        findView();

        initView();

        setListener();
        
        queryIRCodeArea();
    }

    private void initView() {
        mAdapter = new DevAdapter();
        mRvContent.setLayoutManager(new LinearLayoutManager(mActivity));
        mRvContent.setAdapter(mAdapter);
        mRvContent.addItemDecoration(BLDividerUtil.getDefault(mActivity, mAreas));
    }

    private void findView() {

        mRvContent = (RecyclerView) findViewById(R.id.rv_content);
        mIvRefresh = (ImageView) findViewById(R.id.iv_refresh);
        mIvRefresh.setVisibility(View.GONE);
    }

    private void setListener() {
        mAdapter.setOnItemClickListener(new BLBaseRecyclerAdapter.OnClickListener() {
            @Override
            public void onClick(int position, int viewType) {
                final BLIRCodeArea area = mAreas.get(position);

                if (mDeviceType == BLConstants.BL_IRCODE_DEVICE_AC || mDeviceType == BLConstants.BL_IRCODE_DEVICE_TV) {
                    if (mBrandID == null) {
                        mBrandID = area.getAreaId();
                    } else {
                        mDownloadUrl = area.getDownloadUrl();
                        mScriptRandkey = area.getAreaId();
                        mScriptName = area.getAreaName();
                    }
                    queryIRCodeArea();
                } else {
                    if(mIsMatchTree){
                        mBrandID = area.getAreaId();
                        queryIRCodeArea(); 
                    }else{
                        if (!mIsLeaf) {
                            mSubAreaId = area.getAreaId();
                            mIsLeaf = area.isleaf();

                            if (mIsLeaf) {
                                //已经是最后一层叶子支点, 查询当地运营商ID
                                Log.d(BLConstants.BROADLINK_LOG_TAG, area.getAreaName() + " is leaf");
                                querySTBSubAreaProvider();
                            } else {
                                queryIRCodeArea();
                            }

                        } else {
                            if (mProviderId == null) {
                                mProviderId = area.getAreaId();
                            } else if (mDownloadUrl == null) {
                                mDownloadUrl = area.getDownloadUrl();
                                mScriptRandkey = area.getAreaId();
                                mScriptName = area.getAreaName();
                            }

                            querySTBIRCodeScriptDownloadUrl();
                        }
                    }
                }
            }
        });
    }
    
    private void goToNextActivity() {
        if (mDeviceType == BLConstants.BL_IRCODE_DEVICE_AC) {
            BLCommonUtils.toActivity(mActivity, IRCodeAcPanelActivity.class, mSavePath);
        }else{ 
            BLCommonUtils.toActivity(mActivity, IRCodeTvOrBoxPanelActivity.class, mSavePath);
        }
    }
    
    private void downloadScript(){
        mSavePath = BLLet.Controller.queryIRCodePath() + File.separator + mScriptName;
        new DownLoadScriptTask().execute(mDownloadUrl, mSavePath, mScriptRandkey);
    }
    
    private void querySTBSubAreaProvider() {
        if (mDeviceType == BLConstants.BL_IRCODE_DEVICE_TV_BOX) {
            new QuerySubAreaProviderTask().execute(mSubAreaId);
        }
    }

    private void querySTBIRCodeScriptDownloadUrl() {
        if (mDeviceType == BLConstants.BL_IRCODE_DEVICE_TV_BOX) {
            if (mDownloadUrl == null) {
                new QueryStbScriptDownloadUrlTask().execute(mSubAreaId, mProviderId);
            } else {
                downloadScript();
            }
        }
    }

    private void queryIRCodeArea() {
        if (mDeviceType == BLConstants.BL_IRCODE_DEVICE_AC) {
            
            // 空调设备需要查询 Brand 和 Version
            if (mBrandID == null) {
                new QueryIRCodeBrandTask().execute(String.valueOf(mDeviceType));
            } else if (mDownloadUrl == null){
                new QueryAcScriptDownloadUrlTask().execute(mBrandID);
            } else {
                downloadScript();
            }
        } else if (mDeviceType == BLConstants.BL_IRCODE_DEVICE_TV) {
            
            // 电视设备只需要查询 Brand 即可，Version 都为 0
            if (mBrandID == null) {
                new QueryIRCodeBrandTask().execute(String.valueOf(mDeviceType));
            }else{

                if (mDownloadUrl == null) {
                    if(mIsMatchTree){
                        new GetMatchTreeTask(mDeviceType, Integer.parseInt(mBrandID)).executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
                    }else{
                        new QueryTvScriptDownloadUrlTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR,mBrandID);
                    }
                }else{
                    downloadScript();
                }

            }

        } else if (mDeviceType == BLConstants.BL_IRCODE_DEVICE_TV_BOX) {
            if(mIsMatchTree){
                if (mBrandID == null) {
                    new QueryTvBoxBrandTask().execute();
                }else{
                    new GetMatchTreeTask(mDeviceType, Integer.parseInt(mBrandID)).executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
                }
            }else{
                new QuerySubAreaTask().execute(mSubAreaId); 
            }
        }
    }
    
    

    class QueryIRCodeBrandTask extends AsyncTask<String, Void, BLResponseResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Querying Brand List...");
        }
        @Override
        protected BLResponseResult doInBackground(String... strings) {
            int type = Integer.parseInt(strings[0]);
            return BLIRCode.requestIRCodeDeviceBrands(type);
        }

        @Override
        protected void onPostExecute(BLResponseResult blBaseBodyResult) {
            super.onPostExecute(blBaseBodyResult);
            dismissProgressDialog();
            
            if (blBaseBodyResult.succeed()) {
                Log.d(BLConstants.BROADLINK_LOG_TAG, blBaseBodyResult.getResponseBody());
                try {
                    JSONObject jInfo = new JSONObject(blBaseBodyResult.getResponseBody());
                    JSONArray jBrands = jInfo.getJSONArray("brand");

                    int brandCount = jBrands.length();
                    mAreas.clear();
                    for (int i = 0; i < brandCount; i++) {
                        JSONObject jBrand = jBrands.optJSONObject(i);
                        BLIRCodeArea area = new BLIRCodeArea();
                        area.setAreaName(jBrand.optString("brand", null));
                        area.setAreaId(String.valueOf(jBrand.optInt("brandid")));
                        mAreas.add(area);
                    }
                    mAdapter.notifyDataSetChanged();

                } catch (Exception e) {

                }
            } else {
                BLCommonUtils.toastErr(blBaseBodyResult);
            }
        }
    }
    
    class QueryTvBoxBrandTask extends AsyncTask<String, Void, CloudAcBrandResponse> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Querying Brand List...");
        }
        @Override
        protected CloudAcBrandResponse doInBackground(String... strings) {
            
            final BLResponseResult result = BLIRCode.requestStbBrands();
            
            CloudAcBrandResponse rmIrTreeResult = new CloudAcBrandResponse();
            rmIrTreeResult.setStatus(result.getStatus());
            rmIrTreeResult.setMsg(result.getMsg());
            
            if(result != null && result.succeed() && result.getResponseBody() != null){
                final CloudAcBrandResponse.RespbodyBean respBody = JSON.parseObject(result.getResponseBody(), CloudAcBrandResponse.RespbodyBean.class);
                if(respBody.getBrand() != null){
                    rmIrTreeResult.setRespbody(respBody);
                    return rmIrTreeResult;
                }
            }
            return rmIrTreeResult;
        }
        
        @Override
        protected void onPostExecute(CloudAcBrandResponse blBaseBodyResult) {
            super.onPostExecute(blBaseBodyResult);
            dismissProgressDialog();

            if (blBaseBodyResult != null && blBaseBodyResult.isSuccess()) {
                    mAreas.clear();
                    for (int i = 0; i < blBaseBodyResult.getBrand().size(); i++) {
                        BLIRCodeArea area = new BLIRCodeArea();
                        area.setAreaName(blBaseBodyResult.getBrand().get(i).getBrand());
                        area.setAreaId(String.valueOf(blBaseBodyResult.getBrand().get(i).getBrandid()));
                        mAreas.add(area);
                    }
                    mAdapter.notifyDataSetChanged();
            } else {
                BLCommonUtils.toastErr(blBaseBodyResult);
            }
        }
    }


    class QueryAcScriptDownloadUrlTask extends AsyncTask<String, Void, BLResponseResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Downloading Script...");
        }
        @Override
        protected BLResponseResult doInBackground(String... strings) {
            int brand = Integer.parseInt(strings[0]);
            return BLIRCode.requestACIRCodeScriptDownloadUrl(brand);
        }

        @Override
        protected void onPostExecute(BLResponseResult blBaseBodyResult) {
            super.onPostExecute(blBaseBodyResult);
            dismissProgressDialog();

            if (blBaseBodyResult.succeed()) {
                Log.d(BLConstants.BROADLINK_LOG_TAG, blBaseBodyResult.getResponseBody());
                try {
                    JSONObject jResult = new JSONObject(blBaseBodyResult.getResponseBody());
                    JSONArray infos = jResult.optJSONArray("downloadinfo");

                    if (infos != null) {
                        int count = infos.length();
                        if (count > 1) {
                            mAreas.clear();
                            for (int i = 0; i < count; i++) {
                                JSONObject jInfo = infos.optJSONObject(i);
                                BLIRCodeArea area = new BLIRCodeArea();
                                area.setAreaName(jInfo.optString("name", null));
                                area.setAreaId(jInfo.optString("fixkey", null));
                                area.setDownloadUrl(jInfo.optString("downloadurl", null));
                                mAreas.add(area);
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            JSONObject info = infos.getJSONObject(0);
                            if (info != null) {
                                mDownloadUrl = info.optString("downloadurl", null);
                                mScriptRandkey = info.optString("fixkey", null);
                                mScriptName = info.optString("name", null);
                                downloadScript();
                            }
                        }
                    }

                } catch (Exception e) {

                }
            }
        }
    }
    
    class QueryTvScriptDownloadUrlTask extends AsyncTask<String, Void, BLResponseResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Downloading Script...");
        }
        @Override
        protected BLResponseResult doInBackground(String... strings) {
            int brand = Integer.parseInt(strings[0]);
            return BLIRCode.requestTVIRCodeScriptDownloadUrl(brand);
        }

        @Override
        protected void onPostExecute(BLResponseResult blBaseBodyResult) {
            super.onPostExecute(blBaseBodyResult);
            dismissProgressDialog();
            
            if (blBaseBodyResult.succeed()) {
                Log.d(BLConstants.BROADLINK_LOG_TAG, blBaseBodyResult.getResponseBody());
                try {
                    JSONObject jResult = new JSONObject(blBaseBodyResult.getResponseBody());
                    JSONArray infos = jResult.optJSONArray("downloadinfo");

                    if (infos != null) {
                        int count = infos.length();
                        if (count > 1) {
                            mAreas.clear();
                            for (int i = 0; i < count; i++) {
                                JSONObject jInfo = infos.optJSONObject(i);
                                BLIRCodeArea area = new BLIRCodeArea();
                                area.setAreaName(jInfo.optString("name", null));
                                area.setAreaId(jInfo.optString("randkey", null));
                                area.setDownloadUrl(jInfo.optString("downloadurl", null));
                                mAreas.add(area);
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            JSONObject info = infos.getJSONObject(0);
                            if (info != null) {
                                mDownloadUrl = info.optString("downloadurl", null);
                                mScriptRandkey = info.optString("randkey", null);
                                mScriptName = info.optString("name", null);

                                downloadScript();
                            }
                        }
                    }

                } catch (Exception e) {

                }
            }
        }
    }

    class QueryStbScriptDownloadUrlTask extends AsyncTask<String, Void, BLResponseResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Downloading Script...");
        }

        @Override
        protected BLResponseResult doInBackground(String... strings) {
            int locateid = Integer.parseInt(strings[0]);
            int providerid = Integer.parseInt(strings[1]);
            return BLIRCode.requestSTBIRCodeScriptDownloadUrl(locateid, providerid, 0);
        }

        @Override
        protected void onPostExecute(BLResponseResult blBaseBodyResult) {
            super.onPostExecute(blBaseBodyResult);
            dismissProgressDialog();
            
            if (blBaseBodyResult.succeed()) {
                Log.d(BLConstants.BROADLINK_LOG_TAG, blBaseBodyResult.getResponseBody());
                try {
                    JSONObject jResult = new JSONObject(blBaseBodyResult.getResponseBody());
                    JSONArray infos = jResult.optJSONArray("downloadinfo");

                    if (infos != null) {
                        int count = infos.length();
                        if (count > 1) {
                            mAreas.clear();
                            for (int i = 0; i < count; i++) {
                                JSONObject jInfo = infos.optJSONObject(i);
                                BLIRCodeArea area = new BLIRCodeArea();
                                area.setAreaName(jInfo.optString("name", null));
                                area.setAreaId(jInfo.optString("randkey", null));
                                area.setDownloadUrl(jInfo.optString("downloadurl", null));
                                mAreas.add(area);
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            JSONObject info = infos.getJSONObject(0);
                            if (info != null) {
                                mDownloadUrl = info.optString("downloadurl", null);
                                mScriptRandkey = info.optString("randkey", null);
                                mScriptName = info.optString("name", null);

                                downloadScript();
                            }
                        }
                    }

                } catch (Exception e) {

                }
            }
        }
    }

    class QuerySubAreaTask extends AsyncTask<String, Void, BLResponseResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Querying sub area...");
        }

        @Override
        protected BLResponseResult doInBackground(String... strings) {
            int locateid = Integer.parseInt(strings[0]);
            return BLIRCode.requestSubAreas(locateid);
        }

        @Override
        protected void onPostExecute(BLResponseResult blBaseBodyResult) {
            super.onPostExecute(blBaseBodyResult);
            dismissProgressDialog();
            
            if (blBaseBodyResult.succeed()) {
                Log.d(BLConstants.BROADLINK_LOG_TAG, blBaseBodyResult.getResponseBody());
                try {
                    JSONObject jInfo = new JSONObject(blBaseBodyResult.getResponseBody());
                    JSONArray jSubareainfos = jInfo.getJSONArray("subareainfo");

                    int count = jSubareainfos.length();
                    mAreas.clear();
                    for (int i = 0; i < count; i++) {
                        JSONObject jSubInfo = jSubareainfos.optJSONObject(i);
                        BLIRCodeArea area = new BLIRCodeArea();
                        area.setAreaName(jSubInfo.optString("name", null));
                        area.setAreaId(String.valueOf(jSubInfo.optInt("locateid")));
                        int leaf = jSubInfo.getInt("isleaf");
                        area.setIsleaf(leaf > 0);
                        mAreas.add(area);
                    }
                    mAdapter.notifyDataSetChanged();
                } catch (Exception e) {

                }
            }
        }
    }
    
    class QuerySubAreaProviderTask extends AsyncTask<String, Void, BLResponseResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Querying sub area providers...");
        }

        @Override
        protected BLResponseResult doInBackground(String... strings) {
            int locateid = Integer.parseInt(strings[0]);
            return BLIRCode.requestSTBProvider(locateid);
        }

        @Override
        protected void onPostExecute(BLResponseResult blBaseBodyResult) {
            super.onPostExecute(blBaseBodyResult);
            dismissProgressDialog();
            
            if (blBaseBodyResult.succeed()) {
                Log.d(BLConstants.BROADLINK_LOG_TAG, blBaseBodyResult.getResponseBody());
                try {
                    JSONObject jInfo = new JSONObject(blBaseBodyResult.getResponseBody());
                    JSONArray jProviderInfos = jInfo.getJSONArray("providerinfo");

                    int count = jProviderInfos.length();
                    mAreas.clear();
                    for (int i = 0; i < count; i++) {
                        JSONObject jProviderInfo = jProviderInfos.optJSONObject(i);
                        BLIRCodeArea area = new BLIRCodeArea();
                        area.setAreaName(jProviderInfo.optString("providername", null));
                        area.setAreaId(String.valueOf(jProviderInfo.optInt("providerid")));
                        mAreas.add(area);
                    }
                    mAdapter.notifyDataSetChanged();
                } catch (Exception e) {

                }
            }
        }
    }

    class DownLoadScriptTask extends AsyncTask<String, Void, BLDownLoadIRCodeResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Downloading...");
        }

        @Override
        protected BLDownLoadIRCodeResult doInBackground(String... strings) {
            return BLIRCode.downloadIRCodeScript(strings[0], strings[1], strings[2]);
        }

        @Override
        protected void onPostExecute(BLDownLoadIRCodeResult blDownloadScriptResult) {
            super.onPostExecute(blDownloadScriptResult);
            dismissProgressDialog();
            if(blDownloadScriptResult!= null && blDownloadScriptResult.succeed() && blDownloadScriptResult.getSavePath()!=null){
                mSavePath = blDownloadScriptResult.getSavePath();
                goToNextActivity();
            }else{
                BLCommonUtils.toastErr(blDownloadScriptResult);
            }
        }
    }
    
    class DevAdapter extends BLBaseRecyclerAdapter<BLIRCodeArea> {

        public DevAdapter() {
            super(mAreas, R.layout.item_family_room);
        }

        @Override
        public void onBindViewHolder(BLBaseViewHolder holder, final int position) {
            super.onBindViewHolder(holder, position);
            holder.setText(R.id.tv_name, mBeans.get(position).getAreaName());
            holder.setText(R.id.tv_mac, JSON.toJSONString(mBeans.get(position), true));
        }
    }
    
    
    class GetMatchTreeTask extends AsyncTask<Void , Void , RmIrTreeResult>{
        private int brandId;
        private int typeId;

        public GetMatchTreeTask(int typeId, int brandId) {
            this.brandId = brandId;
            this.typeId = typeId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Get Match Tree...");
        }

        @Override
        protected RmIrTreeResult doInBackground(Void... voids) {
            final BLResponseResult matchTree = BLIRCode.getMatchTree("1", typeId, brandId);
            if(matchTree != null && matchTree.succeed() && matchTree.getResponseBody() != null){
                final RmIrTreeResult rmIrTreeResult = new RmIrTreeResult();

                final RmIrTreeResult.RespBody respBody = JSON.parseObject(matchTree.getResponseBody(), RmIrTreeResult.RespBody.class);
                if(respBody.getHotircode().getIrcodeid() != null && respBody.getHotircode().getIrcodeid().size() > 0){

                    rmIrTreeResult.setStatus(matchTree.getStatus());
                    rmIrTreeResult.setMsg(matchTree.getMsg());
                    rmIrTreeResult.setRespbody(respBody);
                    return rmIrTreeResult;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(RmIrTreeResult blResponseResult) {
            super.onPostExecute(blResponseResult);
            dismissProgressDialog();
            if(blResponseResult != null && blResponseResult.isSuccess()){
                BLLog.d("match tree", JSON.toJSONString(blResponseResult, true));
                
                gotoNextActivity(blResponseResult);
            }else{
                BLToastUtils.show("Get Match Tree Fail.");
            }
        }
    }

    private void gotoNextActivity(RmIrTreeResult blResponseResult) {
        final Intent intent = new Intent(mActivity, IRCodeMatchTreeMainActivity.class);
        intent.putExtra(BLConstants.INTENT_SERIALIZABLE, blResponseResult);
        intent.putExtra(BLConstants.INTENT_VALUE, mDeviceType);
        startActivity(intent);
    }
}
