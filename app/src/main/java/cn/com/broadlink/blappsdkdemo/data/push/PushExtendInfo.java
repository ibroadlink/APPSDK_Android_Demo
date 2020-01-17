package cn.com.broadlink.blappsdkdemo.data.push;

import java.util.List;

/**
 * Desc
 *
 * @author JiangYaqiang
 * 2019/4/19 17:04
 */
public class PushExtendInfo {

    public List<ActionBean> action;

    public static class ActionBean {
        /**
         * alicloud : {"action":"","content":"{\"name\":\"$name$\",\"说明\":\"h5需要把keylist的参数补充为左边这种格式\"}","templatecontent":"打开%v(通知中心原来模板内容)",
         * "did":"780f7727e691000005000100b8390cb2","enable":true,"tagcode":"","templateid":""}
         * language : zh-cn
         * name : 
         * status : 测试
         */

        public AlicloudBean alicloud;
        public String language;
        public String name;
        public String status;

        public ActionBean() {
        }

        public static class AlicloudBean {
            /**
             * action : 
             * content : {"name":"$name$","说明":"h5需要把keylist的参数补充为左边这种格式"}
             * templatecontent : 打开%v(通知中心原来模板内容)
             * did : 780f7727e691000005000100b8390cb2
             * enable : true
             * tagcode : 
             * templateid : 
             */

            public String action;
            public String content;
            public String templatecontent;
            public String did;
            public boolean enable = true;
            public String tagcode;
            public String templateid;

            public AlicloudBean() {
            }
        }
    }
}
