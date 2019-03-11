package cn.com.broadlink.blappsdkdemo.data.auth;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/2/17 0017.
 */

public class AuthorityInfo implements Serializable {

    private String idtype;
    private String bizcode;
    private String authname;

    public String getIdtype() {
        return idtype;
    }

    public void setIdtype(String idtype) {
        this.idtype = idtype;
    }

    public String getBizcode() {
        return bizcode;
    }

    public void setBizcode(String bizcode) {
        this.bizcode = bizcode;
    }

    public String getAuthname() {
        return authname;
    }

    public void setAuthname(String authname) {
        this.authname = authname;
    }
}
