package cn.com.broadlink.blappsdkdemo.utils.http;

import android.content.Context;

import cn.com.broadlink.base.BLAppSdkErrCode;
import cn.com.broadlink.blappsdkdemo.R;

/**
 * 项目名称：BLEControlAppV4
 * <br>类名称：BLHttpErrCode
 * <br>类描述： http请求错误类型
 * <br>创建人：YeJing
 * <br>创建时间：2015-4-11 上午10:28:40
 * <br>修改人：Administrator
 * <br>修改时间：2015-4-11 上午10:28:40
 * <br>修改备注：
 */
public class BLHttpErrCode {

    public static final int SUCCESS = 0;

    /****session失效*****/
    public static final int SESSION_INVALID = -1000;

    /****服务器繁忙*****/
    private static final int SERVER_BUSY = -1001;

    /****验证码错误*****/
    private static final int ERROR_VERIFICATION_CODE = -1002;

    /****帐号已被注册*****/
    private static final int ACCOUNT_HAS_REGISTERED = -1003;

    /****请求时间过期*****/
    private static final int REQUERST_TIME_EXPIRED = -1004;

    /****数据错误*****/
    private static final int ERROR_DATA = -1005;

    /****帐号或密码不正确*****/
    public static final int ACCOUNT_PASSWORD_ERR = -1006;

    /****验证码失效*****/
    private static final int VCODE_INVALID = -1007;

    /****账号或者密码不正确*****/
    public static final int ACCOUNT_PASSWORD_ERR2 = -1008;

    /****未登陆*****/
    public static final int UNLOGIN_IN = -1009;

    /****原始密码错误*****/
    private static final int PASSWORD_ERR = -1010;

    /****头像不存在*****/
    private static final int ICON_NOT_EXIST = -1011;

    /****请重新登陆*****/
    public static final int LOGIN_AGAIN = -1012;

    /****信息不匹配*****/
    private static final int INFORMATION_NOT_MATCH = -1013;

    /****三小时后登陆*****/
    private static final int SIGN_AFTER_3_HOURS = -1014;

    /****邮箱错误*****/
    private static final int EMAIL_ERR = -1015;

    /****您已绑定账号*****/
    private static final int ACCOUNT_ALREALY_ASSOCIATED = -1016;

    /***该账号以及被其他用户绑定*****/
    private static final int ACCOUNT_ALREALY_ASSOCIATED_OTHER_USER = -1017;

    /****未绑定第三方帐号*****/
    private static final int THRID_ACCOUNT_UNBIND = -1018;

    /****上传的头像超过了50k*****/
    private static final int UPDATE_AVATAR_EXCEED_500KB = -1019;

    /****邮箱或密码不正确*****/
    private static final int EMAIL_PASSWORD_ERROR = -1020;

    /****注册过于频繁*****/
    private static final int REGISTRATION_FREQUENT = -1021;

    /****不存在第三方帐号类型*****/
    private static final int ACCOUNT_NOT_EXIST_3RD = -1022;

    /****登陆剩余次数*****/
    private static final int REMAINNIBF_SIGNIN_TIMES = -1023;

    /****发送验证码错误*****/
    private static final int SEBD_VCODE_ERROR = -1027;

    /****手机号码错误*****/
    private static final int MOBILE_PHONE_NUMBER_ERROR = -1028;

    /****头像保存错误*****/
    private static final int AVARAR_SAVE_ERROR = -1029;

    /****验证码发送过于频繁*****/
    private static final int SEND_VCODE_TOO_FREQUENTLY = -1030;

    /****验证码超时或不存在*****/
    private static final int VCODE_TIMEOUT_OR_NOT_EXIST = -1031;

    /****手机号码已经注册*****/
    private static final int MOBILE_PHONE_NUM_REGISTERED = -1032;

    /****手机号码或密码不正确*****/
    private static final int MOBOLE_PHONE_PASSWORD_ERROR = -1033;

    /****未验证手机验证码*****/
    private static final int VCODE_UNDER_VALIDATION = -1034;

    /****账号不存在*****/
    private static final int ACCOUNT_NOT_EXIST = -1035;

    /****尝试次数过多，请过一段时间后重试*****/
    private static final int TOO_FREQUENT_REQUEST = -1036;

    /****登陆信息不正确*****/
    private static final int SIGNIN_INFORMATION_ERROR = -1037;

    /****邮箱已经注册*****/
    private static final int EMAIL_HAS_REGISTERED = -1038;

    /****验证码已失效*****/
    private static final int VCODE_INVALID2 = -1039;

    /****发送短信次数过多*****/
    private static final int SMS_SEND_FREQUENT = -1040;

    /****数据超长*****/
    private static final int DATA_TOO_LONG = -1041;

    /****权限格式错误*****/
    private static final int PERMISSION_FORMAT_ERROR = -1042;

    /****权限已存在*****/
    private static final int PERMISSION_EXISTS = -1043;

    /****角色格式错误*****/
    private static final int ROLE_FORMAT_ERROR = -1044;

    /****角色已存在*****/
    private static final int ROLE__EXIST = -1045;

    /****企业已存在*****/
    private static final int COMPANY_EXISTS = -1046;

    /****企业不存在*****/
    private static final int COMPANY_NOT_EXISTS = -1047;

    /****license不存在*****/
    private static final int lICENSE_NOT_EXISTS = -1048;

    /****该账号被禁用*****/
    private static final int ACCOUNT_HAS_BEEN_DISABLED = -1049;

    /****密码已设置，请直接修改密码*****/
    private static final int PASSWORD_HAS_BEEN_SET = -1050;

    /****不支持的第三方oauth平台*****/
    private static final int ACCOUNT_UNSUPPORTED_3RD_OAUTH_PLATFORM = -1051;

    /****第三方oauth未认证成功*****/
    private static final int ACCOUNT_3RD_OAUTH_FAILED = -1052;

    /****操作过于频繁*****/
    private static final int ACCOUT_FREQUERT_OPERATION = -1099;

    ///////////////////////////////////////ihc 家庭 后台//////////////////////////////////////////////
    /***不支持的协议版本*****/
    public static final int FAMILY_UNSUPPORT_PROTOCOL_VERSION = -2000;

    /***服务器繁忙**/
    public static final int FAMILY_SERVER_BUSY = -2001;

    /*** 数据错误***/
    public static final int FAMILY_DATA_ERROR = -2002;

    /*** 不是有效的申请**/
    public static final int FAMILY_INVALID_APPLY = -2003;

    /*** 请求时间过期**/
    public static final int FAMILY_TIMESTAMP_OUTOFDAY = -2004;

    /***用户未登录*****/
    public static final int USER_UNLOGIN = -2006;

    /***没有权限*****/
    public static final int UN_PERMISSION = -2007;

    /***不存在该家庭*****/
    public static final int FAMILY_NOT_EXIST = -2008;

    /** 二维码已失效**/
    public static final int FAMILY_QRCODE_FAILURE = -2009;

    /** 无效的二维码**/
    public static final int FAMILY_QRCODE_INVALID = -2010;

    /** 用户已经是该家庭成员**/
    public static final int FAMILY_USER_HOME_NUM = -2011;

    /** 用户不是该家庭的成员**/
    public static final int FAMILY_USER_NOT_HOME_NUM = -2012;

    /** 家庭创建者不能被踢出**/
    public static final int FAMILY_CREATR_DELETED = -2013;

    /** 家庭信息已过时**/
    public static final int FAMILY_VISION_INVALID = -2014;

    /** 未知条形码**/
    public static final int FAMILY_UNKOWN_BARCODE = -2015;

    /** 房间内有设备，不能删除**/
    public static final int FAMILY_DELETE_ERROR_BY_DEVICES = -2016;

    /** 房间内有模块，不能删除**/
    public static final int FAMILY_DELETE_ERROR_BY_MODULES = -2017;

    /** 没有该产品**/
    public static final int FAMILY_NOT_FIND_PRODUCT = -2018;

    /** 数据冲突**/
    public static final int FAMILY_DATA_CONFLICTED = -2019;

    /** 非内测用户**/
    public static final int FAMILY_NOT_BETA_USER = -2020;

    /** 命名冲突**/
    public static final int FAMILY_NAME_CONFLICTED = -2021;

    /** 非注册用户原始手机**/
    public static final int FAMILY_NOT_ORIGINAL_PHONE = -2022;

    /** 数据超长**/
    public static final int FAMILY_DATA_TOO_LONG = -2023;

    /** 不能退出自己创建的家庭**/
    public static final int FAMILY_CANNOT_QUIT_FAMILY = -2024;

    /** 键值不允许**/
    public static final int FAMILY_KEY_VALUE_NOT_ALLOWED = -2025;

    /** 家庭设备数量过多**/
    public static final int FAMILY_TOO_DEVICES = -2026;

    /** 设备未绑定家庭成员**/
    public static final int FAMILY_DEVICE_UNBOUND_DEVICE = -2027;

    /** 操作不被允许**/
    public static final int FAMILY_OPERATION_NOT_PERMITTED = -2028;

    /** 参数不匹配**/
    public static final int FAMILY_PARAMETEER_NOT_MATCH = -2029;

    /** 设备不属于家庭**/
    public static final int FAMILY_DEVICE_NOT_BELONG_HOME = -2030;

    /** 设备不存在**/
    public static final int FAMILY_DEVICE_NOT_EXIST = -2031;

    /** 单次添加模块数量过多**/
    public static final int FAMILY_TOO_MANY_MODULES_ADDED = -2032;

    /** 房间内有子设备，不能删除**/
    public static final int FAMILY_CANNOT_DELETE_BECAUSE_SUBDEVICE = -2033;

    /** 用户不存在**/
    public static final int FAMILY_USER_NOT_EXIST = -2034;

    /***登陆后操作*****/
    public static final int ACCOUNT_LOGIN_INVALID = 10011;

    ///////////////////////////////////////ihc app 后台//////////////////////////////////////////////
    /**  服务器忙 **/
    public static final int APP_MNG_SERVER_BUSY = -14001;

    /** 数据错误**/
    public static final int APP_MNG_DATA_ERROR = -14002;

    /**  Pid不存在**/
    public static final int APP_MNG_PID_NOT_EXIST = -14017;

    /** 查询类型不存在**/
    public static final int APP_MNG_QUERY_TYPE_NOT_EXIST = -14018;

    /*** 无权限*/
    public static final int APP_MNG_NO_PERISSION = -14021;

    /*** 红码太长*/
    public static final int APP_MNG_IR_CODE_TOO_LONG = -14022;

    /** 图片太大**/
    public static final int APP_MNG_PICTURE_SIZE_TOO_LONG = -14023;

    /** 状态不允许**/
    public static final int APP_MNG_STATE_NOT_ALLOWED = -14024;

    /** 品牌不存在**/
    public static final int APP_MNG_BRAND_NOT_EXIST = -14025;

    /** 家电类型不存在**/
    public static final int APP_MNG_APPLIANCE_NOT_EXIST = -14026;

    /** 品牌家电未绑定**/
    public static final int APP_MNG_APPLIANCE_NOT_ASSOCIATED = -14027;

    /** 型号未绑定到指定品牌家电**/
    public static final int APP_MNG_MODE_NOT_ASSOCIATED = -14028;

    /** 产品未找到**/
    public static final int APP_MNG_CANNOT_FIND_PRODUCT = -14031;

    /** 请求太频繁，请稍后重试**/
    public static final int APP_MNG_TOO_FREQUENT_REQUESTS = -14038;

    /** 请求不存在**/
    public static final int APP_MNG_REQUEST_NOT_EXIST = -14039;

    /** 未查到用户**/
    public static final int APP_MNG_CANNOT_FIND_USER = -14041;

    /** 命名重复**/
    public static final int APP_MNG_NAME_DUPLOCATED = -14043;

    /** 权限不允许**/
    public static final int APP_MNG_PERMISSION_NOT_ALLOWED = -14044;

    /** 未找到**/
    public static final int APP_MNG_CANNOT_FINT = -14045;

    /** 产品分类不存在**/
    public static final int APP_MNG_PRODUCT_CATEGORY_NOT_EXIST = -14046;

    /** 权限未通过**/
    public static final int APP_MNG_PERMISSION_NOT_APPROVED = -14047;

    /** 下载次数达到限额**/
    public static final int APP_MNG_DOWNLOADS_REACH_LIMIT = -14048;

    /** ticket 验证不通过**/
    public static final int APP_MNG_TIKECT_CALIDATION_FAILED = -14049;

    /** 信息不完整**/
    public static final int APP_MNG_INFORMATION_INCOMPLETR = -14051;

    ///////////////////////////////////////红码服务后台//////////////////////////////////////////////
    /** 服务器忙**/
    public static final int IR_SERVER_BUSY = -16001;

    /** 数据错误**/
    public static final int IR_DATA_ERROR = -16002;

    /** 没有该红码**/
    public static final int IR_CANNOT_FIND_IRCODE = -16003;

    /** 红码已存在**/
    public static final int IR_IRCODE_EXIST = -16004;

    /** 下载人数过多,请稍后**/
    public static final int IR_SERVER_IS_BUSY = -16005;

    /** 数据错误**/
    public static final int IR_DATA_ERROR2 = -16006;

    /** 输入不匹配**/
    public static final int IR_INPUT_NOT_MATCH = -16009;

    /** 数据不存在**/
    public static final int IR_DATA_NOT_FIND = -16011;

    /** 红码未匹配**/
    public static final int IR_IRCODE_NOT_MATCH = -16012;

    /** 区域不允许**/
    public static final int IR_REGIN_MOT_PERMITTED = -16013;

    /** 地区ID不匹配**/
    public static final int IR_REGION_ID_NOT_MATCH = -16014;

    /** URL错误**/
    public static final int IR_URl_ERROR = -16101;

    /** 权限未通过**/
    public static final int IR_PERMISSION_NOT_APPROVED = -16102;

    /** 下载次数达到限额**/
    public static final int IR_DIWNLOAD_REACH_LIMIT = -16104;

    /**  无权限 **/
    public static final int IR_NO_PERMISSION = -16105;

    /** 无权限**/
    public static final int NO_PERMISSION = 10003;

    /** 服务器繁忙**/
    public static final int HTT_SERVER_BUSY = 10004;

    ///////////////////////////////////////推送后台服务//////////////////////////////////////////////
    /** 推送失败**/
    public static final int PUSH_FAIL = -25002;
    /** 无内容**/
    public static final int PUSH_NO_CONTENT = -25008;
    /** 该ip不在白名单内**/
    public static final int PUSH_NOT_IN_W_LIST = -25009;
    /** token已切换**/
    public static final int PUSH_TOKEN_CHANGED = -25010;

    /**家庭数据异常**/
    public static final int FAMILY_DATA_EXCEPTION = -14054;
    
    /**
     * 通过错误类型，获取错误提示
     *
     * @param errCode 错误类型
     * @return String 错误提示 ， null表示为找到对应的错误
     */
    public static String getErrorMsg(Context context, int errCode) {
        switch (errCode) {
            case SERVER_BUSY:
            case HTT_SERVER_BUSY:
                return context.getString(R.string.server_is_busy);
            case ERROR_VERIFICATION_CODE:
                return context.getString(R.string.verification_code_error);
            case ACCOUNT_HAS_REGISTERED:
                return context.getString(R.string.account_has_been_registered);
            case REQUERST_TIME_EXPIRED:
                return context.getString(R.string.request_timeout);
            case ERROR_DATA:
                return context.getString(R.string.data_error);
            case ACCOUNT_PASSWORD_ERR:
            case ACCOUNT_PASSWORD_ERR2:
                return context.getString(R.string.account_or_password_incorrect);
            case VCODE_INVALID:
            case VCODE_INVALID2:
                return context.getString(R.string.verification_code_is_invalid);
            case UNLOGIN_IN:
                return context.getString(R.string.please_sign_in_first);
            case PASSWORD_ERR:
                return context.getString(R.string.old_password_incorrect);
            case ICON_NOT_EXIST:
                return context.getString(R.string.avatar_does_not_exist);
            case ACCOUNT_LOGIN_INVALID:
            case LOGIN_AGAIN:
                return context.getString(R.string.please_sign_in_again);
            case INFORMATION_NOT_MATCH:
                return context.getString(R.string.information_does_not_match);
            case SIGN_AFTER_3_HOURS:
                return context.getString(R.string.sign_in_after_3_hours);
            case EMAIL_ERR:
                return context.getString(R.string.email_error);
            case ACCOUNT_ALREALY_ASSOCIATED:
                return context.getString(R.string.you_have_already_associated_this_account);
            case ACCOUNT_ALREALY_ASSOCIATED_OTHER_USER:
                return context.getString(R.string.the_account_has_been_associated_by_another_user);
            case THRID_ACCOUNT_UNBIND:
                return context.getString(R.string.this_3rd_party_account_is_not_associated);
            case UPDATE_AVATAR_EXCEED_500KB:
                return context.getString(R.string.the_uploaded_avatar_exceeds_500KB);
            case EMAIL_PASSWORD_ERROR:
                return context.getString(R.string.email_or_password_incorrect);
            case REGISTRATION_FREQUENT:
                return context.getString(R.string.too_frequent_registrations);
            case ACCOUNT_NOT_EXIST_3RD:
                return context.getString(R.string.account_type_3rd_does_not_exist);
            case REMAINNIBF_SIGNIN_TIMES:
                return context.getString(R.string.remaining_signin_times);
            case SEBD_VCODE_ERROR:
                return context.getString(R.string.sending_verification_code_error);
            case MOBILE_PHONE_NUMBER_ERROR:
                return context.getString(R.string.mobile_phone_number_error);
            case AVARAR_SAVE_ERROR:
                return context.getString(R.string.avatar_saving_error);
            case SEND_VCODE_TOO_FREQUENTLY:
                return context.getString(R.string.sending_validation_code_too_frequently);
            case VCODE_TIMEOUT_OR_NOT_EXIST:
                return context.getString(R.string.validation_code_timeout_or_does_not_exist);
            case MOBILE_PHONE_NUM_REGISTERED:
                return context.getString(R.string.the_mobile_phone_number_has_been_registered);
            case MOBOLE_PHONE_PASSWORD_ERROR:
                return context.getString(R.string.mobile_phone_number_or_password_incorrect);
            case VCODE_UNDER_VALIDATION:
                return context.getString(R.string.under_validation);
            case ACCOUNT_NOT_EXIST:
                return context.getString(R.string.account_does_not_exist);
            case TOO_FREQUENT_REQUEST:
                return context.getString(R.string.too_frequent_requests);
            case SIGNIN_INFORMATION_ERROR:
                return context.getString(R.string.signin_information_error);
            case EMAIL_HAS_REGISTERED:
                return context.getString(R.string.email_address_has_been_registered);
            case SMS_SEND_FREQUENT:
                return context.getString(R.string.too_frequent_tries_of_sending_SMS);
            case DATA_TOO_LONG:
                return context.getString(R.string.data_too_long);
            case PERMISSION_FORMAT_ERROR:
                return context.getString(R.string.permission_format_error);
            case PERMISSION_EXISTS:
                return context.getString(R.string.permission_exists);
            case ROLE_FORMAT_ERROR:
                return context.getString(R.string.role_format_error);
            case ROLE__EXIST:
                return context.getString(R.string.company_exists);
            case COMPANY_EXISTS:
                return context.getString(R.string.company_does_not_exist);
            case COMPANY_NOT_EXISTS:
                return context.getString(R.string.company_does_not_exist);
            case lICENSE_NOT_EXISTS:
                return context.getString(R.string.license_does_not_exist);
            case ACCOUNT_HAS_BEEN_DISABLED:
                return context.getString(R.string.this_account_has_been_disabled);
            case PASSWORD_HAS_BEEN_SET:
                return context.getString(R.string.password_has_been_set);
            case ACCOUNT_UNSUPPORTED_3RD_OAUTH_PLATFORM:
                return context.getString(R.string.aacount_unsupported_3rd_party_oauth_platform);
            case ACCOUNT_3RD_OAUTH_FAILED:
                return context.getString(R.string.aacount_3rd_party_oauth_authentication_failed);
            case ACCOUT_FREQUERT_OPERATION:
                return context.getString(R.string.str_common_too_frequent_operation);
            case FAMILY_UNSUPPORT_PROTOCOL_VERSION:
                return context.getString(R.string.unsupported_protocol_version);
            case FAMILY_SERVER_BUSY:
                return context.getString(R.string.server_is_busy);
            case FAMILY_DATA_ERROR:
                return context.getString(R.string.data_error);
            case FAMILY_INVALID_APPLY:
                return context.getString(R.string.invalid_request);
            case FAMILY_TIMESTAMP_OUTOFDAY:
                return context.getString(R.string.request_timeout);
            case USER_UNLOGIN:
                return context.getString(R.string.user_did_not_sign_in);
            case UN_PERMISSION:
                return context.getString(R.string.no_permission);
            case FAMILY_NOT_EXIST:
                return context.getString(R.string.home_does_not_exist);
            case FAMILY_QRCODE_FAILURE:
                return context.getString(R.string.qrcode_is_invalid);
            case FAMILY_QRCODE_INVALID:
                return context.getString(R.string.invalid_QR_code);
            case FAMILY_USER_HOME_NUM:
                return context.getString(R.string.user_is_already_a_home_member);
            case FAMILY_USER_NOT_HOME_NUM:
                return context.getString(R.string.user_is_not_a_home_member);
            case FAMILY_CREATR_DELETED:
                return context.getString(R.string.home_creator_cannot_be_deleted);
            case FAMILY_VISION_INVALID:
                return context.getString(R.string.home_information_is_expired);
            case FAMILY_UNKOWN_BARCODE:
                return context.getString(R.string.unknown_barcode);
            case FAMILY_DELETE_ERROR_BY_DEVICES:
                return context.getString(R.string.cannot_delete_the_room_because_it_contains_devices);
            case FAMILY_DELETE_ERROR_BY_MODULES:
                return context.getString(R.string.cannot_delete_the_room_because_it_contains_modules);
            case FAMILY_NOT_FIND_PRODUCT:
                return context.getString(R.string.cannot_find_this_product);
            case FAMILY_DATA_CONFLICTED:
                return context.getString(R.string.data_conflicted);
            case FAMILY_NOT_BETA_USER:
                return context.getString(R.string.not_beta_user);
            case FAMILY_NAME_CONFLICTED:
                return context.getString(R.string.name_conflicted);
            case FAMILY_NOT_ORIGINAL_PHONE:
                return context.getString(R.string.not_original_mobile_phone_number_of_registered_user);
            case FAMILY_DATA_TOO_LONG:
                return context.getString(R.string.data_too_long);
            case FAMILY_CANNOT_QUIT_FAMILY:
                return context.getString(R.string.cannot_quit_from_the_home_you_created);
            case FAMILY_KEY_VALUE_NOT_ALLOWED:
                return context.getString(R.string.key_value_not_allowed);
            case FAMILY_TOO_DEVICES:
                return context.getString(R.string.too_many_devices_in_the_home);
            case FAMILY_DEVICE_UNBOUND_DEVICE:
                return context.getString(R.string.home_members_are_not_associated_to_the_device);
            case FAMILY_OPERATION_NOT_PERMITTED:
                return context.getString(R.string.operation_is_not_permitted);
            case FAMILY_PARAMETEER_NOT_MATCH:
                return context.getString(R.string.parameter_does_not_match);
            case FAMILY_DEVICE_NOT_BELONG_HOME:
                return context.getString(R.string.the_device_does_not_belong_to_this_home);
            case FAMILY_DEVICE_NOT_EXIST:
                return context.getString(R.string.device_not_exist);
            case FAMILY_TOO_MANY_MODULES_ADDED:
                return context.getString(R.string.too_many_modules_added);
            case FAMILY_CANNOT_DELETE_BECAUSE_SUBDEVICE:
                return context.getString(R.string.cannot_delete_the_room_because_it_contains_subdevices);
            case FAMILY_USER_NOT_EXIST:
                return context.getString(R.string.user_does_not_exist);
            case APP_MNG_SERVER_BUSY:
                return context.getString(R.string.server_is_busy);
            case APP_MNG_DATA_ERROR:
                return context.getString(R.string.data_error);
            case APP_MNG_PID_NOT_EXIST:
                return context.getString(R.string.pid_does_not_exist);
            case APP_MNG_QUERY_TYPE_NOT_EXIST:
                return context.getString(R.string.querying_type_does_not_exist);
            case APP_MNG_NO_PERISSION:
                return context.getString(R.string.no_permission);
            case APP_MNG_IR_CODE_TOO_LONG:
                return context.getString(R.string.ir_code_is_too_long);
            case APP_MNG_PICTURE_SIZE_TOO_LONG:
                return context.getString(R.string.picture_size_too_big);
            case APP_MNG_STATE_NOT_ALLOWED:
                return context.getString(R.string.state_is_not_allowed);
            case APP_MNG_BRAND_NOT_EXIST:
                return context.getString(R.string.brand_does_not_exist);
            case APP_MNG_APPLIANCE_NOT_EXIST:
                return context.getString(R.string.appliance_type_does_not_exist);
            case APP_MNG_APPLIANCE_NOT_ASSOCIATED:
                return context.getString(R.string.appliance_of_this_brand_is_not_associated);
            case APP_MNG_MODE_NOT_ASSOCIATED:
                return context.getString(R.string.model_is_not_associated_to_the_appliance_of_specified_brand);
            case APP_MNG_CANNOT_FIND_PRODUCT:
                return context.getString(R.string.cannot_find_product);
            case APP_MNG_TOO_FREQUENT_REQUESTS:
                return context.getString(R.string.too_frequent_requests_please_try_again_later);
            case APP_MNG_REQUEST_NOT_EXIST:
                return context.getString(R.string.request_does_not_exist);
            case APP_MNG_CANNOT_FIND_USER:
                return context.getString(R.string.cannot_find_user);
            case APP_MNG_NAME_DUPLOCATED:
                return context.getString(R.string.name_duplicated);
            case APP_MNG_PERMISSION_NOT_ALLOWED:
                return context.getString(R.string.permission_is_not_allowed);
            case APP_MNG_CANNOT_FINT:
                return context.getString(R.string.cannot_find);
            case APP_MNG_PRODUCT_CATEGORY_NOT_EXIST:
                return context.getString(R.string.product_category_does_not_exist);
            case APP_MNG_PERMISSION_NOT_APPROVED:
                return context.getString(R.string.permission_is_not_approved);
            case APP_MNG_DOWNLOADS_REACH_LIMIT:
                return context.getString(R.string.downloads_reach_limit);
            case APP_MNG_TIKECT_CALIDATION_FAILED:
                return context.getString(R.string.ticket_validation_failed);
            case APP_MNG_INFORMATION_INCOMPLETR:
                return context.getString(R.string.information_incomplete);
            case IR_SERVER_BUSY:
                return context.getString(R.string.server_is_busy);
            case IR_DATA_ERROR:
                return context.getString(R.string.data_error);
            case IR_CANNOT_FIND_IRCODE:
                return context.getString(R.string.cannot_find_the_IR_code);
            case IR_IRCODE_EXIST:
                return context.getString(R.string.the_IR_code_already_exists);
            case IR_SERVER_IS_BUSY:
                return context.getString(R.string.server_is_busy_Please_wait);
            case IR_DATA_ERROR2:
                return context.getString(R.string.data_error);
            case IR_INPUT_NOT_MATCH:
                return context.getString(R.string.input_does_not_match);
            case IR_DATA_NOT_FIND:
                return context.getString(R.string.data_does_not_exist);
            case IR_IRCODE_NOT_MATCH:
                return context.getString(R.string.ircode_does_not_match);
            case IR_REGIN_MOT_PERMITTED:
                return context.getString(R.string.region_is_not_permitted);
            case IR_REGION_ID_NOT_MATCH:
                return context.getString(R.string.region_ID_does_not_match);
            case IR_URl_ERROR:
                return context.getString(R.string.url_erro);
            case IR_PERMISSION_NOT_APPROVED:
                return context.getString(R.string.permission_is_not_approved);
            case IR_DIWNLOAD_REACH_LIMIT:
                return context.getString(R.string.downloads_reach_limit);
            case IR_NO_PERMISSION:
                return context.getString(R.string.no_permission);
            case BLAppSdkErrCode.ERR_UNKNOWN:
            case BLAppSdkErrCode.ERR_HTTP:
            case BLAppSdkErrCode.ERR_SERVER_NO_RESULT:
                return context.getString(R.string.str_err_network);
            case NO_PERMISSION:
                return context.getString(R.string.str_no_permission);
            case PUSH_TOKEN_CHANGED:
                return context.getString(R.string.str_push_token_changed);
            case PUSH_FAIL:
            case PUSH_NO_CONTENT:
            case PUSH_NOT_IN_W_LIST:
                return context.getString(R.string.str_push_failed, errCode);
            case FAMILY_DATA_EXCEPTION :
                return context.getString(R.string.str_family_data_error);
            default:
                return context.getString(R.string.str_common_error_code, errCode);
        }
    }
}
