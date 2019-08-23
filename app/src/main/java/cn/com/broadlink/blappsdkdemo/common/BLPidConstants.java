package cn.com.broadlink.blappsdkdemo.common;

/**
 * Desc
 *
 * @author JiangYaqiang
 * 2019/8/20 16:15
 */
public class BLPidConstants {

    public static final String PID_GROUP = "000000000000000000000000AAAA0000";
    
    public static final boolean isFastconGroup(String pid){
        return PID_GROUP.equalsIgnoreCase(pid);
    }
}
