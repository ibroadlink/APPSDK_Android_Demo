package cn.com.broadlink.blappsdkdemo.activity.scene;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseRecyclerAdapter;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseViewHolder;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.divideritemdecoration.BLDividerUtil;
import cn.com.broadlink.blsfamily.BLSFamily;
import cn.com.broadlink.blsfamily.bean.BLSBaseDataResult;
import cn.com.broadlink.blsfamily.bean.scene.BLSSceneDeleteParam;
import cn.com.broadlink.blsfamily.bean.scene.BLSSceneIdData;
import cn.com.broadlink.blsfamily.bean.scene.BLSSceneInfo;
import cn.com.broadlink.blsfamily.bean.scene.BLSSceneListData;


public class SceneListActivity extends TitleActivity{

    private RecyclerView mRvContent;
    private ImageView mIvRefresh;
    private MyAdapter mAdapter;
    private String mFamilyId;
    private List<BLSSceneInfo> mSceneList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);
        setTitle("Scene List");
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
        mRvContent.addItemDecoration(BLDividerUtil.getDefault(mActivity, mSceneList));
    }

    private void setListener(){
        
        mIvRefresh.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                new GetSceneListTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
            }
        });
        
        mAdapter.setOnItemClickListener(new BLBaseRecyclerAdapter.OnClickListener() {
            @Override
            public void onClick(int position, int viewType) {
                BLCommonUtils.toActivity(mActivity, SceneDetailActivity.class, mSceneList.get(position));
            }
        });

        mAdapter.setOnItemLongClickListener(new BLBaseRecyclerAdapter.OnLongClickListener() {
            @Override
            public boolean onLongClick(final int position, int viewType) {

                BLAlert.showDialog(mActivity, "Confirm to delete this scene?", new BLAlert.DialogOnClickListener() {
                    @Override
                    public void onPositiveClick() {
                        new DelSceneTask(position).executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
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
                BLCommonUtils.toActivity(mActivity, SceneDetailActivity.class);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetSceneListTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
    }

    class GetSceneListTask extends AsyncTask<Void, Void, BLSBaseDataResult<BLSSceneListData>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Get Scene List...");
        }

        @Override
        protected BLSBaseDataResult<BLSSceneListData> doInBackground(Void... params) {
            return BLSFamily.Scene.getList(mFamilyId);
        }

        @Override
        protected void onPostExecute(BLSBaseDataResult<BLSSceneListData> result) {
            super.onPostExecute(result);
            dismissProgressDialog();

            if(result != null && result.succeed() && result.getData()!=null && result.getData().getScenes()!=null){
                mSceneList.clear();
                mSceneList.addAll(result.getData().getScenes());
                mAdapter.notifyDataSetChanged();
            } else {
                BLCommonUtils.toastErr(result);
            }
        }

    }

    class DelSceneTask extends AsyncTask<String, Void, BLSBaseDataResult> {
        private int index;

        public DelSceneTask(int index) {
            this.index = index;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Delete Scene...");
        }

        @Override
        protected BLSBaseDataResult doInBackground(String... params) {
            String sceneId = mSceneList.get(index).getSceneId();
            
            final BLSSceneDeleteParam deleteParam = new BLSSceneDeleteParam();
            final BLSSceneIdData delParam = new BLSSceneIdData();
            delParam.setSceneId(sceneId);
            deleteParam.getSceneList().add(delParam);
            return BLSFamily.Scene.delete(mFamilyId, deleteParam);
        }

        @Override
        protected void onPostExecute(BLSBaseDataResult result) {
            super.onPostExecute(result);
            dismissProgressDialog();

            if(result != null && result.succeed()){
                mSceneList.remove(index);
                mAdapter.notifyDataSetChanged();
            } else {
                BLCommonUtils.toastErr(result);
            }
        }

    }

    class MyAdapter extends BLBaseRecyclerAdapter<BLSSceneInfo> {

        public MyAdapter() {
            super(mSceneList, R.layout.item_scene);
        }

        @Override
        public void onBindViewHolder(BLBaseViewHolder holder, final int position) {
            super.onBindViewHolder(holder, position);
            holder.setText(R.id.tv_name, mBeans.get(position).getFriendlyName());

            holder.setOnClickListener(R.id.bt_exe, new OnSingleClickListener() {
                @Override
                public void doOnClick(View v) {
                    // TODO: 2019/7/25 执行场景 
                    BLToastUtils.show("Exe this scene");
                }
            });
        }
    }
}
