package cn.com.broadlink.blappsdkdemo.data.space;

import java.util.List;

/**
 * Desc
 *
 * @author JiangYaqiang
 * 2019/11/30 14:42
 */
public class DataSpaceInfo {

    public List<SpaceBean> topSpace;
    public List<SpaceBean> subSpace;
    public SpaceBean space;

    public DataSpaceInfo() {
    }

    public static class SpaceBean {

        public int userType;
        public SpaceInfoBean spaceInfo;

        public SpaceBean() {
        }

        public static class SpaceInfoBean {
            public String spaceOpenId;
            public String companyId;
            public String name;
            public String parentId;
            public String extend;
            public String master;
            public String updateTime;
            
            public SpaceInfoBean() {
            }
        }
    }
}
