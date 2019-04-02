package cn.com.broadlink.blappsdkdemo.common;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.Toast;

import java.io.File;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.blappsdkdemo.BuildConfig;
import cn.com.broadlink.blappsdkdemo.data.BaseResult;

/**
 * Created by YeJin on 2016/5/9.
 */
public class BLCommonUtils {

    private BLCommonUtils(){}

    public static void toastShow(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void toastShow(Context context, int messageId) {
        Toast.makeText(context, context.getString(messageId), Toast.LENGTH_SHORT).show();
    }

    /**
     * 手机是否链接WIFI网络
     *
     * @return boolean true 手机是否链接的是WIFI网络
     *
     */
    public static boolean isWifiConnect(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    public static String getWIFISSID(Context context){
        String ssid = "";
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        try {
            WifiInfo info = wifi.getConnectionInfo();
            String CurInfoStr = info.toString() + "";
            String CurSsidStr = info.getSSID().toString() + "";
            if (CurInfoStr.contains(CurSsidStr)) {
                ssid = CurSsidStr;
            } else if(CurSsidStr.startsWith("\"") && CurSsidStr.endsWith("\"")){
                ssid = CurSsidStr.substring(1, CurSsidStr.length() - 1);
            } else {
                ssid = CurSsidStr;
            }
        } catch (Exception e) {
        }

        return ssid;
    }

    /**
     * 手机是否链接网络
     *
     * @return boolean
     *
     */
    public static boolean checkNetwork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return ((connectivityManager.getActiveNetworkInfo() != null && connectivityManager
                .getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || telephonyManager
                .getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
    }

    /**
     * 获取手机语言
     * @return
     * <br>zh_Hant 中文繁体
     * <br>zh_Hans 中文简体
     * <br>en 英文
     */
    public static String getLanguage() {
        Locale locale = Locale.getDefault();
        String country = locale.getCountry();

        StringBuffer language = new StringBuffer(locale.getLanguage());
        language.append("-");
        language.append(country);
        return language.toString().toLowerCase();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     *
     * 是否是email
     *
     * @param str
     *
     * @return boolean正确的邮箱格式
     *
     */
    public static boolean isEmail(String str) {
        String regex = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        return match(regex, str);
    }

    /**
     * 验证手机格式
     * @param phone
     * @return
     */
    public static boolean isPhone(String phone) {
        String regex = "[0-9]+";
        return match(regex, phone);
    }

    /**
     * @param regex
     *            正则表达式字符串
     * @param str
     *            要匹配的字符串
     * @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
     */
    private static boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 获取手机设置的地区
     * @return
     */
    public static String getCountry() {
        Locale locale = Locale.getDefault();
        return locale.getCountry();
    }

    public static <T> void toActivity(Context context, Class<T> clazz) {
        Intent intent = new Intent();
        intent.setClass(context, clazz);
        context.startActivity(intent);
    }
    
    public static <T> void toActivity(Context context, Class<T> clazz, Parcelable obj) {
        Intent intent = new Intent();
        intent.setClass(context, clazz);
        intent.putExtra(BLConstants.INTENT_PARCELABLE, obj);
        context.startActivity(intent);
    }
    
    public static <T> void toActivity(Context context, Class<T> clazz, Serializable obj) {
        Intent intent = new Intent();
        intent.setClass(context, clazz);
        intent.putExtra(BLConstants.INTENT_SERIALIZABLE, obj);
        context.startActivity(intent);
    }
    
    public static <T> void toActivity(Context context, Class<T> clazz, int obj) {
        Intent intent = new Intent();
        intent.setClass(context, clazz);
        intent.putExtra(BLConstants.INTENT_VALUE, obj);
        context.startActivity(intent);
    }
    
    public static <T> void toActivity(Context context, Class<T> clazz, String obj) {
        Intent intent = new Intent();
        intent.setClass(context, clazz);
        intent.putExtra(BLConstants.INTENT_VALUE, obj);
        context.startActivity(intent);
    }
    
    public static <T> void toActivityForResult(Activity context, Class<T> clazz, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(context, clazz);
        context.startActivityForResult(intent, requestCode);
    }
    
    public static <T> void toActivityForResult(Activity context, Class<T> clazz, int requestCode, String className) {
        Intent intent = new Intent();
        intent.setClass(context, clazz);
        intent.putExtra(BLConstants.INTENT_CLASS, className);
        context.startActivityForResult(intent, requestCode);
    }
    
    public static void toastErr(BLBaseResult result){
        String msg = "return null!";
        if(result != null && result.getMsg()!=null){
            msg = String.format("%s [%d]", result.getMsg(), result.getStatus());
        }
        BLToastUtils.show(msg);
    }
    
    public static void toastErr(BaseResult result){
        String msg = "return null!";
        if(result != null && result.getMsg()!=null){
            msg = String.format("%s [%d]", result.getMsg(), result.getStatus());
        }
        BLToastUtils.show(msg);
    }

    /***
     * 字符传是否包含双字节字符
     * @param str
     * @return
     */
    public static boolean strContainCNChar(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        for (int i = 0; i < str.length(); i++) {
            if (p.matcher(String.valueOf(str.charAt(i))).matches()) {
                return true;
            }
        }
        return false;
    }
    /**
     * Regex of url.
     */
    public static boolean isURL(final CharSequence input) {
        final String REGEX_URL           = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";
        return isMatch(REGEX_URL, input);
    }
    public static boolean isMatch(final String regex, final CharSequence input) {
        return input != null && input.length() > 0 && Pattern.matches(regex, input);
    }

    public static String Base64(byte[] data) {
        return new String(Base64.encode(data, Base64.NO_WRAP));
    }
    public static String Base64(String data) {
        return new String(Base64.encode(data.getBytes(), Base64.NO_WRAP));
    }



    public static String ints2HexString(Integer[] var0) {
        StringBuffer var1 = new StringBuffer();

        for(int var2 = 0; var2 < var0.length; ++var2) {
            String var3;
            if ((var3 = Integer.toHexString(255 & var0[var2])).length() < 2) {
                var1.append(0);
            }

            var1.append(var3);
        }

        return var1.toString().toLowerCase();
    }

    /**
     * 获取Url的域名
     *
     * @param urlString
     *            Url地址
     *
     * @return String host 域名
     *
     */
    public static String urlHost(String urlString) {
        String host = null;
        try {
            URL url = new URL(urlString);
            host = url.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return host;
    }

    /**
     * 获取Url的协议
     * @param urlString
     * @return
     */

    public static String urlProtocol(String urlString){
        String host = null;
        try {
            URL url = new URL(urlString);
            host = url.getProtocol();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return host;
    }

    /**
     * 域名转Ip地址
     *
     * @param host
     *            域名
     *
     * @return ip地址
     *
     */
    public static String hostInetAddress(String host) {
        String IPAddress = null;
        InetAddress ReturnStr1 = null;
        try {
            ReturnStr1 = java.net.InetAddress.getByName(host);
            IPAddress = ReturnStr1.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return IPAddress;
    }


    /**
     * 16进制顺序颠倒
     *
     *
     */
    public static String Hexbackrow(String b) {
        String c = "";
        String d = "";
        int b_len = b.length();
        int b_len_t = b_len / 2;
        if (b_len % 2 != 0) {
            d = "0" + b.substring(0, 1);
        }
        for (int i = 0; i < b_len_t; i++) {
            c = c + b.substring(b_len - 2, b_len);
            b_len = b_len - 2;
        }
        return c + d;
    }

    
    public static long hexto10(String b) {
        return Long.parseLong(Hexbackrow(b), 16);
    }

    /**
     * 将mac地址转为 b4:43:...:xx:xx格式
     * @param mac
     * @return
     */
    public static String formatMac(String mac) {
        if (mac.length() == 12) {
            StringBuffer sb = new StringBuffer(mac);
            sb.insert(10, ':');
            sb.insert(8, ':');
            sb.insert(6, ':');
            sb.insert(4, ':');
            sb.insert(2, ':');
            return sb.toString();
        }
        return "";
    }

    public static String dnaKitIconUrl(String fileId){
        StringBuffer stringBuffer = new StringBuffer(BLApiUrlConstants.AppManager.PRODUCT_ICON());
        stringBuffer.append(fileId);
        return stringBuffer.toString();
    }

    public static String getVersionInfo(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = null;
        int code = 0;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
            code = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return String.format("%s(%d)", name, code);
    }

    /**
     * 判断app是否存在
     * @param context
     * @return
     */

    public static boolean isAppaLive(Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        boolean isAppRunning = false;

        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(BuildConfig.APPLICATION_ID)
                    || info.baseActivity.getPackageName().equals(BuildConfig.APPLICATION_ID)) {
                isAppRunning = true;
                break;
            }
        }
        return isAppRunning;
    }

    /**
     * 获得本应用activity栈顶的activity名称
     * @param context
     * @return
     */
    public static String getPackageIopActivity(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(BuildConfig.APPLICATION_ID) || info.baseActivity.getPackageName().equals(BuildConfig.APPLICATION_ID)) {
                return info.topActivity.getClassName();
            }
        }
        return null;
    }

    /**
     * 检测应用是否在前台
     * @param context
     * @return
     */
    public static boolean isRunningForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(context.getPackageName())) {
            return true;
        }
        return false;
    }

    public static String rmModuleType2Pid(long type){
        type = type + 68000;
        String pid =  "000000000000000000000000" + tenTo16_2(type) + "00000000";
        return pid.substring(0, 32);
    }

    public static int  rmPid2ModuleType(String pid) {
        pid = pid.substring(24, 30);
        int moduleType = (int) (hexto10(pid) - 68000);
        return moduleType;
    }
    public static String tenTo16_2(long i) {
        String re = "";
        re = Long.toHexString(i);
        if (re.length() % 2 != 0) {
            re = "0" + re;
        }
        re = Hexbackrow(re);
        return re;
    }
    
    public static String deviceType2Pid(String deviceType){
        try {
            final int deviceTypeInt = Integer.parseInt(deviceType);
            final String deviceTypeHex = tenTo16_2(deviceTypeInt);
            return "000000000000000000000000" + deviceTypeHex + "00000000";
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /** 读出目录下所有文件（不递归） **/
    public static ArrayList<String> readFileNameList(String folderPath){
        final ArrayList<String> ret = new ArrayList<>();
        final File folderFile = new File(folderPath);
        if(!folderFile.isDirectory()){
            return ret;
        }
        
        for(File item : folderFile.listFiles()){
            ret.add(item.getName());
        }
        return ret;
    }
    
    /**
     * 打开文件 
     * @param file
     */
    public static void openFile(Context mContext, File file){

        if (file == null || !file.exists()) {
            BLToastUtils.show("File not exist!");
            return;
        }
        
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        String type = getMIMEType(file);
        intent.setDataAndType(/*uri*/Uri.fromFile(file), type);
        mContext.startActivity(intent);
    }
    
    /**
     * 根据文件后缀名获得对应的MIME类型。 
     * @param file
     */
    public static String getMIMEType(File file) {
        String type="*/*";
        String fName = file.getName();
        int dotIndex = fName.lastIndexOf(".");
        if(dotIndex < 0){
            return type;
        }
        String end=fName.substring(dotIndex,fName.length()).toLowerCase();
        if(end=="")return type;
        for(int i=0;i<MIME_MapTable.length;i++){
            if(end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }

    private final static String[][] MIME_MapTable={
            //{后缀名，    MIME类型}
            {".3gp",    "video/3gpp"},
            {".apk",    "application/vnd.android.package-archive"},
            {".asf",    "video/x-ms-asf"},
            {".avi",    "video/x-msvideo"},
            {".bin",    "application/octet-stream"},
            {".bmp",      "image/bmp"},
            {".c",        "text/plain"},
            {".class",    "application/octet-stream"},
            {".conf",    "text/plain"},
            {".cpp",    "text/plain"},
            {".doc",    "application/msword"},
            {".exe",    "application/octet-stream"},
            {".gif",    "image/gif"},
            {".gtar",    "application/x-gtar"},
            {".gz",        "application/x-gzip"},
            {".h",        "text/plain"},
            {".htm",    "text/html"},
            {".html",    "text/html"},
            {".jar",    "application/java-archive"},
            {".java",    "text/plain"},
            {".jpeg",    "image/jpeg"},
            {".jpg",    "image/jpeg"},
            {".js",        "application/x-javascript"},
            {".log",    "text/plain"},
            {".m3u",    "audio/x-mpegurl"},
            {".m4a",    "audio/mp4a-latm"},
            {".m4b",    "audio/mp4a-latm"},
            {".m4p",    "audio/mp4a-latm"},
            {".m4u",    "video/vnd.mpegurl"},
            {".m4v",    "video/x-m4v"},
            {".mov",    "video/quicktime"},
            {".mp2",    "audio/x-mpeg"},
            {".mp3",    "audio/x-mpeg"},
            {".mp4",    "video/mp4"},
            {".mpc",    "application/vnd.mpohun.certificate"},
            {".mpe",    "video/mpeg"},
            {".mpeg",    "video/mpeg"},
            {".mpg",    "video/mpeg"},
            {".mpg4",    "video/mp4"},
            {".mpga",    "audio/mpeg"},
            {".msg",    "application/vnd.ms-outlook"},
            {".ogg",    "audio/ogg"},
            {".pdf",    "application/pdf"},
            {".png",    "image/png"},
            {".pps",    "application/vnd.ms-powerpoint"},
            {".ppt",    "application/vnd.ms-powerpoint"},
            {".prop",    "text/plain"},
            {".rar",    "application/x-rar-compressed"},
            {".rc",        "text/plain"},
            {".rmvb",    "audio/x-pn-realaudio"},
            {".rtf",    "application/rtf"},
            {".sh",        "text/plain"},
            {".tar",    "application/x-tar"},
            {".tgz",    "application/x-compressed"},
            {".txt",    "text/plain"},
            {".wav",    "audio/x-wav"},
            {".wma",    "audio/x-ms-wma"},
            {".wmv",    "audio/x-ms-wmv"},
            {".wps",    "application/vnd.ms-works"},
            //{".xml",    "text/xml"},
            {".xml",    "text/plain"},
            {".z",        "application/x-compress"},
            {".zip",    "application/zip"},
            {"",        "*/*"}
    };


}

