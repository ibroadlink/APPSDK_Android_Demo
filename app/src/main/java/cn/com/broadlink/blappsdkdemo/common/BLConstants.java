package cn.com.broadlink.blappsdkdemo.common;

import android.text.TextUtils;

import cn.com.broadlink.blappsdkdemo.BuildConfig;

public class BLConstants {

    /**app文件夹目录**/
    public static final String BASE_FILE_PATH = "broadlink/blTool";
    public static final String ROOT_FILE_PATH = "broadlink";
    
    public static final String BROADLINK_LOG_TAG = "BROADLINK_DEMO";
    
//    // ihc
//    public static final String SDK_PACKAGE_BAIDU = "cn.com.broadlink.econtrol.plus";
//    public static final String SDK_LICENSE_BAIDU = 
//            "0PlPqgTGPZt7CwNNz4lWVm7qQZqm8AdPyooafIrN9QX5UING6RYrag2V2nFqWRIQrFDxVgAAAADoWWz5UyBiHvQQIPyIUhi4XFSykPGAqniglnoIUWhvuHCgxWxDEyF0yb0xHzyz6V5BLR6D0KoiI6QqjWxRKs8JsEkbxXTfoUSQjDzWcfVjcA4AAADzeX7wfU+3ndxs2/3yXOnJrFAlYyFEltcuD9SloQA7kInW0ynCvel2PMHSm6RgRp/xNYhi5LPROx4fnr746yHD";
  
    // ihc
    public static final String SDK_PACKAGE_BAIDU = "cn.com.broadlink.econtrol.international";
    public static final String SDK_LICENSE_BAIDU = "z3pn54M4x0Ad6uuWSI9G++DMebHcudBcmuQc0EzgH2MYC8m3+LcREcvQ/ihQzqUOeVcgXAAAAADjur2NVArwxwkCXFCflaFAvKQ+Zl0lPyrU2admH4eowk/FnWbIxL62RhRPseZ+Prnck2eU+xOGEEUW14Lo14gMsEkbxXTfoUSQjDzWcfVjcAAAAAA=";

     // international
    public static final String SDK_PACKAGE = "com.broadlink.blappsdkdemo";
    public static final String SDK_LICENSE = "4oQxAHVFYnnY7HPuDlYnm0I6pGcRvFTh/Ct2Vv+/5qZDpJJiIweBn2RUUA6oE8InRDV+XAAAAABz4LOxmXdGndIQ0J762DN4lXimLcoYN1h90T3OlpYrQrNgvm0/7+Kdmrgfawtr+QWBY+UBaf8hxk19tobFrLsFsEkbxXTfoUSQjDzWcfVjcAAAAAA=";

    public static final String PAIR_SERVER_PROFILE = BuildConfig.SUPPORT_AUX ? "{\"tcp\":\"device-heartbeat-chn-f05bd82f.ibroadlink.com\"," + "\"http\":\"device-gateway-chn-f05bd82f.ibroadlink.com\"}" : "{\"tcp\":\"device-heartbeat-chn-ee08f451.ibroadlink.com\",\"http\":\"device-gateway-chn-ee08f451.ibroadlink.com\"}";
    
    /**MD5加密后缀*/
    public static final String STR_BODY_ENCRYPT = "xgx3d*fe3478$ukx";
    
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
    //子设备备份
    public static final String FILE_SUB_DEV_BACKUP = "SubdevBackup";
    //子设备备份
    public static final String FILE_STRESS_TEST_LOG_PATH = "StressTestLog";
    //CrashLog
    public static final String FILE_CRASH_LOG_PATH = "CrashLog";
    //Frimware log
    public static final String FILE_FIRMWARE_LOG_PATH = "FirmwareLog";
    //家庭文件夹名称
    public static final String FILE_FAMILY = "Family";
    //离线图标
    public static final String OFF_LINE_ICON = "OffLineIcon";

    /** 隐藏文件夹 存放S1的传感器信息文件 **/
    public static final String FILE_S1 = ".S1";

    /** IRCode Device type **/
    public static final int BL_IRCODE_DEVICE_TV = 1;
    public static final int BL_IRCODE_DEVICE_TV_BOX = 2;
    public static final int BL_IRCODE_DEVICE_AC = 3;
    
    public final static class IR_AREA_TYPE{
        public final static String COUNTRY = "getcountry";
        public final static String PROVINCE = "getprovince";
        public final static String CITY = "getcity";
    }
    
    public final static class SPACE_URL{
        private static String DOMAIN = "";
        
        public static String ADD() {return DOMAIN + "/appsync/group/space/manage?operation=add";} 
        public static String DEL() {return DOMAIN + "/appsync/group/space/manage?operation=del";}
        public static String MDF() {return DOMAIN + "/appsync/group/space/manage?operation=update";}
        public static String QUE() {return DOMAIN + "/appsync/group/space/query";}
        public static String MOV() {return DOMAIN + "/appsync/group/space/manage?operation=move";}

        public static String QUE_DEV() {return DOMAIN + "/appsync/group/spaceresource/dev/query";}
        public static String QUE_DEV_BY_USER() {return DOMAIN + "/appsync/group/spaceresource/dev/queryuserdev";}
        
        public static void init(String lid, String domain){
            assert !(TextUtils.isEmpty(lid) && TextUtils.isEmpty(domain));
            
            if(TextUtils.isEmpty(domain)){
                DOMAIN =  String.format("https://%sappservice.ibroadlink.com", lid);
            }else{
                DOMAIN = domain;
            }
            if(DOMAIN.endsWith("/")){
                DOMAIN = DOMAIN.substring(0, DOMAIN.length() - 1);
            }
        }
    }

    /** Intent Buddle-key **/
    public static final String INTENT_NAME = "INTENT_NAME";
    public static final String INTENT_CODE = "INTENT_CODE";
    public static final String INTENT_VALUE = "INTENT_VALUE";
    public static final String INTENT_EMAIL = "INTENT_EMAIL";
    public static final String INTENT_PHONE = "INTENT_PHONE";
    public static final String INTENT_EXTEND = "INTENT_EXTEND";
    public static final String INTENT_ID = "INTENT_ID";
    public static final String INTENT_FAMILY_ID = "INTENT_FAMILY_ID";
    public static final String INTENT_ACTION = "INTENT_ACTION";
    public static final String INTENT_URL = "INTENT_URL";
    public static final String INTENT_MODULE = "INTENT_MODULE";
    public static final String INTENT_DEVICE = "INTENT_DEVICE";
    public static final String INTENT_PARCELABLE = "INTENT_PARCELABLE";
    public static final String INTENT_SERIALIZABLE = "INTENT_SERIALIZABLE";
    public static final String INTENT_MODEL = "INTENT_MODEL";
    public static final String INTENT_CLASS = "INTENT_CLASS";
    public static final String INTENT_PARAM = "INTENT_PARAM";
    public static final String INTENT_TYPE = "INTENT_TYPE";
    public static final String INTENT_ARRAY = "INTENT_ARRAY";
    public static final String INTENT_TITLE = "INTENT_TITLE";

}
