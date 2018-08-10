package cn.com.broadlink.blappsdkdemo.activity.IRCode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.broadlink.base.BLCommonTools;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.common.BLFileUtils;

public class IRCodeTCOperateActivity extends Activity {

    private TextView mTvTitleView, mTVIrcodeView;
    private ListView mTvFunctionListView;

    private List<String> mFunctionList = new ArrayList<>();
    private ArrayAdapter<String> mAdapter;
    private Map<String, String> mCodeMap = new HashMap<>();

    private String mScriptPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ircode_tcoperate);

        findView();
        setListener();

        Intent intent = getIntent();
        if (intent != null) {
            mScriptPath = getIntent().getStringExtra("ScriptPath");

            readIRCodeScriptFile(mScriptPath);
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
                mTVIrcodeView.setText(mCodeMap.get(func));
            }
        });
    }

    private void readIRCodeScriptFile(String file) {

        String jsonStr = BLFileUtils.readTextFileContent(file);
        if (!TextUtils.isEmpty(jsonStr) && !jsonStr.contains("error")) {
            try {
                JSONObject irDataJO = new JSONObject(jsonStr);

                mCodeMap.clear();
                JSONArray ircodeJA = irDataJO.optJSONArray("functionList");
                for (int index = 0; index < ircodeJA.length(); index++) {
                    org.json.JSONObject irCodeSrcJO = ircodeJA.optJSONObject(index);
                    JSONArray codeJA = irCodeSrcJO.optJSONArray("code");
                    String function = irCodeSrcJO.optString("function");

                    mCodeMap.put(function, jsonArrayToCode(codeJA));
                }

                mFunctionList.clear();
                mFunctionList.addAll(mCodeMap.keySet());

                mAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                BLCommonTools.handleError(e);
            }

        }
    }

    private String jsonArrayToCode(JSONArray codeJA) {
        byte[] codes = new byte[codeJA.length()];

        try {
            for (int i = 0; i < codeJA.length(); i++) {
                codes[i] = (byte) codeJA.getInt(i);
            }
        } catch (JSONException e) {
            BLCommonTools.handleError(e);
        }

        return BLCommonTools.bytes2HexString(codes);
    }

}
