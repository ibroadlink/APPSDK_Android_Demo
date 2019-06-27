package cn.com.broadlink.blappsdkdemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.com.broadlink.blappsdkdemo.R;

/**
 * 最左边有个ImageView,中间填充TextView,最右边有个ImageView
 * Created by zhaohenghao on 2018/8/14.
 */
public class BLSingleItemView extends LinearLayout {
	private RelativeLayout mLLContent;
	private TextView mTVContent;
	private ImageView mIVLeft;
	private ImageView mIVRight;
	private TextView mTVRight;
	private View mVDivider1, mVDivider2;

	private String mText, mValue;
	private int mTextColor, mValueColor;
	private float mTextSize, mValueSize;
	private int mImgLeft;
	private int mImgRight;
	private int mBackgroud;
	private int mDividerColor;
	private int mValueMaxLine;

	/**
	 * text 显示的位置:默认left
	 * left
	 * center
	 * right
	 * */
	private String mTextGravity;
	/**
	 * item 显示的位置
	 * top
	 * middle
	 * bottom
	 * single 单行
	 * */
	private String mPostion;

	public BLSingleItemView(Context context) {
		super(context);
	}

	public BLSingleItemView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		getAttrs(context, attrs);
	}

	public BLSingleItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		getAttrs(context, attrs);
	}

	private void getAttrs(Context context, AttributeSet attrs){
		LayoutInflater.from(context).inflate(R.layout.view_entry_text,this,true);
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BLItemView);
		mText = array.getString(R.styleable.BLItemView_text);
		mTextColor = array.getColor(R.styleable.BLItemView_textColor, 0);
		mTextSize = array.getDimensionPixelSize(R.styleable.BLItemView_textSize, 0);
		mValue = array.getString(R.styleable.BLItemView_value);
		mValueColor = array.getColor(R.styleable.BLItemView_valueColor, 0);
		mValueSize = array.getDimensionPixelSize(R.styleable.BLItemView_valueSize, 0);
		mImgLeft = array.getResourceId(R.styleable.BLItemView_imgLeft, 0);
		mImgRight = array.getResourceId(R.styleable.BLItemView_imgRight, 0);
		mBackgroud = array.getResourceId(R.styleable.BLItemView_background, 0);
		mDividerColor = array.getResourceId(R.styleable.BLItemView_dividerColor, 0);
		mTextGravity = array.getString(R.styleable.BLItemView_textGravity);
		mPostion = array.getString(R.styleable.BLItemView_position);
		mValueMaxLine = array.getInteger(R.styleable.BLItemView_valueMaxLine, 0);
		array.recycle();
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mLLContent = findViewById(R.id.ll_content);
		mTVContent = findViewById(R.id.tv_content);
		mIVLeft = findViewById(R.id.iv_left);
		mTVRight = findViewById(R.id.iv_right);
		mIVRight = findViewById(R.id.iv_img_right);
		mVDivider1 = findViewById(R.id.v_divider1);
		mVDivider2 = findViewById(R.id.v_divider_2);

		if (mBackgroud != 0){
			mLLContent.setBackgroundResource(mBackgroud);
		}

		if (mValue != null){
			mTVRight.setText(mValue);
		}

		mTVRight.setTextSize(TypedValue.COMPLEX_UNIT_PX, mValueSize);

		if (mText != null){
			mTVContent.setText(mText);
		}

		setTexColor(mTextColor);

		setValueColor(mValueColor);

		if(mValueMaxLine != 0){
			mTVRight.setSingleLine(mValueMaxLine == 1 ? true : false);
			mTVRight.setMaxLines(mValueMaxLine);
		}

		mTVContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);

		mVDivider1.setBackgroundResource(mDividerColor);
		mVDivider2.setBackgroundResource(mDividerColor);

		if(mTextGravity != null){
			if(mTextGravity.equals("right")){
				mTVContent.setGravity(Gravity.RIGHT);
				mTVRight.setVisibility(View.VISIBLE);
			}else if(mTextGravity.equals("center")){
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mTVContent.getLayoutParams();
				params.width = LayoutParams.MATCH_PARENT;
				params.leftMargin = 0;
				mTVContent.setLayoutParams(params);
				mTVContent.setGravity(Gravity.CENTER);
				mTVRight.setVisibility(View.GONE);
			}else{
				mTVContent.setGravity(Gravity.LEFT);
				mTVRight.setVisibility(View.VISIBLE);
			}
		}

		if(mPostion != null){
			if(mPostion.equals("top")){
				mVDivider1.setVisibility(View.GONE);
				mVDivider2.setVisibility(View.VISIBLE);
			}else if(mPostion.equals("bottom")){
				mVDivider1.setVisibility(View.GONE);
				mVDivider2.setVisibility(View.GONE);

				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mVDivider2.getLayoutParams();
				layoutParams.leftMargin = 0;
				mVDivider2.setLayoutParams(layoutParams);
			}else if(mPostion.equals("middle")){
				mVDivider1.setVisibility(View.GONE);
				mVDivider2.setVisibility(View.VISIBLE);
			}else{
				mVDivider1.setVisibility(View.GONE);
				mVDivider2.setVisibility(View.GONE);
				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mVDivider2.getLayoutParams();
				layoutParams.leftMargin = 0;
				mVDivider2.setLayoutParams(layoutParams);
			}
		}else{
			mVDivider1.setVisibility(View.GONE);
			mVDivider2.setVisibility(View.GONE);
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mVDivider2.getLayoutParams();
			layoutParams.leftMargin = 0;
			mVDivider2.setLayoutParams(layoutParams);
		}

		if (mImgLeft != 0){
			mIVLeft.setImageResource(mImgLeft);
			mIVLeft.setVisibility(View.VISIBLE);
		} else {
			mIVLeft.setVisibility(View.GONE);
		}

		setRightImg(mImgRight);
	}

	public void setValue(String text){
		mTVRight.setText(text);
	}

	public void setValue(int textResid){
		mTVRight.setText(textResid);
	}

	public void setValueColor(int color){
		if (color != 0){
			mTVRight.setTextColor(color);
		}
	}

	public String getValue(){
		return  mTVRight.getText().toString();
	}

	public void setText(String text){
		mTVContent.setText(text);
	}

	public void setText(int resId){
		mTVContent.setText(resId);
	}

	public String getText(){
		return mTVContent.getText().toString();
	}

	public void setTexColor(int color){
		if (color != 0){
			mTVContent.setTextColor(color);
		}
	}

	public void setTextSize(float size){
		mTVContent.setTextSize(size);
	}

	public void setBackgroud(int resId){
		mLLContent.setBackgroundResource(resId);
	}

	public void setLeftImg(int resId){
		mIVLeft.setImageResource(resId);
	}

	public void setRightImg(int resId){
		mTVRight.setCompoundDrawablesWithIntrinsicBounds(0,0, resId,0);
	}

	public ImageView getRightContentImg(){
		return mIVRight;
	}
	
	public TextView getLeftTextView(){
		return mTVContent;
	}
	
	public TextView getRightTextView(){
		return mTVRight;
	}

	public void setRightArrowVisibility(boolean visibility){
		mTVRight.setCompoundDrawablesWithIntrinsicBounds(0,0, visibility ? mImgRight : 0,0);
	}

}
