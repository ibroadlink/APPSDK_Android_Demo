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
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseRecyclerAdapter;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseViewHolder;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.divideritemdecoration.BLDividerUtil;


public class IhgBulbWallManageFragment extends Fragment {

    private ArrayList<BulbInfo> mBulbList = new ArrayList<>();
    private RecyclerView mRvContent;
    private MyAdapter mAdapter;

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
        getBulbInfoFromActivity();
        initView();
    }

    private void getBulbInfoFromActivity() {
        final IhgBulbWallMainActivity activity = (IhgBulbWallMainActivity) getActivity();
        try {
            if (activity != null && activity.mIhgBulbInfo!=null) {
                for (int i = 0; i < activity.mIhgBulbInfo.maclist.size(); i++) {
                    mBulbList.add(new BulbInfo(activity.mIhgBulbInfo.maclist.get(i), activity.mIhgBulbInfo.rgblist.get(i), i));
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
