package cn.com.broadlink.blappsdkdemo.data.auth;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备认证查询返回信息
 * Created by YeJin on 2016/9/25.
 */
public class DevAuthInfo {
    private String authid;

    private String userid;

    private String did;

    private String pid;

    private String ticket;

    private List<String> serverlist = new ArrayList<>();

    private String authtime;

    public String getAuthid() {
        return authid;
    }

    public void setAuthid(String authid) {
        this.authid = authid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

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

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public List<String> getServerlist() {
        return serverlist;
    }

    public void setServerlist(List<String> serverlist) {
        this.serverlist = serverlist;
    }

    public String getAuthtime() {
        return authtime;
    }

    public void setAuthtime(String authtime) {
        this.authtime = authtime;
    }
}
