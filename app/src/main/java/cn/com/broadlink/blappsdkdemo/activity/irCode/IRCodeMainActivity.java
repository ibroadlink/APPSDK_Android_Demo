package cn.com.broadlink.blappsdkdemo.activity.irCode;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.view.BLListAlert;

public class IRCodeMainActivity extends TitleActivity {

    private Button mBtAir;
    private Button mBtTv;
    private Button mBtBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ir_code_main);
        setBackWhiteVisible();
        setTitle("IR Code Main");
        
        findView();
        
        setListener();
    }

    private void findView() {
        mBtAir = (Button) findViewById(R.id.bt_air);
        mBtTv = (Button) findViewById(R.id.bt_tv);
        mBtBox = (Button) findViewById(R.id.bt_box);
    }
    private void setListener() {

        mBtAir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BLListAlert.showAlert(mActivity, "Choose the way to add air condition", new String[]{"One Key Recognize", "By Brand"}, new BLListAlert.OnItemClickLister() {
                    @Override
                    public void onClick(int whichButton) {
                        switch (whichButton){
                            case 0:
                                BLCommonUtils.toActivity(mActivity, IRCodeAcPairActivity.class);
                                break;
                                
                            case 1:
                                BLCommonUtils.toActivity(mActivity, IRCodeBrandListActivity.class,  BLConstants.BL_IRCODE_DEVICE_AC);
                                break;

                        }
                    }
                });
            }
        });

        mBtTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BLCommonUtils.toActivity(mActivity, IRCodeBrandListActivity.class,  BLConstants.BL_IRCODE_DEVICE_TV);
            }
        });

        mBtBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BLCommonUtils.toActivity(mActivity, IRCodeBrandListActivity.class,  BLConstants.BL_IRCODE_DEVICE_TV_BOX);
            }
        });

    }


}
