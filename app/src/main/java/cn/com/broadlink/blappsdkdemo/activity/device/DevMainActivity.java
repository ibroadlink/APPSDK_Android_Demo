package cn.com.broadlink.blappsdkdemo.activity.device;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;

public class DevMainActivity extends TitleActivity {

    private Button mConfigBtn;
    private Button mProbeBtn;
    private Button mMyDeviceBtn;
    private Button mBtStressTest;
    private Button mBtHttpStressTest;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.Device_Control_Main);
        setContentView(R.layout.activity_dev_main);
        setBackWhiteVisible();

        findView();
        
        setListener();
    }

    private void findView() {
        mConfigBtn = (Button) findViewById(R.id.btn_config_device);
        mProbeBtn = (Button) findViewById(R.id.btn_probe_device);
        mMyDeviceBtn = (Button) findViewById(R.id.btn_my_device);
        mBtStressTest = (Button) findViewById(R.id.bt_stress_test);
        mBtHttpStressTest = (Button) findViewById(R.id.bt_stress_test_http);
    }

    private void setListener(){

        mConfigBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(DevMainActivity.this, DevConfigActivity.class);
                startActivity(intent);
            }
        });

        mProbeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(DevMainActivity.this, DevProbeListActivity.class);
                startActivity(intent);
            }
        });

        mMyDeviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(DevMainActivity.this, DevMyDevListActivity.class);
                startActivity(intent);
            }
        });
        
        mBtStressTest.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                BLCommonUtils.toActivity(mActivity, DevStressTestActivity.class);
            }
        });
        
        mBtHttpStressTest.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                BLCommonUtils.toActivity(mActivity, HttpStressTestActivity.class);
            }
        });

    }

}
