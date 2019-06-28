package cn.com.broadlink.blappsdkdemo.activity.ihgBulbWall;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseRecyclerAdapter;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseViewHolder;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.divideritemdecoration.BLDividerUtil;


public class IhgBulbWallManageFragment extends Fragment {

    private ArrayList<BulbInfo> mBulbList = new ArrayList<>();
    private RecyclerView mRvContent;
    private MyAdapter mAdapter;
    private IhgBulbWallMainActivity mActivity;
    
    public IhgBulbWallManageFragment() {
    }

    public static IhgBulbWallManageFragment newInstance() {
        IhgBulbWallManageFragment fragment = new IhgBulbWallManageFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View inflate = inflater.inflate(R.layout.fragment_ihg_bulb_wall_manage, container, false);
        findView(inflate);
        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity = (IhgBulbWallMainActivity) getActivity();
        getBulbInfoFromActivity();
        initView();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            refreshView();
        }
    }

    private void getBulbInfoFromActivity() {
        try {
            if (mActivity != null && mActivity.mIhgBulbInfo!=null) {
                mBulbList.clear();
                for (int i = 0; i < mActivity.mIhgBulbInfo.maclist.size(); i++) {
                    mBulbList.add(new BulbInfo(mActivity.mIhgBulbInfo.maclist.get(i), mActivity.mIhgBulbInfo.rgblist.get(i), i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findView(View inflate) {
        mRvContent = (RecyclerView) inflate.findViewById(R.id.rv_content);
    }

    private void initView() {
        mAdapter = new MyAdapter();
        mRvContent.setLayoutManager(new GridLayoutManager(getContext(), 4));
        mRvContent.setAdapter(mAdapter);
        mRvContent.addItemDecoration(BLDividerUtil.getGrid(getContext(), 4, mBulbList));

        mAdapter.setOnItemClickListener(new BLBaseRecyclerAdapter.OnClickListener() {
            @Override
            public void onClick(final int position, int viewType) {
                BLAlert.showEditDilog(mActivity, "Setup Bulb Mac",mBulbList.get(position).mac, new BLAlert.BLEditDialogOnClickListener() {
                    @Override
                    public void onClink(final String value) {

                        if (!BLCommonUtils.isMacShort(value)) {
                            BLToastUtils.show("Mac format should be '001122334455'");
                            return;
                        }
                        mBulbList.get(position).mac = value;
                        mBulbList.get(position).isEdited = true;
                        mActivity.mIhgBulbInfo.maclist.set(position, value);

                        mActivity.mIhgBulbWallManager.setupScene(mActivity.mDNADevice, mActivity.mIhgBulbInfo.maclist,mActivity.mIhgBulbInfo.rgblist,
                                new IhgBulbWallManager.IhgBulbCallBack() {
                                    @Override
                                    public void onResult(String result) {
                                        BLToastUtils.show(result);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                });
                    }

                    @Override
                    public void onCancel(String value) {

                    }
                }, false);
            }
        });
    }
    
    public void refreshView(){
        getBulbInfoFromActivity();
        mAdapter.notifyDataSetChanged();
    }

    static class BulbInfo {
        public String mac;
        public int rgb;
        public int id;
        public boolean isEdited;
        public BulbInfo(String mac, int rgb, int id) {
            this.mac = mac;
            this.rgb = rgb;
            this.id = id;
        }
    }

    class MyAdapter extends BLBaseRecyclerAdapter<BulbInfo> {

        public MyAdapter() {
            super(mBulbList, R.layout.item_ihg_bulb);
        }

        @Override
        public void onBindViewHolder(final BLBaseViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);
            holder.setText(R.id.tv_index, String.valueOf(position));
            holder.setTextColor(R.id.tv_index, getItem(position).isEdited ? Color.RED : Color.BLACK);
            holder.setText(R.id.tv_mac, mBulbList.get(position).mac);
            holder.setBackgroundColor(R.id.ll_root, BLCommonUtils.parseColor(mBulbList.get(position).rgb));
            holder.setAlpha(R.id.ll_root, 200);
        }
    }

}
