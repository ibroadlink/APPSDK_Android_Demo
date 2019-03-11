package cn.com.broadlink.blappsdkdemo.data;

import java.io.Serializable;
import java.util.List;

import static cn.com.broadlink.blappsdkdemo.common.BLApiUrlConstants.CLOUD_NEW_BASE;


public class RmBrandListACResp extends BaseResult implements Serializable {

	/**
	 * respbody : {"downloadinfo":[{"downloadurl":"xxx","fixkey":1,"name":"dsa","fixedid":"xxx"},{"downloadurl":"xxx","fixkey":2,"name":"asdf","fixedid":"xxx"}]}
	 */

	private RespbodyBean respbody;

	public RespbodyBean getRespbody() {
		return respbody;
	}

	public void setRespbody(RespbodyBean respbody) {
		this.respbody = respbody;
	}

	public String getUrl(int index) {
		String sufUrl = this.respbody.getDownloadinfo().get(index).getDownloadurl();
		if(sufUrl == null){
			return null;
		}else{
			if(sufUrl.startsWith("http")){
				return sufUrl;
			}else{
				String restUrl = sufUrl.startsWith("/") ? CLOUD_NEW_BASE + sufUrl + "&mtag=gz" : CLOUD_NEW_BASE + "/" + sufUrl + "&mtag=gz";
				return restUrl;
			}
		}
	}

	public String getFixkey(int index) {
		String sKey = this.respbody.getDownloadinfo().get(index).getFixkey();
		return sKey;
	}

	public String getName(int index) {
		String name = this.respbody.getDownloadinfo().get(index).getName();
		return name;
	}

	public static class RespbodyBean implements Serializable {
		private List<DownloadinfoBean> downloadinfo;

		public List<DownloadinfoBean> getDownloadinfo() {
			return downloadinfo;
		}

		public void setDownloadinfo(List<DownloadinfoBean> downloadinfo) {
			this.downloadinfo = downloadinfo;
		}

		public static class DownloadinfoBean implements Serializable {
			/**
			 * downloadurl : xxx
			 * fixkey : 1
			 * name : dsa
			 * fixedid : xxx
			 */

			private String downloadurl;
			private String fixkey;
			private String name;
			private String fixedid;

			public String getDownloadurl() {
				return downloadurl;
			}

			public void setDownloadurl(String downloadurl) {
				this.downloadurl = downloadurl;
			}

			public String getFixkey() {
				return fixkey;
			}

			public void setFixkey(String fixkey) {
				this.fixkey = fixkey;
			}

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public String getFixedid() {
				return fixedid;
			}

			public void setFixedid(String fixedid) {
				this.fixedid = fixedid;
			}
		}
	}
}