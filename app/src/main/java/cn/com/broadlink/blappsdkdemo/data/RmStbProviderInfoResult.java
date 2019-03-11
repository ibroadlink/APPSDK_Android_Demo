package cn.com.broadlink.blappsdkdemo.data;

import java.util.ArrayList;
import java.util.List;

public class RmStbProviderInfoResult extends BaseResult{
	private RespbodyBean respbody = new RespbodyBean();

	public RespbodyBean getRespbody() {
		return respbody;
	}

	public void setRespbody(RespbodyBean respbody) {
		this.respbody = respbody;
	}

	public List<RmStbProviderInfo> getProviderinfo() {
		return respbody.getProviderinfo();
	}

	public static class RespbodyBean {
		private List<RmStbProviderInfo> providerinfo = new ArrayList<>();

		public List<RmStbProviderInfo> getProviderinfo() {
			return providerinfo;
		}

		public void setProviderinfo(List<RmStbProviderInfo> providerinfo) {
			this.providerinfo = providerinfo;
		}
	}
	
}
