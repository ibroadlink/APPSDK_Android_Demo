package cn.com.broadlink.blappsdkdemo.data.auth;

import cn.com.broadlink.base.BLCommonTools;
import cn.com.broadlink.blappsdkdemo.data.BaseHeadParam;
import cn.com.broadlink.sdk.BLLet;

public class UserHeadParam extends BaseHeadParam {

	public UserHeadParam() {
	}

	public UserHeadParam(String timestamp, String token) {
		super(timestamp, token);
	}

	private String loginsession;

	private String familyid;

	private String sign;

	private String userName;

	public String getLoginsession() {
		return loginsession;
	}

	public void setLoginsession(String loginsession) {
		this.loginsession = loginsession;
	}


	public String getFamilyid() {
		return familyid;
	}

	public void setFamilyid(String familyid) {
		this.familyid = familyid;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public void setSign(String url, String keys) {
		this.sign = BLCommonTools.SHA1(url + this.getTimestamp() + (keys==null?"broadlinkappmanage@":keys) + BLLet.getLicenseId());
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
