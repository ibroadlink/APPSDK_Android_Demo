package cn.com.broadlink.blappsdkdemo.activity.check;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.device.DevMyDevListActivity;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;

public class NetworkCheckActivity extends TitleActivity {

    private Button mCheckSDKBtn, mCheckDeviceBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_check);

        setTitle(R.string.Network_Check);
        setBackWhiteVisible();

        findView();
        setListener();
    }

    private void findView() {
        mCheckSDKBtn = (Button) findViewById(R.id.btn_check_sdk);
        mCheckDeviceBtn = (Button) findViewById(R.id.btn_check_device);
    }

    private void setListener() {

        mCheckSDKBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(NetworkCheckActivity.this, SDKCheckActivity.class);
                startActivity(intent);
            }
        });

        mCheckDeviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("INTENT_IS_CHECK", 1);
                intent.setClass(NetworkCheckActivity.this, DevMyDevListActivity.class);
                startActivity(intent);
            }
        });
    }

}
