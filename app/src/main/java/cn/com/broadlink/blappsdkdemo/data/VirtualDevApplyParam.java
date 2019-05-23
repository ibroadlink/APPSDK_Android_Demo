package cn.com.broadlink.blappsdkdemo.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YeJin on 2016/9/10.
 */
public class VirtualDevApplyParam {

    private String companyid;

    private List<ApplyInfo> devlist = new ArrayList<>();

    public String getCompanyid() {
        return companyid;
    }

    public void setCompanyid(String companyid) {
        this.companyid = companyid;
    }

    public List<ApplyInfo> getDevlist() {
        return devlist;
    }

    public void setDevlist(List<ApplyInfo> devlist) {
        this.devlist = devlist;
    }

    public static class ApplyInfo{
        private String pid;

        private String uniquesn;

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public String getUniquesn() {
            return uniquesn;
        }

        public void setUniquesn(String uniquesn) {
            this.uniquesn = uniquesn;
        }
    }
}
