package cn.com.broadlink.blappsdkdemo;

import android.app.Application;
import android.content.res.Resources;

import net.sqlcipher.database.SQLiteDatabase;

import java.lang.reflect.Field;

import cn.com.broadlink.account.BLAccount;
import cn.com.broadlink.base.BLConfigParam;
import cn.com.broadlink.blappsdkdemo.common.BLSettings;
import cn.com.broadlink.blappsdkdemo.common.BLUserInfoUnits;
import cn.com.broadlink.blappsdkdemo.service.BLLocalDeviceManager;
import cn.com.broadlink.family.BLFamily;
import cn.com.broadlink.ircode.BLIRCode;
import cn.com.broadlink.sdk.BLLet;

public class BLApplcation extends Application{

    public static BLUserInfoUnits getmBLUserInfoUnits() {
        return mBLUserInfoUnits;
    }

    public static void setmBLUserInfoUnits(BLUserInfoUnits mBLUserInfoUnits) {
        BLApplcation.mBLUserInfoUnits = mBLUserInfoUnits;
    }

    public static BLUserInfoUnits mBLUserInfoUnits;

    @Override
    public void onCreate() {
        super.onCreate();
        SQLiteDatabase.loadLibs(this);

        sdkInit();
        queryPhoneWindowInfo();

        mBLUserInfoUnits = new BLUserInfoUnits(this);
        BLLocalDeviceManager.getInstance();
    }

    /**
     * APPSDK 初始化函数
     */
    private void sdkInit(){

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
        blConfigParam.put(BLConfigParam.CONTROLLER_REMOTE_TIMEOUT, "5000");
        // 6. 设置控制重试次数，默认 1
        blConfigParam.put(BLConfigParam.CONTROLLER_SEND_COUNT, "1");
        // 7. 设置设备控制支持的网络模式，默认 -1 都支持。  0 - 局域网控制，非0 - 局域网/远程都支持。
        blConfigParam.put(BLConfigParam.CONTROLLER_NETMODE, "-1");
        // 8. 设置脚本和UI文件下载资源平台。 默认 0 老平台。  1 - 新平台
        blConfigParam.put(BLConfigParam.CONTROLLER_SCRIPT_DOWNLOAD_VERSION, "1");
        // 9. 批量查询设备在线状态最小设备数
        blConfigParam.put(BLConfigParam.CONTROLLER_QUERY_COUNT, "8");

        BLLet.init(this, blConfigParam);

        // 初始化之后，获取 lid 和 companyId ，用于其他类库的初始化
        String lid = BLLet.getLicenseId();
        String companyId = BLLet.getCompanyid();

        // 初始化家庭库
        BLFamily.init(companyId, lid);
        // 初始化账户库
        BLAccount.init(companyId, lid);
        // 初始化红外码库
        BLIRCode.init(lid, blConfigParam);

        // 添加登录成功回调函数
        BLAccount.addLoginListener(BLLet.Controller.getLoginListener());
        BLAccount.addLoginListener(BLFamily.getLoginListener());
        BLAccount.addLoginListener(BLIRCode.getLoginListener());
    }

    public void appFinish(){
        BLLet.finish();
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

    //获取状态栏高度
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
}
