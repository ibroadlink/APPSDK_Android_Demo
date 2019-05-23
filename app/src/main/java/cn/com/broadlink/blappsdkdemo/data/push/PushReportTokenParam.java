package cn.com.broadlink.blappsdkdemo.data.push;

/**
 * Created by YeJin on 2017/7/12.
 */

public class PushReportTokenParam {
    private String userid;

    private String touser;

    private String tousertype;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getTousertype() {
        return tousertype;
    }

    public void setTousertype(String tousertype) {
        this.tousertype = tousertype;
    }
}
