package cn.com.broadlink.blappsdkdemo.activity.IRCode;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.com.broadlink.base.BLCommonTools;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blirdaconlib.BLIrdaConState;
import cn.com.broadlink.ircode.BLIRCode;
import cn.com.broadlink.ircode.result.BLIrdaConDataResult;
import cn.com.broadlink.sdk.data.controller.BLRMCloudAcConstants;


public class IRCodeACOperateActivity extends Activity {

    private String mScriptPath;

    private EditText mStatusEdit, mDirEdit, mModeEdit, mSpeedEdit, mTempEdit;
    private TextView mIRCodeAcTitleView ,mIRCodeDataView;

    private String name;
    private int min_temperature;
    private int max_temperature;
    private int status_count;
    private int[] status = new int[2];
    private String statusString;
    private int mode_count;
    private int[] mode = new int[10];
    private String modeString;
    private int windspeed_count;
    private int[] windspeed = new int[10];
    private String windspeedString;
    private int windirect_count;
    private int[] windirect = new int[10];
    private String windirectString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ircode_acoperate);

        mScriptPath = getIntent().getStringExtra("ScriptPath");
        String scriptInfo = getIntent().getStringExtra("ScriptInfo");
        analysisScriptInfo(scriptInfo);

        findView();
    }

    private void findView() {
        mStatusEdit = (EditText) findViewById(R.id.ac_status_edit);
        mDirEdit = (EditText) findViewById(R.id.ac_dir_edit);
        mModeEdit = (EditText) findViewById(R.id.ac_mode_edit);
        mSpeedEdit = (EditText) findViewById(R.id.ac_speed_edit);
        mTempEdit = (EditText) findViewById(R.id.ac_temp_edit);

        mIRCodeDataView = (TextView) findViewById(R.id.ac_ircode_view);
        mIRCodeAcTitleView = (TextView) findViewById(R.id.ac_title);

        mIRCodeAcTitleView.setText(name);
        mStatusEdit.setHint("status range: " + statusString);
        mModeEdit.setHint("mode range: " + modeString);
        mDirEdit.setHint("wind direction range: " + windirectString);
        mSpeedEdit.setHint("wind speed range: " + windspeedString);
        mTempEdit.setHint("temperature range: " + String.valueOf(min_temperature) + " ~ " + String.valueOf(max_temperature));
    }

    private void analysisScriptInfo(String infomation) {
        try {
            JSONObject jInfo = new JSONObject(infomation);
            name = jInfo.optString("name", null);
            min_temperature= jInfo.optInt("temp_min");
            max_temperature = jInfo.optInt("temp_max");

            JSONArray statusArray = jInfo.getJSONArray("status");
            status_count = statusArray.length();
            for (int i = 0; i < statusArray.length(); i++) {
                status[i] = statusArray.getInt(i);
                if (i == 0) {
                    statusString = "[ " + String.valueOf(status[i]) ;
                } else {
                    statusString = statusString + ", "+ String.valueOf(status[i]) ;
                }
            }
            statusString = statusString + " ]";

            JSONArray modeArray = jInfo.getJSONArray("mode");
            mode_count = modeArray.length();
            for (int i = 0; i < modeArray.length(); i++) {
                mode[i] = modeArray.getInt(i);
                if (i == 0) {
                    modeString = "[ " + String.valueOf(mode[i]) ;
                } else {
                    modeString = modeString + ", "+ String.valueOf(mode[i]) ;
                }
            }
            modeString = modeString + " ]";

            JSONArray speedArray = jInfo.getJSONArray("speed");
            windspeed_count = speedArray.length();
            for (int i = 0; i < speedArray.length(); i++) {
                windspeed[i] = speedArray.getInt(i);
                if (i == 0) {
                    windspeedString = "[ " + String.valueOf(windspeed[i]) ;
                } else {
                    windspeedString = windspeedString + ", "+ String.valueOf(windspeed[i]) ;
                }
            }
            windspeedString = windspeedString + " ]";

            JSONArray directArray = jInfo.getJSONArray("direct");
            windirect_count = directArray.length();
            for (int i = 0; i < directArray.length(); i++) {
                windirect[i] = directArray.getInt(i);
                if (i == 0) {
                    windirectString = "[ " + String.valueOf(windirect[i]) ;
                } else {
                    windirectString = windirectString + ", "+ String.valueOf(windirect[i]) ;
                }
            }
            windirectString = windirectString + " ]";

        } catch (Exception e) {

        }
    }

    public void getACIRCodeData(View view) {
        String statusInput = mStatusEdit.getText().toString();
        String dirInput = mDirEdit.getText().toString();
        String modeInput = mModeEdit.getText().toString();
        String speedInput = mSpeedEdit.getText().toString();
        String tempInput = mTempEdit.getText().toString();

        if (TextUtils.isEmpty(statusInput) || TextUtils.isEmpty(dirInput) || TextUtils.isEmpty(modeInput) ||
                TextUtils.isEmpty(speedInput) || TextUtils.isEmpty(tempInput)) {
            BLCommonUtils.toastShow(IRCodeACOperateActivity.this, "Please input all params!");
        } else {
            BLIrdaConState params = new BLIrdaConState();
            // You can see BLRMCloudAcConstants to know meanings
            params.status = Integer.parseInt(statusInput);
            params.mode = Integer.parseInt(modeInput);
            params.wind_direct = Integer.parseInt(dirInput);
            params.wind_speed = Integer.parseInt(speedInput);
            params.temperature = Integer.parseInt(tempInput);

            BLIrdaConDataResult result = BLIRCode.queryACIRCodeData(mScriptPath, BLRMCloudAcConstants.IRDA_KEY_TEMP_ADD, params);
            if (result.succeed()) {
                mIRCodeDataView.setText(result.getIrcode());
                BLCommonTools.debug(result.getIrcode());
            } else {
                BLCommonUtils.toastShow(IRCodeACOperateActivity.this, "getACIRCodeData failed! Error: " + String.valueOf(result.getStatus()) + " Msg:" + result.getMsg());
            }
        }
    }
}
