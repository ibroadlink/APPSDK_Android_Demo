package cn.com.broadlink.blappsdkdemo.data;

import java.util.ArrayList;
import java.util.List;

/**
 * 电视频道
 * Project: BLEControlAppV4</p>
 * Title: RmTvChannelResult</p>
 * Company: BroadLink</p> 
 * @author YeJing
 * @date 2015-10-23
 */
public class RmTvChannelResult extends BaseResult{

	private String verision;
	
	private List<BLRmBrandInfo> data = new ArrayList<BLRmBrandInfo>();

	public String getVerision() {
		return verision;
	}

	public void setVerision(String verision) {
		this.verision = verision;
	}

	public List<BLRmBrandInfo> getData() {
		return data;
	}

	public void setData(List<BLRmBrandInfo> data) {
		this.data = data;
	}

	
}
