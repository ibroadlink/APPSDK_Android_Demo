package cn.com.broadlink.blappsdkdemo.data;

/**
 * 下载红码参数
 * Created by zhaohenghao on 2018/8/30.
 */
public class IrCodeDownloadParam {
    private String mtag;
    private String ircodeid;

    public String getMtag() {
        return mtag;
    }

    public void setMtag(String mtag) {
        this.mtag = mtag;
    }

    public String getIrcodeid() {
        return ircodeid;
    }

    public void setIrcodeid(String ircodeid) {
        this.ircodeid = ircodeid;
    }
}
