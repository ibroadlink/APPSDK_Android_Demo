package cn.com.broadlink.blappsdkdemo.data;

import java.util.ArrayList;

public class RmBrandAcListResult extends BaseResult {
	private ArrayList<String> url = new ArrayList<String>();
	private ArrayList<String> key = new ArrayList<String>();

	public ArrayList<String> getUrl() {
		return url;
	}

	public void setUrl(ArrayList<String> url) {
		this.url = url;
	}

	public ArrayList<String> getKey() {
		return key;
	}

	public void setKey(ArrayList<String> key) {
		this.key = key;
	}
}