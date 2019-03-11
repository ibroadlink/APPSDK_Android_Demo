package cn.com.broadlink.blappsdkdemo.data;

import java.util.List;

import static cn.com.broadlink.blappsdkdemo.common.BLApiUrlConstants.CLOUD_NEW_BASE;

public class RmBrandListCommonResp extends BaseResult {

	/**
	 * respbody : {"downloadinfo":[{"downloadurl":"/publicircode/v2/app/downloadirdata?interimid=46936977","randkey":"h5d0gvh2","name":"奥林普_664"},{"downloadurl":"/publicircode/v2/app/downloadirdata?interimid=55519371","randkey":"0qeujsxy","name":"奥林普_793"}]}
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
		if (sufUrl == null) {
			return null;
		} else {
			if (sufUrl.startsWith("http")) {
				return sufUrl;
			} else {
				return CLOUD_NEW_BASE + sufUrl;
			}
		}
	}

	public String getRandkey(int index) {
		String key = this.respbody.getDownloadinfo().get(index).getRandkey();
		return key;
	}

	public int getIridByName(int index) {
		int irid = 0;
		try {
			String name = this.respbody.getDownloadinfo().get(index).getName();
			name = name.replaceAll("(.*)_","");
			irid = Integer.parseInt(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return irid;
	}

	public static class RespbodyBean {
		private List<DownloadinfoBean> downloadinfo;

		public List<DownloadinfoBean> getDownloadinfo() {
			return downloadinfo;
		}

		public void setDownloadinfo(List<DownloadinfoBean> downloadinfo) {
			this.downloadinfo = downloadinfo;
		}

		public static class DownloadinfoBean {
			/**
			 * downloadurl : /publicircode/v2/app/downloadirdata?interimid=46936977
			 * randkey : h5d0gvh2
			 * name : 奥林普_664
			 */

			private String downloadurl;
			private String randkey;
			private String name;

			public String getDownloadurl() {
				return downloadurl;
			}

			public void setDownloadurl(String downloadurl) {
				this.downloadurl = downloadurl;
			}

			public String getRandkey() {
				return randkey;
			}

			public void setRandkey(String randkey) {
				this.randkey = randkey;
			}

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}
		}
	}

	public static class RmGetBrandSubListParam {
		private String countrycode;
		private int devtypeid;
		private int brandid;
	
		public String getCountrycode() {
			return countrycode;
		}
	
		public void setCountrycode(String countrycode) {
			this.countrycode = countrycode;
		}
	
		public int getDevtypeid() {
			return devtypeid;
		}
	
		public void setDevtypeid(int devtypeid) {
			this.devtypeid = devtypeid;
		}
	
		public int getBrandid() {
			return brandid;
		}
	
		public void setBrandid(int brandid) {
			this.brandid = brandid;
		}
		
	}

	public static class RmGetStbProviderParam {
	
		private long provinceid;
		
		private long cityid;
		
		private long providerid;
	
		public long getProvinceid() {
			return provinceid;
		}
	
		public void setProvinceid(long provinceid) {
			this.provinceid = provinceid;
		}
	
		public long getCityid() {
			return cityid;
		}
	
		public void setCityid(long cityid) {
			this.cityid = cityid;
		}
	
		public long getProviderid() {
			return providerid;
		}
	
		public void setProviderid(long providerid) {
			this.providerid = providerid;
		}
		
		
	}
}
