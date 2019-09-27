package cn.com.broadlink.blappsdkdemo.activity.device;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.com.broadlink.base.BLBaseHttpAccessor;
import cn.com.broadlink.base.BLTrustManagerV2;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLLog;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;

/**
 * 压力测试
 *
 * @author JiangYaqiang
 * 2019/4/2 10:42
 */
public class HttpStressTestActivity extends TitleActivity {

    private EditText mEtResult;
    private EditText mEtUrl;
    private EditText mEtTimeout;
    private EditText mEtInterval;
    private Button mBtStart;
    private Button mBtStop;
    private Handler mHandler;
    private TestRunnable mTestRunnable;
    private ExecutorService mCachedThreadPool;
    private Thread mTestThread;
    private int mIntervalMs = 1000;
    private int mTimeoutMs = 3000;
    private String mUrl = null;
    private volatile int mTestCnt = 0;
    private volatile int mTestSuccCnt = 0;
    
    /** 停止测试 **/
    private volatile boolean mShouldStop = false;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 保持屏幕常亮
        setContentView(R.layout.activity_http_stress_test);
        setBackWhiteVisible();
        setTitle("Stress Test");
        
        initData();

        findView();

        initView();

        setListener();
    }

    private void findView() {
        mEtResult = (EditText) findViewById(R.id.et_result);
        mEtUrl = (EditText) findViewById(R.id.et_url);
        mEtTimeout = (EditText) findViewById(R.id.et_timeout);
        mEtInterval = (EditText) findViewById(R.id.et_interval);
        mBtStart = (Button) findViewById(R.id.bt_start);
        mBtStop = (Button) findViewById(R.id.bt_stop);
    }

    @SuppressLint("HandlerLeak")
    private void initData() {
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0: // 刷新界面显示
                        handleDescRefresh();
                        break;
                }
            }
        };

        mTestRunnable = new TestRunnable();
    }

    /**
     * 刷新界面显示
     */
    private void handleDescRefresh() {
        mEtResult.setText(calcDescStr().toString());
    }

    private StringBuilder calcDescStr() {
        final StringBuilder sb = new StringBuilder(500);
        sb.append("total ").append(mTestCnt);
        sb.append(", success ").append(mTestSuccCnt );
        return sb;
    }
    

    private void initView() {
        
    }

    private void setListener() {
        
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
    }
    
    private void startTest(){
        if(TextUtils.isEmpty(mEtUrl.getText())){
            BLToastUtils.show("Add url first!");
            return;  
        }
        
        if(mTestThread != null){
            BLToastUtils.show("Test already started!");
            return;
        }

        mUrl = mEtUrl.getText().toString();
        mTimeoutMs = TextUtils.isEmpty(mEtTimeout.getText()) ? 3000 : Integer.parseInt(mEtTimeout.getText().toString());
        mIntervalMs = TextUtils.isEmpty(mEtInterval.getText()) ? 1000 : Integer.parseInt(mEtInterval.getText().toString());

        mBtStart.setEnabled(false);
        mBtStop.setEnabled(true);
        mEtUrl.setEnabled(false);
        mEtInterval.setEnabled(false);
        mEtTimeout.setEnabled(false);
        mTestThread = new Thread(mTestRunnable);
        mTestThread.start();
    }

    private void stopTest(){
        mShouldStop = true;
        mTestThread = null;
        mBtStart.setEnabled(true);
        mBtStop.setEnabled(false);
        mEtUrl.setEnabled(true);
        mEtInterval.setEnabled(true);
        mEtTimeout.setEnabled(true);
        mCachedThreadPool.shutdown();
    }
    
    private void initTestFlags(){
        mShouldStop = false;
        mTestCnt = 0;
        mTestSuccCnt = 0;
        mCachedThreadPool = Executors.newCachedThreadPool();
    }
    
    class TestRunnable implements Runnable {
        
        final Runnable testRunnable = new Runnable() {
            @Override
            public void run() {
                if (mShouldStop) return;
                ++mTestCnt;
                final String result = BLBaseHttpAccessor.get(mUrl, null, null, mTimeoutMs, BLTrustManagerV2.getInstance());

                BLLog.d("HttpStress", "result -> " + result);
                
                if (result != null) {
                    JSONObject jResult = null;
                    try {
                        jResult = new JSONObject(result);
                        int statue = jResult.optInt("error");
                        if (statue == 0) {
                            ++mTestSuccCnt;
                        }
                    } catch (JSONException e) {
                        //e.printStackTrace();
                        ++mTestSuccCnt;
                    }
                }

                if (mShouldStop) return;
                mHandler.sendEmptyMessage(0);
            }
        };
        
        
        @Override
        public void run() {
            
            initTestFlags();

           while (!mShouldStop){
                mCachedThreadPool.execute(testRunnable);
               SystemClock.sleep(mIntervalMs);
            }
        }
    }
}
