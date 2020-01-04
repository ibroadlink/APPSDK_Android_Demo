package cn.com.broadlink.blappsdkdemo.activity.device;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLFileUtils;
import cn.com.broadlink.blappsdkdemo.common.BLMultDidUtils;
import cn.com.broadlink.blappsdkdemo.common.BLStorageUtils;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.db.dao.BLDeviceInfoDao;
import cn.com.broadlink.blappsdkdemo.db.data.BLDeviceInfo;
import cn.com.broadlink.blappsdkdemo.service.BLLocalDeviceManager;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.BLListAlert;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseRecyclerAdapter;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseViewHolder;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.divideritemdecoration.BLDividerUtil;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;
import cn.com.broadlink.sdk.param.controller.BLSubDevRestoreParam;
import cn.com.broadlink.sdk.result.controller.BLPairResult;
import cn.com.broadlink.sdk.result.controller.BLSubDevAddResult;
import cn.com.broadlink.sdk.result.controller.BLSubDevBackupResult;
import cn.com.broadlink.sdk.result.controller.BLSubDevListResult;
import cn.com.broadlink.sdk.result.controller.BLSubDevRestoreResult;
import cn.com.broadlink.sdk.result.controller.BLSubSevBackupInfo;
import cn.com.broadlink.sdk.result.controller.BLSubdevResult;

public class DevGatewayManageActivity extends TitleActivity {

    private EditText mTvResult;
    private Button mBtScanStart;
    private Button mBtScanStop;
    private Button mBtGetScanNewList;
    private Button mBtGetList;
    private Button mBtQueryAddResult;
    private Button mBtSubdevBackup;
    private Button mBtSubdevRestore;
    private RecyclerView mRvList;
    private EditText mEtPid;
    
    private BLDNADevice mDNADevice;
    private List<BLDNADevice> mSubDeviceList = new ArrayList<>();
    private SubDevAdapter mAdapter;
    private boolean mIsNewSubListType = true;
    private String mPid = null;
    private int mSelectedIndex = -1;
    private final int FRAME_SIZE = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_gateway_manage);
        setBackWhiteVisible();
        setTitle("Gateway Management");
        
        mDNADevice = getIntent().getParcelableExtra(BLConstants.INTENT_PARCELABLE);
        
        findView();

        initView();

        setListener();
    }

    private boolean checkPid(){
        final boolean ret = !TextUtils.isEmpty(mEtPid.getText());
        
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!ret){
                        BLToastUtils.show("Input the pid to add first!");
                        mEtPid.requestFocus();
                    }else{
                        if(mEtPid.getText().length()==32){
                            mPid = mEtPid.getText().toString();
                        }else{
                            mPid = BLCommonUtils.deviceType2Pid(mEtPid.getText().toString());
                        }
                    }
                    Log.d("mPid", JSON.toJSONString(mPid, true));
                }
            });
        return ret;
    }
    
    private void setListener() {
        mBtScanStart.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                if (checkPid())new StartScanTask().execute();
            }
        });

        mBtScanStop.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                if (checkPid())new StopScanTask().execute();
            }
        });

        mBtGetScanNewList.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                if (checkPid()) new GetNewSubDevsTask().execute();
            }
        });

        mBtGetList.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                new GetAddedSubDevsTask().execute();
            }
        });

        mBtQueryAddResult.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                if(mSelectedIndex >=0 && mSelectedIndex < mSubDeviceList.size()){
                    new QueryAddResult().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR, mSelectedIndex);
                }else{
                    final String msg = "Scan Sub Devices List, Click One Of Them To Add, Then You Can Query The Add Result.";
                    BLToastUtils.show(msg);
                }
            }
        });

        mAdapter.setOnItemClickListener(new BLBaseRecyclerAdapter.OnClickListener() {
            @Override
            public void onClick(final int position, int viewType) {
                if(mIsNewSubListType){
                    if (checkPid()) {
                        mSelectedIndex = position;
                        new AddSubDevTask().execute(position);
                    }
                }else{
                    BLAlert.showDialog(mActivity, "Add this subdevice 2 devlist?", new BLAlert.DialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {
                            add2DbAndGotoNextActivity(mSubDeviceList.get(position), true);
                        }

                        @Override
                        public void onNegativeClick() {
                            add2DbAndGotoNextActivity(mSubDeviceList.get(position), false);
                        }
                    });
                }
            }
        });

        mAdapter.setOnItemLongClickListener(new BLBaseRecyclerAdapter.OnLongClickListener() {
            @Override
            public boolean onLongClick(final int position, int viewType) {
                if(!mIsNewSubListType){
                    BLAlert.showDialog(mActivity, "Confirm to delete this sub device?", new BLAlert.DialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {
                            new DelSubDevTask().execute(position);
                        }

                        @Override
                        public void onNegativeClick() {

                        }
                    });
                    return true;
                }
                
                return false;
            }
        });
        
        mBtSubdevBackup.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                new SubDevBackupTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR,  0);
            }
        });
        
        mBtSubdevRestore.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                final ArrayList<String> backupDidList = BLCommonUtils.readFileNameList(BLStorageUtils.SUB_DEV_BACKUP_PATH);
                if (backupDidList.size() == 0) {
                    BLToastUtils.show("No backup file found!");
                    return;
                }
                
                String[] items = backupDidList.toArray(new String[backupDidList.size()]);
                BLListAlert.showAlert(mActivity, "Select the did to restore. File in /sdcard/broadlink/blTool/SharedData/SubdevBackup", items, new BLListAlert.OnItemClickLister() {
                    @Override
                    public void onClick(int whichButton) {
                        new SubDevRestoreTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR, backupDidList.get(whichButton));
                    }
                });
            }
            
        });
    }

    private void initView() {
        mAdapter = new SubDevAdapter();
        mRvList.setLayoutManager(new LinearLayoutManager(mActivity));
        mRvList.setAdapter(mAdapter);
        mRvList.addItemDecoration(BLDividerUtil.getDefault(mActivity, mSubDeviceList));
    }


    /**
     * 将子设备添加到数据库,然后跳转到下个页面
     */
    private void add2DbAndGotoNextActivity(BLDNADevice subDevInfo, boolean add2DB){
        if(add2DB){
            //subDevInfo.setOwnerId(BLLocalFamilyManager.getInstance().getCurrentFamilyId());
            
            if(subDevInfo.getMac() == null){
                String mac = subDevInfo.getDid().substring(20, 32);
                subDevInfo.setMac(BLCommonUtils.formatMac(mac));
            }

            try {
                BLDeviceInfoDao blDeviceInfoDao = new BLDeviceInfoDao(getHelper());
                BLDeviceInfo deviceInfo = new BLDeviceInfo(subDevInfo);
                List<BLDeviceInfo> list = new ArrayList<>();
                list.add(deviceInfo);
                blDeviceInfoDao.insertData(list);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            BLLocalDeviceManager.getInstance().addDeviceIntoSDK(subDevInfo);  
        }
        BLCommonUtils.toActivity(mActivity, DevDnaStdControlActivity.class, subDevInfo);
    }
    
    
    private void showResult(Object result) {
        if(result == null){
            mTvResult.setText("Return null");
        }else{
            if(result instanceof String){
                mTvResult.setText((String)result);
            }else{
                mTvResult.setText(JSON.toJSONString(result, true));
            }
        }
    }
    
    private void findView() {
        mTvResult = (EditText) findViewById(R.id.et_result);
        mBtScanStart = (Button) findViewById(R.id.bt_scan_start);
        mBtScanStop = (Button) findViewById(R.id.bt_scan_stop);
        mBtQueryAddResult = (Button) findViewById(R.id.bt_query_add_result);
        mBtGetScanNewList = (Button) findViewById(R.id.bt_get_scan_new_list);
        mBtGetList = (Button) findViewById(R.id.bt_get_list);
        mBtSubdevBackup = (Button) findViewById(R.id.bt_subdev_backup);
        mBtSubdevRestore = (Button) findViewById(R.id.bt_subdev_restore);
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
        mEtPid = (EditText) findViewById(R.id.et_pid);
    }

    class SubDevAdapter extends BLBaseRecyclerAdapter<BLDNADevice> {

        public SubDevAdapter() {
            super(mSubDeviceList, R.layout.item_family_room);
        }

        @Override
        public void onBindViewHolder(BLBaseViewHolder holder, final int position) {
            super.onBindViewHolder(holder, position);
            holder.setText(R.id.tv_name, "Pid: " + mBeans.get(position).getPid());
            holder.setText(R.id.tv_mac, "Did: " + mBeans.get(position).getDid());
        }
    }

    /** 开始扫描子设备 **/
    private class StartScanTask extends AsyncTask<String, Void, BLBaseResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          showProgressDialog("Scan...");
        }

        @Override
        protected BLBaseResult doInBackground(String... params) {
            return BLLet.Controller.subDevScanStart(mDNADevice.getDeviceId(), mPid);
        }

        @Override
        protected void onPostExecute(BLBaseResult blBaseResult) {
            super.onPostExecute(blBaseResult);
            dismissProgressDialog();
            showResult(blBaseResult);
        }
    }
    
    /** 停止扫描子设备 **/
    private class StopScanTask extends AsyncTask<String, Void, BLBaseResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          showProgressDialog("Stop Scan...");
        }

        @Override
        protected BLBaseResult doInBackground(String... params) {
            return BLLet.Controller.subDevScanStop(mDNADevice.getDeviceId());
        }

        @Override
        protected void onPostExecute(BLBaseResult blBaseResult) {
            super.onPostExecute(blBaseResult);
            dismissProgressDialog();
            showResult(blBaseResult);
        }
    }
    
    /** 查询扫描到的待添加设备 **/
    private class GetNewSubDevsTask extends AsyncTask<String, Void, BLSubDevListResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          showProgressDialog("Get Scanned List...");
        }

        @Override
        protected BLSubDevListResult doInBackground(String... params) {
            return BLLet.Controller.devSubDevNewListQuery(mDNADevice.getDeviceId(), mPid,0, 5);
        }

        @Override
        protected void onPostExecute(BLSubDevListResult blBaseResult) {
            super.onPostExecute(blBaseResult);
            dismissProgressDialog();
            showResult(blBaseResult);

            if (blBaseResult != null && blBaseResult.succeed() && blBaseResult.getData() != null) {
                mIsNewSubListType = true;
                mSubDeviceList.clear();
                mSubDeviceList.addAll(blBaseResult.getData().getList());
                
                mAdapter.notifyDataSetChanged();
            }
        }
    }
    
    /** 查询已添加的子设备列表 **/
    private class GetAddedSubDevsTask extends AsyncTask<String, Void, BLSubDevListResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          showProgressDialog("Get Added List...");
        }

        @Override
        protected BLSubDevListResult doInBackground(String... params) {

            int index = 0;
            final int QUERY_MAX_COUNT = 5;
            final String gatewayDid = mDNADevice.getDeviceId();
            BLSubDevListResult result = BLLet.Controller.devSubDevListQuery(gatewayDid, index, QUERY_MAX_COUNT);

            if (result != null && result.succeed() && result.getData() != null && result.getData().getList() != null && !result.getData().getList().isEmpty()) {
                int total = result.getData().getTotal();
                int currListSize = result.getData().getList().size();
                if(currListSize < total){
                    while (true){
                        index = result.getData().getList().size();
                        BLSubDevListResult queryResult = null;
                        //循环最多查询3次，成功之后跳出循环
                        for (int i = 0; i < 3; i++) {
                            queryResult = BLLet.Controller.devSubDevListQuery(gatewayDid, index, QUERY_MAX_COUNT);
                            if (queryResult != null && queryResult.succeed()) {
                                break;
                            }
                        }

                        if (queryResult != null && queryResult.succeed()) {
                            if (queryResult.getData() != null && queryResult.getData().getList() != null && !result.getData().getList().isEmpty()) {
                                result.getData().getList().addAll(queryResult.getData().getList());
                                result.getData().setIndex(queryResult.getData().getIndex());

                                if(result.getData().getList().size() >= total){
                                    return result;
                                }
                            }else{
                                return queryResult;
                            }
                        }else{
                            return queryResult;
                        }
                    }
                }
            }
            return result;

        }

        @Override
        protected void onPostExecute(BLSubDevListResult blBaseResult) {
            super.onPostExecute(blBaseResult);
            dismissProgressDialog();
            showResult(blBaseResult);

            if (blBaseResult != null && blBaseResult.succeed()) {
                mIsNewSubListType = false;
                mSubDeviceList.clear();
                
                if(blBaseResult.getData() != null){
                    for (BLDNADevice dev  : blBaseResult.getData().getList() ) {
                        dev.setpDid(mDNADevice.getDid());
                        mSubDeviceList.add(dev);
                        BLLet.Controller.addDevice(dev);
                    } 
                }
              
                mAdapter.notifyDataSetChanged();
            }
        }
    }
    
    /** 添加子设备 **/
    private class AddSubDevTask extends AsyncTask<Integer, Void, BLBaseResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          showProgressDialog("Add...");
        }

        @Override
        protected BLBaseResult doInBackground(Integer... params) {

            final BLDNADevice subDevInfo = mSubDeviceList.get(params[0]);
            //subDevInfo.setOwnerId(BLLocalFamilyManager.getInstance().getCurrentFamilyId());
            final BLSubdevResult blSubdevResult = BLLet.Controller.subDevAdd(mDNADevice.getDeviceId(), subDevInfo);
            if (blSubdevResult != null && blSubdevResult.succeed()) {
                if(subDevInfo.getMac() != null){
                    
                    BLPairResult pairResult = BLLet.Controller.pair(subDevInfo);
                    if (pairResult.succeed()) {
                        subDevInfo.setId(pairResult.getId());
                        subDevInfo.setKey(pairResult.getKey());
                    }
                    
                    try {
                        BLDeviceInfoDao blDeviceInfoDao = new BLDeviceInfoDao(getHelper());
                        BLDeviceInfo deviceInfo = new BLDeviceInfo(subDevInfo);
                        List<BLDeviceInfo> list = new ArrayList<>();
                        list.add(deviceInfo);
                        blDeviceInfoDao.insertData(list);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    BLLocalDeviceManager.getInstance().addDeviceIntoSDK(subDevInfo);
                   
                }else{
                    try {
                        String mac = subDevInfo.getDid().substring(20, 32);
                        subDevInfo.setMac(BLCommonUtils.formatMac(mac));
                        BLDeviceInfoDao blDeviceInfoDao = new BLDeviceInfoDao(getHelper());
                        BLDeviceInfo deviceInfo = new BLDeviceInfo(subDevInfo);
                        List<BLDeviceInfo> list = new ArrayList<>();
                        list.add(deviceInfo);
                        blDeviceInfoDao.insertData(list);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    BLLocalDeviceManager.getInstance().addDeviceIntoSDK(subDevInfo);
                }
               
            }
            return blSubdevResult;
        }

        @Override
        protected void onPostExecute(BLBaseResult blBaseResult) {
            super.onPostExecute(blBaseResult);
            dismissProgressDialog();
            showResult(blBaseResult);
        }
    }
    
    /** 删除子设备 **/
    private class DelSubDevTask extends AsyncTask<Integer, Void, BLBaseResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          showProgressDialog("Delete...");
        }

        @Override
        protected BLBaseResult doInBackground(Integer... params) {
            return BLMultDidUtils.subDevDel(mDNADevice.getDid(), mSubDeviceList.get(params[0]).getDid());
        }

        @Override
        protected void onPostExecute(BLBaseResult blBaseResult) {
            super.onPostExecute(blBaseResult);
            dismissProgressDialog();
            showResult(blBaseResult);
            if(blBaseResult != null && blBaseResult.succeed()){
                new GetAddedSubDevsTask().execute();
            }
        }
    }

    /** 查询添加结果（固件中异步去下载脚本，所以要主动轮询） **/
    private class QueryAddResult extends AsyncTask<Integer, Void, BLSubDevAddResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Query Add Result...");
        }

        @Override
        protected BLSubDevAddResult doInBackground(Integer... params) {
            BLSubDevAddResult downloadResult = new BLSubDevAddResult();
            
            for (int i = 0; i < 10; i++) {
                SystemClock.sleep(1000);
                downloadResult = BLLet.Controller.devSubDevAddResultQuery(mDNADevice.getDeviceId(), mSubDeviceList.get(params[0]).getDid());
                if(downloadResult != null && downloadResult.succeed() && downloadResult.getSubdevStatus() == 0 && downloadResult.getDownload_status() == 0){
                    BLLet.Controller.subDevScanStop(mDNADevice.getDeviceId());

                    return downloadResult;
                }
            }
            return downloadResult;
        }

        @Override
        protected void onPostExecute(BLSubDevAddResult downloadResult) {
            super.onPostExecute(downloadResult);
            dismissProgressDialog();
            showResult(downloadResult);
        }
    }

    /** 备份子设备列表 **/
    private class SubDevBackupTask extends AsyncTask<Integer, Void, BLSubDevBackupResult> {
        
        String fileName = BLStorageUtils.SUB_DEV_BACKUP_PATH + File.separator + mDNADevice.getDid();
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Backup Sub Devices...");
        }

        @Override
        protected BLSubDevBackupResult doInBackground(Integer... params) {
            final ArrayList<BLSubSevBackupInfo> blSubSevBackupInfos = new ArrayList<>();
            
            int index = params[0];
            final BLSubDevBackupResult blSubDevBackupResult = exportSubList(blSubSevBackupInfos, index);
            if(blSubDevBackupResult !=null && blSubDevBackupResult.succeed()){
                
                blSubDevBackupResult.getList().clear();
                blSubDevBackupResult.getList().addAll(blSubSevBackupInfos);
                
                BLFileUtils.saveStringToFile(JSON.toJSONString(blSubSevBackupInfos, true), fileName);
            }

            return blSubDevBackupResult;
        }

        private BLSubDevBackupResult exportSubList(ArrayList<BLSubSevBackupInfo> blSubSevBackupInfos, int index) {
            final BLSubDevBackupResult blSubDevBackupResult = BLLet.Controller.subDevBackup(mDNADevice.getDid(), FRAME_SIZE, index);
            if(blSubDevBackupResult !=null && blSubDevBackupResult.succeed()){
                if(blSubDevBackupResult.getList() != null){
                    
                    final int size = blSubDevBackupResult.getList().size();
                    final int total = blSubDevBackupResult.getTotal();
                    blSubSevBackupInfos.addAll(blSubDevBackupResult.getList());
                    
                    if(index + size < total && size==FRAME_SIZE){
                        exportSubList(blSubSevBackupInfos, index + size);
                    }
                }
            }
            return blSubDevBackupResult;
        }

        @Override
        protected void onPostExecute(BLSubDevBackupResult result) {
            super.onPostExecute(result);
            dismissProgressDialog();

            if(result !=null && result.succeed()){
                BLToastUtils.show("Backup file: " + fileName);
            }
            
            showResult(result);
        }
    }

    /** 还原自设备列表 **/
    private class SubDevRestoreTask extends AsyncTask<String, Void, BLSubDevRestoreResult> {

        String fileName = null;
        int msgid = new Random().nextInt(99999);
        int index = 0;
        int total = 0;
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Restore Sub Devices...");
        }

        @Override
        protected BLSubDevRestoreResult doInBackground(String... params) {

            String did = params[0];
            fileName = BLStorageUtils.SUB_DEV_BACKUP_PATH + File.separator + did;
            final String data = BLFileUtils.readTextFileContent(fileName);

            final List<BLSubSevBackupInfo> subList = JSON.parseArray(data, BLSubSevBackupInfo.class);
            if (subList != null) {
                total = subList.size();
                return importSubList(subList, 0, total);
            }

            return null;
        }

        private BLSubDevRestoreResult importSubList(List<BLSubSevBackupInfo> blSubSevBackupInfos, int index, int total) {
            final BLSubDevRestoreParam blSubDevRestoreParam = new BLSubDevRestoreParam();
            blSubDevRestoreParam.msgid = msgid;
            blSubDevRestoreParam.index = index;
            blSubDevRestoreParam.total = total;

            final ArrayList<BLSubSevBackupInfo> realSendList = new ArrayList<>();
            int realSendCnt = 0;
            
            if (index + FRAME_SIZE < total) {
                realSendCnt = FRAME_SIZE;
            }else{
                realSendCnt = total - index;
            }
            
            for (int i = 0; i < realSendCnt; i++) {
                realSendList.add(blSubSevBackupInfos.get(index + i));
            }
            
            blSubDevRestoreParam.list.addAll(realSendList);
            
            final BLSubDevRestoreResult blSubDevBackupResult = BLLet.Controller.subDevRestore(mDNADevice.getDid(), blSubDevRestoreParam);
            if(blSubDevBackupResult !=null && blSubDevBackupResult.succeed()){
                if(realSendCnt + index < total){
                    importSubList(blSubSevBackupInfos, realSendCnt + index, total);
                }
            }
            return blSubDevBackupResult;
        }

        @Override
        protected void onPostExecute(BLSubDevRestoreResult result) {
            super.onPostExecute(result);
            dismissProgressDialog();
            if(result == null){
                showResult("restore failed, please check file: " + fileName);
            }else{
                showResult(result);
            }
        }
    }
    
    
}
