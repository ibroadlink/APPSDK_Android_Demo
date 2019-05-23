package cn.com.broadlink.blappsdkdemo.data;

import java.util.List;

/**
 * Created by YeJin on 2016/2/25.
 */
public class GetDNAKitProductListParam {
    private String categoryid;

    private String brandid;

    private List<String> protocols;

    private String brandname;

    public String getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(String categoryid) {
        this.categoryid = categoryid;
    }

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

    public String getBrandname() {
        return brandname;
    }

    public void setBrandname(String brandname) {
        this.brandname = brandname;
    }
}
