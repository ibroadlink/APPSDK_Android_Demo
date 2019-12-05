package cn.com.broadlink.blappsdkdemo.activity.space;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.com.broadlink.base.BLBaseHttpAccessor;
import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.base.BLCommonTools;
import cn.com.broadlink.base.BLTrustManagerV2;
import cn.com.broadlink.base.fastjson.JSONObject;
import cn.com.broadlink.base.fastjson.TypeReference;
import cn.com.broadlink.base.fastjson.parser.Feature;
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.activity.device.DevMainMenuActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.data.space.DataSpaceInfo;
import cn.com.broadlink.blappsdkdemo.service.BLLocalFamilyManager;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseRecyclerAdapter;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseViewHolder;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.divideritemdecoration.BLDividerUtil;
import cn.com.broadlink.blsfamily.bean.BLSBaseDataResult;
import cn.com.broadlink.blsfamily.bean.endpoint.BLSEndpointInfo;
import cn.com.broadlink.blsfamily.bean.endpoint.BLSEndpointListData;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;

public class SpaceManageActivity extends TitleActivity {

    private EditText mEtResult; 
    private RecyclerView mRvContent;
    private List<BLSEndpointInfo> mEndpointList = new ArrayList<>();
    private MyAdapter mAdapter;
    private BLDNADevice mDNADevice;
    private BLBaseResult mResultWhenFail = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space);
        setBackWhiteVisible();
        setTitle("Space Management");
        
        initView();
        setListener();

        querySpaceDevList();
    }

    private void setListener() {
        setRightButtonOnClickListener("Retry", getResources().getColor(R.color.bl_yellow_main_color), new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                querySpaceDevList();
            }
        });

        mAdapter.setOnItemClickListener(new BLBaseRecyclerAdapter.OnClickListener() {
            @Override
            public void onClick(int position, int viewType) {
                toEndpointView(mEndpointList.get(position));
            }
        });

    }

    private void initView() {
        mEtResult = (EditText) findViewById(R.id.et_result);
        mRvContent = (RecyclerView) findViewById(R.id.rv_content);

        mAdapter = new MyAdapter();
        mRvContent.setLayoutManager(new LinearLayoutManager(mActivity));
        mRvContent.setAdapter(mAdapter);
        mRvContent.addItemDecoration(BLDividerUtil.getDefault(mActivity, mEndpointList));
    }

    private void showResult(final EditText view, final Object result, final boolean append) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("\nHH:mm:ss ");
                final String date = sdf.format(new Date());

                if (append) {
                    view.append(date);
                } else {
                    view.setText(date);
                }

                if (result == null) {
                    view.append("Return null");
                } else {
                    if (result instanceof String) {
                        view.append((String) result);
                    } else {
                        view.append(JSON.toJSONString(result, true));
                    }
                }
            }
        });
    }


    private void toEndpointView(BLSEndpointInfo info) {
        if(info==null) return;
        
        // 如果是子设备，先将其网关添加到sdk
        BLDNADevice gatewayDev = null;
        if(!TextUtils.isEmpty(info.getGatewayId())){
            for (BLSEndpointInfo item : mEndpointList) {
                if(info.getGatewayId().equalsIgnoreCase(item.getEndpointId())){
                    gatewayDev = item.toDnadeviceInfo();
                    BLLet.Controller.addDevice(gatewayDev);
                    break;
                }
            }
        }
        
        mDNADevice = info.toDnadeviceInfo();
        BLLet.Controller.addDevice(mDNADevice);
        String pid = mDNADevice.getPid();

        final String gatewayPid = gatewayDev == null ? null : gatewayDev.getPid();
        if (scriptFileExist(pid) && (gatewayPid == null || scriptFileExist(gatewayPid))) {
            gotoDevMainMenu();
        }else{
            new DownLoadResTask(pid, gatewayPid).executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
        }
    }

    private void gotoDevMainMenu() {
        Intent intent = new Intent();
        intent.putExtra(BLConstants.INTENT_DEVICE, mDNADevice);
        intent.setClass(mActivity, DevMainMenuActivity.class);
        startActivity(intent);
    }

    private Map<String, String> generateHead(Map<String, String> head) {
        Map<String, String> mapHead = new HashMap();
        mapHead.put("userid", BLApplication.mBLUserInfoUnits.getUserid());
        mapHead.put("loginsession", BLApplication.mBLUserInfoUnits.getLoginsession());
        mapHead.put("licenseid", BLLet.getLicenseId());
        mapHead.put("language", BLCommonTools.getLanguage());
        long nowTimeInterval = System.currentTimeMillis();
        String msgId = BLApplication.mBLUserInfoUnits.getUserid() + "-" + nowTimeInterval;
        mapHead.put("messageId", msgId);
        if (head != null && !head.isEmpty()) {
            Iterator var6 = head.keySet().iterator();
            while(var6.hasNext()) {
                String k = (String)var6.next();
                mapHead.put(k, head.get(k));
            }
        }
        return mapHead;
    }

    public BLSBaseDataResult<BLSEndpointListData> queryEndpointList(String spaceId) {
        BLSBaseDataResult<BLSEndpointListData> result = new BLSBaseDataResult();
        String finalUrl = BLConstants.SPACE_URL.QUE_DEV();
        JSONObject body = new JSONObject();

        Map<String, String> head = new HashMap();
//        head.put("familyId", BLLocalFamilyManager.getInstance().getCurrentFamilyId());
        head.put("companyId", BLLet.getCompanyid());
        head.put("spaceOpenId", spaceId);
        
        Map<String, String> headMap = this.generateHead(head);
        String ret = BLBaseHttpAccessor.post(finalUrl, headMap, body.toString().getBytes(), 10*1000, BLTrustManagerV2.getInstance());
        if (ret != null) {
            result = JSONObject.parseObject(ret, new TypeReference<BLSBaseDataResult<BLSEndpointListData>>() {});
            return result;
        } else {
            result.setError(-3001);
            result.setMsg("cloud return null");
            return result;
        }
    }

    
    public List<DataSpaceInfo.SpaceBean.SpaceInfoBean> querySpaceTree(String spaceId,  List<DataSpaceInfo.SpaceBean.SpaceInfoBean> list) {
        if (list == null) {
            list = new ArrayList<>();
        }

        String finalUrl = BLConstants.SPACE_URL.QUE();
        JSONObject body = new JSONObject();
        body.put("spaceOpenId", spaceId);

        Map<String, String> head = new HashMap();
        head.put("familyId", BLLocalFamilyManager.getInstance().getCurrentFamilyId());
        head.put("companyId", BLLet.getCompanyid());
        Map<String, String> headMap = this.generateHead(head);
        String retStr = BLBaseHttpAccessor.post(finalUrl, headMap, body.toString().getBytes(), 10 * 1000, BLTrustManagerV2.getInstance());
        
        if (TextUtils.isEmpty(retStr)) {
            mResultWhenFail = new BLBaseResult();
            mResultWhenFail.setError(-1);
            mResultWhenFail.setMsg("Query space return null");
            return list;
        }
            
        final BLSBaseDataResult<DataSpaceInfo> ret = JSONObject.parseObject(retStr, new TypeReference<BLSBaseDataResult<DataSpaceInfo>>() {}, new Feature[0]);
        if (ret == null || !ret.succeed() || ret.getData() == null) {
            mResultWhenFail = ret;
            return list;
        }

        if (spaceId == null && ret.getData().topSpace != null && ret.getData().topSpace.size() > 0) { // 顶层查询
            for (DataSpaceInfo.SpaceBean spaceBean : ret.getData().topSpace) {
                list.add(spaceBean.spaceInfo);
                querySpaceTree(spaceBean.spaceInfo.spaceOpenId, list);
            }
        } else { // 底层查询
            if (ret.getData().subSpace != null) {
                for (DataSpaceInfo.SpaceBean item : ret.getData().subSpace) {
                    querySpaceTree(item.spaceInfo.spaceOpenId, list);
                }
            } else if(ret.getData().space != null){ // 递归的终止条件是，没有树叉
                list.add(ret.getData().space.spaceInfo);
            }
        }
        return list;
    }
    
    
    private void querySpaceDevList(){
        new QuerySpaceTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
    }

    class QuerySpaceTask extends AsyncTask<String, Void, List<BLSEndpointInfo>> {
        
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Query space");
            mResultWhenFail = null;
        }

        @Override
        protected List<BLSEndpointInfo> doInBackground(String... params) {
            List<BLSEndpointInfo> retList = new ArrayList<>();
            final List<DataSpaceInfo.SpaceBean.SpaceInfoBean> spaceInfoList = querySpaceTree(null, null);
            if(spaceInfoList != null){
                for (DataSpaceInfo.SpaceBean.SpaceInfoBean item : spaceInfoList) {
                    final BLSBaseDataResult<BLSEndpointListData> devList = queryEndpointList(item.spaceOpenId);
                    if(devList == null || !devList.succeed()){
                        mResultWhenFail = devList;
                        return retList;
                    }
                    if(devList.getData().getEndpoints() != null){
                        retList.addAll(devList.getData().getEndpoints());
                    }
                }
            }
            return retList;
        }
        
        @Override
        protected void onPostExecute(final List<BLSEndpointInfo> result) {
            super.onPostExecute(result);
            dismissProgressDialog();
            if (mResultWhenFail == null) {
                //showResult(mEtResult, result, false);
                showResult(mEtResult, "Success", false);
                mEndpointList.clear();
                mEndpointList.addAll(result);
                mAdapter.notifyDataSetChanged();
            } else {
                showResult(mEtResult, mResultWhenFail, false);
            }
        }
    }

    
    private boolean scriptFileExist(String pid){
        String scriptFilePath = BLLet.Controller.queryScriptPath(pid);
        Log.e("scriptFileExist" , scriptFilePath);
        File file = new File(scriptFilePath);
        return file.exists();
    }
    
    //脚本下载
    class DownLoadResTask extends AsyncTask<String, Void, BLBaseResult> {
        private ProgressDialog progressDialog;
        private String pid;
        private String gatewayPid;

        public DownLoadResTask(String pid, String gatewayPid) {
            this.pid = pid;
            this.gatewayPid = gatewayPid;
        }

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

            if (pid != null &&!scriptFileExist(pid)){
                result = BLLet.Controller.downloadScript(pid);
                if(result==null || !result.succeed()){
                    return result;
                }
            }

            if (gatewayPid != null && !scriptFileExist(gatewayPid)) {
                result = BLLet.Controller.downloadScript(gatewayPid);
            }
            
            return result;
        }

        @Override
        protected void onPostExecute(BLBaseResult result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            BLCommonUtils.toastErr(result);

            if (scriptFileExist(pid) && (gatewayPid == null || scriptFileExist(gatewayPid))) {
                gotoDevMainMenu();
            }
        }
    }
    
    
    class MyAdapter extends BLBaseRecyclerAdapter<BLSEndpointInfo> {

        public MyAdapter() {
            super(mEndpointList, R.layout.item_family_room);
        }

        @Override
        public void onBindViewHolder(BLBaseViewHolder holder, final int position) {
            super.onBindViewHolder(holder, position);
            holder.setText(R.id.tv_name,  getItem(position).getFriendlyName());
            holder.setText(R.id.tv_mac, String.format("did: %s\ngateway: %S", getItem(position).getEndpointId(), getItem(position).getGatewayId()));
        }
    }
}
