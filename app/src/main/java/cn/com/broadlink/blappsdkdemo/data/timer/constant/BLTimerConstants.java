package cn.com.broadlink.blappsdkdemo.data.timer.constant;

public class BLTimerConstants {

    public static final String DNA_TIMER_ITF = "dev_subdev_timer";
    
    public static class TYPE{
        public final static String COMM = "comm";
        public final static String DELAY = "delay";
        public final static String PERIOD = "period";
        public final static String CYCLE = "cycle";
        public final static String RAND = "rand";
        public final static String SLEEP = "sleep";
        public final static String ALL = "all";
    }

    public static class ACT{
        public final static int ADD = 0;
        public final static int DEL = 1;
        public final static int EDIT = 2;
        public final static int GET_LIST = 3;
        public final static int TOGGER = 4;
        public final static int LIMTE = 5;
        public final static int SUNSET = 6;
    }

}
