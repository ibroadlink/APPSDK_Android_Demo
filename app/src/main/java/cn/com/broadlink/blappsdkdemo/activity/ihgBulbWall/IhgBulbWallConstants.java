package cn.com.broadlink.blappsdkdemo.activity.ihgBulbWall;

/**
 *
 * @author JiangYaqiang
 * 2019/6/27 10:38
 */
public class IhgBulbWallConstants {

    /** 默认256盏灯 **/
    public static final int BULB_COUNT = 256;
    
    /** 每帧40盏灯，约定mac和rgb同时传递 **/
    public static final int BULB_FRAME_SIZE = 40;
    
    
    /** 接口列表 **/
    public static final class  ITF{
        
        /** 由于分包的需要，相同的msgid表示一个完整的设置 **/
        public static final String MSGID = "vt_msgid";
        
        /** 分包数量 **/
        public static final String COUNTER = "vt_counter";
        
        /** 分包的序列号 **/
        public static final String SEQUENCE = "vt_sequence";

        /** 操作
         * 0: 灯组阵列管理
         * 1：流式显示
         * 2：图片显示
         **/
        public static final String CATEGORY = "vt_category";

        /**
         * 4: 编辑场景
         * 3：恢复场景
         * 2: 暂停场景
         * 1: 执行场景
         * 0: 关闭/停止场景
         */
        public static final String ACT = "vt_act";
        
        /**
         *     {
         *     loop:"single|list",
         *     times:[1-xxx],
         *     interval:3,
         *     brightness:[0-100]
         *      }
         */
        public static final String SHOW_PARAM = "show_param";
        
        /** 灯组mac列表 **/
        public static final String MACLIST = "maclist";
        
        /** 灯组色彩列表 **/
        public static final String RGBLIST = "rgblist";


        public static final String LOOP = "loop";
        public static final String TIMES = "times";
        public static final String INTERVAL = "interval";
        public static final String BRIGHTNESS = "brightness";
    }

    /** cat字段，取值 **/
    public final class OPT_CAT{
        public static final int MANAGE = 0;
        public static final int STREAM = 1;
        public static final int IMAGE = 2;
    }
    
    /** 场景字段，取值 **/
    public final class SCENE_ACT{
        public static final int STOP = 0;
        public static final int START = 1;
        public static final int PAUSE = 2;
        public static final int RESUME = 3;
        public static final int EDIT = 4;
    }
    
    /** 循环类型，取值 **/
    public final class LOOP_TYPE{
        public static final String SINGLE = "single";
        public static final String LIST = "list";
    }
}
