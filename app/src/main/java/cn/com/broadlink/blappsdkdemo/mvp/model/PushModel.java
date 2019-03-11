package cn.com.broadlink.blappsdkdemo.mvp.model;

import android.content.Context;

import java.util.List;

import cn.com.broadlink.blappsdkdemo.BuildConfig;
import cn.com.broadlink.blappsdkdemo.db.data.BLDeviceInfo;
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
     * 切换用户
     * userid 统一的存取点在AppContents.getUserInfo().login() & AppContents.getUserInfo().getUserId(),所以不需要传userid过来
     */
    void switchUser(BasePushListener listener);

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
     * 设置单个设备支不支持推送
     * @param did 需要设置推送的设备did
     * @param pushEnable 推送是否打开
     */
    void setDevicePushEnable(String did, boolean pushEnable, BasePushListener listener);

    /**
     * 查询单个设备支不支持推送
     * @param did 设备did
     * @param pid 设备pid
     * @param listener 监听器
     */
    void queryDevicePushEnable(String did, String pid, BasePushListener listener);
    
    /**
     * 查询设备是否设置推送功能
     * @param deviceList
     */
    void queryDeviceListPushEnable(List<BLDeviceInfo> deviceList, BasePushListener listener);

    
    /**
     * 是否打开推送
     * @return
     */
    void isPushEnabled(BasePushListener listener);

    /**
     * 展示推送信息
     * @param title                 消息title               
     * @param msg                   消息内容
     * @param isShowNotification    是否发送通知
     */
    void showMsg(String title, String msg, boolean isShowNotification);

}
