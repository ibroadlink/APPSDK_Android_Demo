package cn.com.broadlink.blappsdkdemo.data;

import java.io.Serializable;

/**
 * 空调品牌信息 Project: BLEControlAppV4</p> Title: AcBrandInfo</p> Company:
 * BroadLink</p>
 * 
 * @author YeJing
 * @date 2015-10-13
 */
public class BLRmBrandInfo implements Serializable {
	private static final long serialVersionUID = -6589587113784527379L;

	//主键
	private long id;
	
	//品牌id
	private int brandid;
	
	//品牌名称
	private String name;

	//新接口字段-品牌名称
	private String brand;

	//语言
	private String language;

	//新接口字段
	private String enbrand;
	
	//是否是著名品牌
	private int famousstatus;

	//品牌
	private String pinyin;
	
	//表示是电视的品牌或者是空调的品牌
	private int type;
	
	//品牌名称
	private String icon;
	
	//版本
	private String version;
	
	//内部频道id
	private int channelid;
	
	//对应的频道数字，-1表示数据库中没有该数据
	private int serialnum;
    
	private long provinceid;
	
	private long cityid;
	
	private long providerid;

	private String providertype;

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getEnbrand() {
		return enbrand;
	}

	public void setEnbrand(String enbrand) {
		this.enbrand = enbrand;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getBrandid() {
		return brandid;
	}

	public void setBrandid(int brandid) {
		this.brandid = brandid;
	}

	public String getName() {
		return brand;
	}

	public void setName(String name) {
		this.brand = name;
	}

	public int getFamousstatus() {
		return famousstatus;
	}

	public void setFamousstatus(int famousstatus) {
		this.famousstatus = famousstatus;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getChannelid() {
		return channelid;
	}

	public void setChannelid(int channelid) {
		this.channelid = channelid;
	}

	public int getSerialnum() {
		return serialnum;
	}

	public void setSerialnum(int serialnum) {
		this.serialnum = serialnum;
	}

	public long getProvinceid() {
		return provinceid;
	}

	public void setProvinceid(long provinceid) {
		this.provinceid = provinceid;
	}

	public long getCityid() {
		return cityid;
	}

	public void setCityid(long cityid) {
		this.cityid = cityid;
	}

	public long getProviderid() {
		return providerid;
	}

	public void setProviderid(long providerid) {
		this.providerid = providerid;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getProvidertype() {
		return providertype;
	}

	public void setProvidertype(String providertype) {
		this.providertype = providertype;
	}
}
