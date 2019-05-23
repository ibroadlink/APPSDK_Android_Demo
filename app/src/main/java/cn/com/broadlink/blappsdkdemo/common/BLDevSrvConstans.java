package cn.com.broadlink.blappsdkdemo.common;

import android.content.Context;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.List;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.data.BLDevProfileInfo;

/**
 * 
 * 项目名称：BLEControlAppV4    
 * <br>类名称：BLDevSrvConstans
 * <br>类描述：设备分类 			
 * <br>创建人：YeJing 			
 * <br>创建时间：2015-7-28 下午1:59:17
 * <br>修改人：YeJin    		
 * <br>修改时间：2015-7-28 下午1:59:17
 * <br>修改备注：     		
 *
 */
public class BLDevSrvConstans {


    /**添加子设备**/
    public static final int TYPE_SUB_DEV = 1;

    /**协议**/
    public static class Protocol{
        /** 协议字段 **/
        public final static String FIELD = "protocol";
        /**WIFI**/
        public static final String WIFI = "1";
        /**虚拟设备局域网发现**/
        public static final String VIRTUAL_UN_APPLY = "7";
        /**虚拟设备**/
        public static final String VIRTUAL_APPLY_DID = "8";

        /**RM 红外**/
        public static final String RM_IR = "10";
        /**RM 315 射频**/
        public static final String RM_315 = "11";
        /**RM 433 射频**/
        public static final String RM_433 = "12";
        /**用于表示使用红外码库创建的设备（面板） 电视/机顶盒**/
        public static final String RM_CLOUD_IRCODE_PRODUCT = "14";
        /**IHG虚拟面板**/
        public static final String IHG_VIRTUAL= "15";
        /**以太网**/
        public static final String ETHRNET = "101";
        /**fastcon配网**/
        public static final String FASTCON = "16";
    }

    /**产品分类**/
    public static class Category{
        /**智能插座**/
        public static final String SMART_PLUG = "50";

        /**智能电工**/
        public static final String SMART_ELECTRICIAN = "305";

        /**智能遥控**/
        public static final String SMART_RM = "5";

        /**音响MS1**/
        public static final String SMART_MS1 = "136";

        /**RM 云码 云空调**/
        public static final String RM_CLOUD_AC = "40";

        /**RM 云码 电视面板**/
        public static final String RM_CLOUD_TV = "53";

        /**RM 云码 机顶盒面板**/
        public static final String RM_CLOUD_STB = "52";

        /**RM 云码 自定义面板**/
        public static final String RM_CLOUD_COUSTOM = "304";

        /**RM 灯**/
        public static final String RM_CLOUD_LAMP = "00000000000000000000000000000028";

        /**RM 窗帘**/
        public static final String RM_CLOUD_CURTAIN = "00000000000000000000000000000027";

        /**RM开关 **/
        public static final String RM_CLOUD_SWITCH =  "00000000000000000000000000000006";

        /**RM风扇 **/
        public static final String RM_CLOUD_FAN = "00000000000000000000000000000030";

        /**RM灯控开关**/
        public static final String RM_CLOUD_LIGHT_SWITCH = "00000000000000000000000000000034";

        /**投影仪**/
        public static final String RM_CLOUD_PROJECTOR = "00000000000000000000000000000065";

        /**互联网盒子**/
        public static final String RM_CLOUD_OTT = "00000000000000000000000000000064";

        /**DVD**/
        public static final String RM_CLOUD_DVD = "00000000000000000000000000000066";

        /**功放**/
        public static final String RM_CLOUD_AMPLIFIER = "00000000000000000000000000000073";

        /**相机**/
        public static final String RM_CLOUD_CAMERA = "00000000000000000000000000000067";

        /**热水器**/
        public static final String RM_CLOUD_HEATER = "00000000000000000000000000000068";

        /**空气净化器**/
        public  static final String RM_CLOUD_AIR_PURIFIER = "00000000000000000000000000000074";

        /**门**/
        public static final String RM_CLOUD_DOOR = "00000000000000000000000000000069";

        /**音响**/
        public static final String RM_CLOUD_AUDIO = "00000000000000000000000000000097";

        /**扫地机器人**/
        public static final String RM_CLOUD_SWEEPING_ROBOT = "00000000000000000000000000000070";

        /**加湿器**/
        public static final String RM_CLOUD_HUMIDIFIER = "00000000000000000000000000000071";

        /**晾衣架**/
        public static final String RM_CLOUD_CLOTHES_HANGER= "00000000000000000000000000000072";

    }

    /**电量**/
    public static final String BATTERY_LEVEL = "batterylevel"; // 电量，枚举值

    /**电量**/
    public static final String  BATTERY = "battery"; // 电量，连续值

    /**充电状态**/
    public static final String  BATTERY_CHARGE_STATE = "chargingstate"; // 充电状态，0-未充电，1-正在充电，2-充电完成
    

    public static String[] CUSTOM_COMMON_CATEGORYS = new String[]{Category.RM_CLOUD_LAMP,Category.RM_CLOUD_FAN,Category.RM_CLOUD_PROJECTOR,Category.RM_CLOUD_OTT,Category.RM_CLOUD_DVD,Category.RM_CLOUD_AMPLIFIER,
            Category.RM_CLOUD_CAMERA,Category.RM_CLOUD_HEATER,Category.RM_CLOUD_AIR_PURIFIER,Category.RM_CLOUD_DOOR,Category.RM_CLOUD_AUDIO,Category.RM_CLOUD_SWEEPING_ROBOT,Category.RM_CLOUD_HUMIDIFIER,Category.RM_CLOUD_CLOTHES_HANGER};

    public static boolean isCustomCommonCategory(String category){

        if(!TextUtils.isEmpty(category)){
           return Arrays.asList(CUSTOM_COMMON_CATEGORYS).contains(category);
        }
        return false;
    }

    public static String getMoudleNameByCategoryID(Context context , String categoryId){
        switch (categoryId){
            case Category.RM_CLOUD_LAMP:
                return context.getString(R.string.str_rm_device_lamp);
            case Category.RM_CLOUD_CURTAIN:
                return context.getString(R.string.str_devices_curtain);
            case Category.RM_CLOUD_SWITCH:
            case Category.RM_CLOUD_LIGHT_SWITCH:
                return context.getString(R.string.str_group_name);
            case Category.RM_CLOUD_FAN:
                return context.getString(R.string.str_devices_fan);
            case Category.RM_CLOUD_PROJECTOR:
                return context.getString(R.string.str_rm_device_projector);
            case Category.RM_CLOUD_OTT:
                return context.getString(R.string.str_rm_device_ott);
            case Category.RM_CLOUD_DVD:
                return context.getString(R.string.str_rm_device_dvd);
            case Category.RM_CLOUD_AMPLIFIER:
                return context.getString(R.string.str_rm_device_amplifier);
            case Category.RM_CLOUD_CAMERA:
                return context.getString(R.string.str_rm_device_camera);
            case Category.RM_CLOUD_HEATER:
                return context.getString(R.string.str_rm_device_heater);
            case Category.RM_CLOUD_AIR_PURIFIER:
                return context.getString(R.string.str_rm_device_air_purifier);
            case Category.RM_CLOUD_DOOR:
                return context.getString(R.string.str_rm_device_door);
            case Category.RM_CLOUD_AUDIO:
                return context.getString(R.string.str_rm_device_audio);
            case Category.RM_CLOUD_SWEEPING_ROBOT:
                return context.getString(R.string.str_rm_device_sweep_robot);
            case Category.RM_CLOUD_HUMIDIFIER:
                return context.getString(R.string.str_rm_device_humidifier);
            case Category.RM_CLOUD_CLOTHES_HANGER:
                return context.getString(R.string.str_rm_device_clothes_hanger);
            default:
                return context.getString(R.string.str_devices_curtain);
        }
    }

    public static String getIconPathByCategoryId(String categoryId){
        switch (categoryId){
            case Category.RM_CLOUD_LAMP:
                return BLApiUrlConstants.Family.GET_LAMP_ICON();
            case Category.RM_CLOUD_CURTAIN:
                return BLApiUrlConstants.Family.GET_CURTAIN_ICON();
            case Category.RM_CLOUD_SWITCH:
            case Category.RM_CLOUD_LIGHT_SWITCH:
                return BLApiUrlConstants.Family.GET_SWITCH_ICON();
            case Category.RM_CLOUD_FAN:
                return BLApiUrlConstants.Family.GET_FAN_ICON();
            case Category.RM_CLOUD_PROJECTOR:
                return BLApiUrlConstants.Family.GET_PROJECTOR_ICON();
            case Category.RM_CLOUD_OTT:
                return BLApiUrlConstants.Family.GET_OTT_ICON();
            case Category.RM_CLOUD_DVD:
                return BLApiUrlConstants.Family.GET_DVD_ICON();
            case Category.RM_CLOUD_AMPLIFIER:
                return BLApiUrlConstants.Family.GET_AMPLIFIER_ICON();
            case Category.RM_CLOUD_CAMERA:
                return BLApiUrlConstants.Family.GET_CAMERA_ICON();
            case Category.RM_CLOUD_HEATER:
                return BLApiUrlConstants.Family.GET_HEATER_ICON();
            case Category.RM_CLOUD_AIR_PURIFIER:
                return BLApiUrlConstants.Family.GET_AIR_PURIFIER_ICON();
            case Category.RM_CLOUD_DOOR:
                return BLApiUrlConstants.Family.GET_DOOR_ICON();
            case Category.RM_CLOUD_AUDIO:
                return BLApiUrlConstants.Family.GET_AUDIO_ICON();
            case Category.RM_CLOUD_SWEEPING_ROBOT:
                return BLApiUrlConstants.Family.GET_SWEEPING_ROBOT_ICON();
            case Category.RM_CLOUD_HUMIDIFIER:
                return BLApiUrlConstants.Family.GET_HUMIDIFIER_ICON();
            case Category.RM_CLOUD_CLOTHES_HANGER:
                return BLApiUrlConstants.Family.GET_CLOTHES_HANGER_ICON();
            default:
                return BLApiUrlConstants.Family.GET_CURTAIN_ICON();
        }
    }

    /**数据类型**/
    public static class ParamType{
        /**枚举类型**/
        public static final int ENUM = 1;

        /**连续类型**/
        public static final int CONTINUOUS = 2;

        /**简单类型**/
        public static final int SIMPLE = 3;
    }


    /**是否是虚拟设备*/
    public static boolean isVT(BLDevProfileInfo profileInfo){
        if(profileInfo == null) return false;
        String protocol = getDevProtocol(profileInfo.getSrvs());
        return protocol.equals(BLDevSrvConstans.Protocol.VIRTUAL_UN_APPLY) || protocol.equals(BLDevSrvConstans.Protocol.VIRTUAL_APPLY_DID);
    }

    /**是否是WiFi设备*/
    public static boolean isWiFi(BLDevProfileInfo profileInfo){
        if(profileInfo == null) return false;
        String protocol = getDevProtocol(profileInfo.getSrvs());
        return protocol.equals(BLDevSrvConstans.Protocol.WIFI);
    }
    
    /**是否是支持电量百分比*/
    public static boolean isSupportBatteryPercent(BLDevProfileInfo curDevProfile){
        final List<Integer> batteryPercentIns = BLProfileTools.checkOutIn(curDevProfile, BATTERY);
        return batteryPercentIns != null && batteryPercentIns.get(0)== ParamType.CONTINUOUS; // 有battery字段，且为连续类型
    }
    
    /**是否是支持电量等级*/
    public static boolean isSupportBatteryLevel(BLDevProfileInfo curDevProfile){
        final List<Integer> batteryLeverIns = BLProfileTools.checkOutIn(curDevProfile, BATTERY_LEVEL);
        return batteryLeverIns != null && batteryLeverIns.get(0)== ParamType.ENUM; // 有batterylevel字段，且为枚举类型
    }
    
    /**是否是支持充电状态查询*/
    public static boolean isSupportBatteryCharging(BLDevProfileInfo curDevProfile){
        final List<Integer> batteryStateIns = BLProfileTools.checkOutIn(curDevProfile, BATTERY_CHARGE_STATE);
        return batteryStateIns != null && batteryStateIns.get(0)==1; // 有chargingstate字段，且为枚举类型
    }

    /**是否是子设备*/
    public static boolean isSubDev(String pid){
        BLDevProfileInfo profileInfo = BLProfileTools.queryProfileInfoByPid(pid);
        if(profileInfo == null) return false;

        return profileInfo.getIssubdev() == BLDevSrvConstans.TYPE_SUB_DEV;
    }


    /**是否是网关设备*/
    public static boolean isGetway(String pid){
        BLDevProfileInfo profileInfo = BLProfileTools.queryProfileInfoByPid(pid);
        if(profileInfo == null) return false;

        return profileInfo.getProtocol() != null && !profileInfo.getProtocol().isEmpty();
    }

    /**是否是网关设备*/
    public static boolean isGetway(BLDevProfileInfo profileInfo){
        if(profileInfo == null) return false;

        return profileInfo.getProtocol() != null && !profileInfo.getProtocol().isEmpty();
    }

    /**是否是RM 品类*/
    public static boolean isRMDevice(String pid){
        BLDevProfileInfo profileInfo = BLProfileTools.queryProfileInfoByPid(pid);
        if(profileInfo == null) return false;

        return isRMDevice(profileInfo);
    }

    /**是否是RM 品类*/
    public static boolean isRMDevice(BLDevProfileInfo profileInfo){
        List<String> srvStrList = profileInfo.getSrvs();
        if(srvStrList.isEmpty()) return false;

        String category = getDevCategory(srvStrList.get(0));
        boolean categoryRm = category != null && category.equals(Category.SMART_RM);

        if(categoryRm){
            if((profileInfo.getProtocol() == null || profileInfo.getProtocol().isEmpty())){
                return true;
            }else if(isGetway(profileInfo) && (profileInfo.getProtocol().contains(Protocol.RM_IR)
                    || profileInfo.getProtocol().contains(Protocol.RM_315) || profileInfo.getProtocol().contains(Protocol.RM_433))){
                return true;
            }
        }
        return false;
    }

    /**是否是SP 品类*/
    public static boolean isSPCategory(List<String> srvStrList){
        if(srvStrList.isEmpty()) return false;

        String category = getDevCategory(srvStrList.get(0));
        return category != null && category.equals(Category.SMART_PLUG);
    }

//    /**智能电工类设备*/
//    public static boolean isSmartElectricianCategory(List<String> srvStrList){
//        if(srvStrList.isEmpty()) return false;
//
//        String category = getDevCategory(srvStrList.get(0));
//        return category != null && category.equals(Category.SMART_ELECTRICIAN);
//    }

    /**是否是RM 品类*/
    public static boolean isRMCategory(List<String> srvStrList){
        if(srvStrList.isEmpty()) return false;

        String category = getDevCategory(srvStrList.get(0));
        return category != null && category.equals(Category.SMART_RM);
    }

    /**
     * 是否是RM下的云码类设备，设备协议是 14
     * 并且品类是 电视 机顶盒 空调 灯 自定义模板
     * @param profileInfo
     * @return
     */
    public static boolean isRMColudIRCodeDeviceCategory(BLDevProfileInfo profileInfo){
        if(profileInfo == null) return false;
        String protocol = getDevProtocol(profileInfo.getSrvs());
        String category = getDevCategory(profileInfo.getSrvs().get(0));
        return (protocol != null && protocol.equals(Protocol.RM_CLOUD_IRCODE_PRODUCT)) && isRMCategory(category);
    }

    public static boolean isRMCategory(String category){
        if(category != null){
           if( category.equals(Category.RM_CLOUD_AC) || category.equals(Category.RM_CLOUD_STB) || category.equals(Category.RM_CLOUD_TV)
                   || category.equals(Category.RM_CLOUD_COUSTOM) || BLDevSrvConstans.isCustomCommonCategory(category)){
               return true;
           }
        }

        return false;
    }


    /**
     * 通过SRV 第二位获取到设备的协议信息
     * @param srvStrList
     *          profile中的srv
     * @return
     *         protocol
     */
    public static String getDevProtocol(List<String> srvStrList){
        if(srvStrList.isEmpty()) return null;
        return getDevProtocol(srvStrList.get(0));
    }

    /**
     * 通过SRV 第二位获取到设备的协议信息
     * @param srvStr
     *          profile中的srv
     * @return
     *         protocol
     */
    public static String getDevProtocol(String srvStr){
        String[] srvs = srvStr.split("\\.");
        if(srvs.length == 3){
            return srvs[1];
        }
        return null;
    }

    /**是否是ifttt 品类*/
    public static boolean isIFTTTCategory(List<String> itfs){
        if(itfs.isEmpty()) return false;

        for (String itf : itfs) {
            if(itf.contains("ifttt")){
                return true;
            }
        }
        return false;
    }

    /**
     * 通过SRV 第三位获取到设备的分类信息
     * @param srvStr
     *          profile中的srv
     * @return
     *         category
     */
    public static String getDevCategory(String srvStr){
        String[] srvs = srvStr.split("\\.");
        if(srvs.length == 3){
            return srvs[2];
        }
        return null;
    }
	
    /**
     * 通过SRV 第三位获取到设备的分类信息
     * @param srvList
     *          profile中的srvList
     * @return
     *          category
     */
    public static String getDevCategory(List<String> srvList){
        if(srvList != null && !srvList.isEmpty()){
            return getDevCategory(srvList.get(0));
        }
        return null;
    }

    /**
     * 通过SRV 第一位获取到设备的大类信息
     * @param srvStr
     * @return
     */
    public static String getDevFirstCategory(String srvStr){
        String[] srvs = srvStr.split("\\.");
        if(srvs.length == 3){
            return srvs[0];
        }
        return null;
    }

}
