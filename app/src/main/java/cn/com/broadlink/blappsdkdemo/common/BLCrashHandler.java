package cn.com.broadlink.blappsdkdemo.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.com.broadlink.blappsdkdemo.BLApplication;

/**
 *     
 * 项目名称：BLEControlAppV4    
 * 类名称：BLCrashHandler    
 * 类描述：   异常错误信息抓取
 * 创建人：YeJing 
 * 创建时间：2015-3-24 下午4:20:05    
 * 修改人：Administrator    
 * 修改时间：2015-3-24 下午4:20:05    
 * 修改备注：    
 * @version     
 *
 */
public class BLCrashHandler implements UncaughtExceptionHandler {
	/**
	 * 	Log日志的tag
	 */
	private static final String TAG = "Econtrol Err Message" ;

	/**
	 * 	系统默认的UncaughtException处理类
	 * 	Thread.UncaughtExceptionHandler			:		mDefaultHandler	
	 */
	private Thread.UncaughtExceptionHandler mDefaultHandler ;
	
	/**
	 * 	CrashHandler实例
	 * 	CrashHandler			:		mInstance	
	 */
	private static BLCrashHandler mInstance = new BLCrashHandler() ;

	/**
	 * 	程序的Context对象
	 * 	Context			:		mContext	
	 */
	private Context mContext ;

	/**
	 * 	用来存储设备信息和异常信息
	 * 	Map<String,String>			:		mLogInfo	
	 */
	private Map<String , String> mLogInfo = new HashMap<String , String>() ;
	
	/**
	 * 	用于格式化日期,作为日志文件名的一部分
	 * 	SimpleDateFormat			:		mSimpleDateFormat	
	 */
	private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyyMMdd_HH-mm-ss") ;
	
	/**
	 * 	Creates a new instance of CrashHandler.
	 */
	private BLCrashHandler() {
	}
	
	private String mSavePath = Environment.getExternalStorageDirectory()
			+ "/BroadLinkAppCrashLog";
	/**
	 * 	getInstance:{获取CrashHandler实例 ,单例模式 }
	 *  ──────────────────────────────────
	 * 	@return 	CrashHandler   
	 * 	@throws 
	 * 	@since  	I used to be a programmer like you, then I took an arrow in the knee　Ver 1.0
	 *	──────────────────────────────────────────────────────────────────────────────────────────────────────
	 */
	public static BLCrashHandler getInstance() {
		return mInstance ;
	}
	
	public void setLogSavePath(String path){
		mSavePath = path;
	}
	
	/**
	 * 	init:{初始化}
	 *  ──────────────────────────────────
	 * 	@param 		paramContext
	 * 	@return 	void   
	 * 	@throws 
	 * 	@since  	I used to be a programmer like you, then I took an arrow in the knee　Ver 1.0
	 *	──────────────────────────────────────────────────────────────────────────────────────────────────────
	 */
	public void init(Context paramContext) {
		mContext = paramContext ;
		// 获取系统默认的UncaughtException处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler() ;
		// 设置该CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this) ;
	}
	
	/**
	 * 	当UncaughtException发生时会转入该重写的方法来处理
	 * 	(non-Javadoc)
	 * 	@see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread, java.lang.Throwable)
	 */
	public void uncaughtException(Thread paramThread , final Throwable paramThrowable) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				BLLog.e(TAG, paramThrowable.getMessage(), paramThrowable);

				((BLApplication) mContext.getApplicationContext()).appFinish();
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(0) ;
			}
		}).start();

		if(! handleException(paramThrowable) && mDefaultHandler != null) {
			// 如果自定义的没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(paramThread , paramThrowable) ;
		}else {
//			try {
//				// 如果处理了，让程序继续运行1秒再退出，保证文件保存并上传到服务器
//				paramThread.sleep(1000) ;
//			}
//			catch(InterruptedException e) {
//				e.printStackTrace() ;
//			}
			// 退出程序
//			android.os.Process.killProcess(android.os.Process.myPid()) ;
		}
	}

	/**
	 * 	handleException:{自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.}
	 *  ──────────────────────────────────
	 * 	@param 		paramThrowable
	 * 	@return 	true:如果处理了该异常信息;否则返回false.   
	 *	──────────────────────────────────────────────────────────────────────────────────────────────────────
	 */
	public boolean handleException(Throwable paramThrowable) {
		if(paramThrowable == null)
			return false ;
		// 获取设备参数信息
		getDeviceInfo(mContext) ;
		// 保存日志文件
		saveCrashLogToFile(paramThrowable) ;
		return false ;
	}
	
	/**
	 * 	getDeviceInfo:{获取设备参数信息}
	 *  ──────────────────────────────────
	 * 	@param 		paramContext
	 * 	@throws 
	 * 	@since  	I used to be a programmer like you, then I took an arrow in the knee　Ver 1.0
	 *	──────────────────────────────────────────────────────────────────────────────────────────────────────
	 */
	public void getDeviceInfo(Context paramContext) {
		try {
			// 获得包管理器
			PackageManager mPackageManager = paramContext.getPackageManager() ;
			// 得到该应用的信息，即主Activity
			PackageInfo mPackageInfo = mPackageManager.getPackageInfo(
					paramContext.getPackageName() , PackageManager.GET_ACTIVITIES) ;
			if(mPackageInfo != null) {
				String versionName = mPackageInfo.versionName == null ? "null"
						: mPackageInfo.versionName ;
				String versionCode = mPackageInfo.versionCode + "" ;
				mLogInfo.put("versionName" , versionName) ;
				mLogInfo.put("versionCode" , versionCode) ;
			}
		}
		catch(NameNotFoundException e) {
			e.printStackTrace() ;
		}
		// 反射机制
		Field[] mFields = Build.class.getDeclaredFields() ;
		// 迭代Build的字段key-value  此处的信息主要是为了在服务器端手机各种版本手机报错的原因
		for(Field field : mFields) {
			try {
				field.setAccessible(true) ;
				mLogInfo.put(field.getName() , field.get("").toString()) ;
				BLLog.d(TAG , field.getName() + ":" + field.get("")) ;
			}
			catch(IllegalArgumentException e) {
				e.printStackTrace() ;
			}
			catch(IllegalAccessException e) {
				e.printStackTrace() ;
			}
		}
	}
	
	/**
	 * 	saveCrashLogToFile:{将崩溃的Log保存到本地}
	 *  ──────────────────────────────────
	 * 	@param 		paramThrowable
	 * 	@return		FileName
	 * 	@throws 
	 * 	@since  	I used to be a programmer like you, then I took an arrow in the knee　Ver 1.0
	 *	──────────────────────────────────────────────────────────────────────────────────────────────────────
	 */
	private String saveCrashLogToFile(Throwable paramThrowable) {
		StringBuffer mStringBuffer = new StringBuffer() ;
		for(Map.Entry<String, String> entry : mLogInfo.entrySet()) {
			String key = entry.getKey() ;
			String value = entry.getValue() ;
			mStringBuffer.append(key + "=" + value + "\r\n") ;
		}
		Writer mWriter = new StringWriter() ;
		PrintWriter mPrintWriter = new PrintWriter(mWriter) ;
		paramThrowable.printStackTrace(mPrintWriter) ;
		Throwable mThrowable = paramThrowable.getCause() ;
		// 迭代栈队列把所有的异常信息写入writer中
		while(mThrowable != null) {
			mThrowable.printStackTrace(mPrintWriter) ;
			// 换行  每个个异常栈之间换行
			mPrintWriter.append("\r\n") ;
			mThrowable = mThrowable.getCause() ;
		}
		//记得关闭
		mPrintWriter.close() ;
		String mResult = mWriter.toString() ;
		mStringBuffer.append(mResult) ;
		// 保存文件，设置文件名
		String mTime = mSimpleDateFormat.format(new Date()) ;
		String mFileName = "CrashLog-" + mTime + ".log" ;
		try {
			File mDirectory = new File(mSavePath) ;
			BLLog.d(TAG , mDirectory.toString()) ;
			if( ! mDirectory.exists())
				mDirectory.mkdir() ;
			FileOutputStream mFileOutputStream = new FileOutputStream(mDirectory + "/"
					+ mFileName) ;
			mFileOutputStream.write(mStringBuffer.toString().getBytes()) ;
			mFileOutputStream.close() ;
			return mFileName ;
		}
		catch(FileNotFoundException e) {
			e.printStackTrace() ;
		}
		catch(IOException e) {
			e.printStackTrace() ;
		}
	
		return null ;
	}
}
