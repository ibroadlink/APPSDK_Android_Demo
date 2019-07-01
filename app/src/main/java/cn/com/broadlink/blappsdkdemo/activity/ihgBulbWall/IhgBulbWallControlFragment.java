package cn.com.broadlink.blappsdkdemo.activity.ihgBulbWall;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.wildma.pictureselector.PictureSelector;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.BLColorPaletteView;
import cn.com.broadlink.blappsdkdemo.view.BLSingleItemView;
import cn.com.broadlink.blappsdkdemo.view.BLStyleDialog;
import cn.com.broadlink.blappsdkdemo.view.InputTextView;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;


public class IhgBulbWallControlFragment extends Fragment {

    private BLSingleItemView mSvForcolor;
    private BLSingleItemView mSvBackcolor;
    private BLSingleItemView mSvText;
    private BLSingleItemView mSvImg;
    private LayoutInflater mInflater;
    private IhgBulbWallMainActivity mActivity;
    private int mForColor = 0xffffff;
    private int mBackColor = 0x000000;
    private String mText;
    private Font16 mFont16;
    private Bitmap mImg = null;
    private Dialog mDialog = null;

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
        mInflater = inflater;
        
        findView(inflate);

        setListener();

        return inflate;
    }

    private void setListener() {
        mSvText.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                BLAlert.showEditDilog(mActivity, "input a letter or Hanzi", mText, new BLAlert.BLEditDialogOnClickListener() {
                    @Override
                    public void onClink(final String value) {
                        mText = String.valueOf(value.charAt(0));
                        sendText();
                    }

                    @Override
                    public void onCancel(String value) {

                    }
                }, false);
            }
        });


        mSvForcolor.setOnClickListener(new OnSingleClickListener() {

            @Override
            public void doOnClick(View v) {
                showColorDialog(true);
            }
        });

        mSvBackcolor.setOnClickListener(new OnSingleClickListener() {

            @Override
            public void doOnClick(View v) {
                showColorDialog(false);
            }
        });

        mSvImg.setOnClickListener(new OnSingleClickListener() {

            @Override
            public void doOnClick(View v) {
                PictureSelector.create(mActivity, PictureSelector.SELECT_REQUEST_CODE).selectPicture(true, 16, 16, 1, 1);
            }
        });
    }

    private void showColorDialog(final boolean isForColor) {
        final View view = mInflater.inflate(R.layout.dialog_color_choose, null);

        final InputTextView mEtCmd = (InputTextView) view.findViewById(R.id.et_cmd);
        Button mBtCommit = (Button) view.findViewById(R.id.bt_commit);
        final BLColorPaletteView mVColorForeground = (BLColorPaletteView) view.findViewById(R.id.v_color_foreground);

        mEtCmd.setText(BLCommonUtils.color2String(isForColor ? mForColor : mBackColor));
        mVColorForeground.setColor(BLCommonUtils.parseColor(isForColor ? mForColor : mBackColor));

        mBtCommit.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                final String colorStr = mEtCmd.getTextString();
                if (!TextUtils.isEmpty(colorStr)) {

                    try {
                        final int color = Color.parseColor(colorStr) & 0xffffff;
                        if (isForColor) {
                            mForColor = color;
                        } else {
                            mBackColor = color;
                        }

                        if (!TextUtils.isEmpty(mText)) {
                            sendText();
                        } else {
                            initView();
                        }
                        if(mDialog != null){
                            mDialog.dismiss();
                        }
                    } catch (Exception e) {
                        BLToastUtils.show("Color format should be '#ffaabb'");
                        e.printStackTrace();
                    }
                }
            }
        });

        mVColorForeground.setOnColorChangerListener(new BLColorPaletteView.ColorChangeListener() {
            @Override
            public void colorChange(int color) {
                mEtCmd.setText(BLCommonUtils.color2String(color & 0xffffff));
            }
        });

        mDialog = BLAlert.showAlert(mActivity, "Setup Color", view, new BLStyleDialog.OnBLDialogBtnListener() {
            @Override
            public void onClick(Button btn) {

                if (isForColor) {
                    mForColor = mVColorForeground.getColorInt();
                } else {
                    mBackColor = mVColorForeground.getColorInt();
                }

                if (!TextUtils.isEmpty(mText)) {
                    sendText();
                } else {
                    initView();
                }
            }
        });
    }

    private void sendText() {
        final byte[][] color = mFont16.resolveString(mText, false);

        mActivity.mIhgBulbInfo.rgblist.clear();
        for (int j = 0; j < IhgBulbWallConstants.BULB_COUNT; j++) {

            if ((color[j / 16][j % 16] == 1)) { // 前景
                mActivity.mIhgBulbInfo.rgblist.add(BLCommonUtils.color2StringNumber(mForColor));
            } else { // 背景
                mActivity.mIhgBulbInfo.rgblist.add(BLCommonUtils.color2StringNumber(mBackColor));
            }
        }
        doSendData(IhgBulbWallConstants.OPT_CAT.STREAM);
    }

    private void doSendData(int act) {
        mActivity.mIhgBulbWallManager.setupScene(mActivity.mDNADevice, act, mActivity.mIhgBulbInfo.maclist, mActivity.mIhgBulbInfo.rgblist,
                new IhgBulbWallManager.IhgBulbCallBack() {
            @Override
            public void onResult(String result) {
                BLToastUtils.show(result);
                initView();
                mActivity.onMacOrRgbListChanged();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity = (IhgBulbWallMainActivity) getActivity();
        mFont16 = new Font16(mActivity);
        initView();
    }

    private void findView(View inflate) {
        mSvForcolor = (BLSingleItemView) inflate.findViewById(R.id.sv_forcolor);
        mSvBackcolor = (BLSingleItemView) inflate.findViewById(R.id.sv_backcolor);
        mSvText = (BLSingleItemView) inflate.findViewById(R.id.sv_text);
        mSvImg = (BLSingleItemView) inflate.findViewById(R.id.sv_img);
    }

    private void initView() {
        final String forColor = BLCommonUtils.color2String(mForColor);
        final String backColor = BLCommonUtils.color2String(mBackColor);

        mSvForcolor.getRightTextView().setText(forColor);
        mSvForcolor.getRightTextView().setBackgroundColor(Color.parseColor(forColor));

        mSvBackcolor.getRightTextView().setText(backColor);
        mSvBackcolor.getRightTextView().setBackgroundColor(Color.parseColor(backColor));

        mSvText.setValue(TextUtils.isEmpty(mText) ? "Unset" : mText);
        if (mImg == null) {
            mSvImg.setValue("Unset");
        } else {
            mSvImg.setValue("");
            mSvImg.getRightContentImg().setVisibility(View.VISIBLE);
            mSvImg.getRightContentImg().setImageBitmap(mImg);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*结果回调*/
        if (requestCode == PictureSelector.SELECT_REQUEST_CODE) {
            if (data != null) {
                String picturePath = data.getStringExtra(PictureSelector.PICTURE_PATH);

                int[] imgRgb = new int[IhgBulbWallConstants.BULB_COUNT];
                
             /*   final Bitmap img = BitmapFactory.decodeFile(picturePath);
                int imgWidth = img.getWidth();
                int imgHeight = img.getHeight();
                Matrix matrix=new Matrix();
                final float scaleWidth = 16f / imgWidth;
                final float scaleHeight = 16f / imgHeight;
                matrix.postScale(scaleWidth, scaleHeight);
                mImg = Bitmap.createBitmap(img, 0, 0, imgWidth, imgHeight, matrix, false);
                mImg.getPixels(imgRgb,0,16,0,0,16,16);
            */
                mImg = BitmapFactory.decodeFile(picturePath);
                mImg.getPixels(imgRgb, 0, 16, 0, 0, 16, 16);
                
                mActivity.mIhgBulbInfo.rgblist.clear();
                for (int item : imgRgb) {
                    mActivity.mIhgBulbInfo.rgblist.add(BLCommonUtils.color2StringNumber(item & 0xffffff));
                }
                doSendData(IhgBulbWallConstants.OPT_CAT.IMAGE);
            }
        }
    }
}
