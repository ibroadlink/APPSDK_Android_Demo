package cn.com.broadlink.blappsdkdemo.activity.websocket;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.BLListAlert;
import cn.com.broadlink.blappsdkdemo.view.InputTextView;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.websocket.BLSWebSocket;
import cn.com.broadlink.websocket.bean.ParamWssBase;
import cn.com.broadlink.websocket.bean.ResultWssBase;
import cn.com.broadlink.websocket.bean.ResultWssUrl;
import cn.com.broadlink.websocket.constant.BLSWebSocketConstants;
import cn.com.broadlink.websocket.manager.BLSWebSocketCallback;
import cn.com.broadlink.websocket.manager.BLSWebSocketClient;

public class WebSocketActivity extends TitleActivity {

    private Button mBtGetUrl;
    private Button mBtCreate;
    private Button mBtSend;
    private Button mBtDestroy;
    private EditText mEtResult;
    private EditText mEtReceive;
    private TextView mTvUrl;
    private String mUrl = null;
    private volatile boolean mInitSucc = false;
    private BLSWebSocketClient mClient;
    private String mTopic = null;
    private String mMsgType = null;
    private String mData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_websocket);
        setBackWhiteVisible();
        setTitle("WebSocket");
        initView();
        setListener();
    }

    private void setListener() {
        setRightButtonOnClickListener("Clear", getResources().getColor(R.color.bl_yellow_main_color), new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                mEtResult.setText(null);
                mEtReceive.setText(null);
            }
        });
        
        mBtGetUrl.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                getUrl();
            }
        });
        
        mBtCreate.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                createLink();
            }
        });

        mBtSend.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                showSendDialog();
            }
        });
        
        mBtDestroy.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                destroyLink();
            }
        });
    }

    private void initView() {
        mBtGetUrl = (Button) findViewById(R.id.bt_get_url);
        mBtCreate = (Button) findViewById(R.id.bt_create);
        mBtSend = (Button) findViewById(R.id.bt_send);
        mBtDestroy = (Button) findViewById(R.id.bt_destroy);
        mEtResult = (EditText) findViewById(R.id.et_result);
        mEtReceive = (EditText) findViewById(R.id.et_receive);
        mTvUrl = (TextView) findViewById(R.id.tv_url);
    }

    private void createLink(){
        if(mClient != null && mInitSucc){
            BLAlert.showDialog(mActivity, "You have aleady created a link, rebuild it ?", new BLAlert.DialogOnClickListener() {
                @Override
                public void onPositiveClick() {
                    destroyLink();
                    doCreateLink();
                }

                @Override
                public void onNegativeClick() {

                }
            });
        }else{
            doCreateLink();
        }
    }

    private void doCreateLink() {
        if(mUrl == null){
            BLToastUtils.show("Get Url First");
            return;
        }

        mClient = BLSWebSocket.createLink(mUrl, new BLSWebSocketCallback() {
            @Override
            public void onInit(ResultWssBase resultWssBase) {
                mInitSucc = (resultWssBase != null && resultWssBase.succeed());
                showResult(mEtResult, resultWssBase, true);
            }

            @Override
            public void onReceive(ResultWssBase resultWssBase) {
                showResult(mEtReceive, resultWssBase, true);
            }

            @Override
            public void onClose(String s) {
                showResult(mEtResult, "onClose: reason: " + s, true);
            }
        });
    }

    private void showResult(final EditText view, final Object result, final boolean append) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("\nHH:mm:ss ");
                final String date = sdf.format(new Date());
                
                if(append){
                    view.append(date);
                }else{
                    view.setText(date);
                }

                if (result == null) {
                    view.append("Return null");
                } else {
                    if (result instanceof String) {
                        view.append((String) result);
                    } else {
                        view.append(JSON.toJSONString(result, true));
                    }
                }
            }
        });
    }


    private void showSendDialog(){

        if (mClient == null) {
            BLToastUtils.show("Create link first.");
            return;
        }

        if (!mInitSucc) {
            BLToastUtils.show("Web socket init fail, try create link again.");
            return;
        }
        
        final LinearLayout dialog = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_websocket_cmd, null);
        final EditText etData = dialog.findViewById(R.id.et_data);
        final InputTextView etTopic = dialog.findViewById(R.id.et_topic);
        final InputTextView etMsgType = dialog.findViewById(R.id.et_msgtype);
        final Spinner mSpTopic = dialog.findViewById(R.id.sp_topic);
        final Spinner mSpMsgType = dialog.findViewById(R.id.sp_msgtype);

        final String[] topicArray = {BLSWebSocketConstants.TOPIC.DEV_PUSH, BLSWebSocketConstants.TOPIC.SENCE_STATUS};
        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, topicArray);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpTopic.setAdapter(spinnerAdapter);
        mSpTopic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                etTopic.setText(topicArray[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                etTopic.setText(topicArray[0]);
            }
        });
        
        final String[] msgTypeArray = { BLSWebSocketConstants.MSG_TYPE.INIT, BLSWebSocketConstants.MSG_TYPE.SUB,BLSWebSocketConstants.MSG_TYPE.UNSUB,BLSWebSocketConstants.MSG_TYPE.PING,
                BLSWebSocketConstants.MSG_TYPE.PUSH,BLSWebSocketConstants.MSG_TYPE.QUERY };
        final ArrayAdapter<String> msgTypeAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, msgTypeArray);
        msgTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpMsgType.setAdapter(msgTypeAdapter);
        mSpMsgType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               etMsgType.setText(msgTypeArray[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                etMsgType.setText(msgTypeArray[0]);
            }
        });

        if (mTopic != null) {
            etTopic.setText(mTopic);
            for (int i = 0; i < topicArray.length; i++) {
                if(mTopic.equals(topicArray[i])){
                    mSpTopic.setSelection(i);
                }
            }
        }
        if (mMsgType != null) {
            etMsgType.setText(mMsgType);
            for (int i = 0; i < msgTypeArray.length; i++) {
                if(mMsgType.equals(msgTypeArray[i])){
                    mSpMsgType.setSelection(i);
                }
            }
        }
        
        if (mData != null) {
            etData.setText(mData);
        }

        BLAlert.showCustomViewDilog(mActivity, dialog, "Ok", "Cancel", new BLAlert.DialogOnClickListener() {
            @Override
            public void onPositiveClick() {
                 String data = etData.getText()==null ? "" : etData.getText().toString();
                 String topic = etTopic.getTextString();
                 String msgType = etMsgType.getTextString();

                if (data == null) {
                    BLToastUtils.show("Data string can not be empty!");
                    return;
                }

                mTopic = !TextUtils.isEmpty(topic) ? topic : topicArray[0];
                mMsgType = !TextUtils.isEmpty(msgType) ? msgType : msgTypeArray[0];
                mData = data;

                mClient.send(new ParamWssBase(topic, msgType, data));
            }

            @Override
            public void onNegativeClick() {

            }
        });
    }
    
    private void destroyLink(){
        if (mClient != null) {
            mClient.finish();
            mClient = null;
        }
    }
    
    private void getUrl() {
        new GetUrlTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
    }
    
    class GetUrlTask extends AsyncTask<String, Void, ResultWssUrl> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Get url");
        }

        @Override
        protected ResultWssUrl doInBackground(String... params) {
            return BLSWebSocket.getUrl();
        }

        @Override
        protected void onPostExecute(final ResultWssUrl result) {
            super.onPostExecute(result);
            dismissProgressDialog();
            showResult(mEtResult, result, true);
            if (result == null || !result.succeed()) {
                BLCommonUtils.toastErr(result);
            } else {
                final ResultWssUrl.DataBean data = result.getData();
                final List<String> urlList = data.getUrl();
                
                if(urlList == null || urlList.size()==0){
                    return;
                }
                
                if(urlList.size()==1){
                    mUrl = result.getFullUrl(0);
                    mTvUrl.setText(String.format("Url: %s", mUrl));
                    return;
                }
                
                final String[] urlArray = urlList.toArray(new String[0]);
                BLListAlert.showAlert(mActivity, null, urlArray, new BLListAlert.OnItemClickLister() {
                    @Override
                    public void onClick(int whichButton) {
                        mUrl = result.getFullUrl(whichButton);
                        mTvUrl.setText(String.format("Url: %s", mUrl));
                    }
                });
            }
        }
    }
    
}
