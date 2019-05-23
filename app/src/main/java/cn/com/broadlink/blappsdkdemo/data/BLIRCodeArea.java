package cn.com.broadlink.blappsdkdemo.data;

/**
 * Created by zhujunjie on 2017/8/9.
 */

public class BLIRCodeArea {
    private String areaName;
    private String areaId;
    private boolean isleaf;
    private String downloadUrl;


    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }


    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public boolean isleaf() {
        return isleaf;
    }

    public void setIsleaf(boolean isleaf) {
        this.isleaf = isleaf;
    }
}
