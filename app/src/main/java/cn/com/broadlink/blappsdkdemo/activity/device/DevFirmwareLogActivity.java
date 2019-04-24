package cn.com.broadlink.blappsdkdemo.activity.device;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLLog;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;

/**
 * Desc
 *
 * @author JiangYaqiang
 * 2019/4/24 10:27
 */
public class DevFirmwareLogActivity extends TitleActivity {

    private TextView mTvIp;
    private Button mBtClear;
    private Switch mSwtEnable;
    private EditText mEtData;
    private BLDNADevice mDNADevice;
    private final int mPort = 9876;
    private String mIp;
    private volatile boolean mShouldStopThread = false;
    private static Handler mHandler;
    private volatile StringBuffer mSb;
    private UdpReceiveThread mLogThread;
    private FwLogParam mLogParam;
    private final String CMD_FW_LOG = "device_log_redirect";
    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 保持屏幕常亮
        setContentView(R.layout.activity_dev_firmware_log);
        setBackWhiteVisible();
        setTitle("Firmware Log");
        mDNADevice = getIntent().getParcelableExtra(BLConstants.INTENT_PARCELABLE);
        
        initData();
        
        findView();

        initView();
      
        setListener();

        startLogThread();
    }

    private void findView() {
        mTvIp = (TextView) findViewById(R.id.tv_ip);
        mBtClear = (Button) findViewById(R.id.bt_clear);
        mSwtEnable = (Switch) findViewById(R.id.swt_enable);
        mEtData = (EditText) findViewById(R.id.et_data);
    }

    @SuppressLint("DefaultLocale")
    private void initView() {
        mTvIp.setText(String.format("%s:%d", mIp, mPort));
    }

    @SuppressLint("HandlerLeak")
    private void initData() {
        mIp = BLCommonUtils.getWifiIp(mActivity);
        if (mIp == null) {
            BLAlert.showAlert(mActivity, null, "Please connect wifi first!", new OnSingleClickListener() {
                @Override
                public void doOnClick(View v) {
                    back();
                }
            });
            return;
        }
        mLogParam = new FwLogParam(1, mIp, mPort);
        
        mSb = new StringBuffer(1024 * 1024);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(!DevFirmwareLogActivity.this.isFinishing() && mEtData != null){
                    mEtData.setText(mSb.toString());
                }
            }
        };
    }

    private void setListener() {
        mBtClear.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                mSb = new StringBuffer();
                mHandler.sendEmptyMessage(0);
            }
        });
        
        mSwtEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startLogThread();
                }else{
                    stopLogThread();
                }
            }
        });
    }


    private void startLogThread() {
        if (datagramSocket != null) {
            datagramSocket.disconnect();
            datagramSocket.close();
        }
        
        mShouldStopThread = false;
        mSb = new StringBuffer();
    
        mLogThread = new UdpReceiveThread();
        mLogThread.start();

        new Thread() {
            @Override
            public void run() {
                super.run();
                final String[] didOrSubDid = BLCommonUtils.parseDidOrSubDid(mDNADevice);
                mLogParam.enable = 1;
                final String result = BLLet.Controller.dnaControl(didOrSubDid[0], didOrSubDid[1], JSON.toJSONString(mLogParam), CMD_FW_LOG, null);
                final BLBaseResult blBaseResult = JSON.parseObject(result, BLBaseResult.class);
                if(blBaseResult == null || !blBaseResult.succeed()){
                    mSb.append(result);
                    mHandler.sendEmptyMessage(0);
                }
            }
        }.start();
    }

    private void stopLogThread() {
        mShouldStopThread = true;
        
        if (datagramSocket != null) {
            datagramSocket.disconnect();
            datagramSocket.close();
        }
        
        if (mLogThread != null) {
            mLogThread = null;
        }

        new Thread() {
            @Override
            public void run() {
                super.run();
                final String[] didOrSubDid = BLCommonUtils.parseDidOrSubDid(mDNADevice);
                mLogParam.enable = 0;
                final String result = BLLet.Controller.dnaControl(didOrSubDid[0], didOrSubDid[1], JSON.toJSONString(mLogParam), CMD_FW_LOG, null);
                final BLBaseResult blBaseResult = JSON.parseObject(result, BLBaseResult.class);
                if(blBaseResult == null || !blBaseResult.succeed()){
                    mSb.append(result);
                    mHandler.sendEmptyMessage(0);
                }
            }
        }.start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLogThread();
    }

    /* 接收tcp连接 */
    private class UdpReceiveThread extends Thread {
   
        byte[] message = new byte[1024];

        @Override
        public void run() {

            try {
                datagramSocket = new DatagramSocket(mPort);
                datagramSocket.setBroadcast(true);
                datagramPacket = new DatagramPacket(message, message.length);
                BLLog.d("fw_log", "thread in!");
                
                while (!mShouldStopThread) {
                    datagramSocket.receive(datagramPacket);
                    String strMsg = new String(datagramPacket.getData()).trim();
                    datagramPacket.setLength(message.length);

                    mSb.append(strMsg);
                    mHandler.sendEmptyMessage(0);
                }

            } catch (IOException e1) {
                BLLog.w("fw_log", e1.getMessage());
            } finally {
                BLLog.d("fw_log", "thread out!");
                if (datagramSocket != null) {
                    datagramSocket.disconnect();
                    datagramSocket.close();
                }
            }
        }
    }

    private static class FwLogParam {
        public int enable = 1;
        public String ip;
        public int port;

        public FwLogParam(int enable, String ip, int port) {
            this.enable = enable;
            this.ip = ip;
            this.port = port;
        }
    }
}
