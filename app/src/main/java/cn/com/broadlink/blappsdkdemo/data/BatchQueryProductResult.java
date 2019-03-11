package cn.com.broadlink.blappsdkdemo.data;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/26 0026.
 */

public class BatchQueryProductResult extends BaseResult {
    private int status;
    private ArrayList<ProductInfoResult.ProductDninfo> allproductinfo;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ArrayList<ProductInfoResult.ProductDninfo> getAllproductinfo() {
        return allproductinfo;
    }

    public void setAllproductinfo(ArrayList<ProductInfoResult.ProductDninfo> allproductinfo) {
        this.allproductinfo = allproductinfo;
    }
}
