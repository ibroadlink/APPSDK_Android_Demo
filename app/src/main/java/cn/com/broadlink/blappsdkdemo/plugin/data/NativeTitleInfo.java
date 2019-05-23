package cn.com.broadlink.blappsdkdemo.plugin.data;

import java.util.List;

/**
 * Created by YeJin on 2016/6/24.
 */
public class NativeTitleInfo {

    private TitleBarInfo titleBar;

    private TitleBtnInfo leftButton;

    private List<TitleBtnInfo> rightButtons;

    public NativeTitleInfo() {
    }

    public TitleBtnInfo getLeftButton() {
        return leftButton;
    }

    public void setLeftButton(TitleBtnInfo leftButton) {
        this.leftButton = leftButton;
    }

    public List<TitleBtnInfo> getRightButtons() {
        return rightButtons;
    }

    public void setRightButtons(List<TitleBtnInfo> rightButtons) {
        this.rightButtons = rightButtons;
    }

    public TitleBarInfo getTitleBar() {
        return titleBar;
    }

    public void setTitleBar(TitleBarInfo titleBar) {
        this.titleBar = titleBar;
    }
}
