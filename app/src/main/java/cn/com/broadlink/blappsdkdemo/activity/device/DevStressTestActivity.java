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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

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
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLFileUtils;
import cn.com.broadlink.blappsdkdemo.common.BLStorageUtils;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.data.BLStressTestCmdBean;
import cn.com.broadlink.blappsdkdemo.data.BLStressTestResultBean;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.InputTextView;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseRecyclerAdapter;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseViewHolder;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.divideritemdecoration.BLDividerUtil;
import cn.com.broadlink.sdk.BLLet;
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
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dev_stress_test);
        setBackWhiteVisible();
        setTitle("Stress Test");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 保持屏幕常亮
        
        mDNADevice = getIntent().getParcelableExtra(BLConstants.INTENT_PARCELABLE);
        
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
                        
                        final StringBuilder sb = new StringBuilder();
                        List<int[]> result = new ArrayList<>();
                        
                        sb.append("Recycle Total ").append(mCycleCount).append(", now ").append(mOutSideIndex).append("\n\n");
                        
                        for (int j = 0; j <mResultList.size(); j++) {

                            final List<BLStressTestResultBean> cycleItemResult = mResultList.get(j);
                            
                            for (int i = 0; i <cycleItemResult.size(); i++) {
                                if(result.size()<=i){
                                    result.add(new int[]{0,0});
                                }
                                final int totalCount = cycleItemResult.get(i).totalCount;
                                final int failCount = cycleItemResult.get(i).failCount;
                                result.get(i)[0] += totalCount;
                                result.get(i)[1] += failCount;
                            }
                        }

                        for (int i = 0; i < mCmdList.size(); i++) {
                            if(result.size()<=i)break;
                            
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

        mLogFilePath = BLStorageUtils.STRESS_TEST_LOG_PATH + File.separator + mDNADevice.getDid()+".log";

        mTestRunnable = new TestRunnable();
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
                showAddCmdDialog();
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
    
    private void showAddCmdDialog(){
        final LinearLayout dialog = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_dev_stess_test_cmd, null);
        final InputTextView etCmd = dialog.findViewById(R.id.et_cmd);
        final InputTextView etData = dialog.findViewById(R.id.et_data);
        final InputTextView etCount = dialog.findViewById(R.id.et_send_count);
        final InputTextView etInterval = dialog.findViewById(R.id.et_interval);
        final InputTextView etDelay = dialog.findViewById(R.id.et_delay);
        
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
                    
                    mCmdList.add(new BLStressTestCmdBean(cmdStr, data, intervalInt, delayInt, countInt));
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
        public void onBindViewHolder(BLBaseViewHolder holder, final int position) {
            super.onBindViewHolder(holder, position);
            holder.setText(R.id.tv_cmd, JSON.toJSONString(mBeans.get(position), true));
            holder.get(R.id.bt_del).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void doOnClick(View v) {
                    mBeans.remove(position);
                    notifyDataSetChanged();
                }
            });
        }
    }
    
    class TestRunnable implements Runnable {

        @Override
        public void run() {
            initTestFlags();
            final String did = TextUtils.isEmpty(mDNADevice.getpDid()) ? mDNADevice.getDid() : mDNADevice.getpDid();
            final String sDid = TextUtils.isEmpty(mDNADevice.getpDid()) ? null : mDNADevice.getDid();

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
                        final String result = BLLet.Controller.dnaControl(did, sDid, blStressTestCmdBean.data, blStressTestCmdBean.cmd, null);
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
