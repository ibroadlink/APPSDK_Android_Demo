package cn.com.broadlink.blappsdkdemo.activity.Device;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import cn.com.broadlink.blappsdkdemo.R;

public class DeviceMainActivity extends Activity {

    private Button mConfigBtn, mProbeBtn, mMyDeviceBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_main);

        findView();
        setListener();
    }

    private void findView() {
        mConfigBtn = (Button) findViewById(R.id.btn_config_device);
        mProbeBtn = (Button) findViewById(R.id.btn_probe_device);
        mMyDeviceBtn = (Button) findViewById(R.id.btn_my_device);
    }

    private void setListener(){

        mConfigBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(DeviceMainActivity.this, DevConfigActivity.class);
                startActivity(intent);
            }
        });

        mProbeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(DeviceMainActivity.this, DevListActivity.class);
                startActivity(intent);
            }
        });

        mMyDeviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(DeviceMainActivity.this, MyDeviceListActivity.class);
                startActivity(intent);
            }
        });

    }

}
