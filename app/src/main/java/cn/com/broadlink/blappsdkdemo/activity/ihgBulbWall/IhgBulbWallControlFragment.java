package cn.com.broadlink.blappsdkdemo.activity.ihgBulbWall;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.view.BLSingleItemView;


public class IhgBulbWallControlFragment extends Fragment {
    
    private BLSingleItemView mSvForcolor;
    private BLSingleItemView mSvBackcolor;
    private BLSingleItemView mSvText;
    private BLSingleItemView mSvImg;

    private int mForColor = 0xffffff;
    private int mBackColor = 0x00;
    
    public IhgBulbWallControlFragment() {
        // Required empty public constructor
    }

    public static IhgBulbWallControlFragment newInstance() {
        IhgBulbWallControlFragment fragment = new IhgBulbWallControlFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View inflate = inflater.inflate(R.layout.fragment_ihg_bulb_wall_control, container, false);
        findView(inflate);
        
        
        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void findView(View inflate) {
        mSvForcolor = (BLSingleItemView) inflate.findViewById(R.id.sv_forcolor);
        mSvBackcolor = (BLSingleItemView) inflate.findViewById(R.id.sv_backcolor);
        mSvText = (BLSingleItemView) inflate.findViewById(R.id.sv_text);
        mSvImg = (BLSingleItemView) inflate.findViewById(R.id.sv_img);
    }
    
    private void initView(){
        final String forColor = BLCommonUtils.color2String(mForColor);
        final String backColor = BLCommonUtils.color2String(mBackColor);
        
        mSvForcolor.getRightTextView().setText(forColor);
        mSvForcolor.getRightTextView().setBackgroundColor(Color.parseColor(forColor));
        
        mSvBackcolor.getRightTextView().setText(backColor);
        mSvBackcolor.getRightTextView().setBackgroundColor(Color.parseColor(backColor));
        
        mSvText.setValue("Unset");
        
        mSvImg.setValue("Unset");
    }
    
}
