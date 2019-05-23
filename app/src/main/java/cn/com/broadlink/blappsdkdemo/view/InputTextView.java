package cn.com.broadlink.blappsdkdemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;

/**
 * 带删除按钮输入框
 * Project: BLEControlAppV4</p>
 * Title: InputTextView</p>
 * Company: BroadLink</p> 
 * @author YeJing
 * @date 2015-12-10
 */
public class InputTextView extends LinearLayout {
    private EditText mEditText;
    private String mEditHint;
    private String mEditValue;
    private TextWatcher mTextWatcher;
    
    public InputTextView(Context context) {
        super(context);
        init(context);
    }

    public InputTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttrs(context,attrs);
        init(context);
    }

    public InputTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getAttrs(context,attrs);
        init(context);
    }

    private void getAttrs(Context context, AttributeSet attrs){
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.InputTextView);
        mEditHint = array.getString(R.styleable.InputTextView_hint);
        mEditValue = array.getString(R.styleable.InputTextView_text);
    }

    private void init(Context context) {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        mEditText = new EditText(context);
        mEditText.setBackgroundResource(0);
        mEditText.setGravity(Gravity.CENTER_VERTICAL);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        layoutParams.weight = 1;
        layoutParams.setMargins(0,16,0,0);
        mEditText.setTextColor(Color.rgb(108, 109, 104));
        //mEditText.setSingleLine();
        mEditText.setLayoutParams(layoutParams);
        mEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        if(mEditHint != null && !mEditHint.equals(""))
            mEditText.setHint(mEditHint);
        if(mEditValue != null && !mEditValue.equals(""))
            mEditText.setText(mEditValue);
        addView(mEditText);

        final ImageView deleteButton = new ImageView(context);
        deleteButton.setImageResource(R.drawable.btn_editor_delete);
        deleteButton.setScaleType(ScaleType.CENTER_INSIDE);
        LayoutParams deleteButtonLayoutParams = new LayoutParams(BLCommonUtils.dip2px(getContext(),25), LayoutParams.FILL_PARENT);
        deleteButtonLayoutParams.weight = 0;
        deleteButton.setLayoutParams(deleteButtonLayoutParams);
        deleteButton.setVisibility(View.GONE);
        addView(deleteButton);

        deleteButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mEditText.setText(null);
            }
        });

        mEditText.addTextChangedListener(new TextWatcher() {

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
                if (s.length() > 0) {
                    deleteButton.setVisibility(View.VISIBLE);
                } else {
                    deleteButton.setVisibility(View.GONE);
                }
                if(mTextWatcher != null){
                    mTextWatcher.afterTextChanged(s);
                }
            }
        });
    }

    public void addTextChangedListener(TextWatcher watcher){
        mTextWatcher = watcher;
    }
    
    /**
     * 设置输入提示内容
     * @param hint
     */
    public void setTextHint(String hint){
        if(mEditText != null){
            mEditText.setHint(hint);
        }
    }
    
    /**
     * 设置输入提示内容
     * @param hintId
     */
    public void setTextHint(int hintId){
        if(mEditText != null){
            mEditText.setHint(hintId);
        }
    }
    
    /**
     * 设置输入框的内容
     * @param text
     */
    public void setText(String text){
        if(mEditText != null && text != null){
            mEditText.setText(text);
            mEditText.setSelection(text.length());
        }
    }
    
    /**
     * 设置最大输入长度
     * @param lenght
     */
    public void setMaxLength(int lenght){
    	if(mEditText != null){
    	 	mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(lenght)});
    	}
    }
    
    /**
     * 设置字体颜色
     * @param color
     */
    public void setTextColor(int color){
    	if(mEditText != null){
    		mEditText.setTextColor(color);
    	}
    }
    
    /**
     * 获取输入框的内容
     * @return String
     */
    public String getTextString(){
        if(mEditText != null){
            return mEditText.getText().toString();
        }
        return null;
    }
    
    public EditText getEditText(){
        return mEditText;
    }
}
