package cn.com.broadlink.blappsdkdemo.data;

import java.util.List;

/**
 * Created by YeJin on 2016/2/24.
 */
public class GetDNAKitDirParam {

    private String brandid;

    private List<String> protocols;

    public String getBrandid() {
        return brandid;
    }

    public void setBrandid(String brandid) {
        this.brandid = brandid;
    }

    public List<String> getProtocols() {
        return protocols;
    }

    public void setProtocols(List<String> protocols) {
        this.protocols = protocols;
    }
}
