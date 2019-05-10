package cn.com.broadlink.blappsdkdemo.common;

import java.io.File;

public class BLApiUrlConstants {

    public static void init(String lid){
        
        BASE_FAMILY_URL = String.format(BASE_FAMILY_URL, lid);

        BASE_APP_MANAGE = String.format(BASE_APP_MANAGE, lid);

        BASE_VIRTUAL_DEV = String.format(BASE_VIRTUAL_DEV, lid);

        CLOUD_NEW_BASE = String.format(CLOUD_NEW_BASE, lid);

        BASE_PUSH_URL = String.format(BASE_PUSH_URL, lid);

        PRIVATE_DATA = String.format(PRIVATE_DATA, lid);

        BASE_RESOURCE_URL = String.format(BASE_RESOURCE_URL, lid);

        BASE_LINKAGE = String.format(BASE_LINKAGE, lid);

        BASE_CLOUD_TIMERURL = String.format(BASE_CLOUD_TIMERURL, lid);

        BASE_FEEDBACK_URL = String.format(BASE_FEEDBACK_URL, lid);

        BASE_MSG_PUSH_URL = String.format(BASE_MSG_PUSH_URL,lid);

        CLOUD_USER_LIB_NEW_BASE = String.format(CLOUD_USER_LIB_NEW_BASE,lid);

        FW_VERSION = String.format(FW_VERSION,lid);

        FASTCON_CONFIG_URL = String.format(FASTCON_CONFIG_URL,lid);

        BASE_DATASERVICE = String.format(BASE_DATASERVICE,lid);

        QUERY_DEV_HISTORY = String.format(QUERY_DEV_HISTORY,lid);
    }

    public static String BASE_URL_APPSERVICE = "https://%sappservice.ibroadlink.com";

    /**APP 家庭域名**/
    public static String BASE_FAMILY_URL = BASE_URL_APPSERVICE;
    /**APP 管理后台域名**/
    public static String BASE_APP_MANAGE = BASE_URL_APPSERVICE;
    /**虚拟设备**/
    public static String BASE_VIRTUAL_DEV = BASE_URL_APPSERVICE;
    /**红码接口**/
    public static String CLOUD_NEW_BASE = BASE_URL_APPSERVICE;
    /**用户库红码接口**/
    public static String CLOUD_USER_LIB_NEW_BASE = BASE_URL_APPSERVICE;
    /**APP 推送域名**/
    public static String BASE_PUSH_URL = BASE_URL_APPSERVICE;
    /**App 消息推送中心域名**/
    public static String BASE_MSG_PUSH_URL = BASE_URL_APPSERVICE;

    /**意见反馈服务**/
    public static String BASE_FEEDBACK_URL = BASE_URL_APPSERVICE + "/ec4/";
    /**联动域名**/
    public static String BASE_LINKAGE = BASE_FAMILY_URL + "/ec4/";
    
    /**固件版本**/
    public static String FW_VERSION = BASE_URL_APPSERVICE + "/getfwversion";
    /**云端数据服务接口 域名**/
    public static String BASE_DATASERVICE = BASE_URL_APPSERVICE + "/dataservice/";
    /**查询设备执行记录**/
    public static  String QUERY_DEV_HISTORY = BASE_DATASERVICE + "v1/device/status";
    /**fastcon配网URL**/
    public static String FASTCON_CONFIG_URL = BASE_URL_APPSERVICE + "/appfront/v1/webui/fastcon/?";
    
    /**私有数据域名**/
    public static String PRIVATE_DATA = BASE_URL_APPSERVICE + File.separator;
    /**资源域名**/
    public static String BASE_RESOURCE_URL = BASE_FAMILY_URL + File.separator;
    /**云定时域名**/
    public static String BASE_CLOUD_TIMERURL = BASE_URL_APPSERVICE + File.separator;



    //////////////////////////////////////////////APP 管理后台//////////////////////////////////////////////////
    public static class AppManager{
        /**云端负载电器列表**/
        public static final String DEVICE_APPLIANCE_LIST() { return BASE_APP_MANAGE + "/ec4/v1/system/getelectricload";}

        /**产品图标**/
        public static final String PRODUCT_ICON() { return BASE_APP_MANAGE + "/ec4/v1/system/configfile";}

        /**获取产品目录**/
        public static final String ADD_PRODUCT_DIRECTORY() { return BASE_APP_MANAGE + "/ec4/v1/system/resource/categorylist";}

        /**查询分类下的品牌列表**/
        public static final String QUERY_BRADN_LIST() { return BASE_APP_MANAGE + "/ec4/v1/system/resource/productbrand/list";}

        /**获取产品列表**/
        public static final String QUERY_PRODUCT_LIST_BY_BRADN() { return BASE_APP_MANAGE + "/ec4/v1/system/resource/productbrandfilter/list";}

        /**获取产品列表**/
        public static final String ADD_PRODUCT_LIST() { return BASE_APP_MANAGE + "/ec4/v1/system/resource/productlist";}

        /**批量查询产品列表**/
        public static final String BATCH_QUERY_PRODUCT_LIST() {return BASE_APP_MANAGE + "/ec4/v1/system/resource/product/version";}

        /**c查询产品详情***/
        public static final String ADD_PRODUCT_DETAIL() { return BASE_APP_MANAGE + "/ec4/v1/system/resource/product/info";}

        /**通过序列号查询产品详情**/
        public static final String ADD_QR_PRODUCT_DETAIL() { return BASE_APP_MANAGE + "/ec4/v1/system/resource/product/qrcode";}

        /**请求虚拟设备DID*/
        public static final String APPLY_VIRTUAL_DEV() { return BASE_VIRTUAL_DEV + "/ec/v2/thirdparty/dev/didapply";}

        /**设备授权认证**/
        public static final String DEVICE_AUTH() { return BASE_FAMILY_URL + "/ec4/v1/auth/add";}

        /**设备授权列表**/
        public static final String DEVICE_AUTH_LIST() { return BASE_FAMILY_URL + "/ec4/v1/auth/query";}
    }


    ////////////////////////////////////////////////家庭/////////////////////////////////////////////////////
    public static class Family{

        /**灯图标地址**/
        public static final String GET_LAMP_ICON(){return BASE_FAMILY_URL + "/ec4appsysinfo/moduleicon/lamp.png";}

        /**窗帘图标地址**/
        public static final String GET_CURTAIN_ICON() {return BASE_FAMILY_URL + "/ec4appsysinfo/moduleicon/curtain_on.png";}

        /**开关图标地址**/
        public static final String GET_SWITCH_ICON(){return BASE_FAMILY_URL + "/ec4appsysinfo/moduleicon/icon_switch.png";}

        /**风扇图标地址**/
        public static final String GET_FAN_ICON(){return BASE_FAMILY_URL + "/ec4appsysinfo/moduleicon/fan.png";}

        /**投影仪图标地址**/
        public static final String GET_PROJECTOR_ICON(){return BASE_FAMILY_URL + "/ec4appsysinfo/moduleicon/touyingyi.png";}

        /**互联网盒子图标地址**/
        public static final String GET_OTT_ICON(){return BASE_FAMILY_URL + "/ec4appsysinfo/moduleicon/ott.png";}

        /**DVD图标地址**/
        public static final String GET_DVD_ICON(){return BASE_FAMILY_URL + "/ec4appsysinfo/moduleicon/DVD@1x.png";}

        /**功放图标地址**/
        public static final String GET_AMPLIFIER_ICON(){return BASE_FAMILY_URL + "/ec4appsysinfo/moduleicon/gongfang.png";}

        /**相机图标地址**/
        public static final String GET_CAMERA_ICON(){return BASE_FAMILY_URL + "/ec4appsysinfo/moduleicon/xiangji.png";}

        /**热水器图标地址**/
        public static final String GET_HEATER_ICON(){return BASE_FAMILY_URL + "/ec4appsysinfo/moduleicon/reshuiqi.png";}

        /**空气净化器图标地址**/
        public static final String GET_AIR_PURIFIER_ICON(){return BASE_FAMILY_URL + "/ec4appsysinfo/moduleicon/kongqijinhuaqi.png";}

        /**门图标地址**/
        public static final String GET_DOOR_ICON(){return BASE_FAMILY_URL + "/ec4appsysinfo/moduleicon/men.png";}

        /**音响图标地址**/
        public static final String GET_AUDIO_ICON(){return BASE_FAMILY_URL + "/ec4appsysinfo/moduleicon/yinxiang.png";}

        /**扫地机器人图标地址**/
        public static final String GET_SWEEPING_ROBOT_ICON(){return BASE_FAMILY_URL + "/ec4appsysinfo/moduleicon/saodijiqiren.png";}

        /**加湿器图标地址**/
        public static final String GET_HUMIDIFIER_ICON(){return BASE_FAMILY_URL + "/ec4appsysinfo/moduleicon/jiashiqi.png";}

        /**晾衣架图标地址**/
        public static final String GET_CLOTHES_HANGER_ICON(){return BASE_FAMILY_URL + "/ec4appsysinfo/moduleicon/liangyijia.png";}
    }

    public static class PushServicer{
        /**1、	用户设置选定的设备(followdev)**/
        public static String ADD_DID() { return BASE_PUSH_URL + "/ec4/v1/pusher/followdev/manage"; }

        /**2、	查询选定的设备(followdev)**/
        public static String QUERY() { return BASE_PUSH_URL + "/ec4/v1/pusher/followdev/query"; }

        /**5、 查询喜好推送方式（favortype**/
        public static String GET_TYPE() { return BASE_PUSH_URL + "/ec4/v1/pusher/favorpushtype/query"; }


        /**3、	用户上报**/
        public static String REG() { return BASE_PUSH_URL + "/appfront/v1/pusher/registerforremotenotify"; }

        /**9、 设置开关 **/
        public static String SET_TYPE() { return BASE_PUSH_URL + "/ec4/v1/pusher/favorpushtype/manage";}
        
        /**8、 用户退出登录**/
        public static String LOGOUT() { return BASE_PUSH_URL + "/ec4/v1/pusher/signoutaccount"; }
        
        /**9、 模版查询**/
        public static String TEMP_QUERY() { return BASE_PUSH_URL + "/appfront/v1/tempalte/query"; }

        /**9、 联动查询 **/
        public static String LINK_QUERY() { return BASE_PUSH_URL + "/appfront/v2/linkage/query"; }
        
        /**9、 联动增加 **/
        public static String LINK_ADD() { return BASE_PUSH_URL + "/appfront/v2/linkage/add"; }

        /**9、 联动删除 **/
        public static String LINK_DEL() { return BASE_PUSH_URL + "/appfront/v2/linkage/delete"; }

    }


    //获取用户库数据
    public static final String QUERY_RM_USER_BRAND(){
        return CLOUD_NEW_BASE + "/publicircode/v2/app/getfuncfilebyirid";
    }
}
