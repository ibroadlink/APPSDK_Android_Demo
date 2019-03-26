package cn.com.broadlink.blappsdkdemo.activity.irCode;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLFileUtils;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.data.BLIrTvOrSTBInfo;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseRecyclerAdapter;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseViewHolder;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.divideritemdecoration.BLDividerUtil;

public class IRCodeTvOrBoxPanelActivity extends TitleActivity {


    private EditText mEtInput;
    private Button mBtSend;
    private Button mBtGetIrCode;
    private RecyclerView mRvContent;
    private String mSavePath;
    private EditText mTvResult;
    private TextView mTvDev;
    private BLIrTvOrSTBInfo mScriptContent;
    private List<BLIrTvOrSTBInfo.FunctionListBean> mFuncList = new ArrayList<>();
    private DevAdapter mAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Tv/Box Panel");
        setContentView(R.layout.activity_ir_tv_box_panel);
        setBackWhiteVisible();
        
        mSavePath = getIntent().getStringExtra(BLConstants.INTENT_VALUE);
        
        findView();
        
        initView();
   
        setListener();
    }

    private void findView() {
        mEtInput = (EditText) findViewById(R.id.et_input);
        mBtSend = (Button) findViewById(R.id.bt_send);
        mBtGetIrCode = (Button) findViewById(R.id.bt_get_ir_code);
        mRvContent = (RecyclerView) findViewById(R.id.rv_params);
        mTvResult = (EditText) findViewById(R.id.et_result);
        mTvDev = (TextView) findViewById(R.id.tv_dev);
    }

    private void initView() {
        
        if(mSavePath == null){
            BLToastUtils.show("Script path null!");
            back();
            return;
        }
        
        mAdapter = new DevAdapter();
        mRvContent.setLayoutManager(new LinearLayoutManager(mActivity));
        mRvContent.setAdapter(mAdapter);
        mRvContent.addItemDecoration(BLDividerUtil.getDefault(mActivity, mFuncList));
        
        if(!getScriptContent()){
            BLToastUtils.show("Script content read fail!");
            back();
            return;
        }
    }

    private void setListener() {
        mAdapter.setOnItemClickListener(new BLBaseRecyclerAdapter.OnClickListener() {
            @Override
            public void onClick(int position, int viewType) {
                mEtInput.setText(getCodeHexStr(position));
            }
        });
    }
    
    private String getCodeHexStr(int position){
        final List<Integer> code = mAdapter.getItem(position).code;
        final Integer[] integers = code.toArray(new Integer[code.size()]);
        return BLCommonUtils.ints2HexString(integers);
    }

    private void showResult(Object result) {
        if(result == null){
            mTvResult.setText("Return null");
        }else{
            if(result instanceof String){
                mTvResult.setText((String)result);
            }else{
                mTvResult.setText(JSON.toJSONString(result, true));
            }
        }
    }
    
    private boolean getScriptContent() {
        if (mSavePath == null) {
            BLCommonUtils.toastShow(mActivity, "Please download script first!");
            return false;
        }

        File file = new File(mSavePath);
        if (!file.exists()) {
            BLCommonUtils.toastShow(mActivity, "Please download script first!");
            return false;
        } else {
            mScriptContent = JSON.parseObject(BLFileUtils.readTextFileContent(mSavePath), BLIrTvOrSTBInfo.class);
            showResult(mScriptContent);
            
            mFuncList.clear();
            mFuncList.addAll(mScriptContent.functionList);
            mAdapter.notifyDataSetChanged();
        }
        return true;
    }

    class DevAdapter extends BLBaseRecyclerAdapter<BLIrTvOrSTBInfo.FunctionListBean> {

        public DevAdapter() {
            super(mFuncList, R.layout.item_family_room);
        }

        @Override
        public void onBindViewHolder(BLBaseViewHolder holder, final int position) {
            super.onBindViewHolder(holder, position);
            holder.setText(R.id.tv_name, mBeans.get(position).function);
            holder.setText(R.id.tv_mac, String.valueOf(mBeans.get(position).value));
        }
    }
}
