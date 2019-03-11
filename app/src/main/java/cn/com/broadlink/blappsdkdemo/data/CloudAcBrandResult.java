package cn.com.broadlink.blappsdkdemo.data;

import java.util.ArrayList;
import java.util.List;


public class CloudAcBrandResult extends BaseResult{

	private List<BLRmBrandInfo> brand = new ArrayList<BLRmBrandInfo>();

	public List<BLRmBrandInfo> getBrand() {
		return brand;
	}

	public void setBrand(List<BLRmBrandInfo> brand) {
		this.brand = brand;
	}
	
}
