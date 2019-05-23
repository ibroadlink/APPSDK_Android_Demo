package cn.com.broadlink.blappsdkdemo.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import cn.com.broadlink.blappsdkdemo.R;


/**
 * 带倒计时的钮输入框
 * Project: BLEControlAppV4</p>
 * Title: InputTextView</p>
 * Company: BroadLink</p> 
 */
public class BLInputCountdownView extends LinearLayout {
    private EditText mEtContent;
    private TextView mTvRight;
    private LinearLayout mLlContent;
    private String mEditHint;
    private TextWatcher mTextWatcher;
    private boolean mIsLightTheme;
    private int mBackgroud;
    private int mTextColor = -1;
    private int mTextMaxLen = -1;
    private int mCountDown = -1;
    private float mTextSize = -1;
    private Timer mTimer = new Timer();
    private volatile int mCountDownFlag = -1;

    private OnVisibleChangeListener mCallback;
    private OnSingleClickListener mReSendListener;

    public interface OnVisibleChangeListener{
        void onCallback(boolean isVisible);
    }

    public BLInputCountdownView(Context context) {
        super(context);
    }

    public BLInputCountdownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttrs(context,attrs);
    }

    public BLInputCountdownView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getAttrs(context,attrs);
    }

    private void getAttrs(Context context, AttributeSet attrs){
        LayoutInflater.from(context).inflate(R.layout.view_count_down,this,true);
        
        TypedArray array = context.obtainStyledAttributes(attrs,R.styleable.BLInputCountdownView);
        mEditHint = array.getString(R.styleable.BLInputCountdownView_hint);
        mIsLightTheme = array.getBoolean(R.styleable.BLInputCountdownView_light, false);
        mTextSize = array.getDimension(R.styleable.BLInputCountdownView_textSize, 16);
        mTextMaxLen = array.getInt(R.styleable.BLInputCountdownView_textMaxLen, 100);
        mCountDown = array.getInt(R.styleable.BLInputCountdownView_countDown, 60);
        mTextColor = array.getColor(R.styleable.BLInputCountdownView_textColor, 0);
        mBackgroud = array.getResourceId(R.styleable.BLInputCountdownView_background, 0);
        array.recycle();
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mEtContent = findViewById(R.id.et_pwd);
        mTvRight = findViewById(R.id.tv_right);
        mLlContent = findViewById(R.id.ll_content);

        mEtContent.setHintTextColor(getResources().getColor(mIsLightTheme ? R.color.title_bar_line : R.color.textGray));
        
        if(mBackgroud>0){
            mLlContent.setBackgroundResource(mBackgroud);
        }

        if(mEditHint != null){
            mEtContent.setHint(mEditHint);
        }

        if(mTextSize > 0){
            mEtContent.setTextSize(mTextSize);
        }

        if(mTextColor > 0){
            mEtContent.setTextColor(mTextColor);
        }else{
            mEtContent.setTextColor(getResources().getColor(mIsLightTheme ? R.color.textDark : R.color.textLight));
        }

        if(mTextMaxLen > 0){
            mEtContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mTextMaxLen)});
        }

        mEtContent.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(mTextWatcher != null){
                    mTextWatcher.onTextChanged(s, start, before, count);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(mTextWatcher != null){
                    mTextWatcher.beforeTextChanged(s, start, count, after);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(mTextWatcher != null){
                    mTextWatcher.afterTextChanged(s);
                }

                if(mCallback != null){
                    mCallback.onCallback(s.length() > 0);
                }
            }
        });

        mTvRight.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(mCountDownFlag >= 0)
                    return;

//               if(mCountDownFlag==0){
//                   mTvRight.setText(null);
//                   startCount();
//               }

               if(mReSendListener != null){
                   mReSendListener.doOnClick(mTvRight);
               }
            }
        });
    }
    
    public void startCount(){
        if(!(getContext() instanceof Activity)){
            return;
        }
        
        final Activity activity = (Activity) getContext();
        if(mTimer == null){
            mTimer = new Timer();
        }
       
        mCountDownFlag = mCountDown;
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                activity.runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        if(mCountDownFlag == mCountDown){
                            mTvRight.setTextColor(activity.getResources().getColor(R.color.common_hint_text));
                        }else if(mCountDownFlag < 0){
                            mTvRight.setTextColor(activity.getResources().getColor(R.color.btn_theme_yellow_selector));
                            mTvRight.setText(R.string.common_account_signup_second);
                            mTimer.cancel();
                            mTimer = null;
                            return;
                        }
                        mTvRight.setText(mCountDownFlag + "s");
                        mCountDownFlag--;
                    }
                });
            }
        },0,1000);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mTimer !=null){
            mTimer.cancel();
            mTimer = null;  
        }
    }

    public void setHint(String hint){
        mEtContent.setHint(hint);
    }

    public TextView getRightTextView() {
        return mTvRight;
    }
    
    public EditText getEditText(){
        return mEtContent;
    }

    public void setVisiableListener(OnVisibleChangeListener callback){
        mCallback = callback;
    }
    
    public void setOnReSendClickListener(OnSingleClickListener listener){
        mReSendListener = listener;
    }

    public String getText(){
        return mEtContent.getText()==null ? null : mEtContent.getText().toString();
    }

}
