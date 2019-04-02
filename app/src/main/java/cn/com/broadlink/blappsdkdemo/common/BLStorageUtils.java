package cn.com.broadlink.blappsdkdemo.common;

import android.content.Context;
import android.os.Environment;

import com.alibaba.fastjson.JSON;

import java.io.File;

import cn.com.broadlink.blappsdkdemo.data.DrpDescInfo;
import cn.com.broadlink.sdk.BLLet;

public class BLStorageUtils {
	/**H5 html主页面**/
	public static final String H5_INDEX_PAGE = "app.html";
	/**H5 场景主页面**/
	public static final String H5_CUSTOM_PAGE = "custom.html";
	
	/** APP根目录 **/
	public static String BASE_PATH = "";

	/** APP错误信息保存路径 **/
	public static String CRASH_LOG_PATH = "";

	/** 临时目录 **/
	public static String TEMP_PATH = "";

	/** 缓存路径 **/
	public static String CACHE_PATH = "";

	/** 数据共享路径 **/
	public static String SHARED_PATH = "";

	/** 云空调控制指令路径 **/
	public static String CONCODE_PATH = "";

	/** 家庭路径 存放家庭下的一些信息 **/
	public static String FAMILY_PATH = "";

	/** 设备图标路径 **/
	public static String DEVICE_ICON_PATH = "";

	/** RM设备路径 **/
	public static String IR_DATA_PATH = "";

	/** 场景图标 **/
	public static String SCENE_ICON_PATH = "";

//	/** SDK 解析库存放目录 **/
//	public static String SCRIPTS_PATH = "";

	/** MS1图标路径 **/
	public static String MS1_PATH = "";

	/** 子设备备份路径 **/
	public static String SUB_DEV_BACKUP_PATH = "";

	/** 压力测试日志路径 **/
	public static String STRESS_TEST_LOG_PATH = "";

	/** 离线图标 **/
	public static String OFF_LINE_ICON = "";

//	/** H5页面路径 **/
//	public static String DRPS_PATH = "";

	/** APP缓存数据文件夹 **/
	public static String APP_CACHE_FILE_PATH = "";

	/** 产品列表 **/
	public static String PRODUCT_LIST_PATH = "";

	/** 产品详情 **/
	public static String PRODUCT_INFO_PATH = "";

	/** 隐藏文件夹 存放S1的传感器信息文件 **/
	public static String S1_PATH = "";

	private BLStorageUtils() {}

	public static void init(Context context) {
		String rootPath = null;
		String appPath = null;
		String fileName = BLConstants.BASE_FILE_PATH;
		
		// 存在SDCARD的时候，路径设置到SDCARD
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			rootPath = Environment.getExternalStorageDirectory().getPath();
			// 不存在SDCARD的时候，路径设置到ROM
		}else{
			rootPath = context.getCacheDir().getAbsolutePath();
		}

		/***APP 统计数据文件夹**/
		File dataDir = new File(new File(rootPath, "Android"), "data");
		File appDir = new File(dataDir, context.getPackageName());
		appDir.mkdirs();

		File appCacheDir = new File(appDir, "crashLog");
		File appStatisticDir = new File(appDir, "cacheFile");
		
		appCacheDir.mkdirs();
		appStatisticDir.mkdirs();
		
		CRASH_LOG_PATH = appCacheDir.getAbsolutePath();
		APP_CACHE_FILE_PATH = appStatisticDir.getAbsolutePath();

		//产品缓存信息文件保存
		PRODUCT_LIST_PATH = APP_CACHE_FILE_PATH + File.separator + "ProductList";
		PRODUCT_INFO_PATH = APP_CACHE_FILE_PATH + File.separator + "ProductInfo";
		new File(PRODUCT_LIST_PATH).mkdirs();
		new File(PRODUCT_INFO_PATH).mkdirs();

		/********************** 一级目录创建 ********************************/
		BASE_PATH = appPath = rootPath + File.separator + fileName;

		/********************** 二级目录创建 ********************************/
		TEMP_PATH = appPath + "/temp";
		CACHE_PATH = appPath + "/cache";
		SHARED_PATH = appPath + File.separator + BLConstants.FILE_SHARE;
		S1_PATH = appPath + File.separator + BLConstants.FILE_S1;
		OFF_LINE_ICON = appPath + File.separator + BLConstants.OFF_LINE_ICON;

		/********************** 三级目录 创建 ********************************/
		CONCODE_PATH = SHARED_PATH + File.separator + BLConstants.FILE_CON_CODE;
		DEVICE_ICON_PATH = SHARED_PATH + File.separator + BLConstants.FILE_DEVICE_ICON;
		IR_DATA_PATH = SHARED_PATH + File.separator + BLConstants.FILE_IR_DATA;
		SCENE_ICON_PATH = SHARED_PATH + File.separator + BLConstants.SCENE_NAME;
//		SCRIPTS_PATH = SHARED_PATH + File.separator + BLConstants.FILE_SCRIPTS;
		MS1_PATH = SHARED_PATH + File.separator + BLConstants.FILE_MS1;
		SUB_DEV_BACKUP_PATH = SHARED_PATH + File.separator + BLConstants.FILE_SUB_DEV_BACKUP;
		STRESS_TEST_LOG_PATH = SHARED_PATH + File.separator + BLConstants.FILE_STRESS_TEST_LOG_PATH;
//		DRPS_PATH = SHARED_PATH + File.separator + BLConstants.FILE_DRPS;
		FAMILY_PATH = SHARED_PATH + File.separator + BLConstants.FILE_FAMILY;

		/********************** 一级目录 ********************************/
		new File(BASE_PATH).mkdirs();

		/********************** 二级目录 ********************************/
		new File(TEMP_PATH).mkdirs();
		new File(CACHE_PATH).mkdirs();
		new File(SHARED_PATH).mkdirs();
		new File(S1_PATH).mkdirs();
		new File(OFF_LINE_ICON).mkdirs();
		new File(OFF_LINE_ICON, ".nomedia").mkdirs();

		/********************** 三级目录 ********************************/
		new File(CONCODE_PATH).mkdirs();
		new File(DEVICE_ICON_PATH).mkdirs();
		new File(DEVICE_ICON_PATH, ".nomedia").mkdirs();
		new File(IR_DATA_PATH).mkdirs();
		new File(SCENE_ICON_PATH).mkdirs();
		new File(SCENE_ICON_PATH, ".nomedia").mkdirs();
//		new File(SCRIPTS_PATH).mkdirs();
		new File(MS1_PATH).mkdirs();
		new File(SUB_DEV_BACKUP_PATH).mkdirs();
		new File(STRESS_TEST_LOG_PATH).mkdirs();
//		new File(DRPS_PATH).mkdirs();
		new File(FAMILY_PATH).mkdirs();
	}

	/**
     * 获取产品脚本文件的绝对路径
     * 
     * @param pid
     * 			设备产品id
     * @return
     */
    public static String getScriptAbsolutePath(String pid){
		return BLLet.Controller.queryScriptPath(pid);
    }
 
    /**
     * 获取设备H5主显示页面
     * 
     * @param pid
     * 			设备产品id
     * @return
     */
	public static String getH5IndexPath(String pid) {
		String folderPath = languageFolder(pid);
		if(folderPath != null){
			return folderPath + File.separator + H5_INDEX_PAGE;
		}
		return null;
	}
	
	/**
	 * 获取设备H5 custom.html文件目录
	 *		此HTML可以选择设备的参数用来执行场景或者定时命名
	 *		#创建场景：custom.html?type=scene
	 *		#创建定时：custom.html?type=timer
	 * @param pid
	 * 			设备产品id
	 * @return
	 */
	public static String getH5DeviceParamPath(String pid) {
		String folderPath = languageFolder(pid);
		if(folderPath != null){
			return folderPath + File.separator + H5_CUSTOM_PAGE;
		}
		return null;
	}
	
	/***
	 * 返回语言包的文件夹夹
	 * 	例如 sdcard/broadlink/pid/zh
	 * @param pid
	 * @return
	 */
	public static String languageFolder(String pid){
		String language = BLCommonUtils.getLanguage();
		String folderPath = BLLet.Controller.queryUIPath(pid) + File.separator + language;
		File languageFolder = new File(folderPath);
		if(languageFolder.exists()){
			return folderPath;
		}else{
			String[] languages = language.split("-");
			String countryPath = BLLet.Controller.queryUIPath(pid) + File.separator + languages[0];
			File countryFolder = new File(countryPath);
			if(countryFolder.exists()){
				return countryPath;
			}
		}

		//解析本地desc。json文件，获取默认语言文件夹
		String content = BLFileUtils.readTextFileContent(getH5Folder(pid) + "/desc.json");
		if(content != null){
			DrpDescInfo descInfo = JSON.parseObject(content, DrpDescInfo.class);
			if(descInfo != null){
				return BLLet.Controller.queryUIPath(pid) + File.separator +  descInfo.getDefault_lang();
			}
		}

		return null;
	}
	
	 /**
     * 获取设备H5 文件夹
     * 
     * @param pid
     * 			设备产品id
     * @return
     */
	public static String getH5Folder(String pid) {
		return BLLet.Controller.queryUIPath(pid);
	}

}
