package cn.com.broadlink.blappsdkdemo;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.didichuxing.doraemonkit.DoraemonKit;
import com.didichuxing.doraemonkit.kit.IKit;

import net.sqlcipher.database.SQLiteDatabase;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.com.broadlink.account.BLAccount;
import cn.com.broadlink.base.BLConfigParam;
import cn.com.broadlink.blappsdkdemo.activity.family.manager.BLSFamilyManager;
import cn.com.broadlink.blappsdkdemo.common.BLApiUrlConstants;
import cn.com.broadlink.blappsdkdemo.common.BLAppUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLSettings;
import cn.com.broadlink.blappsdkdemo.common.BLStorageUtils;
import cn.com.broadlink.blappsdkdemo.common.BLUserInfoUnits;
import cn.com.broadlink.blappsdkdemo.common.PreferencesUtils;
import cn.com.broadlink.blappsdkdemo.mvp.presenter.CountryContentProvider;
import cn.com.broadlink.blappsdkdemo.plugin.ShowVersionInfoKit;
import cn.com.broadlink.blappsdkdemo.service.BLLocalDeviceManager;
import cn.com.broadlink.ircode.BLIRCode;
import cn.com.broadlink.sdk.BLLet;

public class BLApplication extends Application{

    public static BLUserInfoUnits mBLUserInfoUnits;
    
    public static ExecutorService FULL_TASK_EXECUTOR;

    public CloudPushService mAliPushService;
    
    public List<Activity> mActivityList = new ArrayList<Activity>();
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        FULL_TASK_EXECUTOR = Executors.newCachedThreadPool();

        mBLUserInfoUnits = new BLUserInfoUnits(this);

        sdkInit();
        
        queryPhoneWindowInfo();
        
        SQLiteDatabase.loadLibs(this);

        BLLocalDeviceManager.getInstance();

        CountryContentProvider.getInstance().initData(null);
        
        BLAppUtils.init(this);

        BLStorageUtils.init(this);

        initDoraKit();

        initPushService(this);
    }

    private void initDoraKit() {
        final ArrayList<IKit> kits = new ArrayList<>();
        kits.add(new ShowVersionInfoKit());
        DoraemonKit.install(this, kits);
    }

    /**
     * APPSDK 初始化函数
     */
    public void sdkInit(){

        String packageName = PreferencesUtils.getString(this, "packageName", BLConstants.SDK_PACKAGE);
        String license = PreferencesUtils.getString(this, "license", BLConstants.SDK_LICENSE);
        boolean useCluster = PreferencesUtils.getBoolean(this, "cluster",true);

        // 初始化核心库
        BLConfigParam blConfigParam = new BLConfigParam();
        
        // 1. 设置日志级别，默认为 4 全部打印
        blConfigParam.put(BLConfigParam.CONTROLLER_LOG_LEVEL, "4");
        
        // 2. 设置底层打印日志级别，默认为 4 全部打印
        blConfigParam.put(BLConfigParam.CONTROLLER_JNI_LOG_LEVEL, "4");
        
        // 3. 设置脚本保存目录, 默认在 ../let/ 目录下
        // blConfigParam.put(BLConfigParam.SDK_FILE_PATH, "");
        
        // 4. 设置本地控制超时时间，默认 3000ms
        blConfigParam.put(BLConfigParam.CONTROLLER_LOCAL_TIMEOUT, "3000");
        
        // 5. 设置远程控制超时时间，默认 5000ms
        blConfigParam.put(BLConfigParam.CONTROLLER_REMOTE_TIMEOUT, "8000");
        
        // 6. 设置控制重试次数，默认 1
        blConfigParam.put(BLConfigParam.CONTROLLER_SEND_COUNT, "1");
        
        // 7. 设置设备控制支持的网络模式，默认 -1 都支持。  0 - 局域网控制，非0 - 局域网/远程都支持。
        blConfigParam.put(BLConfigParam.CONTROLLER_NETMODE, "-1");
        
        // 8. 设置脚本和UI文件下载资源平台。 默认 0 老平台。  1 - 新平台
        blConfigParam.put(BLConfigParam.CONTROLLER_SCRIPT_DOWNLOAD_VERSION, "1");
        
        // 9. 批量查询设备在线状态最小设备数
        blConfigParam.put(BLConfigParam.CONTROLLER_QUERY_COUNT, "8");
        blConfigParam.put(BLConfigParam.CONTROLLER_RESEND_MODE, "0");
        
        // 10. 设置认证包名，默认为APP自身包名
        blConfigParam.put(BLConfigParam.CONTROLLER_AUTH_PACKAGE_NAME, packageName);
        
        // 11. 使用APPService服务
        blConfigParam.put(BLConfigParam.APP_SERVICE_ENABLE, useCluster ? "1" : "0");
        
        // 12. for test, 设置集群域名
        //blConfigParam.put(BLConfigParam.APP_SERVICE_HOST, "https://01e78622f3e6b3a861133fbc0690f4a9appservice.ibroadlink.com"); 
        //BLApiUrlConstants.init("01e78622f3e6b3a861133fbc0690f4a9"); 
        
        BLLet.init(this, license, "", blConfigParam);

        // 初始化之后，获取 lid 和 companyId ，用于其他类库的初始化
        String lid = BLLet.getLicenseId();
        String companyId = BLLet.getCompanyid();

        // 初始化账户库
        BLAccount.init(companyId, lid);
        
        // 初始化红外码库
        BLIRCode.init(lid, blConfigParam);
        
        // 注册红外callback
        BLIRCode.startRMSubDeviceWork();
        
        // 添加登录成功回调函数
        BLAccount.addLoginListener(BLLet.Controller.getLoginListener());
        BLAccount.addLoginListener(BLIRCode.getLoginListener());

        // 初始化家庭管理接口
        BLSFamilyManager.getInstance().init(lid);

        BLApiUrlConstants.init(lid);
    }

    /**
     * 结束app
     */
    public void appFinish(){
        BLLet.finish();
        
        for (Activity item: mActivityList){
            item.finish();
        }

        System.exit(0);
    }


    /**
     * 获得栈顶的activity
     */
    public Activity getTopActivity(){
        return mActivityList.get(mActivityList.size()-1);
    }

    /**
     * 获取手机屏幕信息
     **/
    private void queryPhoneWindowInfo() {
        // 获得屏幕高度（像素）
        BLSettings.P_HEIGHT = getResources().getDisplayMetrics().heightPixels;
        // 获得屏幕宽度（像素）
        BLSettings.P_WIDTH = getResources().getDisplayMetrics().widthPixels;

        //判断是不是手机被横屏之后，设备宽高获取错误。
        if (BLSettings.P_HEIGHT < BLSettings.P_WIDTH) {
            int temp = BLSettings.P_HEIGHT;
            BLSettings.P_HEIGHT = BLSettings.P_WIDTH;
            BLSettings.P_WIDTH = temp;
        }

        BLSettings.STATUS_HEIGHT = getStatusBarHeight();
        BLSettings.NAVIGATION_HEIGHT = getNavigationBarHeight();
    }

    private int getNavigationBarHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * 获取状态栏高度
     */
    private int getStatusBarHeight() {
        try {
            Class<?> cls = Class.forName("com.android.internal.R$dimen");
            Object obj = cls.newInstance();
            Field field = cls.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
        }
        return 0;
    }
    
    /**
     * 初始化云推送通道
     */
    private void initPushService(final Context applicationContext) {
        PushServiceFactory.init(applicationContext);
        mAliPushService = PushServiceFactory.getCloudPushService();
        mAliPushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                Log.i("push", "init cloudchannel success " + mAliPushService.getDeviceId());
            }
            @Override
            public void onFailed(String errorCode, String errorMessage) {
                Log.e("push", "init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
            }
        });
    }
}
