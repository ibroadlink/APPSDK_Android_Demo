package cn.com.broadlink.blappsdkdemo.data.auth;


import cn.com.broadlink.base.BLBaseResult;

/**
 * Created by YeJin on 2016/9/10.
 */
public class DeviceAuthResult extends BLBaseResult {
    private String ticket;

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
}
