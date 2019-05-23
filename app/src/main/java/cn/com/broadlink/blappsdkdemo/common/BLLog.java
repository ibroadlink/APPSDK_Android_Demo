package cn.com.broadlink.blappsdkdemo.common;

import android.util.Log;

public final class BLLog {
    
    /***是否打印Log Debug信息***/
    private static boolean DEBUG = true;
    
    /***是否打印 Info信息***/
    private static boolean INFO = true;
    
    /***是否打印Log error信息***/
    private static boolean ERROR = true;
    
    /***是否打印Log warn信息***/
    private static boolean WARN = true;

    /**
     * Send a {@link #DEBUG} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void d(String tag, String msg){
        if(DEBUG) {
            Log.d(tag, msg);}
    }
    
    /**
     * Send a {@link #DEBUG} log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static void d(String tag, String msg, Throwable tr){
        if(DEBUG) {
            Log.d(tag, msg, tr);}
    }
    
    /**
     * Send an {@link #INFO} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void i(String tag, String msg){
        if(INFO) {
            Log.i(tag, msg);}
    }
    
    /**
     * Send a {@link #INFO} log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static void i(String tag, String msg, Throwable tr){
        if(INFO) {
            Log.i(tag, msg, tr);}
    }
    
    /**
     * Send an {@link #ERROR} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void e(String tag, String msg){
        if(ERROR) {
            Log.e(tag, msg);}
    }
    
    /**
     * Send a {@link #ERROR} log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static void e(String tag, String msg, Throwable tr){
        if(ERROR) {
            Log.e(tag, msg, tr);}
    }
    
    /**
     * Send a {@link #WARN} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void w(String tag, String msg){
        if(WARN) {
            Log.w(tag, msg);}
    }
    

    /**
     * Send a {@link #WARN} log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static void w(String tag, String msg, Throwable tr){
        if(WARN) {
            Log.w(tag, msg, tr);}
    }
}
