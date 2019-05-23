package cn.com.broadlink.blappsdkdemo.data;

import java.util.ArrayList;

/**
 * Created by YeJin on 2016/2/25.
 */
public class GetDNAKitProductListResult {
    private int status = -1;

    private ArrayList<DNAKitProductInfo> productlist = new ArrayList<>();

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ArrayList<DNAKitProductInfo> getProductlist() {
        return productlist;
    }

    public void setProductlist(ArrayList<DNAKitProductInfo> productlist) {
        this.productlist = productlist;
    }
}
