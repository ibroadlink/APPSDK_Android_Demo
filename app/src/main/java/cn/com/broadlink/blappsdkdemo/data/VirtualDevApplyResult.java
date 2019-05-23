package cn.com.broadlink.blappsdkdemo.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YeJin on 2016/9/10.
 */
public class VirtualDevApplyResult extends BaseResult{

    private List<ApplyDevInfo> devlist = new ArrayList<>();

    public List<ApplyDevInfo> getDevlist() {
        return devlist;
    }

    public void setDevlist(List<ApplyDevInfo> devlist) {
        this.devlist = devlist;
    }

    public static class ApplyDevInfo{
        private String uniquesn;

        private String pid;

        private String did;

        private String mac;

        private String token;

        public String getUniquesn() {
            return uniquesn;
        }

        public void setUniquesn(String uniquesn) {
            this.uniquesn = uniquesn;
        }

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public String getDid() {
            return did;
        }

        public void setDid(String did) {
            this.did = did;
        }

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
