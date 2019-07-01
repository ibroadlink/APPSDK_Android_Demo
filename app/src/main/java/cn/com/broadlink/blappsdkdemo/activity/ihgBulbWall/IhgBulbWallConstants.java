package cn.com.broadlink.blappsdkdemo.activity.ihgBulbWall;

/**
 *
 * @author JiangYaqiang
 * 2019/6/27 10:38
 */
public class IhgBulbWallConstants {

    public static final int BULB_COUNT = 256;
    public static final int BULB_FRAME_SIZE = 40;
    
    public static final class  ITF{
        /** 由于分包的需要，相同的msgid表示一个完整的设置 **/
        public static final String MSGID = "msgid";
        /** 分包数量 **/
        public static final String COUNTER = "counter";
        /** 分包的序列号 **/
        public static final String SEQUENCE = "sequence";
        /** 配置虚拟设备时H5需选择不同的分类，页面展示不同的元素功能按钮/菜单 **/
        public static final String CATEGORY = "category";
        /** 操作
         * 0: 灯组阵列管理
         * 1：流式显示
         * 2：图片显示
         **/
        public static final String ACT = "act";
        /**
         *     {
         *     loop:"single|list",
         *     times:[1~xxx],
         *     interval:3,
         *     brightness:[up|down]
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

    public final class OPT_CAT{
        public static final int MANAGE = 0;
        public static final int STREAM = 1;
        public static final int IMAGE = 2;
    }
    
    public final class SCENE_ACT{
        public static final int STOP = 0;
        public static final int START = 1;
        public static final int PAUSE = 2;
        public static final int RESUME = 3;
        public static final int EDIT = 4;
    }
    
    public final class LOOP_TYPE{
        public static final String SINGLE = "single";
        public static final String LIST = "list";
    }
}
