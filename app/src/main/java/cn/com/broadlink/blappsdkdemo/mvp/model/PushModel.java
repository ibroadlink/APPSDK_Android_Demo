package cn.com.broadlink.blappsdkdemo.mvp.model;

import android.content.Context;

import cn.com.broadlink.blappsdkdemo.BuildConfig;
import cn.com.broadlink.blappsdkdemo.data.link.LinkageInfo;
import cn.com.broadlink.blappsdkdemo.intferfacer.BasePushListener;


/**
 * Created by YeJin on 2017/7/12.
 */

public interface PushModel {
    //推送客户端
    String PUSH_CLIENT_APP = "app";

    //设备开启推送标识
    String PUSH_ACTION_DEV_FOLLOW = "favor";

    //设备关闭推送标识
    String PUSH_ACTION_DEV_QUIT_FOLLOW = "quitfavor";

    // appid传递给云端标识是哪个app
    String APPID = BuildConfig.APPLICATION_ID;

    void init(Context context);

    /**上报token云端**/
    void reportToken(BasePushListener listener);

    /**
     * 退出用户登陆
     */
    void logoutUser(BasePushListener listener);

    /**
     * 设置推送开关
     * @param enable
     * true 开 false 关
     */
    void setPushEnable(boolean enable, BasePushListener listener);


    /**
     * 展示推送信息
     * @param title                 消息title               
     * @param msg                   消息内容
     * @param isShowNotification    是否发送通知
     */
    void showMsg(String title, String msg, boolean isShowNotification);
    
    void queryTempList(String cat, BasePushListener listener);

    void addLink(LinkageInfo pushLinkInfo, BasePushListener listener);
    
    void queryLink(BasePushListener listener);

    void delLink(String ruleId, BasePushListener listener);
}
