package cn.com.broadlink.blappsdkdemo.activity.IRCode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;


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
                    }
                    break;
                    case 1: {
                        intent.putExtra("DEVICE_TYPE", BLConstants.BL_IRCODE_DEVICE_AC);
                        intent.setClass(IRCodeOptActivity.this, IRCodeAreaSelectActivity.class);
                    }
                    break;
                    case 2: {
                        intent.putExtra("DEVICE_TYPE", BLConstants.BL_IRCODE_DEVICE_TV);
                        intent.setClass(IRCodeOptActivity.this, IRCodeAreaSelectActivity.class);
                    }
                    break;
                    case 3: {
                        intent.putExtra("DEVICE_TYPE", BLConstants.BL_IRCODE_DEVICE_TV_BOX);
                        intent.setClass(IRCodeOptActivity.this, IRCodeAreaSelectActivity.class);
                    }
                    break;
                    default:
                        return;
                }

                startActivity(intent);
            }
        });
    }
}
