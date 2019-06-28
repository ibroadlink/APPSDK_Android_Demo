package cn.com.broadlink.blappsdkdemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 简单的条形颜色选择器，可以使用xml设置宽和高
 * 圆形滑块
 * 其中的高为滑块的高，颜色条的高为滑块高的1/2
 */
public class BLColorPaletteView extends View {
    /**
     * Colors to construct the color wheel using {@link android.graphics.SweepGradient}.
     * 用于构造色条的颜色数组，利用android的LinearGradient类
     */
    private static final int[] COLORS = new int[]{0xFFFF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF, 0xFF00FF00, 0xFFFFFF00, 0xFFFFFFFF, 0xFF000000};

    /**
     * 长条宽度
     */
    private int barHeight = 30;
    /**
     * 长条的高度
     */
    private int barWidth;

    /**
     * 滑块的半径
     */
    private int thumbRadius = barHeight;

    /**
     * 滑块当前的位置
     */
    private int currentThumbOffset = thumbRadius;

    /**
     * 长条开始位置
     */
    private int barStartX, barStartY;

    private static int STATUS;
    private static final int STATUS_INIT = 0;
    /**
     * 移动了action bar
     */
    private static final int STATUS_SEEK = 1;

    Paint thumbPaint = new Paint();

    private int currentColor;

    private enum INIT_COLOR {BLACK, WHITE, NONE}

    
    private INIT_COLOR pendingInitColor = INIT_COLOR.NONE;    

    public void setHeight(int h) {
        thumbRadius = barHeight = h / 2;
    }

    public interface ColorChangeListener {
        void colorChange(int color);
    }

    ColorChangeListener colorChangeListener;

    public BLColorPaletteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        currentColor = COLORS[0];
        STATUS = STATUS_INIT;
        invalidate();
    }

    /**
     * onsizechanged时获取组件的长和宽，后面ondraw时就利用它们进行绘图
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        thumbRadius = h / 2;
        barHeight = thumbRadius;
        barWidth = w - thumbRadius * 2;
        barStartX = thumbRadius;//不从0开始，左右边缘用于显示滑块
        barStartY = thumbRadius - barHeight / 2;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void setOnColorChangerListener(ColorChangeListener colorChangerListener) {
        this.colorChangeListener = colorChangerListener;
    }

    /**
     * 绘制底部颜色条
     *
     * @param canvas
     */
    private void drawBar(Canvas canvas) {
        Paint barPaint = new Paint();
        barPaint.setShader(new LinearGradient(barStartX, barStartY + barHeight / 2, barStartX + barWidth, barStartY + barHeight / 2, COLORS, null, Shader.TileMode.CLAMP));
        canvas.drawRect(new Rect(barStartX, barStartY, barStartX + barWidth, barStartY + barHeight), barPaint);
    }

    /**
     * 处理点击和滑动事件
     *
     * @param event
     * @return
     */

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {//点击时
            case MotionEvent.ACTION_DOWN:
                currentThumbOffset = (int) event.getX();
                if (currentThumbOffset <= thumbRadius) currentThumbOffset = thumbRadius;
                if (currentThumbOffset >= barWidth + thumbRadius) currentThumbOffset = barWidth + thumbRadius;
                STATUS = STATUS_SEEK;
                break;
            //滑动时
            case MotionEvent.ACTION_MOVE:
                currentThumbOffset = (int) event.getX();
                if (currentThumbOffset <= thumbRadius) currentThumbOffset = thumbRadius;
                if (currentThumbOffset >= barWidth + thumbRadius) currentThumbOffset = barWidth + thumbRadius;
                break;

        }
        //局部更新，好像没什么用
        invalidate(currentThumbOffset - thumbRadius, barStartY + barHeight / 2 - thumbRadius, currentThumbOffset + thumbRadius, barStartY + barHeight / 2 + thumbRadius);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int unit = barWidth / (COLORS.length - 1);
        switch (pendingInitColor){
            case BLACK:
                currentThumbOffset = unit * 7 + thumbRadius;
                break;
            case WHITE:
                currentThumbOffset = unit * 6 + thumbRadius;
                break;
        }
        pendingInitColor = INIT_COLOR.NONE;
        
        switch (STATUS) {
            case STATUS_INIT:
                drawBar(canvas);
                drawThumb(canvas);
                break;
            case STATUS_SEEK:
                drawBar(canvas);
                if( currentColor != getCurrentColor()){
                    currentColor = getCurrentColor();
                    if (colorChangeListener != null) colorChangeListener.colorChange(currentColor);
                }
                drawThumb(canvas);
        }
        super.onDraw(canvas);
    }
    
    public int[] getColor(){
       return new int[] { Color.red(currentColor), Color.green(currentColor), Color.blue(currentColor)};
    }

    private int ave(int s, int t, int unit, int step) {
        return s + (t - s) * step / unit;
    }

    /**
     * 获取当前所在区间，再根据颜色变换算法获取颜色值
     */
    private int getCurrentColor() {
        int unit = barWidth / (COLORS.length - 1);
        int position = currentThumbOffset - thumbRadius;
        int i = position / unit;
        int step = position % unit;
        if (i >= COLORS.length - 1) return COLORS[COLORS.length - 1];
        int c0 = COLORS[i];
        int c1 = COLORS[i + 1];

        int a = ave(Color.alpha(c0), Color.alpha(c1), unit, step);
        int r = ave(Color.red(c0), Color.red(c1), unit, step);
        int g = ave(Color.green(c0), Color.green(c1), unit, step);
        int b = ave(Color.blue(c0), Color.blue(c1), unit, step);

        return Color.argb(a, r, g, b);
    }

    public void setColor(int color){
        currentColor = color;
        currentThumbOffset = -1;
        postInvalidate();
    }


    public void setWhiteColor(){
        if(barWidth<=0){
            currentColor = Color.WHITE;
            pendingInitColor = INIT_COLOR.WHITE;
        }else{
            int unit = barWidth / (COLORS.length - 1);
            currentThumbOffset = unit * 5;
            currentColor = Color.WHITE;
            postInvalidate();  
        }
    }

    public void setBlackColor(){
        if(barWidth<=0){
            currentColor = Color.BLACK;
            pendingInitColor = INIT_COLOR.BLACK;
        }else{
            int unit = barWidth / (COLORS.length - 1);
            currentThumbOffset = unit * 6;
            currentColor = Color.BLACK;
            postInvalidate(); 
        }
    }
    

    public int getColorInt(){
        return currentColor & 0xffffff;
    }

    private void drawThumb(Canvas canvas) {
        if(currentThumbOffset <0) return;
        
        thumbPaint.setColor(currentColor);
        canvas.drawOval(getThumbRect(), thumbPaint);
    }

    /**
     * 获取滑块所在的矩形区域
     */
    private RectF getThumbRect() {
        return new RectF(currentThumbOffset - thumbRadius, barStartY + barHeight / 2 - thumbRadius, currentThumbOffset + thumbRadius, barStartY + barHeight / 2 + thumbRadius);
    }
}