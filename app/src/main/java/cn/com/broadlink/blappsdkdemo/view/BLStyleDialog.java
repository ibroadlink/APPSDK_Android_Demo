package cn.com.broadlink.blappsdkdemo.view;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.com.broadlink.blappsdkdemo.R;


/**
 * BroadLink 自定义对话框
 * Created by YeJin on 2017/5/4.
 */

public class BLStyleDialog {
//    public static final int BL_BTN_ID_CONFIM = 1;
//
//    public static final int BL_BTN_ID_CANCEL = 2;

    private static Context mContext;
    private static BLStyleDialog mBLStyleDialog;

    private Dialog mDialog;

    private LinearLayout mDialogLayout;

    private TextView mTitleView, mMsgView;

    private Button mConfimBtn, mCancelBtn;

    private LinearLayout mBodyView;

    private View mLineView;

    private OnBLDialogBtnListener mOnConfimBtnListener, mOnCancelBtnListener;

    private BLDialogDismissListener mBLDialogDismissListener;

    private BLStyleDialog(){
        mDialog = new Dialog(mContext, R.style.BLTheme_Dialog);
        initView();

        setListener();
    }

    private static BLStyleDialog initDialog(){
        return new BLStyleDialog();
    }

    public static BLStyleDialog Builder(Context context){
        mContext = context;
        mBLStyleDialog = initDialog();
        return mBLStyleDialog;
    }

    public Dialog show(){
        Window w = mDialog.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        lp.gravity = Gravity.CENTER;
        mDialog.onWindowAttributesChanged(lp);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setCancelable(false);
        mDialog.setContentView(mDialogLayout);
        mDialog.show();
        return mDialog;
    }

    public void dismiss(){
        if(mDialog != null){
            mDialog.dismiss();
        }
    }

    public BLStyleDialog setTitle(String title){
        mTitleView.setVisibility(TextUtils.isEmpty(title) ? View.GONE : View.VISIBLE);
        mTitleView.setText(title);
        return mBLStyleDialog;
    }

    public BLStyleDialog setMessage(String msg){
        mBodyView.setVisibility(View.GONE);
        mMsgView.setVisibility(View.VISIBLE);
        mMsgView.setText(msg);
        return mBLStyleDialog;
    }

    private void initView(){
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDialogLayout = (LinearLayout) inflater.inflate(R.layout.view_bl_custom_alert, null);
        mDialogLayout.setMinimumWidth(10000);

        mBodyView = (LinearLayout) mDialogLayout.findViewById(R.id.body_view);

        mTitleView = (TextView) mDialogLayout.findViewById(R.id.dialog_title);
        mMsgView = (TextView) mDialogLayout.findViewById(R.id.dialog_msg);

        mConfimBtn = (Button) mDialogLayout.findViewById(R.id.dialog_yes);
        mCancelBtn = (Button) mDialogLayout.findViewById(R.id.dialog_no);

        mLineView = mDialogLayout.findViewById(R.id.line_view);
    }

    public BLStyleDialog setView(View view){
        mMsgView.setVisibility(View.GONE);
        mBodyView.setVisibility(View.VISIBLE);
        mBodyView.removeAllViews();
        mBodyView.addView(view);

        return mBLStyleDialog;
    }

    public BLStyleDialog setConfimButton(String text, OnBLDialogBtnListener listener){
        if(text != null){
            mConfimBtn.setText(text);
            mConfimBtn.setVisibility(View.VISIBLE);

            if(mCancelBtn.getVisibility() == View.VISIBLE){
                mLineView.setVisibility(View.VISIBLE);
            }

            mOnConfimBtnListener = listener;
        }
        return mBLStyleDialog;
    }

    public BLStyleDialog setCacelButton(String text, OnBLDialogBtnListener listener){
        if(text != null){
            mCancelBtn.setText(text);
            mCancelBtn.setVisibility(View.VISIBLE);

            if(mConfimBtn.getVisibility() == View.VISIBLE){
                mLineView.setVisibility(View.VISIBLE);
            }

            mOnCancelBtnListener = listener;
        }

        return mBLStyleDialog;
    }

    public BLStyleDialog setConfimButton(String text){
        mConfimBtn.setText(text);
        mConfimBtn.setVisibility(View.VISIBLE);

        if(mCancelBtn.getVisibility() == View.VISIBLE){
            mLineView.setVisibility(View.VISIBLE);
        }
        return mBLStyleDialog;
    }

    public BLStyleDialog setCacelButton(String text){
        mCancelBtn.setText(text);
        mCancelBtn.setVisibility(View.VISIBLE);

        if(mConfimBtn.getVisibility() == View.VISIBLE){
            mLineView.setVisibility(View.VISIBLE);
        }
        return mBLStyleDialog;
    }

    public Button getConfimButton(){
        return mConfimBtn;
    }

    public Button getCancelButton(){
        return mCancelBtn;
    }

    private void setListener(){
        mConfimBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                if(mBLDialogDismissListener == null || mBLDialogDismissListener.confimBtnDismiss()){
                    mDialog.dismiss();
                }

                if(mOnConfimBtnListener != null){
                    mOnConfimBtnListener.onClick(mConfimBtn);
                }
            }
        });

        mCancelBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {

                if(mBLDialogDismissListener == null || mBLDialogDismissListener.cancelBtnDismiss()){
                    mDialog.dismiss();
                }

                if(mOnCancelBtnListener != null){
                    mOnCancelBtnListener.onClick(mCancelBtn);
                }
            }
        });
    }

//    public abstract class BLStyleBtnListener{
//        public void setConfimButton(String text, View.OnClickListener listener){}
//        public void setCacelButton(String text, View.OnClickListener listener){}
//    }

    public interface OnBLDialogBtnListener{
        void onClick(Button btn);
    }

    public BLStyleDialog setBLDialogDismissListener(BLDialogDismissListener dialogDismissListener){
        mBLDialogDismissListener = dialogDismissListener;
        return mBLStyleDialog;
    }

    public static abstract class BLDialogDismissListener{
        public boolean confimBtnDismiss(){return true;}
        public boolean cancelBtnDismiss(){return true;}
    }
}
