package cn.com.broadlink.blappsdkdemo.activity.device;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.base.BLDateUtils;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLFileUtils;
import cn.com.broadlink.blappsdkdemo.common.BLStorageUtils;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.data.BLStressTestCmdBean;
import cn.com.broadlink.blappsdkdemo.data.BLStressTestResultBean;
import cn.com.broadlink.blappsdkdemo.service.BLLocalDeviceManager;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.BLListAlert;
import cn.com.broadlink.blappsdkdemo.view.InputTextView;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseRecyclerAdapter;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseViewHolder;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.divideritemdecoration.BLDividerUtil;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.constants.controller.BLDevCmdConstants;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;

/**
 * 压力测试
 *
 * @author JiangYaqiang
 * 2019/4/2 10:42
 */
public class DevStressTestActivity extends TitleActivity {

    private EditText mEtResult;
    private Button mBtStart;
    private Button mBtStop;
    private Button mBtAddItem;
    private Button mBtExport;
    private RecyclerView mRvContent;
    private BLDNADevice mDNADevice;
    private List<BLStressTestCmdBean> mCmdList = new ArrayList<>();
    private List<List<BLStressTestResultBean>> mResultList = new ArrayList<>();
    private CmdAdapter mAdapter;
    private Handler mHandler;
    private String mLogFilePath;
    private TestRunnable mTestRunnable;
    private Thread mTestThread;
    
    /** 停止测试 **/
    private volatile boolean mShouldStop = false;
    /** 进行到第几个命令 **/
    private int mInSideIndex = 0;
    /** 循环第几次 **/
    private int mOutSideIndex = 0;
    /** 循环次数 **/
    private long mCycleCount = 0;
    
    /** sdk支持的所有指令 **/
    private List<String> mDnaCmdList = new ArrayList<>();
    /** sdk支持指令的例程 **/
    private List<String> mDnaCmdDefaultList = new ArrayList<>();
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 保持屏幕常亮
        setContentView(R.layout.activity_dev_stress_test);
        setBackWhiteVisible();
        setTitle("Stress Test");
        
        initData();

        findView();

        initView();

        setListener();
    }

    private void findView() {
        mEtResult = (EditText) findViewById(R.id.et_result);
        mBtStart = (Button) findViewById(R.id.bt_start);
        mBtStop = (Button) findViewById(R.id.bt_stop);
        mBtAddItem = (Button) findViewById(R.id.bt_add_item);
        mBtExport = (Button) findViewById(R.id.bt_export);
        mRvContent = (RecyclerView) findViewById(R.id.rv_content);
    }

    @SuppressLint("HandlerLeak")
    private void initData() {
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        
                        final StringBuilder sb = new StringBuilder(1000);
                        
                        // 缓存每个命令执行的结果，result[0] - 执行总数， result[1] - 执行失败次数
                        List<int[]> result = new ArrayList<>();
                        
                        sb.append("Recycle Total ").append(mCycleCount).append(", now ").append(mOutSideIndex).append("\n\n");
                        
                        for (int j = 0; j <mResultList.size(); j++) {

                            final List<BLStressTestResultBean> cycleItemResult = mResultList.get(j);
                            
                            for (int i = 0; i <cycleItemResult.size(); i++) {
                                
                                // 初始化
                                if (i >= result.size()) {
                                    result.add(new int[]{0,0});
                                }
                                
                                final int totalCount = cycleItemResult.get(i).totalCount;
                                final int failCount = cycleItemResult.get(i).failCount;
                                result.get(i)[0] += totalCount;
                                result.get(i)[1] += failCount;
                            }
                        }

                        for (int i = 0; i < mCmdList.size(); i++) {
                            
                            //还未执行的命令，不显示统计结果
                            if (i >= result.size()) break;
                            
                            final int totalCount =  result.get(i)[0];
                            final int failCount =  result.get(i)[1];
                            final String accuracy = new DecimalFormat("#.##%").format((float) (totalCount - failCount) / (float) totalCount);

                            sb.append("Cmd").append(i).append(": \n");
                            sb.append("total ").append(mCmdList.get(i).sendCount * mCycleCount);
                            sb.append(", already run ").append(totalCount);
                            sb.append(", fail ").append(failCount);
                            sb.append(", accuracy rate ").append(accuracy);
                            sb.append("\n\n");
                        }
                        
                        sb.append("\n").append("Logfile path: " + mLogFilePath);

                        mEtResult.setText(sb.toString());
                        break;
                        
                    case 1:
                        stopTest();
                        break;     
                }
                
               
            }
        };

        mLogFilePath = BLStorageUtils.STRESS_TEST_LOG_PATH + File.separator + BLDateUtils.formatDate(System.currentTimeMillis()) +".log";

        mTestRunnable = new TestRunnable();

        initDnaCmd();
    }

    private void initDnaCmd() {
        mDnaCmdList.add(BLDevCmdConstants.DEV_CTRL);
        mDnaCmdDefaultList.add(getString(R.string.str_data));

        mDnaCmdList.add(BLDevCmdConstants.DEV_PASSTHROUGH);
        mDnaCmdDefaultList.add("");

        mDnaCmdList.add(BLDevCmdConstants.DEV_ONLINE);
        mDnaCmdDefaultList.add("");

        //mDnaCmdList.add(BLDevCmdConstants.DEV_DATA);
        //mDnaCmdDefaultList.add("");

        mDnaCmdList.add(BLDevCmdConstants.DEV_INFO);
        mDnaCmdDefaultList.add(getString(R.string.str_cmd_devinfo));

        mDnaCmdList.add(BLDevCmdConstants.DEV_FW_VERSION);
        mDnaCmdDefaultList.add("");

        mDnaCmdList.add(BLDevCmdConstants.DEV_FW_UPGRADE);
        mDnaCmdDefaultList.add(getString(R.string.str_cmd_fw_upgrade));

        mDnaCmdList.add(BLDevCmdConstants.DEV_SERVICE_TIMER);
        mDnaCmdDefaultList.add("");

        //mDnaCmdList.add(BLDevCmdConstants.DEV_GET_CONNECT_SERVICE_INFO);
        //mDnaCmdDefaultList.add("");

        mDnaCmdList.add(BLDevCmdConstants.DEV_FASTCON_NO_CONFIG);
        mDnaCmdDefaultList.add(getString(R.string.str_cmd_fastcon));

        mDnaCmdList.add(BLDevCmdConstants.DEV_NEWSUBDEV_SCAN_START);
        mDnaCmdDefaultList.add(getString(R.string.str_cmd_new_sub_scan));

        mDnaCmdList.add(BLDevCmdConstants.DEV_NEWSUBDEV_SCAN_STOP);
        mDnaCmdDefaultList.add("");

        mDnaCmdList.add(BLDevCmdConstants.DEV_NEWSUBDEV_LIST);
        mDnaCmdDefaultList.add(getString(R.string.str_cmd_new_sub_list));

        mDnaCmdList.add(BLDevCmdConstants.DEV_SUBDEV_ADD);
        mDnaCmdDefaultList.add(getString(R.string.str_cmd_sub_add));

        mDnaCmdList.add(BLDevCmdConstants.DEV_SUBDEV_ADD_RESULT);
        mDnaCmdDefaultList.add(getString(R.string.str_cmd_sub_del));

        mDnaCmdList.add(BLDevCmdConstants.DEV_SUBDEV_LIST);
        mDnaCmdDefaultList.add(getString(R.string.str_cmd_new_sub_list));

        mDnaCmdList.add(BLDevCmdConstants.DEV_SUBDEV_DEL);
        mDnaCmdDefaultList.add(getString(R.string.str_cmd_sub_del));

        mDnaCmdList.add(BLDevCmdConstants.DEV_SUBDEV_BUCKUP);
        mDnaCmdDefaultList.add(getString(R.string.str_cmd_new_sub_list));

        mDnaCmdList.add(BLDevCmdConstants.DEV_SUBDEV_RESTORE);
        mDnaCmdDefaultList.add("");

        mDnaCmdList.add(BLDevCmdConstants.DEV_SUBDEV_UPGRADE);
        mDnaCmdDefaultList.add(getString(R.string.str_cmd_sub_fw_upgrade));

        mDnaCmdList.add(BLDevCmdConstants.DEV_SUBDEV_MODIFY);
        mDnaCmdDefaultList.add(getString(R.string.str_cmd_sub_mdf));

        mDnaCmdList.add(BLDevCmdConstants.DEV_SUBDEV_TIMER);
        mDnaCmdDefaultList.add("");

        mDnaCmdList.add(BLDevCmdConstants.DEV_SUBDEV_VERSION);
        mDnaCmdDefaultList.add(getString(R.string.str_cmd_sub_del));

        mDnaCmdList.add(BLDevCmdConstants.DEV_TASKLIST);
        mDnaCmdDefaultList.add("");

        mDnaCmdList.add(BLDevCmdConstants.DEV_TASKADD);
        mDnaCmdDefaultList.add("");

        mDnaCmdList.add(BLDevCmdConstants.DEV_TASKDEL);
        mDnaCmdDefaultList.add("");

        mDnaCmdList.add(BLDevCmdConstants.DEV_TASKDATA);
        mDnaCmdDefaultList.add("");
    }

    private void initView() {
        mAdapter = new CmdAdapter();
        mRvContent.setLayoutManager(new LinearLayoutManager(mActivity));
        mRvContent.setAdapter(mAdapter);
        mRvContent.addItemDecoration(BLDividerUtil.getDefault(mActivity, mCmdList));
    }

    private void setListener() {
        mBtAddItem.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                selectDevice();
            }
        });
        
        mBtStart.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                startTest();
            }
        });
        
        mBtStop.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                stopTest();
            }
        });
        
        mBtExport.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                BLCommonUtils.openFile(mActivity, new File(mLogFilePath));
            }
        });
    }
    
    private void selectDevice(){
        final List<BLDNADevice> devicesAddInSDK = BLLocalDeviceManager.getInstance().getDevicesAddInSDK();
        if (devicesAddInSDK != null && !devicesAddInSDK.isEmpty()) {
            final String[] dids = new String[devicesAddInSDK.size()];
            for (int i = 0; i < devicesAddInSDK.size(); i++) {
                dids[i] = devicesAddInSDK.get(i).getName() + "\n" + devicesAddInSDK.get(i).getDid();
            }
            BLListAlert.showAlert(mActivity, "Select a device", dids, new BLListAlert.OnItemClickLister() {
                @Override
                public void onClick(int whichButton) {
                    mDNADevice = devicesAddInSDK.get(whichButton);
                    showAddCmdDialog();
                }
            });
        } else {
            BLToastUtils.show("Devices added to sdk is empty, add one first!");
        }
    }
    
    private void showAddCmdDialog(){
        final LinearLayout dialog = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_dev_stess_test_cmd, null);
        final InputTextView etCmd = dialog.findViewById(R.id.et_cmd);
        final InputTextView etData = dialog.findViewById(R.id.et_data);
        final InputTextView etCount = dialog.findViewById(R.id.et_send_count);
        final InputTextView etInterval = dialog.findViewById(R.id.et_interval);
        final InputTextView etDelay = dialog.findViewById(R.id.et_delay);
        final Spinner mSpCmd = dialog.findViewById(R.id.sp_cmd);

        final String[] cmds = mDnaCmdList.toArray(new String[mDnaCmdList.size()]);
        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, cmds);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpCmd.setAdapter(spinnerAdapter);
        mSpCmd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                etCmd.setText(cmds[position]);
                etData.setText(mDnaCmdDefaultList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                etCmd.setText(cmds[0]);
                etData.setText(mDnaCmdDefaultList.get(0));
            }
        });
        
        BLAlert.showCustomViewDilog(mActivity, dialog, "Ok", "Cancel", new BLAlert.DialogOnClickListener() {
            @Override
            public void onPositiveClick() {
                final String cmd = etCmd.getTextString();
                final String data = etData.getTextString();
                final String count = etCount.getTextString();
                final String interval = etInterval.getTextString();
                final String delay = etDelay.getTextString();

                if (data == null) {
                    BLToastUtils.show("Data string can not be empty!");
                    return;
                }

                try {
                    String cmdStr = !TextUtils.isEmpty(cmd) ?  cmd : "dev_ctrl";
                    int countInt = !TextUtils.isEmpty(count) ?  Integer.parseInt(count) : 1;
                    int intervalInt = !TextUtils.isEmpty(interval) ?  Integer.parseInt(interval) : 1000;
                    int delayInt = !TextUtils.isEmpty(delay) ?  Integer.parseInt(delay) : 1000;

                    final String[] dids = BLCommonUtils.parseDidOrSubDid(mDNADevice);

                    mCmdList.add(new BLStressTestCmdBean(dids[0], dids[1], cmdStr, data, intervalInt, delayInt, countInt));
                    mAdapter.notifyDataSetChanged();
                    
                } catch (NumberFormatException e) {
                    BLToastUtils.show("cmd param invalid!");
                    e.printStackTrace();
                }
            }

            @Override
            public void onNegativeClick() {

            }
        });
    }
    
    private void startTest(){
        if(mCmdList==null || mCmdList.size()==0){
            BLToastUtils.show("Add cmd first!");
            return;  
        }
        
        if(mTestThread != null){
            BLToastUtils.show("Test already started!");
            return;
        }
        
        if(new File(mLogFilePath).exists()){
            BLAlert.showDialog(mActivity, null, "Old log file will be deleted, please confirm", new BLAlert.DialogOnClickListener() {
                @Override
                public void onPositiveClick() {
                    doStartTest();
                }

                @Override
                public void onNegativeClick() {

                }
            });
        }else{
            doStartTest();
        }
    }

    private void doStartTest() {
        BLAlert.showEditDilog(mActivity, "Recycle how many times? ", "1", new BLAlert.BLEditDialogOnClickListener() {
            @Override
            public void onClink(String value) {
                try {
                    mCycleCount = Long.parseLong(value);

                    mBtStart.setEnabled(false);
                    mBtStop.setEnabled(true);
                    mTestThread = new Thread(mTestRunnable);
                    mTestThread.start();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    BLToastUtils.show("Param invalid or too big!");
                }
            }

            @Override
            public void onClinkCacel(String value) {

            }
        }, true);
        

    }

    private void stopTest(){
        mShouldStop = true;
        mTestThread = null;
        mBtStart.setEnabled(true);
        mBtStop.setEnabled(false);
    }
    
    private void initTestFlags(){
        mShouldStop = false;
        mInSideIndex = 0;
        mOutSideIndex = 0;
        mResultList.clear();
        BLFileUtils.deleteFile(new File(mLogFilePath));
    }

    class CmdAdapter extends BLBaseRecyclerAdapter<BLStressTestCmdBean> {

        public CmdAdapter() {
            super(mCmdList, R.layout.item_dev_stress_test_cmd);
        }

        @Override
        public void onBindViewHolder(final BLBaseViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);
            holder.setText(R.id.tv_cmd, JSON.toJSONString(mBeans.get(position), true));
            holder.get(R.id.bt_del).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void doOnClick(View v) {
                    mBeans.remove(holder.getAdapterPosition());
                    notifyDataSetChanged();
                }
            });
        }
    }
    
    class TestRunnable implements Runnable {

        @Override
        public void run() {
            initTestFlags();

            for (int k = 0; k < mCycleCount; k++) {
                if (k != 0) {
                    SystemClock.sleep(1000);
                }
                
                mOutSideIndex = k + 1;
                final ArrayList<BLStressTestResultBean> cycleItemResult = new ArrayList<>();
                mResultList.add(cycleItemResult);

                for (int i = 0; i < mCmdList.size(); i++) {
                    if (mShouldStop) return;

                    mInSideIndex = i;
                    cycleItemResult.add(new BLStressTestResultBean(mInSideIndex));
                    final BLStressTestCmdBean blStressTestCmdBean = mCmdList.get(i);

                    if (i != 0) {
                        SystemClock.sleep(blStressTestCmdBean.delay);
                    }

                    final StringBuffer sb = new StringBuffer(BLDateUtils.formatDate(System.currentTimeMillis()));
                    sb.append(": ");
                    sb.append("Recycle Total ").append(mCycleCount).append(", now ").append(mOutSideIndex).append("\n");

                    BLFileUtils.writeFile(mLogFilePath, sb.toString(), true);

                    for (int j = 0; j < blStressTestCmdBean.sendCount; j++) {
                        if (mShouldStop) return;

                        if (j != 0) {
                            SystemClock.sleep(blStressTestCmdBean.interval);
                        }

                        cycleItemResult.get(i).totalCount = j + 1;
                        final String result = BLLet.Controller.dnaControl(blStressTestCmdBean.did, blStressTestCmdBean.sDid, blStressTestCmdBean.data, blStressTestCmdBean.cmd, null);
                        if (mShouldStop) return;

                        final BLBaseResult blBaseResult = JSON.parseObject(result, BLBaseResult.class);
                        if (blBaseResult == null || !blBaseResult.succeed()) {
                            cycleItemResult.get(i).failCount++;
                        }

                        // 通知界面刷新
                        mHandler.sendEmptyMessage(0);

                        if (mShouldStop) return;

                        final StringBuilder sb2 = new StringBuilder(BLDateUtils.formatDate(System.currentTimeMillis()));
                        sb2.append(": \n");
                        sb2.append("cmd: ").append(blStressTestCmdBean.cmd).append("\n");
                        sb2.append("data: ").append(blStressTestCmdBean.data).append("\n");
                        sb2.append("result: ").append(result).append("\n");

                        BLFileUtils.writeFile(mLogFilePath, sb2.toString(), true);

                    }

                    if (mShouldStop) return;
                }
            }

            // 测试自然结束 
            mHandler.sendEmptyMessage(1);
        }
    }
}
