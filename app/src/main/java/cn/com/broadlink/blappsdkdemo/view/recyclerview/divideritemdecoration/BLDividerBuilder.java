package cn.com.broadlink.blappsdkdemo.view.recyclerview.divideritemdecoration;

import android.support.annotation.ColorInt;

public class BLDividerBuilder {

    private SideLine leftSideLine;
    private SideLine topSideLine;
    private SideLine rightSideLine;
    private SideLine bottomSideLine;


    public BLDividerBuilder setLeftSideLine(boolean isHave, @ColorInt int color, int widthPx, float startPaddingDp, float endPaddingDp) {
        this.leftSideLine = new SideLine(isHave, color, widthPx, startPaddingDp, endPaddingDp);
        return this;
    }

    public BLDividerBuilder setTopSideLine(boolean isHave, @ColorInt int color, int widthPx, float startPaddingDp, float endPaddingDp) {
        this.topSideLine = new SideLine(isHave, color, widthPx, startPaddingDp, endPaddingDp);
        return this;
    }

    public BLDividerBuilder setRightSideLine(boolean isHave, @ColorInt int color, int widthPx, float startPaddingDp, float endPaddingDp) {
        this.rightSideLine = new SideLine(isHave, color, widthPx, startPaddingDp, endPaddingDp);
        return this;
    }

    public BLDividerBuilder setBottomSideLine(boolean isHave, @ColorInt int color, int widthPx, float startPaddingDp, float endPaddingDp) {
        this.bottomSideLine = new SideLine(isHave, color, widthPx, startPaddingDp, endPaddingDp);
        return this;
    }

    public Divider create() {
        //提供一个默认不显示的sideline，防止空指针
        SideLine defaultSideLine = new SideLine(false, 0xff666666, 0, 0, 0);

        leftSideLine = (leftSideLine != null ? leftSideLine : defaultSideLine);
        topSideLine = (topSideLine != null ? topSideLine : defaultSideLine);
        rightSideLine = (rightSideLine != null ? rightSideLine : defaultSideLine);
        bottomSideLine = (bottomSideLine != null ? bottomSideLine : defaultSideLine);

        return new Divider(leftSideLine, topSideLine, rightSideLine, bottomSideLine);
    }


}



