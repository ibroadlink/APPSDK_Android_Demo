package cn.com.broadlink.blappsdkdemo.activity.IRCode;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.com.broadlink.base.BLCommonTools;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.view.BLProgressDialog;
import cn.com.broadlink.ircode.BLIRCode;


public class IRCodeOptActivity extends Activity {

    private ListView mOperateListView;
    private String[] mOperateList = {
            "Recognize IRCode",
            "AC IRCode",
            "TV IRCode",
            "STB IRCode"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ircode_opt);

        findView();
        setListener();
    }

    private void findView() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mOperateList);

        mOperateListView = (ListView) findViewById(R.id.ircode_operate_list);
        mOperateListView.setAdapter(adapter);
    }

    private void setListener(){
        mOperateListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();

                switch (position) {
                    case 0: {
                        intent.setClass(IRCodeOptActivity.this, IRCodeRecognizeActivity.class);
                        startActivity(intent);
                    }
                    break;
                    case 1: {
                        intent.putExtra("DEVICE_TYPE", BLConstants.BL_IRCODE_DEVICE_AC);
                        intent.setClass(IRCodeOptActivity.this, IRCodeAreaSelectActivity.class);
                        startActivity(intent);
                    }
                    break;
                    case 2: {
                        intent.putExtra("DEVICE_TYPE", BLConstants.BL_IRCODE_DEVICE_TV);
                        intent.setClass(IRCodeOptActivity.this, IRCodeAreaSelectActivity.class);
                        startActivity(intent);
                    }
                    break;
                    case 3: {
                        intent.putExtra("DEVICE_TYPE", BLConstants.BL_IRCODE_DEVICE_TV_BOX);
                        intent.setClass(IRCodeOptActivity.this, IRCodeAreaSelectActivity.class);
                        startActivity(intent);
                    }
                    break;
                    default:
                        break;
                }
            }
        });
    }

}
