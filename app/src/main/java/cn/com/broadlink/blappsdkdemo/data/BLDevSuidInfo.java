package cn.com.broadlink.blappsdkdemo.data;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 设备Suid
 * @author YeJin
 *
 */
public class BLDevSuidInfo {

	private String suid;
	
	private JSONObject intfs;

	public String getSuid() {
		return suid;
	}

	public void setSuid(String suid) {
		this.suid = suid;
	}

	public JSONObject getIntfs() {
		return intfs;
	}

	public void setIntfs(JSONObject intfs) {
		this.intfs = intfs;
	}
	
	/**
	 * 获取设备的接口列表
	 * 
	 * @return
	 * 		List<String> 支持的接口名称列表	
	 */
	public List<String> getIntfsList(){
		if(intfs != null){
			List<String> keyList = new ArrayList<String>();
			Set<Entry<String, Object>> it = intfs.entrySet();
			for(Entry<String, Object> entry : it){
				keyList.add(entry.getKey());
			}
			
			return keyList;
		}
		return null;
	}
	
	/***
	 * 获取接口对应的值
	 * 
	 * @param key
	 * 
	 * @return
	 */
	public List<BLDevProfileInftsValueInfo> getIntfValue(String key){
		if(key == null || intfs.get(key) == null){
			return null;
		}
		
		return JSON.parseArray(intfs.get(key).toString(), 
				BLDevProfileInftsValueInfo.class);
	}
	
}
