package cn.com.broadlink.blappsdkdemo.data;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.base.BLBaseResult;

/**
 * Created by YeJin on 2016/2/24.
 */
public class GetAreaListResult extends BLBaseResult {

    public List<DataBean> data = new ArrayList<>();

    public static class DataBean {
        /**
         * country : 中国
         * code : 1
         * children : [{"province":"北京","code":"11","subchildren":[{"city":"东城","code":"1"},{"city":"西城","code":"2"},{"city":"朝阳","code":"5"},{"city":"丰台","code":"6"},{"city
         * ":"石景山","code":"7"},{"city":"海淀","code":"8"}]}]
         */

        public String country;
        public String code;
        public List<ChildrenBean> children = new ArrayList<>();

        public static class ChildrenBean {
            /**
             * province : 北京
             * code : 11
             * subchildren : [{"city":"东城","code":"1"},{"city":"西城","code":"2"},{"city":"朝阳","code":"5"},{"city":"丰台","code":"6"},{"city":"石景山","code":"7"},{"city":"海淀",
             * "code":"8"}]
             */

            public String province;
            public String code;
            public List<SubchildrenBean> subchildren = new ArrayList<>();

            public static class SubchildrenBean {
                /**
                 * city : 东城
                 * code : 1
                 */

                public String city;
                public String code;
            }
        }
    }
}
