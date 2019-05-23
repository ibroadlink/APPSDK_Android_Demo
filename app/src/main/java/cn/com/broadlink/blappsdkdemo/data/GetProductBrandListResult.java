package cn.com.broadlink.blappsdkdemo.data;

import java.util.ArrayList;
import java.util.List;

/**
 * File description
 *
 * @author YeJing
 * @data 2017/8/31
 */

public class GetProductBrandListResult extends BaseResult{
	private List<String> brandnamelist = new ArrayList<>();

	public List<String> getBrandnamelist() {
		return brandnamelist;
	}

	public void setBrandnamelist(List<String> brandnamelist) {
		this.brandnamelist = brandnamelist;
	}
}
