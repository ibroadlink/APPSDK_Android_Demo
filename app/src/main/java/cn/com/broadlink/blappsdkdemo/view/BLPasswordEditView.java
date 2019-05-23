package cn.com.broadlink.blappsdkdemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cn.com.broadlink.blappsdkdemo.R;

/**
 * 作者：EchoJ on 2018/1/12 11:19 <br>
 * 邮箱：echojiangyq@gmail.com <br>
 * 描述：密码输入框，带眼睛
 */

public class BLPasswordEditView extends LinearLayout {
  
    private EditText metPwd;
    private ImageView mivEye;

    private String mHint = null;
    private int mTextColor = -1;
    private int mTextMaxLen = -1;
    private float mTextSize = -1;
    private Drawable mLeftImg = null;
    
  public interface BLOnTextChangedListener {
       void onTextChanged(Editable s);
    }

    private BLOnTextChangedListener mTextChangeListener;
    
    public BLPasswordEditView(Context context) {
        super(context);
        initView(context, null);
    }

    public BLPasswordEditView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public BLPasswordEditView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }
    
    private void initView(Context context, @Nullable AttributeSet attrs){
        LayoutInflater.from(context).inflate(R.layout.view_password_edit,this,true);
        
        if(attrs == null) return;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BLPasswordEditView);
        mHint = typedArray.getString(R.styleable.BLPasswordEditView_pwdHint);
        mTextSize = typedArray.getDimension(R.styleable.BLPasswordEditView_pwdTextSize, 16);
        mTextMaxLen = typedArray.getInt(R.styleable.BLPasswordEditView_pwdTextMaxLen, 100);
        mTextColor = typedArray.getColor(R.styleable.BLPasswordEditView_pwdTextColor, 0);
        mLeftImg = typedArray.getDrawable(R.styleable.BLPasswordEditView_pwdHeftImg);
        typedArray.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        metPwd = (EditText) findViewById(R.id.password_view);
        mivEye = (ImageView) findViewById(R.id.password_eye_view);

        if(mHint != null){
            metPwd.setHint(mHint);
        }
        
        if(mTextSize > 0){
            metPwd.setTextSize(mTextSize);
        }
        
        if(mTextColor > 0){
            metPwd.setTextColor(mTextColor);
        }
        
        if(mLeftImg != null){
            metPwd.setCompoundDrawablesWithIntrinsicBounds(mLeftImg, null, null, null);
        }

        if(mTextMaxLen > 0){
            metPwd.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mTextMaxLen)});
        }

        metPwd.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mivEye.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                if(mTextChangeListener != null){
                    mTextChangeListener.onTextChanged(s);
                }
            }
        });

        mivEye.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = String.valueOf(mivEye.getTag());
                if (tag == null || tag.equals("1")) {
                    mivEye.setImageResource(R.drawable.login_eye_close);
                    metPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mivEye.setTag(0);
                } else {
                    mivEye.setImageResource(R.drawable.login_eye);
                    metPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mivEye.setTag(1);
                }
                metPwd.setSelection(metPwd.length());
            }
        });
    }

    public EditText getEtPwd() {
        return metPwd;
    }

    public ImageView getIvEye() {
        return mivEye;
    }
    
    public void setOnTextChangeListener(BLOnTextChangedListener listener){
        mTextChangeListener = listener;
    }
}
