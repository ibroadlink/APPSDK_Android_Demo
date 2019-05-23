package cn.com.broadlink.blappsdkdemo.data.fastcon.constant;

public class BLFastconConstans {

    public static final String ITF_MARST = "fastcon_no_config";
    
    public static final class ACT{
        public static final int GET_NEW_LIST = 0;
        public static final int BATCH_CONFIG = 1;
        public static final int GET_CONFIG_STATUS = 2;
    }
    
    public static final class CONFIG_STATUS{
        public static final int WAIT = 0;
        public static final int DOING = 1;
        public static final int SUCCESS = 2;
        public static final int TIMEOUT = 3;
        public static final int OFFLINE = 4;
    }
}
