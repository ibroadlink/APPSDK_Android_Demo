package cn.com.broadlink.blappsdkdemo.common;

public class BLConstants {

    public static class APPConfigInfo{
        /**app文件夹目录**/
        public static final String BASE_FILE_PATH = "broadlink/eControl";
        
        /**app版本号**/
        public static final int APP_VERSION = 6;
        
        public static final String BROADLINK = "BroadLink";
    }

    public static final String BROADLINK_LOG_TAG = "BROADLINK_DEMO";

    /** 文件夹名称 存放一些APP临时数据**/
    public static final String FILE_DATA = "data";
    //分享备份文件夹
    public static final String FILE_SHARE = "SharedData";
    //云空调控制指令
    public static final String FILE_CON_CODE = "ConCode";
    //设备图标
    public static final String FILE_DEVICE_ICON = "DeviceIcon";
    //RM数据
    public static final String FILE_IR_DATA = "IrData";
    //场景图标
    public static final String SCENE_NAME = "SceneIcon";
    //设备脚本文件
    public static final String FILE_SCRIPTS = "Scripts";
    //存放下载下来的HTML5文件夹
    public static final String FILE_DRPS = "Drps";
    //MS1背景图片
    public static final String FILE_MS1 = "MS1";
    //家庭文件夹名称
    public static final String FILE_FAMILY = "Family";
    //离线图标
    public static final String OFF_LINE_ICON = "OffLineIcon";

    /** 隐藏文件夹 存放S1的传感器信息文件 **/
    public static final String FILE_S1 = ".S1";

    //IRCode Device type
    public static final int BL_IRCODE_DEVICE_TV = 1;
    public static final int BL_IRCODE_DEVICE_TV_BOX = 2;
    public static final int BL_IRCODE_DEVICE_AC = 3;

    public static final String INTENT_NAME = "INTENT_NAME";
    public static final String INTENT_CODE = "INTENT_CODE";
    public static final String INTENT_VALUE = "INTENT_VALUE";
    public static final String INTENT_EMAIL = "INTENT_EMAIL";
    public static final String INTENT_PHONE = "INTENT_PHONE";
    public static final String INTENT_EXTEND = "INTENT_EXTEND";
    public static final String INTENT_ID = "INTENT_ID";
    public static final String INTENT_FAMILY_ID = "INTENT_FAMILY_ID";
}
