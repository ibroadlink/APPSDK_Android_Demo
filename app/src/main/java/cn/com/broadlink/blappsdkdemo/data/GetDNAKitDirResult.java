package cn.com.broadlink.blappsdkdemo.data;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.blappsdkdemo.db.data.DNAKitDirInfo;

/**
 * Created by YeJin on 2016/2/24.
 */
public class GetDNAKitDirResult{

    private int status = - 1;

    private List<ProductInfoResult.ProductDninfo> hotproducts = new ArrayList<>();

    private List<DNAKitDirInfo> categorylist = new ArrayList<>();

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<DNAKitDirInfo> getCategorylist() {
        return categorylist;
    }

    public void setCategorylist(List<DNAKitDirInfo> categorylist) {
        this.categorylist = categorylist;
    }

    public List<ProductInfoResult.ProductDninfo> getHotproducts() {
        return hotproducts;
    }

    public void setHotproducts(List<ProductInfoResult.ProductDninfo> hotproducts) {
        this.hotproducts = hotproducts;
    }
}
