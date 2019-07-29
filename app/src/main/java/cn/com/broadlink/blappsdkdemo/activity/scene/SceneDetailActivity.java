package cn.com.broadlink.blappsdkdemo.activity.scene;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import cn.com.broadlink.base.fastjson.BLJSON;
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLLog;
import cn.com.broadlink.blappsdkdemo.service.BLLocalFamilyManager;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseRecyclerAdapter;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseViewHolder;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.divideritemdecoration.BLDividerUtil;
import cn.com.broadlink.blsfamily.BLSFamily;
import cn.com.broadlink.blsfamily.bean.BLSBaseAttrInfo;
import cn.com.broadlink.blsfamily.bean.BLSBaseDataResult;
import cn.com.broadlink.blsfamily.bean.scene.BLSSceneAddOrUpdateParam;
import cn.com.broadlink.blsfamily.bean.scene.BLSSceneAttrUpdateParam;
import cn.com.broadlink.blsfamily.bean.scene.BLSSceneDevItemInfo;
import cn.com.broadlink.blsfamily.bean.scene.BLSSceneInfo;
import cn.com.broadlink.blsfamily.bean.scene.BLSSceneUpdateAttrInfoData;

/**
 * Desc
 *
 * @author JiangYaqiang
 * 2019/7/25 17:36
 */
public class SceneDetailActivity extends TitleActivity {

    private TextView mTvName;
    private TextView mTvExtend;
    private RecyclerView mRvContent;
    private LinearLayout mLlName;
    private LinearLayout mLlExtend;
    private BLSSceneInfo mSceneInfo;
    private List<BLSSceneDevItemInfo> mSceneDevList = new ArrayList<>();
    private MyAdapter mAdapter;
    private ItemTouchHelper mItemToucheHelper;
    
    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scene_detail);
        setBackWhiteVisible();
        setTitle("Scene Detail");

        initData();

        findView();

        initView();

        setListener();
    }

    private void findView() {
        mTvName = (TextView) findViewById(R.id.tv_name);
        mTvExtend = (TextView) findViewById(R.id.tv_extend);
        mRvContent = (RecyclerView) findViewById(R.id.rv_content);
        mLlName = (LinearLayout) findViewById(R.id.ll_name);
        mLlExtend = (LinearLayout) findViewById(R.id.ll_extend);
    }

    private void initData() {

        mSceneInfo = getIntent().getParcelableExtra(BLConstants.INTENT_PARCELABLE);

        if (mSceneInfo == null) {
            mSceneInfo = new BLSSceneInfo();
            mSceneInfo.setFriendlyName("New Scene");
            mSceneInfo.setFamilyId(BLLocalFamilyManager.getInstance().getCurrentFamilyId());
        }

        if(mSceneInfo.getScenedev()!=null){
            mSceneDevList.addAll(mSceneInfo.getScenedev());
        }
        
        mSceneDevList.add(new BLSSceneDevItemInfo()); // add
    }

    private void initView() {
        mTvName.setText(mSceneInfo.getFriendlyName());
        mTvExtend.setText(mSceneInfo.getExtend());

        mAdapter = new MyAdapter();
        mRvContent.setLayoutManager(new LinearLayoutManager(mActivity));
        mRvContent.setAdapter(mAdapter);
        mRvContent.addItemDecoration(BLDividerUtil.getDefault(mActivity, mSceneDevList));

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
                if(fromPosition == mSceneDevList.size()-1 || toPosition == mSceneDevList.size()-1){
                    return true;
                }
                
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(mSceneDevList, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(mSceneDevList, i, i - 1);
                    }
                }

                // 交换order
                final int orderFrom = mSceneDevList.get(fromPosition).getOrder();
                final int orderTo = mSceneDevList.get(toPosition).getOrder();
                mSceneDevList.get(fromPosition).setOrder(orderTo);
                mSceneDevList.get(toPosition).setOrder(orderFrom);
                
                //mAdapter.notifyItemMoved(fromPosition, toPosition); // 内部会改变点击时的position
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
        
        
//        mAdapter.setOnItemLongClickListener(new BLBaseRecyclerAdapter.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(int position, int viewType) {
//                //如果item不是最后一个，则执行拖拽
//                if (position!=mSceneDevList.size()-1) {
//                    mItemToucheHelper.startDrag(Objects.requireNonNull(mRvContent.findViewHolderForAdapterPosition(position)));
//                }
//                return true;
//            }
//        });

        mAdapter.setOnItemClickListener(new BLBaseRecyclerAdapter.OnClickListener() {
            @Override
            public void onClick(int position, int viewType) {
                final Intent intent = new Intent(mActivity, SceneDevListActivity.class);
                intent.putExtra(BLConstants.INTENT_VALUE, mSceneInfo.getFamilyId());
                intent.putExtra(BLConstants.INTENT_PARCELABLE, mSceneDevList.get(position));
                intent.putExtra(BLConstants.INTENT_ID, position);
                startActivityForResult(intent, 101);
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
                        
                        if(!TextUtils.isEmpty(mSceneInfo.getSceneId())){
                            final BLSSceneAttrUpdateParam blsSceneAttrUpdateParam = new BLSSceneAttrUpdateParam();
                            final BLSSceneUpdateAttrInfoData attr = new BLSSceneUpdateAttrInfoData();
                            attr.setSceneId(mSceneInfo.getSceneId());
                            final ArrayList<BLSBaseAttrInfo> attributes = new ArrayList<>();
                            final BLSBaseAttrInfo<String> attrInfo = new BLSBaseAttrInfo<>();
                            attrInfo.setAttributeName("friendlyName");
                            attrInfo.setAttributeValue(value);
                            attributes.add(attrInfo);
                            attr.setAttributes(attributes);
                            blsSceneAttrUpdateParam.getSceneList().add(attr);

                            new UpdateSceneAttrsTask(blsSceneAttrUpdateParam).executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
                        }
                    }

                    @Override
                    public void onCancel(String value) {

                    }
                }, false);
            }
        });

        mLlExtend.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                BLAlert.showEditDilog(mActivity, "Input extend json string", TextUtils.isEmpty(mTvExtend.getText()) ? "" : mTvExtend.getText().toString(),
                        new BLAlert.BLEditDialogOnClickListener() {
                            @Override
                            public void onClink(String value) {
                                mTvExtend.setText(value);

                                if (!TextUtils.isEmpty(mSceneInfo.getSceneId())) {
                                    final BLSSceneAttrUpdateParam blsSceneAttrUpdateParam = new BLSSceneAttrUpdateParam();
                                    final BLSSceneUpdateAttrInfoData attr = new BLSSceneUpdateAttrInfoData();
                                    attr.setSceneId(mSceneInfo.getSceneId());
                                    final ArrayList<BLSBaseAttrInfo> attributes = new ArrayList<>();
                                    final BLSBaseAttrInfo<String> attrInfo = new BLSBaseAttrInfo<>();
                                    attrInfo.setAttributeName("extend");
                                    attrInfo.setAttributeValue(value);
                                    attributes.add(attrInfo);
                                    attr.setAttributes(attributes);
                                    blsSceneAttrUpdateParam.getSceneList().add(attr);

                                    new UpdateSceneAttrsTask(blsSceneAttrUpdateParam).executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
                                }
                               
                            }

                            @Override
                            public void onCancel(String value) {

                            }
                        }, false);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101 && resultCode==RESULT_OK){
            BLSSceneDevItemInfo sceneDevItemInfo = data.getParcelableExtra(BLConstants.INTENT_PARCELABLE);
            int position = getIntent().getIntExtra(BLConstants.INTENT_ID, -1);

            BLLog.d("scene_detail", "mSceneDevItemInfo" + BLJSON.toJSONString(sceneDevItemInfo, true));
            BLLog.d("scene_detail", "position" + position);
            
            if(position>=0){ // 编辑
                mSceneDevList.set(position, sceneDevItemInfo);
                sceneDevItemInfo.setOrder(position);
            }else{ // 新增
                mSceneDevList.add(mSceneDevList.size()-1 ,sceneDevItemInfo);
                sceneDevItemInfo.setOrder(mSceneDevList.size()-2);
            }
            mAdapter.notifyDataSetChanged();
        }
    }
    
    
    class UpdateSceneAttrsTask extends AsyncTask<String, Void, BLSBaseDataResult> {

        BLSSceneAttrUpdateParam attrUpdateParam;
        
        public UpdateSceneAttrsTask(BLSSceneAttrUpdateParam attrUpdateParam) {
            this.attrUpdateParam = attrUpdateParam;
        }
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Update Scene Attr...");
        }

        @Override
        protected BLSBaseDataResult doInBackground(String... params) {
            return BLSFamily.Scene.updateAttrs(mSceneInfo.getFamilyId(), attrUpdateParam);
        }

        @Override
        protected void onPostExecute(BLSBaseDataResult result) {
            super.onPostExecute(result);
            dismissProgressDialog();

            if(result != null && result.succeed()){
                
            } else {
                BLCommonUtils.toastErr(result);
            }
        }

    }

    class AddOrUpdateTask extends AsyncTask<String, Void, BLSBaseDataResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Save...");
        }

        @Override
        protected BLSBaseDataResult doInBackground(String... params) {

            final BLSSceneAddOrUpdateParam addOrUpdateParam = new BLSSceneAddOrUpdateParam();
            final ArrayList<BLSSceneDevItemInfo> realDevItemList = new ArrayList<>();
            for (int i = 0; i < mSceneDevList.size()-1; i++) {
                realDevItemList.add(mSceneDevList.get(i));
            }
            mSceneInfo.setScenedev(realDevItemList);
            addOrUpdateParam.setSceneInfo(mSceneInfo);
            
            if (!TextUtils.isEmpty(mSceneInfo.getSceneId())) {
                return BLSFamily.Scene.update(mSceneInfo.getFamilyId(), addOrUpdateParam);
            }else{
                return BLSFamily.Scene.add(mSceneInfo.getFamilyId(), addOrUpdateParam);
            }
        }

        @Override
        protected void onPostExecute(BLSBaseDataResult result) {
            super.onPostExecute(result);
            dismissProgressDialog();

            if(result != null && result.succeed()){
                back();
            } else {
                BLCommonUtils.toastErr(result);
            }
        }

    }

    class MyAdapter extends BLBaseRecyclerAdapter<BLSSceneDevItemInfo> {

        public MyAdapter() {
            super(mSceneDevList);
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
                holder.setText(R.id.tv_name, mBeans.get(position).getEndpointId());
                holder.setText(R.id.tv_detail, BLJSON.toJSONString(mBeans.get(position)));

                holder.setOnClickListener(R.id.iv_delete, new OnSingleClickListener() {
                    @Override
                    public void doOnClick(View v) {
                        mSceneDevList.remove(position);
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
                        final Intent intent = new Intent(mActivity, SceneDevListActivity.class);
                        intent.putExtra(BLConstants.INTENT_VALUE, mSceneInfo.getFamilyId());
                        intent.putExtra(BLConstants.INTENT_ID, -1);
                        startActivityForResult(intent, 101);
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
