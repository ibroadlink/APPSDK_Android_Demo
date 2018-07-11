package cn.com.broadlink.blappsdkdemo.data;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/***
 * 设备ProfileInfo
 * 
 * @author YeJin
 * 
 */
public class BLDevProfileInfo {

	private String ver;

	private BLDevDescInfo desc;

	private List<String> srvs = new ArrayList<String>();

	private List<String> funcs = new ArrayList<String>();
	
	private List<BLDevSuidInfo> suids = new ArrayList<BLDevSuidInfo>();

	private JSONObject limits;
	
	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

	public BLDevDescInfo getDesc() {
		return desc;
	}

	public void setDesc(BLDevDescInfo desc) {
		this.desc = desc;
	}

	public List<String> getSrvs() {
		return srvs;
	}

	public void setSrvs(List<String> srvs) {
		this.srvs = srvs;
	}

	public List<BLDevSuidInfo> getSuids() {
		return suids;
	}

	public void setSuids(List<BLDevSuidInfo> suids) {
		this.suids = suids;
	}

	public JSONObject getLimits() {
		return limits;
	}

	public void setLimits(JSONObject limits) {
		this.limits = limits;
	}

	public List<String> getLimtKeyList(){
		if(limits != null){
			List<String> keyList = new ArrayList<String>();
			Set<Entry<String, Object>> it = limits.entrySet();
			for(Entry<String, Object> entry : it){
				keyList.add(entry.getKey());
			}
			
			return keyList;
		}
		return null;
	}
	
	public List<Integer> getLimitValue(String key){
		if(key == null || limits.get(key) == null){
			return null;
		}
		
		return JSON.parseArray(limits.get(key).toString(), 
				Integer.class);
	}

	public List<String> getFuncs() {
		return funcs;
	}

	public void setFuncs(List<String> funcs) {
		this.funcs = funcs;
	}
}
