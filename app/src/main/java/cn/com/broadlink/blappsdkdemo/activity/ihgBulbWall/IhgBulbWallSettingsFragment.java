package cn.com.broadlink.blappsdkdemo.activity.ihgBulbWall;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.BLListAlert;
import cn.com.broadlink.blappsdkdemo.view.BLSingleItemView;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;


public class IhgBulbWallSettingsFragment extends Fragment {


    private BLSingleItemView mSvCycle;
    private BLSingleItemView mSvCount;
    private BLSingleItemView mSvInterval;
    private BLSingleItemView mSvBrightness;
    private BLSingleItemView mSvScene;
    private IhgBulbWallMainActivity mActivity;
    private final String[] sLoopTypeArray = {IhgBulbWallConstants.LOOP_TYPE.SINGLE, IhgBulbWallConstants.LOOP_TYPE.LIST};
    private final String[] sSceneOptNameArray = {"Stop","Start","Pause","Resume"};
    private final int[] sSceneOptArray = {IhgBulbWallConstants.SCENE_ACT.STOP, IhgBulbWallConstants.SCENE_ACT.START, IhgBulbWallConstants.SCENE_ACT.PAUSE, IhgBulbWallConstants.SCENE_ACT.RESUME};

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

        setListener();

        return inflate;
        }

    private void setListener() {
        mSvCycle.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                BLListAlert.showAlert(mActivity, "Select Loop Type", sLoopTypeArray, new BLListAlert.OnItemClickLister() {
                    @Override
                    public void onClick(int whichButton) {
                        
                        String type;
                        if(whichButton==0){
                             type = IhgBulbWallConstants.LOOP_TYPE.SINGLE;
                        }else{
                            type = IhgBulbWallConstants.LOOP_TYPE.LIST;
                        }
                        mSvCycle.setValue(type);
                        
                        mActivity.mIhgBulbWallManager.setLoopType(mActivity.mDNADevice, type, new IhgBulbWallManager.IhgBulbCallBack() {
                            @Override
                            public void onResult(String result) {
                                BLToastUtils.show(result);
                            }
                        });
                    }
                });
            }
        });

        mSvCount.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {

                BLAlert.showEditDilog(mActivity, "Setup Loop Count(>0)", mSvCount.getValue(), new BLAlert.BLEditDialogOnClickListener() {
                    @Override
                    public void onClink(final String value) {
                        
                        int count = Integer.parseInt(value);
                        if (count <= 0) {
                            BLToastUtils.show("Should larger than 0");
                            return;
                        }
                        
                        mActivity.mIhgBulbWallManager.setLoopCount(mActivity.mDNADevice, count, new IhgBulbWallManager.IhgBulbCallBack() {
                            @Override
                            public void onResult(String result) {
                                BLToastUtils.show(result);
                                mSvCount.setValue(value);
                            }
                        });
                    }

                    @Override
                    public void onCancel(String value) {

                    }
                }, true);
            }
        });

        mSvInterval.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {

                BLAlert.showEditDilog(mActivity, "Setup Loop Interval(>0)", mSvInterval.getValue(), new BLAlert.BLEditDialogOnClickListener() {
                    @Override
                    public void onClink(final String value) {
                        
                        int count = Integer.parseInt(value);
                        if (count <= 0) {
                            BLToastUtils.show("Should larger than 0");
                            return;
                        }
                        
                        mActivity.mIhgBulbWallManager.setLoopInterval(mActivity.mDNADevice, count, new IhgBulbWallManager.IhgBulbCallBack() {
                            @Override
                            public void onResult(String result) {
                                BLToastUtils.show(result);
                                mSvInterval.setValue(value);
                            }
                        });
                    }

                    @Override
                    public void onCancel(String value) {

                    }
                }, true);
            }
        });

        mSvBrightness.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {

                BLAlert.showEditDilog(mActivity, "Setup Brightness(0-100)", mSvBrightness.getValue(), new BLAlert.BLEditDialogOnClickListener() {
                    @Override
                    public void onClink(final String value) {
                        
                        int count = Integer.parseInt(value);
                        if (count < 0 || count>100) {
                            BLToastUtils.show("Should be 0-100");
                            return;
                        }
                        
                        mActivity.mIhgBulbWallManager.setupBrightness(mActivity.mDNADevice, count, new IhgBulbWallManager.IhgBulbCallBack() {
                            @Override
                            public void onResult(String result) {
                                BLToastUtils.show(result);
                                mSvBrightness.setValue(value);
                            }
                        });
                    }

                    @Override
                    public void onCancel(String value) {

                    }
                }, true);
            }
        });
        mSvScene.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                BLListAlert.showAlert(mActivity, "Select Scene Operation", sSceneOptNameArray, new BLListAlert.OnItemClickLister() {
                    @Override
                    public void onClick(int whichButton) {

                        mActivity.mIhgBulbWallManager.setupSceneAct(mActivity.mDNADevice, sSceneOptArray[whichButton], new IhgBulbWallManager.IhgBulbCallBack() {
                            @Override
                            public void onResult(String result) {
                                BLToastUtils.show(result);
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity = (IhgBulbWallMainActivity) getActivity();
        initView();
    }

    private void findView(View inflate) {
        mSvCycle = (BLSingleItemView) inflate.findViewById(R.id.sv_cycle);
        mSvCount = (BLSingleItemView) inflate.findViewById(R.id.sv_count);
        mSvInterval = (BLSingleItemView) inflate.findViewById(R.id.sv_interval);
        mSvBrightness = (BLSingleItemView) inflate.findViewById(R.id.sv_brightness);
        mSvScene = (BLSingleItemView) inflate.findViewById(R.id.sv_scene);
    }

    private void initView() {
        final IhgBulbWallMainActivity activity = (IhgBulbWallMainActivity) getActivity();
        try {
            if (activity != null && activity.mIhgBulbInfo!=null) {
                mSvCycle.setValue(activity.mIhgBulbInfo.showParam.loop);
                mSvCount.setValue(String.valueOf(activity.mIhgBulbInfo.showParam.times));
                mSvInterval.setValue(String.valueOf(activity.mIhgBulbInfo.showParam.interval));
                mSvBrightness.setValue(String.valueOf(activity.mIhgBulbInfo.showParam.brightness));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshView(){
        initView();
    }

}
