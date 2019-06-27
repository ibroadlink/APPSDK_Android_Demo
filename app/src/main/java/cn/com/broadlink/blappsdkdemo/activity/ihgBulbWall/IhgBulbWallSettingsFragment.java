package cn.com.broadlink.blappsdkdemo.activity.ihgBulbWall;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.view.BLSingleItemView;


public class IhgBulbWallSettingsFragment extends Fragment {


    private BLSingleItemView mSvCycle;
    private BLSingleItemView mSvCount;
    private BLSingleItemView mSvInterval;
    private BLSingleItemView mSvBrightness;

    public IhgBulbWallSettingsFragment() {
        // Required empty public constructor
    }

    public static IhgBulbWallSettingsFragment newInstance() {
        IhgBulbWallSettingsFragment fragment = new IhgBulbWallSettingsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View inflate = inflater.inflate(R.layout.fragment_ihg_bulb_wall_settings, container, false);
            findView(inflate);
            return inflate;
        }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void findView(View inflate) {
        mSvCycle = (BLSingleItemView) inflate.findViewById(R.id.sv_cycle);
        mSvCount = (BLSingleItemView) inflate.findViewById(R.id.sv_count);
        mSvInterval = (BLSingleItemView) inflate.findViewById(R.id.sv_interval);
        mSvBrightness = (BLSingleItemView) inflate.findViewById(R.id.sv_brightness);
    }

    private void initView() {
        final IhgBulbWallMainActivity activity = (IhgBulbWallMainActivity) getActivity();
        try {
            if (activity != null && activity.mIhgBulbInfo!=null) {
                mSvCycle.setValue(activity.mIhgBulbInfo.showParam.loop);
                mSvCount.setValue(activity.mIhgBulbInfo.showParam.times);
                mSvInterval.setValue(activity.mIhgBulbInfo.showParam.interval);
                mSvBrightness.setValue(activity.mIhgBulbInfo.showParam.brightness);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshView(){
        initView();
    }

}
