package cn.com.broadlink.blappsdkdemo.activity.linkage;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.base.BLDateUtils;
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.service.BLLocalFamilyManager;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.BLListAlert;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseRecyclerAdapter;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseViewHolder;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.divideritemdecoration.BLDividerUtil;
import cn.com.broadlink.blsfamily.BLSFamily;
import cn.com.broadlink.blsfamily.bean.BLSBaseDataResult;
import cn.com.broadlink.blsfamily.bean.linkage.BLSLinkageInfo;
import cn.com.broadlink.blsfamily.bean.scene.BLSSceneInfo;
import cn.com.broadlink.blsfamily.bean.scene.BLSSceneListData;

/**
 * Desc
 *
 * @author JiangYaqiang
 * 2019/7/25 17:36
 */
public class LinkageDetailActivity extends TitleActivity {

    private TextView mTvName;
    private RecyclerView mRvContent;
    private LinearLayout mLlName;
    private LinearLayout mLlExtend;
    private BLSLinkageInfo mLinkageInfo;
    private List<BLSSceneInfo> mSelectedSceneList = new ArrayList<>();
    private List<BLSSceneInfo> mAllSceneList = new ArrayList<>();
    private MyAdapter mAdapter;
    private ItemTouchHelper mItemToucheHelper;
    
    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scene_detail);
        setBackWhiteVisible();
        setTitle("Linkage Detail");

        initData();

        findView();

        initView();

        setListener();

        new GetSceneListTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
    }

    private void findView() {
        mTvName = (TextView) findViewById(R.id.tv_name);
        mRvContent = (RecyclerView) findViewById(R.id.rv_content);
        mLlName = (LinearLayout) findViewById(R.id.ll_name);
        mLlExtend = (LinearLayout) findViewById(R.id.ll_extend);
    }

    private void initData() {

        mLinkageInfo = getIntent().getParcelableExtra(BLConstants.INTENT_PARCELABLE);

        if (mLinkageInfo == null) {
            mLinkageInfo = new BLSLinkageInfo();
            mLinkageInfo.setRulename("NewLinkage " + BLDateUtils.getCurrentDate("_yyyy-MM-dd-HH-mm"));
            mLinkageInfo.setFamilyid(BLLocalFamilyManager.getInstance().getCurrentFamilyId());
            mLinkageInfo.setCharacteristicinfo("{}");
            mLinkageInfo.setSource("{}");
            mLinkageInfo.setRuleid("");
            mLinkageInfo.setEnable(1);
        }
    }

    private void initView() {
        mTvName.setText(mLinkageInfo.getRulename());
        mLlExtend.setVisibility(View.GONE);
        
        mAdapter = new MyAdapter();
        mRvContent.setLayoutManager(new LinearLayoutManager(mActivity));
        mRvContent.setAdapter(mAdapter);
        mRvContent.addItemDecoration(BLDividerUtil.getDefault(mActivity, mSelectedSceneList));

        mItemToucheHelper = new ItemTouchHelper(new ItemTouchHelper.Callback(){

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags=0;
                int swipeFlags=0;
                
                if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                    dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                    swipeFlags = 0;
                } else {
                    dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                    swipeFlags = 0;
                }
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                if(fromPosition == mSelectedSceneList.size()-1 || toPosition == mSelectedSceneList.size()-1){
                    return true;
                }
                
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(mSelectedSceneList, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(mSelectedSceneList, i, i - 1);
                    }
                }

                mAdapter.notifyDataSetChanged();
                return true;
            }

            // 左右滑动
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            //当长按选中item的时候（拖拽开始的时候）调用
            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                    viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
                }
                super.onSelectedChanged(viewHolder, actionState);
            }

            //当手指松开的时候（拖拽完成的时候）调用
            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                viewHolder.itemView.setBackgroundColor(Color.WHITE);
                super.clearView(recyclerView, viewHolder);
            }

        });
        mItemToucheHelper.attachToRecyclerView(mRvContent);
    }

    private void setListener() {

        setRightButtonOnClickListener("Save", getResources().getColor(R.color.bl_yellow_main_color), new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                new AddOrUpdateTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
            }
        });

        mLlName.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                BLAlert.showEditDilog(mActivity, "Input a new name", TextUtils.isEmpty(mTvName.getText()) ? "" : mTvName.getText().toString(),
                        new BLAlert.BLEditDialogOnClickListener() {
                    @Override
                    public void onClink(String value) {
                        mTvName.setText(value);
                    }

                    @Override
                    public void onCancel(String value) {

                    }
                }, false);
            }
        });
    }


    class AddOrUpdateTask extends AsyncTask<String, Void, BLBaseResult> {
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Save...");

            final CharSequence name = mTvName.getText();
            mLinkageInfo.setRulename(name==null ? "" : name.toString());
        }

        @Override
        protected BLBaseResult doInBackground(String... params) {

            final ArrayList<String> realDevItemList = new ArrayList<>();
            for (int i = 0; i < mSelectedSceneList.size()-1; i++) {
                realDevItemList.add(mSelectedSceneList.get(i).getSceneId());
            }
            
            mLinkageInfo.setSceneIds(realDevItemList);
            
            if (!TextUtils.isEmpty(mLinkageInfo.getRuleid())) {
                return BLSFamily.Linkage.update(BLLocalFamilyManager.getInstance().getCurrentFamilyId(), mLinkageInfo);
            }else{
                return BLSFamily.Linkage.add(BLLocalFamilyManager.getInstance().getCurrentFamilyId(), mLinkageInfo);
            }
        }

        @Override
        protected void onPostExecute(BLBaseResult result) {
            super.onPostExecute(result);
            dismissProgressDialog();

            if(result != null && result.succeed()){
                back();
            } else {
                BLCommonUtils.toastErr(result);
            }
        }

    }


    class GetSceneListTask extends AsyncTask<Void, Void, BLSBaseDataResult<BLSSceneListData>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Get Scene List...");
        }

        @Override
        protected BLSBaseDataResult<BLSSceneListData> doInBackground(Void... params) {
            return BLSFamily.Scene.getList(BLLocalFamilyManager.getInstance().getCurrentFamilyId());
        }

        @Override
        protected void onPostExecute(BLSBaseDataResult<BLSSceneListData> result) {
            super.onPostExecute(result);
            dismissProgressDialog();

            if(result != null && result.succeed() && result.getData()!=null && result.getData().getScenes()!=null){
                mAllSceneList.clear();
                mAllSceneList.addAll(result.getData().getScenes());
                
                mSelectedSceneList.clear();
                if(mLinkageInfo.getSceneIds()!=null){
                    for (BLSSceneInfo blsSceneInfo : mAllSceneList) {
                        if(mLinkageInfo.getSceneIds().contains(blsSceneInfo.getSceneId())){
                            mSelectedSceneList.add(blsSceneInfo);
                        }
                    }
                }
                mSelectedSceneList.add(new BLSSceneInfo()); // add
                mAdapter.notifyDataSetChanged();
            } else {
                BLCommonUtils.toastErr(result);
            }
        }

    }
    
    
    class MyAdapter extends BLBaseRecyclerAdapter<BLSSceneInfo> {

        public MyAdapter() {
            super(mSelectedSceneList);
        }

        @Override
        protected int layoutId(int viewType) {
            switch (viewType) {
                case 0:
                    return R.layout.item_scene_dev;
                case 1:
                    return R.layout.item_single_center;
                default:
                    return R.layout.item_scene_dev;
            }
        }

        @Override
        public void onBindViewHolder(BLBaseViewHolder holder, final int position) {
            super.onBindViewHolder(holder, position);
            
            if(getItemViewType(position)==0){
                holder.setText(R.id.tv_name, mBeans.get(position).getFriendlyName());
                holder.setText(R.id.tv_detail, mBeans.get(position).getSceneId());

                holder.setOnClickListener(R.id.iv_delete, new OnSingleClickListener() {
                    @Override
                    public void doOnClick(View v) {
                        mSelectedSceneList.remove(position);
                        MyAdapter.this.notifyDataSetChanged();
                    }
                });

                final View dragView = holder.get(R.id.iv_drag);
                dragView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if(event.getAction() == MotionEvent.ACTION_DOWN){
                            mItemToucheHelper.startDrag(Objects.requireNonNull(mRvContent.findViewHolderForAdapterPosition(position)));
                        }
                        return true;
                    }
                });
            }else{
                holder.setOnClickListener(R.id.ll_root, new OnSingleClickListener() {
                    @Override
                    public void doOnClick(View v) {
                        final ArrayList<BLSSceneInfo> sceneListToSelect = new ArrayList<>();

                        for (BLSSceneInfo blsSceneInfo : mAllSceneList) {
                            if(!mSelectedSceneList.contains(blsSceneInfo)){
                                sceneListToSelect.add(blsSceneInfo);
                            }
                        }
                        if(sceneListToSelect.size()==0){
                            BLToastUtils.show("All Scenes have been added.");
                            return;
                        }
                        
                        final String[] sceneNames = new String[sceneListToSelect.size()];
                        for (int i = 0; i < sceneListToSelect.size(); i++) {
                            sceneNames[i] = sceneListToSelect.get(i).getFriendlyName();
                        }

                        BLListAlert.showAlert(mActivity, "Select Scene To Add", sceneNames, new BLListAlert.OnItemClickLister() {
                            @Override
                            public void onClick(int whichButton) {
                                mSelectedSceneList.add(mSelectedSceneList.size()-1, sceneListToSelect.get(whichButton));
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
            }
        }

        @Override
        public int getItemViewType(int position) {
            
            if(position==getItemCount()-1){
                return 1;
            }
            
            return 0;
        }
    }
}
