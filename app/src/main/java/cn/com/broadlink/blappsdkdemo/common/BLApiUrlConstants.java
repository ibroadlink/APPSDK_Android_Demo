package cn.com.broadlink.blappsdkdemo.common;

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
    }

    /**意见反馈服务**/
    public static String BASE_FEEDBACK_URL = "https://%sappservice.ibroadlink.com/ec4/";

    /**云定时域名**/
    public static String BASE_CLOUD_TIMERURL = "https://%sappservice.ibroadlink.com/";

    /**APP 家庭域名**/
    public static String BASE_FAMILY_URL = "https://%sappservice.ibroadlink.com";
    /**APP 管理后台域名**/
    public static String BASE_APP_MANAGE = "https://%sappservice.ibroadlink.com";
    /**虚拟设备**/
//    public static String BASE_VIRTUAL_DEV = "http://%svirtualdevregister.ibroadlink.com";
    public static String BASE_VIRTUAL_DEV = "https://%sappservice.ibroadlink.com";
    /**固件版本**/
    public static String FW_VERSION = "https://%sappservice.ibroadlink.com/getfwversion";
    /**电视云码域名**/
    private static final String BASE_TV_URL = "http://publictvir.ibroadlink.com";
    /**意见反馈域名**/
    public static final String FEEDBACK = "http://feedback.ibroadlink.com/feedback/report";

    /**云端数据服务接口 域名**/
    public static final String BASE_DATASERVICE = "https://%srtasquery.ibroadlink.com/dataservice/";

    /**查询设备执行记录**/
    public static final String QUERY_DEV_HISTORY = BASE_DATASERVICE + "v1/device/status";

    /**设备能耗查询**/
    public static final String QUERY_DEV_ELEC = BASE_DATASERVICE + "v1/device/stats";

    /**私有数据域名**/
    public static String PRIVATE_DATA = "https://%sappservice.ibroadlink.com/";

    /**联动域名**/
    public static String BASE_LINKAGE = BASE_FAMILY_URL + "/ec4/";

    /**资源域名**/
    public static String BASE_RESOURCE_URL = BASE_FAMILY_URL + "/";

    /**历史状态查询*/
    public static final String SP_HOSTROY = "http://cn-clouddb.ibroadlink.com/spmini/history/status";
    /**MS1 版本号查询**/
    public static final String MS1_VERSION_URL = "http://upgrade.ibroadlink.com/sw/musicbox/stable/version.html";
    /**检查第三方设备合法性**/
    public static final String THRID_DEV_VALIDITY_CHECK_RUL = "http://3rddevrecognize.ibroadlink.com/ec/v1/thirdparty/productinfo/check";
    /**红码接口**/
    public static String CLOUD_NEW_BASE = "https://%sappservice.ibroadlink.com";

    /**用户库红码接口**/
    public static String CLOUD_USER_LIB_NEW_BASE= "https://%sappservice.ibroadlink.com";

    /**APP 推送域名**/
    public static String BASE_PUSH_URL = "https://%sappservice.ibroadlink.com";

    /**App 消息推送中心域名**/
    public static String BASE_MSG_PUSH_URL = "https://%sappservice.ibroadlink.com";

    /**fastcon配网URL**/
    public static final String FASTCON_CONFIG_URL = "https://d0f94faa04c63d9b7b0b034dcf895656appservice.ibroadlink.com/appfront/v1/webui/fastcon/?";

    // //////////////////////////////////////////////微信/////////////////////////////////////////////////////
    public static final String WEIXIN_REQUEST_TOKEN = "http://wechat.ibroadlink.com/control/key";

    public static final String WEIXIN_REQUEST_AUTHORIZE = "https://api.weixin.qq.com/device/authorize_device?access_token=";

    public static final String WEIXIN_REQUEST_QR = "https://api.weixin.qq.com/device/create_qrcode?access_token=";


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

        /**获取智能插座详情*/
        public static final String GET_SP_APPLIANCE_LIST() { return BASE_APP_MANAGE + "/ec4/v1/system/getsmartplugapp";}

        /** 获取家庭下的云端默认房间列表 **/
        public static final String QUERY_DEFAULT_ROOM() { return BASE_FAMILY_URL + "/ec4/v1/system/defineroom";}

        /**请求虚拟设备DID*/
        public static final String APPLY_VIRTUAL_DEV() { return BASE_VIRTUAL_DEV + "/ec/v2/thirdparty/dev/didapply";}

        /**设备授权认证**/
        public static final String DEVICE_AUTH() { return BASE_FAMILY_URL + "/ec4/v1/auth/add";}

        /**设备授权列表**/
        public static final String DEVICE_AUTH_LIST() { return BASE_FAMILY_URL + "/ec4/v1/auth/query";}

        /**国家地区位置配置文件目录**/
        public static final String LOCATION_CONFIG_URL() { return BASE_APP_MANAGE + "/ec4/v1/system/getlocatelist";}

        /** 场景相关的信息 **/
        public static final String GET_SCENE_DETAIL() { return BASE_APP_MANAGE + "/ec4/v1/system/scenedetail";}

        /** 通过IP地址获取当前所在位置 **/
        public static final String GET_LOCATE() { return BASE_APP_MANAGE + "/ec4/v1/system/getlocate";}

        /**通过SN码获取PID和DID**/
        public static final String PID_BY_SN() { return BASE_APP_MANAGE + "/ec4/v1/system/product/pidbysn";}
    }

    /**云定时**/
    public static class CloudTimer{
        /**查询定时任务列表**/
        public static final String QUERY_TIMER_LIST() { return BASE_CLOUD_TIMERURL + "ec4appsysinfo/app/update";}
    }

    ////////////////////////////////////////////////家庭/////////////////////////////////////////////////////
    public static class Family{
        public static final String NOTIFY_PUSH_QUERY() { return BASE_FAMILY_URL + "/appfront/v1/notifypush/query";}

        //////////////////////////////////////////////RM 模块图标//////////////////////////////////////////////////
        public static final String RM_STATISTICS() { return BASE_FAMILY_URL + "/ec4appsysinfo/moduleicon/icon_module_statistics.png";}

        /**APP帮助页面**/
        public static final String APP_HELP_URL() { return BASE_FAMILY_URL +  "/ec4appsysinfo/helppage/help.html";}

        /** 获取家庭时间戳 **/
        public static final String FAMILY_REQUEST_TIMESTAMP() { return BASE_FAMILY_URL + "/ec4/v1/common/api";}

        /** 获取设备所在的家庭 **/
        public static final String GET_DEVICE_FAMILY() { return BASE_FAMILY_URL + "/ec4/v1/dev/getfamily";}

        /**获取家庭图标列表**/
//        public static final String GET_FAMILY_ICON_LIST() { return BASE_FAMILY_URL + "/ec4/v1/system/definefamilypic";}

        /**获取RM 自定义按钮列表**/
        public static final String GET_RM_BTN_ICON_LIST() { return BASE_FAMILY_URL + "/ec4/v1/system/defineircodeicon";}

        /** 创建家庭 **/
        public static final String CREATE_FAMILY() { return BASE_FAMILY_URL + "/ec4/v1/family/add";}

        /** 修改家庭信息 **/
        public static final String EDIT_FAMILY_INFO() { return BASE_FAMILY_URL + "/ec4/v1/family/modifyinfo";}

        /** 修改家庭图标 **/
        public static final String EDIT_FAMILY_ICON() { return BASE_FAMILY_URL + "/ec4/v1/family/modifyicon";}

        /** 修改家庭房间列表 **/
        public static final String EDIT_ROOM_LIST() { return BASE_FAMILY_URL + "/ec4/v1/room/manage";}

        /** 请求加入家庭的二维码 **/
        public static final String REQUEST_FAMILY_QRCODE() { return BASE_FAMILY_URL + "/ec4/v1/member/invited/reqqrcode";}

        /** 请求二维码家庭的信息 **/
        public static final String REQUEST_QRCODE_FAMILY_INFO() { return BASE_FAMILY_URL + "/ec4/v1/member/invited/scanqrcode";}

        /** 通过家庭邀请码加入家庭 **/
        public static final String FAMILY_JOIN_BY_QR() { return BASE_FAMILY_URL + "/ec4/v1/member/invited/joinfamily";}

        /** 加入公开家庭 **/
        public static final String JOIN_PUBLIC_FAMILY() { return BASE_FAMILY_URL + "/ec4/v1/member/joinpublicfamily";}

        /** 获取家庭成员 **/
        public static final String FAMILY_NUM_QUERY() { return BASE_FAMILY_URL + "/ec4/v1/member/getfamilymember";}

        /** 删除家庭成员 **/
        public static final String FAMILY_NUM_DELETE() { return BASE_FAMILY_URL + "/ec4/v1/member/delfamilymember";}

        /** 获取已经配置的家庭列表 **/
        public static final String GET_CONFIG_DEVICE_LIST() { return BASE_FAMILY_URL + "/ec4/v1/dev/getconfigdev";}

        /** 请求云端家庭版本列表 **/
        public static final String GET_FAMILY_VERSION_LIST() { return BASE_FAMILY_URL + "/ec4/v1/user/getfamilyid";}

        /** 请求云端单个家庭版本 **/
        public static final String GET_FAMILY_VERSION() { return BASE_FAMILY_URL + "/ec4/v1/family/getversion";}

        /** 请求云端家庭下的所有信息 **/
        public static final String GET_FAMILY_LIST_DETAL_INFO() { return BASE_FAMILY_URL + "/ec4/v1/family/getallinfo";}

        /** 创建默认家庭和房间接口 **/
        public static final String CREATE_DEFAULT_FAMILY() { return BASE_FAMILY_URL + "/ec4/v1/family/default";}

        /** 请求栏目下的分类列表 **/
        public static final String GET_CATEGORY_LIST() { return BASE_FAMILY_URL + "/ec4/v1/system/getchilddir";}

        /** 模块添加 **/
        public static final String ADD_MODULE() { return BASE_FAMILY_URL + "/ec4/v1/module/add";}

        /** 批量修改模块顺序 **/
        public static final String MODIFY_MODULE_ORDER() { return BASE_FAMILY_URL + "/ec4/v1/module/modifyorder";}

        /** 批量模块添加 **/
        public static final String ADD_MODULE_LIST() { return BASE_FAMILY_URL + "/ec4/v1/module/addlist";}

        /** 修改模块标志位**/
        public static final String EDIT_MODULE_FLAG() { return BASE_FAMILY_URL + "/ec4/v1/module/modifyflag";}

        /** 模块信息修改**/
        public static final String EDIT_MODULE_INFO_AND_ROOM() { return BASE_FAMILY_URL + "/ec4/v1/module/modifyandmovemodule";}

        /** 模块信息修改**/
        public static final String EDIT_MODULE() { return BASE_FAMILY_URL + "/ec4/v1/module/modify";}

        /** 模块信息修改New**/
        public static final String EDIT_MODULE_NEW() { return BASE_FAMILY_URL + "/ec4/v1/module/modifybasicinfo";}

        /** 删除模块 **/
        public static final String DELETE_MODULE() { return BASE_FAMILY_URL + "/ec4/v1/module/del";}

        /** 批量删除模块列表 **/
        public static final String DELETE_MODULE_LIST() { return BASE_FAMILY_URL + "/ec4/v1/module/dellist";}

        /** 删除设备**/
        public static final String DELETE_DEV() { return BASE_FAMILY_URL + "/ec4/v1/dev/deldev";}

        /** 模块房间修改 **/
        public static final String MODFIY_MODULE_ROOM() { return BASE_FAMILY_URL + "/ec4/v1/module/movemodule";}

        /** 设置峰谷时间电价 **/
        public static final String SET_PEAK_INFO() { return BASE_FAMILY_URL + "/ec4/v1/electricinfo/config";}

        /** 查询峰谷时间电价 **/
        public static final String QUERY_PEAK_INFO() { return BASE_FAMILY_URL + "/ec4/v1/electricinfo/query";}

        /** 删除家庭 **/
        public static final String DELETE_FAMILY() { return BASE_FAMILY_URL + "/ec4/v1/family/del";}

        /** 推出家庭 **/
        public static final String QUIT_FAMILY() { return BASE_FAMILY_URL + "/ec4/v1/member/quitfamily";}

        /** 设备房间修改 **/
        public static final String DEV_ROOM_EDIT() { return BASE_FAMILY_URL + "/ec4/v1/dev/movedev";}

        /** 添加图片 **/
        public static final String ADD_PIC() { return BASE_FAMILY_URL + "/ec4/v1/system/addpic";}

        /** 模块名称修改 **/
        public static final String MODULE_NAME_EDIT() { return BASE_FAMILY_URL + "/ec4/v1/module/modifyname";}

        /** 模块关联关系修改 **/
        public static final String MODULE_RELATION_EDIT() { return BASE_FAMILY_URL + "/ec4/v1/module/modifyrelation";}

        /** 添加家庭私有数据 **/
        public static final String SET_USER_PRIVATE_DAT() { return BASE_FAMILY_URL + "/ec4/v1/family/upsertprivatedata";}

        /** 获取家庭私有数据 **/
        public static final String GET_USER_PRIVATE_DAT() { return BASE_FAMILY_URL + "/ec4/v1/family/getprivatedata";}

        /** 联动添加 **/
        public static final String LINKAGE_ADD() { return BASE_FAMILY_URL + "/ec4/v1/linkage/add";}

        /** 获取家庭私有数据 **/
        public static final String LINKAGE_DELETE() { return BASE_FAMILY_URL + "/ec4/v1/linkage/delete";}

        /** 获取家庭私有数据 **/
        public static final String LINKAGE_QUERY() { return BASE_FAMILY_URL + "/ec4/v1/linkage/query";}

        /** 获取家庭私有数据 **/
        public static final String LINKAGE_UPDATE() { return BASE_FAMILY_URL + "/ec4/v1/linkage/update";}

        /** 批量删除设备接口*/
        public static final String DELETE_FAMILY_DEVICE_LIST() { return BASE_FAMILY_URL + "/ec4/v1/dev/dellist";}

        /** 授权中心 获取所有权限列表**/
        public static final String AUTH_LIST() { return BASE_FAMILY_URL + "/ec4/v1/authmanager/auth/list";}

        /** 授权中心 获取某权限下设备列表**/
        public static final String GET_DEV_LIST_BY_AUTH() { return BASE_FAMILY_URL + "/ec4/v1/authmanager/auth/devlist/query";}

        /** 授权中心 更新某权限下设备列表*/
        public static final String UPDATE_DEV_LIST_BY_AUTH() { return BASE_FAMILY_URL + "/ec4/v1/authmanager/auth/devlist/update";}

        /** 授权中心 获取已授权的设备列表*/
        public static final String GET_AUTHORIZED_DEV_LIST() { return BASE_FAMILY_URL + "/ec4/v1/authmanager/dev/list";}

        /** 授权中心 获取某设备下权限列表*/
        public static final String GET_DEV_AUTH_LIST() { return BASE_FAMILY_URL + "/ec4/v1/authmanager/dev/authlist/query";}

        /** 授权中心 更新某设备下权限列表*/
        public static final String UPDATE_DEV_AUTH_LIST() { return BASE_FAMILY_URL + "/ec4/v1/authmanager/dev/authlist/update";}

        /**获取用户下家庭基本信息列表**/
        public static final String GET_BASE_FAMILY_LIST() { return BASE_FAMILY_URL + "/ec4/v1/user/getbasefamilylist";}

        /**根据家庭ID，Version返回当前家庭最新数据(ec4serverback)**/
        public static final String GET_FAMILY_INFO_BY_VERSION() { return BASE_FAMILY_URL + "/ec4/v1/family/getfamilyversioninfo";}

        /**查询第三方服务列表 包含绑定的设备列表**/
        public static final String GET_FAMILY_3RD_SERVICE_LIST() { return BASE_FAMILY_URL + "/ec4/v1/module/query3rdquotelist";}

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

        /**3、	用户上报**/
        public static String REG() { return BASE_PUSH_URL + "/ec4/v1/pusher/registerforremotenotify"; }

        /**4、	用户设置选定的喜好推送方式（favortype）**/
        public static String SET_TYPE() { return BASE_PUSH_URL + "/ec4/v1/pusher/favorpushtype/manage"; }

        /**5、 查询喜好推送方式（favortype**/
        public static String GET_TYPE() { return BASE_PUSH_URL + "/ec4/v1/pusher/favorpushtype/query"; }

        /**6、 获取所有推送类型**/
        public static String GET_ALL_TYPE() { return BASE_PUSH_URL + "/ec4/v1/pusher/pushtypelist/query"; }

        /**7、 通过Pid获取相应的推送类型**/
        public static String GET_TYPE_BY_PID() { return BASE_PUSH_URL + "/ec4/v1/pusher/mesgtypebypid/query"; }

        /**8、 用户退出登录**/
        public static String LOGOUT() { return BASE_PUSH_URL + "/ec4/v1/pusher/signoutaccount"; }
    }

    public static class APPFRONT{
        public static String MSG_CENTER_WEB_URL()  {return BASE_MSG_PUSH_URL + "/appfront/v1/webui/notify/";}
        public static String QUERY_TEMPLATE_URL()  {return BASE_MSG_PUSH_URL + "/appfront/v1/tempalte/query";}
        public static String QUERY_LINKAGE_URL()   {return BASE_MSG_PUSH_URL + "/appfront/v1/linkage/query";}
        public static String ADD_LINKAGE_URL()     {return BASE_MSG_PUSH_URL + "/appfront/v1/linkage/add";}
        public static String UPDATE_LINKAGE_URL()  {return BASE_MSG_PUSH_URL + "/appfront/v1/linkage/update";}
    }

    ////////////////////////////////////////////新的红码库//////////////////////////////////////////////
    public static class IrdaAPI {
        // 运营商品牌信息
        public static String CLOUD_PROVIDER_BRAND_LIST() {
            return CLOUD_NEW_BASE + "/publicircode/v2/stb/getproviderbrand";
        }

        // 获取空调/电视类别列表
        public static String CLOUD_BRAND_LIST() {
            return CLOUD_NEW_BASE + "/publicircode/v2/app/getbrand";
        }

        // 获取空调/电视类别列表
        public static String CLOUD_GET_MODLE_LIST() {
            return CLOUD_NEW_BASE + "/publicircode/v2/app/getversion";
        }

        // 获取红码匹配树
        public static String CLOUD_GET_IR_TREE(){
            return CLOUD_NEW_BASE + "/publicircode/v2/app/getmatchtree";
        }

        //根据ircodeid获取下载
        public static String CLOUD_GET_IR_BY_IRID(){
            return CLOUD_NEW_BASE + "/publicircode/v2/app/getircode";
        }

        // 根据品牌型号获取临时下载URL(空调带mtag=app,其他不带，电视使用该URL会正则去掉)
        public static String CLOUD_BRAND_CLASS_LIST() {
            return CLOUD_NEW_BASE + "/publicircode/v2/app/geturlbybrandversion?mtag=app";
        }

        // 空调红码一键识别(空调必须带有mtag=app)
        public static String CLOUD_AC_RECOGNIZE_LIST() {
            return CLOUD_NEW_BASE + "/publicircode/v2/cloudac/recognizeirdata?mtag=app";
        }

        // 获取省份和城市
        public static String CLOUD_PROVINCES_LIST() {
            return CLOUD_NEW_BASE + "/publicircode/v2/app/getsubarea";
        }

        // 获取供应商列表
        public static String CLOUD_PROVIDERS_LIST() {
            return CLOUD_NEW_BASE + "/publicircode/v2/stb/getprovider";
        }

        // 下载某运行商的红码
        public static String CLOUD_PROVINCES_CODE_LIST() {
            return CLOUD_NEW_BASE + "/publicircode/v2/stb/geturlbyarea";
        }

        // 通过区域ID获取区域名称
        public static String CLOUD_AREA_BY_ID() {
            return CLOUD_NEW_BASE + "/publicircode/v2/app/getareainfobyid";
        }

        // 通过供应商ID获取供应商名称信息
        public static String CLOUD_PROVIDER_BY_ID() {
            return CLOUD_NEW_BASE + "/publicircode/v2/stb/getproviderinfobyid";
        }

        // 根据区域ID获取频道列表
        public static String CLOUD_CHANNEL_LIST_BY_ID() {
            return CLOUD_NEW_BASE + "/publicircode/v2/stb/getchannel";
        }
        //获取用户库品牌
        public static String CLOUD_USER_LIB_BRAND(){
            return CLOUD_USER_LIB_NEW_BASE + "/publicircode/v2/app/usercode/getbrand";
        }

        //获取用户库品牌下的红码列表
        public static String CLOUD_USER_LIB_IRCodeLIST(){
            return CLOUD_USER_LIB_NEW_BASE + "/publicircode/v2/app/usercode/getircodelist";
        }

        //用户库红码下载
        public static String CLOUD_USER_LIB_IR_DOWNLOAD(){
            return CLOUD_USER_LIB_NEW_BASE + "/publicircode/v2/app/usercode/downircode?method=download&path=";
        }

        /*** 通过邮编查询所在地区**/
        public static final String GET_LOCATIE_BY_ZIP() { return CLOUD_NEW_BASE + "/publicircode/v2/stb/getlocatebyzip";}
    }


    /**
     * 萤石摄像头子账户API
     */
    public static class EzCameraUrl{
        //        private static final String BASE_URL = "http://172.16.10.169:19990";
        private static final String BASE_URL = BASE_FAMILY_URL;
        // 请求子账户token
        public static final String URL_SUB_ACCOUNT_TOKEN = BASE_URL + "/linkezviz/v1/app/getaccesskeytoken";
        // 添加设备
        public static final String URL_ADD_DEV = BASE_URL + "/linkezviz/v1/app/adddevice";
        // 删除设备
        public static final String URL_DEL_DEV = BASE_URL + "/linkezviz/v1/app/deletedevice";
        // 云台转向
        public static final String URL_PTZ_OPR_START =  "https://open.ys7.com/api/lapp/device/ptz/start";
        // 停止云台转向
        public static final String URL_PTZ_OPR_STOP =  "https://open.ys7.com/api/lapp/device/ptz/stop";
        // 获取单个子账户信息
        public static final String URL_GET_ACCOUNT_INFO =  "https://open.ys7.com/api/lapp/ram/account/get";

    }

    ////////////////////////////////////////////RM 空调系统//////////////////////////////////////////////
    /**公共红码库上传用户设定的频道数据**/
    public static final String UPDATE_CHANNEL = BASE_TV_URL + "/publictv/user/revisechannel";
    /**上传用户学习的code**/
    public static final String UPLOAD_REVISE_IRDA = BASE_TV_URL + "/publictv/user/revisedata";

    /**获取云电视频道列表**/
//    public static final String QUERY_TV_CHANNEL_LIST = BASE_TV_URL + "/publictv/tvchannel/getchannel";
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    //喜马拉雅获取分类下的内容
    public static final String XIMA_GET_CATEGORIES_TAGS = "http://3rd.ximalaya.com/categories/%1$s/tags";
    //喜马拉雅获取分类下的专辑
    public static final String XIMA_GET_CATEGORIES_ALBUMS = "http://3rd.ximalaya.com/categories/%1$s/hot_albums";
    //搜索喜马拉雅获取分类的专辑
    public static final String XIMA_SEARCH_ALBUMS = "http://3rd.ximalaya.com/search/albums";
    //搜索喜马拉雅的声音
    public static final String XIMA_SEARCH_VOICE = "http://3rd.ximalaya.com/search/tracks";
    //喜马拉雅获取分类
    public static final String XIMA_GET_CATEGORIES = "http://3rd.ximalaya.com/categories";

    //蜻蜓获取分类下的专辑
    public static final String QT_FM_CATEGORIES = "http://api.qingting.fm/api/tongli/qtradiov4/categories?id=507&deviceid=%1$s";
    //搜索蜻蜓获取分类的专辑
    public static final String QT_FM_CHANNEL_LIST = "http://api.qingting.fm/api/tongli/qtradiov4/items";
    //搜索蜻蜓的声音
    public static final String QT_FM_PROGRAM_LIST = "http://api.qingting.fm/api/tongli/qtradiov4/programs";
    //蜻蜓获取分类
    public static final String QT_FM_RADIO_CATEGORY = "http://api.qingting.fm/api/tongli/qtradiov2/categories?id=100002&deviceid=%1$s";
    //蜻蜓获取分类
    public static final String QT_FM_RADIO_STATION_LIST = "http://api.qingting.fm/api/tongli/qtradiov2/items";
    //获取SPK网络电台跟新时间
    public static final String SPK_GET_NET_RADIO_UPDATE_TIME = "https://ms1.ibroadlink.com/spkchannel?method=getupdatetime";
    //获取所有电台
    public static final String SPK_GET_ALL_RADIO_LIST = "https://ms1.ibroadlink.com/spkchannel?method=getall";

    //百度认证
    public static final String BAIDU_AUTH_INFO = "https://openapi.baidu.com/oauth/2.0/authorize";
    //百度认证获取token
    public static final String BAIDU_AUTH_INFO2 = "https://openapi.baidu.com/oauth/2.0/token";

    //淘宝认证
    public static final String TAOBAO_AUTH_INFO = "https://oauth.taobao.com/authorize";
    //淘宝认证获取token
    public static final String TAOBAO_AUTH_INFO2 = "https://oauth.taobao.com/token";


    /**
     * 反馈页面链接
     */
    public static final String BL_SUPPORT_FEEDBACK = "https://d0f94faa04c63d9b7b0b034dcf895656feedback.ibroadlink.com";

    /**
     * 设备离线说明页面链接
     */
    public static final String BL_SUPPORT_DEV_OFFLINE = "https://d0f94faa04c63d9b7b0b034dcf895656online.ibroadlink.com/deviceoffline/";

    /**
     * 什么值得买网址
     */
    public static final String SMZDM_URL = "https://pinpai.m.smzdm.com/5125?utm_source=broadlink&utm_medium=hezuo&utm_campaign=swtg";

    /**
     * 频道图标地址
     */
    public static String CHANNEL_ICON_URL(){
        return CLOUD_NEW_BASE + "/publicircode/v2/stb/getchannelicon?channelid=%1$s";
    }

    //微信accressToken获取
    public static final String WECHAT_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token";

    //微信用户信息获取
    public static final String WECHAT_USERINFO = "https://api.weixin.qq.com/sns/userinfo";

    //备份上传图片地址
    public static final String RM_BACKUP_PIC(){
        return BASE_FAMILY_URL + "/userfeedback/v2/uploadfile";
    }

    //红码新增备份/评分地址 愿望清单
    public static final String RM_BACKUP_FEEDBACK(){
        return BASE_FAMILY_URL + "/userfeedback/v2/feedback";
    }

    //查询有无评分
    public static final String RM_QUERY_SCORE (){
        return BASE_FAMILY_URL + "/userfeedback/v2/query";
    }

    //获取用户库数据
    public static final String QUERY_RM_USER_BRAND(){
        return CLOUD_NEW_BASE + "/publicircode/v2/app/getfuncfilebyirid";
    }

    //删除备份接口
    public static final String DELETE_BACKUP(){
        return "http://192.168.1.40:18885/publicircode/v1/manage/delshareircodebyuserid";
    }

    //备份图片地址
    public static final String getBackupIconPath(String iconPath){
        return BASE_FAMILY_URL + "/userfeedback/v2/file?filepath="+iconPath;
    }
    /**
     * 语料说明
     */
    public static final String BL_SUPPORT_VOICE_CONTROL = "https://faq.ibroadlink.com/support/voice/index.html";

    //查询推送历史记录
    public static final String NOTIFY_PUSH_QUEYR= "https://d5973f547ac838af36723dcd796bd062appfront.ibroadlink.com/appfront/v1/notifypush/query";

    /**
     * 天猫旗舰店网址
     */
    public static final String TMALL_URL = "https://broadlink.m.tmall.com/";

    /**
     * 京东旗舰店网址
     */
    public static final String JD_URL = "https://shop.m.jd.com/?shopId=1000008872";
}
