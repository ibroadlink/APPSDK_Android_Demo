package cn.com.broadlink.blappsdkdemo.data;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/***
 * 设备ProfileInfo
 *
 * @author YeJin
 */
public class BLDevProfileInfo {

	private String ver;

	private BLDevDescInfo desc;

	private List<String> srvs = new ArrayList<String>();

//	private List<String> funcs = new ArrayList<String>();

	private List<BLDevSuidInfo> suids = new ArrayList<BLDevSuidInfo>();

	private org.json.JSONObject limits;

	private int issubdev;

	private int subscribable;

	private int wificonfigtype;

	private String apprefix;

	private List<String> protocol = new ArrayList<String>();

	private ArrayList<BLDevParamsGroupInfo> groups = new ArrayList<>();

	private int scan2add = 1;

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

	public org.json.JSONObject getLimits() {
		return limits;
	}

	public void setLimits(org.json.JSONObject limits) {
		this.limits = limits;
	}

	public BLDevProfileInfo() {
		setScan2add(1);
	}

	public List<String> getLimtKeyList() {
		if (limits != null) {
			List<String> keyList = new ArrayList<String>();
			Iterator<String> it = limits.keys();
			while (it.hasNext()) {
				keyList.add(it.next());
			}

			return keyList;
		}
		return null;
	}

	public List<Integer> getLimitValue(String key) {
		if (key == null || limits.isNull(key)) {
			return null;
		}

		return JSON.parseArray(limits.optJSONArray(key).toString(),
				Integer.class);
	}

//	public List<String> getFuncs() {
//		return funcs;
//	}
//
//	public void setFuncs(List<String> funcs) {
//		this.funcs = funcs;
//	}

	public int getIssubdev() {
		return issubdev;
	}

	public void setIssubdev(int issubdev) {
		this.issubdev = issubdev;
	}

	public List<String> getProtocol() {
		return protocol;
	}

	public void setProtocol(List<String> protocol) {
		this.protocol = protocol;
	}

	public int getSubscribable() {
		return subscribable;
	}

	public void setSubscribable(int subscribable) {
		this.subscribable = subscribable;
	}

	public int getWificonfigtype() {
		return wificonfigtype;
	}

	public void setWificonfigtype(int wificonfigtype) {
		this.wificonfigtype = wificonfigtype;
	}

	public String getApprefix() {
		return apprefix;
	}

	public void setApprefix(String apprefix) {
		this.apprefix = apprefix;
	}

	public int getScan2add() {
		return scan2add;
	}

	public void setScan2add(int scan2add) {
		this.scan2add = scan2add;
	}

	public ArrayList<BLDevParamsGroupInfo> getGroups() {
		return groups;
	}

	public void setGroups(ArrayList<BLDevParamsGroupInfo> groups) {
		this.groups = groups;
	}
}
