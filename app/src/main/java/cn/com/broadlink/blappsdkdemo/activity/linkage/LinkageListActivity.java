package cn.com.broadlink.blappsdkdemo.activity.linkage;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.base.fastjson.BLJSON;
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseRecyclerAdapter;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseViewHolder;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.divideritemdecoration.BLDividerUtil;
import cn.com.broadlink.blsfamily.BLSFamily;
import cn.com.broadlink.blsfamily.bean.BLSBaseDataResult;
import cn.com.broadlink.blsfamily.bean.linkage.BLSLinkageInfo;
import cn.com.broadlink.blsfamily.bean.linkage.BLSLinkageListData;


public class LinkageListActivity extends TitleActivity{

    private RecyclerView mRvContent;
    private ImageView mIvRefresh;
    private MyAdapter mAdapter;
    private String mFamilyId;
    private List<BLSLinkageInfo> mLinkageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);
        setTitle("Linkage List");
        setBackWhiteVisible();
        
        mFamilyId = getIntent().getStringExtra(BLConstants.INTENT_VALUE);
        
        findView();

        initView();
        
        setListener();
    }
    
    private void findView() {
        mRvContent = (RecyclerView) findViewById(R.id.rv_content);
        mIvRefresh = findViewById(R.id.iv_refresh);
    }

    private void initView() {
        mAdapter = new MyAdapter();
        mRvContent.setLayoutManager(new LinearLayoutManager(mActivity));
        mRvContent.setAdapter(mAdapter);
        mRvContent.addItemDecoration(BLDividerUtil.getDefault(mActivity, mLinkageList));
    }

    private void setListener(){
        
        mIvRefresh.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                new GetLinkageListTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
            }
        });
        
        mAdapter.setOnItemClickListener(new BLBaseRecyclerAdapter.OnClickListener() {
            @Override
            public void onClick(int position, int viewType) {
                BLCommonUtils.toActivity(mActivity, LinkageDetailActivity.class, mLinkageList.get(position));
            }
        });

        mAdapter.setOnItemLongClickListener(new BLBaseRecyclerAdapter.OnLongClickListener() {
            @Override
            public boolean onLongClick(final int position, int viewType) {

                BLAlert.showDialog(mActivity, "Confirm to delete this scene?", new BLAlert.DialogOnClickListener() {
                    @Override
                    public void onPositiveClick() {
                        new DelLinkageTask(position).executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
                    }

                    @Override
                    public void onNegativeClick() {

                    }
                });
                
                return true;
            }
        });

        setRightButtonOnClickListener(R.drawable.btn_add_cycle_white, new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                BLCommonUtils.toActivity(mActivity, LinkageDetailActivity.class);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetLinkageListTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
    }

    class GetLinkageListTask extends AsyncTask<Void, Void, BLSBaseDataResult<BLSLinkageListData>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Get Linkage List...");
        }

        @Override
        protected BLSBaseDataResult<BLSLinkageListData> doInBackground(Void... params) {
            return BLSFamily.Linkage.getList(mFamilyId);
        }

        @Override
        protected void onPostExecute(BLSBaseDataResult<BLSLinkageListData> result) {
            super.onPostExecute(result);
            dismissProgressDialog();

            if(result != null && result.succeed() && result.getData()!=null && result.getData().getLinkages()!=null){
                mLinkageList.clear();
                mLinkageList.addAll(result.getData().getLinkages());
                mAdapter.notifyDataSetChanged();
            } else {
                BLCommonUtils.toastErr(result);
            }
        }

    }

    class DelLinkageTask extends AsyncTask<String, Void, BLBaseResult> {
        private int index;

        public DelLinkageTask(int index) {
            this.index = index;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Delete Scene...");
        }

        @Override
        protected BLBaseResult doInBackground(String... params) {
            String sceneId = mLinkageList.get(index).getRuleid();
            
            return BLSFamily.Linkage.delete(mFamilyId, sceneId);
        }

        @Override
        protected void onPostExecute(BLBaseResult result) {
            super.onPostExecute(result);
            dismissProgressDialog();

            if(result != null && result.succeed()){
                mLinkageList.remove(index);
                mAdapter.notifyDataSetChanged();
            } else {
                BLCommonUtils.toastErr(result);
            }
        }

    }

    class MyAdapter extends BLBaseRecyclerAdapter<BLSLinkageInfo> {

        public MyAdapter() {
            super(mLinkageList, R.layout.item_family_room);
        }

        @Override
        public void onBindViewHolder(BLBaseViewHolder holder, final int position) {
            super.onBindViewHolder(holder, position);
            holder.setText(R.id.tv_name, mBeans.get(position).getRulename());
            holder.setText(R.id.tv_mac, BLJSON.toJSONString(mBeans.get(position), true));
        }
    }
}
