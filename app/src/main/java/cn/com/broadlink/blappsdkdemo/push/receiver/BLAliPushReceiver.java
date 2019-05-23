package cn.com.broadlink.blappsdkdemo.push.receiver;

import android.content.Context;

import com.alibaba.sdk.android.push.notification.CPushMessage;

import java.util.Map;

import cn.com.broadlink.blappsdkdemo.common.BLLog;
import cn.com.broadlink.blappsdkdemo.mvp.presenter.BLPushServicePresenter;


/**
 * 作者：EchoJ on 2017/5/17 14:07 <br>
 * 邮箱：echojiangyq@gmail.com <br>
 * 描述：
 */

public class BLAliPushReceiver extends com.alibaba.sdk.android.push.MessageReceiver {
    
    public static final String REC_TAG = "push";
    @Override
    public void onNotification(Context context, String title, String summary, Map<String, String> extraMap) {
        BLLog.e(REC_TAG, "Receive notification, title: " + title + ", summary: " + summary + ", extraMap: " + extraMap);
        BLPushServicePresenter.getInstance(context).showMsg(title,summary,false);
    }

    @Override
    public void onMessage(Context context, CPushMessage cPushMessage) {
        BLLog.e(REC_TAG, "onMessage, messageId: " + cPushMessage.getMessageId() + ", title: " + cPushMessage.getTitle() + ", content:" + cPushMessage.getContent());
        BLPushServicePresenter.getInstance(context).showMsg(cPushMessage.getTitle(), cPushMessage.getContent(), true);
    }
    
    @Override
    public void onNotificationOpened(Context context, String title, String summary, String extraMap) {
        BLLog.e(REC_TAG, "onNotificationOpened, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap);
    }
    @Override
    protected void onNotificationClickedWithNoAction(Context context, String title, String summary, String extraMap) {
        BLLog.e(REC_TAG, "onNotificationClickedWithNoAction, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap);
    }
    @Override
    protected void onNotificationReceivedInApp(Context context, String title, String summary, Map<String, String> extraMap, int openType, String openActivity, String openUrl) {
        BLLog.e(REC_TAG, "onNotificationReceivedInApp, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap + ", openType:" + openType + ", openActivity:" + openActivity + ", openUrl:" + openUrl);
    }
    @Override
    protected void onNotificationRemoved(Context context, String messageId) {
        BLLog.e(REC_TAG, "onNotificationRemoved");
    }

}
