package cn.com.broadlink.blappsdkdemo.activity.family.constant;

final public class BLSFamilyConstants {

    /**域名**/
    public static final String BASE_DOMAIN = "appservice.ibroadlink.com";

    /**家庭自身信息接口**/
    //新增家庭
    public static final String ADD_FAMILY = "/appsync/group/family/add";

    //修改家庭
    public static final String MODIFY_FAMILY = "/appsync/group/family/modifyinfo";

    //删除家庭
    public static final String DEL_FAMILY = "/appsync/group/family/del";

    //修改家庭图标
    public static final String MODIFY_FAMILY_ICON = "/appsync/group/family/modifyicon";

    //获取家庭基本信息
    public static final String GET_FAMILY_BASE_INFO = "/appsync/group/family/getfamilyinfo";

    /**家庭成员相关接口**/
    //获取家庭列表
    public static final String GET_FAMILY_LIST = "/appsync/group/member/getfamilylist";

    //获取邀请家庭的二维码
    public static final String GET_FAMILY_INVITED_QRCODE = "/appsync/group/member/invited/reqqrcode";

    //扫描二维码，获取家庭信息
    public static final String SCAN_FAMILY_INVITED_QRCODE = "/appsync/group/member/invited/scanqrcode";

    //扫码加入家庭
    public static final String JOIN_FAMILY_INVITED_QRCODE = "/appsync/group/member/invited/joinfamily";

    //获取家庭下所有成员
    public static final String GET_FAMILY_MEMBERS = "/appsync/group/member/getfamilymember";

    //删除家庭成员
    public static final String DEL_FAMILY_MEMBERS = "/appsync/group/member/delfamilymember";

    //家庭成员主动退出家庭
    public static final String QUITE_FAMILY_MEMBERS = "/appsync/group/member/quitfamily";

    /**房间相关接口**/
    //获取房间列表
    public static final String QUERY_ROOM_LIST = "/appsync/group/room/query";

    //房间管理接口
    public static final String MANAGE_ROOM = "/appsync/group/room/manage";

    /**家庭设备相关接口**/
    //查询家庭下设备列表
    public static final String QUERY_ENDPOINT_LIST = "/appsync/group/dev/query";

    //新增设备
    public static final String ADD_ENDPOINT = "/appsync/group/dev/manage?operation=add";

    //删除设备
    public static final String DEL_ENDPOINT = "/appsync/group/dev/manage?operation=del";

    //更新设备信息
    public static final String UPDATE_ENDPOINT = "/appsync/group/dev/manage?operation=update";

    //更新设备属性
    public static final String UPDATE_ENDPOINT_ATTRIBUTE = "/appsync/group/dev/updateattribute";

}
