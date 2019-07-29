package cn.com.broadlink.blappsdkdemo.activity.scene;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.base.fastjson.BLJSON;
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLLog;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseRecyclerAdapter;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseViewHolder;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.divideritemdecoration.BLDividerUtil;
import cn.com.broadlink.blsfamily.BLSFamily;
import cn.com.broadlink.blsfamily.bean.BLSBaseDataResult;
import cn.com.broadlink.blsfamily.bean.endpoint.BLSEndpointInfo;
import cn.com.broadlink.blsfamily.bean.endpoint.BLSEndpointListData;
import cn.com.broadlink.blsfamily.bean.scene.BLSSceneDevItemInfo;


public class SceneDevListActivity extends TitleActivity{

    private RecyclerView mRvContent;
    private ImageView mIvRefresh;
    private MyAdapter mAdapter;
    private String mFamilyId;
    private List<BLSEndpointInfo> mEndpointList = new ArrayList<>();
    private BLSSceneDevItemInfo mSceneDevItemInfo;
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);
        setTitle("Scene Select Device");
        setBackWhiteVisible();
        
        mFamilyId = getIntent().getStringExtra(BLConstants.INTENT_VALUE);
        mSceneDevItemInfo = getIntent().getParcelableExtra(BLConstants.INTENT_PARCELABLE);
        mPosition = getIntent().getIntExtra(BLConstants.INTENT_ID, -1);
        
        findView();

        initView();
        
        setListener();

        new QueryEndpointListTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR, mFamilyId);
    }
    
    private void findView() {
        mRvContent = (RecyclerView) findViewById(R.id.rv_content);
        mIvRefresh = findViewById(R.id.iv_refresh);
    }

    private void initView() {
        mAdapter = new MyAdapter();
        mRvContent.setLayoutManager(new LinearLayoutManager(mActivity));
        mRvContent.setAdapter(mAdapter);
        mRvContent.addItemDecoration(BLDividerUtil.getDefault(mActivity, mEndpointList));
    }

    private void setListener(){
        
        mIvRefresh.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                new QueryEndpointListTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
            }
        });
        
        mAdapter.setOnItemClickListener(new BLBaseRecyclerAdapter.OnClickListener() {
            @Override
            public void onClick(int position, int viewType) {
                final Intent intent = new Intent(mActivity, SceneDevCmdActivity.class);
                intent.putExtra(BLConstants.INTENT_DEVICE, mEndpointList.get(position));
                startActivityForResult(intent, 101);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101){
            if(resultCode==RESULT_OK){
                mSceneDevItemInfo = data.getParcelableExtra(BLConstants.INTENT_PARCELABLE);
                BLLog.d("scene_dev", "mSceneDevItemInfo" + BLJSON.toJSONString(mSceneDevItemInfo, true));
                final Intent intent = new Intent();
                intent.putExtra(BLConstants.INTENT_PARCELABLE, mSceneDevItemInfo);
                intent.putExtra(BLConstants.INTENT_ID, mPosition);
                setResult(RESULT_OK, intent); 
            }else{
                setResult(RESULT_CANCELED);
            }
            back();
        }
    }

    private class QueryEndpointListTask extends AsyncTask<String, Void, BLSBaseDataResult<BLSEndpointListData> > {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Loading...");
        }

        @Override
        protected BLSBaseDataResult<BLSEndpointListData> doInBackground(String... strings) {
            String familyId = strings[0];
            return BLSFamily.Endpoint.getList(familyId);
        }

        @Override
        protected void onPostExecute(BLSBaseDataResult<BLSEndpointListData>  result) {
            super.onPostExecute(result);
            dismissProgressDialog();
            if (result != null && result.succeed() && result.getData() != null && result.getData().getEndpoints() != null) {
                if(mSceneDevItemInfo != null){
                    BLSEndpointInfo sceneDev = null;
                    for (BLSEndpointInfo endpoint : result.getData().getEndpoints()) {
                        if (endpoint.getEndpointId().equalsIgnoreCase(mSceneDevItemInfo.getEndpointId())) {
                            sceneDev = endpoint;
                        }
                    }

                    if (sceneDev == null) {
                        BLToastUtils.show("Endpoint not found");
                        return;
                    }else{
                        final Intent intent = new Intent(mActivity, SceneDevCmdActivity.class);
                        intent.putExtra(BLConstants.INTENT_PARCELABLE, mSceneDevItemInfo);
                        intent.putExtra(BLConstants.INTENT_DEVICE, sceneDev);
                        startActivityForResult(intent, 101);
                    }
                    
                    
                }else{
                    mEndpointList.clear();
                    if (result.getData().getEndpoints() != null) {
                        mEndpointList.addAll(result.getData().getEndpoints());
                    }else{
                        BLCommonUtils.toastErr(result);
                    }
                    mAdapter.notifyDataSetChanged();
                }
         
            }else{
                BLCommonUtils.toastErr(result);
            }
        }
    }

    class MyAdapter extends BLBaseRecyclerAdapter<BLSEndpointInfo> {

        public MyAdapter() {
            super(mEndpointList, R.layout.item_family_room);
        }

        @Override
        public void onBindViewHolder(BLBaseViewHolder holder, final int position) {
            super.onBindViewHolder(holder, position);
            holder.setText(R.id.tv_name, mBeans.get(position).getFriendlyName());
            holder.setText(R.id.tv_mac, mBeans.get(position).getEndpointId());
        }
    }
}
