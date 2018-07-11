package cn.com.broadlink.blappsdkdemo.plugin;

public class BLNetworkStatusInfo {

	private String status;

	private MoreInfo more;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public MoreInfo getMore() {
		return more;
	}

	public void setMore(MoreInfo more) {
		this.more = more;
	}

	public static class MoreInfo {
		private String type;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

	}
}
