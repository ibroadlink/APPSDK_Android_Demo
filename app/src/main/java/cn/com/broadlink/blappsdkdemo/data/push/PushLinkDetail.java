package cn.com.broadlink.blappsdkdemo.data.push;

import java.util.ArrayList;
import java.util.List;

/**
 * Desc
 *
 * @author JiangYaqiang
 * 2019/4/19 16:57
 */
public class PushLinkDetail {

    /**
     * conditionsinfo : {"property":[{"idev_did":"did","ikey":"pir_detected","ref_value":1,"ref_value_name":"SR3有人","trend_type":4}]}
     * events : [{"dev_name":"无线门磁","idev_did":"780f77090e8f000003000100b037693e","ikey":"doorsensor_status","expression":"","keeptime":10000,"ref_value":0,
     * "ref_value_name":"无线门磁关门 ","trend_type":4,"type":0}]
     * datetime : {"weekdays":"123","timezone":8,"validperiod":["12:00:00-13:00:00","15:00:00-16:00:00"]}
     */

    public ConditionsinfoBean conditionsinfo;
    public DatetimeBean datetime;
    public List<EventsBean> events = new ArrayList<>();

    public PushLinkDetail() {
    }

    public static class ConditionsinfoBean {
        public List<PropertyBean> property;

        public static class PropertyBean {
            /**
             * idev_did : did
             * ikey : pir_detected
             * ref_value : 1
             * ref_value_name : SR3有人
             * trend_type : 4
             */

            public String idev_did;
            public String ikey;
            public int ref_value;
            public String ref_value_name;
            public int trend_type;

            public PropertyBean() {
            }
        }
    }

    public static class DatetimeBean {
        /**
         * weekdays : 123
         * timezone : 8
         * validperiod : ["12:00:00-13:00:00","15:00:00-16:00:00"]
         */

        public String weekdays;
        public int timezone;
        public List<String> validperiod = new ArrayList<>();

        public DatetimeBean() {
        }
    }

    public static class EventsBean {
        /**
         * dev_name : 无线门磁
         * idev_did : 780f77090e8f000003000100b037693e
         * ikey : doorsensor_status
         * expression : 
         * keeptime : 10000
         * ref_value : 0
         * ref_value_name : 无线门磁关门 
         * trend_type : 4
         * type : 0
         */

        public String dev_name;
        public String idev_did;
        public String ikey;
        public String expression;
        public int keeptime;
        public int ref_value;
        public String ref_value_name;
        public int trend_type;
        public int type;

        public EventsBean() {
        }
    }
}
