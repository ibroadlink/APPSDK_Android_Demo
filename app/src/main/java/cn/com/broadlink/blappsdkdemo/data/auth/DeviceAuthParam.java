package cn.com.broadlink.blappsdkdemo.data.auth;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YeJin on 2016/9/10.
 */
public class DeviceAuthParam {
    private String did;

    private String pid;

    private String extend;

    private List<String> serverlist = new ArrayList<>();

    private String lid;

    private String licensesig;

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    public List<String> getServerlist() {
        return serverlist;
    }

    public void setServerlist(List<String> serverlist) {
        this.serverlist = serverlist;
    }

    public String getLid() {
        return lid;
    }

    public void setLid(String lid) {
        this.lid = lid;
    }

    public String getLicensesig() {
        return licensesig;
    }

    public void setLicensesig(String licensesig) {
        this.licensesig = licensesig;
    }
}
