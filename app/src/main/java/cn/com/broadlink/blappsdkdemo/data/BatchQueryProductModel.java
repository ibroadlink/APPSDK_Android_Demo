package cn.com.broadlink.blappsdkdemo.data;

import java.util.List;

/**
 * Created by Administrator on 2017/7/26 0026.
 */

public class BatchQueryProductModel {

    private String brandid;
    private List<ProductversionlistBean> productversionlist;

    public String getBrandid() {
        return brandid;
    }

    public void setBrandid(String brandid) {
        this.brandid = brandid;
    }

    public List<ProductversionlistBean> getProductversionlist() {
        return productversionlist;
    }

    public void setProductversionlist(List<ProductversionlistBean> productversionlist) {
        this.productversionlist = productversionlist;
    }

    public static class ProductversionlistBean {
        private String pid;
        private String productversion;

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public String getProductversion() {
            return productversion;
        }

        public void setProductversion(String productversion) {
            this.productversion = productversion;
        }
    }
}
