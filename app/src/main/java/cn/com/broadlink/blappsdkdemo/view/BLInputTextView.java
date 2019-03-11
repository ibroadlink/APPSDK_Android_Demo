package cn.com.broadlink.blappsdkdemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;

/**
 * 带删除的输入框、带眼睛的密码框
 */
public class BLInputTextView extends LinearLayout {
    private EditText mEtContent;
    private ImageView mIvDel;
    private LinearLayout mLlContent;
    private String mEditHint;

//    private boolean mIsLightTheme;
    private boolean mIsPwdType;

    private int mPaddingLeft = -1;
    private int mBackgroud;
    private int mTextColor = -1;
    private int mTextColorHint = -1;
    private int mTextMaxLen = -1;
    private float mTextSize = -1;
    
    private OnContentChangedListener mOnContentChangeListener;

    private TextChangedListener mTextChangedListener;

    public BLInputTextView(Context context) {
        super(context);
    }

    public BLInputTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttrs(context,attrs);
    }

    public BLInputTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getAttrs(context,attrs);
    }

    private void getAttrs(Context context, AttributeSet attrs){
        LayoutInflater.from(context).inflate(R.layout.view_edit_text,this,true);
        
        TypedArray array = context.obtainStyledAttributes(attrs,R.styleable.BLInputTextView);
        mEditHint = array.getString(R.styleable.BLInputTextView_hint);
//        mIsLightTheme = array.getBoolean(R.styleable.BLInputTextView_light, false);
        mTextSize = array.getDimension(R.styleable.BLInputTextView_textSize, BLCommonUtils.dip2px(context,16));
        mTextMaxLen = array.getInt(R.styleable.BLInputTextView_textMaxLen, 100);
        mTextColor = array.getColor(R.styleable.BLInputTextView_textColor, 0);
        mBackgroud = array.getResourceId(R.styleable.BLInputTextView_background, 0);
        mIsPwdType = array.getBoolean(R.styleable.BLInputTextView_isPwdType, false);
        mPaddingLeft = array.getDimensionPixelSize(R.styleable.BLInputTextView_paddingLeft,-1);
        mTextColorHint = array.getColor(R.styleable.BLInputTextView_textColorHint, 0);
        array.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mEtContent = findViewById(R.id.et_pwd);
        mIvDel = findViewById(R.id.iv_eye);
        mLlContent = findViewById(R.id.ll_content);

        
        if(mBackgroud != 0 ){
            mLlContent.setBackgroundResource(mBackgroud);
        }

        if(mEditHint != null){
            mEtContent.setHint(mEditHint);
        }

        if(mTextSize != 0){
            mEtContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        }

        if(mPaddingLeft != -1){
            mEtContent.setPadding(mPaddingLeft, 0 , 0, 0);
        }

        if(mTextColor != 0){
            mEtContent.setTextColor(mTextColor);
        }else{
            mEtContent.setTextColor(getResources().getColor(R.color.common_text));
        }

        if(mTextColorHint != 0){
            mEtContent.setHintTextColor(mTextColorHint);
        }

        setTextMaxLength(mTextMaxLen);

        if(mIsPwdType){
            mEtContent.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mIvDel.setVisibility(View.VISIBLE);
            mIvDel.setTag(1);
        }

        mIvDel.setImageResource(mIsPwdType ? R.drawable.icon_input_invisible_white : R.drawable.icon_input_del);
        mEtContent.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mIvDel.setVisibility((s.length() > 0 || mIsPwdType ) ? View.VISIBLE : View.GONE);

                if(mOnContentChangeListener != null){
                    mOnContentChangeListener.onChanged(s.length() > 0);
                }

                if(mTextChangedListener != null){
                    mTextChangedListener.afterTextChanged(s);
                }
            }
        });

        mIvDel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(mIsPwdType){
                    String tag = String.valueOf(mIvDel.getTag());
                    if (tag == null || tag.equals("1")) {
                        mIvDel.setImageResource(R.drawable.icon_input_invisible);
                        mEtContent.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        mIvDel.setTag(0);
                    } else {
                        mIvDel.setImageResource(R.drawable.icon_input_invisible_white);
                        mEtContent.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        mIvDel.setTag(1);
                    }
                    mEtContent.setSelection(mEtContent.length());
                }else{
                    mEtContent.setText(null);
                }
            }
        });
    }

    public void setTextMaxLength(int textMaxLen){
        if(textMaxLen > 0){
            mEtContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(textMaxLen)});
        }
    }

    public void addContentChangedListener(OnContentChangedListener callback){
        mOnContentChangeListener = callback;
    }

    public void addTextChangedListener(TextChangedListener textChangedListener){
        mTextChangedListener = textChangedListener;
    }

    public void setHint(String hint){
        mEtContent.setHint(hint);
    }

    public void setText(String text){
         mEtContent.setText(text);
         if(text != null){
             try {
                 mEtContent.setSelection(text.length());
             }catch (Exception e){}
         }
    }

    public String getText(){
       return mEtContent.getText()==null ? null : mEtContent.getText().toString();
    }

    public EditText getEditText(){
        return mEtContent;
    }
    
    public interface OnContentChangedListener{
        void onChanged(boolean notEmpty);
    }

    public interface TextChangedListener{
        void afterTextChanged(Editable editable);
    }

}
