package cn.com.broadlink.blappsdkdemo.data;

import java.io.Serializable;

/**
 * RM机顶盒运行商信息
 * Project: BLEControlAppV4</p>
 * Title: RmStbProviderInfo</p>
 * Company: BroadLink</p> 
 * @author YeJing
 * @date 2015-10-23
 */
public class RmStbProviderInfo implements Serializable {
	private static final long serialVersionUID = 7175399591218101126L;

	private long providerid;
	
	private String provider;

	private String providername;

	private int hasbrand;

	public long getProviderid() {
		return providerid;
	}

	public void setProviderid(long providerid) {
		this.providerid = providerid;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getProvidername() {
		return providername;
	}

	public void setProvidername(String providername) {
		this.providername = providername;
	}

	public int getHasbrand() {
		return hasbrand;
	}

	public void setHasbrand(int hasbrand) {
		this.hasbrand = hasbrand;
	}

	public boolean hasBrand(){
		return this.hasbrand == 1;
	}
}
