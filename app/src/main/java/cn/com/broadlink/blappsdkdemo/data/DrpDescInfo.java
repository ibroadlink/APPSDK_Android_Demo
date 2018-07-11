package cn.com.broadlink.blappsdkdemo.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Drp资源包中的信息类
 * @author YeJin
 *
 */
public class DrpDescInfo {

	private int id;

	private List<String> suid = new ArrayList<String>();
	
	private int version;
	
	private String default_lang;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<String> getSuid() {
		return suid;
	}

	public void setSuid(List<String> suid) {
		this.suid = suid;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getDefault_lang() {
		return default_lang;
	}

	public void setDefault_lang(String default_lang) {
		this.default_lang = default_lang;
	}
	
	
}
