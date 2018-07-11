package cn.com.broadlink.blappsdkdemo.activity.IRCode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.result.controller.BLIRCodeDataResult;

public class IRCodeTCOperateActivity extends Activity {

    private TextView mTvTitleView, mTVIrcodeView;
    private ListView mTvFunctionListView;

    private List<String> mFunctionList = new ArrayList<>();
    private ArrayAdapter<String> mAdapter;
    private String mScriptPath, mScriptInfo;
    private int mDeviceType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ircode_tcoperate);

        findView();
        setListener();

        Intent intent = getIntent();
        if (intent != null) {
            mScriptPath = getIntent().getStringExtra("ScriptPath");
            mScriptInfo = getIntent().getStringExtra("ScriptInfo");
            mDeviceType = getIntent().getIntExtra("DeviceType", BLConstants.BL_IRCODE_DEVICE_TV);

            analysisScriptInfo(mScriptInfo);
        }

    }

    private void findView() {
        mTvTitleView = (TextView) findViewById(R.id.tv_title);
        mTVIrcodeView = (TextView) findViewById(R.id.tv_ircode_data_view);
        mTvFunctionListView = (ListView) findViewById(R.id.tv_function_list);

        mAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mFunctionList);
        mTvFunctionListView.setAdapter(mAdapter);
    }

    private void setListener() {
        mTvFunctionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String func = mFunctionList.get(position);
                BLIRCodeDataResult result = BLLet.IRCode.queryTVIRCodeData(mScriptPath, mDeviceType, func);
                if (result.succeed()) {
                    mTVIrcodeView.setText(result.getIrcode());
                } else {
                    BLCommonUtils.toastShow(IRCodeTCOperateActivity.this, "getIRCodeScriptBaseInfo failed! Error: " + String.valueOf(result.getStatus()) + " Msg:" + result.getMsg());
                }
            }
        });
    }

    private void analysisScriptInfo(String infomation) {
        Log.d(BLConstants.BROADLINK_LOG_TAG, infomation);
        String[] arrayStr = new String[]{};
        arrayStr = infomation.split(",");

        mFunctionList.clear();
        for (int i = 0; i < arrayStr.length; i++) {
            String fun = arrayStr[i];
            fun = fun.replace("[", "").replace("\"", "").replace("]", "");
            mFunctionList.add(fun);
        }
        mAdapter.notifyDataSetChanged();
    }
}
