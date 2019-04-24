package cn.com.broadlink.blappsdkdemo.mvp.presenter;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.Button;

import com.alibaba.fastjson.JSON;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.BuildConfig;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.h5.DeviceH5Activity;
import cn.com.broadlink.blappsdkdemo.common.BLApiUrlConstants;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLLog;
import cn.com.broadlink.blappsdkdemo.data.link.LinkageInfo;
import cn.com.broadlink.blappsdkdemo.data.push.BLPushMsg;
import cn.com.broadlink.blappsdkdemo.data.push.PlatformPushInfo;
import cn.com.broadlink.blappsdkdemo.data.push.PlatformPushInfoParam;
import cn.com.broadlink.blappsdkdemo.data.push.PushDelLinktParam;
import cn.com.broadlink.blappsdkdemo.data.push.PushQueryTempListParam;
import cn.com.broadlink.blappsdkdemo.data.push.PushReportHeader;
import cn.com.broadlink.blappsdkdemo.data.push.PushReportTokenParam;
import cn.com.broadlink.blappsdkdemo.intferfacer.BasePushListener;
import cn.com.broadlink.blappsdkdemo.mvp.model.PushModel;
import cn.com.broadlink.blappsdkdemo.utils.http.BLHttpPostAccessor;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.BLProgressDialog;
import cn.com.broadlink.blappsdkdemo.view.BLStyleDialog;

/**
 * Created by YeJin on 2017/7/12.
 */
public class BLPushServicePresenter implements PushModel {
    public static final String FAMILY_ID_KEY = "familyId";
    private Context mContext;
    private static final String TAG = "BLPushServicePresenter";
    private Dialog mDialog;
    private Handler mPushRegHandler;
    private int mReportTokenRetryCnt = 1;
    final int REPORT_TOKEN_RETRY_MAX_CNT = 20;
    static volatile BLPushServicePresenter instance;
    private BLProgressDialog mProgressDialog;
    private String mShowMsg = "";
    private String mPushUrl = "";
    private String mFamilyId;
    
    BLPushServicePresenter(Context mContext){
        init(mContext);
    }
    public static final BLPushServicePresenter getInstance(Context mContext) {
        if (instance == null) {
            synchronized (BLPushServicePresenter.class) {
                if (instance == null) {
                    instance =new BLPushServicePresenter(mContext);
                }
            }
        }else if(mContext != instance.mContext){
            instance.init(mContext);
        }
        
        return instance;
    }

    @Override
    public void init(Context context) {
        mContext = context;
        if(mContext instanceof Activity){
            mProgressDialog = BLProgressDialog.createDialog(mContext);
        }else{
            mProgressDialog = null;
        }
    }

    protected class CHECK_PUSH_RESULT {
        public static final int ERR_USERID_NULL = -1;
        public static final int ERR_TOKEN_NULL = -2;
        public static final int SUCC = -2;
    }
    private int checkUseridAndToken(){
        final String token = getToken();
        final String userId = BLApplication.mBLUserInfoUnits.getUserid();
        final boolean ret = !TextUtils.isEmpty(token) && !TextUtils.isEmpty(userId);
        
        BLLog.d(TAG, String.format("[uid]: %s, [token]: %s", userId, token));
        if(ret){
            return CHECK_PUSH_RESULT.SUCC;
        } else{
            if(TextUtils.isEmpty(userId)){
                return CHECK_PUSH_RESULT.ERR_USERID_NULL;
            }else{
                return CHECK_PUSH_RESULT.ERR_TOKEN_NULL;
            }
        }
    }

    private void executePushTask(BasePushTask task, BasePushListener listener){
        final int res = checkUseridAndToken();
        if(res == CHECK_PUSH_RESULT.SUCC){
            task.execute();
        }else if(listener != null){
            listener.onCallBack(mContext.getString( res== CHECK_PUSH_RESULT.ERR_USERID_NULL ?  R.string.push_userid_null : R.string.push_token_null));
        }
    }
    
    @Override
    public void reportToken(BasePushListener listener) {
        
        final String token = getToken();
        PushReportTokenTask  mPushReportTokenTask = new PushReportTokenTask(listener,token,getTokenSystem(mContext));
        executePushTask(mPushReportTokenTask, listener);
    }
    
    /** 上报token **/
    private class PushReportTokenTask extends BasePushTask{
        String token;
        String tokenSystem;
        public PushReportTokenTask(BasePushListener listener, String token, String tokenSystem){
            super(listener);
            this.token = token;
            this.tokenSystem = tokenSystem;
        }

        @Override
        protected String doInBackground(Void... voids) {

            List<PushReportTokenParam> reportTokenParamList = new ArrayList<>();
            PushReportTokenParam pushReportTokenParam = new PushReportTokenParam();
            pushReportTokenParam.setUserid(BLApplication.mBLUserInfoUnits.getUserid());
            pushReportTokenParam.setTousertype(PUSH_CLIENT_APP);
            pushReportTokenParam.setTouser(token);
            reportTokenParamList.add(pushReportTokenParam);
            
            BLHttpPostAccessor mPostAccessor = new BLHttpPostAccessor(mContext);
            mPostAccessor.setToastError(false);
            PushReportHeader pushReportHeader = new PushReportHeader();
            pushReportHeader.setSystem(tokenSystem);
            return mPostAccessor.execute(BLApiUrlConstants.PushServicer.REG(), pushReportHeader, JSON.toJSONString(reportTokenParamList), String.class);
        }
        @Override
        protected void onPostExecute(String blBaseResult) {
            super.onPostExecute(blBaseResult);
        }
    }


    @Override
    public void queryTempList(String cat, BasePushListener listener) {
        QueryTempListTask  mPushReportTokenTask = new QueryTempListTask(cat, listener);
        executePushTask(mPushReportTokenTask, listener);
    }
    
    private class QueryTempListTask extends BasePushTask{
        String cat;
        public QueryTempListTask(String cat, BasePushListener listener){
            super(listener);
            this.cat = cat;
        }

        @Override
        protected String doInBackground(Void... voids) {

            final PushQueryTempListParam pushQueryTempListParam = new PushQueryTempListParam(cat);

            BLHttpPostAccessor mPostAccessor = new BLHttpPostAccessor(mContext);
            mPostAccessor.setToastError(false);
            PushReportHeader pushReportHeader = new PushReportHeader();
            return mPostAccessor.execute(BLApiUrlConstants.PushServicer.TEMP_QUERY(), pushReportHeader, JSON.toJSONString(pushQueryTempListParam), String.class);
        }
        @Override
        protected void onPostExecute(String blBaseResult) {
            super.onPostExecute(blBaseResult);
        }
    }

    
    @Override
    public void addLink(LinkageInfo pushLinkInfo, BasePushListener listener) {
        AddLinkTask mPushReportTokenTask = new AddLinkTask(listener, pushLinkInfo);
        executePushTask(mPushReportTokenTask, listener);
    }
    private class AddLinkTask extends BasePushTask{
        LinkageInfo pushLinkInfo;
                
        public AddLinkTask(BasePushListener listener, LinkageInfo pushLinkInfo){
            super(listener);
            this.pushLinkInfo = pushLinkInfo;
        }

        @Override
        protected String doInBackground(Void... voids) {

            BLHttpPostAccessor mPostAccessor = new BLHttpPostAccessor(mContext);
            mPostAccessor.setToastError(false);
            PushReportHeader pushReportHeader = new PushReportHeader();
            return mPostAccessor.execute(BLApiUrlConstants.PushServicer.LINK_ADD(), pushReportHeader, JSON.toJSONString(pushLinkInfo), String.class);
        }
        @Override
        protected void onPostExecute(String blBaseResult) {
            super.onPostExecute(blBaseResult);
        }
    }

    
    
    @Override
    public void queryLink(BasePushListener listener) {
        QueryLinkTask mPushReportTokenTask = new QueryLinkTask(listener);
        executePushTask(mPushReportTokenTask, listener);
    }
    private class QueryLinkTask extends BasePushTask{

        public QueryLinkTask(BasePushListener listener){
            super(listener);
        }

        @Override
        protected String doInBackground(Void... voids) {


            BLHttpPostAccessor mPostAccessor = new BLHttpPostAccessor(mContext);
            mPostAccessor.setToastError(false);
            PushReportHeader pushReportHeader = new PushReportHeader();
            return mPostAccessor.execute(BLApiUrlConstants.PushServicer.LINK_QUERY(), pushReportHeader, null, String.class);
        }
        @Override
        protected void onPostExecute(String blBaseResult) {
            super.onPostExecute(blBaseResult);
        }
    }

    @Override
    public void delLink(String ruleId, BasePushListener listener) {

        DelLinkTask mPushReportTokenTask = new DelLinkTask(listener, ruleId);
        executePushTask(mPushReportTokenTask, listener);
    }
    private class DelLinkTask extends BasePushTask{
        String ruleId;

        public DelLinkTask(BasePushListener listener, String ruleId){
            super(listener);
            this.ruleId = ruleId;
        }

        @Override
        protected String doInBackground(Void... voids) {

            final PushDelLinktParam pushQueryTempListParam = new PushDelLinktParam(ruleId);

            BLHttpPostAccessor mPostAccessor = new BLHttpPostAccessor(mContext);
            mPostAccessor.setToastError(false);
            PushReportHeader pushReportHeader = new PushReportHeader();
            return mPostAccessor.execute(BLApiUrlConstants.PushServicer.LINK_DEL(), pushReportHeader, JSON.toJSONString(pushQueryTempListParam), String.class);
        }
        @Override
        protected void onPostExecute(String blBaseResult) {
            super.onPostExecute(blBaseResult);
        }
    }



    @Override
    public void setPushEnable(boolean enable, BasePushListener listener) {
        SetPushEnableTask  mPushReportTokenTask = new SetPushEnableTask(listener, enable);
        executePushTask(mPushReportTokenTask, listener);
    }

    /** 设置推送的总开关 **/
    private class SetPushEnableTask extends BasePushTask{
        private boolean pushEnable = false;

        private SetPushEnableTask(BasePushListener listener, boolean pushEnable){
            super(listener);
            this.pushEnable = pushEnable;
        }

        @Override
        protected String doInBackground(Void... voids) {
            PlatformPushInfo platformPushInfo = new PlatformPushInfo();
            platformPushInfo.tokentype.tousertype = PUSH_CLIENT_APP;
            platformPushInfo.tokentype.touser = getToken();
            platformPushInfo.action = (pushEnable ? PUSH_ACTION_DEV_FOLLOW : PUSH_ACTION_DEV_QUIT_FOLLOW);

            PlatformPushInfoParam param = new PlatformPushInfoParam();
            param.setUserid(BLApplication.mBLUserInfoUnits.getUserid());
            param.getManagetypeinfo().add(platformPushInfo);

            BLHttpPostAccessor mPostAccessor = new BLHttpPostAccessor(mContext);
            return mPostAccessor.execute(BLApiUrlConstants.PushServicer.SET_TYPE(), new PushReportHeader(), JSON.toJSONString(param), String.class);
        }
        @Override
        protected void onPostExecute(String blBaseResult) {
            super.onPostExecute(blBaseResult);
        }
    }
    

    @Override
    public void logoutUser(BasePushListener listener) {
        UserLogoutTask mPushReportTokenTask = new UserLogoutTask(listener);
        executePushTask(mPushReportTokenTask, listener);
    }

    /** 用户退出 **/
    private class UserLogoutTask extends BasePushTask{

        private UserLogoutTask(BasePushListener listener) {
            super(listener);
        }

        @Override
        protected String doInBackground(Void... param) {
            BLHttpPostAccessor mPostAccessor = new BLHttpPostAccessor(mContext);
            mPostAccessor.setToastError(false);
            final JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("accountlabel", 1);
                jsonObject.put("touser", getToken());
            } catch (JSONException e) {
                e.printStackTrace();
            }
           return mPostAccessor.execute(BLApiUrlConstants.PushServicer.LOGOUT(),  new PushReportHeader(), jsonObject.toString(), String.class);
        }
    }
    private void destory(){
      
    }

    /**
     * 获取推送的token
     * @return
     */
    public String getToken(){
        String token = null;
        final BLApplication applicationContext = (BLApplication) (mContext.getApplicationContext());
        if(applicationContext != null && applicationContext.mAliPushService != null){
            token = applicationContext.mAliPushService.getDeviceId();
        }
        return token;
    }

    /**
     * 获取推送的系统平台
     * @param context
     * @return
     */

    public static String getTokenSystem(Context context){
        return "android-alicloud";
    }

    @Override
    public void showMsg(String title, String msg, boolean isShowNotification) {
         parsePushMsg(msg);
        if (BLCommonUtils.isRunningForeground(mContext)) { //在前台运行
            sendDialog(title);
        }else if(isShowNotification){
            sendNotification(title);
        }
    }

    private void sendNotification(String title) {
        Intent launchIntent = null;
        //app是否存活
        if (!BLCommonUtils.isAppaLive(mContext)) {
            //启动app
            launchIntent = mContext.getPackageManager().
                    getLaunchIntentForPackage(BuildConfig.APPLICATION_ID);
            launchIntent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            launchIntent.putExtra(BLConstants.INTENT_URL, mPushUrl);
        } else {
            launchIntent = new Intent();
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            launchIntent.putExtra(BLConstants.INTENT_URL, mPushUrl);
            //launchIntent.putExtra(BLConstants.INTENT_TITLE, title);
            launchIntent.setClass(mContext, DeviceH5Activity.class);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 通知渠道的id
            String chanelId = "1";
            // 通知渠道的名字.
            CharSequence chanelName = "pushchannel";
            NotificationChannel channel = new NotificationChannel(chanelId, chanelName, NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.setShowBadge(false);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(channel);

            Notification.Builder builder = new Notification.Builder(mContext,chanelId);
            builder.setSmallIcon(R.mipmap.icon)
                    .setContentTitle(TextUtils.isEmpty(title) ? mContext.getString(R.string.app_name) : title)
                    .setContentText(mShowMsg)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
            notificationManager.notify(0, builder.build());
        }else{
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
                    .setSmallIcon(R.mipmap.icon)
                    .setContentTitle(TextUtils.isEmpty(title) ? mContext.getString(R.string.app_name) : title)
                    .setContentText(mShowMsg)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            notificationManager.notify(0 , notificationBuilder.build());
        }
    }

    private void sendDialog(final String title){

        final BLApplication applicationContext = (BLApplication)(mContext.getApplicationContext());
            final Activity activity = applicationContext.getTopActivity();
            final NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            if(mDialog != null && mDialog.isShowing()){ // 如果已经显示有Dialog，则先取消旧的再显示新的
                mDialog.dismiss();
            }

            mDialog = BLAlert.showAlert(activity, TextUtils.isEmpty(title) ? "Push Message" : title, mShowMsg, mContext.getString(R.string.str_common_sure), new
                    BLStyleDialog.OnBLDialogBtnListener() {

                        @Override
                        public void onClick(Button btn) {
                            notificationManager.cancel(0);
                            if(!TextUtils.isEmpty(mPushUrl)){
                                Intent intent = new Intent();
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra(BLConstants.INTENT_URL, mPushUrl);
                                //intent.putExtra(BLConstants.INTENT_TITLE, title);
                                intent.setClass(mContext, DeviceH5Activity.class);
                                mContext.startActivity(intent);
                            }
                        }
                    },null,null); 
    }

    private void parsePushMsg(String messageBody){
        BLPushMsg pushMsg = null;
        try {
            pushMsg = JSON.parseObject(messageBody, BLPushMsg.class);
        } catch (com.alibaba.fastjson.JSONException e) {
            e.printStackTrace();
        }
        if(pushMsg == null){
            mShowMsg = messageBody;
            return;
        }
        
        mShowMsg  = pushMsg.getMsg();
          String data = pushMsg.getData();
          com.alibaba.fastjson.JSONObject jsonData = JSON.parseObject(data);
          String actionStr = jsonData.getString("action");
          Uri actionUri = Uri.parse(actionStr);
          String target = actionUri.getAuthority();
          String action = actionUri.getPath().replace("/","");
          if(target.equals("common") && action.equals("web")){
              mPushUrl  = actionUri.getQueryParameter("url");
          }else if(target.equals("device") && action.equals("fastconConfig")){
              
              // fastcon配置推送url，暂时保留
              String baseurl = BLApiUrlConstants.FASTCON_CONFIG_URL + "familyId=%1$s&masterDid=%2$s&clientInfo=%3$s";
              mFamilyId = actionUri.getQueryParameter(FAMILY_ID_KEY);
              String masterDid = actionUri.getQueryParameter("masterDid");
              String clientInfoStr = actionUri.getQueryParameter("clientInfo");
              com.alibaba.fastjson.JSONObject jsonClient = JSON.parseObject(clientInfoStr);
              mPushUrl = String.format(baseurl,mFamilyId,masterDid,jsonClient.toJSONString());
          }
    }



    /** Base AsyncTask **/
    private class BasePushTask extends AsyncTask<Void, Void, String> {
        protected BasePushListener listener;

        private BasePushTask(BasePushListener listener){
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
            if(mProgressDialog != null){
                if(listener != null){
                    if(!listener.isShowProgressDialog()) return;
                    mProgressDialog.setMessage(listener.getProgreeDialogTip());
                    mProgressDialog.show();
                }
            }
        }

        @Override
        protected String doInBackground(Void... param) {
            return null;
        }

        @Override
        protected void onPostExecute(String blBaseResult) {
            super.onPostExecute(blBaseResult);

            if(mProgressDialog != null){
                mProgressDialog.dismiss();
            }

            if(listener != null){
                listener.onCallBack(blBaseResult);
            }
        }
    }
}
