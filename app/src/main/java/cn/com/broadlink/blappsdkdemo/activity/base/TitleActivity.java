package cn.com.broadlink.blappsdkdemo.activity.base;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLSettings;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.blappsdkdemo.view.OnSingleItemClickListener;


public class TitleActivity extends BaseActivity {
    private FrameLayout mBody;
    protected FrameLayout mContentLayout;
    private RelativeLayout mTitleLayout;
    protected ListView mMoreListView;
    private Button mLeftButton;
    private Button mRightButton;
    private TextView mTitle, mViceTiteView;
    private View mMoreShadowView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.view_title_layout);

        findView();

        setListener();
        
        setTitleBackgroundColor(getResources().getColor(R.color.title_bar_color));

        init();
    }

    @Override
    public void setContentView(int layoutResID) {
        getLayoutInflater().inflate(layoutResID, mBody, true);
    }
    
    private void findView() {
        mMoreShadowView = findViewById(R.id.more_shadow_view);

        mBody = (FrameLayout) findViewById(R.id.body);
        mContentLayout = (FrameLayout) findViewById(R.id.content_layout);
        mTitleLayout = (RelativeLayout) findViewById(R.id.title_layout);
        mMoreListView = (ListView) findViewById(R.id.more_list_view);
        
        mLeftButton = (Button) findViewById(R.id.btn_left);
        mRightButton = (Button) findViewById(R.id.btn_right);

        mTitle = (TextView) findViewById(R.id.title_view);
        mViceTiteView = (TextView) findViewById(R.id.vice_title_view);
    }
    
    private void setListener(){
        mLeftButton.setOnClickListener(new OnSingleClickListener() {
            
            @Override
            public void doOnClick(View v) {
                back();
            }
        });
    }
    
    private void init(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
			LayoutParams moreParams = (LayoutParams) mMoreListView.getLayoutParams();
			moreParams.topMargin = moreParams.topMargin + BLSettings.STATUS_HEIGHT;
			mMoreListView.setLayoutParams(moreParams);
        }
    }

    /**
     * 设置标题的透明度
     * <br>Specify an alpha value for the drawable. 0 means fully transparent, and 255 means fully opaque.
     * @param alpha
     *          透明度的值[0-255]
     */
    public void setTitleAlpha(int alpha){
        mTitleLayout.getBackground().setAlpha(alpha);
    }


    /**
     * 设置标题颜色
     * @param titleColor
     *          标题颜色
     */
    public void setTitleTextColor(int titleColor){
        mTitle.setTextColor(titleColor);
    }

    /**
     * setTitle(设置标题 和 标题监听事件)
     *
     * @param title
     * @param onSingleClickListener
     */
    public void setTitle(String title, OnSingleClickListener onSingleClickListener) {
        mTitle.setVisibility(View.VISIBLE);
        mTitle.setText(title);
        mTitle.setOnClickListener(onSingleClickListener);
    }

    /***
     * 设置副标题和字体白色
     * @param title
     */
    public void setViceTitle(String title) {
        setViceTitle(title, Color.WHITE);
    }

    /***
     * 设置副标题和字体颜色
     * @param title
     * @param titleColor
     */
    public void setViceTitle(String title, int titleColor) {
        mViceTiteView.setVisibility(TextUtils.isEmpty(title) ? View.GONE : View.VISIBLE);
        mViceTiteView.setText(title);
        mViceTiteView.setTextColor(titleColor);
    }

    /**
     * 设置标题的左右上下图片
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public void setTitleDrawable(int left, int top, int right, int bottom) {
        mTitle.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
    }

    /**
     * 设置标题名称
     * @param title
     *          标题名称
     * @param titleColor
     *          标题颜色
     */
    public void setTitle(String title, int titleColor){
        mTitle.setVisibility(View.VISIBLE);
        mTitle.setText(title);
       if(titleColor != 0) mTitle.setTextColor(titleColor);
    }

    /**
     * 设置标题名称
     * @param titleId
     *          标题名称
     * @param titleColor
     *          标题颜色
     */
    public void setTitle(int titleId, int titleColor){
        mTitle.setVisibility(View.VISIBLE);
        mTitle.setText(titleId);
        if(titleColor != 0) mTitle.setTextColor(titleColor);
    }

    /**
     * 设置标题名称
     * @param title
     * title字体颜色默认白色
     */
    public void setTitle(String title){
        setTitle(title, 0);
    }

    /**
     * 设置标题名称
     * @param titleId
     * title字体颜色默认白色
     */
    public void setTitle(int titleId){
        setTitle(titleId, 0);
    }

    /**
     * 设置标题背景
     * @param resid
     */
    public void setTitleBackgroundResource(int resid){
//        mSystemBarTintManager.setStatusBarTintDrawable(getResources().getDrawable(resid));
        mTitleLayout.setBackgroundResource(resid);
    }

    /**
     * 设置标题背景
     * @param color
     */
    public void setTitleBackgroundColor(int color){
//        mSystemBarTintManager.setStatusBarTintColor(color);
        mTitleLayout.setBackgroundColor(color);
    }

    /**
     * 显示左边返回按钮
     * <br> color
     *          按钮颜色#FFF
     * <br> icon
     *          NULL
     * @param textId
     *          按钮名称
     */
    public void setBackVisible(int textId){
        setBackVisible(textId, 0, 0);
    }


    /**
     * 显示左边返回按钮
     * 按钮图标为白色
     */
    public void setBackWhiteVisible(){
        setBackVisible(0, 0, R.drawable.btn_back_white);
    }

    /**
     * 设置返回按键是否显示
     * @param visibility
     */
    public void setBackVisibility(int visibility){
        mLeftButton.setVisibility(visibility);
    }

    /**
     * 显示左边返回按钮
     * <br>按钮颜色-白色
     * <br>按钮图标-白色
     * @param textId
     *          按钮名称
     */
    public void setBackWhitVisible(int textId){
        mLeftButton.setVisibility(View.VISIBLE);
        if(textId > 0) mLeftButton.setText(textId);
        mLeftButton.setTextColor(Color.WHITE);
        mLeftButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_back_white, 0, 0, 0);
    }

    /**
     * 显示左边返回按钮
     * @param textId
     *          按钮名称
     * @param textColor
     *          按钮颜色
     * @param resid
     *         按钮图片
     */
    public void setBackVisible(int textId, int textColor, int resid){
        mLeftButton.setVisibility(View.VISIBLE);
        if(textId > 0) mLeftButton.setText(textId);
        if(textColor != 0) mLeftButton.setTextColor(textColor);
        mLeftButton.setCompoundDrawablesWithIntrinsicBounds(resid, 0, 0, 0);
    }

    /**
     * 显示左边返回按钮
     * @param text
     *          按钮名称
     * @param textColor
     *          按钮颜色
     * @param resid
     *         按钮图片
     */
    public void setBackVisible(String text, int textColor, int resid){
        mLeftButton.setVisibility(View.VISIBLE);
        mLeftButton.setText(text);
        if(textColor != 0) mLeftButton.setTextColor(textColor);
        mLeftButton.setCompoundDrawablesWithIntrinsicBounds(resid, 0, 0, 0);
    }

    public void setRightButtonGone(){
        mRightButton.setVisibility(View.GONE);
    }

    public void setRightVisibility(int visibility){
        mRightButton.setVisibility(visibility);
    }


    /**
     * 设置左边按钮显示事件
     * @param textId
     *          按钮名称
     * @param textColor
     *          按钮颜色
     * @param resid
     *         按钮图片
     * @param onSingleClickListener
     *          按钮事件
     */
    public void setLeftButtonOnClickListener(int textId, int textColor,
            int resid, OnSingleClickListener onSingleClickListener){
        mLeftButton.setVisibility(View.VISIBLE);
        if(textId > 0) mLeftButton.setText(textId);
        if(textColor != 0) mLeftButton.setTextColor(textColor);
        mLeftButton.setCompoundDrawablesWithIntrinsicBounds(resid, 0, 0, 0);

        if(onSingleClickListener != null){
            mLeftButton.setOnClickListener(onSingleClickListener);
        }
    }

    public void setLeftButtonOnClickListener(String text, int textColor, Drawable drawable, OnSingleClickListener onSingleClickListener){
        mLeftButton.setVisibility(View.VISIBLE);
        mLeftButton.setText(text);

        if(textColor == 0) mLeftButton.setTextColor(Color.WHITE); else mLeftButton.setTextColor(textColor);

        mLeftButton.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);

        if(onSingleClickListener != null) mLeftButton.setOnClickListener(onSingleClickListener);
    }

    public void setLeftButtonOnClickListener(OnSingleClickListener onSingleClickListener){
        setBackVisible(0, 0, R.drawable.btn_back_white);
        if(onSingleClickListener != null)
            mLeftButton.setOnClickListener(onSingleClickListener);
    }

    /**
     * 设置右边按钮显示事件
     * @param textId
     *          按钮名称
     * @param textColor
     *          按钮颜色
     * @param onSingleClickListener
     *          按钮事件
     */
    public void setRightButtonOnClickListener(int textId, int textColor,
            OnSingleClickListener onSingleClickListener){
        setRightButtonOnClickListener(textId, textColor, 0, onSingleClickListener);
    }

    /**
     * 设置右边按钮名称
     * @param textId
     */
    public void setRightButtonText(int textId){
        if(textId > 0) mRightButton.setText(textId);
    }

    /**
     * 设置右边按钮显示事件
     * @param resid
     *          按钮图片
     * @param onSingleClickListener
     *          按钮事件
     */
    public void setRightButtonOnClickListener(int resid,
            OnSingleClickListener onSingleClickListener){
        setRightButtonOnClickListener(0, 0, resid, onSingleClickListener);
    }


    public void setRightButtonOnClickListener(String text, int textColor, Drawable drawable, OnSingleClickListener onSingleClickListener){
        mRightButton.setVisibility(View.VISIBLE);
        mRightButton.setText(text);

        if(textColor == 0) mRightButton.setTextColor(Color.WHITE); else mRightButton.setTextColor(textColor);

        mRightButton.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);

        if(onSingleClickListener != null) mRightButton.setOnClickListener(onSingleClickListener);
    }

    /**
     * 设置右边按钮显示事件
     * @param textId
     *          按钮名称
     * @param textColor
     *          按钮颜色
     * @param resid
     *          按钮图片
     * @param onSingleClickListener
     *          按钮事件
     */
    public void setRightButtonOnClickListener(int textId, int textColor,  int resid,
            OnSingleClickListener onSingleClickListener){
        mRightButton.setVisibility(View.VISIBLE);
        if(textId > 0) mRightButton.setText(textId); else mRightButton.setText(null);
        if(textColor != 0) mRightButton.setTextColor(textColor);

        mRightButton.setCompoundDrawablesWithIntrinsicBounds(resid, 0, 0, 0);

        if(onSingleClickListener != null) mRightButton.setOnClickListener(onSingleClickListener);
    }

    public void setRightWhitTextBtnOnClickListener(String textId,
                                              OnSingleClickListener onSingleClickListener){
        setRightButtonOnClickListener(textId, Color.WHITE, onSingleClickListener);
    }

    public void setRightButtonOnClickListener(String textId, int textColor,
                                              OnSingleClickListener onSingleClickListener){
        mRightButton.setVisibility(View.VISIBLE);
        mRightButton.setText(textId);
        if(textColor != 0) mRightButton.setTextColor(textColor);

        if(onSingleClickListener != null) mRightButton.setOnClickListener(onSingleClickListener);
    }

    /**
     * Sets whether or not this view should account for system screen decorations
     * such as the status bar and inset its content; that is, controlling whether
     * the default implementation of {@link # fitSystemWindows(Rect)} will be
     * executed.  See that method for more details.
     */
//    public void setFitsSystemWindows(boolean fitSystemWindows){
//        mContentLayout.setFitsSystemWindows(fitSystemWindows);
//    }

    /**
     * 设置标题栏 Topmargin
     *
     * @param margin
     */
    public void setTitleTopMargin(int margin){
        LayoutParams lParams = (LayoutParams) mTitleLayout.getLayoutParams();
        lParams.height += margin;
        mTitleLayout.setLayoutParams(lParams);

        mTitleLayout.setPadding(0, margin, 0, 0);
        mBody.setPadding(0, lParams.height, 0, 0);
    }

    @Override
    public void onBackPressed() {
        back();
    }

    /**
     * 设备body没有45dp的Top padding
     */
    public void setBodyNoPadding(){
        mBody.setPadding(0, 0, 0, 0);
    }

    @Override
	public boolean dispatchTouchEvent(MotionEvent event) {
    	 if (event.getAction() == MotionEvent.ACTION_UP
                 || event.getAction() == MotionEvent.ACTION_CANCEL) {
             closeInputMethod();
         }

      	if (mMoreListView.getVisibility() == View.VISIBLE && !inRangeOfView(mMoreListView, event)) {
            hideMoreSelectWindow();
  			return true;
  		}

		return super.dispatchTouchEvent(event);
    }
//
//    // 点击空白处 关闭软键盘
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_UP
//                || event.getAction() == MotionEvent.ACTION_CANCEL) {
//            closeInputMethod();
//        }
//
//    	if (mMoreListView.getVisibility() == View.VISIBLE && !inRangeOfView(mMoreListView, event)) {
//    		mMoreListView.setVisibility(View.GONE);
//			return true;
//		}
//
//        return super.onTouchEvent(event);
//    }

//    @Override
//    public void activityFinish() {
//        back();
//        super.activityFinish();
//    }

    /**
     *
     * setTitleBarGone(隐藏标题栏)
     *
     */
    public void setTitleBarGone() {
        mTitleLayout.setVisibility(View.GONE);
        setBodyNoPadding();
    }

    /**
     *
     * setTitleBarVisble(显示标题栏)
     *
     */
    public void setTitleBarVisble() {
        mTitleLayout.setVisibility(View.VISIBLE);
        int hight = 0;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            hight = BLSettings.STATUS_HEIGHT;
        }
        mBody.setPadding(0, BLCommonUtils.dip2px(TitleActivity.this, 45) + hight, 0, 0);
    }

    public void setBodyDefaultPadding(){
        LayoutParams lParams = (LayoutParams) mTitleLayout.getLayoutParams();
        mBody.setPadding(0, lParams.height, 0, 0);
    }

    public void setBodyPadding(int height){
        mBody.setPadding(0, height, 0, 0);
    }

    public Button getRightButton() {
        return mRightButton;
    }

    public Button getLeftButton() {
        return mLeftButton;
    }

    public TextView getTitleView(){
        return mTitle;
    }

    /**
     * 显示遇到页面
     * @param key
     *          获取是否显示属性的key。
     *          (1) 当key为null时，页面每次加载总是显示；
     *          (2) 当key为某个字符串时，只有第一次加载时显示，后续不显示
     * @param view
     *          显示在那个控件上
     * @param shadowTouched
     *          点击其他阴影部分是否关闭引导页面
     * @param handerPostion
     *          手势显示的位置
     * @param HintStr
     *          提示文本
     * @param hintPosition
     *          提示语位置
     * @param onFloatViewClickListener
     *          浮层事件
     */
//    public void showGuideView(final String key, final View view, final boolean shadowTouched, final int handerPostion, final String HintStr, final int hintPosition, final FloatGuideView.OnFloatViewClickListener onFloatViewClickListener){
//        if(AppContents.getSettingInfo().commonGuide(key) && view != null){
//	        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//		        @Override
//		        public boolean onPreDraw() {
//			        view.getViewTreeObserver().removeOnPreDrawListener(this);
//			        int[] location = new int[2];
//			        view.getLocationOnScreen(location);
//
//			        final FloatGuideView floatGuideView = new FloatGuideView(TitleActivity.this);
//			        floatGuideView.setOnFloatViewClickListener(onFloatViewClickListener);
//			        mContentLayout.addView(floatGuideView);
//			        floatGuideView.setShadowTouched(shadowTouched);
//
//			        int top = location[1];
//			        floatGuideView.setFloatLocation(location[0], top, view.getWidth(), view.getHeight(),
//					        handerPostion, HintStr, hintPosition);
//
//			        floatGuideView.setOnFloatViewGoneListener(new FloatGuideView.OnFloatViewGoneListener() {
//				        @Override
//				        public void onViewGone() {
//					        mContentLayout.removeView(floatGuideView);
//					        AppContents.getSettingInfo().commonGuideClose(key);
//				        }
//			        });
//			        return true;
//		        }
//	        });
//        }
//    }

    /**显示更多选择窗口**/
    public void showMoreSelectWindow(){
        mMoreShadowView.setVisibility(View.GONE);
        mMoreListView.setVisibility(View.VISIBLE);
    }

    /**显示更多选择窗口**/
    public void hideMoreSelectWindow(){
        mMoreShadowView.setVisibility(View.GONE);
        mMoreListView.setVisibility(View.GONE);
    }

    /**设置更多选择列表适配器**/
    public void setMoreAdapter(final BaseAdapter adapter){
        mMoreListView.setAdapter(adapter);
        mMoreListView.setOnItemClickListener(new OnSingleItemClickListener() {
            @Override
            public void doOnClick(AdapterView<?> parent, View view, int position, long id) {
                onMoreItemClick(position, adapter.getItem(position));
            }
        });
    }

    public void onMoreItemClick(int position, Object object){
        hideMoreSelectWindow();
    }

    public void showProgressDialog(String name) {
        showProgressDialog(name, true);
    }
    public void showProgressDialog(String name, boolean cancelable) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(TitleActivity.this);
            progressDialog.setMessage(name);
            progressDialog.setCancelable(cancelable);
            progressDialog.show();
        }
    }
    public void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
