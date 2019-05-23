package cn.com.broadlink.blappsdkdemo.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.blappsdkdemo.common.BLProfileTools;
import cn.com.broadlink.blappsdkdemo.utils.http.BLHttpErrCode;

/**
 * 
 * 项目名称：BLEControlAppV4 <br>
 * 类名称：AddDeviceDetailInfo <br>
 * 类描述： 添加的设备详细信息 <br>
 * 创建人：YeJing <br>
 * 创建时间：2015-5-9 上午10:43:44 <br>
 * 修改人：Administrator <br>
 * 修改时间：2015-5-9 上午10:43:44 <br>
 * 修改备注：
 * 
 */
public class ProductInfoResult extends BaseResult{
    private int status = -1;

    private ProductDninfo productinfo;

    public ProductDninfo getProductinfo() {
        return productinfo;
    }

    public void setProductinfo(ProductDninfo productinfo) {
        this.productinfo = productinfo;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    public boolean isSuccess(){
        return status == BLHttpErrCode.SUCCESS;
    }

    public static class ProductDninfo implements Serializable {
        private static final long serialVersionUID = 7784629427007340255L;

        private String pid;

        private String name;

        private String brandname;

        private String model;

        //模块快捷图标
        private String shortcuticon;

        private String icon;

        // 介绍图片
        private List<String> ads = new ArrayList<String>();

        private List<DetailItemInfo> introduction = new ArrayList<DetailItemInfo>();

        private String beforecfgpurl;

        private String configpic;

        private String cfgfailedurl;
        private String resetpic;
        private String resettext;
        private String configtext;
        private String productversion;
        private ArrayList<String> pids;
        private String mappid;

        //产品profile信息
        private String profile;

        //产品协议
        private String protocol;

        private int rank;

        public String getMappid() {
            return mappid;
        }

        public void setMappid(String mappid) {
            this.mappid = mappid;
        }

        public ArrayList<String> getPids() {
            return pids;
        }

        public void setPids(ArrayList<String> pids) {
            this.pids = pids;
        }

        public static long getSerialVersionUID() {
            return serialVersionUID;
        }

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

	    public String getBrandname() {
		    if(brandname == null){
			    return "";
		    }else{
			    return brandname;
		    }
	    }

	    public void setBrandname(String brandname) {
		    this.brandname = brandname;
	    }

	    public String getModel() {
		    if(model == null){
			    return "";
		    }else{
			    return model;
		    }
	    }

        public void setModel(String model) {
            this.model = model;
        }

        public String getShortcuticon() {
            return shortcuticon;
        }

        public void setShortcuticon(String shortcuticon) {
            this.shortcuticon = shortcuticon;
        }

        public List<String> getAds() {
            return ads;
        }

        public void setAds(List<String> ads) {
            this.ads = ads;
        }

        public List<DetailItemInfo> getIntroduction() {
            return introduction;
        }

        public void setIntroduction(List<DetailItemInfo> introduction) {
            this.introduction = introduction;
        }

        public String getBeforecfgpurl() {
            return beforecfgpurl;
        }

        public void setBeforecfgpurl(String beforecfgpurl) {
            this.beforecfgpurl = beforecfgpurl;
        }

        public String getConfigpic() {
            return configpic;
        }

        public void setConfigpic(String configpic) {
            this.configpic = configpic;
        }

        public String getCfgfailedurl() {
            return cfgfailedurl;
        }

        public void setCfgfailedurl(String cfgfailedurl) {
            this.cfgfailedurl = cfgfailedurl;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
        public String getResetpic() {
            return resetpic;
        }

        public void setResetpic(String resetpic) {
            this.resetpic = resetpic;
        }

        public String getResettext() {
            return resettext;
        }

        public void setResettext(String resettext) {
            this.resettext = resettext;
        }

        public String getConfigtext() {
            return configtext;
        }

        public void setConfigtext(String configtext) {
            this.configtext = configtext;
        }

        public String getProductversion() {
            return productversion;
        }

        public void setProductversion(String productversion) {
            this.productversion = productversion;
        }

        public String getProfile() {
            return profile;
        }

        public void setProfile(String profile) {
            this.profile = profile;
        }

        public String getProtocol() {
            return protocol;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }

        public BLDevProfileInfo profileDetailInfo() {
            return BLProfileTools.parseObject(this.profile);
        }

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }
    }

    public static class DetailItemInfo implements Serializable {

        private static final long serialVersionUID = 399370100673616469L;

        private String name;

        private String icon;

        private String url;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }
}
